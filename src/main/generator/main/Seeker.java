package main;

import main.processers.MergeListMaster;
import main.processers.MultiGetArray;
import main.processers.CustomJSonReader;
import main.threads.*;
import main.types.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Seeker {
    private static ReentrantLock storgeLock = new ReentrantLock(false);
    private static ReentrantLock factionStorgeLock = new ReentrantLock(false);
    public static HashMap<String, ModStorge> storge = new HashMap<>();
    public static HashMap<String,FactionStorge> factionStorge = new HashMap<>();

    //private static MultiGetArray mods;
    //private static HashMap<String,String> modPaths = new HashMap<>();

    private static ArrayList<Fighter> finalFighters = new ArrayList<>();
    private static ArrayList<Hull> finalHulls = new ArrayList<>();
    private static ArrayList<Variant> finalVariants = new ArrayList<>();
    private static ArrayList<HullJson> finalHullJson = new ArrayList<>();
    public static void runAllSeekers() throws IOException, ParseException, InterruptedException {
        /*todo: there is a possibility I will need to look at all the factionCSV files to get the currect location of were to put the faction folder.
                if so, I can simply run that process first. it should be done by the time everything else is, but I should still lock the program at some ponit to avoid issues. (like at the stage I need faction data.)*/
        //todo: I need to errase all variant and ship files creates by this mod when it starts up, to avoid issues.
        findValidMods();
        Thread factionFinder = findFactionCSVFiles();
        findCSVFiles();
        reorganizeHullsAndFighters();
        ThreadGroup factionFighters = findFactionFighters(factionFinder);
        findVariantFiles();
        reorganizeVariants();
        findHullFiles();
        //applyFightersToFactions(factionFighters);
        createFighterSpec();
    }
    public static void findValidMods() throws IOException, ParseException, InterruptedException {
        JSONObject b = CustomJSonReader.getObject("../enabled_mods.json");
        System.out.println("looking for enabled mods...");
        ArrayList<String> getMods = new ArrayList<>();
        for (Object a : ((JSONArray)b.get("enabledMods"))){
            //System.out.println("    "+a.toString());
            getMods.add(a.toString());
        }
        System.out.println("finished finding enabled mods ("+getMods.size()+" found)");
        File folder = new File("..");
        File[] fileList = folder.listFiles();
        //create number of required threads with links.


        MultiGetArray<String> mods = new MultiGetArray<String>(getMods, (fileList.length / 3) + 1);
        ThreadGroup pGroup = new ThreadGroup("seeker0");
        for (File a : fileList) {
            System.out.println("../"+a.getName());
            new Thread(pGroup, new SeekModId("../"+a.getName(),mods)).start();
        }
        while (pGroup.activeCount() != 0) {
            Thread.sleep(100);//wait for all threads to finish.
            System.out.println("status: "+(fileList.length-pGroup.activeCount())+ " / "+fileList.length);
        }
        Seeker.addModPath("starsector", "../../starsector-core");
        System.out.println("status: compleat. mods valid:");
        for (String a : storge.keySet()) System.out.println("   "+a);
    }
    public static Thread findFactionCSVFiles(){
        Thread out = new Thread(new Process_FactionData());
        out.start();
        return out;
    }
    public static void findCSVFiles() throws InterruptedException {

        ThreadGroup pGroup = new ThreadGroup("seeker1");
        int ThreadSize = storge.size()*2;
        System.out.println("looking for hulls and fighters in mods...");
        getStorgeLock().lock();
        for (String a : storge.keySet()){
            ModStorge b = storge.get(a);
            String mod = a;
            String path = b.path;
            //TODO: ISSUE: I need to get the currect priority of a mod before this happens? wait... no.
            //      I dont need that yet. having priority on this ships does not matter. I will keep it as it is for now.
            float priority = 0;
            new Thread(pGroup, new SeekFighters(mod,path,priority)).run();
            new Thread(pGroup, new SeekHulls(mod,path,priority)).run();
        }
        getStorgeLock().unlock();
        while (pGroup.activeCount() != 0){
            Thread.sleep(100);
            System.out.println("status: "+(ThreadSize-pGroup.activeCount())+ " / "+ThreadSize);
        }
        System.out.println("status: complete. found "+fighterTotal+" fighters, and "+hullTotal+" hulls");
        String log = "mods with fighters: ";
        //System.out.println("mods with fighters: ");
        getStorgeLock().lock();
        for (String a : storge.keySet()) if(storge.get(a).fighters!=null && !storge.get(a).fighters.isEmpty())log+="\n   "+a;
        getStorgeLock().unlock();
        System.out.println(log);
    }
    public static void reorganizeHullsAndFighters() throws InterruptedException {
        System.out.println("timing fighter and hull data so no copys exist...");
        ArrayList<ArrayList<Hull>> hulls = new ArrayList<>();
        ArrayList<ArrayList<Fighter>> fighters = new ArrayList<>();
        int hullSize = 0;
        int fightersSize = 0;
        getStorgeLock().lock();
        for (String a : storge.keySet()){
            ModStorge b = storge.get(a);
            hulls.add(b.hulls);
            fighters.add(b.fighters);

            hullSize+=b.hulls.size();
            fightersSize+=b.fighters.size();
        }
        getStorgeLock().unlock();
        MergeListMaster<Hull> hullController = new MergeListMaster<Hull>(hulls) {
            @Override
            public Runnable getRunnable(ArrayList<Hull> listA, ArrayList<Hull> listB, MergeListMaster<Hull> This) {
                return new OrganizeHulls(listA,listB,This);
            }
        };
        MergeListMaster<Fighter> fighterController = new MergeListMaster<Fighter>(fighters) {
            @Override
            public Runnable getRunnable(ArrayList<Fighter> listA, ArrayList<Fighter> listB, MergeListMaster<Fighter> This) {
                return new OrganizeFighters(listA,listB,This);
            }
        };
        new Thread(hullController).start();
        new Thread(fighterController).start();
        while (!fighterController.isComplete() || !hullController.isComplete()){
            Thread.sleep(1000);
            if (hullController.isComplete()) System.out.println("status: (hulls) completed.");
            else System.out.println("status: (hulls)"+hullController.getStatus());

            if (fighterController.isComplete()) System.out.println("status: (fighters) completed.");
            else System.out.println("status: (fighters)"+fighterController.getStatus());
        }
        finalFighters = fighterController.getFinalLists();
        finalHulls = hullController.getFinalLists();
        System.out.println("status: (hulls) completed. trimmed hulls from "+hullSize+" to "+ finalHulls.size());
        System.out.println("status: (fighters) completed. trimmed fighters from "+fightersSize+" to "+ finalFighters.size());
    }
    public static ThreadGroup findFactionFighters(Thread factionFinder){
        //note: this is moved into the secondary thread.
        /*
        todo:
            1) create 1 Organize_FactionFighters per mod. input the list of fighterIDs from final fighters.
            2) allow the Seeker to continue to run this in the background. but dont allow for the 'applyFightersToFactions' function to run before this is done.
            -) if I input a list, I need to copy it for each thread, to avoid data issues
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
        return null;//this will return the threadGroup created here. this allows for other items to continue well this runs in the background.
    }
    public static void findVariantFiles() throws InterruptedException {
        ThreadGroup pGroup = new ThreadGroup("seeker2");
        System.out.println("attampting to find fighter variant files...");
        getStorgeLock().lock();
        int ThreadSize = storge.size();
        ArrayList<String> fighterIDs = new ArrayList<>();
        for (Fighter a : finalFighters){
            fighterIDs.add(a.fighter_csv.variant);
        }

        MultiGetArray<String> listTemp = new MultiGetArray<String>(fighterIDs,ThreadSize/2);
        for (String a : storge.keySet()) {
            ModStorge b = storge.get(a);
            new Thread(pGroup,new SeekVariants(a,b.path,listTemp)).start();
        }

        getStorgeLock().unlock();
        while (pGroup.activeCount() != 0){
            Thread.sleep(100);
            System.out.println("status (find variant files): "+(ThreadSize-pGroup.activeCount())+ " / "+ThreadSize);
        }
        System.out.println("status (find variant files): complete. found "+foundVariants+" valid variants");
        String log = "mods with fighter variant files:";
        getStorgeLock().lock();
        for (String a : storge.keySet()) if(storge.get(a).variants!=null && !storge.get(a).variants.isEmpty())log+="\n   "+a;
        getStorgeLock().unlock();
        System.out.println(log);
    }
    public static void reorganizeVariants() throws InterruptedException {
        System.out.println("timing fighter variants data so no copys exist...");
        ArrayList<ArrayList<Variant>> lists = new ArrayList<>();
        int listSize = 0;
        getStorgeLock().lock();
        for (String a : storge.keySet()){
            ModStorge b = storge.get(a);
            lists.add(b.variants);

            listSize+=b.variants.size();
        }
        getStorgeLock().unlock();
        MergeListMaster<Variant> controller = new MergeListMaster<Variant>(lists) {
            @Override
            public Runnable getRunnable(ArrayList<Variant> listA, ArrayList<Variant> listB, MergeListMaster<Variant> This) {
                return new OrganizeVariants(listA,listB,This);
            }
        };
        new Thread(controller).start();
        while (!controller.isComplete()){
            Thread.sleep(1000);
            System.out.println("status: "+controller.getStatus());
        }
        finalVariants = controller.getFinalLists();
        System.out.println("status: completed. trimmed variants from "+listSize+" to "+ finalVariants.size());
    }
    public static void findHullFiles(){
        /*
        todo:
            1) create 1 SeekHullsJsons per mod. input the list of hulls created in reorganize00 for the 'seeking hulls'
            2) WAIT on this step until all threads created here have compleated there missions.
            -) if I input a list, I need to copy it for each thread, to avoid data issues
        */
    }
    public static void applyFightersToFactions(ThreadGroup factionFighters){
        //NOTE: this is moved into the secondary thread.
        /*todo:
            !) hold this until factionFighters has completed its operations.
            1) for each faction that has fighters added, create 1 Create_FactionFighters thread.
            2) create 1 Create_RolesJson thread
            3) no need to wait, but don't close the program before this is done.
            -) despite what it looks like, no lists overlap between threads here, do to the way this is set up.

            */
    }
    public static void createFighterSpec(){
        /*
        todo:
            1) create 1 Create_ per fighter.
            2) WAIT on this step until all threads created here have completed there missions
        */
    }

    public static void addModPath(String id,String path){
        getStorgeLock().lock();
        storge.put(id,new ModStorge(path,id));
        getStorgeLock().unlock();
        //System.out.println("adding mod id, path: "+id+", "+path);
    }
    public static void addModStorge(String id,ModStorge newStorge){
        getStorgeLock().lock();
        storge.put(id,newStorge);
        getStorgeLock().unlock();
    }
    public static ModStorge getModStorge(String id){
        getStorgeLock().lock();
        ModStorge out = storge.get(id);
        getStorgeLock().unlock();
        return storge.get(id);
    }
    public static void addFactionStorge(String id,FactionStorge storge){
        getFactionStorgeLock().lock();
        factionStorge.put(id,storge);
        getFactionStorgeLock().unlock();
    }
    public static FactionStorge getFactionStorge(String id){
        getFactionStorgeLock().lock();
        FactionStorge out = factionStorge.get(id);
        getFactionStorgeLock().unlock();
        return out;
    }

    public static synchronized ReentrantLock getStorgeLock(){
        return storgeLock;
    }
    public static synchronized ReentrantLock getFactionStorgeLock(){
        return factionStorgeLock;
    }

    private static int fighterTotal = 0;
    private static int hullTotal = 0;
    public static void addFighters(String id,ArrayList<Fighter> fighters){
        //note: by my calculation, this should be fine without, but I really dont wanna risk it. so here we are.
        try {
            getModStorge(id).lock.lock();
            getModStorge(id).fighters = fighters;
            fighterTotal+=fighters.size();
        }catch (Exception e){
            System.out.println("ERROR in addFighters: "+e);
        }finally {
            getModStorge(id).lock.unlock();
        }
    }
    public static void addHulls(String id,ArrayList<Hull> hulls){
        try {
            getModStorge(id).lock.lock();
            getModStorge(id).hulls = hulls;
            hullTotal+=hulls.size();
        }catch (Exception e){
            System.out.println("ERROR in addHulls: "+e);
        }finally {
            getModStorge(id).lock.unlock();
        }
    }
    private static int foundVariants = 0;
    public static void addVariants(String id, ArrayList<Variant> variants){
        try {
            getModStorge(id).lock.lock();
            getModStorge(id).variants = variants;
            foundVariants += variants.size();
        }catch (Exception e){
            System.out.println("ERROR in addVariants: "+e);
        }finally {
            getModStorge(id).lock.unlock();
        }
    }
    public static void addHullJsons(String id, ArrayList<HullJson> hullJsons){
        try {
            getModStorge(id).lock.lock();
            getModStorge(id).hullJsons = hullJsons;
        }catch (Exception e){
            System.out.println("ERROR in addHullJsons: "+e);
        }finally {
            getModStorge(id).lock.unlock();
        }
    }
    public static void addFactionFighters(String id,ArrayList<String> fighters){
        try {
            getFactionStorge(id).lock.lock();
            getFactionStorge(id).fighters = fighters;
        }catch (Exception e){
            System.out.println("ERROR in addHullJsons: "+e);
        }finally {
            getFactionStorge(id).lock.unlock();
        }

    }
}
