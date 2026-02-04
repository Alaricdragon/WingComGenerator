package main.threads;

import main.settings.ManufacturerSettings;
import main.settings.Settings;

import java.util.ArrayList;

public class TrimBaseShipSettings implements Runnable{
    @Override
    public void run() {
        ManufacturerSettings a = Settings.getBaseShipSettings();
        Settings.getBaseShipSettings_lock().lock();
        for (int b = a.hullMods.size()-1; b >=0; b--){
            for (int c = 0; c < b; c++) if (a.hullMods.get(b).equals(a.hullMods.get(c))) a.hullMods.remove(b);
        }
        Settings.getBaseShipSettings_lock().unlock();
    }
}
