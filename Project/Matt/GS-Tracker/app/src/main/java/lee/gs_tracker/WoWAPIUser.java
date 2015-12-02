package lee.gs_tracker;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.*;
import org.json.JSONTokener;

import java.io.BufferedInputStream;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.renderscript.ScriptGroup;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    public static Bitmap getCharPic(org.json.simple.JSONObject User){
        String url = "http://render-api-us.worldofwarcraft.com/static-render/us/";
        String thumbnail = User.get("thumbnail").toString();
        url = url + thumbnail;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();
            //InputStream ImageStream = ((InputStream)new URL(url));
            Bitmap x = BitmapFactory.decodeStream(input);
            //Drawable Image = Drawable.createFromStream(ImageStream, url);
            return x;

        }
        catch(Exception E){
            String Dupe = E.getMessage();
            //System.out.println(E.toString());
            return null;
        }

        /*
        HttpURLConnection connection = (HttpURLConnection)new URL(url) .openConnection();
    connection.setRequestProperty("User-agent","Mozilla/4.0");

    connection.connect();
    InputStream input = connection.getInputStream();

         */
    }
}