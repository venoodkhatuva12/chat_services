package de.affinitas.chat.communications;

import de.affinitas.chat.communications.serversentevent.ServerSentEvent;
import de.affinitas.chat.service.SSEConfig;
import de.affinitas.chat.service.ServerSentEventConfig;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.Executors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public final class QueueToManyStreamsRegistryTest {

    @Test
    public void noneAttached() throws Exception {
        QueueToManyStreamsRegistry unit = new QueueToManyStreamsRegistry(UUID.randomUUID(), new SSEConfig());
        assertThat(unit.hasStreamsAttached(), is((false)));
    }

    @Test
    public void canAttachStream() throws Exception {
        ServerSentEvent mockStream = mock(ServerSentEvent.class);
        QueueToManyStreamsRegistry unit = new QueueToManyStreamsRegistry(UUID.randomUUID(), new SSEConfig());
        unit.attachEventStreamToQueue(mockStream);
        assertThat(unit.hasStreamsAttached(), is((true)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void canSendToOneStream() throws Exception {

        ServerSentEvent mockStream = mock(ServerSentEvent.class);

        Receiver<String> continuallyReceivingReceiver= new Receiver<String>() {
            @Override public void subscribeTo(UUID id, MessageReceivedCallback callback) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    boolean humansRuleTheEarth = true;
                    while(humansRuleTheEarth) { callback.call("fred"); }
                });
            }
            @Override public void killConnection() {}
        };
        QueueToManyStreamsRegistry unit = new QueueToManyStreamsRegistry(UUID.randomUUID(), new SSEConfig()) {
            @Override protected Receiver<String> makeMessageReceiver(ServerSentEventConfig config) {
                return continuallyReceivingReceiver;
            }
        };
        unit.attachEventStreamToQueue(mockStream);

        Thread.sleep(50);
        verify(mockStream, atLeastOnce()).data("fred");
    }


}
