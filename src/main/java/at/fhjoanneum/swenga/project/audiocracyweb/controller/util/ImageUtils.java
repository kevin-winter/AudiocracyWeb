/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.fhjoanneum.swenga.project.audiocracyweb.controller.util;

/**
 *
 * @author Fabian
 */
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;


public class ImageUtils {
 
    public static enum MaxType {MAX_HEIGHT,MAX_WIDTH,MAX_DIMENSION};
 
    public static byte[] resizeImage(InputStream content, String type, int maxDimension, MaxType maxDimensionType) throws IOException {
        BufferedImage org = ImageIO.read(content);
        double scaleFactor = 1.0;
        switch (maxDimensionType) {
            case MAX_HEIGHT: scaleFactor = (double) maxDimension / org.getHeight(); break;
            case MAX_WIDTH: scaleFactor = (double) maxDimension / org.getWidth(); break;    
            case MAX_DIMENSION: scaleFactor = (double) maxDimension / Math.max(org.getWidth(),org.getHeight()); break;
        }
        int width = (int) (scaleFactor * org.getWidth());
        int height = (int) (scaleFactor * org.getHeight());
        BufferedImage scaledImage = new BufferedImage(width, height, org.getType());
        Graphics g = scaledImage.getGraphics();
        g.drawImage(org.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH), 0, 0, width, height, null);
        g.dispose();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(scaledImage, type, out);
        return out.toByteArray();
    }
}