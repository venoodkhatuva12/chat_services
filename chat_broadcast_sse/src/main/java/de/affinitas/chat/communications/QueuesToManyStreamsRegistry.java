package de.affinitas.chat.communications;

import de.affinitas.chat.model.Visitor;
import de.affinitas.chat.service.ServerSentEventConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QueuesToManyStreamsRegistry implements QueuesRegistry {

    private final Map<UUID, QueueRegistry> map;
    private ServerSentEventConfig config;

    public QueuesToManyStreamsRegistry(ServerSentEventConfig config) {
        this.config = config;
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public boolean exists(UUID channelId) {
        return map.containsKey(channelId);
    }

    @Override
    public QueueRegistry queueFor(UUID channelId) {
        if (!exists(channelId)) {
            map.put(channelId, new QueueToManyStreamsRegistry(channelId, config));
        }
        return map.get(channelId);
    }

    @Override
    public void remove(UUID channelId) {
        QueueRegistry queueRegistry = map.get(channelId);
        queueRegistry.closeQueue();
        map.remove(channelId);
    }

    @Override
    public void accept(Visitor<QueueRegistry> visitor) {
        List<QueueRegistry> values = new ArrayList<>(map.values());
        values.forEach(visitor::visit);
    }

}
