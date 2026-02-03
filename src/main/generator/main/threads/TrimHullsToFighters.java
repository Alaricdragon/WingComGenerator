package main.threads;

import main.Seeker;
import main.processers.MultiGetArray;
import main.types.Hull;

import java.util.ArrayList;

public class TrimHullsToFighters implements Runnable{
    ArrayList<Hull> hulls;
    MultiGetArray<String> validIds;
    public TrimHullsToFighters(ArrayList<Hull> hulls, MultiGetArray<String> validIds){
        this.hulls = hulls;
        this.validIds = validIds;
    }
    @Override
    public void run() {
        int id = validIds.getAndReserveListID();
        ArrayList<String> list = validIds.getList(id);
        for (Hull a : hulls){
            if (list.contains(a.ship_csv.id)) Seeker.addFinalHull(a);
        }
        validIds.unlockList(id);
    }
}
