package com.cschar.pmode3.config.common;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.logging.Level;
import com.intellij.openapi.diagnostic.Logger;

public class SoundData extends PathData{
    private static final Logger LOGGER = Logger.getInstance( SoundData.class.getName() );

    public String soundExtra1 = "";

    public SoundData(boolean enabled, int val1, String defaultPath, String customPath) {
        super(enabled, defaultPath, customPath, val1);
        setValidPath();

    }
    public SoundData(boolean enabled, int val1, String defaultPath, String customPath, String soundExtra1) {
        super(enabled, defaultPath, customPath, val1);
        setValidPath();
        this.soundExtra1 = soundExtra1;
    }

    private void setValidPath(){
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


    @Override
    public JSONObject toJSONObject(){
        ////enabled, scale, speed, defaultPath, customPath, isCyclic, val2, alpha, val1
        JSONObject jo = new JSONObject();
        try {
            jo.put("enabled", this.enabled);
            jo.put("defaultPath",this.defaultPath);
            jo.put("customPath",this.customPath);
            jo.put("val1",this.val1);
            jo.put("soundExtra1", this.soundExtra1);
        }catch(JSONException e){
            LOGGER.error(e.toString(),e);
        }

        return jo;
    }

    public static SoundData fromJsonObjectString(String s){

        SoundData sd = null;
        try {
            JSONObject jo = new JSONObject(s);
            sd =  new SoundData(jo.getBoolean("enabled"),
                    jo.getInt("val1"),
                    jo.getString("defaultPath"),
                    jo.getString("customPath"));
            //set optional extra fields
            if (jo.has("soundExtra1")){
                sd.soundExtra1 = jo.getString("soundExtra1");
            }

        }catch(JSONException e){
            LOGGER.error(e.toString(),e);
        }

        return sd;
    }


}
