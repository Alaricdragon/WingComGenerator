package main.threads;

import main.processers.MergeListMaster;
import main.processers.MultiGetArray;
import main.settings.Settings;
import main.types.Fighter;

import java.util.ArrayList;

public class OrganizeFighters implements Runnable{
    ArrayList<Fighter> list;
    ArrayList<Fighter> list2;
    MergeListMaster<Fighter> master;

    MultiGetArray<String> excludeList;
    MultiGetArray<String> allowRestricted;
    public OrganizeFighters(ArrayList<Fighter> list, ArrayList<Fighter> list2, MergeListMaster<Fighter> master, MultiGetArray<String> excludeList,MultiGetArray<String> allowRestricted){
        this.list = list;
        this.list2 = list2;
        this.master = master;

        this.excludeList = excludeList;
        this.allowRestricted = allowRestricted;
    }
    @Override
    public void run() {
        ArrayList<Fighter> out = list;
        boolean build;
        for (Fighter a : list2){
            String id = a.fighter_csv.id;
            build = true;
            for (int c = 0; c < out.size(); c++){
                Fighter b = out.get(c);
                //todo: find if lower 'level' hulls go first?
                if (id.equals(b.fighter_csv.id)){
                    if (b.priority > a.priority){
                        //do nothing.
                    }else{
                        out.set(c,a);
                    }
                    build = false;
                    break;
                }
            }
            if (build) out.add(a);
        }
        //System.out.println("started the second skim");
        int eid = excludeList.getAndReserveListID();
        int aid = allowRestricted.getAndReserveListID();
        ArrayList<String> exclude = excludeList.getList(eid);
        ArrayList<String> allow = allowRestricted.getList(aid);
        //System.out.println("    1");
        for (int a = out.size()-1; a >= 0; a--){
            Fighter b = out.get(a);
            if (allow.contains(b.fighter_csv.id)) continue;
            if (exclude.contains(b.fighter_csv.id)){
                out.remove(a);
                continue;
            }
            if (Settings.isRestrictedMaybe(b.fighter_csv) && !Settings.buildRestricted.get()) out.remove(a);
        }
        //System.out.println("    2");
        excludeList.unlockList(eid);
        allowRestricted.unlockList(aid);
        //System.out.println("    3");

        master.addList(out);
    }
}
