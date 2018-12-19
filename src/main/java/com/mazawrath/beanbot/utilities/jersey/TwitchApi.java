package com.mazawrath.beanbot.utilities.jersey;
import com.sun.research.ws.wadl.Response;

import javax.ws.rs.*;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import java.net.URL;

@Path("/twitchapi")
public class TwitchApi {
    @GET
    @Path("/subscription")
    @Produces(MediaType.APPLICATION_JSON)
    public Result subscription(@QueryParam("hub.mode") String mode, @QueryParam("hub.topic") URL topic,
                                        @QueryParam("hub.lease_seconds") int seconds, @QueryParam("hub.secret") String secret){
        System.out.println("A thing happened");
        return null;
    }

    private class Result{
        double input;
        double output;
        String action;

        public Result(){}

        public Result(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public double getInput() {
            return input;
        }

        public void setInput(double input) {
            this.input = input;
        }

        public double getOutput() {
            return output;
        }

        public void setOutput(double output) {
            this.output = output;
        }
    }
}
