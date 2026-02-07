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
    public LockedVariable<HullJson> json = null;
    public int priority;
    //NOTICE: -9999 for unset variables.
    public ArrayList<String> tags = new ArrayList<>();
    public ArrayList<String> perma_hullmods = new ArrayList<>();
    public ArrayList<String> hints = new ArrayList<>();
    public int baseFighters = -9999;
    public int emptyFighters = -9999;
    public int op = -9999;
    public int burn = -9999;
    public int fuel = -9999;
    public int cargo = -9999;
    public int crew = -9999;
    public float fuelLY = -9999;
    public int crDay = -9999;
    public int peakCR = -9999;
    public float crLossPerSecond = -9999;
    public float breakProb = -9999;
    public int maxPeaces = -9999;
    public int minPeaces = -9999;
    public float dpM = -9999;
    public int dpF = -9999;
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
    public float valueM = -9999;
    public float valueF = -9999;
    public float suppliesRecoverM = -9999;
    public int suppliesRecoverF;
    public float suppliesMonthM;
    public int suppliesMonthF;
    public float accelerationM;
    public float accelerationF;
    public float decelerationM;
    public float decelerationF;
    public float crToDeployM;
    public float crToDeployF;
    public HullSettings(int priority, JSONObject json){
        this.priority = priority;
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
        dpM = getJSonOrNull(json,"dpPerOp");
        dpF = (int) getJSonOrNull(json,"dpFlat");
        emptyFighters = (int) getJSonOrNull(json,"fighter bays without base fighter");
        baseFighters = (int) getJSonOrNull(json,"fighter bays with base fighter");
        crew = (int) getJSonOrNull(json,"crew cap");
        cargo = (int) getJSonOrNull(json,"cargo cap");
        fuel = (int) getJSonOrNull(json,"fuel cap");
        burn = (int) getJSonOrNull(json,"burn");
        op = (int) getJSonOrNull(json,"op");
        fuelLY = getJSonOrNull(json,"fuel ly");
        crDay = (int) getJSonOrNull(json,"cr-day");
        peakCR = (int) getJSonOrNull(json,"peck cr");
        crLossPerSecond = getJSonOrNull(json,"cr loss - sec");
        breakProb = getJSonOrNull(json,"break prob");
        minPeaces = (int) getJSonOrNull(json,"min peaces");
        maxPeaces = (int) getJSonOrNull(json,"max peaces");
        valueM = getJSonOrNull(json,"value multi");
        valueF = getJSonOrNull(json,"value flat");
        suppliesRecoverM = getJSonOrNull(json,"supplies-rec multi");
        suppliesRecoverF = (int) getJSonOrNull(json,"supplies-rec flat");
        suppliesMonthM = getJSonOrNull(json,"supplies-month multi");
        suppliesMonthF = (int) getJSonOrNull(json,"supplies-month flat");
        accelerationM = getJSonOrNull(json,"acceleration multi");
        accelerationF = getJSonOrNull(json,"acceleration flat");
        decelerationM = getJSonOrNull(json,"deceleration multi");
        decelerationF = getJSonOrNull(json,"deceleration flat");
        crToDeployM = getJSonOrNull(json,"cr to deploy multi");
        crToDeployF = getJSonOrNull(json,"cr to deploy flat");

        if (json.containsKey("hints")){
            JSONArray array = (JSONArray) json.get("hints");
            for (Object a : array){
                hints.add(a.toString());
            }
        }
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
    /// creates a empty hull settings. usefull for one math eq.
    public HullSettings(){

    }
    /// in this case, b is the 'overrideing' hullSettings. a is the not overriding hullsettings.
    public HullSettings(HullSettings a, HullSettings b){
        tags = a.tags;
        hints = canUse(b.hints) ? b.hints : a.hints;
        perma_hullmods = canUse(b.perma_hullmods) ? b.perma_hullmods : a.perma_hullmods;
        baseFighters = canUse(b.baseFighters) ? b.baseFighters : a.baseFighters;
        emptyFighters = canUse(b.emptyFighters) ? b.emptyFighters : a.emptyFighters;
        op = canUse(b.op) ? b.op :a.op;
        burn = canUse(b.burn) ? b.burn :a.burn;
        fuel = canUse(b.fuel) ? b.fuel :a.fuel;
        cargo = canUse(b.cargo) ? b.cargo :a.cargo;
        crew = canUse(b.crew) ? b.crew :a.crew;
        fuelLY = canUse(b.fuelLY) ? b.fuelLY :a.fuelLY;
        crDay = canUse(b.crDay) ? b.crDay :a.crDay;
        peakCR = canUse(b.peakCR) ? b.peakCR :a.peakCR;
        crLossPerSecond = canUse(b.crLossPerSecond) ? b.crLossPerSecond :a.crLossPerSecond;
        breakProb = canUse(b.breakProb) ? b.breakProb :a.breakProb;
        maxPeaces = canUse(b.maxPeaces) ? b.maxPeaces :a.maxPeaces;
        minPeaces = canUse(b.minPeaces) ? b.minPeaces :a.minPeaces;
        dpM = canUse(b.dpM) ? b.dpM :a.dpM;
        dpF = canUse(b.dpF) ? b.dpF :a.dpF;
        ventM = canUse(b.ventM) ? b.ventM :a.ventM;
        ventF = canUse(b.ventF) ? b.ventF :a.ventF;
        capM = canUse(b.capM) ? b.capM :a.capM;
        capF = canUse(b.capF) ? b.capF :a.capF;
        hullM = canUse(b.hullM) ? b.hullM :a.hullM;
        hullF = canUse(b.hullF) ? b.hullF :a.hullF;
        armorM = canUse(b.armorM) ? b.armorM :a.armorM;
        armorF = canUse(b.armorF) ? b.armorF :a.armorF;
        speedM = canUse(b.speedM) ? b.speedM :a.speedM;
        speedF = canUse(b.speedF) ? b.speedF :a.speedF;
        valueM = canUse(b.valueM) ? b.valueM :a.valueM;
        valueF = canUse(b.valueF) ? b.valueF :a.valueF;
        suppliesRecoverM = canUse(b.suppliesRecoverM) ? b.suppliesRecoverM :a.suppliesRecoverM;
        suppliesRecoverF = canUse(b.suppliesRecoverF) ? b.suppliesRecoverF :a.suppliesRecoverF;
        suppliesMonthM = canUse(b.suppliesMonthM) ? b.suppliesMonthM :a.suppliesMonthM;
        suppliesMonthF = canUse(b.suppliesMonthF) ? b.suppliesMonthF :a.suppliesMonthF;
        accelerationM = canUse(b.accelerationM) ? b.accelerationM :a.accelerationM;
        accelerationF = canUse(b.accelerationF) ? b.accelerationF :a.accelerationF;
        decelerationM = canUse(b.decelerationM) ? b.decelerationM :a.decelerationM;
        decelerationF = canUse(b.decelerationF) ? b.decelerationF :a.decelerationF;
        crToDeployM = canUse(b.crToDeployF) ? b.crToDeployF :a.crToDeployF;
        crToDeployF = canUse(b.crToDeployF) ? b.crToDeployF :a.crToDeployF;
        json = canUse(b.json) ? b.json : a.json;
    }
    private boolean canUse(ArrayList<String> in){
        return !in.isEmpty();
    }
    private boolean canUse(float in){
        return in != -9999;
    }
    private boolean canUse(LockedVariable<HullJson> json){
        return json != null;
    }
}
