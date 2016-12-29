package de.affinitas.chat.communications.serversentevent;

import java.io.IOException;

public interface ServerSentEvent {
    /**
     * <p>Sends a named event with data to the client.</p>
     * <p>When invoked as: <code>event("foo", "bar")</code>, the client will receive the lines:</p>
     * <pre>
     * event: foo
     * data: bar
     * </pre>
     *
     * @param name the event name
     * @param data the data to be sent
     * @throws IOException if an I/O failure occurred
     * @see #data(String)
     */
    public void event(String name, String data) throws IOException;

    /**
     * <p>Sends a default event with data to the client.</p>
     * <p>When invoked as: <code>data("baz")</code>, the client will receive the line:</p>
     * <pre>
     * data: baz
     * </pre>
     * <p>When invoked as: <code>data("foo\r\nbar\rbaz\nbax")</code>, the client will receive the lines:</p>
     * <pre>
     * data: foo
     * data: bar
     * data: baz
     * data: bax
     * </pre>
     *
     * @param data the data to be sent
     * @throws IOException if an I/O failure occurred
     */
    public void data(String data) throws IOException;

    /**
     * <p>Sends a comment to the client.</p>
     * <p>When invoked as: <code>comment("foo")</code>, the client will receive the line:</p>
     * <pre>
     * : foo
     * </pre>
     *
     * @param comment the comment to send
     * @throws IOException if an I/O failure occurred
     */
    public void comment(String comment) throws IOException;

    /**
     * <p>Closes this event source connection.</p>
     */
    public void close();

}