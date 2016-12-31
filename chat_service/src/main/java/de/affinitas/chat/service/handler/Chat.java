package de.affinitas.chat.service.handler;

import com.eclipsesource.json.JsonObject;
import de.affinitas.chat.messagequeue.ConnectionNotEstablished;
import de.affinitas.chat.service.listener.BroadcastChatListener;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.UUID;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/chat/")
public class Chat {

    @POST
    @Path("publish/{channel_id}/{user_id}")
    @Produces(TEXT_PLAIN)
    public Response publishChat(@PathParam("channel_id") String channelIdString, @PathParam("user_id") String userIdString, String message) {
        try {
            UUID channelId = UUID.fromString(channelIdString);
            UUID userId = UUID.fromString(userIdString);
            String messageId = UUID.randomUUID().toString();
            String toBroadcast = formatMessage(userId.toString(), messageId, message);
            broadcast(channelId, toBroadcast);
            //TODO: remove the live cross scripting attack potential - need to implement reverse proxy
            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        } catch (ConnectionNotEstablished e) {
            return Response.status(Response.Status.ACCEPTED)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        }
    }

    protected void broadcast(UUID channelId, String toBroadcast) {
        BroadcastChatListener.broadcast(channelId, toBroadcast);
    }

    private String formatMessage(String userId, String messageId, String message) {
        JsonObject reply = new JsonObject();
        return reply
                .set("id", messageId)
                .set("userId", userId)
                .set("message", message)
               .toString();
    }
}