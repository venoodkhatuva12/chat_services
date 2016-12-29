package de.affinitas.chat.communications;

import de.affinitas.chat.communications.serversentevent.ServerSentEvent;
import de.affinitas.chat.service.ServerSentEventConfig;

import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

public class QueueToManyStreamsRegistry implements QueueRegistry {

    private final UUID areaId;
    private final Queue<ServerSentEvent> streams;
    private MessageReceiver receiver;

    public QueueToManyStreamsRegistry(UUID areaId, ServerSentEventConfig config) {
        this.areaId = areaId;
        streams = new ConcurrentLinkedQueue<>();
        attachToQueue(areaId, config);
    }

    protected void attachToQueue(UUID areaId, ServerSentEventConfig config) {
        Executors.newSingleThreadExecutor().execute(() -> {
            receiver = new MessageReceiver(config);
            receiver.subscribeTo(areaId, this::broadcastToAllStreams);
        });
    }

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
        return areaId;
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
    }
}
