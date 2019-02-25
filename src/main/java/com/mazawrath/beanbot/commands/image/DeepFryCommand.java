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
import java.awt.*;
import java.net.URL;

public class DeepFryCommand implements CommandExecutor {
    private Points points;

    public DeepFryCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"deepfry"},
            usage = "deepfry [url] [[brightness] [contrast]]",
            description = "Creates a deep fried image with custom options to decide the brightness and contrast.",
            privateMessages = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server, Message message) {
        SentryLog.addContext(args, author, server);

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
                serverTextChannel.sendMessage(request.getDeepFry());
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
