package main.settings;

import main.types.MatedFighters;

public class ChosenShipSettings {
    public VariantSettings variantSettings = null;
    public HullSettings hullSettings = null;
    public ChosenShipSettings(MatedFighters mate, ShipGroupSettings a, ShipGroupSettings b, ShipGroupSettings c){
        /*order of overwriting:
            c-> overwrites all
            b-> overwrite a
            a-> backup.
         */
        String type = getFighterType(mate);
        String def = getDefenseType(mate);
        ChosenShipSettings a1 = new ChosenShipSettings(a,type,def);
        ChosenShipSettings b1 = new ChosenShipSettings(b,type,def);
        ChosenShipSettings c1 = new ChosenShipSettings(c,type,def);
        hullSettings = new HullSettings(a1.hullSettings,new HullSettings(b1.hullSettings,c1.hullSettings));
        if (c1.variantSettings != null) {
            variantSettings = c1.variantSettings;
            return;
        }
        if (b1.variantSettings != null){
            variantSettings = b1.variantSettings;
            return;
        }
        if (a1.variantSettings != null){
            variantSettings = a1.variantSettings;
        }
    }
    private ChosenShipSettings(ShipGroupSettings d, String type,String def){
        selectVariantSettings(d,type,def);
        selectHullSettings(d,type,def);
    }
    private void selectVariantSettings(ShipGroupSettings d, String type,String def){
        if(d.variantSettings.containsKey(def)){
            if (d.variantSettings.get(def).containsKey(type)){
                variantSettings = d.variantSettings.get(def).get(type);
                return;
            }
            if (d.variantSettings.get(def).containsKey("base")){
                variantSettings = d.variantSettings.get(def).get("base");
                return;
            }
        }
        if (d.variantSettings.containsKey("base") && d.variantSettings.get("base").containsKey("base")){
            variantSettings = d.variantSettings.get("base").get("base");
        }

    }
    private void selectHullSettings(ShipGroupSettings d, String type,String def){
        //I am using the new HullSettings(HullSettings,HullSettings) function to only override 'null' varubles.
        //this is useful when I need to have all data be set, but only want the 'deepest' data on the tree.
        //and I dont want any data on the tree to be 'forced'.
        hullSettings = new HullSettings();
        if(d.hullSettings.containsKey(def)){
            if (d.hullSettings.get(def).containsKey(type)){
                hullSettings = new HullSettings(hullSettings,d.hullSettings.get(def).get(type));
            }
            if (d.hullSettings.get(def).containsKey("base")){
                hullSettings = new HullSettings(hullSettings,d.hullSettings.get(def).get("base"));
            }
        }
        if (d.hullSettings.containsKey("base") && d.hullSettings.get("base").containsKey("base")){
            hullSettings = new HullSettings(hullSettings,d.hullSettings.get("base").get("base"));
        }
    }
    private String getFighterType(MatedFighters mate){
        String type = mate.fighter.fighter_csv.role;
        String type2 = switch (type){
            case "FIGHTER" -> "fighters";
            case "BOMBER" -> "bombers";
            case "INTERCEPTOR" -> "interceptors";
            default -> "other";
        };
        return type2;
    }
    private String getDefenseType(MatedFighters mate) {
        String defense = mate.hull.ship_csv.shield_type;
        if (defense.isBlank() || defense.equals("NONE")) {
            if (mate.hull.ship_csv.defense_id.isBlank()) {
                defense = "NONE";
            } else {
                defense = "OTHER";
            }
        }
        String def2 = switch (defense) {
            case "NONE" -> "undefended";
            case "FRONT", "OMNI" -> "shielded";
            case "PHASE"-> "phased";
            default -> "other";
        };
        return def2;
    }
}
