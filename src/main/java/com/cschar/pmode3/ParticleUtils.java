package com.cschar.pmode3;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ParticleUtils {
    private static final Logger LOGGER = Logger.getLogger( ParticleUtils.class.getName() );

    public static BufferedImage loadSprite(String name){

        try {
            return ImageIO.read(ParticleSpriteLightning.class.getResource(name));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex );
        }
        return null;
    }

//    BufferedImage resized_image =  Scalr.resize(tmp, Scalr.Method.BALANCED,
//                    tmp.getWidth()/2, tmp.getHeight()/2);
//

}
