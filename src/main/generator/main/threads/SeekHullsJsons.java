package main.threads;

import main.Seeker;
import main.processers.CustomJSonReader;
import main.processers.MultiGetArray;
import main.types.HullJson;
import main.types.Variant;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.ArrayList;

public class SeekHullsJsons implements Runnable{
    private String mod;
    private String path;
    private MultiGetArray<String> seekingIds;
    int priority;
    public SeekHullsJsons(String mod, String path, MultiGetArray<String> seekingIds){
        this.mod = mod;
        this.path = path;
        this.seekingIds = seekingIds;
        priority = Seeker.getModStorge(mod).order;
    }
    @Override
    public void run() {
        ArrayList<HullJson> out = new ArrayList<>();
        paths.add(path+"/data/hulls");
        while (!paths.isEmpty()) {
            getFolder(paths.getFirst());
        }
        int check = seekingIds.getAndReserveListID();
        ArrayList<String> list = seekingIds.getList(check);
        while (!filePaths.isEmpty()){
            HullJson b = checkHulls(filePaths.getFirst(),list);
            if (b != null) out.add(b);
        }
        seekingIds.unlockList(check);
        Seeker.addHullJsons(mod,out);
    }
    private ArrayList<String> paths = new ArrayList<>();
    private ArrayList<String> filePaths = new ArrayList<>();
    private void getFolder(String path){
        paths.removeFirst();
        File folder;
        try {
            folder = new File(path);
        } catch (Exception e) {
            return;
        }
        File[] fileList = folder.listFiles();
        if (fileList == null) return;
        for( File file : fileList ){
            String filename = file.getName();
            if (filename.endsWith(".ship")){
                filePaths.add(path+"/"+file.getName());
            }else{
                paths.add(path+"/"+file.getName());
            }
        }
    }
    private HullJson checkHulls(String path, ArrayList<String> list){
        filePaths.removeFirst();
        try {
            JSONObject json = CustomJSonReader.getObject(path);
            String id = json.get("hullId").toString();
            if (list.contains(id)) return new HullJson(json,priority);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
