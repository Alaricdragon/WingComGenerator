package main.threads;

import main.processers.CustomJSonReader;
import main.settings.ManufacturerSettings;
import main.settings.Settings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

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
            Settings.getBaseShipSettings_lock().lock();
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.getBaseShipSettings().hullMods.add(a);
            Settings.getBaseShipSettings_lock().unlock();
        }
        if (json.containsKey("manufactureModifications")) {
            JSONObject mMods = (JSONObject) json.get("manufactureModifications");
            for (Object a : mMods.keySet()) {
                JSONObject mMods3 = (JSONObject) mMods.get(a.toString());//each diffrent manufactuer edit.
                if (mMods3.containsKey("BuiltInHullMods")){
                    JSONArray mMods2 = (JSONArray) mMods.get("BuiltInHullMods");
                    ManufacturerSettings c = new ManufacturerSettings(modID);
                    for (String d : CustomJSonReader.getItemsInArray(mMods2)) c.hullMods.add(d);
                    Settings.addShipSettings(c);
                }
            }
        }
        if (json.containsKey("forceExclude")){
            JSONArray array = (JSONArray) json.get("forceExclude");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.addForceExclude(a);
        }
        if (json.containsKey("ignoreRestrictedStatus")){
            JSONArray array = (JSONArray) json.get("ignoreRestrictedStatus");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.addAllowedRestricted(a);
        }
    }
}
