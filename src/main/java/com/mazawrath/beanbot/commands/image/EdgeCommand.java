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
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class EdgeCommand implements CommandExecutor {
    private Points points;

    public EdgeCommand(Points points) {
        this.points = points;
    }

    @Command(
            aliases = {"edge"},
            usage = "edge [url]",
            description = "Creates a deep fried image with custom options to decide the brightness and contrast.",
            privateMessages = false
    )

    public void onCommand(String[] args, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server, Message message) {
        SentryLog.addContext(args, author, server);

        URL url;
        if (message.getAttachments().size() != 0)
            url = message.getAttachments().get(0).getUrl();
        else if (args.length > 0) {
            try {
                url = new URL(args[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                serverTextChannel.sendMessage("URL is not valid.");
                return;
            }
        } else {
            serverTextChannel.sendMessage("You must either have a URL in your message or an attachment.");
            return;
        }

        if (urlContainsImage(url)) {
            try {
                MarvinRequest request = new MarvinRequest(url);
                serverTextChannel.sendMessage(request.getEdges());
            } catch (Exception e) {
                e.printStackTrace();
                serverTextChannel.sendMessage("Something went wrong.");
                return;
            }
        }

        Sentry.clearContext();
    }

    private boolean urlContainsImage(URL url) {
        File f = new File(url.toString());
        String mimetype = new MimetypesFileTypeMap().getContentType(f);
        String type = mimetype.split("/")[0];
        return type.equals("image");
    }
}
