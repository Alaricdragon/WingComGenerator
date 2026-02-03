package main.settings;

import java.util.ArrayList;

public class ManufacturerSettings {
    //add additional hullmods based on the manufacturer (such as automated ships)
    //todo: add additional settings for custom variants?
    public String id;
    public ArrayList<String> hullMods = new ArrayList<>();
    public ManufacturerSettings(String id){
        this.id = id;
    }
}
