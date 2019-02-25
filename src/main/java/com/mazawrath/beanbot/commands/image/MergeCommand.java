package com.mazawrath.beanbot.commands.image;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import com.mazawrath.beanbot.utilities.photo.MergeRequest;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import javax.activation.MimetypesFileTypeMap;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MergeCommand implements CommandExecutor {
    private Points points;

    public MergeCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"merge"},
            usage = "merge [urls]",
            description = "Merges multiple images into one.",
            privateMessages = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server, Message message) {
        SentryLog.addContext(args, author, server);

        URL[] urls = new URL[args.length];
        if (args.length > 1) {
            for (int i = 0; i < args.length; i++) {
                try {
                    urls[i] = new URL(args[i]);
                    if (!urlContainsImage(urls[i])) {
                        serverTextChannel.sendMessage(urls[i] + " is not a valid image.");
                        return;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    serverTextChannel.sendMessage("Invalid URL.");
                    return;
                }
            }
        } else {
            serverTextChannel.sendMessage("You must either have at least 2 URL's in your message.");
            return;
        }

        try {
            MergeRequest request = new MergeRequest(urls);
            serverTextChannel.sendMessage(request.getMerge());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Sentry.clearContext();
    }

    private boolean urlContainsImage(URL url) {
        return new ImageIcon(url).getImage().getWidth(null) != -1;
    }
}
