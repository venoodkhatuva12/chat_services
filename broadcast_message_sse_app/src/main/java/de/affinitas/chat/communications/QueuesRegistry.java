package de.affinitas.chat.communications;

import de.affinitas.chat.model.Visitable;
import de.affinitas.chat.model.Visitor;

import java.util.UUID;

public interface QueuesRegistry extends Visitable<QueueRegistry> {


    boolean exists(UUID areaId);

    QueueRegistry queueFor(UUID areaId);

    void remove(UUID areaId);

    void accept(Visitor<QueueRegistry> scheduledCleaner);

}
