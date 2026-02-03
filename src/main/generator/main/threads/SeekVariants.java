package main.threads;

import main.Seeker;
import main.processers.CustomJSonReader;
import main.processers.MultiGetArray;
import main.types.Variant;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.ArrayList;

public class SeekVariants implements Runnable{
    private String mod;
    private String path;
    private MultiGetArray<String> seekingIds;
    private int priority;
    public SeekVariants(String mod, String path, MultiGetArray<String> lists){
        this.mod = mod;
        this.path = path;
        this.seekingIds = lists;
        priority = Seeker.getModStorge(mod).order;
    }
    @Override
    public void run() {
        /*todo:
            1) I take the path, and I look at every variant inside of said mods variant folder
            2) I store any variants with a matching 'id' to any seekingIds into a array
            3) then I set it to be remembered. (into the inputed ModStorge)
         */

        ArrayList<Variant> out = new ArrayList<>();
        //todo: process all Variants here.
        /* so this is a bit harder then normal.
            1) I need to look into the folder, and find all files of valid types. (.variant)
            2) I need to look at everything else, and attempt to look -into- everything else.
            3) go back to step one within the new zone each time, for better seeking
            4) once I have found all the files, I want to look at them all one by one looking for valid data.
         */
        paths.add(path+"/data/variants");
        while (!paths.isEmpty()) {
            getFolder(paths.getFirst());
        }
        int check = seekingIds.getAndReserveListID();
        ArrayList<String> list = seekingIds.getList(check);
        while (!filePaths.isEmpty()){
            Variant b = checkVariant(filePaths.getFirst(),list);
            if (b != null) out.add(b);
        }
        seekingIds.unlockList(check);
        Seeker.addVariants(mod,out);
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
            if (filename.endsWith(".variant")){
                filePaths.add(path+"/"+file.getName());
            }else{
                paths.add(path+"/"+file.getName());
            }
        }
    }
    private Variant checkVariant(String path,ArrayList<String> list){
        filePaths.removeFirst();
        try {
            JSONObject json = CustomJSonReader.getObject(path);
            String id = json.get("variantId").toString();
            if (list.contains(id)) return new Variant(json,priority);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
