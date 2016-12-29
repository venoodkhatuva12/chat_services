package de.affinitas.chat.service;

import de.affinitas.chat.communications.QueuesRegistry;
import de.affinitas.chat.service.handler.ServerSentEventServlet;
import de.affinitas.chat.communications.QueuesToManyStreamsRegistry;
import org.eclipse.jetty.server.Server;

import java.net.URI;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.MINUTES;

public class ServerSentEventService {

    private final Server webServer;

    public static void main(String... args) throws Exception {
        ServerSentEventConfig config = new SSEConfig();
        ServerSentEventService service = new ServerSentEventService(config);
        service.start();
    }


    public ServerSentEventService(ServerSentEventConfig config) throws Exception {

        QueuesRegistry registry = new QueuesToManyStreamsRegistry(config);

        startScheduledCleaner(registry);

        webServer = HttpServerBuilder.builder()
                .port(config.getPort())
                .addToContext("/", HttpServerBuilder.ServletsBuilder.sb().addServlet(config.getServletPathStream(), new ServerSentEventServlet(config, registry)))
                .build();
    }

    protected void startScheduledCleaner(QueuesRegistry registry) {
        Duration cleanupPeriod = Duration.of(1, MINUTES);
        ScheduledRegistryCleaner scheduledRegistryCleaner = new ScheduledRegistryCleaner(cleanupPeriod, registry);
        scheduledRegistryCleaner.startCleaningQueuesWithNoStreamsAttached();
    }

    public void start() throws Exception {
        webServer.start();
        System.out.println("Server started");
        webServer.join();
    }

    public boolean isStarted() {
        return webServer.isStarted();
    }

    public URI uri() {
        return webServer.getURI();
    }
}
