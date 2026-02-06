package main.threads;

import main.settings.HullSettings;
import main.settings.Settings;
import main.settings.ShipGroupSettings;
import main.settings.VariantSettings;
import org.LockedHashMap;
import org.LockedInteger;
import org.LockedVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class OrganizeBaseShipSettings implements Runnable{
    LockedVariable<Integer> maxItems = new LockedVariable<>(0,false);
    LockedInteger doneItems = new LockedInteger(0,false);
    public String status(){
        return doneItems.get() + " / "+maxItems.get();
    }
    @Override
    public void run() {
        ArrayList<ShipGroupSettings> settings = Settings.unsortedBaseShipSettings.getListWithLock();
        maxItems.set(settings.size());
        ShipGroupSettings item = Settings.baseShipSettings.getItemWithLock();

        //so in theory this is simple.
        //for each item insdie that is a higher piroirity, replace the base setting item with it.
        for (ShipGroupSettings a : settings){
            checkVariant(a,item);
            checkHull(a,item);
            doneItems.change(1);
        }

        Settings.unsortedBaseShipSettings.set(new ArrayList<>());
        Settings.unsortedBaseShipSettings.unlock();
        Settings.baseShipSettings.unlock();
    }
    private void checkVariant(ShipGroupSettings a,ShipGroupSettings item){
        HashMap<String, LockedHashMap<String, VariantSettings>> b = a.variantSettings.getListWithLock();
        for (String key : b.keySet()){
            if (!item.variantSettings.containsKey(key)){
                item.variantSettings.put(key,b.get(key));
                continue;
            }
            HashMap<String, VariantSettings> c = b.get(key).getListWithLock();
            for (String key2 : c.keySet()){
                if (!item.variantSettings.get(key).containsKey(key2)){
                    item.variantSettings.get(key).put(key2,c.get(key2));
                    continue;
                }
                if (item.variantSettings.get(key).get(key2).variant.priority < c.get(key2).variant.priority){
                    item.variantSettings.get(key).put(key2,c.get(key2));
                }
            }
            b.get(key).unlock();
        }
        a.variantSettings.unlock();
    }
    private void checkHull(ShipGroupSettings a,ShipGroupSettings item){
        HashMap<String, LockedHashMap<String, HullSettings>> b = a.hullSettings.getListWithLock();
        for (String key : b.keySet()){
            if (!item.hullSettings.containsKey(key)){
                item.hullSettings.put(key,b.get(key));
                continue;
            }
            HashMap<String, HullSettings> c = b.get(key).getListWithLock();
            for (String key2 : c.keySet()){
                if (!item.hullSettings.get(key).containsKey(key2)){
                    item.hullSettings.get(key).put(key2,c.get(key2));
                    continue;
                }
                if (item.hullSettings.get(key).get(key2).priority < c.get(key2).priority){
                    item.hullSettings.get(key).put(key2,c.get(key2));
                }
            }
            b.get(key).unlock();
        }
        a.hullSettings.unlock();
    }
}
