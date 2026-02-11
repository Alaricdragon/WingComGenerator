package main.processers;

import main.processers.overrides.StarsectorJsonGetter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

public class CustomJSonReader {
    /*todo:
        ... I have somehow gotten the simple json libary working.
        ...
        at the end, I was this close to just... not doing this anymore. I kinda just wanted to make my own fucking json libary.
        because the simple one cant read starsectors files.
        like at all. there are , at the end of files, random lines without "" around them, and all sorts of really fucked up stuff....
        I am not happy about that. it was really frustrating
        and I 100% think I am going to continue to get hit by more of this shit. I do not like this. not at fucking all.
        so ya. keep in reserve a way to read json files. that or find a way to override the normal json reader, so I dont have to care about this crap anymore.
        I am so fucking tired of this ship. its been a week now. I am sad.
        fsdjknfgjkmx,cmf DVBNC
        AND ANGRY
    */
    private static JSONObject getJSonObjectFromPath(String path){
        File myObj = new File(path);//NOTE: this does not have a .text because I made it a not text file somehow???

        // try-with-resources: Scanner will be closed automatically
        ArrayList<Integer> inObject = new ArrayList<>();//how it works: 1 is object, 0 is array.
        ArrayList<JSONObject> activeObject = new ArrayList<>();
        ArrayList<JSONArray> activeArray = new ArrayList<>();
        boolean inCommas = false;
        try (Scanner in = new Scanner(myObj)) {
            while (in.hasNextLine()) {
                String data = in.nextLine();
                //so here is how it works:
                //1: .
                //1: every { } opens / closes a object
                //2: every [ ] opens / closes a array
                //3: every , marks the beginning of the next key / item or item combow
                //4: every : marks the transition between a key / item.
                //5: whitespace outside of "" / '' is ignored.
                //6:
                //4: everything inside of "" or '' is always data
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return activeObject.get(0);
    }
    private static StringBuilder removeInvalidLinesOLD(String path){
        StringBuilder jsonContent = new StringBuilder();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {
            String line;
            //String line2;
            String[] line2;
            String linet;
            while ((line = reader.readLine()) != null) {
                //todo: sometimes a item can have '' instead of "" in starsector. that should be fixed here.
                linet = line.trim();
                if (!linet.startsWith("#")) {
                    line2 = linet.split("#");
                    jsonContent.append(changeDashes(line2[0])).append("\n");
                    continue;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }
        //System.out.println(jsonContent);
        return jsonContent;
    }
    private static String removeInvalidLines(String path){
        StringBuilder jsonContent = new StringBuilder();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {
            String line = reader.readLine();
            //String line2;
            String[] line2;
            String linet;
            String newLine;
            while (line != null) {
                newLine = reader.readLine();
                //todo: sometimes a item can have '' instead of "" in starsector. that should be fixed here.
                linet = line.trim();

                if (!linet.startsWith("#")) {
                    line2 = linet.split("#");
                    String line3 = line2[0];
                    if (!line3.isBlank()) {
                        if (line3.startsWith("id")) {
                            line3 = line3.replaceFirst("id", "\"id\"");
                        }
                        if (line3.endsWith("f,") || line3.endsWith("d,")){
                            line3 = line3.substring(0, line3.length() - 2);
                            line3+=",";
                        }
                        jsonContent.append(changeDashes(line3)).append("\n");
                    }
                }
                line = newLine;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }

        String out = jsonContent.toString();
        //System.out.println(jsonContent);
        return removeCom(out);
    }
    private static String removeCom(String in){
        if (in.isBlank()) return in;
        if (in.endsWith(",\n")) return in.substring(0, in.length() - 2);
        if (in.endsWith(",\n ")) return in.substring(0, in.length() - 2);
        if (in.endsWith(",")) return in.substring(0, in.length() - 1);
        return in;
    }
    /*private static StringBuilder removeONEFUCKGIKVSDBFVjcbn(String in){
        //HOW THE FUCK AM I SUPPOSE TO REMOVE THE LAST ITEM ON A FUCKING STRING
       StringBuilder out = new StringBuilder();
       for (int a = 0; a < in.length()-1; a++){
           out.append(in.charAt(a));
       }
       return out;
    }*/
    private static StringBuilder removeInvalidLinesNEW(String path){
        StringBuilder jsonContent = new StringBuilder();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(path))) {
            String line = reader.readLine();
            //String line2;
            String[] line2;
            String linet;
            String newLine;

            while (line != null) {
                //todo: sometimes a item can have '' instead of "" in starsector. that should be fixed here.
                newLine = reader.readLine();
                linet = line.trim();
                if (!linet.startsWith("#")) {
                    line2 = linet.split("#");
                    String line3 = line2[0];
                    if (line3.isBlank()) continue;
                    if (line3.startsWith("id")){
                        line3 = line3.replaceFirst("id","\"id\"");
                    }
                    if (newLine == null && linet.endsWith(",")){
                        for (int a = 0; a < linet.length()-1; a++){
                            char b = linet.charAt(a);
                            jsonContent.append(b);
                        }
                        jsonContent.append("\n");
                    }else {
                        jsonContent.append(changeDashes(line3)).append("\n");
                    }
                    //continue;
                }
                line = newLine;
                //System.out.println("line: "+line);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }
        //System.out.println(jsonContent);
        //System.out.println(jsonContent);
        return jsonContent;
    }
    private static String changeDashes(String str){
        StringBuilder builder = new StringBuilder();
        boolean insideItem = false;
        for (int a = 0; a < str.length(); a++){
            char b = str.charAt(a);
            if (b == '"') insideItem = !insideItem;
            if (!insideItem && b == '\'') b = '"';
            builder.append(b);
        }
        return builder.toString();
        /*char a = '\'';
        char b = '"';
        return str.replace(a,b);*/
        //return str;
    }
    public static JSONArray getArray(String string,boolean rarData) throws ParseException {
        if (rarData) return getArray(string);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(string);
        return (JSONArray) obj;
    }
    public static JSONObject getObject(JSONObject string,boolean rarData) throws ParseException {
        return getObject(string.toJSONString(),rarData);
    }
    public static JSONObject getObject(String string,boolean rarData) throws ParseException {
        if (rarData) return getObject(string);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(string);
        return (JSONObject) obj;
    }
    public static JSONArray getArray(String path) throws ParseException {
        String string = removeInvalidLines(path);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(string.toString());//transformJsonString(string.toString()));
        return (JSONArray) obj;
    }
    /*public static JSONObject getObject(String path) throws ParseException {
        String string = removeInvalidLines(path);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(string.toString());//transformJsonString(string.toString()));
        //System.out.println("path of: "+path+" string of: \n"+string);
        return (JSONObject) obj;
    }*/
    public static JSONObject getObject(String path) {
        return StarsectorJsonGetter.getJsonObject(path);
    }
    public static void getObjectLog(String path) {
        String log = "path of: "+path+" string of: \n";
        JSONParser parser = new JSONParser();
        try {
            JSONObject string = StarsectorJsonGetter.getJsonObject(path);
            Object obj = parser.parse(string.toJSONString());//transformJsonString(string.toString()));
        } catch (ParseException e) {
            System.out.println(log + "\n"+e);
            //throw new RuntimeException(e);
        }
        //return (JSONObject) obj;

    }
    /*public static void getObjectLog(String path) {
        String string = removeInvalidLines(path);
        String log = "path of: "+path+" string of: \n"+string;
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(string.toString());//transformJsonString(string.toString()));
        } catch (ParseException e) {
            System.out.println(log + "\n"+e);
            //throw new RuntimeException(e);
        }
        //return (JSONObject) obj;

    }*/
    public static String[] getItemsInArray(JSONArray array){
        if (array == null) return new String[0];
        String[] out = new String[array.size()];
        int d = 0;
        for (Object b : array){
            out[d] = b.toString();
            d++;
        }
        return out;
    }
    public static void writeJsonFile(String path, JSONObject json) throws IOException {
        PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

        //writer.println(json);//transformJsonString(json.toJSONString()));
        writer.println(json.toJSONString());//transformJsonString(json.toJSONString()));
        writer.close();
    }
    public static String transformJsonString(String string){
        String indent = " ";
        int indents = 0;
        StringBuilder jsonContent = new StringBuilder();
        for (int a = 0; a < string.length(); a++){
            char b = string.charAt(a);
            if (b == ':') indents++;
            if (b == ',') indents--;
            jsonContent.append(b);
            if (b == ',' || b == '{' || b == '['){
                //line break.
                jsonContent.append("\n");
                for (int c = 0; c < indents; c++){
                    jsonContent.append(' ');
                }
            }
        }
        //System.out.println("got json content as: "+ jsonContent.toString());
        return jsonContent.toString();
    }
}
