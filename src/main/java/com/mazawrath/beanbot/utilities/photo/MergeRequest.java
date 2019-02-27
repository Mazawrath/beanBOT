package com.mazawrath.beanbot.utilities.photo;

import marvin.MarvinPluginCollection;
import marvin.image.MarvinImage;
import marvin.plugin.MarvinImagePlugin;
import marvin.util.MarvinPluginLoader;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MergeRequest {
    private List<MarvinImage> images = new ArrayList<>();

    public MergeRequest(URL[] url) throws IOException {
        for (URL url1 : url)
            images.add(new MarvinImage(ImageIO.read(new ByteArrayInputStream(downloadFile(url1)))));
    }

    public File getMerge() throws IOException {
        MarvinImage marvinOutput = new MarvinImage();
        MarvinPluginCollection.mergePhotos(images, marvinOutput, 38);

        marvinOutput.update();
        File output = new File("merge.png");
        ImageIO.write(marvinOutput.getBufferedImage(), "png", output);
        return output;
    }

    private static byte[] downloadFile(URL url) {
        try {
            URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
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
