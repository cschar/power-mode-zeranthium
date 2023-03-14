package com.cschar.pmode3;

import com.google.gson.*;

import java.awt.*;
import java.lang.reflect.Type;

public class ZColorSerializer implements JsonSerializer<Color> {
    @Override
    public JsonElement serialize
            (Color src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jObject = new JsonObject();
        jObject.addProperty("r", src.getRed());
        jObject.addProperty("g", src.getGreen());
        jObject.addProperty("b", src.getBlue());
        return jObject;
    }

}
