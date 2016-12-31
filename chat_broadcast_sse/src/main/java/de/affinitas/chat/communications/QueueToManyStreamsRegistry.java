package de.affinitas.chat.communications;

import de.affinitas.chat.communications.serversentevent.ServerSentEvent;
import de.affinitas.chat.service.ServerSentEventConfig;

import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueueToManyStreamsRegistry implements QueueRegistry {

    private final UUID channelId;
    private final Queue<ServerSentEvent> streams;
    private final ExecutorService executorService;
    private Receiver<String> receiver;

    public QueueToManyStreamsRegistry(UUID channelId, ServerSentEventConfig config) {
        this.channelId = channelId;
        streams = new ConcurrentLinkedQueue<>();
        executorService = registerQueue(channelId, config);
    }

    private ExecutorService registerQueue(UUID channelId, ServerSentEventConfig config) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            receiver = makeMessageReceiver(config);
            receiver.subscribeTo(channelId, this::broadcastToAllStreams);
        });
        return executor;
    }

    protected Receiver<String> makeMessageReceiver(ServerSentEventConfig config) {return new MessageReceiver(config);}

    private void broadcastToAllStreams(String message) {
        Iterator<ServerSentEvent> iterator = streams.iterator();
        while(iterator.hasNext()) {
            ServerSentEvent stream = iterator.next();
            try {
                stream.data(message);
            } catch (IOException e) {
                stream.close();
                iterator.remove();
            }
        }
    }

    @Override
    public UUID getId() {
        return channelId;
    }

    @Override
    public void attachEventStreamToQueue(ServerSentEvent stream) {
        streams.add(stream);
    }

    @Override
    public boolean hasStreamsAttached() {
        return streams.size() > 0;
    }

    @Override
    public void closeQueue() {
        if(receiver!=null) {
            receiver.killConnection();
        }
        executorService.shutdown();
    }
}
