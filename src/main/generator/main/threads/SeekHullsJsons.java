package main.threads;

import main.Seeker;
import main.processers.MultiGetArray;
import main.types.HullJson;

import java.util.ArrayList;

public class SeekHullsJsons implements Runnable{
    private String mod;
    private String path;
    private MultiGetArray<String> seekingIds;
    public SeekHullsJsons(String mod, String path, MultiGetArray<String> seekingIds){
        this.mod = mod;
        this.path = path;
        this.seekingIds = seekingIds;
    }
    @Override
    public void run() {
        /*todo:
            1) I take the path, and I look at every variant inside of said mods variant folder
            2) I store any variants with a matching 'id' to any seekingIds into a array
            3) then I set it to be remembered. (into the inputed ModStorge)
         */
        //WARNING: I do not yet have a class to store relevant hull data. in theory, I could add them to the relevant variants.
        ArrayList<HullJson> out = new ArrayList<>();
        //todo: process all Variants here.
        Seeker.addHullJsons(mod,out);
    }
}
