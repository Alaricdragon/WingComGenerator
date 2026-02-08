package main.threads;

import main.Seeker;

import java.io.File;

public class Delete_Variants implements Runnable{
    @Override
    public void run() {
        //todo: this, and all deleate items, must deleate every item one by one.
        //  basicly, I need a function for mass deleation of all contents. and I dont require to deleate the folders themsefls.
        System.out.println("status (remove old variant files): stated");
        File myObj = new File("./data/variants");
        //myObj.delete();

        File[] fileList = myObj.listFiles();
        if (fileList == null) return;
        for( File file : fileList ){
            String filename = file.getName();
            if (filename.endsWith(".variant")){
                File toRemove = new File("./data/variants/"+filename);
                toRemove.delete();
            }
        }


        String log = "status (remove old variant files): complete";
        System.out.println(log);
        Seeker.finishedClearingData.change(1);
    }
}
