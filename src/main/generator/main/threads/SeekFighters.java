package main.threads;

import com.opencsv.bean.CsvToBeanBuilder;
import main.Seeker;
import main.beans.Bean_Fighter;
import main.types.Fighter;
import main.types.Hull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SeekFighters implements Runnable{
    private String path;
    private String mod;
    private float priority;
    public SeekFighters(String mod,String path,float priority){
        this.mod = mod;
        this.path = path;
        this.priority = priority;
    }
    @Override
    public void run() {
        ArrayList<Fighter> out = new ArrayList<>();
        //todo: process fighterCSV here.
        /*so.. how?
            1) get the csv file.
            2) make sure its valid. I think I have a types that handles this?

        */
        try {
            List<Bean_Fighter> beans = new CsvToBeanBuilder(new FileReader(path+"/data/hulls/wing_data.csv")).withType(Bean_Fighter.class).build().parse();
            for (Bean_Fighter a : beans) if (a.isValid()) out.add(new Fighter(a,priority));
        } catch (FileNotFoundException e) {
            //System.err.println("failed to get fighter. reason: "+e);
            return;
            //throw new RuntimeException(e);
        }
        //System.out.println("get fighters. yay.");
        Seeker.addFighters(mod,out);
    }
}
