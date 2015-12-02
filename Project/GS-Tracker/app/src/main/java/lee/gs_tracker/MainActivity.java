package lee.gs_tracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.ExpandableListView;
import java.io.File;
import java.io.FileOutputStream;
import android.content.Context;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Scanner;

import java.util.ArrayList;

import lee.gs_tracker.Starcraft.StarcraftCredentials;
import lee.gs_tracker.WoW.WoWCredentials;
import lee.gs_tracker.gsCustom.GSCustom;

public class MainActivity extends AppCompatActivity implements
      ExpandableListView.OnChildClickListener {
    private ExpandableListView mExpandableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mExpandableList = (ExpandableListView) findViewById(R.id.gameStatContent);

        ArrayList<Parent> arrayParents = new ArrayList<Parent>();
        ArrayList<String> arrayChildren = new ArrayList<String>();
        ArrayList<Child> customChildren = new ArrayList<Child>();

        int i;
        arrayParents.add(Initializer.initialize_Blizz());

        Parent Custom = new Parent();
        Custom.setTitle("Custom");
        Child CustomChild = new Child();
        CustomChild.ChildName = "Custom";
        CustomChild.Title = "Custom Templates";

        customChildren.add(CustomChild);

        Custom.setArrayChildren(customChildren);

        arrayParents.add(Custom);

        //sets the adapter that provides data to the list.
        mExpandableList.setAdapter(new MyCustomAdapter(MainActivity.this, arrayParents));
        mExpandableList.setOnChildClickListener(this);
    }
    public File createFile(String FileName, String Data){

        File file = new File(getFilesDir(),FileName);
        FileOutputStream outputStream;
        String string = "";
        try {
            outputStream = openFileOutput(FileName, Context.MODE_PRIVATE);
            outputStream.write(Data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id)
    {
        String TagName = view.getTag().toString();

        if(TagName == "WoW") {
            File file = getBaseContext().getFileStreamPath("WoWUser.txt");
            if(file.exists()){       //if a WoW is is already in, grab it

                try {
                    String data = new Scanner(file).next();
                    JSONObject User = (JSONObject)JSONValue.parse(data);
                    startActivity(new WoWCredentials().goToTemplate(this, User.get("Server").toString(), User.get("CharName").toString()));

                }
                catch(Exception E){
                    file.delete(); //bad user
                    E.printStackTrace();
                }

            }
            else{
                Intent intent = new Intent(this, WoWCredentials.class);
                startActivity(intent);
            }

        }
        else if(TagName == "StarcraftII"){
            File file = getBaseContext().getFileStreamPath("StarCraftUser.txt");
            if(file.exists()){
                try{
                    String data = new Scanner(file).next();
                    JSONObject User = (JSONObject)JSONValue.parse(data);
                    startActivity(new StarcraftCredentials().goToTemplate(this, User.get("ID").toString(), User.get("ProfileName").toString()));
                }
                catch(Exception E){
                    file.delete();
                    E.printStackTrace();
                }
            }
            else {
                Intent intent = new Intent(this, StarcraftCredentials.class);
                startActivity(intent);
            }
        }
        else if(TagName == "Custom"){
            Intent intent = new Intent(this, GSCustom.class);
            startActivity(intent);
        }
        return true;
    }

    public void writeToFile(String FileName, String Data){
        FileOutputStream Writer;

        try
        {
            //File root = Environment.getExternalStorageDirectory();
            //File file = new File(root, "WoWUser");
            //Writer = new FileOutputStream(FileName);
            if(new File(FileName).exists()){
                System.out.println("HI");
            }
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
}

