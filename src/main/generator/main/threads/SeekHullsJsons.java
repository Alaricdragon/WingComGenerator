package main.threads;

import main.Seeker;
import main.processers.CustomJSonReader;
import main.processers.MultiGetArray;
import main.types.HullJson;
import main.types.Variant;
import org.LockedList;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
            String savedPath = path.replaceFirst(this.path,"");
            if (!json.containsKey("hullId")){
                //HOW THE FUCK AM I SUPPOSE TO DEAL WITH THIS??? KEEP THE PATH OF ALL FOUND SHIPS??? WHAT THE FUCK IS THE MATTER WITH THIS FUCKING THING. WHY IS IT LIKE THIS???? HOW DOES IT DETERMIN PATHS?!?!?!?!?!?!?!
                //FOR REAL THOUGH, WHAT THE FUCK AM I SUPPOSE TO DO ABOUT THIS????
                /*todo:
                    the only path forward here is relatively simple.
                    1: (done) when this happens, create a new 'pathed' hull json with no id. just a new array.
                    2: (done) make it so normal hulljson remembers its path, if applicable.
                    3: (place after the first organization of this data.)create a new organizer that takes the paths and merges them. so overrides are in effect.
                    aka there was no realy need to deal with the fucking shity shity whatbshjndbfhnvasm
                    sdgjvkdfsavbn cxs
                 */
                HashMap<String, ArrayList<HullJson>> a = Seeker.incompleatShipJsons.getListWithLock();
                if (!a.containsKey(savedPath)){
                    ArrayList<HullJson> b = new ArrayList<>();
                    b.add(new HullJson(json,savedPath,priority));
                    a.put(savedPath, b);
                }else{
                    a.get(savedPath).add(new HullJson(json,savedPath,priority));
                }
                Seeker.incompleatShipJsons.unlock();
                return null;
            }
            String id = json.get("hullId").toString();
            if (list.contains(id)) return new HullJson(json,savedPath,priority);
        } catch (Exception e) {
            System.err.println("failed to get hull from path of "+path);
            throw e;
        }
        return null;
    }

}
