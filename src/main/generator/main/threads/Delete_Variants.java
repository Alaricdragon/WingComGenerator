package main.threads;

import java.io.File;

public class Delete_Variants implements Runnable{
    @Override
    public void run() {
        System.out.println("status (remove old variant files): stated");
        File myObj = new File("./data/variants");
        myObj.delete();
        String log = "status (remove old variant files): complete";
        System.out.println(log);
    }
}
