package com.mazawrath.beanbot.utilities.photo;

import marvin.MarvinPluginCollection;
import marvin.image.MarvinImage;
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
    URL url2;
    BufferedImage buffImage;
    MarvinImage image;

    public MarvinRequest(URL url) throws IOException {
        url2 = url;
        buffImage = ImageIO.read(new ByteArrayInputStream(downloadFile(url)));
        image = new MarvinImage(buffImage);
    }

    public File deepFryImage() throws IOException {
        MarvinPluginCollection.brightnessAndContrast(image, 10, 140);
        MarvinPluginCollection.colorChannel(image, 100, 10, 10);
        image.update();

        File output = new File("Output.png");
        ImageIO.write(image.getBufferedImage(), "png", output);
        return output;
    }

    public File embossImage() throws IOException {
        MarvinPluginCollection.emboss(image, image);
        image.update();

        File output = new File("Output.png");
        ImageIO.write(image.getBufferedImage(), "png", output);
        return output;
    }

    public File invertImage() throws IOException {
        MarvinPluginCollection.invertColors(image);
        image.update();

        File output = new File("Output.png");
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
