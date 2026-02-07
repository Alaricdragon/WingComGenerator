package main.beans;

import com.opencsv.bean.CsvBindByName;

public class Bean_Ship {
    public Bean_Ship(){

    }
    public Bean_Ship(Bean_Ship b){

          name=b.name;

          id=b.id;

          designation=b.designation;
          tech_manufacturer=b.tech_manufacturer;
          system_id=b.system_id;
          fleet_pts=b.fleet_pts;

          hitpoints=b.hitpoints;
          armor_rating=b.armor_rating;
          max_flux=b.max_flux;
          _8654=b._8654;
          flux_dissipation=b.flux_dissipation;
          ordnance_points=b.ordnance_points;
          fighter_bays=b.fighter_bays;
          max_speed=b.max_speed;

          acceleration=b.acceleration;

          deceleration=b.deceleration;
          max_turn_rate=b.max_turn_rate;
          turn_acceleration=b.turn_acceleration;

          mass=b.mass;
          shield_type=b.shield_type;
          defense_id=b.defense_id;
          shield_arc=b.shield_arc;
          shield_upkeep=b.shield_upkeep;
          shield_efficiency=b.shield_efficiency;
          phase_cost=b.phase_cost;
          phase_upkeep=b.phase_upkeep;
          min_crew=b.min_crew;
          max_crew=b.max_crew;

          cargo=b.cargo;

          fuel=b.fuel;
          fuel_ly=b.fuel_ly;

          range=b.range;
          max_burn=b.max_burn;
          base_value=b.base_value;
          cr_day=b.cr_day;
          CR_to_deploy=b.CR_to_deploy;
          peak_CR_sec=b.peak_CR_sec;
          CR_loss_sec=b.CR_loss_sec;
          supplies_rec=b.supplies_rec;
          supplies_mo=b.supplies_mo;
          c_s=b.c_s;
          c_f=b.c_f;
          f_s=b.f_s;
          f_f=b.f_f;
          crew_s=b.crew_s;
          crew_f=b.crew_f;

          hints=b.hints;

          tags=b.tags;
          logistics_n_a_reason=b.logistics_n_a_reason;
          codex_variant_id=b.codex_variant_id;

          rarity=b.rarity;

          breakProb=b.breakProb;

          minPieces=b.minPieces;

          maxPieces=b.maxPieces;
          travel_drive=b.travel_drive;

          number=b.number=b.number;
    }
    public boolean isValid(){
        //todo: more data needs to be aquired so I can determin how to handle this....
        //      for now, I will simply use name and id for this check. maybe a few other things will prove 'required' latter.
        if (this.name == null || this.name.startsWith("#") || this.name.isBlank()) return false;
        if (this.id == null || this.id.startsWith("#") || this.id.isBlank()) return false;
        /*if (this.name.startsWith("#")) return false;
        if (this._8654.startsWith("#")) return false;
        if (this.acceleration.startsWith("#")) return false;
        if (this.armor_rating.startsWith("#")) return false;
        if (this.number.startsWith("#")) return false;
        if (this.c_f.startsWith("#")) return false;
        if (this.base_value.startsWith("#")) return false;
        if (this.breakProb.startsWith("#")) return false;
        if (this.c_s.startsWith("#")) return false;
        if (this.cargo.startsWith("#")) return false;
        if (this.codex_variant_id.startsWith("#")) return false;
        if (this.cr_day.startsWith("#")) return false;
        if (this.CR_loss_sec.startsWith("#")) return false;
        if (this.CR_to_deploy.startsWith("#")) return false;
        if (this.crew_f.startsWith("#")) return false;
        if (this.crew_s.startsWith("#")) return false;
        if (this.deceleration.startsWith("#")) return false;
        if (this.defense_id.startsWith("#")) return false;
        if (this.designation.startsWith("#")) return false;
        if (this.f_f.startsWith("#")) return false;
        if (this.f_s.startsWith("#")) return false;
        if (this.fighter_bays.startsWith("#")) return false;
        if (this.fleet_pts.startsWith("#")) return false;
        if (this.flux_dissipation.startsWith("#")) return false;
        if (this.fuel.startsWith("#")) return false;
        if (this.fuel_ly.startsWith("#")) return false;
        if (this.hints.startsWith("#")) return false;
        if (this.hitpoints.startsWith("#")) return false;
        if (this.id.startsWith("#")) return false;
        if (this.logistics_n_a_reason.startsWith("#")) return false;
        if (this.mass.startsWith("#")) return false;
        if (this.max_burn.startsWith("#")) return false;
        if (this.max_crew.startsWith("#")) return false;
        if (this.max_flux.startsWith("#")) return false;
        if (this.max_speed.startsWith("#")) return false;
        if (this.startsWith("#")) return false;
        if (this.startsWith("#")) return false;
        if (this.startsWith("#")) return false;
        if (this.startsWith("#")) return false;
        if (this.startsWith("#")) return false;
        if (this.startsWith("#")) return false;
        if (this.startsWith("#")) return false;
        if (this.startsWith("#")) return false;
        if (this.startsWith("#")) return false;
        if (this.startsWith("#")) return false;*/
        return true;
    }
    @CsvBindByName
    public String name;
    @CsvBindByName
    public String id;
    @CsvBindByName
    public String designation;
    @CsvBindByName(column = "tech/manufacturer")
    public String tech_manufacturer;
    @CsvBindByName(column = "system id")
    public String system_id;
    @CsvBindByName(column = "fleet pts")
    public String fleet_pts;
    @CsvBindByName
    public String hitpoints;
    @CsvBindByName(column = "armor rating")
    public String armor_rating;
    @CsvBindByName(column = "max flux")
    public String max_flux;
    @CsvBindByName(column = "8/6/5/4%")
    public String _8654;
    @CsvBindByName(column = "flux dissipation")
    public String flux_dissipation;
    @CsvBindByName(column = "ordnance points")
    public String ordnance_points;
    @CsvBindByName(column = "fighter bays")
    public String fighter_bays;
    @CsvBindByName(column = "max speed")
    public String max_speed;
    @CsvBindByName
    public String acceleration;
    @CsvBindByName
    public String deceleration;
    @CsvBindByName(column = "max turn rate")
    public String max_turn_rate;
    @CsvBindByName(column = "turn acceleration")
    public String turn_acceleration;
    @CsvBindByName
    public String mass;
    @CsvBindByName(column = "shield type")
    public String shield_type;
    @CsvBindByName(column = "defense id")
    public String defense_id;
    @CsvBindByName(column = "shield arc")
    public String shield_arc;
    @CsvBindByName(column = "shield upkeep")
    public String shield_upkeep;
    @CsvBindByName(column = "shield efficiency")
    public String shield_efficiency;
    @CsvBindByName(column = "phase cost")
    public String phase_cost;
    @CsvBindByName(column = "phase upkeep")
    public String phase_upkeep;
    @CsvBindByName(column = "min crew")
    public String min_crew;
    @CsvBindByName(column = "max crew")
    public String max_crew;
    @CsvBindByName
    public String cargo;
    @CsvBindByName
    public String fuel;
    @CsvBindByName(column = "fuel/ly")
    public String fuel_ly;
    @CsvBindByName
    public String range;
    @CsvBindByName(column = "max burn")
    public String max_burn;
    @CsvBindByName(column = "base value")
    public String base_value;
    @CsvBindByName(column = "cr %/day")
    public String cr_day;
    @CsvBindByName(column = "CR to deploy")
    public String CR_to_deploy;
    @CsvBindByName(column = "peak CR sec")
    public String peak_CR_sec;
    @CsvBindByName(column = "CR loss/sec")
    public String CR_loss_sec;
    @CsvBindByName(column = "supplies/rec")
    public String supplies_rec;
    @CsvBindByName(column = "supplies/mo")
    public String supplies_mo;
    @CsvBindByName(column = "c/s")
    public String c_s;
    @CsvBindByName(column = "c/f")
    public String c_f;
    @CsvBindByName(column = "f/s")
    public String f_s;
    @CsvBindByName(column = "f/f")
    public String f_f;
    @CsvBindByName(column = "crew/s")
    public String crew_s;
    @CsvBindByName(column = "crew/f")
    public String crew_f;
    @CsvBindByName
    public String hints;
    @CsvBindByName
    public String tags;
    @CsvBindByName(column = "logistics n/a reason")
    public String logistics_n_a_reason;
    @CsvBindByName(column = "codex variant id")
    public String codex_variant_id;
    @CsvBindByName
    public String rarity;
    @CsvBindByName
    public String breakProb;
    @CsvBindByName
    public String minPieces;
    @CsvBindByName
    public String maxPieces;
    @CsvBindByName(column = "travel drive")
    public String travel_drive;
    @CsvBindByName
    public String number;
    /*
    @CsvBindByName
    public String name;
    @CsvBindByName
    public String id;
    @CsvBindByName
    public String designation;
    @CsvBindByName(column = "tech/manufacturer")
    public String tech_manufacturer;
    @CsvBindByName(column = "system id")
    public String system_id;
    @CsvBindByName(column = "fleet pts")
    public int fleet_pts;
    @CsvBindByName
    public int hitpoints;
    @CsvBindByName(column = "armor rating")
    public int armor_rating;
    @CsvBindByName(column = "max flux")
    public int max_flux;
    @CsvBindByName(column = "8/6/5/4%")
    public int _8654;
    @CsvBindByName(column = "flux dissipation")
    public int flux_dissipation;
    @CsvBindByName(column = "ordnance points")
    public int ordnance_points;
    @CsvBindByName(column = "fighter bays")
    public int fighter_bays;
    @CsvBindByName(column = "max speed")
    public int max_speed;
    @CsvBindByName
    public int acceleration;
    @CsvBindByName
    public int deceleration;
    @CsvBindByName(column = "max turn rate")
    public int max_turn_rate;
    @CsvBindByName(column = "turn acceleration")
    public int turn_acceleration;
    @CsvBindByName
    public int mass;
    @CsvBindByName(column = "shield type")
    public String shield_type;
    @CsvBindByName(column = "defense id")
    public String defense_id;
    @CsvBindByName(column = "shield arc")
    public int shield_arc;
    @CsvBindByName(column = "shield upkeep")
    public float shield_upkeep;
    @CsvBindByName(column = "shield efficiency")
    public float shield_efficiency;
    @CsvBindByName(column = "phase cost")
    public float phase_cost;
    @CsvBindByName(column = "phase upkeep")
    public float phase_upkeep;
    @CsvBindByName(column = "min crew")
    public int min_crew;
    @CsvBindByName(column = "max crew")
    public int max_crew;
    @CsvBindByName
    public int cargo;
    @CsvBindByName
    public int fuel;
    @CsvBindByName(column = "fuel/ly")
    public float fuel_ly;
    @CsvBindByName
    public int range;
    @CsvBindByName(column = "max burn")
    public int max_burn;
    @CsvBindByName(column = "base value")
    public int base_value;
    @CsvBindByName(column = "cr %/day")
    public int cr_day;
    @CsvBindByName(column = "CR to deploy")
    public int CR_to_deploy;
    @CsvBindByName(column = "peak CR sec")
    public int peak_CR_sec;
    @CsvBindByName(column = "CR loss/sec")
    public float CR_loss_sec;
    @CsvBindByName(column = "supplies/rec")
    public int supplies_rec;
    @CsvBindByName(column = "supplies/mo")
    public int supplies_mo;
    @CsvBindByName(column = "c/s")
    public int c_s;
    @CsvBindByName(column = "c/f")
    public int c_f;
    @CsvBindByName(column = "f/s")
    public int f_s;
    @CsvBindByName(column = "f/f")
    public int f_f;
    @CsvBindByName(column = "crew/s")
    public int crew_s;
    @CsvBindByName(column = "crew/f")
    public int crew_f;
    @CsvBindByName
    public String hints;
    @CsvBindByName
    public String tags;
    @CsvBindByName(column = "logistics n/a reason")
    public String logistics_n_a_reason;
    @CsvBindByName(column = "codex variant id")
    public String codex_variant_id;
    @CsvBindByName
    public float rarity;
    @CsvBindByName
    public float breakProb;
    @CsvBindByName
    public int minPieces;
    @CsvBindByName
    public int maxPieces;
    @CsvBindByName(column = "travel drive")
    public String travel_drive;
    @CsvBindByName
    public float number;
     */
}
