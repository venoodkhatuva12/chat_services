package de.affinitas.chat.communications.serversentevent;

import org.eclipse.jetty.continuation.Continuation;

import java.io.*;
import java.nio.charset.Charset;

public class ServerSentEventStream implements ServerSentEvent {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final byte[] crlf = new byte[]{'\r', '\n'};
    private final byte[] eventField;
    private final byte[] dataField;
    private final byte[] commentField;

    private final Continuation continuation;
    private final OutputStream output;

    public ServerSentEventStream(Continuation continuation) throws IOException {
        this.continuation = continuation;
        this.output = getOutputStream(continuation);
        try {
            eventField = "event: ".getBytes(UTF_8.name());
            dataField = "data: ".getBytes(UTF_8.name());
            commentField = ": ".getBytes(UTF_8.name());
        } catch (UnsupportedEncodingException x) {
            throw new RuntimeException(x);
        }
    }

    private OutputStream getOutputStream(Continuation continuation) throws IOException {
        return continuation.getServletResponse().getOutputStream();
    }

    public void event(String name, String data) throws IOException {
        synchronized (this) {
            output.write(eventField);
            output.write(name.getBytes(UTF_8.name()));
            output.write(crlf);
            data(data);
        }
    }

    public void data(String data) throws IOException {
        synchronized (this) {
            BufferedReader reader = new BufferedReader(new StringReader(data));
            String line;
            while ((line = reader.readLine()) != null) {
                output.write(dataField);
                output.write(line.getBytes(UTF_8.name()));
                output.write(crlf);
            }
            output.write(crlf);
            flush();
        }
    }

    public void comment(String comment) throws IOException {
        synchronized (this) {
            output.write(commentField);
            output.write(comment.getBytes(UTF_8.name()));
            output.write(crlf);
            output.write(crlf);
            flush();
        }
    }

    protected void flush() throws IOException {
        continuation.getServletResponse().flushBuffer();
    }

    public void close() {
        continuation.complete();
    }
}
