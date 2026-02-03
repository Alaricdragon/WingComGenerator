package main.threads;

import java.io.File;

public class Delete_Hulls implements Runnable{
    @Override
    public void run() {
        System.out.println("status (remove old variant files): stated");
        File myObj = new File("./data/hulls");
        myObj.delete();
        String log = "status (remove old variant files): complete";
        System.out.println(log);
    }
}
