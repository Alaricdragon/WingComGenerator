package main.settings;

import main.types.MatedFighters;
import main.types.Variant;
import org.json.simple.JSONObject;

public class VariantSettings {

    //note: no need for locked varubles. they are locked in other places
    public Variant variant = null;
    public float spawnWeight = -1;//-1 spawn weight is null. any number less then 1 is null.
    public String spawnGroup = null;
    public VariantSettings(int priority,JSONObject json){
        //in theory, this is dirt simple. I just need to get the relevent variant file.
        if (json.containsKey("spawnWeight")) spawnWeight = Float.parseFloat(json.get("spawnWeight").toString());
        if (json.containsKey("spawnGroup")) spawnGroup = json.get("spawnGroup").toString();
        if (json.containsKey(".variant")) variant = new Variant((JSONObject) json.get(".variant"),priority);
    }
    public VariantSettings(VariantSettings a,VariantSettings b){
        variant = b.variant != null ? b.variant:a.variant;
        spawnWeight = b.spawnWeight >= 0 ? b.spawnWeight : a.spawnWeight;
        spawnGroup = b.spawnGroup != null ? b.spawnGroup : a.spawnGroup;
    }
    public VariantSettings(){}
}
