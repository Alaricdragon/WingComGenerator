package main.threads;

import main.processers.CustomJSonReader;
import main.settings.ManufacturerSettings;
import main.settings.Settings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class SeekSettings implements Runnable{
    private String modID;//this is keept just in case I need it.
    private String path;
    public SeekSettings(String modID,String path){
        this.modID = modID;
        this.path = path;
    }
    @Override
    public void run() {
        try {
            processData(CustomJSonReader.getObject(path));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public void processData(JSONObject json){
        if (json.containsKey("baseBuiltInHullMods")){
            JSONArray array = (JSONArray) json.get("baseBuiltInHullMods");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.getBaseShipSettings().hullMods.add(a);
        }
        if (json.containsKey("manufactureModifications")) {
            JSONObject mMods = (JSONObject) json.get("manufactureModifications");
            for (Object a : mMods.keySet()) {
                JSONArray mMods2 = (JSONArray) mMods.get(a.toString());
                ManufacturerSettings c = new ManufacturerSettings(a.toString());
                for (String d : CustomJSonReader.getItemsInArray(mMods2)) c.hullMods.add(d);
                Settings.addShipSettings(c);
            }
        }
        if (json.containsKey("forceExclude")){
            JSONArray array = (JSONArray) json.get("forceExclude");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.setForceExclude(a);
        }
        if (json.containsKey("ignoreRestrictedStatus")){
            JSONArray array = (JSONArray) json.get("ignoreRestrictedStatus");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.addAllowedRestricted(a);
        }
    }
}
