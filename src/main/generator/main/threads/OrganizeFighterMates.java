package main.threads;

import main.Seeker;
import main.processers.MultiGetArray;
import main.types.*;

import java.util.ArrayList;

public class OrganizeFighterMates implements Runnable{
    MultiGetArray<Hull> hulls;
    MultiGetArray<HullJson> hullJsons;
    MultiGetArray<Variant> variants;
    Fighter fighter;
    public OrganizeFighterMates(Fighter fighter, MultiGetArray<Hull> hulls, MultiGetArray<HullJson> hullJsons, MultiGetArray<Variant> variants){
        this.fighter = fighter;
        this.hulls = hulls;
        this.hullJsons = hullJsons;
        this.variants = variants;
    }
    @Override
    public void run() {
        /*todo:
        *  I need to take the fighter, and match its relevent ids to a variant, hull, and hulljson.
        *  then done.*/
        Variant vPair = null;
        Hull hPair = null;
        HullJson hjPair = null;
        String looking = fighter.fighter_csv.variant;
        int id = variants.getAndReserveListID();
        ArrayList<Variant> list = variants.getList(id);
        for (Variant a : list){
            if(a.json.get("variantId").toString().equals(looking)){
                vPair = a;
                break;
            }
        }
        variants.unlockList(id);
        if (vPair == null){
            System.err.println("failed to get variant of ID "+looking+" for fighter "+fighter.fighter_csv.id);
            return;
        }

        looking = vPair.json.get("hullId").toString();
        id = hulls.getAndReserveListID();
        ArrayList<Hull> list1 = hulls.getList(id);
        for (Hull a : list1){
            if (a.ship_csv.id.equals(looking)){
                hPair = a;
                break;
            }
        }
        hulls.unlockList(id);
        if (hPair == null){
            System.err.println("failed to get hull of ID "+looking+" for fighter "+fighter.fighter_csv.id);
            return;
        }

        id = hullJsons.getAndReserveListID();
        hullJsons.unlockList(id);
        //looking = vPair.json.get("hullId").toString();
        id = hullJsons.getAndReserveListID();
        ArrayList<HullJson> list2 = hullJsons.getList(id);
        for (HullJson a : list2){
            if (a.json.get("hullId").toString().equals(looking)){
                hjPair = a;
                break;
            }
        }
        hullJsons.unlockList(id);
        if (hPair == null){
            System.err.println("failed to get hull (from.ship) of ID "+looking+" for fighter "+fighter.fighter_csv.id);
            return;
        }
        MatedFighters out = new MatedFighters(hPair,hjPair,vPair,fighter);
        Seeker.matedFighters.add(out);
    }
}
