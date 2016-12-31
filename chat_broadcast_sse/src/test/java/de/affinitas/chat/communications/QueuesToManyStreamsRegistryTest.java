package de.affinitas.chat.communications;

import de.affinitas.chat.service.SSEConfig;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public final class QueuesToManyStreamsRegistryTest {

    @Test public void queueDoesntExists() {
        QueuesRegistry unit= new QueuesToManyStreamsRegistry(new SSEConfig());
        assertThat(unit.exists(UUID.randomUUID()), is(false));
    }

    @Test public void queueDoesExistsAfterAskingForIt() {
        QueuesRegistry unit= new QueuesToManyStreamsRegistry(new SSEConfig());
        UUID channelId = UUID.randomUUID();
        unit.queueFor(channelId);
        assertThat(unit.exists(channelId), is(true));
    }

    @Test public void canAddThreeQueues() {
        QueuesRegistry unit= new QueuesToManyStreamsRegistry(new SSEConfig());
        unit.queueFor(UUID.randomUUID());
        unit.queueFor(UUID.randomUUID());
        unit.queueFor(UUID.randomUUID());
        final int[] count = {0};
        unit.accept(queueRegistry -> count[0]++);
        assertThat(count[0], is(3));
    }

    @Test public void canRemoveQueue() {
        QueuesRegistry unit= new QueuesToManyStreamsRegistry(new SSEConfig());
        UUID channelId = UUID.randomUUID();
        unit.queueFor(channelId);
        assertThat(unit.exists(channelId), is(true));
        unit.remove(channelId);
        assertThat(unit.exists(channelId), is(false));
    }

}
