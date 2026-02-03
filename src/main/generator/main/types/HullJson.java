package main.types;

import org.json.simple.JSONObject;

public class HullJson {
    public JSONObject json;
    public int priority;
    public HullJson(JSONObject json,int priority) {
        this.json = json;
        this.priority = priority;
    }
}
