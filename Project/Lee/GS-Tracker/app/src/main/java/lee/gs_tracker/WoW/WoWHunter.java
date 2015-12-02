package lee.gs_tracker.WoW;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.*;

import java.io.File;

import lee.gs_tracker.DownloadImageTask;
import lee.gs_tracker.MainActivity;
import lee.gs_tracker.R;
import android.content.DialogInterface.OnClickListener;

public class WoWHunter extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String From = intent.getStringExtra(WoWCredentials.EXTRA_MESSAGE);
        JSONParser Parser = new JSONParser();
        JSONObject User;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wo_whunter);

        try {
            Object obj = Parser.parse(From);
            User = (JSONObject)(obj);   //get JSONObject for this user
            obj = Parser.parse(WoWAPIUser.getStats(User).toString());
            JSONObject userStats = (JSONObject) (obj);

            new DownloadImageTask((ImageView) findViewById(R.id.imageView)) //set thumbnail and fields for Rogue
                    .execute(WoWAPIUser.getCharPic(User));
            setFields(User, userStats);


        }
        catch(Exception E){
            User = null;
        }
    }


        //set max range and pet health for hunter

    public void setFields(JSONObject User, JSONObject userStats){
        Object UserStats = WoWAPIUser.getStats(User);

        ((TextView) findViewById(R.id.nameText)).setText(WoWAPIUser.getCharName(User));
        ((TextView)findViewById(R.id.serverText)).setText(WoWAPIUser.getServer(User));


        ((TextView)findViewById(R.id.levelEdit)).setText(WoWAPIUser.getLevel(User));
        ((TextView)findViewById(R.id.healthEdit)).setText(WoWAPIUser.getHealth(userStats));
        ((TextView)findViewById(R.id.strengthEdit)).setText(WoWAPIUser.getStrength(userStats));
        ((TextView)findViewById(R.id.agilityEdit)).setText(WoWAPIUser.getAgility(userStats));
        ((TextView)findViewById(R.id.intellectEdit)).setText(WoWAPIUser.getIntellect(userStats));
        ((TextView)findViewById(R.id.staminaEdit)).setText(WoWAPIUser.getStamina(userStats));
        ((TextView)findViewById(R.id.energyEdit)).setText(WoWAPIUser.getEnergy(userStats)); //change
        ((TextView)findViewById(R.id.dpsEdit)).setText(WoWAPIUser.getDPS(userStats)); //change
        ((TextView)findViewById(R.id.specEdit)).setText(WoWAPIUser.getSpec(User));

        Spinner dropdown = (Spinner)findViewById(R.id.armorList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, WoWAPIUser.generateItemList(User));
        dropdown.setAdapter(adapter);


        Spinner dd = (Spinner)findViewById(R.id.talentList);
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, WoWAPIUser.generateTalentList(User));
        dd.setAdapter(adapt);
        //continue here


    }

    public static ArrayList<String> buildPetList(JSONObject User){
        JSONArray petArray = WoWAPIUser.getHunterPets(User);
        org.json.JSONObject pet;
        ArrayList<String> petList = new ArrayList<>();
        int i = 0;
        try{
            for(i=0; i<petArray.length(); i++){
                pet = (org.json.JSONObject)petArray.get(i);
                petList.add(pet.get("name").toString());
            }
            return petList;
        }
        catch(Exception e){
            return null;
        }


    }
    public void deleteTemplate(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Saved User?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                File file = getBaseContext().getFileStreamPath("WoWUser.txt");
                file.delete();
                Intent intent = new Intent(WoWHunter.this, MainActivity.class);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void resetTemplate(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Saved User?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                File file = getBaseContext().getFileStreamPath("WoWUser.txt");
                file.delete();
                Intent intent = new Intent(WoWHunter.this, WoWCredentials.class);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wo_whunter, menu);
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
