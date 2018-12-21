package com.mazawrath.beanbot.utilities.jersey;

import org.json.JSONObject;

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
    public void response(@HeaderParam("x-hub-signature") String signature, @HeaderParam("content-length") int length, JSONObject response) {
        // TODO handle livestream notification
        System.out.println("Someone went live");
        System.out.println(response);
        System.out.println(length);
        System.out.println(signature);
    }
}
