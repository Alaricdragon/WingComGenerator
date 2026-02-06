package main.threads;

import main.Seeker;

import java.io.File;

public class Delete_Roles implements Runnable{
    @Override
    public void run() {
        System.out.println("status (remove old rules files): stated");
        File myObj = new File("./data/world/factions/default_ship_roles.json");
        myObj.delete();
        String log = "status (remove old rules files): complete";
        System.out.println(log);
        Seeker.finishedClearingData.change(1);
    }
}
