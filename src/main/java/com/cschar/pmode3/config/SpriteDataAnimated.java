package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

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
//        else{
//            setImageAnimated(customPath, false);
//        }

    }

    public void setImageAnimated(String path, boolean isResource){
        ImageIcon imageIcon;

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


            System.out.println("Loading resource");
            System.out.println(dir.getPath());
//            File[] files = new File(dir.getPath()).listFiles();
//            System.out.println(files[0].getPath());


            for(int i = 0; i<20; i++){
                String tmpPath = path + String.format("/00%02d.png", i);
                System.out.println(tmpPath);

                imageIcon = new ImageIcon(this.getClass().getResource(tmpPath));
                Image image = imageIcon.getImage(); // transform it
                Image newimg = image.getScaledInstance(120, 120,  Image.SCALE_SMOOTH); // scale it the smooth way
                previewIcon = new ImageIcon(newimg);
                this.previewIcons.add(previewIcon);
                this.images.add(ParticleUtils.loadSprite(tmpPath));
            }
////            if (dir.isDirectory()) { // make sure it's a directory
//                System.out.println("Loading directory");
//                for (final File f : dir.listFiles(IMAGE_FILTER)) {
//                    BufferedImage img = null;
//
//                    URL resourcePath = this.getClass().getResource(f.getPath());
//                    System.out.println(f.getPath());
//                    try {
//                        img = ImageIO.read(f);
//                        images.add(img);
//                    } catch (final IOException e) {
//                        // handle errors here
//                    }
//                }
////            }
//
////            if (dir.isDirectory()) { // make sure it's a directory
//                for (final File f : dir.listFiles(IMAGE_FILTER)) {
//                    BufferedImage img = null;
//
////                    try {
//
//                        imageIcon = new ImageIcon(f.getPath());
//                        if(imageIcon != null) {
//                            Image image = imageIcon.getImage(); // transform it
//                            Image newimg = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH); // scale it the smooth way
//                            previewIcon = new ImageIcon(newimg);
//                            previewIcons.add(previewIcon);
//                        }
//
////                    } catch (final IOException e) {
////                        // handle errors here
////                    }
//                }
//            }



        }
//        else{
//
//            try {
//                File f = new File(path);
//                this.images[0] = ImageIO.read(f);
//
//                customPathValid = true;
//
//                imageIcon = new ImageIcon(path);
//                Image image = imageIcon.getImage(); // transform it
//                Image newimg = image.getScaledInstance(60, 60, Image.SCALE_SMOOTH); // scale it the smooth way
//                previewIcon = new ImageIcon(newimg);
//
//            } catch (IOException e) {
//                Logger logger  = Logger.getLogger(ParticleSpriteLightning.class.getName());
//                logger.severe("error loading image file: " + path);
//
//                setImageAnimated(this.defaultPath, true);
//
//                customPathValid = false;
//
//            }
//        }

    }
}
