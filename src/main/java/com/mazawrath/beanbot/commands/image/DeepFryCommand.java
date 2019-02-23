package com.mazawrath.beanbot.commands.image;

import com.mazawrath.beanbot.utilities.Points;
import com.mazawrath.beanbot.utilities.SentryLog;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import marvin.MarvinPluginCollection;
import marvin.image.MarvinImage;
import org.apache.commons.io.IOUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
                BufferedImage buffImage = ImageIO.read(new ByteArrayInputStream(downloadFile(url)));
                MarvinImage image = new MarvinImage(buffImage);

                MarvinPluginCollection.brightnessAndContrast(image, 10, 140);
                MarvinPluginCollection.colorChannel(image, 100, 10, 10);
                image.update();
                File output = new File("Output.png");
                ImageIO.write(image.getBufferedImage(), "png", output);
                serverTextChannel.sendMessage(output);
            } catch (Exception e) {
                e.printStackTrace();
                serverTextChannel.sendMessage("Something went wrong.");
                return;
            }
        }

    }

    private boolean urlContainsImage(URL url) {
        File f = new File(url.toString());
        String mimetype = new MimetypesFileTypeMap().getContentType(f);
        String type = mimetype.split("/")[0];
        return type.equals("image");
    }

    private static byte[] downloadFile(URL url) {
        try {
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(conn.getInputStream(), baos);

            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
