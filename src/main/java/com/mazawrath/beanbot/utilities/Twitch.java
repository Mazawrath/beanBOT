package com.mazawrath.beanbot.utilities;

import me.philippheuer.twitch4j.TwitchClient;
import me.philippheuer.twitch4j.TwitchClientBuilder;
import me.philippheuer.twitch4j.model.Channel;

import java.io.File;

public class Twitch {
    private TwitchClient client;

    public void connectClient(String id, String secret, String credential) {
        client = TwitchClientBuilder.init()
                .withClientId(id)
                .withClientSecret(secret)
                .withAutoSaveConfiguration(true)
                .withConfigurationDirectory(new File("config"))
                .withCredential(credential) // Get your token at: https://twitchapps.com/tmi/
                .connect();
    }

    public boolean checkIfLive(String channel) {
        return client.getStreamEndpoint().isLive(client.getChannelEndpoint().getChannel(channel));
    }
}
