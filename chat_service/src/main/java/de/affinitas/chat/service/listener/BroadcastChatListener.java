package de.affinitas.chat.service.listener;

import de.affinitas.chat.service.ChatConfig;
import de.affinitas.chat.messagequeue.Broadcaster;
import de.affinitas.chat.messagequeue.ConnectionNotEstablished;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BroadcastChatListener implements ServletContextListener {

    private static Map<UUID, Broadcaster> REGISTRY;
    private static ChatConfig config;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        if(REGISTRY==null) {
            REGISTRY = new ConcurrentHashMap<>();
        }
        config = new ChatConfig();
        System.out.println("Broadcast listener initialised");
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if(REGISTRY != null) {
            REGISTRY.values().forEach(Broadcaster::close);
            REGISTRY = null;
        }
    }

    public static void broadcast(UUID channelId, String message) throws IllegalArgumentException, ConnectionNotEstablished {
        System.out.println("Attempting to broadcast message \""+message+"\" on channel: "+channelId.toString());
        Broadcaster broadcaster = makeBroadcasterIfNotExists(channelId);
        broadcaster.broadcast(message);
    }

    private static Broadcaster makeBroadcasterIfNotExists(UUID channelId) throws IllegalArgumentException {
        Broadcaster broadcaster = REGISTRY.get(channelId);
        if(broadcaster==null) {
            broadcaster = new Broadcaster(channelId, config);
            REGISTRY.put(channelId, broadcaster);
        }
        return broadcaster;
    }
}
