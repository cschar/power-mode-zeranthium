package com.cschar.pmode3;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.SmartList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ZJSONLoader {
    private HashMap<PowerMode3.ConfigType, String> defaultJSONTables;
    private static final Logger LOGGER = Logger.getInstance(ZJSONLoader.class);

    public ZJSONLoader() {
        LOGGER.debug("Initializing JSONLoader");
        defaultJSONTables = new HashMap<PowerMode3.ConfigType, String>() {{
            put(PowerMode3.ConfigType.LOCKED_LAYER, "LOCKED_LAYER.json");
            put(PowerMode3.ConfigType.COPYPASTEVOID, "COPYPASTEVOID.json");
            put(PowerMode3.ConfigType.DROSTE, "DROSTE.json");
            put(PowerMode3.ConfigType.LINKER, "LINKER.json");
            put(PowerMode3.ConfigType.LANTERN, "LANTERN.json");
            put(PowerMode3.ConfigType.LIZARD, "LIZARD.json");
            put(PowerMode3.ConfigType.MULTI_LAYER, "MULTI_LAYER.json");
            put(PowerMode3.ConfigType.MULTI_LAYER_CHANCE, "MULTI_LAYER_CHANCE.json");
            put(PowerMode3.ConfigType.TAP_ANIM, "TAP_ANIM.json");

            put(PowerMode3.ConfigType.MUSIC_TRIGGER, "MUSIC_TRIGGERS.json");
            put(PowerMode3.ConfigType.SOUND, "SOUND.json");
            put(PowerMode3.ConfigType.SPECIAL_ACTION_SOUND, "SPECIAL_ACTION_SOUND.json");
        }};
    }

    public void loadSingleJSONTableConfig(Map<PowerMode3.ConfigType, SmartList<String>> pathDataMap, PowerMode3.ConfigType t) {

        String jsonFile = defaultJSONTables.get(t);
        InputStream inputStream = ZJSONLoader.class.getResourceAsStream("/configJSON/" + jsonFile);

        StringBuilder sb = new StringBuilder();
        Scanner s = new Scanner(inputStream);
        while (s.hasNextLine()) {
            sb.append(s.nextLine());
        }

        SmartList<String> smartList = new SmartList<>();
        try {
            JSONObject jo = new JSONObject(sb.toString());

            //TODO this is extra work, we go .json --> JSONObject --> string ( ..later --> JSONObject ---> pathData)
            JSONArray tableData = jo.getJSONArray("data");
            for (int i = 0; i < tableData.length(); i++) {
                smartList.add(tableData.getJSONObject(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pathDataMap.put(t, smartList);
    }


    public HashMap<PowerMode3.ConfigType, SmartList<String>> getDefaultJSONTableConfigs() {
        HashMap<PowerMode3.ConfigType, SmartList<String>> pathDataMap = new HashMap<>();
        for (PowerMode3.ConfigType t : defaultJSONTables.keySet()) {
            loadSingleJSONTableConfig(pathDataMap, t);
        }
        return pathDataMap;
    }

}