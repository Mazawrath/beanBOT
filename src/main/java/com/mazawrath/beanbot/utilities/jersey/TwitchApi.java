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
    @Path("/subscription")
    @Produces(MediaType.TEXT_PLAIN)
    public String subscription(@QueryParam("hub.mode") String mode, @QueryParam("hub.topic") URL topic,
                               @QueryParam("hub.lease_seconds") int seconds, @QueryParam("hub.challenge") String challenge) {
        System.out.println("Subscription to " + topic + " received.");
        return challenge;
    }

    @POST
    @Path("/subscription")
    public void response(@HeaderParam("x-hub-signature") String signature, @HeaderParam("content-length") int length, String payload) {
        // TODO handle livestream notification
        System.out.println("Someone went live");
        sha256Encrypter(payload, "very_secret");
        System.out.println(payload);
        System.out.println(signature);

        JSONObject payloadJson = new JSONObject(payload);
        if (payloadJson.getJSONArray("data").length() != 0) {
            payloadJson = new JSONObject(payload).getJSONArray("data").getJSONObject(0);
            System.out.println("ID: " + payloadJson.getString("user_id"));
            System.out.println("User Name: " + payloadJson.getString("user_name"));
            System.out.println("Thumbnail: " + payloadJson.getString("thumbnail_url"));
            System.out.println("Title: " + payloadJson.getString("title"));

            Twitch.notifyLive(new LivestreamNotification(payloadJson.getString("id"), payloadJson.getString("user_name"), payloadJson.getString("title"), payloadJson.getString("game_id"), payloadJson.getString("thumbnail_url")));

        } else
            System.out.println("Someone went offline");

        System.out.println(length);
    }


    private String sha256Encrypter(String payload, String secret) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(payload.getBytes()));
            System.out.println(hash);
            return hash;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
