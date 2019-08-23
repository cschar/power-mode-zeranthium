package com.cschar.pmode3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;

public class ParticleUtils {
    public static BufferedImage loadSprite(String name){
        try {
            return ImageIO.read(ParticleSpriteLightning.class.getResource(name));
        } catch (IOException e) {
            Logger logger  = Logger.getLogger(ParticleSpriteLightning.class.getName());
            logger.severe("error loading image file: " + name);
//            System.out.println("error loading image");
            e.printStackTrace();
        }
        return null;
    }
}
