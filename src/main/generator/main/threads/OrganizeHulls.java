package main.threads;

import main.processers.MergeListMaster;
import main.types.Hull;

import java.util.ArrayList;

public class OrganizeHulls implements Runnable{
    ArrayList<Hull> list;
    ArrayList<Hull> list2;
    MergeListMaster<Hull> master;
    public OrganizeHulls(ArrayList<Hull> list, ArrayList<Hull> list2, MergeListMaster<Hull> master){
        this.list = list;
        this.list2 = list2;
        this.master = master;
    }
    @Override
    public void run() {
        ArrayList<Hull> out = list;
        boolean build;
        for (Hull a : list2){
            String id = a.ship_csv.id;
            build = true;
            for (Hull b : out){
                //todo: find if lower 'level' hulls go first?
                if (id.equals(b.ship_csv.id )&& b.priority > a.priority){
                    build = false;
                    break;
                }
            }
            if (build) out.add(a);
        }
        master.addList(out);
    }
}

