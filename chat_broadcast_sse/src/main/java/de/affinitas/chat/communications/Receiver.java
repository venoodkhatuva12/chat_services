package de.affinitas.chat.communications;

import java.util.UUID;

public interface Receiver<T> {
    void subscribeTo(UUID id, MessageReceivedCallback callback);
    void killConnection();
}
