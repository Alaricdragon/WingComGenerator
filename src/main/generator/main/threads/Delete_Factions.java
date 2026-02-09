package main.threads;

import main.Seeker;

import java.io.File;

public class Delete_Factions implements Runnable{
    @Override
    public void run() {
        System.out.println("status (remove old faction files): stated");
        File myObj = new File("./data/world/factions");
        File[] fileList = myObj.listFiles();
        if (fileList == null){
            String log = "status (remove old faction files): complete";
            System.out.println(log);
            Seeker.finishedClearingData.change(1);
            return;
        }
        for( File file : fileList ){
            String filename = file.getName();
            if (filename.endsWith(".faction")){
                File toRemove = new File("./data/world/factions/"+filename);
                toRemove.delete();
            }
        }
        String log = "status (remove old faction files): complete";
        System.out.println(log);
        Seeker.finishedClearingData.change(1);
    }
}
