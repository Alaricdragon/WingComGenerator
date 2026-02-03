package main.threads;

import org.json.simple.JSONObject;

public class SeekBaseSettings extends SeekSettings{
    public SeekBaseSettings(String modID, String path) {
        super(modID, path);
    }

    @Override
    public void processData(JSONObject json) {
        //todo: make it so this just gets the relevent data outside of here.
    }
}
