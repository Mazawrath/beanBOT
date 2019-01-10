package com.mazawrath.beanbot.commands.googlevision;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.SafeSearchAnnotation;
import com.mazawrath.beanbot.utilities.GoogleCloudVision;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import org.apache.commons.lang3.text.WordUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.Embed;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import javax.activation.MimetypesFileTypeMap;
import java.awt.*;
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
            privateMessages = false,
            async = true
    )

    public void onCommand(String[] args, Message message, DiscordApi api, ServerTextChannel serverTextChannel, User author, Server server) {
        List<EntityAnnotation> labelAnnotation;
        AnnotateImageResponse faceDetection;
        SafeSearchAnnotation safeSearchAnnotation;

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
                safeSearchAnnotation = cloudVision.detectSafeSearch(url);
            } catch (Exception e) {
                e.printStackTrace();
                serverTextChannel.sendMessage("Something went wrong.");
                return;
            }
        } else {
            serverTextChannel.sendMessage("URL must be an image.");
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Image Analysis")
                .setColor(Color.BLUE);

        StringBuilder labels = new StringBuilder();
        for (int i = 0; i < labelAnnotation.size(); i++) {
            if (i != labelAnnotation.size() - 1) {
                labels.append(labelAnnotation.get(i).getDescription()).append(" (").append(Math.round(labelAnnotation.get(0).getScore() * 100)).append("%), ");
            } else
                labels.append(labelAnnotation.get(i).getDescription()).append(" (").append(Math.round(labelAnnotation.get(0).getScore() * 100)).append("%)");
        }

        embed.addField("Things I see", labels.toString());

        embed.addField("Faces I See", String.valueOf(faceDetection.getFaceAnnotationsCount()));

        for (int i = 0; i < faceDetection.getFaceAnnotationsCount(); i++) {
            StringBuilder emotionsSeen = new StringBuilder();
            if (faceDetection.getFaceAnnotations(i).getJoyLikelihoodValue() > 1)
                emotionsSeen.append("joy, ");
            if (faceDetection.getFaceAnnotations(i).getSorrowLikelihoodValue() > 1)
                emotionsSeen.append("sorrow, ");
            if (faceDetection.getFaceAnnotations(i).getAngerLikelihoodValue() > 1)
                emotionsSeen.append("anger, ");
            if (faceDetection.getFaceAnnotations(i).getSurpriseLikelihoodValue() > 1)
                emotionsSeen.append("surprise, ");

            if (emotionsSeen.length() != 0)
                embed.addField("Face " + (i + 1) + "'s Possible Emotions", emotionsSeen.substring(0, emotionsSeen.length() - 2));
            else
                embed.addField("Face " + (i + 1) + "'s Possible Emotions", "none");
        }

        if (safeSearchAnnotation.getAdultValue() > 2)
            embed.addField("Adult Content", WordUtils.capitalizeFully(safeSearchAnnotation.getAdult().name().replaceAll("_", " ")));
        if (safeSearchAnnotation.getSpoofValue() > 2)
            embed.addField("Spoof / Edited Phto", WordUtils.capitalizeFully(safeSearchAnnotation.getSpoof().name().replaceAll("_", " ")));
        if (safeSearchAnnotation.getMedicalValue() > 2)
            embed.addField("Blood / Gore", WordUtils.capitalizeFully(safeSearchAnnotation.getMedical().name().replaceAll("_", " ")));
        if (safeSearchAnnotation.getViolenceValue() > 2)
            embed.addField("Violence", WordUtils.capitalizeFully(safeSearchAnnotation.getViolence().name().replaceAll("_", " ")));
        if (safeSearchAnnotation.getRacyValue() > 2)
            embed.addField("Skimpy / Nudity", WordUtils.capitalizeFully(safeSearchAnnotation.getRacy().name().replaceAll("_", " ")));

        serverTextChannel.sendMessage(embed);
    }

    private boolean urlContainsImage(URL url) {
        File f = new File(url.toString());
        String mimetype = new MimetypesFileTypeMap().getContentType(f);
        String type = mimetype.split("/")[0];
        return type.equals("image");
    }
}
