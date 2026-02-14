package main.threads;

import main.settings.ShipGroupSettings;
import main.settings.Settings;
import org.LockedVariable;
import org.json.simple.JSONObject;


public class SeekBaseSettings extends SeekSettings{

    public SeekBaseSettings(String modID, int order, String path) {
        super(modID, order, path);
    }

    public void processData(JSONObject json){
        super.processData(json);
        ShipGroupSettings a = getShipGroupSettings((JSONObject) json.get("defaultShipData"));
        Settings.baseShipSettings = new LockedVariable<>(a,false);
        if (json.containsKey("buildRestricted")){
            Settings.buildRestricted.set(Boolean.parseBoolean(json.get("buildRestricted").toString()));
        }
        if (json.containsKey("spawnRestricted")){
            Settings.spawnRestricted.set(Boolean.parseBoolean(json.get("spawnRestricted").toString()));
        }
        if (json.containsKey("automateDroneFighters")){
            Settings.automateDroneFighters.set(Boolean.parseBoolean(json.get("automateDroneFighters").toString()));
        }
        if (json.containsKey("autoShips_DontCostAutoPoints")){
            Settings.autoShips_DontCostAutoPoints.set(Boolean.parseBoolean(json.get("autoShips_DontCostAutoPoints").toString()));
        }
        if (json.containsKey("tryToAddToBlueprintPacks")){
            Settings.tryToAddToBlueprintPacks.set(Boolean.parseBoolean(json.get("tryToAddToBlueprintPacks").toString()));
        }
    }

    @Override
    public void processDefaultShipData(JSONObject json) {
        //empty so I dont need to wast calculations on this.
    }
}
