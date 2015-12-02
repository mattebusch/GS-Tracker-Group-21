package lee.gs_tracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.view.View;
import android.content.Intent;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class WoWCredentials extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "WoWRogue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wo_wcredentials);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wo_wcredentials, menu);
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

    public String getCharName(){
        EditText mEdit   = (EditText)findViewById(R.id.editText);
        String CharName = mEdit.getText().toString();
        return CharName;
    }

    public String getServerName(){
        EditText mEdit = (EditText)findViewById(R.id.editText2);
        String ServerName = mEdit.getText().toString();
        return ServerName;
    }


    public String getClassName(){  //returns the characters class to use the appropriate Template

        return null;
    }



    public void goToTemplate(View view){
        String InputURL = "https://us.api.battle.net/wow/character/" + getServerName() + "/" + getCharName() + "?fields=stats&locale=en_US&apikey=bmhx5s3efzdhghwvjpr778zhg4a6yhnd";
        org.json.JSONObject Obj = WoWAPIUser.sentGet(InputURL);
        String Class = WoWAPIUser.getCharClass(Obj);
        if(Class.equals("Rogue")){
            Intent intent = new Intent(this, WoWRogue.class);
            intent.putExtra(EXTRA_MESSAGE, Obj.toString());
            startActivity(intent);

        }

    }



}
