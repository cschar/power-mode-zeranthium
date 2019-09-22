package com.cschar.pmode3.config.common;

import com.cschar.pmode3.ParticleSpriteLightning;
import com.cschar.pmode3.ParticleUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class SpriteData extends PathData{

    public float scale=1.0f;


    public ImageIcon previewIcon;
    public BufferedImage image;


    public SpriteData(boolean enabled, float scale, int val1, String defaultPath, String customPath) {
        super(enabled, defaultPath,customPath, val1);
        this.scale = scale;

        setupImage();

    }

    private void setupImage(){
        if(customPath.equals("")){
            setImage(defaultPath, true);
        }else if(!defaultPath.equals("")){
            setImage(customPath, false);
        }
    }

    public SpriteData(boolean enabled, float scale, int val1, String defaultPath, String customPath, boolean doSetupImage) {
        super(enabled, defaultPath,customPath, val1);
        this.scale = scale;
        if(doSetupImage) setupImage();
    }

    public boolean setImage(String path, boolean isResource){
        ImageIcon imageIcon;
        if(isResource){
            imageIcon = new ImageIcon(this.getClass().getResource(path));
            Image image = imageIcon.getImage(); // transform it
            Image newimg = image.getScaledInstance(60, 60,  Image.SCALE_SMOOTH); // scale it the smooth way
            previewIcon = new ImageIcon(newimg);
            this.image = ParticleUtils.loadSprite(path);
            return true;
        }else{

            try {
                File f = new File(path);
                this.image = ImageIO.read(f);

                customPathValid = true;

                imageIcon = new ImageIcon(path);
                Image image = imageIcon.getImage(); // transform it
                Image newimg = image.getScaledInstance(60, 60, Image.SCALE_SMOOTH); // scale it the smooth way
                previewIcon = new ImageIcon(newimg);

            } catch (IOException e) {
                Logger logger  = Logger.getLogger(ParticleSpriteLightning.class.getName());
                logger.severe("error loading image file: " + path);
                logger.severe(e.toString());

                setImage(this.defaultPath, true);

                customPathValid = false;
                return false;
            }
        }
        return false;
    }

}
