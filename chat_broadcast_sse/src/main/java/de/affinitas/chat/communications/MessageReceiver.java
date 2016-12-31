package de.affinitas.chat.communications;

import de.affinitas.chat.service.ServerSentEventConfig;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.hawtdispatch.Task;
import org.fusesource.mqtt.client.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.fusesource.mqtt.client.QoS.EXACTLY_ONCE;

public final class MessageReceiver implements Receiver<String> {


    //TODO: a complete mess - copy and pasted from online to get it running quickly
    private final ServerSentEventConfig config;
    private final CallbackConnection connection;

    public MessageReceiver(ServerSentEventConfig config) {
        this.config = config;
        MQTT mqtt = new MQTT();
        try {
            mqtt.setHost(config.getMqttBrokerUrl());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mqtt.setUserName(config.getMqttUser());
        mqtt.setPassword(config.getMqttPassword());
        connection = mqtt.callbackConnection();
    }

    public void subscribeTo(UUID id, MessageReceivedCallback callback) {
        String topicName = id.toString();
        final String destination = config.getMqttRootTopic() + "/" + topicName;
        Topic[] topics = {new Topic(destination, EXACTLY_ONCE)};

        final CountDownLatch disconnected = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                this.setName("MQTT client shutdown");

                connection.getDispatchQueue().execute(new Task() {
                    public void run() {
                        connection.disconnect(new Callback<Void>() {
                            @Override
                            public void onSuccess(Void o) {
                                disconnected.countDown();
                            }

                            public void onFailure(Throwable value) {
                                disconnected.countDown();
                            }
                        });
                    }
                });
            }
        });
        connection.listener(new Listener() {

            public void onConnected() { }

            public void onDisconnected() { }

            public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack) {
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    body.writeTo(out);
                    String reply = out.toString("UTF-8");
                    callback.call(reply);
                    ack.run();
                } catch (IOException e) {
                    this.onFailure(e);
                }

            }

            public void onFailure(Throwable value) {
                stderr(value);
            }
        });
        connection.resume();
        connection.connect(new Callback<Void>() {

            public void onFailure(Throwable value) { }

            public void onSuccess(Void value) {
                connection.subscribe(topics, new Callback<byte[]>() {
                    public void onSuccess(byte[] value) {}
                    public void onFailure(Throwable value) {
                        stderr("Subscribe failed: " + value);
                    }
                });
            }
        });

        try {
            disconnected.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void stderr(Object x) {
        System.err.println(x);
    }

    public void killConnection() {
        connection.kill(new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });
    }

}
