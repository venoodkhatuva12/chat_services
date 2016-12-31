package de.affinitas.chat.communications;

import de.affinitas.chat.model.Visitable;
import de.affinitas.chat.model.Visitor;

import java.util.UUID;

public interface QueuesRegistry extends Visitable<QueueRegistry> {


    boolean exists(UUID channelId);

    QueueRegistry queueFor(UUID channelId);

    void remove(UUID channelId);

    void accept(Visitor<QueueRegistry> scheduledCleaner);

}
