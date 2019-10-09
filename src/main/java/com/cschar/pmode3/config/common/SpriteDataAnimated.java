package com.cschar.pmode3.config.common;

import com.cschar.pmode3.ParticleSpriteLightning;
import com.cschar.pmode3.PowerMode3;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import org.json.JSONException;
import org.json.JSONObject;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.jar.JarFile;
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

    private double totalLoadedAssetSizeMB =0;

    public SpriteDataAnimated(int previewSize, boolean enabled, float scale, int speedRate, String defaultPath, String customPath,
                              boolean isCyclic, int val2, float alpha, int val1) {
        super(enabled, scale, val1, defaultPath, customPath,false);

        this.previewSize = previewSize;
        this.speedRate = speedRate;
        this.isCyclic = isCyclic;
        this.val2 = val2; //maxNumCYcleParticles
        this.alpha = alpha;
        if(this.alpha < 0.0f) this.alpha = 0.f;
        if(this.alpha > 1.0f) this.alpha = 1.0f;
        if(this.speedRate < 1) this.speedRate = 1;
        if(this.speedRate > 10) this.speedRate = 10;
//        if(this.val2 < 1) this.val2 = 1;
//        if(this.val2 > 20) this.val2 = 20;


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
//            LOGGER.info("Loading default resources: " + dir.getPath());

            double totalSizeMBResources = 0;

            for(int i = 0; i<100; i++){
                String tmpPath = path + String.format("/0%03d.png", i);

                    URL imageURL = this.getClass().getResource(tmpPath);
                    if(imageURL == null){
                        continue;
//                        break;
                    }


                    BufferedImage loadedImage = null;
                    try {

                        loadedImage  = ImageIO.read(imageURL);
//                        DataBuffer dataBuffer = loadedImage.getRaster().getDataBuffer();
////                        double sizeBytes = ((double) dataBuffer.getSize());
//                        //getSize --> size in bits
//                        int sizeBytes = dataBuffer.getSize() * DataBuffer.getDataTypeSize(dataBuffer.getDataType()) / 8;
//                        double sizeMB = sizeBytes / (1024.0 * 1024.0);
                        //files inside jar alway return length = 0 ! ! ! ! ! ! !
//                        File f = new File(String.valueOf(imageURL));

                        InputStream tmpStream = imageURL.openStream();
                        int length = tmpStream.available();  //Not supposed to work in all VMs... https://stackoverflow.com/a/18237279/5198805
                        tmpStream.close();
                        double sizeMB = length / 1024.0 / 1024.0;



                        totalSizeMBResources += sizeMB;
//                        System.out.println(String.format("1 asset is size %.9f - %s", sizeMB, imageURL.toString()));

                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, ex.toString(), ex );
                    }

//                    BufferedImage loadedImage = ParticleUtils.loadSprite(tmpPath);
                    if(loadedImage != null) {
                        this.images.add(loadedImage);
                    }


            }
            this.image = this.images.get(this.images.size()-1);
            Image previewImage = image.getScaledInstance(previewSize, previewSize, Image.SCALE_SMOOTH);
            this.previewIcon = new ImageIcon(previewImage);

            this.totalLoadedAssetSizeMB = totalSizeMBResources;
//            LOGGER.info(String.format("Loaded assets with size MB: %.2f",this.totalLoadedAssetSizeMB));
            LOGGER.info(String.format("Loaded default resource %s    size MB: %.2f", dir.getPath(), this.totalLoadedAssetSizeMB));

        }else{
            ArrayList<BufferedImage> newImages = new ArrayList<BufferedImage>();
//            ArrayList<ImageIcon> newPreviewIcons = new ArrayList<ImageIcon>();

            final File dir = new File(path);

            if (dir.isDirectory()) { // make sure it's a directory
                LOGGER.info("Loading customPath directory: " + path);

                File[] files = dir.listFiles(IMAGE_FILTER);
                Arrays.sort(files);

                double totalSizeGB = 0;
                int fileCount = 0;
                //Load Buffered Images
                try {
                    for (final File f : files) {
                        totalSizeGB += getGBFileSize(f);
                        fileCount += 1;

                        if(totalSizeGB > MAX_TOTAL_GB_SIZE){
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

                    this.totalLoadedAssetSizeMB = totalSizeGB * 1024;
                }


            }
        }



    }

    public double getAssetSizeMB(){
        return this.totalLoadedAssetSizeMB;
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
        double kilobytes = (bytes / 1024.0);
        double megabytes = (kilobytes / 1024.0);
        double gigabytes = (megabytes / 1024.0);
        return gigabytes;
    }

    private double getMBFileSize(File file){
        double bytes = file.length();
        double kilobytes = (bytes / 1024.0);
        double megabytes = (kilobytes / 1024.0);
        return megabytes;
    }

    private void notifyError(String msg){
        LOGGER.severe(msg);
        Notification n = new Notification(PowerMode3.NOTIFICATION_GROUP_DISPLAY_ID,
                PowerMode3.NOTIFICATION_GROUP_DISPLAY_ID + ": Error loading image set",
                msg,
                NotificationType.WARNING);
        Notifications.Bus.notify(n);
    }


    public static void drawSprite(Graphics2D g2d, Point p0, Point p1, SpriteDataAnimated pData, int frame){
        if( !pData.enabled) return;

        AffineTransform at = new AffineTransform();
        at.scale(pData.scale, pData.scale);
        at.translate((int) p0.x * (1 / pData.scale), (int) p0.y * (1 / pData.scale));


        double radius = Point.distance(p0.x, p0.y, p1.x, p1.y);
        int adjacent = (p0.x - p1.x);
        double initAnchorAngle = Math.acos(adjacent / radius);

        if (p0.y - p1.y < 0) {
            initAnchorAngle *= -1;
        }

        at.rotate(initAnchorAngle);

        at.translate(-pData.image.getWidth() / 2,
                -pData.image.getHeight() / 2);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pData.alpha));


        g2d.drawImage(pData.images.get(frame), at, null);
    }
}
