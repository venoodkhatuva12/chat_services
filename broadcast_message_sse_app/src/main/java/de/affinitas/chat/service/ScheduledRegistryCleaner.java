package de.affinitas.chat.service;

import de.affinitas.chat.communications.QueueRegistry;
import de.affinitas.chat.communications.QueuesRegistry;
import de.affinitas.chat.model.Visitor;

import java.time.Duration;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class ScheduledRegistryCleaner implements Visitor<QueueRegistry> {

    private final Duration cleanupDuration;
    private final QueuesRegistry queuesRegistry;

    public ScheduledRegistryCleaner(Duration cleanupDuration, QueuesRegistry queuesRegistry) {
        this.cleanupDuration = cleanupDuration;
        this.queuesRegistry = queuesRegistry;
    }

    void startCleaningQueuesWithNoStreamsAttached() {
        long cleanupDurationNano = cleanupDuration.toNanos();
        newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::visitQueueRegistry, cleanupDurationNano, cleanupDurationNano, NANOSECONDS);
    }

    private void visitQueueRegistry() {
        queuesRegistry.accept(this);
    }

    @Override public void visit(QueueRegistry queueRegistry) {
        if(!queueRegistry.hasStreamsAttached()) {
            queueRegistry.closeQueue();
            queuesRegistry.remove(queueRegistry.getId());
        }
    }
}
