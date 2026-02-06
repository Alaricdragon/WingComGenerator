package main.settings;

import main.types.HullJson;
import org.LockedVariable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class HullSettings {
    //todo: the hull varuble needs to be changed to something custom. because its not good as is. I only need to change certen data.
    //note: no need for locked varubles. they are locked in other places
    // on the other hand, having them linked would..
    // arg... ok so: linked value issues. I get the class and its fine. the object is not protected unless I take specal consideration! solution required. some day.
    //public LockedVariable<Hull> hull;
    public LockedVariable<HullJson> json;
    public int priority;
    //NOTICE: -9999 for unset variables.
    public ArrayList<String> tags = new ArrayList<>();
    public ArrayList<String> perma_hullmods = new ArrayList<>();
    public int baseFighters = -9999;
    public int emptyFighters = -9999;
    public int op = -9999;
    public int burn = -9999;
    public int fuel = -9999;
    public int cargo = -9999;
    public int crew = -9999;
    public int dpMin = -9999;
    public float dpFleetPoint = -9999;
    public float ventM = -9999;
    public float ventF = -9999;
    public float capM = -9999;
    public float capF = -9999;
    public float hullM = -9999;
    public float hullF = -9999;
    public float armorM = -9999;
    public float armorF = -9999;
    public float speedM = -9999;
    public float speedF = -9999;
    /*public HullSettings(int baseFighters,int emptyFighters,Hull hull, HullJson json){
        this.baseFighters = baseFighters;
        this.emptyFighters = emptyFighters;
        this.hull = new LockedVariable<>(hull,false);
        this.json = new LockedVariable<>(json,false);
    }*/
    public HullSettings(int priority, JSONObject json){
        this.priority = priority;
    /*
            -: op (amount of op on ship)
            -: burn
            -: fuel cap
            -: cargo cap
            -: crew cap
            -: fighter bays with base fighter
            -: fighter bays without base fighter
            -: tags
            -: perma mods
            -
            -: dp min
            -: dp per fleet point
            -
            -: flux vent multi
            -: flux vent flat
            -
    */
        /*

            -: flux cap multi
            -: flux cap flat
            -
            -: hull multi
            -: hull flat
            -
            -: armor multi
            -: armor flat
            -
            -: speed multi
            -: speed flat
        */
        //todo: add lists of both types.
        //todo: add a second getJsonOrNull for floats.
        speedF = getJSonOrNull(json,"speed flat");
        speedM = getJSonOrNull(json,"speed multi");
        armorF = getJSonOrNull(json,"armor flat");
        armorM = getJSonOrNull(json,"armor multi");
        hullF = getJSonOrNull(json,"hull flat");
        hullM = getJSonOrNull(json,"hull multi");
        capF = getJSonOrNull(json,"flux cap flat");
        capM = getJSonOrNull(json,"flux cap multi");
        ventF = getJSonOrNull(json,"flux vent flat");
        ventM = getJSonOrNull(json,"flux vent multi");
        dpFleetPoint = getJSonOrNull(json,"dp per fleet point");
        dpMin = (int) getJSonOrNull(json,"dp min");
        emptyFighters = (int) getJSonOrNull(json,"fighter bays without base fighter");
        baseFighters = (int) getJSonOrNull(json,"fighter bays with base fighter");
        crew = (int) getJSonOrNull(json,"crew cap");
        cargo = (int) getJSonOrNull(json,"cargo cap");
        fuel = (int) getJSonOrNull(json,"fuel cap");
        burn = (int) getJSonOrNull(json,"burn");
        op = (int) getJSonOrNull(json,"op");

        if (json.containsKey("tags")){
            JSONArray array = (JSONArray) json.get("tags");
            for (Object a : array){
                tags.add(a.toString());
            }
        }
        if (json.containsKey("perma mods")){
            JSONArray array = (JSONArray) json.get("perma mods");
            for (Object a : array){
                perma_hullmods.add(a.toString());
            }
        }
        if (json.containsKey(".ship")){
            this.json.set(new HullJson((JSONObject) json.get(".ship"),priority));
        }
    }
    private float getJSonOrNull(JSONObject json,String key){
        if (!json.containsKey(key)) return -9999;
        return Float.parseFloat(json.get(key).toString());
    }
}
