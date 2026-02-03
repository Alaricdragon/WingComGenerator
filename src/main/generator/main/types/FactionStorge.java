package main.types;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class FactionStorge {
    public ReentrantLock lock = new ReentrantLock(false);
    public int priority;
    public ArrayList<String> fighters = new ArrayList<>();
    public String path;
    public String id;
    public FactionStorge(String path){
        this.path = path;
    }
}
