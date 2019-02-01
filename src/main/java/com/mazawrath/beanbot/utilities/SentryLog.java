package com.mazawrath.beanbot.utilities;

import io.sentry.Sentry;
import io.sentry.event.UserBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class SentryLog {
    public static void addContext(String[] args, User author, Server server) {
        Sentry.getContext().addExtra("Arguments", args);
        Sentry.getContext().addTag("serverId", server.getIdAsString());

        Sentry.getContext().setUser(
                new UserBuilder()
                        .setId(author.getIdAsString())
                        .setUsername(author.getDiscriminatedName()).build()
        );
    }
}
