package main.types;

import main.beans.Bean_Fighter;

public class Fighter {
    public Bean_Fighter fighter_csv;
    public float priority;//higher priority goes first
    public Fighter(Bean_Fighter fighter_csv, float priority){
        this.fighter_csv = fighter_csv;
        this.priority = priority;
    }
}
