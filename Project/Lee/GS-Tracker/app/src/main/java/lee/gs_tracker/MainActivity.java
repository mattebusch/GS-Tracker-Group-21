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

import lee.gs_tracker.WoW.WoWCredentials;
import lee.gs_tracker.gsCustom.GSCustom;

public class MainActivity extends AppCompatActivity {
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
        Parent FPS = new Parent();
        FPS.setTitle("FPS");
        ArrayList<Child> FPSChildren = new ArrayList<>();
        customChildren.add(CustomChild);
        for (i = 0; i < 3; i++) {
            Child FPSChild = new Child();
            FPSChild.ChildName = "Template" + (i + 1);
            FPSChild.Title = "Template" + (i+1);
            FPSChildren.add(FPSChild);
            //FPSChildren.add("Template" + (i + 1));
        }

        FPS.setArrayChildren(FPSChildren);
        Custom.setArrayChildren(customChildren);
        arrayParents.add(FPS);
        arrayParents.add(Custom);

        //here we set the parents and the children
        /*for (int i = 0; i < 10; i++){
            //for each "i" create a new Parent object to set the title and the children
            Parent parent = new Parent();
            parent.setTitle("Parent " + i);

            arrayChildren = new ArrayList<String>();
            for (int j = 0; j < 10; j++) {
                arrayChildren.add("Child " + j);
            }
            parent.setArrayChildren(arrayChildren);

            //in this array we add the Parent object. We will use the arrayParents at the setAdapter
            arrayParents.add(parent);
        }*/

        //sets the adapter that provides data to the list.
        mExpandableList.setAdapter(new MyCustomAdapter(MainActivity.this, arrayParents));

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
    public void goToStat(View view){
        String TagName = view.getTag().toString();

        if(TagName == "WoW") {
            File file = getBaseContext().getFileStreamPath("WoWUser.txt");
            if(file.exists()){       //if a WoW is is already in, grab it
                //getBaseContext().getFileStreamPath(fname);
                //Object data = new InternalData().getFileContents("WoWUser.txt");
                try {
                    String data = new Scanner(file).next();
                    JSONObject User = (JSONObject)JSONValue.parse(data);
                    startActivity(new WoWCredentials().goToTemplate(this, User.get("Server").toString(), User.get("CharName").toString()));

                }
                catch(Exception E){
                    E.printStackTrace();
                }

            }
            else{
                Intent intent = new Intent(this, WoWCredentials.class);
                startActivity(intent);
                //createFile("WoWUser.txt");
            }

        }
        else if(TagName == "Custom"){
            Intent intent = new Intent(this, GSCustom.class);
            startActivity(intent);
        }


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

