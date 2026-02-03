package main.threads;

import java.util.ArrayList;

public class Create_FactionFighters implements Runnable{
    String facID;
    ArrayList<String> list;
    public Create_FactionFighters(String facID, ArrayList<String> list){
        this.facID = facID;
        this.list = list;
    }
    @Override
    public void run() {
        /*
        todo:
            1: create a faction.json file.
            2: add the relevant fighter blueprints to each faction.
            -) note: the list is the fighterIds. NOT the newly created fighter IDs. so that's a thing.
         */
    }
}
