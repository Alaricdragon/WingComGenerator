package main.types;

import org.json.simple.JSONObject;

public class Variant {
    public JSONObject json;
    public int priority;
    public Variant(JSONObject json,int priority){
        this.json = json;
        this.priority = priority;
    }
    public String path;
    public Variant(JSONObject json,String path,int priority){
        this.json = json;
        this.priority = priority;
        this.path = path;
    }
}
