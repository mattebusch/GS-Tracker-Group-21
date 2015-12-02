package lee.gs_tracker.WoW;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.JSONTokener;
import java.util.ArrayList;

import java.io.BufferedInputStream;
import java.util.Iterator;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.renderscript.ScriptGroup;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.net.Uri;
import java.net.*;
import java.lang.Object;
import android.media.Image;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;




import javax.net.ssl.HttpsURLConnection;



public class WoWAPIUser {
    public static JSONObject sentGet(String InputURL) {
        try {   //create the API call using the user input
            //"https://us.api.battle.net/wow/character/" + ServerInput + "/" + UserInput + "?fields=stats&locale=en_US&apikey=bmhx5s3efzdhghwvjpr778zhg4a6yhnd
            URL url = new URL(InputURL);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection(); //open the connection
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder()  //had to put this to allow an api call from main
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); //get the response stream

            String inputLine;
            StringBuffer response = new StringBuffer();


            while ((inputLine = in.readLine()) != null) {  //put the response stream into a string
                response.append(inputLine);
            }

            in.close();

            JSONObject Request = new JSONObject(response.toString()); //create a new JSON object from that string it is parsed


            return Request;  //return the JSON object
        } catch (Exception e) {
            return null;
        }

    }

    public static String getCharName(org.json.simple.JSONObject User){
        try {
            return User.get("name").toString();

        }

        catch(Exception E){
            E.printStackTrace();
            return "ERROR";
        }
    }
    public static String getServer(org.json.simple.JSONObject User){
        try{
            return User.get("realm").toString();
        }
        catch(Exception E){
            return "ERROR";
        }
    }

    public static String getLevel(org.json.simple.JSONObject User){
        try{
            return User.get("level").toString();
        }
        catch(Exception E){
            return "ERROR";
        }
    }

    public static String getHealth(org.json.simple.JSONObject userStats){
        try{
            org.json.simple.JSONObject stats = (org.json.simple.JSONObject)userStats.get("stats");
            return stats.get("health").toString();
        }
        catch(Exception E){
            return "ERROR";
        }

    }

    public static String getEnergy(org.json.simple.JSONObject userStats){
        try{
            org.json.simple.JSONObject stats = (org.json.simple.JSONObject)userStats.get("stats");
            return stats.get("power").toString();
        }
        catch(Exception E){
            return "ERROR";
        }

    }

    public static String getDPS(org.json.simple.JSONObject userStats){
        try{
            org.json.simple.JSONObject stats = (org.json.simple.JSONObject)userStats.get("stats");
            return stats.get("mainHandDps").toString();
        }
        catch(Exception E){
            return "ERROR";
        }

    }

    public static String getStrength(org.json.simple.JSONObject userStats){
        try{
            org.json.simple.JSONObject stats = (org.json.simple.JSONObject)userStats.get("stats");
            return stats.get("str").toString();
        }
        catch(Exception E){
            return "ERROR";
        }

    }

    public static String getAgility(org.json.simple.JSONObject userStats){
        try{
            org.json.simple.JSONObject stats = (org.json.simple.JSONObject)userStats.get("stats");
            return stats.get("agi").toString();
        }
        catch(Exception E){
            return "ERROR";
        }

    }

    public static String getIntellect(org.json.simple.JSONObject userStats){
        try{
            org.json.simple.JSONObject stats = (org.json.simple.JSONObject)userStats.get("stats");
            return stats.get("int").toString();
        }
        catch(Exception E){
            return "ERROR";
        }

    }

    public static String getStamina(org.json.simple.JSONObject userStats){
        try{
            org.json.simple.JSONObject stats = (org.json.simple.JSONObject)userStats.get("stats");
            return stats.get("sta").toString();
        }
        catch(Exception E){
            return "ERROR";
        }

    }

    public static String[] generateItemList(org.json.simple.JSONObject User){
        String[] itemList = new String[17];
        int i = 0;
        JSONObject userItems = getItems(User);

        //check if the user is wearing each armor for body type, if yes, add to list

        try{
            userItems.getJSONObject("head");
            itemList[i] = "Head: " + userItems.getJSONObject("head").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Head: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("neck");
            itemList[i] = "Neck: " + userItems.getJSONObject("neck").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Neck: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("shoulder");
            itemList[i] = "Shoulder: " + userItems.getJSONObject("shoulder").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Shoulder: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("back");
            itemList[i] = "Back: " + userItems.getJSONObject("back").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Back: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("chest");
            itemList[i] = "Chest: " + userItems.getJSONObject("chest").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Chest: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("shirt");
            itemList[i] = "Shirt: " + userItems.getJSONObject("shirt").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Shirt: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("wrist");
            itemList[i] = "Wrist: " + userItems.getJSONObject("wrist").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Wrist: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("hands");
            itemList[i] = "Hands: " + userItems.getJSONObject("hands").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Hands: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("waist");
            itemList[i] = "Waist: " + userItems.getJSONObject("waist").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Waist: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("legs");
            itemList[i] = "Legs: " + userItems.getJSONObject("legs").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Legs: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("feet");
            itemList[i] = "Feet: " + userItems.getJSONObject("feet").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Feet: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("finger1");
            itemList[i] = "Finger 1: " + userItems.getJSONObject("finger1").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Finger 1: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("finger2");
            itemList[i] = "Finger 2: " + userItems.getJSONObject("finger2").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Finger 2: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("trinket1");
            itemList[i] = "Trinket 1: " + userItems.getJSONObject("trinket1").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Trinket 1: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("trinket2");
            itemList[i] = "Trinket 2: " + userItems.getJSONObject("trinket2").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Trinket 2: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("mainHand");
            itemList[i] = "Main Hand: " + userItems.getJSONObject("mainHand").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Main Hand: Empty";
        }
        i++;

        try{
            userItems.getJSONObject("offHand");
            itemList[i] = "Off Hand: " + userItems.getJSONObject("offHand").get("name").toString();
        }
        catch(Exception e){
            itemList[i] = "Off Hand: Empty";
        }
        i++;

        return itemList;
    }

    public static String getSpec(org.json.simple.JSONObject User){
        JSONParser parser = new JSONParser();

        try{
            org.json.simple.JSONArray talents = (org.json.simple.JSONArray)(parser.parse(getTalents(User).toString()));
            org.json.simple.JSONObject specs = (org.json.simple.JSONObject)talents.get(0);
            org.json.simple.JSONObject spec = (org.json.simple.JSONObject) specs.get("spec");

            return spec.get("name").toString();
        }
        catch(Exception e){
            return "Empty";
        }

    }

    public static ArrayList<String> generateTalentList(org.json.simple.JSONObject User){
        JSONParser parser = new JSONParser();
        ArrayList<String> talentList = new ArrayList<>();
        int i = 0;
        try{
            org.json.simple.JSONArray talents = (org.json.simple.JSONArray)(parser.parse(getTalents(User).toString()));
            org.json.simple.JSONObject talent = (org.json.simple.JSONObject)talents.get(0);
            org.json.simple.JSONArray talentArray = (org.json.simple.JSONArray) talent.get("talents");
            for(i = 0; i < talentArray.size(); i++){
                org.json.simple.JSONObject newTalent = (org.json.simple.JSONObject)talentArray.get(i);
                org.json.simple.JSONObject spell = (org.json.simple.JSONObject)newTalent.get("spell");
                talentList.add(spell.get("name").toString());
            }

        }
        catch(Exception e){
            talentList.add("Empty");
            return talentList;
        }

        return talentList;
    }


    public static org.json.JSONObject getStats(org.json.simple.JSONObject User){
        return sentGet("https://us.api.battle.net/wow/character/" + getServer(User) + "/" + getCharName(User) + "?fields=stats&locale=en_US&apikey=bmhx5s3efzdhghwvjpr778zhg4a6yhnd");
    }

    public static org.json.JSONObject getItems(org.json.simple.JSONObject User) {
        try {
            return sentGet("https://us.api.battle.net/wow/character/" + getServer(User) + "/" + getCharName(User) + "?fields=items&locale=en_US&apikey=bmhx5s3efzdhghwvjpr778zhg4a6yhnd").getJSONObject("items");
        }
        catch(Exception e){
            return null;
        }
    }

    public static org.json.JSONArray getHunterPets(org.json.simple.JSONObject User){
        try{
            return sentGet("https://us.api.battle.net/wow/character/" + getServer(User) + "/" + getCharName(User) + "?fields=hunterPets&locale=en_US&apikey=bmhx5s3efzdhghwvjpr778zhg4a6yhnd").getJSONArray("hunterPets");

        }
        catch(Exception e){
            return null;
        }
    }

    public static org.json.JSONArray getTalents(org.json.simple.JSONObject User){
        try{
            return sentGet("https://us.api.battle.net/wow/character/" + getServer(User) + "/" + getCharName(User) + "?fields=talents&locale=en_US&apikey=bmhx5s3efzdhghwvjpr778zhg4a6yhnd").getJSONArray("talents");
        }
        catch(Exception e){
            return null;
        }
    }

    public static String getCharClass(JSONObject User){
       JSONObject Classes = sentGet("https://us.api.battle.net/wow/data/character/classes?locale=en_US&apikey=bmhx5s3efzdhghwvjpr778zhg4a6yhnd");
        try {
            String ClassID = User.get("class").toString();
            JSONArray ClassArray = Classes.getJSONArray("classes");
            int i;
            for(i = 0; i < ClassArray.length(); i++){
                if(ClassArray.getJSONObject(i).get("id").toString() == ClassID){
                    return ClassArray.getJSONObject(i).get("name").toString();
                }
            }
        }
        catch(Exception E) {
            return null;
        }
        return null;

    }

    public static String getCharPic(org.json.simple.JSONObject User){
        String url = "http://render-api-us.worldofwarcraft.com/static-render/us/";
        String thumbnail = User.get("thumbnail").toString();
        url = url + thumbnail;
        return url;

    }
}

