package main.threads;

import main.processers.MergeListMaster;
import main.types.Fighter;
import main.types.Variant;

import java.util.ArrayList;

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
                if (id.equals(a.json.get("variantId").toString())&& b.priority > a.priority){
                    build = false;
                    break;
                }
            }
            if (build) out.add(a);
        }
        master.addList(out);
    }
}
