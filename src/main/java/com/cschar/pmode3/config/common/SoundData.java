package com.cschar.pmode3.config.common;

import com.cschar.pmode3.ParticleSpriteLightning;
import com.cschar.pmode3.ParticleUtils;
import com.intellij.openapi.vfs.VirtualFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class SoundData extends PathData{

    public boolean enabled=true;
    public int val1 =100;

    public SoundData(boolean enabled, int val1, String defaultPath, String customPath) {
        super(defaultPath, customPath);
        this.val1 = val1;
        this.enabled = enabled;
    }

    public boolean setValidMP3Path(VirtualFile f){

        if(!f.getExtension().equals("mp3")) {
            this.customPath = f.getPath();
            this.customPathValid = false;
            return false;
        }else{
            this.customPath = f.getPath();
            this.customPathValid = true;
            return true;
        }
    }

    public String getPath(){
        if(!this.customPath.equals("") && !this.customPathValid){
            return this.defaultPath;
        }else{
            return this.customPath;
        }
    }
//
//    public boolean setImage(String path, boolean isResource){
//        ImageIcon imageIcon;
//        if(isResource){
//            imageIcon = new ImageIcon(this.getClass().getResource(path));
//            Image image = imageIcon.getImage(); // transform it
//            Image newimg = image.getScaledInstance(60, 60,  Image.SCALE_SMOOTH); // scale it the smooth way
//            previewIcon = new ImageIcon(newimg);
//            this.image = ParticleUtils.loadSprite(path);
//            return true;
//        }else{
//
//            try {
//                File f = new File(path);
//                this.image = ImageIO.read(f);
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
//                setImage(this.defaultPath, true);
//
//                customPathValid = false;
//                return false;
//            }
//        }
//        return false;
//    }


    public static int getWeightedAmountWinningIndex(Collection<? extends SoundData> soundData){
        int sumWeight = 0;
        for(SoundData d: soundData){
            if(d.enabled){
                sumWeight += d.val1;
            }
        }
        if(sumWeight == 0){ return -1; }

        int weightChance = ThreadLocalRandom.current().nextInt(0, sumWeight);
        //roll random chance between 0-->w1+w2+...wn
//       |--- w1-- | -------- weight 2 ------ | --X---- weight 3 --|
        int winnerIndex = -1;
        int limit = 0;
        for(SoundData d: soundData){
            winnerIndex += 1;
            if(d.enabled){
                limit += d.val1;
                if(weightChance <= limit){ //we've found the winner
                    break;
                }
            }

        }
        return winnerIndex;

    }
}
