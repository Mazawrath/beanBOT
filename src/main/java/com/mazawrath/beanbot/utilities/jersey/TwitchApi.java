package com.mazawrath.beanbot.utilities.jersey;

import com.google.api.client.util.Base64;
import com.mazawrath.beanbot.utilities.LivestreamNotification;
import com.mazawrath.beanbot.utilities.Twitch;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.time.Instant;

@Path("/twitchapi")
public class TwitchApi {
    @GET
    @Path("/stream_changed")
    @Produces(MediaType.TEXT_PLAIN)
    public String subscription(@QueryParam("hub.mode") String mode, @QueryParam("password") String password, @QueryParam("hub.topic") String topic,
                               @QueryParam("hub.lease_seconds") int seconds, @QueryParam("hub.challenge") String challenge) {
        String retVal = "";

        System.out.println("Password: " + password);
        String databasePassword = Twitch.getPassword(Long.valueOf(topic.substring(44)));
        System.out.println("Database password = " + databasePassword);

        if (databasePassword != null) {
            if (password.equals(databasePassword)) {
                System.out.println("Password matches");

                if (mode.equals("subscribe"))
                    System.out.println("Subscription to " + topic.substring(44) + " received.");
                else if (mode.equals("unsubscribe")) {
                    System.out.println("Unsubscribe from " + topic.substring(44) + " received.");
                    Twitch.removeTwitchChannel(Long.valueOf(topic.substring(44)));
                }
                retVal = challenge;
            } else
                System.out.println("Password doesn't match");
        } else
            System.out.println("Database password not found");
        return retVal;
    }

    @POST
    @Path("/stream_changed")
    public void response(@HeaderParam("x-hub-signature") String signature, @HeaderParam("content-length") int length, @QueryParam("password") String password, @QueryParam("username") String userName, String payload) {
        System.out.println("POST received.");
        JSONObject payloadJson = new JSONObject(payload);
        if (payloadJson.getJSONArray("data").length() != 0) {
            System.out.println("Someone went live");
            payloadJson = new JSONObject(payload).getJSONArray("data").getJSONObject(0);

            System.out.println("ID: " + payloadJson.getString("user_id"));

            String databasePassword = Twitch.getPassword(Long.valueOf(payloadJson.getString("user_id")));

            System.out.println("Password: " + password);
            System.out.println("Database password = " + databasePassword);
            //
            //            if (databasePassword != null) {
            //                if (password.equals(databasePassword)) {
            //                    System.out.println("Password matches");
            //                } else
            //                    System.out.println("Password doesn't match");
            //            } else
            //                System.out.println("Database password not found");

            System.out.println("ID: " + payloadJson.getString("user_id"));
            System.out.println("User Name: " + payloadJson.getString("user_name"));
            System.out.println("Header user Name: " + userName);
            System.out.println("Thumbnail: " + payloadJson.getString("thumbnail_url"));
            System.out.println("Title: " + payloadJson.getString("title"));

            if (!payloadJson.getString("user_name").equals(""))
                userName = payloadJson.getString("user_name");

            String hubSecret = Twitch.getHubSecret(payloadJson.getLong("user_id"));

            if (hubSecret != null) {
                System.out.println("Key found.");

                String hashCheck = sha256Encryptor(payload, hubSecret);
                System.out.println("Hash check : " + hashCheck);
            } else
                System.out.println("Secret not found.");
            System.out.println("Actual signature: " + signature);

            long streamStartInstant = Instant.parse(payloadJson.getString("started_at")).getEpochSecond();
            System.out.println("Started at: " + streamStartInstant);

            //if ((System.currentTimeMillis() / 1000) - streamStartInstant < 180)
            if (!Twitch.getStatus(payloadJson.getLong("user_id")) && System.currentTimeMillis() - Twitch.getOfflineTime(payloadJson.getLong("user_id")) > 600000) {
                Twitch.notifyLive(new LivestreamNotification(payloadJson.getString("user_id"), userName, payloadJson.getString("title"), payloadJson.getString("game_id"), payloadJson.getInt("viewer_count"), payloadJson.getString("thumbnail_url")));
                Twitch.setStatus(payloadJson.getLong("user_id"), true);
            } else
                System.out.println("Streamer recently live. Not notifying.");

        } else {
            System.out.println("Someone went offline");
            long userId = Twitch.getUserID(userName);

            Twitch.setStatus(userId, false);
            Twitch.setOfflineTime(userId);
        }

        System.out.println(length);
    }


    private String sha256Encryptor(String payload, String secret) {
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
