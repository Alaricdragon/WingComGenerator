package main.threads;

import main.Seeker;
import main.processers.MergeListMaster;
import main.processers.MultiGetArray;
import main.types.FactionPaths;
import main.types.FactionStorge;
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

        /*todo:
            this has two jobs. first, it creates a second thread to handle 'secondary' operations. this thread will do the following:
            1: find faction CSV files.
            2: find faction.json fines.
            3: organize faction.json files.
            4: let this thread 'hold' at 'find faction fighters'.
                -note: I should make it so reorganizeHullAndFighters sets a flag when its done. this process can run parallel.
                -this lets me move findFactionFighters into the this thread group.
            5: add the blueprints to the relative factions.
            6: create the roles.json file.
            NOTICE: its possable to fail to get the currect variant on certen fighters.
            as a resalt, I need to wait until -after- fighter mates have been created to create relevent faction and roles.json files.

            yes, this intier process can run paraleal. so it should be fine...?
         */
        /*

        */
        /*possable issues: 1: are faction files always in the same position?*/
        // faction csv location 'data\world\factions\factions.csv' it finds us the faction files with the 'faction' field. (relitive to the mod)
        // known fighters: faction file :
        // "knownFighters":{
        //		"tags":["rat_abyssals"],
        //		"fighters":[
        //		],
        //	},
        // all fighters with the right tags are added.
        // all fighters in the "fighters":[] are added. both are json arrays.
        clearFactionJsons();
        clearRolesJson();
        try {
            findFactionCSV();
            findFactionJSons();
            findFactionFighters();//only once the merged list is fully ready.
            createRolesJson();
            createFactionJsons();
        }catch (Exception e){
            System.err.println("EMERGENCY: failure in ProcessFactionData. the generator may still compleat, but ships wont show up without consal commands. \n"+e);
        }
    }
    public void clearFactionJsons(){
        /*todo:
           1) just remove the 'factions' folder? it should work.... right?
       */
        File myObj = new File("./data/world/factions");
        myObj.delete();
        Seeker.finishedClearingData.change(1);
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
        while (!Seeker.finishedGettingCoreData() || !Seeker.hasMatedFighters.get()){//this holds this part of the program here, until I have fully gotten the relevant data.
            Thread.sleep(100);
        }
        /* todo:
            1) create 1 Organize_ per faction.
            2) wait for this to finish
         */
    }
    public void createRolesJson(){
        /*todo:
            1) create 1 Create_.
            this process can run in the background.
        */
    }
    public void createFactionJsons(){
        /*todo:
            1) create 1 Create_ per faction.
            2) wait for this process to finish
        */
    }
    public static LockedList<FactionPaths> factionPaths = new LockedList<>(false);
    public static LockedHashMap<String, LockedVariable<FactionStorge>> factionStorge = new LockedHashMap<>(false);
}
