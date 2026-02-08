package main.threads;

import main.Seeker;

import java.io.File;

public class Delete_Roles implements Runnable{
    @Override
    public void run() {
        //todo: this, and all deleate items, must deleate every item one by one.
        //  basicly, I need a function for mass deleation of all contents. and I dont require to deleate the folders themsefls.
        System.out.println("status (remove old rules files): stated");
        File myObj = new File("./data/world/factions/default_ship_roles.json");
        myObj.delete();
        String log = "status (remove old rules files): complete";
        System.out.println(log);
        Seeker.finishedClearingData.change(1);
    }
}
