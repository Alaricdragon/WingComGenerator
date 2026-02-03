package main.threads;

import main.Seeker;
import main.types.FactionPaths;
import main.types.FactionStorge;
import main.types.ModStorge;

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

            yes, this intier process can run paraleal. so it should be fine...?
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
            organizeFactionCSV();
            findFactionJSons();
            organizeFactionJsons();
            findFactionFighters();
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
        for (FactionPaths a : factionPaths){
            log+="\n    "+a.path;
        }
        System.out.println(log);

    }
    public void organizeFactionCSV(){
        /*todo:
            1) create 1 MergeListMaster for this. it should beable to handle this (faction CSVs are probably load order dependant.)
            -) this will be what creates the FactionStorge files.
        */
    }
    public void findFactionJSons(){
        /*todo:
            1) create 1 Seek_ per mod (look for all relevent faction files in each mod)
            2) wait for this process to finish
        */

    }
    public void organizeFactionJsons(){
        /* todo:
            1) merge all the 'to add' fighters together. (can I use MergeListMaster here? maybe...)
            -) no need to worry about overwriting here. they are all merged anyways, not overwriten like others.
         */
        //NOTE: unlike other things, faction jsons are added together, effectively. so I dont really need this step? that or I just merge all the lists.
    }
    public void findFactionFighters() throws InterruptedException {
        while (!Seeker.finishedGettingCoreData()){//this holds this part of the program here, until I have fully gotten the relevant data.
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
    private static ArrayList<FactionPaths> factionPaths = new ArrayList<>();
    public static synchronized void addFactionPath(String path,int order){
        factionPaths.add(new FactionPaths(path,order));
    }
}
