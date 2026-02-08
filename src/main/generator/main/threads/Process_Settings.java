package main.threads;

import main.Seeker;
import main.processers.MergeListMaster;
import main.settings.ShipGroupSettings;
import main.settings.Settings;

import java.util.ArrayList;
import java.util.HashMap;

public class Process_Settings implements Runnable{
    @Override
    public void run() {
        //Settings.lock.lock();
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
        Settings.ready.set(true);

    }
    private void findSetting() throws InterruptedException {
        /*todo: create 1 'SeekBaseSettings'. wait for it to finish, because it is important.*/
        System.out.println("starting to find and process mod settings...");
        ThreadGroup pGroup = new ThreadGroup("Settings_0");
        new Thread(new SeekBaseSettings("starsector",Integer.MAX_VALUE,"./data/config/WinComGenerator_Settings.json")).start();
        Seeker.getStorgeLock().lock();
        int size = Seeker.storge.size();
        for (String a : Seeker.storge.keySet()){
            new Thread(new SeekSettings(a,Seeker.storge.get(a).order,Seeker.storge.get(a).path));
        }
        Seeker.getStorgeLock().unlock();
        while (pGroup.activeCount() != 0){
            System.out.println("status (finding setting files): "+(size-pGroup.activeCount())+" / "+size);
            Thread.sleep(1000);
        }
        String log = "status (finding setting files): complete. \n got mods with manufactuer settings as (copys are common and will be filtered out soon):";
        for (ShipGroupSettings a : Settings.shipSettings_manufacturer.getListWithLock().values()){
            log += "\n  "+a.mod_id;
        }
        Settings.shipSettings_manufacturer.unlock();

        log+=" \n got mods with single ship settings as:";
        for (ShipGroupSettings a : Settings.shipSettings_shipID.getListWithLock().values()) log += "\n  "+a.mod_id;
        Settings.shipSettings_shipID.unlock();

        log+=" \n got allow restricted as:";
        for (String a : Settings.allowRestricted.getListWithLock()) log+="\n  "+a;
        Settings.allowRestricted.unlock();

        log+=" \n got forcefully restricted as:";
        for (String a : Settings.forceExclude.getListWithLock()) log+="\n  "+a;
        Settings.forceExclude.unlock();

        log+=" \n got force allow spawns:";
        for (String a : Settings.forceAllowSpawns.getListWithLock()) log+="\n  "+a;
        Settings.forceAllowSpawns.unlock();

        log+=" \n got force prevent spawns:";
        for (String a : Settings.forcePreventSpawns.getListWithLock()) log+="\n  "+a;
        Settings.forcePreventSpawns.unlock();

        log+=" \n got base hullmods as:";
        for (String a : Settings.permaMods.getListWithLock()) log+="\n  "+a;
        Settings.permaMods.unlock();
        System.out.println(log);
    }
    private void findJsonFiles(){
        //unrequired.
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
        System.out.println("starting to trim base ship files...");
        //this only has one job: merge and override the inputted defalt ship settings.
        OrganizeBaseShipSettings runnable = new OrganizeBaseShipSettings();
        Thread thread = new Thread(runnable);
        thread.start();
        while (thread.isAlive()){
            System.out.println("status: (trimming base ship settings): "+runnable.status());
            Thread.sleep(1000);
        }
        System.out.println("status: (trimming base ship settings): compleat");
        //todo: learn if this even worked???
        //todo: settup the .json file, so I can use it
    }
}
