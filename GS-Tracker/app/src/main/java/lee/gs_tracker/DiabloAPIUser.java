package lee.gs_tracker;

/**
 * Created by Matt on 11/8/2015.
 */

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

public class DiabloAPIUser {

    public static String btag;
    public static org.json.JSONObject Obj, charObj;

    public static void UseAPI() throws Exception{
        String InputURL = "https://us.api.battle.net/d3/profile/" + btag + "/?locale=en_US&apikey=dzdyu73w47us458g5h89grjqgfs4ctpw";
        Obj = WoWAPIUser.sentGet(InputURL);//should work even though it's in the WOWAPI class

    }

    public static JSONArray getCharArray()throws Exception {
        JSONArray charArray = Obj.getJSONArray("heroes");
        return charArray;
    }

    public static JSONArray getActiveSkillArray() throws Exception{
        org.json.JSONObject skillArray = charObj.getJSONObject("skills");
        JSONArray active = skillArray.getJSONArray("active");
        return active;
    }

    public static JSONArray getPassiveSkillArray() throws Exception{
        org.json.JSONObject skillArray = charObj.getJSONObject("skills");
        JSONArray active = skillArray.getJSONArray("passive");
        return active;
    }

    public static void UseCharAPI(int i) throws Exception{
        JSONArray charArray = DiabloAPIUser.getCharArray();
        JSONObject character = charArray.getJSONObject(i);
        String sid = character.get("id").toString();
        int id = Integer.parseInt(sid);
        String InputURL = "https://us.api.battle.net/d3/profile/" + btag + "/hero/" + id + "?locale=en_US&apikey=dzdyu73w47us458g5h89grjqgfs4ctpw";
        charObj = WoWAPIUser.sentGet(InputURL);
    }



}
