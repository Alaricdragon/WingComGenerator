package main.threads;

import main.processers.CustomJSonReader;
import main.processers.MultiGetArray;
import main.types.FactionPaths;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.ArrayList;

public class SeekFactionPaths implements Runnable{
    private String path;
    private MultiGetArray<FactionPaths> paths;
    public SeekFactionPaths(String path, MultiGetArray<FactionPaths> paths){
        this.path = path;
        this.paths = paths;
    }
    @Override
    public void run() {
        int id = paths.getAndReserveListID();
        ArrayList<FactionPaths> paths2 = paths.getList(id);
        //todo: attempt to find the relevent json at the filepath.
        for (FactionPaths a : paths2) {
            try {
                JSONObject b = CustomJSonReader.getObject(path+"/"+a.path);

                //if (!b.containsKey("id")) return;
                //id = b.get("id").toString();
                //if (id.equals("wingcom_generator")) return;
            }catch (Exception e){
                //System.err.println("failed loop for faction: "+path+"/"+a.path+" of: "+e);
                //return;
            }
            /*
            try {
                JSONObject json = CustomJSonReader.getObject(path+a.path);
                System.out.println("got item of path: "+paths+a.path);
            } catch (ParseException e) {
                System.out.println("didnt item of path: "+paths+a.path);
                //throw new RuntimeException(e);
            }*/
        }
        paths.unlockList(id);
    }
}
