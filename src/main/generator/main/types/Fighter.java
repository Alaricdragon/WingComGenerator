package main.types;

import main.beans.Bean_Fighter;

public class Fighter {
    public Bean_Fighter fighter_csv;
    public float priority;//higher priority goes first
    public Fighter(Bean_Fighter fighter_csv, float priority){
        this.fighter_csv = fighter_csv;
        this.priority = priority;
    }
    public Hull convertToShip(){
        //todo: make it so this returns a new hull, AKA a converted fighter.
        //Hull out = new Hull();
        return null;
    }
    public Variant convertToVariant(){
        //todo: make it so this returns a new varient, AKA a converted fighter
        //Variant out = new Variant();
        return null;
    }
}
