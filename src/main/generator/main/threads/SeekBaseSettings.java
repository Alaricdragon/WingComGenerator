package main.threads;

import main.processers.CustomJSonReader;
import main.settings.ManufacturerSettings;
import main.settings.Settings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class SeekBaseSettings extends SeekSettings{
    public SeekBaseSettings(String modID, String path) {
        super(modID, path);
    }

    public void processData(JSONObject json){
        super.processData(json);
        ManufacturerSettings z = Settings.getBaseShipSettings();
        Settings.getBaseShipSettings_lock().lock();
        //todo: other things, like additional weapons, and flux stats need to be included here
        Settings.getBaseShipSettings_lock().unlock();
        if (json.containsKey("buildRestricted")){
            Settings.setBuildRestricted(Boolean.parseBoolean(json.get("buildRestricted").toString()));
        }
    }
}
