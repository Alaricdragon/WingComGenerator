package main.processers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CustomJSonReader {
    private static StringBuilder removeInvalidLines(String path){
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
        StringBuilder string = removeInvalidLines(path);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(string.toString());//transformJsonString(string.toString()));
        return (JSONArray) obj;
    }
    public static JSONObject getObject(String path) throws ParseException {
        StringBuilder string = removeInvalidLines(path);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(string.toString());//transformJsonString(string.toString()));
        return (JSONObject) obj;
    }
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
        writer.println(transformJsonString(json.toJSONString()));
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
