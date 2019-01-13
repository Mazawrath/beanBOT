package com.mazawrath.beanbot.utilities;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.SafeSearchAnnotation;
import com.google.cloud.vision.v1.WebDetection;
import org.apache.commons.lang3.text.WordUtils;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.net.URL;
import java.util.List;

public class ImageRequest {
    private URL image;

    private List<EntityAnnotation> labelAnnotation;
    private AnnotateImageResponse faceDetection;
    private SafeSearchAnnotation safeSearchAnnotation;
    private WebDetection webDetection;

    public ImageRequest(URL image) {
        this.image = image;
        GoogleCloudVision cloudVision = new GoogleCloudVision();

        try {
            labelAnnotation = cloudVision.getLabelDetection(image);
            faceDetection = cloudVision.getFaceDetection(image);
            safeSearchAnnotation = cloudVision.detectSafeSearch(image);
            webDetection = cloudVision.getWebDetection(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public URL getImage() {
        return image;
    }

    public List<EntityAnnotation> getLabelAnnotation() {
        return labelAnnotation;
    }

    public AnnotateImageResponse getFaceDetection() {
        return faceDetection;
    }

    public SafeSearchAnnotation getSafeSearchAnnotation() {
        return safeSearchAnnotation;
    }

    public WebDetection getWebDetection() {
        return webDetection;
    }

    public EmbedBuilder buildEmbed() {
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

        embed.addField("Things I See", labels.toString());

        embed.addInlineField("Faces I See", String.valueOf(faceDetection.getFaceAnnotationsCount()));

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
                embed.addInlineField("Face " + (i + 1) + "'s Possible Emotions", WordUtils.capitalizeFully(emotionsSeen.substring(0, emotionsSeen.length() - 2)));
            else
                embed.addInlineField("Face " + (i + 1) + "'s Possible Emotions", "none");
        }
        embed.addInlineField("Best Guess", webDetection.getBestGuessLabels(0).getLabel());

        StringBuilder webLabels = new StringBuilder();
        for (int i = 0; i < webDetection.getWebEntitiesCount(); i++) {
            if (i != webDetection.getWebEntitiesCount() - 1)
                webLabels.append(webDetection.getWebEntities(i).getDescription()).append(" (").append(Math.round(webDetection.getWebEntities(i).getScore() * 100)).append("%), ");
            else
                webLabels.append(webDetection.getWebEntities(i).getDescription()).append(" (").append(Math.round(webDetection.getWebEntities(i).getScore() * 100)).append("%)");
        }
        embed.addField("Things I Think This Is", webLabels.toString());

        if (safeSearchAnnotation.getAdultValue() > 2)
            embed.addInlineField("Adult Content", WordUtils.capitalizeFully(safeSearchAnnotation.getAdult().name().replaceAll("_", " ")));
        if (safeSearchAnnotation.getSpoofValue() > 2)
            embed.addInlineField("Spoof / Edited Photo", WordUtils.capitalizeFully(safeSearchAnnotation.getSpoof().name().replaceAll("_", " ")));
        if (safeSearchAnnotation.getMedicalValue() > 2)
            embed.addInlineField("Medical / Surgery", WordUtils.capitalizeFully(safeSearchAnnotation.getMedical().name().replaceAll("_", " ")));
        if (safeSearchAnnotation.getViolenceValue() > 2)
            embed.addInlineField("Violence / Blood / Gore", WordUtils.capitalizeFully(safeSearchAnnotation.getViolence().name().replaceAll("_", " ")));
        if (safeSearchAnnotation.getRacyValue() > 2)
            embed.addInlineField("Skimpy / Nudity", WordUtils.capitalizeFully(safeSearchAnnotation.getRacy().name().replaceAll("_", " ")));

        return embed;
    }
}
