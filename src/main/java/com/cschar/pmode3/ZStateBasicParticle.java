package com.cschar.pmode3;

import com.cschar.pmode3.config.common.SoundData;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.xmlb.Converter;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ZStateBasicParticle {
    private static final Logger LOGGER = Logger.getInstance(ZStateBasicParticle.class);
    public ZStateBasicParticle(){
    }

    public boolean emitTop = false;
    public boolean emitBot = true;
    public int numParticles = 7;
    public int maxParticleSize = 5;
    public Color basicColor = new Color(204, 0,0);
}
