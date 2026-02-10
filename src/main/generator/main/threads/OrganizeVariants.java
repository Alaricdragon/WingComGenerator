package main.threads;

import main.processers.MergeListMaster;
import main.processers.MultiGetArray;
import main.types.Fighter;
import main.types.Variant;

import java.util.ArrayList;

import static main.threads.OrganizeHullJsons.attemptMerging;

public class OrganizeVariants implements Runnable{
    ArrayList<Variant> list;
    ArrayList<Variant> list2;
    MergeListMaster<Variant> master;
    public OrganizeVariants(ArrayList<Variant> list, ArrayList<Variant> list2, MergeListMaster<Variant> master){
        this.list = list;
        this.list2 = list2;
        this.master = master;
    }
    @Override
    public void run() {
        ArrayList<Variant> out = list;
        boolean build;
        for (Variant a : list2){
            String id = a.json.get("variantId").toString();
            build = true;
            for (Variant b : out){
                //todo: find if lower 'level' hulls go first?
                if (id.equals(b.json.get("variantId").toString())){
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
}
