package com.mazawrath.beanbot.commands.googlevision;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.mazawrath.beanbot.utilities.GoogleCloudVision;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class AnalyzeCommand implements CommandExecutor {
    private GoogleCloudVision cloudVision;

    public AnalyzeCommand(GoogleCloudVision cloudVision) {
        this.cloudVision = cloudVision;
    }

    @Command(
            aliases = {"analyze"},
            usage = "cloudVision",
            privateMessages = false
    )

    public void onCommand(String[] args, Message message, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        MessageBuilder messageBuilder = new MessageBuilder();
        List<EntityAnnotation> labelAnnotation = null;
        List<AnnotateImageResponse> faceDetection = null;
        URL url;
        if (message.getAttachments().size() != 0)
            url = message.getAttachments().get(0).getUrl();
        else {
            try {
                url = new URL(args[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                serverTextChannel.sendMessage("URL is not valid.");
                return;
            }
        }
        if (urlContainsImage(url)) {
            try {
                labelAnnotation = cloudVision.getLabelDetection(url);
                faceDetection = cloudVision.getFaceDetection(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            serverTextChannel.sendMessage("URL must be an image.");
            return;
        }
        for (int i = 0; i <labelAnnotation.size(); i++)
            messageBuilder.append("I see a " + labelAnnotation.get(i).getDescription() + "\n");
        for (int i = 0; i < faceDetection.size(); i++)
            messageBuilder.append("I see " + faceDetection.get(i).getFaceAnnotations(i).getAngerLikelihoodValue());
        messageBuilder.send(serverTextChannel);
    }

    private boolean urlContainsImage(URL url) {
        File f = new File(url.toString());
        String mimetype= new MimetypesFileTypeMap().getContentType(f);
        String type = mimetype.split("/")[0];
        return type.equals("image");
    }
}
