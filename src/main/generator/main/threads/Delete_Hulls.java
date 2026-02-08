package main.threads;

import main.Seeker;

import java.io.File;

public class Delete_Hulls implements Runnable{
    @Override
    public void run() {
        System.out.println("status (remove old variant files): stated");
        File myObj = new File("./data/hulls");
        File[] fileList = myObj.listFiles();
        if (fileList == null) return;
        for( File file : fileList ){
            String filename = file.getName();
            if (filename.endsWith(".ship")){
                File toRemove = new File("./data/hulls/"+filename);
                toRemove.delete();
            }
        }
        String log = "status (remove old variant files): complete";
        System.out.println(log);
        Seeker.finishedClearingData.change(1);
    }
}
