package de.affinitas.chat.service;

import de.affinitas.chat.communications.QueuesRegistry;
import de.affinitas.chat.communications.QueuesToManyStreamsRegistry;
import de.affinitas.chat.service.handler.ServerSentEventServlet;
import org.eclipse.jetty.server.Server;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.MINUTES;

public class ChatBroadcastSSEService {

    private final Server webServer;

    public static void main(String... args) throws Exception {
        ServerSentEventConfig config = new SSEConfig();
        ChatBroadcastSSEService service = new ChatBroadcastSSEService(config);
        service.start();
    }

    public ChatBroadcastSSEService(ServerSentEventConfig config) throws Exception {

        QueuesRegistry registry = new QueuesToManyStreamsRegistry(config);

        startScheduledCleaner(registry);

        webServer = HttpServerBuilder.builder()
                .port(config.getPort())
                .addToContext("/", HttpServerBuilder.ServletsBuilder.sb().addServlet(config.getServletPathStream(), new ServerSentEventServlet(config, registry)))
                .build();
    }

    protected void startScheduledCleaner(QueuesRegistry registry) {
        Duration cleanupPeriod = Duration.of(1, MINUTES);
        ScheduledRegistryCleaner scheduledRegistryCleaner = new ScheduledRegistryCleaner(registry);
        scheduledRegistryCleaner.schedule(cleanupPeriod);
    }

    public void start() throws Exception {
        webServer.start();
        System.out.println("Server started");
        webServer.join();
    }

}
