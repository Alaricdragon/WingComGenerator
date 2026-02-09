package main.threads;

import main.processers.CustomJSonReader;
import main.processers.MultiGetArray;
import main.settings.Settings;
import main.types.FactionStorge;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CreateFactionFiles implements Runnable{
    private FactionStorge faction;
    private MultiGetArray<String> ids;
    private MultiGetArray<ArrayList<String>> tags;
    public CreateFactionFiles(FactionStorge faction, MultiGetArray<String> ids, MultiGetArray<ArrayList<String>> tags){
        this.faction = faction;
        this.ids = ids;
        this.tags = tags;
    }
    @Override
    public void run() {
        /*todo:
            there is a possability I messed up here.
            if I did, i will need to consider the 'restricted' status of a giving wing as well.
            but hopefully I didnt. besides, restriced is disabled by defalt.
         */
        int id1 = ids.getAndReserveListID();
        int id2 = tags.getAndReserveListID();
        ArrayList<String> ids2 = ids.getList(id1);
        ArrayList<ArrayList<String>> tags2 = tags.getList(id2);
        ArrayList<String> fightersToAdd = new ArrayList<>();
        for (int a = 0; a < ids2.size(); a++){
            String fid = ids2.get(a);
            if (faction.blueprints_fighters.contains(fid)){
                fightersToAdd.add(Settings.getHullID(fid));
                continue;
            }

            for (String b : faction.blueprints_tags.getListWithLock()){
                if (tags2.get(a).contains(b.trim())){
                    fightersToAdd.add(Settings.getHullID(fid));
                    break;
                }
            }
            faction.blueprints_tags.unlock();
        }
        ids.unlockList(id1);
        tags.unlockList(id2);
        //all ids added. yay.
        faction.fighters = fightersToAdd;//for refrence. I dont think I will use it though?
        if (fightersToAdd.isEmpty()) return;
        JSONObject out = new JSONObject();
        out.put("id",faction.id);
        JSONObject objectA = new JSONObject();
        out.put("knownShips",objectA);
        JSONArray arrayA = new JSONArray();
        objectA.put("hulls",arrayA);
        for (String a : fightersToAdd){
            arrayA.add(a);
        }
        String key = "./"+faction.path;
        //System.out.println("attempting to get file path of: "+key);
        String[] splitKey = key.split("/");
        String newKey = "";
        for (int a = 0; a < splitKey.length-1; a++){
            newKey+=splitKey[a];
            if (a < splitKey.length-2)newKey+="/";
        }
        //System.out.println("creating path to faction: "+newKey);
        new File(newKey).mkdirs();//make sure file path exsists.
        try {
            //System.out.println("attempting to get file path of: "+key);
            CustomJSonReader.writeJsonFile(key,out);
        } catch (IOException e) {
            //System.out.println("failed to create file at path of: "+key);
            throw new RuntimeException(e);
        }
    }
}
