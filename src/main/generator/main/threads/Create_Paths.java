package main.threads;

import main.Seeker;

import java.io.File;

public class Create_Paths implements Runnable{
    @Override
    public void run() {
        while (Seeker.finishedClearingData.get() < 4){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        new File("./data/hulls").mkdirs();
        new File("./data/variants").mkdirs();
        //new File("./data/hulls").mkdirs();
        Seeker.finishedCreatingPaths.set(true);
    }
}
