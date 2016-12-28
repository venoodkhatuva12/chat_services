package de.affinitas.chat;

import de.affinitas.chat.listener.BroadcastChatListener;
import de.affinitas.chat.messagequeue.ConnectionNotEstablished;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/chat/")
public class PublishChat {

    @POST
    @Path("publish/{channel_id}/{user_id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response publishChat(@PathParam("channel_id") String channelIdString, @PathParam("user_id") String userIdString, String message) {
        UUID channelId = UUID.fromString(channelIdString);
        UUID userId = UUID.fromString(userIdString);
        String messageId = UUID.randomUUID().toString();
        try {
            String toBroadcast = formatMessage(userId.toString(), messageId, message);
            BroadcastChatListener.broadcast(channelId, toBroadcast);
            return Response.ok().header("Access-Control-Allow-Origin", "*").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).header("Access-Control-Allow-Origin", "*").build();
        } catch (ConnectionNotEstablished e) {
            return Response.status(Response.Status.ACCEPTED).header("Access-Control-Allow-Origin", "*").build();
        }
    }

    private String formatMessage(String userId, String messageId, String message) {
        return "{\"id\":\"" + messageId + "\",\"userId\":\"" + userId + "\",\"message\":\"" + message + "\"}";
    }
}