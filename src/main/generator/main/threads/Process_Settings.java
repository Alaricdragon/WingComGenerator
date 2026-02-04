package main.threads;

import main.Seeker;
import main.processers.MergeListMaster;
import main.settings.ManufacturerSettings;
import main.settings.Settings;

import java.util.ArrayList;
import java.util.Set;

public class Process_Settings implements Runnable{
    @Override
    public void run() {
        Settings.lock.lock();
        //is locked in the function that starts this loop.
        try {
            findSetting();
            findJsonFiles();
            processJsonFiles();
        } catch (InterruptedException e) {
            System.out.println("CRITICAL ERROR: FAILED TO GET SETTING FILES.");
            System.out.println(e);
            throw new RuntimeException(e);

        }
        Settings.lock.unlock();

    }
    private void findSetting() throws InterruptedException {
        /*todo: create 1 'SeekBaseSettings'. wait for it to finish, because it is important.*/
        System.out.println("starting to find and process mod settings...");
        ThreadGroup pGroup = new ThreadGroup("Settings_0");
        new Thread(new SeekBaseSettings("starsector","./data/config/WinComGenerator_Settings.json")).start();
        Seeker.getStorgeLock().lock();
        int size = Seeker.storge.size();
        for (String a : Seeker.storge.keySet()){
            new Thread(new SeekSettings(a,Seeker.storge.get(a).path));
        }
        Seeker.getStorgeLock().unlock();
        while (pGroup.activeCount() != 0){
            Thread.sleep(1000);
            System.out.println("status (finding setting files): "+(size-pGroup.activeCount())+" / "+size);
        }
        String log = "status (finding setting files): complete. \n got mods with settings as (copys are common and will be filtered out soon):";
        for (ManufacturerSettings a : Settings.getShipSettings()){
            log += "\n  "+a.id;
        }
        log+=" \n got allow restricted as:";
        Settings.getAllowRestricted_lock().lock();
        for (String a : Settings.getAllowRestricted()) log+="\n  "+a;
        Settings.getAllowRestricted_lock().unlock();

        log+=" \n got forcefully restricted as:";
        Settings.getForceExclude_lock().lock();
        for (String a : Settings.getForceExclude()) log+="\n  "+a;
        Settings.getForceExclude_lock().unlock();

        log+=" \n got base hullmods as:";
        Settings.getBaseShipSettings_lock().lock();
        for (String a : Settings.getBaseShipSettings().hullMods) log+="\n  "+a;
        Settings.getBaseShipSettings_lock().unlock();
        System.out.println(log);
    }
    private void findJsonFiles(){
    }
    private void processJsonFiles() throws InterruptedException {
        /*todo:
            1) create 4 MergeListMasters for the following:
                -baseShipSettings (remove dop hullmods (for same factions))
                -shipSettings (remove dop hullmods (for same factions))
                -allowRestricted (merge strings)
                -forceExclude (merge strings)
            2) wait for this to compleat. once it is, unlock the settings.
        */
        System.out.println("starting to trim gathered settings files...");
        ThreadGroup pGroup = new ThreadGroup("Settings_1");
        Settings.getAllowRestricted_lock().lock();
        Settings.getShipSettings_lock().lock();
        Settings.getAllowRestricted_lock().lock();
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        ArrayList<String> temp = new ArrayList<>();
        list.add(temp);
        for (String a : Settings.getAllowRestricted()){
            temp.add(a);
            if (temp.size() >= 1){
                temp = new ArrayList<>();
                list.add(temp);
            }
        }
        MergeListMaster<String> merge0 = new MergeListMaster<String>(list) {
            @Override
            public Runnable getRunnable(ArrayList<String> listA, ArrayList<String> listB, MergeListMaster<String> This) {
                return new OrganizeAllowRestricted(listA,listB,This);
            }
        };
        new Thread(pGroup,merge0).start();

        list = new ArrayList<>();
        temp = new ArrayList<>();
        list.add(temp);
        for (String a : Settings.getForceExclude()){
            temp.add(a);
            if (temp.size() >= 1){
                temp = new ArrayList<>();
                list.add(temp);
            }
        }
        MergeListMaster<String> merge1 = new MergeListMaster<String>(list) {
            @Override
            public Runnable getRunnable(ArrayList<String> listA, ArrayList<String> listB, MergeListMaster<String> This) {
                return new OrganizeForceExclude(listA,listB,This);
            }
        };
        new Thread(pGroup,merge1).start();

        ArrayList<ArrayList<ManufacturerSettings>> list2 = new ArrayList<>();
        ArrayList<ManufacturerSettings> temp2 = new ArrayList<>();
        list2.add(temp2);
        for (ManufacturerSettings a : Settings.getShipSettings()){
            temp2.add(a);
            if (temp2.size() >= 1){
                temp2 = new ArrayList<>();
                list2.add(temp2);
            }
        }
        MergeListMaster<ManufacturerSettings> merge2 = new MergeListMaster<ManufacturerSettings>(list2) {
            @Override
            public Runnable getRunnable(ArrayList<ManufacturerSettings> listA, ArrayList<ManufacturerSettings> listB, MergeListMaster<ManufacturerSettings> This) {
                return new OrganizeShipSettings(listA,listB,This);
            }
        };
        new Thread(pGroup,merge2).start();

        new Thread(pGroup,new TrimBaseShipSettings()).start();

        while (!merge0.isComplete() || !merge1.isComplete() || !merge2.isComplete() || pGroup.activeCount() != 0){
            Thread.sleep(1000);
            System.out.println("status (trim settings): "+pGroup.activeCount() + "/ 4");
        }
        Settings.getAllowRestricted_lock().unlock();
        Settings.getShipSettings_lock().unlock();
        Settings.getAllowRestricted_lock().unlock();
        //note: setting the data when the lock is unset for a moment might be bad practice. this is the type of time one would set some more complecated locks just in case
        Settings.setAllowedRestricted(merge0.getFinalLists());
        Settings.setForceExclude(merge1.getFinalLists());
        Settings.setShipSettings(merge2.getFinalLists());

        String log = "status (trim settings): completed";

        log+="\n got mods with settings as:";
        Settings.getShipSettings_lock().lock();
        for (ManufacturerSettings a : Settings.getShipSettings()){
            log += "\n  "+a.id;
        }
        Settings.getShipSettings_lock().unlock();

        log+=" \n got allow restricted as:";
        Settings.getAllowRestricted_lock().lock();
        for (String a : Settings.getAllowRestricted()) log+="\n  "+a;
        Settings.getAllowRestricted_lock().unlock();

        log+=" \n got forcefully restricted as:";
        Settings.getForceExclude_lock().lock();
        for (String a : Settings.getForceExclude()) log+="\n  "+a;
        Settings.getForceExclude_lock().unlock();

        log+=" \n got base hullmods as:";
        Settings.getBaseShipSettings_lock().lock();
        for (String a : Settings.getBaseShipSettings().hullMods) log+="\n  "+a;
        Settings.getBaseShipSettings_lock().unlock();
        System.out.println(log);
        //Seeker.getStorgeLock().lock();
        //Seeker.getStorgeLock().unlock();


    }
}
