package main.settings;

import main.beans.Bean_Fighter;
import main.beans.Bean_Ship;
import org.LockedHashMap;
import org.LockedList;
import org.LockedVariable;

public class Settings {
    //public static ReentrantLock lock = new ReentrantLock(false);
    public static LockedVariable<Boolean> ready = new LockedVariable<>(false,false);

    public static LockedVariable<ShipGroupSettings> baseShipSettings;// = new LockedVariable<>(,new ShipGroupSettings("starsector"),false);
    public static final LockedList<ShipGroupSettings> unsortedBaseShipSettings = new LockedList<>(false);//this requires special sorting to function

    public static final LockedHashMap<String,ShipGroupSettings> shipSettings_manufacturer = new LockedHashMap<>(false);
    public static final LockedHashMap<String,ShipGroupSettings> shipSettings_shipID = new LockedHashMap<>(false);
    //public static final LockedList<ShipGroupSettings> unsortedShipSettings = new LockedList<>(false);


    public static final LockedVariable<Boolean> buildRestricted = new LockedVariable<>(false,false);
    public static final LockedVariable<Boolean> spawnRestricted = new LockedVariable<>(false,false);
    public static final LockedVariable<Boolean> automateDroneFighters = new LockedVariable<>(false,false);
    public static final LockedVariable<Boolean> autoShips_DontCostAutoPoints = new LockedVariable<>(false,false);

    /// mods to always be added to every hull file, regardless of context.
    public static final LockedList<String> permaMods = new LockedList<>(false);
    public static final LockedList<String> permaMods_ifBaseFighter = new LockedList<>(false);
    public static final LockedList<String> tags = new LockedList<>(false);
    public static final LockedList<String> hints = new LockedList<>(false);
    public static final LockedList<String> forceExclude = new LockedList<>(false);
    public static final LockedList<String> allowRestricted = new LockedList<>(false);
    public static final LockedList<String> forceAllowSpawns = new LockedList<>(false);
    public static final LockedList<String> forcePreventSpawns = new LockedList<>(false);
    public static final LockedList<String> manufacturersForceAutomated = new LockedList<>(false);
    public static final LockedList<String> shipsForceAutomated = new LockedList<>(false);
    public static final LockedList<String> shipsForceNotAutomated = new LockedList<>(false);

    public static final LockedHashMap<String,String> swapHullMods = new LockedHashMap<>(false);
    public static final LockedHashMap<String,String> fighterTagsToHullTags = new LockedHashMap<>(false);
    public static final LockedHashMap<String,String> fighterTagsToHullHints = new LockedHashMap<>(false);
    /*
    "permaMods": [] #hullmods that will always be added to all generated fighters as a perma mod
    "tags": [] #tags that will be always be added to all generated fighters
    "forceExclude": []#ships within this will NOT be generated
    "ignoreRestrictedStatus: []#ships within this will be generated.
    "forceAllowSpawns": []#ships within this will spawn in fleets
    "forcePreventSpawns": []#ships within this will NOT spawn in fleets.
     */


    public static String getName(ChosenShipSettings settings,Bean_Ship ship){
        return settings.hullSettings.hullName.replaceFirst("%s",ship.name);//NOTE: this needs to be set in addShipFile as well.
    }
    public static String getHullID(Bean_Fighter fighter){
        return getHullID(fighter.id);
    }
    public static String getHullID(String fighterID){
        return "WinComGeneratorH_"+fighterID;
    }
    public static String getVariantId(Bean_Fighter fighter){
        return getVariantId(fighter.id);
    }
    public static String getVariantId(String fighter){
        if (fighter.endsWith("_wing")){
            fighter+="_not";
        }
        return "WinComGeneratorV_"+fighter;
    }
    public static boolean isRestrictedMaybe(Bean_Fighter fighter){
        if (fighter.tags.contains("no_drop") && fighter.tags.contains("no_sell")) return true;
        if (fighter.tags.contains("restricted")) return true;
        return false;
    }
}
