package main.processers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
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
                    jsonContent.append(line2[0]).append("\n");
                }else {
                    //System.out.println(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }

        return jsonContent;
    }
    public static JSONArray getArray(String path) throws ParseException {
        StringBuilder string = removeInvalidLines(path);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(string.toString());
        return (JSONArray) obj;
    }
    public static JSONObject getObject(String path) throws ParseException {
        StringBuilder string = removeInvalidLines(path);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(string.toString());
        return (JSONObject) obj;
    }
}
