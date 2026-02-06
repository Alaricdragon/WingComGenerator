package main.settings;

import main.types.MatedFighters;
import main.types.Variant;
import org.json.simple.JSONObject;

public class VariantSettings {

    //note: no need for locked varubles. they are locked in other places
    public Variant variant;
    public float spawnWeight;
    public String spawnGroup;
    public VariantSettings(int priority,JSONObject json){
        //in theory, this is dirt simple. I just need to get the relevent variant file.
        if (json.containsKey("spawnWeight")) spawnWeight = Float.parseFloat(json.get("spawnWeight").toString());
        if (json.containsKey("spawnGroup")) spawnGroup = json.get("spawnGroup").toString();
        if (json.containsKey(".variant")) variant = new Variant((JSONObject) json.get(".variant"),priority);
    }
}
