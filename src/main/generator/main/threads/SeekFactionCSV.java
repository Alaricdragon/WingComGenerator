package main.threads;

import com.opencsv.bean.CsvToBeanBuilder;
import main.beans.Bean_Faction;
import main.beans.Bean_Fighter;
import main.types.Fighter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SeekFactionCSV implements Runnable{
    private String path;
    private String mod;
    private int order;
    public SeekFactionCSV(String mod,String path,int order){
        this.path = path;
        this.mod = mod;
        this.order = order;
    }
    @Override
    public void run() {
        try {
            List<Bean_Faction> beans = new CsvToBeanBuilder(new FileReader(path+"/data/world/factions/factions.csv")).withType(Bean_Faction.class).build().parse();
            for (Bean_Faction a : beans){
                if (a.faction.startsWith("#")) continue;
                Process_FactionData.addFactionPath(a.faction,order);
            }
        } catch (FileNotFoundException e) {
            return;
        }
    }
}
