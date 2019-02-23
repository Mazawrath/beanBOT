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
import java.util.List;

public class MergeRequest {
    private List<MarvinImage> images;

    public MergeRequest(URL[] url) throws IOException {
        for (int i = 0; i < url.length; i++) {
            images.add(new MarvinImage(ImageIO.read(new ByteArrayInputStream(downloadFile(url[i])))));
        }
    }

    public File getMerge() throws IOException {
        MarvinImage marvinOutput = new MarvinImage();
        MarvinPluginCollection.mergePhotos(images, marvinOutput, 1);

        marvinOutput.update();
        File output = new File("merge.png");
        ImageIO.write(marvinOutput.getBufferedImage(), "png", output);
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
