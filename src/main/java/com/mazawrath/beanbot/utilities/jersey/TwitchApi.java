package com.mazawrath.beanbot.utilities.jersey;

import com.google.api.client.util.Base64;
import com.mazawrath.beanbot.utilities.LivestreamNotification;
import com.mazawrath.beanbot.utilities.Twitch;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URL;

@Path("/twitchapi")
public class TwitchApi {
    @GET
    @Path("/stream_changed")
    @Produces(MediaType.TEXT_PLAIN)
    public String subscription(@QueryParam("hub.mode") String mode, @QueryParam("password") String password, @QueryParam("hub.topic") String topic,
                               @QueryParam("hub.lease_seconds") int seconds, @QueryParam("hub.challenge") String challenge) {
        System.out.println("Password: " + password);
        String databasePassword = Twitch.getPassword(Long.valueOf(topic.substring(44)));
        System.out.println("Database password = " + databasePassword);

        if (mode.equals("subscribe"))
            System.out.println("Subscription to " + topic.substring(44) + " received.");
        else if (mode.equals("unsubscribe")) {
            System.out.println("Unsubscribe from " + topic.substring(44) + " received.");
            Twitch.removeTwitchChannel(Long.valueOf(topic.substring(44)));
        }

        if (databasePassword != null) {
            if (password.equals(databasePassword)) {
                System.out.println("Password matches");
            } else
                System.out.println("Password doesn't match");
        } else
            System.out.println("Database password not found");
        return challenge;
    }

    @POST
    @Path("/stream_changed")
    public void response(@HeaderParam("x-hub-signature") String signature, @HeaderParam("content-length") int length, @QueryParam("password") String password, @QueryParam("username") String userName, String payload) {
        System.out.println("Someone went live");

        JSONObject payloadJson = new JSONObject(payload);
        if (payloadJson.getJSONArray("data").length() != 0) {
            payloadJson = new JSONObject(payload).getJSONArray("data").getJSONObject(0);

            String databasePassword = Twitch.getPassword(Long.valueOf(payloadJson.getString("user_id")));

            System.out.println("Password: " + password);
            System.out.println("Database password = " + databasePassword);

            if (databasePassword != null) {
                if (password.equals(databasePassword)) {
                    System.out.println("Password matches");
                } else
                    System.out.println("Password doesn't match");
            } else
                System.out.println("Database password not found");

            System.out.println("ID: " + payloadJson.getString("user_id"));
            System.out.println("User Name: " + payloadJson.getString("user_name"));
            System.out.println("Header user Name: " + userName);
            System.out.println("Thumbnail: " + payloadJson.getString("thumbnail_url"));
            System.out.println("Title: " + payloadJson.getString("title"));

            //String hashCheck = sha256Encrypter(payload.substring(2, payload.length() - 1), Twitch.getHubSecret(payloadJson.getLong("id")));
            //System.out.println(hashCheck);
            System.out.println(signature);

            Twitch.notifyLive(new LivestreamNotification(payloadJson.getString("user_id"), userName, payloadJson.getString("title"), payloadJson.getString("game_id"), payloadJson.getString("thumbnail_url")));

        } else
            System.out.println("Someone went offline");

        System.out.println(length);
    }


    private String sha256Encrypter(String payload, String secret) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            return Base64.encodeBase64String(sha256_HMAC.doFinal(payload.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
