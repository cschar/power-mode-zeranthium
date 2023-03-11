package com.cschar.pmode3;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import com.intellij.openapi.diagnostic.Logger;

public class ParticleUtils {
    private static final Logger LOGGER = Logger.getInstance( ParticleUtils.class.getName() );

    public static BufferedImage loadSprite(String name){

        try {
            return ImageIO.read(ParticleUtils.class.getResource(name));
        } catch (IOException ex) {
            LOGGER.error(ex.toString(), ex);
        }
        return null;
    }


}
