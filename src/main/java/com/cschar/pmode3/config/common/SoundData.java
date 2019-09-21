package com.cschar.pmode3.config.common;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class SoundData extends PathData{

    public boolean enabled=true;
    public int val1 =100;

    public SoundData(boolean enabled, int val1, String defaultPath, String customPath) {
        super(defaultPath, customPath);
        this.val1 = val1;
        this.enabled = enabled;

        if(!customPath.equals("")){ //check if value on filesystem is bad on initialization
            VirtualFile tmp = LocalFileSystem.getInstance().findFileByPath(customPath);
            if(tmp == null){
                this.customPathValid = false;
            }else if(Objects.equals(tmp.getExtension(), "mp3")) {
                this.customPathValid = true;
            }
        }

    }

    public void setValidMP3Path(VirtualFile f){
        if(f == null){
            this.customPathValid = false;
            return;
        }
        if(Objects.equals(f.getExtension(), "mp3")) {
            this.customPath = f.getPath();
            this.customPathValid = true;
        }else{
            this.customPath = f.getPath();
            this.customPathValid = false;
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
            if(d.enabled){
                winnerIndex += 1;
                limit += d.val1;
                if(weightChance <= limit){ //we've found the winner
                    break;
                }
            }

        }
        return winnerIndex;

    }
}
