package main.types;

import org.LockedList;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class FactionStorge {
    public int priority;
    public ArrayList<String> fighters = new ArrayList<>();
    public LockedList<String> blueprints_tags = new LockedList<>(false);
    public LockedList<String> blueprints_fighters = new LockedList<>(false);
    public String path;
    public String id;
    //public JSONObject json;
    public FactionStorge(String id, String path){//, JSONObject json){
        this.id = id;
        this.path = path;
        //this.json = json;
    }
}
