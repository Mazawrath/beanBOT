package com.mazawrath.beanbot.commands.googlevision;

import com.mazawrath.beanbot.utilities.ImageRequest;
import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.NonThrowingAutoCloseable;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class AnalyzeCommand implements CommandExecutor {
    private Points points;

    public AnalyzeCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"analyze"},
            usage = "cloudVision",
            privateMessages = false,
            async = true
    )

    public void onCommand(String[] args, Message message, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        SentryLog.addContext(args, author, server);

        URL url;
        if (message.getAttachments().size() != 0)
            url = message.getAttachments().get(0).getUrl();
        else if (args.length > 0) {
            try {
                url = new URL(args[0]);
            } catch (MalformedURLException e) {
                serverTextChannel.sendMessage("URL is not valid.");
                return;
            }
        } else {
            serverTextChannel.sendMessage("You must either have a URL in your message or an attachment.");
            return;
        }

        if (!points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.GOOGLE_VISION_COST)) {
            serverTextChannel.sendMessage("You do not have enough beanCoin for this command");
            return;
        }

        ImageRequest imageRequest;
        try (NonThrowingAutoCloseable typingIndicator = serverTextChannel.typeContinuouslyAfter(5, TimeUnit.MICROSECONDS)) {


            if (urlContainsImage(url)) {
                try {
                    imageRequest = new ImageRequest(url);
                } catch (Exception e) {
                    e.printStackTrace();
                    serverTextChannel.sendMessage("Something went wrong.");
                    return;
                }
            } else {
                serverTextChannel.sendMessage("URL must be an image.");
                return;
            }
        }

            serverTextChannel.sendMessage(imageRequest.buildEmbed());

        Sentry.clearContext();
    }

    private boolean urlContainsImage(URL url) {
        Image image = new ImageIcon(url).getImage();
        return image.getWidth(null) != -1;
    }
}
