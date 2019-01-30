package com.mazawrath.beanbot.utilities;

import io.sentry.Sentry;
import io.sentry.event.UserBuilder;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

public class SentryLog {
    public static void addContext(String[] args, ServerTextChannel serverTextChannel, User author, Server server) {
        Sentry.getContext().addExtra("Arguments", args);
        Sentry.getContext().addTag("serverId", server.getIdAsString());

        Sentry.getContext().setUser(
                new UserBuilder()
                        .setId(author.getIdAsString())
                        .setUsername(author.getDiscriminatedName()).build()
        );
    }
}
