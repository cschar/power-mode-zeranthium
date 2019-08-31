package com.cschar.pmode3.config;

import com.cschar.pmode3.Particle;
import com.cschar.pmode3.ParticleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SparkData{
    public int roundRobinAmount=100;
    public boolean enabled=true;
    public boolean customPathEnabled =true;
    public String path="/tmp";
    public ImageIcon previewIcon;
    public BufferedImage image;

    public SparkData(boolean enabled, int roundRobinAmount, boolean customPathEnabled, String path) {
        this.roundRobinAmount = roundRobinAmount;
        this.enabled = enabled;
        this.customPathEnabled = customPathEnabled;
        this.path = path;


        ImageIcon imageIcon = new ImageIcon(this.getClass().getResource(path));
//        ImageIcon imageIcon = sparkData[row].path
        Image image = imageIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(60, 60,  Image.SCALE_SMOOTH); // scale it the smooth way
        previewIcon = new ImageIcon(newimg);

        this.image = ParticleUtils.loadSprite(path);
    }
}
