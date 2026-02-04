package main.threads;

import main.processers.MergeListMaster;
import main.settings.ManufacturerSettings;
import main.types.Hull;

import java.util.ArrayList;

public class OrganizeShipSettings implements Runnable{
    ArrayList<ManufacturerSettings> list;
    ArrayList<ManufacturerSettings> list2;
    MergeListMaster<ManufacturerSettings> master;
    public OrganizeShipSettings(ArrayList<ManufacturerSettings> list, ArrayList<ManufacturerSettings> list2, MergeListMaster<ManufacturerSettings> master){
        this.list = list;
        this.list2 = list2;
        this.master = master;
    }
    @Override
    public void run() {
        ArrayList<ManufacturerSettings> out = list;
        boolean build;
        for (ManufacturerSettings a : list2){
            build = true;
            for (ManufacturerSettings b : out){
                if (b.id.equals(a.id)){
                    for (String c : a.hullMods){
                        if (b.hullMods.contains(c)) continue;
                        b.hullMods.add(c);//merge hullmods together.
                    }
                    build = false;
                    break;
                }
            }
            if (build){
                out.add(a);
                continue;
            }
        }
        master.addList(out);
    }
}
