package de.affinitas.chat.service.handler;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import de.affinitas.chat.messagequeue.ConnectionNotEstablished;
import org.hamcrest.Description;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;

import javax.ws.rs.core.Response;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ChatTest {

    @Test
    public void successfulPost() {
        final String[] actualMessage = {""};
        Chat unit = new Chat() {
            @Override
            protected void broadcast(UUID channelId, String toBroadcast) {
                actualMessage[0] = toBroadcast;
            }
        };
        String channelId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        String message = "hello world";
        Response reply = unit.publishChat(channelId, userId, message);
        JsonObject parsed = Json.parse(actualMessage[0]).asObject();

        assertThat(reply.getStatus(), is(200));
        assertThat(parsed.get("id").asString(), isUUID());
        assertThat(parsed.get("userId").asString(), is(userId));
        assertThat(parsed.get("message").asString(), is("hello world"));
    }

    @Test
    public void unsuccessfulPostChannelNotUUID() {
        Chat unit = new Chat() { @Override protected void broadcast(UUID channelId, String toBroadcast) { /* override actually sending */ } };
        String channelId = "im not a UUID";
        String userId = UUID.randomUUID().toString();
        Response reply = unit.publishChat(channelId, userId, "hello world");
        assertThat(reply.getStatus(), is(400));
    }

    @Test
    public void unsuccessfulPostUserIdNotUUID() {
        Chat unit = new Chat() { @Override protected void broadcast(UUID channelId, String toBroadcast) { /* override actually sending */ } };
        String channelId = UUID.randomUUID().toString();
        String userId = "im not a UUID";
        Response reply = unit.publishChat(channelId, userId, "hello world");
        assertThat(reply.getStatus(), is(400));
    }

    @Test
    public void unsuccessfulPostCannotConnectToChannel() {
        Chat unit = new Chat() {
            @Override protected void broadcast(UUID channelId, String toBroadcast) {
                throw new ConnectionNotEstablished();
            }
        };
        String channelId = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        Response reply = unit.publishChat(channelId, userId, "hello world");
        assertThat(reply.getStatus(), is(202));
    }

    private TypeSafeMatcher<String> isUUID() {
        return new TypeSafeMatcher<String>() {
            @Override
            public boolean matchesSafely(String s) {
                try {
                    UUID ifItDoesntThrowItsAGoodUUID = UUID.fromString(s);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {  }
        };
    }


}
