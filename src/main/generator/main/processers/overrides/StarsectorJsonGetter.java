package main.processers.overrides;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class StarsectorJsonGetter {
    public static JSONObject getJsonObject(String path) {
        StringBuilder jsonContent = new StringBuilder();
        StarsectorJsonGetter out;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {
            out = new StarsectorJsonGetter(reader);
            return out.getOutput();
        } catch (IOException e) {
            return null;
        }
    }

    private BufferedReader reader;
    ArrayList<Object> depth = new ArrayList<>();
    private JSONObject currentObject = null;
    private JSONArray currentArray = null;
    int varType = -1; //-1 == not in varuble, 0 == no container. 1 == ''. 2 == ""
    int phase = -1;//-1 is getting first item. 1 the rest.
    //int processing = 2; //-1 for nothing, 0 for other, 1 for object, 2 for array. starts at 2 because it only matters then.
    boolean done = false;
    private String currentName = "";
    private String currentVar = "";
    boolean getingArray = false;

    public StarsectorJsonGetter(String path) {
        this.reader = reader;
    }
    public StarsectorJsonGetter(BufferedReader reader) {
        this.reader = reader;
    }
    public JSONObject getOutput() throws IOException {
        JSONObject out = new JSONObject();
        currentObject = out;
        depth.add(out);
        /*todo:
            1: key starts at:
                a: first char.
                b: first " or ' (but not including the " or ')
            2: varble starts at:
                a: first char.
                b: first " or ' (but not including the " or ')
                c: { or [ (for object / array respectively). (outside of "" or '')
            3: varuble ends at:
                a: first white space (if in varType = 0)
                b: matching the start item of b. (' if var type = 1, " if var type = 2)
                c: ] or } (outside of "" or '') (does not matter if in a object or not.)
            4: after a varuble ends, if the next item is a ',' I iggnore it. it does not fucking matter.
            5: if on any line I bump into a '#' I skip the rest of that line --unless-- it is inside of a varubles or keys '' / ""
            6: white space outside of variables (' ' or '/n' ) does not matter at all. skip them.
            -: there might be more failures in latter bits. I will learn them

         */
        //first, we need to continue untill we find the first {
        String line;
        //boolean shouldBreak;
        while ((line = reader.readLine()) != null && !done){
            for (char a : line.toCharArray()){
                //shouldBreak = false;
                if (varType <= 0 && a == '#') break;//if I am in a var, I cant leave untill it is time.
                switch (phase){
                    case -1:
                        if (a == '{') phase = 0;
                        break;
                    case 0:
                        processKey(a);
                        break;
                    case 1:
                        processTypeStart(a);
                        break;
                }
                if (done) return out;
            }
        }
        return out;
    }
    private void processKey(char a){
        //key can be: the start of a 'key', or the end of a 'type'. I dont know what one.
        //also gets the full name of the next varuble. apparently.
        //if so, maybe I should also get when a new list starts????
        //....
        //I think I need my own process for getting... arg... asdakjdska
        //THIS IS ONLY SUPPOSE TO GET KEYS. IT GETS KEYS. THERE. ITS ALL GOOD.
        if (varType == -1) {
            if ((a == ' ' || a == '\n' || a == ',' || a == 9)) return;//this is iggnored.
            if (a == ']' || a == '}') {
                //ends an object.
                getingArray = false;
                depth.removeLast();
                if (depth.size() == 0) {
                    done = true;
                    return;
                }
                if (depth.getLast() instanceof JSONObject) {
                    currentObject = (JSONObject) depth.getLast();
                    currentArray = null;
                } else {
                    currentObject = null;
                    currentArray = (JSONArray) depth.getLast();
                    getingArray = true;
                    phase = 1;//move to phase one for repetitive key processing.
                }
                return;
            }
            //creates a new start object key
            if (a == '\'') {
                varType = 1;
                currentName = "";
                return;
            }
            if (a == '"') {
                varType = 2;
                currentName = "";
                return;
            }
            varType = 0;
            currentName = "" + a;
            return;
        }
        switch (varType){
            case 0:
                if (a == ' ' || a == '\n' || a == ':' || a == ',' || a == 9 || a == '}' || a == ']'){
                    //ends the object.
                    phase = 1;
                }else{
                    currentName+=a;
                }
                break;
            case 1:
                if (a == '\''){
                    phase = 1;
                }else{
                    currentName+=a;
                }
                break;
            case 2:
                if (a == '"'){
                    phase = 1;
                }else{
                    currentName+=a;
                }
                break;
        }
        if (phase == 1) varType = -1;

    }
    private void processTypeStart(char a){
        //this should get the following:
        //1: it should get varubles.
        //2: it should get varuble types (just like get key).
        //it should not:
        //1: get keys.

        //
        if (varType == -1) {
            if ((a == ' ' || a == ':' || a == '\n' || a == ',' || a == 9)) return;
            //start of array data.
            if (a == '{') {
                //starts an object. sets that object as current objecct. returns to phase 0 to look for keys.
                currentObject = new JSONObject();
                Object addTo = depth.getLast();
                if (addTo instanceof JSONObject) {
                    ((JSONObject) addTo).put(currentName, currentObject);
                } else {
                    ((JSONArray) addTo).add(currentObject);
                }
                currentArray = null;//no longer on current array.
                currentName = "";
                depth.add(currentObject);
                phase = 0;
                return;
            }
            if (a == '[') {
                //starts an array.
                currentArray = new JSONArray();
                Object addTo = depth.getLast();
                if (addTo instanceof JSONObject) {
                    ((JSONObject) addTo).put(currentName, currentArray);
                } else {
                    ((JSONArray) addTo).add(currentArray);
                }
                currentObject = null;//no longer on current object.
                depth.add(currentArray);

                return;
            }
            if (a == ']' || a == '}') {
                //ends an object.
                getingArray = false;
                depth.removeLast();
                if (depth.size() == 0) {
                    done = true;
                    return;
                }
                if (depth.getLast() instanceof JSONObject) {
                    currentObject = (JSONObject) depth.getLast();
                    currentArray = null;
                    phase = 0;//move to jsonObject get key
                } else {
                    currentObject = null;
                    currentArray = (JSONArray) depth.getLast();
                    getingArray = true;
                    //phase = 1;//move to phase one for repetitive key processing.
                }
                return;
            }
            if (a == '\'') {
                currentVar = "";
                varType = 1;
                return;
            }
            if (a == '"') {
                currentVar = "";
                varType = 2;
                return;
            }
            currentVar=""+a;
            varType = 0;
            return;
        }
        switch (varType){
            case 0:
                if (a == ' ' || a == '\n' || a == ':' || a == ',' || a == 9 || a == '}' || a == ']'){
                    processVar();
                    checkClosingBrackets(a);
                }else{
                    currentVar+=a;
                }
                break;
            case 1:
                if (a == '\''){
                    processVar();
                }else{
                    currentVar+=a;
                }
                break;
            case 2:
                if (a == '"'){
                    processVar();
                }else{
                    currentVar+=a;
                }
                break;
        }
    }
    private void checkClosingBrackets(char a){
        if (a == ']' || a == '}') {
            //ends an object.
            getingArray = false;
            depth.removeLast();
            if (depth.size() == 0) {
                done = true;
                return;
            }
            if (depth.getLast() instanceof JSONObject) {
                currentObject = (JSONObject) depth.getLast();
                currentArray = null;
                phase = 0;//move to jsonObject get key
            } else {
                currentObject = null;
                currentArray = (JSONArray) depth.getLast();
                getingArray = true;
                //phase = 1;//move to phase one for repetitive key processing.
            }
            return;
        }
    }
    private void processVar(){
        if (!getingArray) phase = 0;
        if (depth.getLast() instanceof JSONObject){
            phase = 0;
            ((JSONObject)depth.getLast()).put(currentName,currentVar);
        }else{
            phase = 1;
            ((JSONArray)depth.getLast()).add(currentVar);
        }
        currentVar = "";
        currentName = "";
        varType = -1;
    }

}
