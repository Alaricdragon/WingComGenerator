package main.threads;

import main.processers.MergeListMaster;
import main.settings.Settings;

import java.util.ArrayList;

public class OrganizeAllowRestricted implements Runnable{
    private ArrayList<String> list;
    private ArrayList<String> list2;
    private MergeListMaster<String> master;
    public OrganizeAllowRestricted(ArrayList<String> list, ArrayList<String> list2, MergeListMaster<String> master){
        this.list = list;
        this.list2 = list2;
        this.master = master;
    }
    @Override
    public void run() {
        //String log = "in as:";
        //log+= "\n(old size a)"+list.size();
        //log+= "\n(old size b)"+list2.size();
        ArrayList<String> out = list;
        for (String a : list2){
            if (!out.contains(a)) out.add(a);
        }
        //log+="\n(new size)"+out.size();
        //System.err.println(log);
        master.addList(out);
    }
}
