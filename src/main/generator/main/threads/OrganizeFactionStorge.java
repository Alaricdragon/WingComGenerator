package main.threads;

import main.processers.MergeListMaster;
import main.types.FactionStorge;

import java.util.ArrayList;

public class OrganizeFactionStorge implements Runnable{
    private ArrayList<FactionStorge> listA;
    private ArrayList<FactionStorge> listB;
    private MergeListMaster<FactionStorge> This;
    public OrganizeFactionStorge(ArrayList<FactionStorge> listA, ArrayList<FactionStorge> listB, MergeListMaster<FactionStorge> This){
        this.listA = listA;
        this.listB = listB;
        this.This = This;
    }
    @Override
    public void run() {
        /*
        ArrayList<FactionStorge> out = listA;
        for (FactionStorge b : listB){
            boolean canAdd = true;
            for (FactionStorge a : out){
                if (){

                }
            }
            if (canAdd) out.add(b);
        }
        This.addList(out);*/
    }
}
