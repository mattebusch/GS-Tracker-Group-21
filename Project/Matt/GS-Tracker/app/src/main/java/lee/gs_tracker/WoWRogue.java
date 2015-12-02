package lee.gs_tracker;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.ImageView;

import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import android.media.Image;
//import org.json.JSONObject;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import com.koushikdutta.ion.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;



public class WoWRogue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        String From = intent.getStringExtra(WoWCredentials.EXTRA_MESSAGE);
        JSONParser Parser = new JSONParser();
        JSONObject User;
        try {
            Object obj = Parser.parse(From);
            User = (JSONObject)(obj);   //get JSONObject for this user
        }
        catch(Exception E){
            User = null;
        }


        super.onCreate(savedInstanceState);
        ImageView image = (ImageView)findViewById(R.id.imageView);
        //image.setImageDrawable(WoWAPIUser.getCharPic(User));
        //((ImageView)view).setImageBitmap(BitmapFactory.decodeFile("/data/data/com.myapp/files/someimage.jpg"));
        //image.setImageBitmap(BitmapFactory.decodeFile("/Users/Lee/Documents/Skool/COP 4331/Project/GS-Tracker-Group-21/Project/Lee/GS-Tracker/app/libs/icon.jpg"));
        //Ion.with(image).load("icon.jpg");
        //image.setImageBitmap(WoWAPIUser.getCharPic(User));
        setContentView(R.layout.activity_wo_wrogue);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wo_wrogue, menu);
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
