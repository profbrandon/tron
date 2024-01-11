import java.io.File;
import java.io.IOException;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

class ImageHandler {

    public static BufferedImage getImage (String path) {

        BufferedImage buff = null;

        try {
            buff = ImageIO.read (new File (path));
        }
        catch (IOException e) {
            System.out.println ("Failed to load '" + path + "'");
        }

        return buff;
    }
}