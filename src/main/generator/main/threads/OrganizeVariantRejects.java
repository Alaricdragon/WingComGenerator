package main.threads;

import main.processers.MultiGetArray;
import main.types.HullJson;
import main.types.Variant;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class OrganizeVariantRejects implements Runnable{
    private Variant main;
    private MultiGetArray<String> path;
    MultiGetArray<ArrayList<Variant>> finders;
    public OrganizeVariantRejects(Variant main, MultiGetArray<String> path, MultiGetArray<ArrayList<Variant>> finders){
        //......
        //
        //todo: run this again for variants. I think I made a variant rejects.
        this.main = main;
        this.path = path;
        this.finders = finders;
    }
    @Override
    public void run() {
        String p = main.path;

        int id = path.getAndReserveListID();
        ArrayList<String> list = path.getList(id);
        ArrayList<Variant> organized = new ArrayList<>();
        for (int a = 0; a < list.size(); a++){
            if (list.get(a).equals(p)){
                int id2 = finders.getAndReserveListID();
                ArrayList<Variant> list2 = finders.getList(id2).get(a);
                //Variant active = list2.get(0);
                for (int c = 0; c < list2.size(); c++){
                    Variant active2 = list2.get(c);
                    if (organized.isEmpty()){
                        organized.add(active2);
                        continue;
                    }
                    boolean done = false;
                    for (int d = 0; d < organized.size(); d++){
                        //starts at highest value. ends at lowest.
                        //{3,6,1,22,7} = {1,3,6,7,22}
                        //hopefully, this will never include more then 6 items.
                        if (organized.get(d).priority < active2.priority){
                            organized.add(d,active2);
                            done = true;
                            break;
                        }
                    }
                    if (done) continue;
                    organized.add(active2);
                }
                finders.unlockList(id2);
                break;
            }
        }
        path.unlockList(id);
        if (organized.isEmpty()) return;
        System.out.println("merging a single file for a variant of hull id: "+main.json.get("hullId")+" with a path of: "+main.path);
        JSONObject f = new JSONObject();
        for (Variant a : organized){
            if (a.priority < main.priority){
                OrganizeHullJsons.attemptMerging(f,main.json);
                //break;//no break because I need to add both.
            }
            OrganizeHullJsons.attemptMerging(f,a.json);
        }
        main.json = f;
        //System.out.println("got final json from item of : "+main.json.get("hullId")+" as: \n"+main.json.toJSONString());
    }
}
