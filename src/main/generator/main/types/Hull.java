package main.types;

import main.beans.Bean_Ship;

public class Hull {
    public float priority;
    public Bean_Ship ship_csv;
    public Hull(Bean_Ship ship_csv, float priority){
        this.ship_csv=ship_csv;
        this.priority = priority;
    }
}
