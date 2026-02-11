package main.threads;

import main.processers.MergeListMaster;
import main.types.HullJson;
import main.types.Variant;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class OrganizeHullJsons implements Runnable{
    ArrayList<HullJson> list;
    ArrayList<HullJson> list2;
    MergeListMaster<HullJson> master;
    public OrganizeHullJsons(ArrayList<HullJson> list, ArrayList<HullJson> list2, MergeListMaster<HullJson> master){
        this.list = list;
        this.list2 = list2;
        this.master = master;
    }
    @Override
    public void run() {
        ArrayList<HullJson> out = list;
        boolean build;
        for (HullJson a : list2){
            String id = a.json.get("hullId").toString();
            build = true;
            for (HullJson b : out){
                //todo: find if lower 'level' hulls go first?
                if (id.equals(b.json.get("hullId").toString())){
                    System.out.println("found identical hull jsons of "+id);
                    if (b.priority > a.priority){
                        b.json = attemptMerging(a.json,b.json);
                    }else{
                        b.json = attemptMerging(b.json,a.json);
                    }
                    build = false;
                }
            }
            if (build) out.add(a);
        }

        master.addList(out);
    }
    public static JSONObject attemptMerging(JSONObject primary, JSONObject secondary){
        /*todo:
            1: get all 'keys' in a giving jsonObject.
            2: any key that exists in secondary but not primary gets copyed.
            3: any time both objects have the same key, it is a jsonObject, AND primary has the key 'id' in it:
                run this on said jsonObject, with checkIds true.
         */
        Iterator keySet = primary.keySet().iterator();
        while (keySet.hasNext()){
            Object p_key = keySet.next();
            if (!secondary.containsKey(p_key)) continue;//no need to check anything
            Object p_item = primary.get(p_key);
            if (p_item instanceof JSONArray){
                JSONArray p_array = (JSONArray) p_item;
                if (p_array.isEmpty()) continue;
                if (!(p_array.getFirst() instanceof JSONObject)) continue;
                if (((JSONObject)p_array.getFirst()).containsKey("id")){
                    JSONObject p_json;
                    JSONObject s_json;
                    JSONArray s_array = (JSONArray) secondary.get(p_key);
                    if (s_array.isEmpty()) continue;
                    JSONObject n_json;
                    //this looks for items of the same ID.
                    //this intier loop is organized backwards. it should be looking at secondary array instead of primary. I think.
                    ArrayList<Boolean> exists = new ArrayList<>();
                    for (int z = 0; z < s_array.size(); z++) exists.add(false);
                    for (int a = 0; a < p_array.size(); a++){
                        Object t = p_array.get(a);
                        if (!(t instanceof JSONObject)) continue;
                        p_json = (JSONObject) p_array.get(a);
                        for (int b = 0; b < s_array.size(); b++){
                            t = s_array.get(b);
                            if (!(t instanceof JSONObject)){
                                exists.set(b,true);
                                continue;
                            }
                            s_json = (JSONObject) t;
                            if (p_json.containsKey("id") && s_json.containsKey("id") && s_json.get("id").toString().equals(p_json.get("id").toString())){
                                n_json = attemptMerging(p_json,s_json);
                                p_array.set(a,n_json);
                                exists.set(b,true);
                                break;
                            }
                        }
                    }
                    for (int b = 0; b < exists.size(); b++){
                        if (exists.get(b)) continue;
                        Object t = s_array.get(b);
                        if (!(t instanceof JSONObject) || !(((JSONObject) t).containsKey("id"))){
                            continue;
                        }
                        p_array.add(t);
                    }
                }
                continue;
            }
            if (!(p_item instanceof JSONObject)) continue;
            Object s_item = secondary.get(p_key);
            if (!(s_item instanceof JSONObject)) continue;
            JSONObject p_json = (JSONObject) p_item;
            JSONObject s_json = (JSONObject) s_item;
            if (p_json.containsKey("id") && s_json.containsKey("id") && p_json.get("id").toString().equals(s_json.get("id").toString())){
                primary.put(p_key.toString(),attemptMerging(p_json,s_json));
            }
        }
        keySet = secondary.keySet().iterator();
        while (keySet.hasNext()){
            Object s_key = keySet.next();
            if (!primary.containsKey(s_key)){
                primary.put(s_key.toString(),secondary.get(s_key.toString()));
                //System.out.println("adding a new item form that thing of key: "+s_key.toString()+" of data: "+secondary.get(s_key.toString()));
            }
        }

        return primary;
    }
}
