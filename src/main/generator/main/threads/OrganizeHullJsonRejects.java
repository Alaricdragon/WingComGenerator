package main.threads;

import main.Seeker;
import main.processers.MultiGetArray;
import main.types.HullJson;

import java.util.ArrayList;

public class OrganizeHullJsonRejects implements Runnable{
    private HullJson main;
    private MultiGetArray<String> path;
    MultiGetArray<ArrayList<HullJson>> finders;
    public OrganizeHullJsonRejects(HullJson main, MultiGetArray<String> path, MultiGetArray<ArrayList<HullJson>> finders){
        //......
        //
        this.main = main;
        this.path = path;
        this.finders = finders;
    }
    @Override
    public void run() {
        String p = main.path;

        int id = path.getAndReserveListID();
        ArrayList<String> list = path.getList(id);
        for (int a = 0; a < list.size(); a++){
            if (list.get(a).equals(p)){
                int id2 = finders.getAndReserveListID();
                ArrayList<HullJson> list2 = finders.getList(id2).get(a);
                HullJson active = list2.get(0);
                for (int c = 1; c < list2.size(); c++){
                    //todo: in here... I would need to.. to... to... why? whyasdjasjda
                    //      I would need to get the 'main' item, and have it override any items of a lower priority. but not ones of a greater priority. its a big fucking mess. I dont want to deal with it at all....
                    HullJson active2 = list2.get(c);
                    if (active.priority > active2.priority){
                        OrganizeHullJsons.attemptMerging(active2.json,active.json);
                        active = list2.get(c);
                    }else{
                        OrganizeHullJsons.attemptMerging(active.json,active2.json);
                    }
                }
                finders.unlockList(id2);
                break;
            }
        }
        path.unlockList(id);
    }
}
