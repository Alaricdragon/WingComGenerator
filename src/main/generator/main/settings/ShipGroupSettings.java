package main.settings;

import org.LockedHashMap;
import org.LockedVariable;
import org.json.simple.JSONObject;

public class ShipGroupSettings {
    /*todo:
        make it so 3 of this file can be merged into 1 with 'priority' (aka a,b,c, were a is overriden by b, only if c is not already override that giving stat for a).
     */
    public LockedVariable<String> mod_id;//the mod id, for cross references. just in case.
    public LockedVariable<Integer> priority;
    public LockedHashMap<String,LockedHashMap<String,HullSettings>> hullSettings = new LockedHashMap<>(false);
    public LockedHashMap<String,LockedHashMap<String,VariantSettings>> variantSettings = new LockedHashMap<>(false);
    /*public ShipGroupSettings(String id){
        this.id = id;
    }*/
    /*


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
     */
    private static String[][] link = {
            {
                "base",
                "base",
                "shielded",
                "phased",
                "undefended",
                "other"
            },
            {
                "fighters",
                "base",
                "shielded",
                "phased",
                "undefended",
                "other"
            },
            {
                "bombers",
                "base",
                "shielded",
                "phased",
                "undefended",
                "other"
            },
            {
                "interceptors",
                "base",
                "shielded",
                "phased",
                "undefended",
                "other"
            },
            {
                "other",
                "base",
                "shielded",
                "phased",
                "undefended",
                "other"
            }
    };
    public ShipGroupSettings(String mod_id,int priority,JSONObject json){
        //todo: this is the simple part.
        //  all it needs to do is get the relevant 'names' and process them into variants and hulls.
        this.priority = new LockedVariable<>(priority,false);
        if (json.containsKey("hull_data")){
            JSONObject json2 = (JSONObject) json.get("hull_data");
            boolean run = false;
            for (String a : link[0]) if (json2.containsKey(a)){
                run = true;
                break;
            }
            if (run){
                getSingleGroup_hull( mod_id,priority,json2,link[0]);
            }
            if (json2.containsKey("fighters")) getSingleGroup_hull( mod_id,priority, (JSONObject) json2.get("fighters"),link[1]);
            if (json2.containsKey("bombers")) getSingleGroup_hull( mod_id,priority, (JSONObject) json2.get("bombers"),link[2]);
            if (json2.containsKey("interceptors")) getSingleGroup_hull( mod_id,priority, (JSONObject) json2.get("interceptors"),link[3]);
            if (json2.containsKey("other")) getSingleGroup_hull( mod_id,priority, (JSONObject) json2.get("other"),link[4]);
        }
        if (json.containsKey("variant_data")){
            JSONObject json2 = (JSONObject) json.get("variant_data");
            boolean run = false;
            for (String a : link[0]) if (json2.containsKey(a)){
                run = true;
                break;
            }
            if (run){
                getSingleGroup_variant( mod_id,priority,json2,link[0]);
            }
            if (json2.containsKey("fighters")) getSingleGroup_variant( mod_id,priority, (JSONObject) json2.get("fighters"),link[1]);
            if (json2.containsKey("bombers")) getSingleGroup_variant( mod_id,priority, (JSONObject) json2.get("bombers"),link[2]);
            if (json2.containsKey("interceptors")) getSingleGroup_variant( mod_id,priority, (JSONObject) json2.get("interceptors"),link[3]);
            if (json2.containsKey("other")) getSingleGroup_variant( mod_id,priority, (JSONObject) json2.get("other"),link[4]);

        }
    }
    private void getSingleGroup_hull(String id, int priority, JSONObject json, String[] links){
        LockedHashMap<String,HullSettings> out = new LockedHashMap<>(false);
        boolean added = false;
        for (int a = 1; a < links.length; a++){
            String b = links[a];
            if (!json.containsKey(b)) continue;
            added = true;
            out.put(b,new HullSettings(priority, (JSONObject) json.get(b)));
        }
        if (added) hullSettings.put(links[0],out);
    }
    private void getSingleGroup_variant(String id, int priority, JSONObject json, String[] links){
        LockedHashMap<String,VariantSettings> out = new LockedHashMap<>(false);
        boolean added = false;
        for (int a = 1; a < links.length; a++){
            String b = links[a];
            if (!json.containsKey(b)) continue;
            added = true;
            out.put(b,new VariantSettings(priority, (JSONObject) json.get(b)));
        }
        if (added) variantSettings.put(links[0],out);
    }
}
