package main.threads;

import main.Seeker;
import main.processers.CustomJSonReader;
import main.types.ShipRoles;
import org.json.simple.JSONObject;

import java.io.IOException;

public class Create_RolesJson implements Runnable{
    @Override
    public void run() {
        /*
        todo:
            1) in theory I just... add each fighter to a single json item with a priority of like, 0.2?
            -) no need for a inputted data, I can draw directly from seeker, as that data should be done being set at this stage.
        */
        JSONObject out = new JSONObject();
        for (ShipRoles a : Seeker.roles.getListWithLock()){
            JSONObject item;
            if (out.containsKey(a.role)) item = (JSONObject) out.get(a.role);
            else{
                item = new JSONObject();
                out.put(a.role,item);
            }
            item.put(a.id,a.weight);
        }
        Seeker.roles.unlock();
        try {
            CustomJSonReader.writeJsonFile("./data/world/factions/default_ship_roles.json",out);
        } catch (IOException e) {
            System.out.println("failed to create json. error");
            throw new RuntimeException(e);
        }
    }
}
