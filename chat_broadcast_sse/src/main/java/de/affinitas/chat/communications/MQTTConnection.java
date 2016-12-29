package de.affinitas.chat.communications;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.hawtdispatch.Task;
import org.fusesource.mqtt.client.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static org.fusesource.mqtt.client.QoS.EXACTLY_ONCE;

public class MQTTConnection {

    private final MQTT mqtt;
    private final ArrayList<Topic> topics;

    private static void stderr(Object x) {
        System.err.println(x);
    }

    public static void main(String[] args) {
        new MQTTConnection();
    }

    public MQTTConnection() {
        mqtt = new MQTT();
        topics = new ArrayList<>();
        try {
            mqtt.setHost("tcp://localhost:61613");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mqtt.setUserName("admin");
        mqtt.setPassword("password");
        topics.add(new Topic("hahai/1fa70a00-66d4-4fe1-93d8-7faf280090c8", EXACTLY_ONCE));
        execute(System.out::println);

    }

    private void execute(MessageReceivedCallback callback) {
        final CallbackConnection connection = this.mqtt.callbackConnection();
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

            public void onConnected() {  }
            public void onDisconnected() {  }

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

            public void onFailure(Throwable value) {  }

            public void onSuccess(Void value) {
                final Topic[] ta = topics.toArray(new Topic[topics.size()]);
                connection.subscribe(ta, new Callback<byte[]>() {
                    public void onSuccess(byte[] value) {  }
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

        System.exit(0);
    }
}


