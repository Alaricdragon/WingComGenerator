package main.threads;

import main.processers.MergeListMaster;
import main.types.HullJson;
import main.types.Variant;

import java.util.ArrayList;

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
                if (id.equals(a.json.get("hullId").toString())&& b.priority > a.priority){
                    build = false;
                    break;
                }
            }
            if (build) out.add(a);
        }

        master.addList(out);
    }
}
