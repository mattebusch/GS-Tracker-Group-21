package lee.gs_tracker;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import android.app.Activity;
import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.app.Activity;

import java.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Lee on 11/21/15.
 * Use this class to create and manipulate JSON Files
 */
public class InternalData extends MainActivity{        //creates a new file and saves internally

    public File createFile(String FileName, File path){

        File file = new File(path,FileName);
        FileOutputStream outputStream;
        String string = "";
        try {
            outputStream = openFileOutput(FileName, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    public void writeToFile(String FileName, String Data){
        FileOutputStream Writer;

        try {
            //File root = Environment.getExternalStorageDirectory();
            //File file = new File(root, "WoWUser");
            //Writer = new FileOutputStream(FileName);
            Writer = openFileOutput(FileName, Context.MODE_PRIVATE);
            //Writer = context.openFileOutput("WoWUser", Context.MODE_PRIVATE);
            Writer.write(Data.getBytes());
            Writer.close();

        }
        catch(Exception E)
        {
            E.printStackTrace();

        }

    }

    public String getFileContents(String FileName){  //gets all the contents of the file to use
        try
        {
            FileInputStream input = openFileInput(FileName);
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
        catch(Exception E){
            E.printStackTrace();
        }
        return null;
    }



}
