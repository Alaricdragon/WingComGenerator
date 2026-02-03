package main.threads;

import main.Seeker;
import main.processers.CustomJSonReader;
import main.processers.MultiGetArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;

public class SeekModId implements Runnable{
    private String path;
    private MultiGetArray<String> list;
    public SeekModId(String path,MultiGetArray<String> list){
        this.path = path;
        this.list = list;
    }
    @Override
    public void run() {
        String id = "";
        try {
            JSONObject b = CustomJSonReader.getObject(path + "/mod_info.json");

            if (!b.containsKey("id")) return;
            id = b.get("id").toString();
        }catch (Exception e){
            //System.err.println("failed loop for mod: "+path+" of: "+e);
            return;
        }
        if (id.isBlank()) return;
        /*
            checked were threads 'fail' at:
            here is a list of threads final position:
            1: 6
            2: 6
            3: 6
            4: 4
            5: na
            6: 6
            7: 6
            8: na
            9: 6
            10: 2
            11: 6
            12: 1
            13: na
            14: 1
            15: na
            16: 2
            17: 4
            18: 2
            19: 4
            20: 6
            21: 1
            22: 2
            23: 2

            total (for each position):
                1: 3
                2: 5
                3: 0
                4: 3
                5: 0
                6: 8.
                so, nothing got stuck at step 3 or 5.
                so nothing got stuck at 'addModPath'.
                also, nothing got stuck at 'the unlock.'

                things did get stuck at:
                1: 3
                    stage 1 is before the lock triggers. failing here is expected, if all locks are reserved.
                2: 5
                    stage 2 failing is strage. I gues there is a syncronized function here to get traped on?
                4: 3
                    stage 4 failing is also strange. there is also a syncronized function here two so...
                    I guess that's the link here? synchronized functions?
            conclusion:
                I am doing something wrong with synchronized functions. additional research is required.
                noticeable, Seeker.addModPath didnt deadlock. despite being synchronized.
         */
        //System.out.println("ID: "+Thread.currentThread().getName()+" 1) starting loop...");
        int check = list.getAndReserveListID();
        //System.out.println("ID: "+Thread.currentThread().getName()+" 2) got past the lock...");
        try {
            ArrayList<String> list2 = list.getList(check);
            //System.out.println("ID: "+Thread.currentThread().getName()+" 3) got to the check....");
            if (list2.contains(id)) Seeker.addModPath(id, path);
            //System.out.println("ID: "+Thread.currentThread().getName()+" 4)got past the setting");
        }catch (Exception e){
            //System.err.println(e);
        }finally{
            list.unlockList(check);
            //System.out.println("ID: "+Thread.currentThread().getName()+" 5) unlocked...");
        }
        //System.out.println("ID: "+Thread.currentThread().getName()+" 6) completed...");
    }
}
