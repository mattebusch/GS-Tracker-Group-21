package lee.gs_tracker;


import android.os.StrictMode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class APIConnection {

    public static JSONObject sentGet(String InputURL) {
        try {   //create the API call using the user input
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
}
