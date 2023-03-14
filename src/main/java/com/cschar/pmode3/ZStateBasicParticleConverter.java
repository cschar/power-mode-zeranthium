package com.cschar.pmode3;

import com.google.gson.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.xmlb.Converter;

import java.awt.*;


public class ZStateBasicParticleConverter extends Converter<ZStateBasicParticle> {
    private static final Logger LOGGER = Logger.getInstance(ZStateBasicParticleConverter.class);
    public ZStateBasicParticleConverter(){}
    public ZStateBasicParticle fromString(String value) {
        LOGGER.debug("deserializing");
        GsonBuilder gsonBuildr = new GsonBuilder();
        gsonBuildr.registerTypeAdapter(Color.class, new ZColorDeserializer());
        return gsonBuildr.create().fromJson(value, ZStateBasicParticle.class);
    }

    public String toString(ZStateBasicParticle value) {
        LOGGER.debug("serializing");
        GsonBuilder gsonBuildr = new GsonBuilder();
        gsonBuildr.registerTypeAdapter(Color.class, new ZColorSerializer());
        return gsonBuildr.create().toJson(value);
    }

}
