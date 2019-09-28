package com.cschar.pmode3.config.common;

import com.cschar.pmode3.ParticleSpriteLightning;
import com.cschar.pmode3.ParticleUtils;
import com.cschar.pmode3.PowerMode3;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.messages.impl.Message;
import org.json.JSONException;
import org.json.JSONObject;


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
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpriteDataAnimated  extends SpriteData {
    private static final Logger LOGGER = Logger.getLogger( SpriteDataAnimated.class.getName() );

    public int speedRate = 2;
    public boolean isCyclic=false;
    public int val2 =20;

    public int previewSize=60;


    public ArrayList<BufferedImage> images = new ArrayList<>();
//    public ArrayList<ImageIcon> previewIcons = new ArrayList<>();

    public float alpha =1.0f;

    private int MAX_NUM_FILES = 500;
    private double MAX_TOTAL_GB_SIZE = 1.0;

    public SpriteDataAnimated(int previewSize, boolean enabled, float scale, int speedRate, String defaultPath, String customPath,
                              boolean isCyclic, int val2, float alpha, int val1) {
        super(enabled, scale, val1, defaultPath, customPath,false);

        this.previewSize = previewSize;
        this.speedRate = speedRate;
        this.isCyclic = isCyclic;
        this.val2 = val2; //maxNumCYcleParticles
        this.alpha = alpha;

        //TODO do some sanity check so w'ere not loading a million files in each of 30 mb size etc..
        // all files are the same dimensions
        if(customPath.equals("")){
            setImageAnimated(defaultPath, true);
//            customPathValid = true; //only valid if empty path
        }
        else{
            setImageAnimated(customPath, false);
        }

    }


    public void setImageAnimated(String path, boolean isResource){
        ImageIcon imageIcon;



        //https://stackoverflow.com/questions/11300847/load-and-display-all-the-images-from-a-folder

        // array of supported extensions (use a List if you prefer)
        final String[] EXTENSIONS = new String[]{
                "png" // and other formats you need
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
            images = new ArrayList<BufferedImage>();
//            previewIcons = new ArrayList<ImageIcon>();


            // File representing the folder that you select using a FileChooser
            final File dir = new File(String.valueOf(this.getClass().getResource(path)));
            LOGGER.info("Loading default resources: " + dir.getPath());

            for(int i = 0; i<100; i++){
                String tmpPath = path + String.format("/0%03d.png", i);

                    URL imageURL = this.getClass().getResource(tmpPath);
                    if(imageURL == null){
                        continue;
//                        break;
                    }
//                    imageIcon = new ImageIcon(imageURL);
//
//                    Image image = imageIcon.getImage(); // transform it
//                    Image newimg = image.getScaledInstance(previewSize, previewSize, Image.SCALE_SMOOTH); // scale it the smooth way
//                    previewIcon = new ImageIcon(newimg);
//                    this.previewIcons.add(previewIcon);
                    BufferedImage loadedImage = ParticleUtils.loadSprite(tmpPath);
                    if(loadedImage != null) {
                        this.images.add(loadedImage);
                    }
            }
            this.image = this.images.get(this.images.size()-1);
            Image previewImage = image.getScaledInstance(previewSize, previewSize, Image.SCALE_SMOOTH);
            this.previewIcon = new ImageIcon(previewImage);

        }else{
            ArrayList<BufferedImage> newImages = new ArrayList<BufferedImage>();
//            ArrayList<ImageIcon> newPreviewIcons = new ArrayList<ImageIcon>();

            final File dir = new File(path);

            if (dir.isDirectory()) { // make sure it's a directory
                LOGGER.info("Loading customPath directory");

                File[] files = dir.listFiles(IMAGE_FILTER);
                Arrays.sort(files);

                double totalSize = 0;
                int fileCount = 0;
                //Load Buffered Images
                try {
                    for (final File f : files) {
                        totalSize += getGBFileSize(f);
                        fileCount += 1;

                        if(totalSize > MAX_TOTAL_GB_SIZE){
                            String msg = path + "\n Reached Max Size: " + MAX_TOTAL_GB_SIZE + " GB of files already loaded";
                            notifyError(msg);
                            break;
                        }
                        if(fileCount > MAX_NUM_FILES){
                            String msg = path + "\n Reached Max File Count: " + MAX_NUM_FILES + " files already loaded";
                            notifyError(msg);
                            break;
                        }

                        BufferedImage img = null;
                        img = ImageIO.read(f);
                        newImages.add(img);
                    }

                } catch (final IOException e) {
                    LOGGER.severe("error loading image directory: " + path);
                    setImageAnimated(this.defaultPath, true);
                    customPathValid = false;
                    return;
                }

                //Load previewIcons
//                for (final File f : files) {
//
//                    imageIcon = new ImageIcon(f.getPath());
//                    Image image = imageIcon.getImage(); // transform it
//                    Image newimg = image.getScaledInstance(previewSize, previewSize, Image.SCALE_SMOOTH); // scale it the smooth way
//                    newPreviewIcons.add(new ImageIcon(newimg));
//                }

//                if(newPreviewIcons.size() == 0 || newImages.size() == 0){
                if(newImages.size() == 0){
                    LOGGER.severe("No images found in directory: " + path);
                    this.customPathValid = false;
                }else{
;
                    this.image = newImages.get(newImages.size()-1);
                    this.images = newImages;

                    this.customPathValid = true;
//                    this.previewIcons = newPreviewIcons;
//                    this.previewIcon = newPreviewIcons.get(0);
//                    newPreviewIcons.add(new ImageIcon(newimg))
                    Image previewImage = image.getScaledInstance(previewSize, previewSize, Image.SCALE_SMOOTH); // scale it the smooth way
                    this.previewIcon = new ImageIcon(previewImage);
                }


            }
        }
    }

    @Override
    public JSONObject toJSONObject(){
        ////enabled, scale, speed, defaultPath, customPath, isCyclic, val2, alpha, val1
        JSONObject jo = new JSONObject();
        try {
            jo.put("previewSize", this.previewSize);
            jo.put("enabled", this.enabled);
            jo.put("scale", this.scale);
            jo.put("speedRate", this.speedRate);
            jo.put("defaultPath",this.defaultPath);
            jo.put("customPath",this.customPath);
            jo.put("isCyclic",this.isCyclic);
            jo.put("val2",this.val2);
            jo.put("alpha",this.alpha);
            jo.put("val1",this.val1);


        }catch(JSONException e){
            LOGGER.log(Level.SEVERE,e.toString(),e);
        }

        return jo;
    }

    public static SpriteDataAnimated fromJsonObjectString(String s){


        SpriteDataAnimated sd = null;
        try {
            JSONObject jo = new JSONObject(s);
            sd =  new SpriteDataAnimated(
                    jo.getInt("previewSize"),
                    jo.getBoolean("enabled"),
                    (float) jo.getDouble("scale"),
                    jo.getInt("speedRate"),
                    jo.getString("defaultPath"),
                    jo.getString("customPath"),
                    jo.getBoolean("isCyclic"),
                    jo.getInt("val2"),
                    (float) jo.getDouble("alpha"),
                    jo.getInt("val1"));


        }catch(JSONException e){
            LOGGER.log(Level.SEVERE,e.toString(),e);
        }

//public SpriteDataAnimated(int previewSize, boolean enabled, float scale, int speedRate, String defaultPath, String customPath,
//        boolean isCyclic, int val2, float alpha, int val1) {

        return sd;
    }


    private double getGBFileSize(File file){
        double bytes = file.length();
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);
        double gigabytes = (megabytes / 1024);
        return gigabytes;
    }

    private void notifyError(String msg){
        LOGGER.severe(msg);
        Notification n = new Notification(PowerMode3.NOTIFICATION_GROUP_DISPLAY_ID,
                PowerMode3.NOTIFICATION_GROUP_DISPLAY_ID + ": Error loading image set",
                msg,
                NotificationType.WARNING);
        Notifications.Bus.notify(n);
    }
}
