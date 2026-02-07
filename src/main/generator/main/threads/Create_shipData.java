package main.threads;

import main.Seeker;
import main.beans.Bean_Fighter;
import main.beans.Bean_Ship;
import main.settings.ChosenShipSettings;
import main.settings.HullSettings;
import main.settings.Settings;
import main.settings.ShipGroupSettings;
import main.types.HullJson;
import main.types.MatedFighters;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class Create_shipData implements Runnable{
    private MatedFighters matedFighter;
    public Create_shipData(MatedFighters matedFighter){
        this.matedFighter = matedFighter;
    }
    @Override
    public void run() {
        /*todo:
            ship_data.csv:
                id = "wingcomGenerated_"+id;
                name = name + (WINGCOM)
                all need modifications from HullSettings:
                    fleet ponits = Math.max((wing.csv.op cost / 3) + 1,min value)
                    hp,             (*1)
                    armor,          (*1)
                    max flux,       (*1)
                    flux disp       (*1)
                    base value,     (*1.5 (from fighter.csv))
                    supplies/rec    (= fleet ponits)
                    supplies/mo     (= fleet ponits)
                    acceleration    (+50)
                    deceleration    (+50)
                    max speed       (+25)
                fighterbays = HullSettings.empty + base bays.
                all need to be set from HullSettings:
                    op              (20) (+8 for heavy armor?)
                    max crew,       (0)
                    cargo,          (0)
                    fuel,           (0)
                    fuel/ly,        (0.5)
                    max burn,       (11)
                    cr / day,       (8)
                    peak cr,        (180)
                    cr loss / sec,  (0.25)
                custom sets:
                    rarity:
                        0
                        if wing.csv->tag->rare_bp: 1
                    break prob = 0.5
                    min peaces = 1
                    max pieces = 2
                    tags = tags from combined HullSettings + baseTags
                    hints = -- (need to add this to settings and HullSettings)
                            PLAY_FIGHTER_OVERLOAD_SOUNDS
               -
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
            .variant file:
                -I should just get this naturaly..???
                -but read the star federation mechs for how they look by default.
            .roles file:
                1: get the ships 'role' and 'weight' from VariantSettings.
                2: add this to the outputed roles.json (will be created after this step)
                note: remember to check and make sure this can spawn (wing.csv->tags-> no drop / no sell, or the Settings / ShipGroupSettings demand it.)

         */

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

        addShipCSV(settings);

    }
    private void addShipFile(ChosenShipSettings settings){
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
        JSONObject out = new JSONObject();
        HullJson j = settings.hullSettings.json.getItemWithLock();
        for (Object a : j.json.keySet()){
            String key = a.toString();
            out.put(key,j.json.get(key));
        }
        settings.hullSettings.json.unlock();//items copyed. proboly?

        //copying builtInMods so I can refactor them.
        JSONArray bultIn = new JSONArray();
        JSONArray b = (JSONArray) out.get("builtInMods");
        bultIn.addAll(b);
        ArrayList<String> mods = Settings.permaMods.getListWithLock();
        bultIn.addAll(mods);
        Settings.permaMods.unlock();
        bultIn.addAll(settings.hullSettings.perma_hullmods);
        ArrayList<Object> toRemove = new ArrayList<>();
        for (Object a : bultIn){
            if (a.toString().equals("no_weapon_flux")){
                toRemove.add(a);
                continue;
            }
            int copys = 0;
            for (Object c : bultIn){
                if (c.equals(a)){
                    copys++;
                }
            }
            if (copys > 1) toRemove.add(a);
        }
        for (Object a : toRemove) bultIn.remove(a);
        out.put("builtInMods",bultIn);

        //todo: I am on step 4 now.
    }
    private void addShipCSV(ChosenShipSettings settings){
        HullSettings hSettings= settings.hullSettings;
        Bean_Ship m_s_bean = matedFighter.hull.ship_csv;
        Bean_Ship shipCSV = new Bean_Ship(m_s_bean);
        Bean_Fighter m_f_bean = matedFighter.fighter.fighter_csv;
        shipCSV.name = matedFighter.hull.ship_csv.name+" (WINGCOM)";
        shipCSV.id = "WinComGenerator"+matedFighter.hull.ship_csv.id;
        shipCSV.max_speed = getModifiedValue_S_Int(m_s_bean.max_speed,hSettings.speedM,hSettings.speedF);
        shipCSV.fleet_pts = getModifiedValue_S_Int(m_f_bean.op_cost,hSettings.dpM,hSettings.dpF);
        shipCSV.CR_to_deploy = getModifiedValue_S_Int(m_f_bean.op_cost,hSettings.crToDeployM,hSettings.crToDeployF);
        shipCSV.hitpoints = getModifiedValue_S_Int(m_s_bean.hitpoints,hSettings.hullM,hSettings.hullF);
        shipCSV.armor_rating = getModifiedValue_S_Int(m_s_bean.armor_rating,hSettings.armorM,hSettings.armorF);
        shipCSV.max_flux = getModifiedValue_S_Int(m_s_bean.max_flux,hSettings.capM,hSettings.capF);
        shipCSV.flux_dissipation = getModifiedValue_S_Int(m_s_bean.flux_dissipation,hSettings.ventM,hSettings.ventF);
        shipCSV.base_value = getModifiedValue_S_Int(m_s_bean.base_value,hSettings.valueM,hSettings.valueF);
        shipCSV.supplies_rec = getModifiedValue_S_Int(m_s_bean.supplies_rec,hSettings.suppliesRecoverM,hSettings.suppliesRecoverF);
        shipCSV.supplies_mo = getModifiedValue_S_Int(m_s_bean.supplies_mo,hSettings.suppliesMonthM,hSettings.suppliesMonthF);
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

        String temp = "";
        if (!shipCSV.tags.isBlank())temp+=", ";
        int items = Settings.tags.size() + hSettings.tags.size();
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
        shipCSV.tags = shipCSV+temp;

        temp = "";
        if (!shipCSV.hints.isBlank())temp+=", ";
        items = Settings.hints.size() + hSettings.hints.size();
        for (String d : Settings.hints.getListWithLock()){
            temp+=d;
            items--;
            if (items != 0) temp +=",";
        }
        Settings.hints.unlock();
        for (String d : hSettings.hints){
            temp+=d;
            items--;
            if (items != 0) temp +=",";
        }
        shipCSV.hints = shipCSV+temp;
        Seeker.shipsToPrintToCSV.add(shipCSV);
        //...
        //thats the ship.csv done.
        //in theory, I can just... create it now...??????
        //mmmm
    }
    private String getModifiedValue_S_Int(String baseValue,float multi,float flat){
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
