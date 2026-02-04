package main.threads;

import main.processers.MergeListMaster;

import java.util.ArrayList;

public class OrganizeForceExclude implements Runnable{
    private ArrayList<String> list;
    private ArrayList<String> list2;
    private MergeListMaster<String> master;
    public OrganizeForceExclude(ArrayList<String> list, ArrayList<String> list2, MergeListMaster<String> master){
        this.list = list;
        this.list2 = list2;
        this.master = master;
    }
    @Override
    public void run() {
        ArrayList<String> out = list;
        for (String a : list2){
            if (!out.contains(a)) out.add(a);
        }
        master.addList(out);
    }
}
