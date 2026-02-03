package main.threads;

import main.processers.MergeListMaster;
import main.types.Fighter;

import java.util.ArrayList;

public class OrganizeFighters implements Runnable{
    ArrayList<Fighter> list;
    ArrayList<Fighter> list2;
    MergeListMaster<Fighter> master;
    public OrganizeFighters(ArrayList<Fighter> list, ArrayList<Fighter> list2, MergeListMaster<Fighter> master){
        this.list = list;
        this.list2 = list2;
        this.master = master;
    }
    @Override
    public void run() {
        ArrayList<Fighter> out = list;
        boolean build;
        for (Fighter a : list2){
            String id = a.fighter_csv.id;
            build = true;
            for (Fighter b : out){
                //todo: find if lower 'level' hulls go first?
                if (id.equals(b.fighter_csv.id )&& b.priority > a.priority){
                    build = false;
                    break;
                }
            }
            if (build) out.add(a);
        }
        master.addList(out);
    }
}
