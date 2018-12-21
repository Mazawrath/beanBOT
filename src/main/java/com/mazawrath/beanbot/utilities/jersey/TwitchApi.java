package com.mazawrath.beanbot.utilities.jersey;

import com.google.api.client.util.Base64;

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
        sha256Encrypter(payload, signature);
        System.out.println("Someone went live");
        System.out.println(payload);
        System.out.println(length);
        System.out.println(signature);
    }


    private String sha256Encrypter(String payload, String secret) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(payload.getBytes()));
            System.out.println(hash);
            return hash;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
