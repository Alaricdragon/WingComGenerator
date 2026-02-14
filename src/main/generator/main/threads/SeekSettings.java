package main.threads;

import main.processers.CustomJSonReader;
import main.settings.ShipGroupSettings;
import main.settings.Settings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class SeekSettings implements Runnable{
    private String modID;//this is keept just in case I need it.
    private String path;
    private int order;
    public SeekSettings(String modID,int order,String path){
        this.modID = modID;
        this.path = path;
        this.order = order;
    }
    @Override
    public void run() {
        try {
            processData(CustomJSonReader.getObject(path));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /*todo:
        1) I need to create a function just for getting ShipSettings data.
        2) I need the following (outside of ship settings):
            1: perma mods: (added with ship settings)
            2: tags:       (added with ship settings)
        2) settings per 'ship settings' type: (in ship settings):
            -: hull_data
            -: variant_data:
        -2.1 hull data:
            -: op (amount of op on ship)
            -: burn
            -: fuel cap
            -: cargo cap
            -: crew cap
            -: fighter bays with 'base' fighter
            -: fighter bays without 'base' fighter
            -: tags
            -: perma mods
            -
            -: dp min
            -: dp per fleet point
            -
            -: flux vent multi
            -: flux vent flat
            -
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
            -
            -: hull.json data.
        -2.2) the actual structure will be as follows: (they are all simple .variant files.)
            #please keep in mind: the first gets overwriten by anything. the second 4 get overwriten by the rest. they are the 'base'. the others are the 'overwriters'
            variants: (were everything not followed by a indent is an optional variant)
                #please keep in mind: the first 4 get overwriten by the rest. they are the 'base'. the others are the 'overwriters'
                "base"
                "shielded"
                "phased"
                "undefended"
                "other"
                "fighters":
                    "shielded"
                    "phased"
                    "undefended"
                    "other"
                "bombers":
                    "shielded"
                    "phased"
                    "undefended"
                    "other"
                "interceptors":
                    "shielded"
                    "phased"
                    "undefended"
                    "other"
                "other":
                    "shielded"
                    "phased"
                    "undefended"
                    "other"
            hulls: (were everything not followed by a indent is an optional variant)
                #please keep in mind: the first gets overwriten by anything. the second 4 get overwriten by the rest. they are the 'base'. the others are the 'overwriters'
                #also keep in mind: hull settings includes all base settings.
                "base"
                "shielded"
                "phased"
                "undefended"
                "other"
                "fighters":
                    "shielded"
                    "phased"
                    "undefended"
                    "other"
                "bombers":
                    "shielded"
                    "phased"
                    "undefended"
                    "other"
                "interceptors":
                    "shielded"
                    "phased"
                    "undefended"
                    "other"
                "other":
                    "shielded"
                    "phased"
                    "undefended"
                    "other"
        3) structure:
        base settings:
            "buildRestricted": boolean
            "spawnRestricted": boolean #only works if buildRestricted is on
            "permaMods": [] #hullmods that will always be added to all generated fighters as a perma mod
            "tags": [] #tags that will be always be added to all generated fighters
            "forceExclude": []#ships within this will NOT be generated
            "ignoreRestrictedStatus: []#ships within this will be generated.
            "forceAllowSpawns": []#ships within this will spawn in fleets
            "forcePreventSpawns": []#ships within this will NOT spawn in fleets.
            "defaultShipData": ship settings
            -
            "manufacturerSettings" : array "manufactuer": shipSettings
            "fighterOverride": array "shipName": shipSettings
        -
        for this itself:
        1) create a function to 'read' hull data (group), hull data (single), variant data (group), variant data (single).
        2) use that, like heavaly.
     */
    public void processDefaultShipData(JSONObject json){
        if (json.containsKey("defaultShipData")){
            Settings.unsortedBaseShipSettings.add(getShipGroupSettings((JSONObject) json.get("defaultShipData")));
        }
    }
    public void processData(JSONObject json){
        if (json.containsKey("permaMods")){
            JSONArray array = (JSONArray) json.get("permaMods");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.permaMods.add(a);
        }
        if (json.containsKey("permaMods_ifBaseFighter")){
            JSONArray array = (JSONArray) json.get("permaMods_ifBaseFighter");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.permaMods_ifBaseFighter.add(a);
        }
        if (json.containsKey("tags")){
            JSONArray array = (JSONArray) json.get("tags");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.tags.add(a);
        }
        if (json.containsKey("hints")){
            JSONArray array = (JSONArray) json.get("hints");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.hints.add(a);
        }
        if (json.containsKey("forceExclude")){
            JSONArray array = (JSONArray) json.get("forceExclude");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.forceExclude.add(a);
        }
        if (json.containsKey("ignoreRestrictedStatus")){
            JSONArray array = (JSONArray) json.get("ignoreRestrictedStatus");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.allowRestricted.add(a);
        }
        if (json.containsKey("forceAllowSpawns")){
            JSONArray array = (JSONArray) json.get("forceAllowSpawns");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.forceAllowSpawns.add(a);
        }
        if (json.containsKey("forcePreventSpawns")){
            JSONArray array = (JSONArray) json.get("forcePreventSpawns");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.forcePreventSpawns.add(a);
        }

        if (json.containsKey("manufacturersForceAutomated")){
            JSONArray array = (JSONArray) json.get("manufacturersForceAutomated");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.manufacturersForceAutomated.add(a);
        }
        if (json.containsKey("shipsForceAutomated")){
            JSONArray array = (JSONArray) json.get("shipsForceAutomated");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.shipsForceAutomated.add(a);
        }
        if (json.containsKey("shipsForceNotAutomated")){
            JSONArray array = (JSONArray) json.get("shipsForceNotAutomated");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.shipsForceNotAutomated.add(a);
        }

        if (json.containsKey("swapHullMods")){
            JSONObject json2 = (JSONObject) json.get("swapHullMods");
            for (Object a : json2.keySet()) Settings.swapHullMods.put(a.toString(),json2.get(a.toString()).toString());
        }
        if (json.containsKey("fighterTagsToHullTags")){
            JSONObject json2 = (JSONObject) json.get("fighterTagsToHullTags");
            for (Object a : json2.keySet()) Settings.fighterTagsToHullTags.put(a.toString(),json2.get(a.toString()).toString());
        }
        if (json.containsKey("fighterTagsToHullHints")){
            JSONObject json2 = (JSONObject) json.get("fighterTagsToHullHints");
            for (Object a : json2.keySet()) Settings.fighterTagsToHullHints.put(a.toString(),json2.get(a.toString()).toString());
        }
        if (json.containsKey("forceNotBlueprintTag")){
            JSONArray array = (JSONArray) json.get("forceNotBlueprintTag");
            for (String a : CustomJSonReader.getItemsInArray(array)) Settings.forceNotBlueprintTag.add(a);
        }

        processDefaultShipData(json);

        if (json.containsKey("manufacturerSettings")){
            JSONObject json2 = (JSONObject) json.get("manufacturerSettings");
            for (Object a : json2.keySet()){
                String b = a.toString();
                ShipGroupSettings c = getShipGroupSettings((JSONObject) json2.get(b));
                Settings.shipSettings_manufacturer.getListWithLock();//need a separate locking function for when I am not getting the lists.
                boolean canAdd = true;
                if (Settings.shipSettings_manufacturer.containsKey(b) && c.priority.get() < Settings.shipSettings_manufacturer.get(b).priority.get()){
                    canAdd = false;
                }

                if (!canAdd) continue;
                Settings.shipSettings_manufacturer.put(b,c);
                Settings.shipSettings_manufacturer.unlock();
            }
        }
        if (json.containsKey("fighterOverride")){
            JSONObject json2 = (JSONObject) json.get("fighterOverride");
            for (Object a : json2.keySet()){
                String b = a.toString();
                ShipGroupSettings c = getShipGroupSettings((JSONObject) json2.get(b));
                Settings.shipSettings_shipID.getListWithLock();//need a separate locking function for when I am not getting the lists.
                boolean canAdd = true;
                if (Settings.shipSettings_shipID.containsKey(b) && c.priority.get() < Settings.shipSettings_shipID.get(b).priority.get()){
                    canAdd = false;
                }

                if (!canAdd) continue;
                Settings.shipSettings_shipID.put(b,c);
                Settings.shipSettings_shipID.unlock();
            }
        }
    }
    protected ShipGroupSettings getShipGroupSettings(JSONObject object){
        return new ShipGroupSettings(modID,order,object);
    }
}
