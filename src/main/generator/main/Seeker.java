package main;

import main.beans.Bean_Ship;
import main.processers.MergeListMaster;
import main.processers.MultiGetArray;
import main.processers.CustomJSonReader;
import main.settings.Settings;
import main.threads.*;
import main.types.*;
import org.LockedInteger;
import org.LockedList;
import org.LockedVariable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class Seeker {
    public static LockedVariable<Boolean> hasMatedFighters = new LockedVariable<>(false,false);
    public static LockedVariable<Boolean> finishedCreatingPaths = new LockedVariable<>(false,false);
    public static LockedList<Bean_Ship> shipsToPrintToCSV = new LockedList<>(false);

    private static ReentrantLock storgeLock = new ReentrantLock(false);
    public static HashMap<String, ModStorge> storge = new HashMap<>();

    //private static MultiGetArray mods;
    //private static HashMap<String,String> modPaths = new HashMap<>();
    @Deprecated
    private static ReentrantLock finalFighters_lock = new ReentrantLock(false);
    @Deprecated
    private static ReentrantLock finalHulls_lock = new ReentrantLock(false);
    @Deprecated
    private static ReentrantLock finalVariants_lock = new ReentrantLock(false);
    @Deprecated
    private static ReentrantLock finalHullsJson_lock = new ReentrantLock(false);

    private static LockedList<Fighter> finalFighters = new LockedList<>(false);
    private static LockedList<Hull> finalHulls = new LockedList<>(false);
    private static LockedList<Variant> finalVariants = new LockedList<>(false);
    public static LockedList<HullJson> finalHullJson = new LockedList<>(false);
    public static LockedList<MatedFighters> matedFighters = new LockedList<>(false);


    private static ReentrantLock finishedGettingCoreData_Lock = new ReentrantLock(false);
    private static boolean finishedGettingCoreData = false;

    public static LockedInteger finishedClearingData = new LockedInteger(0,false);//+1 for each type of item cleared. when a certen number, creation can start

    public static void runAllSeekers() throws IOException, ParseException, InterruptedException {
        /*todo: what is left?
        *  1: creating the faction files (aka adding ships into the world)
        *  2: getting all ships that are 'automated' in the modverse so I can stop being scared of getting that wrong
        *  3: getting all ships from armma armada that should NOT be included in this, because there are a few that are already strike craft.
        *  4: make sure this works on larger mod lists
        *  5: make sure the mod 'order'/'priority' is currect, beccause it might not be.*/

        clearOldFiles();
        repairPaths();
        findValidMods();
        findSettings();
        Thread factionFinder = findFactionCSVFiles();
        findCSVFiles();
        reorganizeHullsAndFighters();
        //ThreadGroup factionFighters = findFactionFighters(factionFinder);
        findVariantFiles();
        reorganizeVariants();
        reorganizeValidHulls2();
        findHullFiles();
        reorganizeHullJsons();
        mateFightersToHullsAndVariants();
        //applyFightersToFactions(factionFighters);
        createFighterSpec();
        createHullCSV();
    }
    public static void clearOldFiles(){
        //I should multithread this, do to the complexity.
        new Thread(new Delete_Hulls()).start();
        new Thread(new Delete_Variants()).start();
        new Thread(new Delete_Roles()).start();
    }
    public static void repairPaths(){
        new Thread(new Create_Paths()).start();
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
    public static void findSettings(){
        new Thread(new Process_Settings()).start();
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
            float priority = 0;
            new Thread(pGroup, new SeekFighters(mod,path,priority)).run();
            new Thread(pGroup, new SeekHulls(mod,path,priority)).run();
        }
        getStorgeLock().unlock();
        while (pGroup.activeCount() != 0){
            System.out.println("status: "+(ThreadSize-pGroup.activeCount())+ " / "+ThreadSize);
            Thread.sleep(1000);
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
        while (!Settings.ready.get()){
            Thread.sleep(100);
        }
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
        MultiGetArray<String> excludeList = new MultiGetArray<>(Settings.forceExclude.getListWithLock(),(fighters.size() / 2) + 1);
        MultiGetArray<String> allowRestricted = new MultiGetArray<>(Settings.allowRestricted.getListWithLock(),(fighters.size() / 2) + 1);

        MergeListMaster<Hull> hullController = new MergeListMaster<Hull>(hulls) {
            @Override
            public Runnable getRunnable(ArrayList<Hull> listA, ArrayList<Hull> listB, MergeListMaster<Hull> This) {
                return new OrganizeHulls(listA,listB,This);
            }
        };
        Settings.forceExclude.unlock();
        Settings.allowRestricted.unlock();

        //Settings.getForceExclude_lock().lock();
        //Settings.getAllowRestricted_lock().lock();

        //Settings.getForceExclude_lock().unlock();
        //Settings.getAllowRestricted_lock().unlock();
        
        MergeListMaster<Fighter> fighterController = new MergeListMaster<Fighter>(fighters) {
            @Override
            public Runnable getRunnable(ArrayList<Fighter> listA, ArrayList<Fighter> listB, MergeListMaster<Fighter> This) {
                return new OrganizeFighters(listA,listB,This,excludeList,allowRestricted);
            }
        };
        new Thread(hullController).start();
        new Thread(fighterController).start();
        while (!fighterController.isComplete() || !hullController.isComplete()){
            if (hullController.isComplete()) System.out.println("status: (hulls) completed.");
            else System.out.println("status: (hulls)"+hullController.getStatus());

            if (fighterController.isComplete()) System.out.println("status: (fighters) completed.");
            else System.out.println("status: (fighters)"+fighterController.getStatus());
            Thread.sleep(1000);
        }
        finalFighters.set(fighterController.getFinalLists());
        finalHulls.set(hullController.getFinalLists());
        System.out.println("status: (hulls) completed. trimmed hulls from "+hullSize+" to "+ finalHulls.size());
        System.out.println("status: (fighters) completed. trimmed fighters from "+fightersSize+" to "+ finalFighters.size());
    }
    public static void findVariantFiles() throws InterruptedException {
        ThreadGroup pGroup = new ThreadGroup("seeker2");
        System.out.println("attampting to find fighter variant files...");
        getStorgeLock().lock();
        int ThreadSize = storge.size();
        ArrayList<String> fighterIDs = new ArrayList<>();
        ArrayList<Fighter> temp = finalFighters.getListWithLock();
        for (Fighter a : temp){
            fighterIDs.add(a.fighter_csv.variant);
        }
        finalFighters.unlock();

        MultiGetArray<String> listTemp = new MultiGetArray<String>(fighterIDs,ThreadSize/2);
        for (String a : storge.keySet()) {
            ModStorge b = storge.get(a);
            new Thread(pGroup,new SeekVariants(a,b.path,listTemp)).start();
        }

        getStorgeLock().unlock();
        while (pGroup.activeCount() != 0){
            System.out.println("status (find variant files): "+(ThreadSize-pGroup.activeCount())+ " / "+ThreadSize);
            Thread.sleep(1000);
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
            System.out.println("status: "+controller.getStatus());
            Thread.sleep(1000);
        }
        
        getFinalVariants_lock().lock();
        finalVariants.set(controller.getFinalLists());
        System.out.println("status: completed. trimmed variants from "+listSize+" to "+ finalVariants.size());
        getFinalVariants_lock().unlock();
    }
    public static void reorganizeValidHulls2() throws InterruptedException {
        System.out.println("timing hulls from csv so only ones required by fighters exist...");
        getFinalHulls_lock().lock();
        ArrayList<Hull> hulls = finalHulls.getListWithLock();
        int totalHulls = hulls.size();
        finalHulls.set(new ArrayList<>());
        finalHulls.unlock();
        getFinalHulls_lock().unlock();
        ArrayList<ArrayList<Hull>> splitHulls = new ArrayList<>();
        ArrayList<Hull> target = new ArrayList<>();
        splitHulls.add(target);
        while (!hulls.isEmpty()){
            target.add(hulls.getFirst());
            if (target.size() >= 20){
                target = new ArrayList<>();
                splitHulls.add(target);
            }
            hulls.removeFirst();
        }
        getFinalVariants_lock().lock();
        ArrayList<String> variantHullList = new ArrayList<>();
        ArrayList<Variant> temp = finalVariants.getListWithLock();
        for (Variant a : temp) if (a.json.containsKey("hullId")) variantHullList.add(a.json.get("hullId").toString());
        finalVariants.unlock();
        getFinalVariants_lock().unlock();
        MultiGetArray<String> getArray = new MultiGetArray<>(variantHullList,(splitHulls.size() / 3) + 1);
        ThreadGroup pGroup = new ThreadGroup("seeker3");
        for (ArrayList<Hull> a : splitHulls) new Thread(pGroup,new TrimHullsToFighters(a,getArray)).start();
        while (pGroup.activeCount() != 0){
            System.out.println("status (final hull.csv organization): "+(totalHulls-(pGroup.activeCount()*20))+ " / "+totalHulls);
            Thread.sleep(1000);
        }
        getFinalHulls_lock().lock();
        String log = "status (final hull.csv organization): completed. trimmed hulls from "+totalHulls+" to "+ finalHulls.size();
        log += "\n got valid hulls as:";
        ArrayList<Hull> temp2 = finalHulls.getListWithLock();
        for (Hull a : temp2) log += "\n  "+a.ship_csv.id;
        finalHulls.unlock();
        getFinalHulls_lock().unlock();
        System.out.println(log);
        setFinishedGettingCoreData(true);
    }
    public static void findHullFiles() throws InterruptedException {
        System.out.println("finding the relevant hull files from all available hull files");
        ArrayList<String> list = new ArrayList<>();
        getStorgeLock().lock();
        int size = storge.size();
        getStorgeLock().unlock();
        getFinalVariants_lock().lock();
        ArrayList<Variant> temp = finalVariants.getListWithLock();
        for (Variant a : temp){
            if (!a.json.containsKey("hullId")) continue;
            String c = a.json.get("hullId").toString();
            list.add(c);
        }
        finalVariants.unlock();
        getFinalVariants_lock().unlock();
        MultiGetArray<String> get = new MultiGetArray<>(list,(size / 2) + 1);
        ThreadGroup pGroup = new ThreadGroup("seeker4");
        getStorgeLock().lock();
        for (String a : storge.keySet()){
            ModStorge b = storge.get(a);
            new Thread(pGroup,new SeekHullsJsons(a,b.path,get)).start();
        }
        getStorgeLock().unlock();
        while (pGroup.activeCount() != 0){
            System.out.println("status (find .ship files): "+(size-pGroup.activeCount())+" / "+size);
            Thread.sleep(1000);
        }
        String log = "status (find .ship files): completed \n the following mods hold valid .ship files:";
        storgeLock.lock();
        for (String a : storge.keySet()){
            ModStorge b = storge.get(a);
            if (b.hullJsons.isEmpty()) continue;
            log += "\n  "+b.id+" ("+b.hullJsons.size()+")";
        }
        storgeLock.unlock();
        System.out.println(log);
    }
    public static void reorganizeHullJsons() throws InterruptedException {
        System.out.println("timing .ship data so no copys exist ");
        getStorgeLock().lock();
        ArrayList<ArrayList<HullJson>> list = new ArrayList<>();
        int listSize = 0;
        for (String a : storge.keySet()){
            list.add(storge.get(a).hullJsons);
            listSize+=storge.get(a).hullJsons.size();
        }
        getStorgeLock().unlock();
        MergeListMaster contoler = new MergeListMaster<HullJson>(list) {
            @Override
            public Runnable getRunnable(ArrayList<HullJson> listA, ArrayList<HullJson> listB, MergeListMaster<HullJson> This) {
                return new OrganizeHullJsons(listA,listB,This);
            }
        };
        new Thread(contoler).start();
        while (!contoler.isComplete()){
            System.out.println("status (triming .ships): "+contoler.getStatus());
            Thread.sleep(1000);
        }
        finalHullJson.set(contoler.getFinalLists());
        System.out.println("status (triming .ships): completed. trimed "+listSize+" .ships down to "+finalHullJson.size());
    }
    public static void mateFightersToHullsAndVariants() throws InterruptedException {
        System.out.println("started to pair hull, ship, and variant data...");
        ArrayList<Hull> hulls = finalHulls.getListWithLock();
        ArrayList<Variant> variants = finalVariants.getListWithLock();
        ArrayList<HullJson> hullJsons = finalHullJson.getListWithLock();
        finalHulls.unlock();
        finalVariants.unlock();
        finalHullJson.unlock();
        int hSize = hulls.size();
        int vSize = variants.size();
        int hjSize = hullJsons.size();
        int fSize = finalFighters.size();
        MultiGetArray<Hull> hArray = new MultiGetArray<Hull>(hulls,(fSize / 3) + 1);
        MultiGetArray<Variant> vArray = new MultiGetArray<Variant>(variants,(fSize / 3) + 1);
        MultiGetArray<HullJson> hjArray = new MultiGetArray<HullJson>(hullJsons,(fSize / 3) + 1);
        ThreadGroup pGroup = new ThreadGroup("seeker5");
        ArrayList<Fighter> temp = finalFighters.getListWithLock();
        System.out.println("status (pairing fighter, hull, ship, and variant data...): "+" got Fighters, Hulls, Variants, and Ships to pair: "+fSize+", "+hSize+", "+vSize+", "+hjSize);
        for (Fighter a : temp){
            new Thread(pGroup,new OrganizeFighterMates(a,hArray,hjArray,vArray)).start();
        }
        finalFighters.unlock();
        while (pGroup.activeCount() != 0){
            System.out.println("status (pairing fighter, hull, ship, and variant data...): "+(fSize - pGroup.activeCount())+" / "+fSize);
            Thread.sleep(1000);
        }
        String log = "status (pairing fighter, hull, ship, and variant data...): complete. got "+matedFighters.size()+" pairs";
        log += "\n the final paired data is as follows:";
        ArrayList<MatedFighters> list = matedFighters.getListWithLock();
        for (MatedFighters a : list){
            log += "\n  fighter, hull, ship, variant: "+a.fighter.fighter_csv.id+", "+a.hull.ship_csv.id+", "+a.hullJson.json.get("hullId").toString()+", "+a.variant.json.get("variantId").toString();
        }
        matedFighters.unlock();
        hasMatedFighters.set(true);
        System.out.println(log);
    }
    public static void createFighterSpec() throws InterruptedException {
        //if (true) return;//dont want to deal with this yet.
        while (!finishedCreatingPaths.get()){
            Thread.sleep(1000);//continue this untill ready.
        }
        System.out.println("started the process of creating fighter .variant, and .ship files...");
        ThreadGroup pGroup = new ThreadGroup("seeker6");
        int size = matedFighters.size();
        for (MatedFighters a : matedFighters.getListWithLock()){
            new Thread(pGroup,new Create_shipData(a)).start();
        }
        matedFighters.unlock();
        while (pGroup.activeCount() != 0){
            System.out.println("status (create fighter .variant, and .ship):"+(size-pGroup.activeCount())+" / "+size);
            Thread.sleep(1000);
        }
        System.out.println("status (create fighter .variant, and .ship): compleat. hopefully that worked without to many errors");
    }
    public static void createHullCSV() throws InterruptedException {
        System.out.println("started the process of creating the hulls.csv file...");
        Thread a = new Thread(new Create_HullCSV());
        a.start();
        while (a.isAlive()){
            System.out.println("status (create hull.csv file): 0 / 1");
            Thread.sleep(1000);
        }
        System.out.println("status (create hull.csv file): completed.");
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


    public static synchronized ReentrantLock getStorgeLock(){
        return storgeLock;
    }
    public static synchronized ReentrantLock getFinalFighters_lock(){
        return finalFighters_lock;
    }
    public static synchronized ReentrantLock getFinalHulls_lock(){
        return finalHulls_lock;
    }
    public static synchronized ReentrantLock getFinalVariants_lock(){
        return finalVariants_lock;
    }
    public static synchronized ReentrantLock getFinalHullsJson_lock(){
        return finalHullsJson_lock;
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


    public static boolean finishedGettingCoreData(){
        finishedGettingCoreData_Lock.lock();
        boolean out = finishedGettingCoreData;
        finishedGettingCoreData_Lock.unlock();
        return out;
    }

    public static void setFinishedGettingCoreData(boolean setFinishedGettingCoreData){
        finishedGettingCoreData_Lock.lock();
        finishedGettingCoreData = setFinishedGettingCoreData;
        finishedGettingCoreData_Lock.unlock();
    }

    public static void addFinalHull(Hull hull){
        getFinalHulls_lock().lock();
        finalHulls.add(hull);
        getFinalHulls_lock().unlock();
    }
}
