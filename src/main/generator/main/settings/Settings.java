package main.settings;

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

    /// mods to always be added to every hull file, regardless of context.
    public static final LockedList<String> permaMods = new LockedList<>(false);
    public static final LockedList<String> tags = new LockedList<>(false);
    public static final LockedList<String> hints = new LockedList<>(false);
    public static final LockedList<String> forceExclude = new LockedList<>(false);
    public static final LockedList<String> allowRestricted = new LockedList<>(false);
    public static final LockedList<String> forceAllowSpawns = new LockedList<>(false);
    public static final LockedList<String> forcePreventSpawns = new LockedList<>(false);
    /*
    "permaMods": [] #hullmods that will always be added to all generated fighters as a perma mod
    "tags": [] #tags that will be always be added to all generated fighters
    "forceExclude": []#ships within this will NOT be generated
    "ignoreRestrictedStatus: []#ships within this will be generated.
    "forceAllowSpawns": []#ships within this will spawn in fleets
    "forcePreventSpawns": []#ships within this will NOT spawn in fleets.
     */
}
