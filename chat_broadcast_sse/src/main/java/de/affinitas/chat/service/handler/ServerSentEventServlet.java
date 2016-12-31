package de.affinitas.chat.service.handler;

import de.affinitas.chat.communications.QueuesRegistry;
import de.affinitas.chat.communications.QueueRegistry;
import de.affinitas.chat.communications.serversentevent.ServerSentEvent;
import de.affinitas.chat.communications.serversentevent.ServerSentEventStream;
import de.affinitas.chat.service.ServerSentEventConfig;
import de.affinitas.chat.service.path.PathParser;
import de.affinitas.chat.service.path.PathValues;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

public class ServerSentEventServlet extends HttpServlet {

    private final ServerSentEventConfig config;
    private final QueuesRegistry registry;
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    public ServerSentEventServlet(ServerSentEventConfig config, QueuesRegistry registry) {
        this.config = config;
        this.registry = registry;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String> acceptValues = fillWith(request.getHeaders("Accept"));
        if (acceptValues.contains("text/event-stream")) {
            setResponseHeaders(response);
            UUID channelId = getChannelIdFromPath(request);
            System.out.println("Connection request to channel: "+channelId.toString());
            QueueRegistry queueRegistry = registry.queueFor(channelId);
            Continuation continuation = makeContinuation(request, response);
            ServerSentEvent serverSentEvent = new ServerSentEventStream(continuation);
            queueRegistry.attachEventStreamToQueue(serverSentEvent);
        } else {
            super.doGet(request, response);
        }
    }

    private List<String> fillWith(Enumeration<String> requestHeaders) {
        ArrayList<String> reply = new ArrayList<>();
        while (requestHeaders.hasMoreElements()) {
            reply.add(requestHeaders.nextElement());
        }
        return reply;
    }

    private Continuation makeContinuation(HttpServletRequest request, HttpServletResponse response) {
        Continuation continuation = ContinuationSupport.getContinuation(request);
        // Infinite timeout because the continuation is never resumed,
        // but only completed on close
        long infiniteTimeout = 0L;
        continuation.setTimeout(infiniteTimeout);
        continuation.suspend(response);
        return continuation;
    }

    private UUID getChannelIdFromPath(HttpServletRequest request) {
        String uri = getPath(request);
        String valuesTemplateStream = config.getValuesTemplateStream();
        PathParser parser = new PathParser(valuesTemplateStream);
        PathValues values = parser.parse(uri);
        return values.uuid("channel_id");
    }

    private String getPath(HttpServletRequest request) {
        try {
            return new URI(request.getRequestURL().toString()).normalize().toURL().getPath();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setResponseHeaders(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(UTF_8.name());
        response.setContentType("text/event-stream");
        response.addHeader("X-Accel-Buffering", "no");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Access-Control-Allow-Origin", "*");
        // By adding this header, and not closing the connection,
        // we disable HTTP chunking, and we can use write()+flush()
        // to send data in the text/event-stream protocol
        response.addHeader("Connection", "close");
        response.flushBuffer();
    }
}
