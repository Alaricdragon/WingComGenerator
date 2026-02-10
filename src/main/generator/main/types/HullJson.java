package main.types;

import org.json.simple.JSONObject;

public class HullJson {
    public JSONObject json;
    public int priority;
    public HullJson(JSONObject json,int priority) {
        this.json = json;
        this.priority = priority;
    }
    public String path;
    public HullJson(JSONObject json,String path,int priority) {
        this.json = json;
        this.priority = priority;
        this.path = path;
    }
}
