package main.types;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class FactionStorge {
    public ReentrantLock lock = new ReentrantLock(false);
    public int priority;
    public ArrayList<String> fighters = new ArrayList<>();
    public String path;
    public String id;
    public JSONObject json;
    public FactionStorge(String id, String path, JSONObject json){
        this.id = id;
        this.path = path;
        this.json = json;
    }
}
