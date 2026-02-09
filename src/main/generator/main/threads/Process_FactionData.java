package main.threads;

import main.Seeker;
import main.processers.MergeListMaster;
import main.processers.MultiGetArray;
import main.types.FactionPaths;
import main.types.FactionStorge;
import main.types.MatedFighters;
import main.types.ModStorge;
import org.LockedHashMap;
import org.LockedList;
import org.LockedVariable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Process_FactionData implements Runnable{
    @Override
    public void run() {
        //clearFactionJsons();
        //clearRolesJson();
        try {
            findFactionCSV();
            findFactionJSons();
            findFactionFighters();
        }catch (Exception e){
            System.err.println("EMERGENCY: failure in ProcessFactionData. the generator may still compleat, but ships wont show up without consal commands. \n"+e);
        }
    }
    public void clearFactionJsons(){
        /*todo:
           1) just remove the 'factions' folder? it should work.... right?
       */
        //File myObj = new File("./data/world/factions");
        //myObj.delete();
        //Seeker.finishedClearingData.change(1);
    }
    public void clearRolesJson(){
        /* todo:
            1) just delete the roles.json. located at....?
         */
        File myObj = new File("./data/world/default_ship_roles.json");
        myObj.delete();
    }
    public void findFactionCSV() throws InterruptedException {
        Seeker.getStorgeLock().lock();
        HashMap<String, ModStorge> mods = Seeker.storge;
        int size = mods.size();
        ThreadGroup pGroup = new ThreadGroup("FactionData_0");
        for (String a : mods.keySet()){
            ModStorge m = mods.get(a);
            String b = m.path;
            int order = m.order;
            new Thread(pGroup,new SeekFactionCSV(a,b,order)).start();
        }
        Seeker.getStorgeLock().unlock();
        while (pGroup.activeCount() != 0){
            Thread.sleep(100);
            System.out.println("status (find faction CSV): "+(size-pGroup.activeCount())+ " / "+size);
        }
        String log = "status (find faction CSV)"+"\n"+"faction paths found:";
        for (FactionPaths a : factionPaths.getListWithLock()){
            log+="\n    "+a.path;
        }
        factionPaths.unlock();
        System.out.println(log);

    }
    public void findFactionJSons() throws InterruptedException {
        /*todo:
            1) create 1 SeekFactionPaths per mod (look for all relevent faction files in each mod)
            2) wait for this process to finish
        */
        //if (true) return;
        System.out.println("finding faction jsons...");
        ArrayList<String> pathst = new ArrayList<>();
        for (FactionPaths a : factionPaths.getListWithLock()){
            pathst.add(a.path);
        }
        factionPaths.unlock();
        MultiGetArray<String> paths = new MultiGetArray<>(pathst,(factionPaths.size() / 3) + 1);
        //factionPaths.unlock();
        ThreadGroup pGroup = new ThreadGroup("find_faction_jsons");
        Seeker.getStorgeLock().lock();
        int size = Seeker.storge.size();
        for (String a : Seeker.storge.keySet()){
            String b = Seeker.storge.get(a).path;
            new Thread(pGroup,new SeekFactionPaths(b,paths)).start();
        }
        Seeker.getStorgeLock().unlock();
        while (pGroup.activeCount() != 0){
            System.out.println("status: (find faction jsons): "+(size-pGroup.activeCount())+" / "+size);
            Thread.sleep(1000);
        }
        String log = "status: (find faction jsons): compleat. \n factions with fighters are:";
        for (String a : factionStorge.getListWithLock().keySet()){
            FactionStorge b = factionStorge.get(a).get();
            log += "\n    "+b.id+" has "+b.blueprints_tags.size()+" tags and "+b.blueprints_fighters.size()+" fighter blueprints";
        }
        factionStorge.unlock();
        System.out.println(log);
    }

    public void findFactionFighters() throws InterruptedException {
        while (!Seeker.finishedGettingCoreData() || !Seeker.hasMatedFighters.get() || Seeker.finishedClearingData.get() < 4){//this holds this part of the program here, until I have fully gotten the relevant data.
            Thread.sleep(1000);
        }
        System.out.println("started creation of faction files (so factions have acsess to new wingcom ships)");
        ArrayList<String> idt = new ArrayList<>();
        ArrayList<ArrayList<String>> tagt = new ArrayList<>();
        String temp;
        ArrayList<String> tempList;
        for (MatedFighters a : Seeker.matedFighters.getListWithLock()){
            idt.add(a.fighter.fighter_csv.id);
            tempList = new ArrayList<>();
            tagt.add(tempList);
            temp = a.fighter.fighter_csv.tags;
            if (temp.isBlank())  continue;
            String[] items = temp.split(",");
            for (String b : items) {
                tempList.add(b.trim());
            }
        }
        Seeker.matedFighters.unlock();
        int size = factionStorge.size();
        MultiGetArray<String> ids = new MultiGetArray<>(idt,(size / 2) + 1);
        MultiGetArray<ArrayList<String>> tags = new MultiGetArray<>(tagt,(size / 2) + 1);
        ThreadGroup pGroup = new ThreadGroup("creating faction json data");
        for (String a : factionStorge.getListWithLock().keySet()){
            new Thread(pGroup,new CreateFactionFiles(factionStorge.get(a).get(),ids,tags)).start();
        }
        factionStorge.unlock();
        while (pGroup.activeCount() != 0){
            System.out.println("status (create faction json files): "+(size-pGroup.activeCount()) + " / "+pGroup.activeCount());
            Thread.sleep(1000);
        }
        String log = "status (create faction json files): Compleat \n found the following fighters inside of each faction:";
        for (String a : factionStorge.getListWithLock().keySet()){
            FactionStorge b = factionStorge.get(a).get();
            if (b.fighters.isEmpty()) continue;
            log+="\n  "+b.id+"found "+b.fighters.size()+" fighters of Ids: ";
            for (String c : b.fighters){
                log+=c+", ";
            }
        }
        factionStorge.unlock();
        System.out.println(log);
    }
    public static LockedList<FactionPaths> factionPaths = new LockedList<>(false);
    public static LockedHashMap<String, LockedVariable<FactionStorge>> factionStorge = new LockedHashMap<>(false);
}
