package de.affinitas.chat.messagequeue;


import de.affinitas.chat.service.ChatConfig;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.SECONDS;

public final class Broadcaster {

    private final String destination;
    private final CallbackConnection connection;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean connecting = new AtomicBoolean(false);
    private final ExecutorService executorService;

    public Broadcaster(UUID id, ChatConfig config) {
        String topicName = id.toString();
        destination = config.getMqttRootTopic() + "/" + topicName;
        MQTT mqtt = new MQTT();
        try {
            mqtt.setHost(config.getMqttBrokerUrl());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mqtt.setUserName(config.getMqttUser());
        mqtt.setPassword(config.getMqttPassword());
        connection = mqtt.callbackConnection();
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(this::connect);
    }

    void connect() {
        connecting.set(true);
        connection.connect(new Callback<Void>() {
            public void onSuccess(Void aVoid) {
                connecting.set(false);
                connected.set(true);
            }
            public void onFailure(Throwable throwable) {
                connecting.set(false);
                connected.set(false);
            }
        });
    }

    public void broadcast(String message) throws ConnectionNotEstablished {
        broadcast(message, 0);
    }

    private void broadcast(String message, int retryCount) throws ConnectionNotEstablished {
        if (connected.get()) {
            System.out.println("Publishing to " + destination + ": " + message);
            connection.publish(destination, message.getBytes(Charset.forName("UTF8")), QoS.EXACTLY_ONCE, false, null);
        } else {
            queue(message, retryCount);
        }
    }

    private void queue(String message, final int retryCount) throws ConnectionNotEstablished {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            try {
                broadcast(message, (retryCount+1));
            } catch (ConnectionNotEstablished e) {
                if (retryCount > 5) {
                    executorService.shutdown();
                    throw new ConnectionNotEstablished();
                }
            }
        }, 1, SECONDS);
    }

    public void close() {
        if(connection!=null) {
            connection.kill(new Callback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(executorService!=null) {
                        executorService.shutdown();

                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    if(executorService!=null) {
                        executorService.shutdown();
                    }
                }
            });
        }
    }
}
