package de.affinitas.chat;

import de.affinitas.chat.communications.QueueRegistry;
import de.affinitas.chat.communications.QueuesRegistry;
import de.affinitas.chat.model.Visitor;
import de.affinitas.chat.service.ScheduledRegistryCleaner;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public final class ScheduledRegistryCleanerTest {

    private List<UUID> removed;

    @Test
    public void willCleanWhenNoSSEAttached() throws Exception {
        QueueRegistry queueRegistry = mock(QueueRegistry.class);
        when(queueRegistry.hasStreamsAttached()).thenReturn(false);

        QueuesRegistry registry = new FakeQueuesRegistry(queueRegistry);
        ScheduledRegistryCleaner unit = new ScheduledRegistryCleaner(registry);
        unit.schedule(Duration.of(1, MILLIS));
        Thread.sleep(50);

        verify(queueRegistry, atLeastOnce()).closeQueue();
        assertThat(removed.isEmpty(), is(false));
    }

    private class FakeQueuesRegistry implements QueuesRegistry {

        private final QueueRegistry queueRegistry;

        public FakeQueuesRegistry(QueueRegistry queueRegistry) {
            this.queueRegistry = queueRegistry;
            removed = new ArrayList<>();
        }

        @Override public boolean exists(UUID channelId) {
            return true;
        }

        @Override public QueueRegistry queueFor(UUID channelId) {
            return queueRegistry;
        }

        @Override public void remove(UUID channelId) {
            removed.add(channelId);
        }

        @Override public void accept(Visitor<QueueRegistry> visitor) {
            visitor.visit(queueRegistry);
        }
    }
}
