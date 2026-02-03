package main.threads;

import main.Seeker;

public class Process_Settings implements Runnable{
    @Override
    public void run() {
        findBaseSettingFile();
        findJsonFiles();
        processJsonFiles();

    }
    private void findBaseSettingFile(){
        /*todo: create 1 'SeekBaseSettings'. wait for it to finish, because it is important.*/
    }
    private void findJsonFiles(){
        /*todo:
            1)
            2) create 1 'SeekSettings' per mod.
            3) wait for this to compleat.
         */
        ThreadGroup pGroup = new ThreadGroup("Settings0");
        Seeker.getStorgeLock().lock();


    }
    private void processJsonFiles(){
        /*todo:
            1) create 4 MergeListMasters for the following:
                -baseShipSettings (remove dop hullmods (for same factions))
                -shipSettings (remove dop hullmods (for same factions))
                -allowRestricted (merge strings)
                -forceExclude (merge strings)
            2) wait for this to compleat. once it is, unlock the settings.
        */
    }
}
