package main.types;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class ModStorge {
    //how this will work:
    //in stage 1,
    public String path;
    public String id;
    public int order;
    public ModStorge(String path,String id){
        this.id = id;
        this.path = path;
        setOrderValue(id);
    }
    private void setOrderValue(String id){
        if (id.equals("starsector")){
            order = Integer.MAX_VALUE;
            //order = Integer.MIN_VALUE;
            System.out.println(id+" has a order of: "+order);
            return;
        }
        order = 0;
        int size = 1000;
        String id2 = id.toLowerCase();
        for (int a = 0; a < 5 && a < id2.length(); a++) {
            order += ((int)id2.charAt(a)) * (((4-a)*size)+1);
        }
        System.out.println(id+" has a order of: "+order);
    }

    public ReentrantLock lock = new ReentrantLock(false);
    public ArrayList<Fighter> fighters = new ArrayList<>();
    public ArrayList<Hull> hulls = new ArrayList<>();
    public ArrayList<Variant> variants = new ArrayList<>();
    public ArrayList<HullJson> hullJsons = new ArrayList<>();
    public JSONObject factionSettings = null;
}
