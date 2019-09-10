package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteLightning;
import com.cschar.pmode3.ParticleUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class SpriteDataAnimated {
    public float scale =1.0f;
    public int speedRate = 2;
    public boolean enabled=true;

    public String defaultPath;
    public String customPath;

    public boolean customPathValid = false;


    public ImageIcon previewIcon;


    public ArrayList<BufferedImage> images;
    public ArrayList<ImageIcon> previewIcons;

    private boolean isAnimated = false;

    public SpriteDataAnimated(boolean enabled, float scale, int speedRate, String defaultPath, String customPath) {
        this.scale = scale;
        this.enabled = enabled;
        this.speedRate = speedRate;
        this.customPath = customPath;
        this.defaultPath = defaultPath;

        images = new ArrayList<BufferedImage>();
        previewIcons = new ArrayList<ImageIcon>();

        if(customPath == ""){
            setImageAnimated(defaultPath, true);
        }
        else{
            setImageAnimated(customPath, false);
        }

    }

    public void setImageAnimated(String path, boolean isResource){
        ImageIcon imageIcon;

        if(images.size() != 0){
            images = new ArrayList<BufferedImage>();
        }
        if(previewIcons.size() != 0){
            previewIcons = new ArrayList<ImageIcon>();
        }



        //https://stackoverflow.com/questions/11300847/load-and-display-all-the-images-from-a-folder


        // array of supported extensions (use a List if you prefer)
        final String[] EXTENSIONS = new String[]{
                "gif", "png" // and other formats you need
        };
        // filter to identify images based on their extensions
        final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

            @Override
            public boolean accept(final File dir, final String name) {
                for (final String ext : EXTENSIONS) {
                    if (name.endsWith("." + ext)) {
                        return (true);
                    }
                }
                return (false);
            }
        };

        if(isResource){

            // File representing the folder that you select using a FileChooser
            final File dir = new File(String.valueOf(this.getClass().getResource(path)));
            System.out.println("Loading resources");
            System.out.println(dir.getPath());

            for(int i = 0; i<40; i++){
                String tmpPath = path + String.format("/00%02d.png", i);
                System.out.println(tmpPath);

                    URL imageURL = this.getClass().getResource(tmpPath);
                    if(imageURL == null){
                        break;
                    }
                    imageIcon = new ImageIcon(imageURL);

                    Image image = imageIcon.getImage(); // transform it
                    Image newimg = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH); // scale it the smooth way
                    previewIcon = new ImageIcon(newimg);
                    this.previewIcons.add(previewIcon);
                    BufferedImage loadedImage = ParticleUtils.loadSprite(tmpPath);
                    if(loadedImage != null) {
                        this.images.add(loadedImage);
                    }
            }
        }else{
            final File dir = new File(path);

            if (dir.isDirectory()) { // make sure it's a directory
                System.out.println("Loading directory");

                File[] files = dir.listFiles(IMAGE_FILTER);
                Arrays.sort(files);
                //Load Buffered Images
                try {
                    for (final File f : files) {
                        BufferedImage img = null;

                        System.out.println(f.getPath());

                        img = ImageIO.read(f);
                        images.add(img);
                    }
                } catch (final IOException e) {
                    Logger logger  = Logger.getLogger(ParticleSpriteLightning.class.getName());
                    logger.severe("error loading image directory: " + path);
                    setImageAnimated(this.defaultPath, true);
                    customPathValid = false;
                    return;
                }

                //Load previewIcons
                for (final File f : files) {

                    imageIcon = new ImageIcon(f.getPath());
                    Image image = imageIcon.getImage(); // transform it
                    Image newimg = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH); // scale it the smooth way
                    previewIcon = new ImageIcon(newimg);
                    previewIcons.add(previewIcon);
                }
                customPathValid = true;

            }

//            if (errorLoadingDirectory){ }
        }


    }
}
