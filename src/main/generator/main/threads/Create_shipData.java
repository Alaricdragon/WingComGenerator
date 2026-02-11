package main.threads;

import main.Seeker;
import main.beans.Bean_Fighter;
import main.beans.Bean_Ship;
import main.processers.CustomJSonReader;
import main.settings.*;
import main.types.HullJson;
import main.types.MatedFighters;
import main.types.ShipRoles;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class Create_shipData implements Runnable{
    private MatedFighters matedFighter;
    public Create_shipData(MatedFighters matedFighter){
        this.matedFighter = matedFighter;
    }
    @Override
    public void run() {

        //this gets the final ship settigns for a single ship. its magical.
        ShipGroupSettings c = Settings.shipSettings_shipID.get(matedFighter.fighter.fighter_csv.id);
        ShipGroupSettings b = Settings.shipSettings_manufacturer.get(matedFighter.hull.ship_csv.tech_manufacturer);
        ShipGroupSettings a = Settings.baseShipSettings.getItemWithLock();
        Settings.shipSettings_shipID.getListWithLock();
        Settings.shipSettings_manufacturer.getListWithLock();
        ChosenShipSettings settings = new ChosenShipSettings(matedFighter,a,b,c);
        Settings.baseShipSettings.unlock();
        Settings.shipSettings_shipID.unlock();
        Settings.shipSettings_manufacturer.unlock();
        //String an = a == null ? "_" : a.mod_id.get();
        //String bn = b == null ? "_" : b.mod_id.get();
        //String cn = c == null ? "_" : c.mod_id.get();
        //System.out.println(matedFighter.fighter.fighter_csv.id+" got items of a,b,c: "+an+", "+bn+", "+cn);
        try {
            //System.out.println("id: "+matedFighter.fighter.fighter_csv.id+" 0.0");
            addShipFile(settings);
            //System.out.println("id: "+matedFighter.fighter.fighter_csv.id+" 1.0");
            addVariantFile(settings);
            //System.out.println("id: "+matedFighter.fighter.fighter_csv.id+" 2.0");
            addShipCSV(settings);
            //System.out.println("id: "+matedFighter.fighter.fighter_csv.id+" 3.0");
            addShipRoles(settings);
        }catch (Exception e){
            System.err.println("failed to create a ship of ID: "+matedFighter.fighter.fighter_csv.id+" because of exseption \n"+e);
        }

    }
    private void addShipFile(ChosenShipSettings settings) throws ParseException, IOException {
        /*
   .ship file:
        keep exsactly the same as ships BUT:
            1) merge together with the selected Hull.json data from the HullSettings
            2) add 'builtInMods' jsonArray with the apropreate objects
            3) if available, remove 'builtInMods' -> 'no_weapon_flux'
            4) adding a 'builtInWeapons': {.variant weapons ID: .variantWeapon'}
                (gotten from .variant -> 'weaponGroups' jsonArrray -> each object -> weapons jsonArray ->each item)
            5) from the weapons gotten in 4), change the
                .ship -> 'weaponSlots'(jsonArray) -> eachItem
                    -> type -> 'BUILT_IN'
            6) for 'builtInWings' jsonArray add 1 'Fighter.id' per 'base bays'
            -
         */
        //JSONObject out = new JSONObject();
        HullJson j = settings.hullSettings.json.getItemWithLock();
        JSONObject out = CustomJSonReader.getObject(matedFighter.hullJson.json,false);
        settings.hullSettings.json.unlock();//please note: this is settings, NOT Settings. this is the chosen ship settings all combined all nice like.


        JSONArray array = out.containsKey("builtInMods") ? (JSONArray) out.get("builtInMods") : new JSONArray();
        for (String b : Settings.permaMods.getListWithLock()) array.add(b);
        Settings.permaMods.unlock();
        if (isWingCommander(settings)){
            for (String b : Settings.permaMods_ifBaseFighter.getListWithLock()) array.add(b);
            Settings.permaMods_ifBaseFighter.unlock();
        }
        out.put("builtInMods",array);
        if (addAuto()) array.add("automated");
        for (String b : settings.hullSettings.perma_hullmods) array.add(b);
        for (String b : Settings.swapHullMods.getListWithLock().keySet()){
            if (array.contains(b)){
                array.remove(b);
                String c = Settings.swapHullMods.get(b);
                if (!c.isBlank())array.add(c);
            }
        }
        Settings.swapHullMods.unlock();
        //if (array.contains("no_weapon_flux")) array.remove("no_weapon_flux");
        out.put("hullSize","FRIGATE");
        out.put("hullId",getHullID(settings));
        out.put("hullName",getName(settings));

        //todo: I am on step 4 now.
        //copying was sucsesfull. therefor....
        HashMap<String,String> v_weaponData = new HashMap<>();
        //data is: 1, mountID, 2, is weaponID.

        JSONObject variantJson = CustomJSonReader.getObject(matedFighter.variant.json,false);
        if (variantJson.containsKey("weaponGroups")){
            array = (JSONArray) variantJson.get("weaponGroups");
            for (Object b : array){
                JSONObject weaponJson = (JSONObject) b;
                if (weaponJson.containsKey("weapons")){
                    JSONObject weapon = (JSONObject) weaponJson.get("weapons");
                    for (Object c : weapon.keySet()){
                        v_weaponData.put(c.toString(),weapon.get(c).toString());
                    }
                }
            }
        }
        //gotten weapon data from relevant items....
        //...
        JSONObject object = out.containsKey("builtInWeapons") ? (JSONObject) out.get("builtInWeapons") : new JSONObject() ;
        for (String a : v_weaponData.keySet()){
            object.put(a,v_weaponData.get(a));
            //System.out.println("adding wepon of slot, id: "+a+", "+v_weaponData.get(a));
        }
        out.put("builtInWeapons",object);
        array = out.containsKey("builtInWings") ? (JSONArray) out.get("builtInWings") : new JSONArray() ;
        for (int a = 0; a < settings.hullSettings.baseFighters; a++) array.add(matedFighter.fighter.fighter_csv.id);
        out.put("builtInWings",array);

        //reorganize 'out' weapons into being 1: built in, and 2: holding the relevent wepon inside of iteself.
        if (out.containsKey("weaponSlots")) {
            array = (JSONArray) out.get("weaponSlots");
            for (Object b : array) {
                JSONObject c = (JSONObject) b;
                if (c.get("type").toString().equals("DECORATIVE")) {
                    //... do nothing
                } else {
                    c.put("type", "BUILT_IN");
                }
            }
        }

        //finally, putting in the json data into this mess...
        HullJson hullJson = settings.hullSettings.json.getItemWithLock();//this is from hullSettings.
        if (hullJson.json.containsKey("builtInMods")){
            array = (JSONArray) hullJson.json.get("builtInMods");
            JSONArray array1 = (JSONArray) out.get("builtInMods");
            for (Object a : array){
                array1.add(a.toString());
            }
        }
        if (hullJson.json.containsKey("builtInWings")){
            array = (JSONArray) hullJson.json.get("builtInWings");
            JSONArray array1 = (JSONArray) out.get("builtInWings");
            for (Object a : array){
                array1.add(a.toString());
            }
        }
        //System.out.println("got json as: "+ out.toJSONString());
        if (hullJson.json.containsKey("weaponSlots")){
            array = (JSONArray) hullJson.json.get("weaponSlots");
            JSONArray array1 = out.containsKey("weaponSlots") ? (JSONArray) out.get("weaponSlots") : new JSONArray();
            for (Object a : array){
                array1.add(a);
            }
            out.put("weaponSlots",array1);
        }
        settings.hullSettings.json.unlock();
        //System.out.println("got json done as: "+ out);

        String key = "./data/hulls/"+getHullID(settings)+".ship";
        CustomJSonReader.writeJsonFile(key,out);
    }
    private boolean addAuto(){
        String id = matedFighter.fighter.fighter_csv.id;
        String manufactuer = matedFighter.hull.ship_csv.tech_manufacturer;
        if (Settings.shipsForceNotAutomated.contains(id)) return false;
        if (Settings.shipsForceAutomated.contains(id)) return true;
        if (Settings.manufacturersForceAutomated.contains(manufactuer)) return true;
        if (Settings.automateDroneFighters.get() && !matedFighter.fighter.fighter_csv.tags.isBlank() && matedFighter.fighter.fighter_csv.tags.contains("auto_fighter")) return true;
        return false;
    }
    private boolean addFreeAutoTag(){
        return addAuto() && Settings.autoShips_DontCostAutoPoints.get();
    }
    private boolean isWingCommander(ChosenShipSettings settings){
        return settings.hullSettings.baseFighters > 0;
    }
    private void addVariantFile(ChosenShipSettings settings) throws ParseException, IOException {
        /*
        data needed:
            hullId
            variantId
            for everything else, its just a copy of the chosen ship settings variant.json

         */
        JSONObject variantJson = CustomJSonReader.getObject(settings.variantSettings.variant.json,false);
        variantJson.put("hullId",getHullID(settings));
        variantJson.put("variantId",getVariantId(settings));
        String key = "./data/variants/"+getVariantId(settings)+".variant";
        CustomJSonReader.writeJsonFile(key,variantJson);
    }
    private void addShipCSV(ChosenShipSettings settings){
        HullSettings hSettings= settings.hullSettings;
        Bean_Ship m_s_bean = matedFighter.hull.ship_csv;
        Bean_Ship shipCSV = new Bean_Ship(m_s_bean);
        Bean_Fighter m_f_bean = matedFighter.fighter.fighter_csv;
        shipCSV.name = getName(settings);//matedFighter.hull.ship_csv.name+" (WINGCOM)";//NOTE: this needs to be set in addShipFile as well.
        shipCSV.id = getHullID(settings);//"WinComGenerator"+matedFighter.hull.ship_csv.id;//NOTE: this needs to be set in addShipFile, and addVariantFile.
        shipCSV.max_speed = getModifiedValue_S_Int(m_s_bean.max_speed,hSettings.speedM,hSettings.speedF);
        shipCSV.fleet_pts = getModifiedValue_S_Int(m_f_bean.op_cost,hSettings.dpM,hSettings.dpF);
        shipCSV.CR_to_deploy = String.valueOf(hSettings.crToDeploy);//getModifiedValue_S_Int(m_f_bean.op_cost,hSettings.crToDeployM,hSettings.crToDeployF);
        shipCSV.hitpoints = getModifiedValue_S_Int(m_s_bean.hitpoints,hSettings.hullM,hSettings.hullF);
        shipCSV.armor_rating = getModifiedValue_S_Int(m_s_bean.armor_rating,hSettings.armorM,hSettings.armorF);
        shipCSV.max_flux = getModifiedValue_S_Int(m_s_bean.max_flux,hSettings.capM,hSettings.capF);
        shipCSV.flux_dissipation = getModifiedValue_S_Int(m_s_bean.flux_dissipation,hSettings.ventM,hSettings.ventF);
        shipCSV.base_value = getModifiedValue_S_Int(m_f_bean.base_value,hSettings.valueM,hSettings.valueF);
        shipCSV.supplies_rec = getModifiedValue_S_Int(m_f_bean.op_cost,hSettings.suppliesRecoverM,hSettings.suppliesRecoverF);
        shipCSV.supplies_mo = getModifiedValue_S_Int(m_f_bean.op_cost,hSettings.suppliesMonthM,hSettings.suppliesMonthF);
        shipCSV.acceleration = getModifiedValue_S_Int(m_s_bean.acceleration,hSettings.accelerationM,hSettings.accelerationF);
        shipCSV.deceleration = getModifiedValue_S_Int(m_s_bean.deceleration,hSettings.decelerationM,hSettings.decelerationF);
        //done the not static values. the rest are completely static.
        shipCSV.ordnance_points = String.valueOf(hSettings.op);
        shipCSV.max_crew = String.valueOf(hSettings.crew);
        shipCSV.cargo = String.valueOf(hSettings.cargo);
        shipCSV.fuel = String.valueOf(hSettings.fuel);
        shipCSV.fuel_ly = String.valueOf(hSettings.fuelLY);
        shipCSV.max_burn = String.valueOf(hSettings.burn);
        shipCSV.cr_day = String.valueOf(hSettings.crDay);
        shipCSV.peak_CR_sec = String.valueOf(hSettings.peakCR);
        shipCSV.CR_loss_sec = String.valueOf(hSettings.crLossPerSecond);
        shipCSV.breakProb = String.valueOf(hSettings.breakProb);
        shipCSV.minPieces = String.valueOf(hSettings.minPeaces);
        shipCSV.maxPieces = String.valueOf(hSettings.maxPeaces);

        shipCSV.fighter_bays = "";
        //shipCSV.fighter_bays = String.valueOf(settings.hullSettings.baseFighters + settings.hullSettings.emptyFighters);
        //shipCSV.
        String temp = "";
        ArrayList<String> swaps = new ArrayList<>();
        for (String a : Settings.fighterTagsToHullTags.getListWithLock().keySet()){
            if (matedFighter.fighter.fighter_csv.tags.contains(a)) swaps.add(Settings.fighterTagsToHullTags.get(a));
        }
        Settings.fighterTagsToHullTags.unlock();
        int items = Settings.tags.size() + hSettings.tags.size() + swaps.size();
        if (!shipCSV.tags.isBlank() && (items != 0 || addFreeAutoTag()))temp+=", ";
        if (addFreeAutoTag()){
            temp += "no_auto_penalty";
            if (items != 0) temp += ", ";
        }
        for (String d : Settings.tags.getListWithLock()){
            temp+=d;
            items--;
            if (items != 0) temp +=",";
        }
        Settings.tags.unlock();
        for (String d : hSettings.tags){
            temp+=d;
            items--;
            if (items != 0) temp +=",";
        }
        for (String d : swaps){
            temp+=d;
            items--;
            if (items != 0) temp +=",";
        }
        shipCSV.tags = shipCSV.tags+temp;

        temp = "";
        if (!shipCSV.hints.isBlank())temp+=", ";
        swaps = new ArrayList<>();
        for (String a : Settings.fighterTagsToHullHints.getListWithLock().keySet()){
            if (matedFighter.fighter.fighter_csv.tags.contains(a)) swaps.add(Settings.fighterTagsToHullHints.get(a));
        }
        Settings.fighterTagsToHullHints.unlock();
        items = Settings.hints.size() + hSettings.hints.size() + swaps.size();
        for (String d : Settings.hints.getListWithLock()){
            temp+=d;
            items--;
            if (items != 0) temp +=",";
        }
        Settings.hints.unlock();
        for (String d : swaps){
            temp+=d;
            items--;
            if (items != 0) temp +=",";
        }
        for (String d : hSettings.hints){
            temp+=d;
            items--;
            if (items != 0) temp +=",";
        }
        shipCSV.hints = shipCSV.hints+temp;
        Seeker.shipsToPrintToCSV.add(shipCSV);
        //...
        //thats the ship.csv done.
        //in theory, I can just... create it now...??????
        //mmmm
    }
    private void addShipRoles(ChosenShipSettings settings){
        if (!Settings.forceAllowSpawns.contains(matedFighter.fighter.fighter_csv.id)){
            if (Settings.forcePreventSpawns.contains(matedFighter.fighter.fighter_csv.id)) return;
            if (!Settings.spawnRestricted.get() && Settings.isRestrictedMaybe(matedFighter.fighter.fighter_csv)) return;
        }
        Seeker.roles.add(new ShipRoles(getVariantId(settings),settings.variantSettings.spawnGroup,settings.variantSettings.spawnWeight));
    }

    private String getName(ChosenShipSettings settings){
        return Settings.getName(settings,matedFighter.hull.ship_csv);//NOTE: this needs to be set in addShipFile as well.
    }
    private String getHullID(ChosenShipSettings settings){
        return Settings.getHullID(matedFighter.fighter.fighter_csv);
    }
    private String getVariantId(ChosenShipSettings settings){
        return Settings.getVariantId(matedFighter.fighter.fighter_csv);
    }
    private String getModifiedValue_S_Int(String baseValue,float multi,float flat){
        if (baseValue.isBlank()) return ""+flat;
        return getModifiedValue_S_Int(Integer.parseInt(baseValue),multi,flat);
    }
    private String getModifiedValue_S_Int(int baseValue,float multi,float flat){
        return String.valueOf((int)(((baseValue * multi) + flat)));
    }
    private String getModifiedValue_S_Float(String baseValue,float multi,float flat){
        return getModifiedValue_S_Float(Float.parseFloat(baseValue),multi,flat);
    }
    private String getModifiedValue_S_Float(float baseValue,float multi,float flat){
        return String.valueOf(((baseValue * multi) + flat));
    }
    private int getModifiedValue_V_Int(String baseValue,float multi,float flat){
        return getModifiedValue_V_Int(Integer.parseInt(baseValue),multi,flat);
    }
    private int getModifiedValue_V_Int(int baseValue,float multi,float flat){
        return (int) ((baseValue * multi) + flat);
    }
    private float getModifiedValue_V_Float(String baseValue,float multi,float flat){
        return getModifiedValue_V_Float(Float.parseFloat(baseValue),multi,flat);
    }
    private float getModifiedValue_V_Float(float baseValue,float multi,float flat){
        return ((baseValue * multi) + flat);
    }
}
