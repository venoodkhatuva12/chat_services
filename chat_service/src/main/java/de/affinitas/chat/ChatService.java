package de.affinitas.chat;

import de.affinitas.chat.handler.Chat;
import de.affinitas.chat.listener.BroadcastChatListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class ChatService {

    public static void main(String[] args) throws Exception {

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addEventListener(new BroadcastChatListener());

        Server server = new Server(8080);
        server.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", Chat.class.getCanonicalName());

        try {
            server.start();
            server.join();
        } finally {
            server.destroy();
        }
    }
}