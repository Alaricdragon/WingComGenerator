package main.threads;

import main.types.Fighter;
import main.types.Hull;
import main.types.Variant;

public class Create_FighterSpec implements Runnable{
    Fighter fighter;
    Hull hull;
    Variant variant;
    public Create_FighterSpec(Fighter fighter, Hull hull, Variant variant){
        this.fighter = fighter;
        this.hull = hull;
        this.variant = variant;
    }
    @Override
    public void run() {
        /*todo:
            1) create a blank __a with the require modifications.
            2) add the __a to Seeker with the required modifications for its function. (just a big list of em. I forgot why I needed them...)
            3) create a new hull spec for this item with the following modifications:
                1: all weapons (from both the variant and hull) need to be built in (that are not decorative)
                2: the new hullmods need to be added
                    -some factions, like remnants, need to have the 'automated ship' hullmod added as well.
                    -some other factions need there own hull mods added. maybe make a settings.json for that?
                3: add any weapons that need to be added (like a single center mounted one)
                4: make the required fighter built in.
                5: any built in wepons need to have there energy reduced to 0 with a tag. I think thats a thing?
                    -if not, it should be fine.... probably?
                    -I think there was a way to set the energy to actualy be used, so might be better.
            4) create the variant file:
                multiple variants:
                    for phase fighters:
                        add 10,10 caps / vents
                        add heavy armor
                        add 10,10 caps / vents
                        add heavy armor
                    for shield fighters:
                    for unshielded / un-phased fighters:
                        add makeshift shield
                        add heavy armor
                        add 6,6 caps / vents
         */
    }
}
