package com.mazawrath.beanbot.utilities;

import com.mazawrath.beanbot.utilities.Points;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;

public class BeanTalk {

    public static void createListener(DiscordApi api, Points points) {
        api.addMessageCreateListener(event -> {
            Message message = event.getMessage();
            if (!message.getAuthor().isYourself()) {
                points.giveFreePoints(message.getAuthor().getIdAsString(), message.getServer().get().getIdAsString());
            }
        });
    }
}
