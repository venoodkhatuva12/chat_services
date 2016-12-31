package de.affinitas.chat.communications;

import de.affinitas.chat.communications.serversentevent.ServerSentEvent;
import de.affinitas.chat.service.SSEConfig;
import de.affinitas.chat.service.ServerSentEventConfig;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;

public final class QueueToManyStreamsRegistryTwoTest {

    @Test
    @SuppressWarnings("unchecked")
    public void canSendToTwoStreams() throws Exception {

        ServerSentEvent streamOne = mock(ServerSentEvent.class);
        ServerSentEvent streamTwo = mock(ServerSentEvent.class);

        Receiver<String> continuallyReceivingReceiver= new Receiver<String>() {
            @Override public void subscribeTo(UUID id, MessageReceivedCallback callback) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    boolean humansRuleTheEarth = true;
                    while(humansRuleTheEarth) { callback.call("wilma"); }
                });
            }
            @Override public void killConnection() {}
        };
        QueueToManyStreamsRegistry unit = new QueueToManyStreamsRegistry(UUID.randomUUID(), new SSEConfig()) {
            @Override protected Receiver<String> makeMessageReceiver(ServerSentEventConfig config) {
                return continuallyReceivingReceiver;
            }
        };
        unit.attachEventStreamToQueue(streamOne);
        unit.attachEventStreamToQueue(streamTwo);

        Thread.sleep(100);//hacky as hell, but don't have enough time to fix the actual problem (the production class and the way its written)
        verify(streamOne, atLeastOnce()).data("wilma");
        verify(streamTwo, atLeastOnce()).data("wilma");
    }

}
