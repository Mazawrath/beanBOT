package com.mazawrath.beanbot.utilities.photo;

import marvin.MarvinPluginCollection;
import marvin.image.MarvinImage;
import marvin.image.MarvinImageMask;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class MarvinRequest {
    private MarvinImage image;

    public MarvinRequest(URL url) throws IOException {
        image = new MarvinImage(ImageIO.read(new ByteArrayInputStream(downloadFile(url))));
    }

    public File getDeepFry() throws IOException {
        int pixelValue = image.getHeight() * image.getWidth() ;
        MarvinPluginCollection.pixelize(image, image, 1);
        MarvinPluginCollection.brightnessAndContrast(image, 0, 140);
        MarvinPluginCollection.colorChannel(image, 100, 10, 10);
        image.update();

        File output = new File(".\\ext\\out\\deepfry.png");
        ImageIO.write(image.getBufferedImage(), "png", output);
        return output;
    }

    public File getEmboss() throws IOException {
        MarvinPluginCollection.emboss(image, image);
        image.update();

        File output = new File(".\\ext\\out\\emboss.png");
        ImageIO.write(image.getBufferedImage(), "png", output);
        return output;
    }

    public File getInvert() throws IOException {
        MarvinPluginCollection.invertColors(image);
        image.update();

        File output = new File(".\\ext\\out\\invert.png");
        ImageIO.write(image.getBufferedImage(), "png", output);
        return output;
    }

    public File getEdges() throws IOException {
        MarvinPluginCollection.sobel(image,image);
        image.update();

        File output = new File(".\\ext\\out\\edge.png");
        ImageIO.write(image.getBufferedImage(), "png", output);
        return output;
    }

    public File getErrorDiffusion() throws IOException {
        MarvinPluginCollection.halftoneErrorDiffusion(image,image);
        image.update();

        File output = new File(".\\ext\\out\\errorTone.png");
        ImageIO.write(image.getBufferedImage(), "png", output);
        return output;
    }

    public File getMosaic() throws IOException {
        MarvinPluginCollection.mosaic(image,image, "triangles", 10, true);
        image.update();

        File output = new File(".\\ext\\out\\mosaic.png");
        ImageIO.write(image.getBufferedImage(), "png", output);
        return output;
    }

    public File getSepia() throws IOException {
        MarvinPluginCollection.sepia(image, 100);
        image.update();

        File output = new File(".\\ext\\out\\sepia.png");
        ImageIO.write(image.getBufferedImage(), "png", output);
        return output;
    }

    public File getHistogram() throws IOException {
        MarvinPluginCollection.histogramEqualization(image, image);
        image.update();

        File output = new File(".\\ext\\out\\histogram.png");
        ImageIO.write(image.getBufferedImage(), "png", output);
        return output;
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
