package com.cschar.pmode3.config.common;

import org.json.JSONObject;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public abstract class PathData {

    public boolean enabled;
    public String defaultPath;
    public String customPath;
    public boolean customPathValid = false;
    public int val1;



    public PathData(boolean enabled, String defaultPath, String customPath, int val1) {
        this.enabled = enabled;
        this.customPath = customPath;
        this.defaultPath = defaultPath;
        this.customPathValid = false;
        this.val1 = val1;
//        if(this.val1 < 1 ) this.val1 = 1;
//        if(this.val1 > 1000) this.val1 = 1000;
    }

    public String getPath(){
        if(this.customPath.equals("") && !this.customPathValid){
            return this.defaultPath;
        }else{
            return this.customPath;
        }
    }

    public static int getWeightedAmountWinningIndex(Collection<? extends PathData> pathData){
        int sumWeight = 0;
        for(PathData d: pathData){
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
        for(PathData d: pathData){
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


    public abstract JSONObject toJSONObject();

}
