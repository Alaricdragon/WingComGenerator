package main.threads;

import java.util.ArrayList;

public class Organize_FactionFighters implements Runnable{
    String path;
    ArrayList<String> list;
    public Organize_FactionFighters(String path, ArrayList<String> list){
        this.path = path;
        this.list = list;
    }
    @Override
    public void run() {
        /*
        todo:
            1) look into the faction file, and see the bluepirnts they have available
            2) IF they have a valid fighter blueprint available, add that fighter to the 'available fighter' of that faction. (relevent faction data not created yet this is a lot of work)
        */
    }
}
