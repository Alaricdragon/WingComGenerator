package main.beans;

import com.opencsv.bean.CsvBindByName;
//import lombok.Getter;
//import lombok.Setter;

//@Getter
//@Setter
public class Bean_Fighter {
    public boolean isValid(){
        if (id == null || id.startsWith("#")) return false;
        if (variant == null || variant.startsWith("#")) return false;
        /*if (number.startsWith("#")) return false;
        if (num.startsWith("#")) return false;
        if (attackPositionOffset.startsWith("#")) return false;
        if (base_value.startsWith("#")) return false;
        if (attackRunRange.startsWith("#")) return false;
        if (fleet_pts.startsWith("#")) return false;
        if (formation.startsWith("#")) return false;
        if (op_cost.startsWith("#")) return false;
        if (range.startsWith("#")) return false;
        if (rarity.startsWith("#")) return false;
        if (refit.startsWith("#")) return false;
        if (role.startsWith("#")) return false;
        if (role_desc.startsWith("#")) return false;
        if (tags.startsWith("#")) return false;
        if (tier.startsWith("#")) return false;*/
        return true;
    }
    //everything is strings so I can detect if a hull is 'disabled'
    @CsvBindByName
    public String id;
    @CsvBindByName
    public String variant;
    @CsvBindByName
    public String tags;
    @CsvBindByName
    public String tier;
    @CsvBindByName
    public String rarity;
    @CsvBindByName(column = "fleet pts")
    public String fleet_pts;
    @CsvBindByName(column = "op cost")
    public String op_cost;
    @CsvBindByName(column = "base value")
    public String base_value;

    //unneeded values.
    @CsvBindByName
    public String number;
    @CsvBindByName
    public String formation;
    @CsvBindByName
    public String range;
    @CsvBindByName
    public String attackRunRange;
    @CsvBindByName
    public String attackPositionOffset;
    @CsvBindByName
    public String num;
    @CsvBindByName
    public String role;
    @CsvBindByName(column = "role desc")
    public String role_desc;
    @CsvBindByName
    public String refit;
    /*
    @CsvBindByName
    private String id;
    @CsvBindByName
    private String variant;
    @CsvBindByName
    private String tags;
    @CsvBindByName
    private int tier;
    @CsvBindByName
    private float rarity;
    @CsvBindByName(column = "fleet pts")
    private int fleet_pts;
    @CsvBindByName(column = "op cost")
    private int op_cost;
    @CsvBindByName(column = "base value")
    private int base_value;

    //unneeded values.
    @CsvBindByName
    private int number;
    @CsvBindByName
    private String formation;
    @CsvBindByName
    private int range;
    @CsvBindByName
    private int attackRunRange;
    @CsvBindByName
    private int attackPositionOffset;
    @CsvBindByName
    private int num;
    @CsvBindByName
    private String role;
    @CsvBindByName(column = "role desc")
    private String role_desc;
    @CsvBindByName
    private String refit;

     */
}
