package main.types;

public class MatedFighters {
    public Hull hull;
    public HullJson hullJson;
    public Variant variant;
    public Fighter fighter;
    public MatedFighters(Hull hull, HullJson hullJson, Variant variant, Fighter fighter){
        this.hull = hull;
        this.hullJson = hullJson;
        this.variant = variant;
        this.fighter = fighter;
    }
}
