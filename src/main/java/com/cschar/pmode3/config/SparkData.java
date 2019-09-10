package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteLightning;
import com.cschar.pmode3.ParticleUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class SparkData{
    public int weightedAmount =100;
    public float scale=1.0f;
    public boolean enabled=true;

    public String defaultPath;
    public String customPath;

    public boolean customPathValid = false;


    public ImageIcon previewIcon;


    public BufferedImage image;

    public SparkData(boolean enabled, float scale, int weightedAmount, String defaultPath, String customPath) {
        this.weightedAmount = weightedAmount;
        this.enabled = enabled;
        this.scale = scale;
        this.customPath = customPath;
        this.defaultPath = defaultPath;


        if(customPath == ""){
            setImage(defaultPath, true);
        }else{
            setImage(customPath, false);
        }

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

                setImage(this.defaultPath, true);

                customPathValid = false;
                return false;
            }
        }
        return false;
    }
}
