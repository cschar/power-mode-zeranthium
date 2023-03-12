package com.cschar.pmode3.config.common;

import com.cschar.pmode3.ParticleUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import com.intellij.openapi.diagnostic.Logger;

public class SpriteData extends PathData{

    private static final Logger LOGGER = Logger.getInstance( SpriteData.class.getName() );

    public float scale=1.0f;


    public ImageIcon previewIcon;
    public BufferedImage image;


    public SpriteData(boolean enabled, float scale, int val1, String defaultPath, String customPath) {
        super(enabled, defaultPath,customPath, val1);
        this.scale = scale;
        if(this.scale < 0.1) this.scale = 0.1f;
        if(this.scale > 2) this.scale = 2.0f;

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

                LOGGER.error("error loading image file: " + path);
                LOGGER.error(e);

                setImage(this.defaultPath, true);

                customPathValid = false;
                return false;
            }
        }
        return false;
    }

    @Override
    public JSONObject toJSONObject(){
        ////enabled, scale, speed, defaultPath, customPath, isCyclic, val2, alpha, val1
        JSONObject jo = new JSONObject();
        try {
            jo.put("enabled", this.enabled);
            jo.put("scale", this.scale);
//            jo.put("speed", this.speedRate);
            jo.put("defaultPath",this.defaultPath);
            jo.put("customPath",this.customPath);
//            jo.put("isCyclic",this.isCyclic);
//            jo.put("val2",this.val2);
//            jo.put("alpha",this.alpha);
            jo.put("val1",this.val1);
        }catch(JSONException e){
            LOGGER.error(e.toString(),e);
        }

        return jo;
    }

//    public static SpriteData fromJsonObject(JSONObject jo){
    public static SpriteData fromJsonObjectString(String s){


        SpriteData sd = null;
        try {
            JSONObject jo = new JSONObject(s);
            sd =  new SpriteData(jo.getBoolean("enabled"),
                   (float) jo.getDouble("scale"),
                   jo.getInt("val1"),
                   jo.getString("defaultPath"),
                   jo.getString("customPath"));


        }catch(JSONException e){
            LOGGER.error(e.toString(),e);
        }

        //public SpriteData(boolean enabled, float scale, int val1, String defaultPath, String customPath, boolean doSetupImage) {
        return sd;
    }

}
