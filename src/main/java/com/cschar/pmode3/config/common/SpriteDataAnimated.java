package com.cschar.pmode3.config.common;

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
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import com.intellij.openapi.diagnostic.Logger;

/**
 * <pre>
 * Represents a Row in a Sprite Table
 *
 * Also contains ImageData for that Sprite Sequence
 * </pre>
 */
public class SpriteDataAnimated  extends SpriteData {
    private static final Logger LOGGER = Logger.getInstance( SpriteDataAnimated.class.getName() );

    public int speedRate = 2;
    public boolean isCyclic=false;
    public int val2 =20;
    public int val3 = 0;

    public int previewSize=60;

    //flush these images on dispose
    public ArrayList<BufferedImage> images = new ArrayList<>();
//    public ArrayList<ImageIcon> previewIcons = new ArrayList<>();

    public float alpha =1.0f;
    private int MAX_NUM_DEFAULT_FILES = 100;
    private int MAX_NUM_FILES = 500;
    private double MAX_TOTAL_GB_SIZE = 1.0;

    private double totalLoadedAssetSizeMB =0;
    private FilenameFilter IMAGE_FILTER;
    private String currentPath = "";

    public SpriteDataAnimated(int previewSize, boolean enabled, float scale, int speedRate, String defaultPath, String customPath,
                              boolean isCyclic, int val2, float alpha, int val1, int val3) {
        super(enabled, scale, val1, defaultPath, customPath,false);

//        this.image.flush();
        this.previewSize = previewSize;
        this.speedRate = speedRate;
        this.isCyclic = isCyclic;
        this.val2 = val2; //maxNumCYcleParticles
        this.val3 = val3; //max particle length
        this.alpha = alpha;
        if(this.alpha < 0.0f) this.alpha = 0.f;
        if(this.alpha > 1.0f) this.alpha = 1.0f;
        if(this.speedRate < 1) this.speedRate = 1;
        if(this.speedRate > 10) this.speedRate = 10;
//        if(this.val2 < 1) this.val2 = 1;
//        if(this.val2 > 20) this.val2 = 20;


        final String[] EXTENSIONS = new String[]{
                "png" // and other formats you need
        };
        // filter to identify images based on their extensions
        IMAGE_FILTER = new FilenameFilter() {
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

        if(customPath.equals("")){
            setImageAnimated(defaultPath, true);
        }else{
            setImageAnimated(customPath, false);
        }
    }


    public void loadImages(){
        if(customPath.equals("")){
            setImageAnimated(defaultPath, true);
        }else{
            setImageAnimated(customPath, false);
        }
    }

    public void unloadImages(){
        LOGGER.trace("Unloading " + this.images.size() + " images from path: " + this.currentPath);
        for(BufferedImage i : this.images){
            i.flush();
        }
        this.images.clear();

    }


    public void setImageAnimated(String path, boolean isResource){
        currentPath = path;
        //https://stackoverflow.com/questions/11300847/load-and-display-all-the-images-from-a-folder
        // array of supported extensions (use a List if you prefer)

        //If its part of the built-in blender resource images...
        if(isResource){
            images = new ArrayList<BufferedImage>();

            // File representing the folder that you select using a FileChooser
            final File dir = new File(String.valueOf(this.getClass().getResource(path)));
//            LOGGER.info("Loading default resources: " + dir.getPath());

            double totalSizeMBResources = 0;

            //load in images, then determine each ones filesize
            for(int i = 0; i<MAX_NUM_DEFAULT_FILES; i++){
                String tmpPath = path + String.format("/0%03d.png", i);
                URL imageURL = this.getClass().getResource(tmpPath);
                if(imageURL == null){
                    continue;
                }

                BufferedImage loadedImage = null;
                try {
                    loadedImage  = ImageIO.read(imageURL);
                    // this method is not working..
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

                } catch (IOException ex) {
                    LOGGER.error(ex.toString(), ex );
                }
                if(loadedImage != null) {
                    this.images.add(loadedImage);
                }
            }


            this.image = this.images.get(this.images.size()-1);
            Image previewImage = image.getScaledInstance(previewSize, previewSize, Image.SCALE_SMOOTH);
            this.previewIcon = new ImageIcon(previewImage);

            this.totalLoadedAssetSizeMB = totalSizeMBResources;
//            LOGGER.info(String.format("Loaded assets with size MB: %.2f",this.totalLoadedAssetSizeMB));
            LOGGER.debug(String.format("Loaded default resource %s    size MB: %.2f", dir.getPath(), this.totalLoadedAssetSizeMB));

        }else{
            ArrayList<BufferedImage> newImages = new ArrayList<BufferedImage>();
//            ArrayList<ImageIcon> newPreviewIcons = new ArrayList<ImageIcon>();

            final File dir = new File(path);

            if (dir.isDirectory()) { // make sure it's a directory


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

                        BufferedImage img = ImageIO.read(f);
                        newImages.add(img);
                    }

                } catch (final IOException e) {
                    LOGGER.warn("error loading image directory: " + path);
                    LOGGER.debug("falling back to default path: " + this.defaultPath);
                    this.setImageAnimated(this.defaultPath, true);
                    customPathValid = false;
                    return;
                }

                if(newImages.size() == 0){
                    LOGGER.info("No images found in directory: " + path);
                    this.customPathValid = false;
                }else{
;
                    this.image = newImages.get(newImages.size()-1);
                    this.images = newImages;
                    this.customPathValid = true;

                    Image previewImage = image.getScaledInstance(previewSize, previewSize, Image.SCALE_SMOOTH); // scale it the smooth way
                    this.previewIcon = new ImageIcon(previewImage);

                    this.totalLoadedAssetSizeMB = totalSizeGB * 1024;
                    LOGGER.debug("Loaded customPath directory: " + path + "   total size (MB): " + this.totalLoadedAssetSizeMB);
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
            jo.put("alpha",this.alpha);
            jo.put("val1",this.val1);
            jo.put("val2",this.val2);
            jo.put("val3",this.val3);


        }catch(JSONException e){
            LOGGER.error(e.toString(),e);
        }

        return jo;
    }

    public static SpriteDataAnimated fromJsonObjectString(String s){
        SpriteDataAnimated sd = null;
        try {
            JSONObject jo = new JSONObject(s);
            return fromJSONObject(jo);
        }catch(JSONException e){
            LOGGER.error(e.toString(),e);
        }
        return sd;
    }

    /** used only for resources since defaultPath is defined there */
    public static SpriteDataAnimated fromJSONObject(JSONObject jo){
        SpriteDataAnimated sd = null;
            //TODO REMOVE PREVIEW SIZE, set in table directly

            sd =  new SpriteDataAnimated(

//                  jo.getInt("previewSize),,
                    (Integer) safeUnwrap(0,"previewSize", jo),

                    (Boolean) safeUnwrap(false, "enabled", jo),
//                    (float) jo.getDouble("scale"),
                    (float) safeUnwrap(0.0f, "scale", jo),
//                    jo.getInt("speedRate"),
                    (Integer) safeUnwrap(0, "speedRate", jo),
//                    jo.getString("defaultPath"),
                    (String) safeUnwrap("", "defaultPath", jo),
//                    jo.getString("customPath"),
                    (String) safeUnwrap("", "customPath", jo),
//                    jo.getBoolean("isCyclic"),
                    (Boolean) safeUnwrap(false, "isCyclic", jo),
//                    jo.getInt("val2"),
                    (Integer) safeUnwrap(0, "val2", jo),
//                    (float) jo.getDouble("alpha"),
                    (float) safeUnwrap(0.0f, "alpha", jo),
//                    jo.getInt("val1"),
                    (Integer) safeUnwrap(0, "val1", jo),
//                    jo.getInt("val3")
                    (Integer) safeUnwrap(-1, "val3", jo)
            );

        return sd;
    }

    public static Object safeUnwrap( Object myDefault, String inputValue, JSONObject jo) {
        try {
            if(myDefault.getClass().equals(Boolean.class)){
                return jo.getBoolean(inputValue);
            }else if(myDefault.getClass().equals(Double.class)){
                return (float) jo.getDouble(inputValue);
            }else if(myDefault.getClass().equals(Float.class)){
                return (float) jo.getDouble(inputValue);
            }else if(myDefault.getClass().equals(Integer.class)){
                return jo.getInt(inputValue);
            }else if(myDefault.getClass().equals(String.class)){
                return jo.getString(inputValue);
            }
        } catch (Exception e) {
//            LOGGER.warning("Could not unwrap value: " + inputValue);
            return myDefault;
        }
//        LOGGER.warning("Could not identify value to unwrap: " + inputValue);
        return 0;
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
        LOGGER.error(msg);
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
//        at.translate((int) p0.x, (int) p0.y);


        double radius = Point.distance(p0.x, p0.y, p1.x, p1.y);
        int adjacent = (p0.x - p1.x);
        double initAnchorAngle = Math.acos(adjacent / radius);

        if (p0.y - p1.y < 0) {
            initAnchorAngle *= -1;
        }

        at.rotate(initAnchorAngle);

        at.translate(-pData.image.getWidth() / 2.0,
                     -pData.image.getHeight() / 2.0);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pData.alpha));


        g2d.drawImage(pData.images.get(frame), at, null);
    }
}
