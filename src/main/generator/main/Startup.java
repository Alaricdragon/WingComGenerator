package main;

import main.processers.CustomJSonReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;

public class Startup {
    /*/public static void main (String [] args) throws IOException, InterruptedException, URISyntaxException, ParseException {
        //PrintStream con=new PrintStream();
        //System.setOut(con);
        //System.setErr(con);
        Console console = System.console();
        //console;
        if(console == null && !GraphicsEnvironment.isHeadless()){
            System.out.println("started to attempt to open thing...");
            String filename = Startup.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            filename = filename.replaceAll("%20"," ");
            Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar \"" + filename + "\""});
            System.out.println("started to attempt to open thing (2)...");
        }else{
            System.out.println("prepared to open thing...");
            Main.main(new String[0]);
            System.out.println("Program has ended, please type 'exit' to close the console");
        }
        Main.main(new String[0]);
    }/**/
    /**/public static void main(String[] args) throws Exception {
        try {
            if (System.console() == null) {
                String jarPath = Startup.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
                //issues: this cannot comprehend spaces.
                System.out.println("running checking path...");
                jarPath = jarPath.replaceAll("%20", " ");
                System.out.println("got path as: " + jarPath);
                if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    Runtime.getRuntime().exec(new String[]{
                            "osascript", "-e",
                            "tell application \"Terminal\" to do script \"java -jar '" + jarPath + "'\""
                    });
                } else if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    Runtime.getRuntime().exec("cmd /c start cmd /k java -jar \"" + jarPath + "\"");
                }
            }
        }catch (Exception e){
            JSONObject out = new JSONObject();
            out.put("error of:",e.toString());
            out.put("time of: ",System.currentTimeMillis());
            CustomJSonReader.writeJsonFile("failedToRun.json",out);
            throw e;
        }
        System.out.println("running data...");
        Main.main(new String[0]);
        System.out.println("completely, 100% finished generating wingcom units (unless there was a critical error, you should be good to play the game now). Also dear god what the hell is up with the .json files in starsector? I know its been around for a while, but a few of the issues I had reading them shocked me. like 'id' in .faction files not needing \"\" around it. or the fact that some varubles are marked as 'f', but like only twice? or that one mod file were it has '' instead of \"\" over one varuble. \n alex defintly did not use the simple json libary to handle this. in fact, I bet he made his own to handle this. respect to him, but it did make this really hard. exspecaly for my first time managing a bunch of random data from all sorts of files like this. I felt like I was compleatly crazy. \n anyways, I am dont complaining. go play starsector. you earned it >=( (angry face because I am still grumpy. I will feel better soon, and I hope you feel well as well.)");
        System.out.println("\n\n Done. type 'exit' to close the terminal.");
        System.exit(0);
    }/**/
}
