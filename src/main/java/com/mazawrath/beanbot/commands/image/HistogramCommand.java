package com.mazawrath.beanbot.commands.image;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import com.mazawrath.beanbot.utilities.photo.MarvinRequest;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import io.sentry.Sentry;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import javax.swing.*;
import java.net.URL;

public class HistogramCommand implements CommandExecutor {
    private Points points;

    public HistogramCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"histogram"},
            usage = "histogram [url]",
            description = "Creates a histogram image.",
            privateMessages = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server, Message message) {
        SentryLog.addContext(args, author, server);

        if (!points.removePoints(author.getIdAsString(), api.getYourself().getIdAsString(), server.getIdAsString(), Points.COMMAND_COST)) {
            serverTextChannel.sendMessage("You do not have enough beanCoin for this command");
            return;
        }

        serverTextChannel.type();

        URL url = null;
        if (message.getAttachments().size() != 0)
            url = message.getAttachments().get(0).getUrl();
        else {
            MessageSet previousMessages = message.getMessagesBefore(20).join();

            for (Message previousMessage: previousMessages.descendingSet()) {
                URL urlTest;
                if (previousMessage.getAttachments().size() != 0) {
                    urlTest = previousMessage.getAttachments().get(0).getUrl();
                    if (urlContainsImage(urlTest)) {
                        url = urlTest;
                        break;
                    }
                }
            }
        }
        if (url == null) {
            serverTextChannel.sendMessage("You must either have a URL in your message or an attachment.");
            return;
        }

        if (urlContainsImage(url)) {
            try {
                MarvinRequest request = new MarvinRequest(url);
                serverTextChannel.sendMessage(request.getHistogram());
            } catch (Exception e) {
                e.printStackTrace();
                serverTextChannel.sendMessage("Something went wrong.");
                return;
            }
        }

        Sentry.clearContext();
    }

    private boolean urlContainsImage(URL url) {
        return new ImageIcon(url).getImage().getWidth(null) != -1;
    }
}
