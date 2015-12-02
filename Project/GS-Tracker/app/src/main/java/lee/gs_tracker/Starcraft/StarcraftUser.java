package lee.gs_tracker.Starcraft;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;

import lee.gs_tracker.MainActivity;
import lee.gs_tracker.R;
import lee.gs_tracker.WoW.WoWCredentials;


public class StarcraftUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String From = intent.getStringExtra(StarcraftCredentials.EXTRA_MESSAGE);
        JSONParser Parser = new JSONParser();
        JSONObject User;




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starcraft_user);

        try {
            Object obj = Parser.parse(From);
            User = (JSONObject)(obj);   //get JSONObject for this user
            //JSONObject userStats = (JSONObject) (obj);

            setFields(User);

        }
        catch(Exception E){
            User = null;
        }

    }

    public void setFields(JSONObject User){
        JSONObject Career = StarcraftAPIUser.getCareer(User);
        String PrimaryRace = StarcraftAPIUser.getPrimaryRace(Career);
                ((TextView) findViewById(R.id.profileText)).setText((StarcraftAPIUser.getProfileName(User)));
        ((TextView) findViewById(R.id.textView12)).setText((StarcraftAPIUser.getPrimaryRace(Career)));
        ((TextView) findViewById(R.id.textView14)).setText((StarcraftAPIUser.getTerranWins(Career)));
        ((TextView) findViewById(R.id.textView25)).setText((StarcraftAPIUser.getprotossWins(Career)));
        ((TextView) findViewById(R.id.textView15)).setText((StarcraftAPIUser.getzergWins(Career)));
        ((TextView) findViewById(R.id.textView27)).setText((StarcraftAPIUser.getSoloRank(Career)));
        ((TextView) findViewById(R.id.textView28)).setText((StarcraftAPIUser.getTeamRank(Career)));
        ((TextView) findViewById(R.id.totalGamesEdit)).setText((StarcraftAPIUser.getTotalGames(Career)));

        if(PrimaryRace.equals("TERRAN")){
            ((ImageView) findViewById(R.id.raceView)).setImageResource(R.drawable.terran);
        }
        else if(PrimaryRace.equals("PROTOSS")){
            ((ImageView) findViewById(R.id.raceView)).setImageResource(R.drawable.protoss);
        }
        else{
            ((ImageView) findViewById(R.id.raceView)).setImageResource(R.drawable.zerg);
        }


    }

    public void deleteTemplate(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Saved User?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                File file = getBaseContext().getFileStreamPath("StarCraftUser.txt");
                file.delete();
                Intent intent = new Intent(StarcraftUser.this, MainActivity.class);
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
                File file = getBaseContext().getFileStreamPath("StarCraftUser.txt");
                file.delete();
                Intent intent = new Intent(StarcraftUser.this, StarcraftCredentials.class);
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
}
