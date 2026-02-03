package main.threads;

import com.opencsv.bean.CsvToBeanBuilder;
import main.Seeker;
import main.beans.Bean_Fighter;
import main.beans.Bean_Ship;
import main.types.Fighter;
import main.types.Hull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SeekHulls implements Runnable{
    private String path;
    private String mod;
    private float priority;
    public SeekHulls(String mod,String path,float priority){
        this.mod = mod;
        this.path = path;
        this.priority = priority;
    }
    @Override
    public void run() {
        ArrayList<Hull> out = new ArrayList<>();
        //todo: process hullCSV here.
        try {
            List<Bean_Ship> beans = new CsvToBeanBuilder(new FileReader(path+"/data/hulls/ship_data.csv")).withType(Bean_Ship.class).build().parse();
            for (Bean_Ship a : beans) if (a.isValid()) out.add(new Hull(a,priority));
        } catch (FileNotFoundException e) {
            //System.err.println("failed to get hull. reason: "+e);
            //throw new RuntimeException(e);
            return;
        }
        //System.out.println("get hulls. yay.");
        Seeker.addHulls(mod,out);
    }
}
