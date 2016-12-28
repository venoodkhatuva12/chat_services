package de.affinitas.chat.communications;

import de.affinitas.chat.communications.serversentevent.ServerSentEvent;

import java.util.UUID;

public interface QueueRegistry {

    UUID getId();

    void attachEventStreamToQueue(ServerSentEvent stream);

    boolean hasStreamsAttached();

    void closeQueue();
}
