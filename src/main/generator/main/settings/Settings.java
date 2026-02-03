package main.settings;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Settings {
    public static ReentrantLock lock = new ReentrantLock(false);

    public static ReentrantLock baseShipSettings_lock = new ReentrantLock(false);
    public static ReentrantLock shipSettings_lock = new ReentrantLock(false);
    public static ReentrantLock allowRestricted_lock = new ReentrantLock(false);
    public static ReentrantLock buildRestricted_lock = new ReentrantLock(false);
    public static ReentrantLock forceExclude_lock = new ReentrantLock(false);
    //public static ReentrantLock settingsJSon_lock = new ReentrantLock(false);

    private static ManufacturerSettings baseShipSettings;
    private static ArrayList<ManufacturerSettings> shipSettings = new ArrayList<>();
    private static ArrayList<String> allowRestricted = new ArrayList<>();
    private static boolean buildRestricted;
    private static ArrayList<String> forceExclude = new ArrayList<>();

    //private static ArrayList<JSONObject> settingsJSon = new ArrayList<>();
    public static void getAllSettings(){
        //this will create a new thread to get the relevant settings.
    }

    public static synchronized ReentrantLock getAllowRestricted_lock() {
        return allowRestricted_lock;
    }

    public static synchronized ReentrantLock getBaseShipSettings_lock() {
        return baseShipSettings_lock;
    }

    public static synchronized ReentrantLock getShipSettings_lock() {
        return shipSettings_lock;
    }

    public static synchronized ReentrantLock getBuildRestricted_lock() {
        return buildRestricted_lock;
    }

    public static synchronized ReentrantLock getForceExclude_lock() {
        return forceExclude_lock;
    }

    /*public static synchronized ReentrantLock getSettingsJSon_lock() {
        return settingsJSon_lock;
    }*/

    public static ArrayList<ManufacturerSettings> getShipSettings() {
        getShipSettings_lock().lock();
        ArrayList<ManufacturerSettings> out = shipSettings;
        getShipSettings_lock().unlock();
        return out;
    }
    public static ManufacturerSettings getBaseShipSettings() {
        getBaseShipSettings_lock().lock();
        ManufacturerSettings out = baseShipSettings;
        getBaseShipSettings_lock().unlock();
        return out;
    }
    public static ArrayList<String> getAllowRestricted() {
        getAllowRestricted_lock().lock();
        ArrayList<String> out = allowRestricted;
        getAllowRestricted_lock().unlock();
        return out;
    }
    public static ArrayList<String> getForceExclude() {
        getForceExclude_lock().lock();
        ArrayList<String> out = forceExclude;
        getForceExclude_lock().unlock();
        return out;
    }
    public static boolean isBuildRestricted() {
        getBuildRestricted_lock().lock();
        boolean out = buildRestricted;
        getBuildRestricted_lock().unlock();
        return out;
    }

    /*public static ArrayList<JSONObject> getSettingsJSon() {
        getSettingsJSon_lock().lock();
        ArrayList<JSONObject> out =  settingsJSon;
        getSettingsJSon_lock().unlock();
        return out;
    }*/

    public static void addAllowedRestricted(String in){
        getAllowRestricted_lock().lock();
        allowRestricted.add(in);
        getAllowRestricted_lock().unlock();
    }
    public static void setBaseShipSettings(ManufacturerSettings in){
        getBaseShipSettings_lock().lock();
        baseShipSettings = in;
        getBaseShipSettings_lock().unlock();
    }
    public static void addShipSettings(ManufacturerSettings in){
        getAllowRestricted_lock().lock();
        shipSettings.add(in);
        getAllowRestricted_lock().unlock();
    }
    public static void setForceExclude(String in) {
        getForceExclude_lock().lock();
        forceExclude.add(in);
        getForceExclude_lock().unlock();
    }
    public static void setBuildRestricted(boolean in) {
        getBuildRestricted_lock().lock();
        buildRestricted = in;
        getBuildRestricted_lock().unlock();
    }
    /*public static void addSettingsJson(JSONObject in) {
        getSettingsJSon_lock().lock();
        settingsJSon.add(in);
        getSettingsJSon_lock().unlock();
    }*/
}
