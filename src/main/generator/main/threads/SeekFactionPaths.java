package main.threads;

import main.processers.CustomJSonReader;
import main.processers.MultiGetArray;
import main.types.FactionStorge;
import org.LockedVariable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.ArrayList;

public class SeekFactionPaths implements Runnable{
    private String path;
    private MultiGetArray<String> paths;
    public SeekFactionPaths(String path, MultiGetArray<String> paths){
        this.path = path;
        this.paths = paths;
    }
    @Override
    public void run() {
        //if (true) return;
        int id = paths.getAndReserveListID();
        ArrayList<String> paths2 = paths.getList(id);
        String seekPath;
        for (String a : paths2) {
            seekPath = path+"/"+a;
            //System.out.println("seeking path of: "+seekPath);
            File f = new File(seekPath);
            //f.
            if(!f.exists()){// || f.isDirectory()) {
                //System.out.println("-:"+seekPath+" does not exist");
                continue;
            }
            //System.out.println("+:"+seekPath+" does exist");
            try {
                JSONObject b = CustomJSonReader.getObject(seekPath);
                //System.out.println("found a item of: "+seekPath);
                if (!b.containsKey("id")) continue;
                String id2 = b.get("id").toString();
                FactionStorge storge;
                if (Process_FactionData.factionStorge.getListWithLock().containsKey(id2)){
                    storge = Process_FactionData.factionStorge.get(id2).getItemWithLock();
                    addToItem(b,storge);
                    Process_FactionData.factionStorge.get(id2).unlock();
                }else{
                    storge = new FactionStorge(id2,path);
                    addToItem(b,storge);

                    Process_FactionData.factionStorge.put(id2,new LockedVariable<FactionStorge>(storge,false));
                }
                Process_FactionData.factionStorge.unlock();
                /*
                todo:
                    1) get the faction id.
                    2) get the added blueprints to this faction (be it tags or others)
                        -what is the blueprints json coled again? jsons?
                    3) I will NOT be adding the fighters here. I will do that in another function. it will take less resources then.
	"knownFighters":{
		"tags":["sindrian_diktat", "lions_guard"],
		"fighters":[
			"talon_wing",
		],
	},
	id
                 */
                /*
                todo:
                    after some time I am forced to make an assumption:
                    -every- csv files data is valid regardless of priority.
                    why? because every csv file only links to a path leading to a faction file. all faction data is stored there.
                    therefore, if someone wanted to completely override a faction, they would not beable to.
                 */
            }catch (Exception e){
                CustomJSonReader.getObjectLog(seekPath);
                //System.out.println("failed to get faction of: "+seekPath+" for error of: "+e);
                continue;
            }
        }
        paths.unlockList(id);
    }
    private void addToItem(JSONObject json, FactionStorge storge){
        if (!json.containsKey("knownFighters")) return;
        JSONObject kf = (JSONObject) json.get("knownFighters");
        if (kf.containsKey("tags")){
            JSONArray array = (JSONArray) kf.get("tags");
            storge.blueprints_tags.getListWithLock();
            for (Object a : array){
                if (storge.blueprints_tags.contains(a.toString())) continue;
                storge.blueprints_tags.add(a.toString());
            }
            storge.blueprints_tags.unlock();
        }
        if (kf.containsKey("fighters")){
            JSONArray array = (JSONArray) kf.get("fighters");
            storge.blueprints_fighters.getListWithLock();
            for (Object a : array){
                if (storge.blueprints_fighters.contains(a.toString())) continue;
                storge.blueprints_fighters.add(a.toString());
            }
            storge.blueprints_fighters.unlock();
        }
    }
}
