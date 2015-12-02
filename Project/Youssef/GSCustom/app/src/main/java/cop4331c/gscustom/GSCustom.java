package cop4331c.gscustom;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import android.app.AlertDialog.Builder;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Random;

public class GSCustom extends AppCompatActivity implements View.OnClickListener,
      QuickQuestionDialog.Listener
{
   public static GSCustom instance;
   /**
    * ATTENTION: This was auto-generated to implement the App Indexing API.
    * See https://g.co/AppIndexing/AndroidStudio for more information.
    */
   private GoogleApiClient client;

   ListView gameList = null;
   GameListAdapter gameListAdapter = null;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      instance = this;
      CustomData.SetContext(this);
      CustomData.ReadDataInit();
      //CustomData.NewData();

      setContentView(R.layout.activity_gscustom);

      gameList = (ListView) findViewById(R.id.gamelist);
      gameListAdapter = new GameListAdapter();
      gameList.setAdapter(gameListAdapter);
      gameList.setOnItemClickListener(gameListAdapter);
      gameList.setOnItemLongClickListener(gameListAdapter);
      gameList.setDrawingCacheBackgroundColor(GSCustom.instance.getResources()
                                                    .getColor(R.color.colorPrimary));

      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

      fab.setOnClickListener(
            new View.OnClickListener()
            {
               @Override
               public void onClick(View view)
               {
                  AddGameDialog d = new AddGameDialog();
               }
            }
      );
      // ATTENTION: This was auto-generated to implement the App Indexing API.
      // See https://g.co/AppIndexing/AndroidStudio for more information.
      client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
   }

   @Override
   public void onClick(View v)
   {

   }

   @Override
   protected void onDestroy()
   {
      super.onDestroy();
      CustomData.WriteData();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_gscustom, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      //noinspection SimplifiableIfStatement
      if (id == R.id.gamelist_deleteall)
      {
         new QuickQuestionDialog(this, "deleteall", this,
                                      "Are you sure you want to delete all profiles?",
                                      "confirmation...", new String[]{"yes", "no"},
                                      new int[]{1, -1});
         return true;
      }

      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onStart()
   {
      super.onStart();
      /*
      if (!CustomData.ReadDataInit())
         CustomData.NewData();
      */

      // ATTENTION: This was auto-generated to implement the App Indexing API.
      // See https://g.co/AppIndexing/AndroidStudio for more information.
      client.connect();
      Action viewAction = Action.newAction(
            Action.TYPE_VIEW, // TODO: choose an action type.
            "GSCustom Page", // TODO: Define a title for the content shown.
            // TODO: If you have web page content that matches this app activity's content,
            // make sure this auto-generated web page URL is correct.
            // Otherwise, set the URL to null.
            Uri.parse("http://host/path"),
            // TODO: Make sure this auto-generated app deep link URI is correct.
            Uri.parse("android-app://cop4331c.gscustom/http/host/path")
      );
      AppIndex.AppIndexApi.start(client, viewAction);
   }

   @Override
   public void onStop()
   {
      super.onStop();
      //CustomData.WriteData();

      // ATTENTION: This was auto-generated to implement the App Indexing API.
      // See https://g.co/AppIndexing/AndroidStudio for more information.
      Action viewAction = Action.newAction(
            Action.TYPE_VIEW, // TODO: choose an action type.
            "GSCustom Page", // TODO: Define a title for the content shown.
            // TODO: If you have web page content that matches this app activity's content,
            // make sure this auto-generated web page URL is correct.
            // Otherwise, set the URL to null.
            Uri.parse("http://host/path"),
            // TODO: Make sure this auto-generated app deep link URI is correct.
            Uri.parse("android-app://cop4331c.gscustom/http/host/path")
      );
      AppIndex.AppIndexApi.end(client, viewAction);
      client.disconnect();
   }

   @Override
   public void onQuickQuestionDialogClick(String name, int response, Object tag)
   {
      Toast.makeText(this, "name: \"" + name + "\", response: " + response + ", tag: " +
                           (tag != null ? tag.toString() : "null"), Toast.LENGTH_SHORT).show();
      if (name.equals("deleteone") && response == 1 && tag != null)
      {
         CustomData.RemoveProfile(((Integer)tag).intValue());
         gameListAdapter.notifyDataSetInvalidated();
         gameListAdapter.notifyDataSetChanged();
         findViewById(R.id.gscustom_root).postInvalidate();
      }
      else if (name.equals("deleteall") && response == 1)
      {
         CustomData.RemoveAll();
         gameListAdapter.notifyDataSetInvalidated();
         gameListAdapter.notifyDataSetChanged();
         findViewById(R.id.gscustom_root).postInvalidate();
      }
   }

   class GameListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener,
         AdapterView.OnItemLongClickListener
   {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id)
      {
         CustomData.SetActiveProfile(position);
         Intent intent = null;
         if (CustomData.GetProfileType(position) == CustomData.SPORTS)
            intent = new Intent(GSCustom.instance, Sports.class);
         else if (CustomData.GetProfileType(position) == CustomData.RPG)
            intent = new Intent(GSCustom.instance, RpgNotes.class);
         else if (CustomData.GetProfileType(position) == CustomData.SHOOTER)
            intent = new Intent(GSCustom.instance, Shooter.class);
         if (intent != null)
         {
            intent.putExtra("gamename", CustomData.GetProfileName(position));
            intent.putExtra("gametype", CustomData.GetProfileType(position));
            GSCustom.instance.startActivity(intent);
            CustomData.WriteData();
         }
      }

      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
      {
         new QuickQuestionDialog(
               GSCustom.instance, "deleteone", GSCustom.instance,
               "Are you sure you want to delete " +
                  CustomData.GAMETYPE[CustomData.GetProfileType(position)] +
                     " profile, \"" + CustomData.GetProfileName(position) + "\"?", "confirmation",
                                          new String[]{"Yes", "No"}, new int[]{1, -1},
                                                   new Integer[]{position, position});
         return true;
      }

      @Override
      public int getCount()
      {
         return CustomData.GetNumProfiles();
      }

      @Override
      public Object getItem(int position)
      {
         return position >= 0 && position < CustomData.GetNumProfiles() ? position : null;
      }

      @Override
      public long getItemId(int position)
      {
         return position;
      }

      @Override
      public boolean hasStableIds()
      {
         return true;
      }

      @Override
      public boolean isEmpty()
      {
         return CustomData.GetNumProfiles() == 0;
      }

      @Override
      public boolean areAllItemsEnabled()
      {
         return true;
      }

      @Override
      public int getItemViewType(int position)
      {
         return 0;
      }

      @Override
      public int getViewTypeCount()
      {
         return 1;
      }

      @Override
      public boolean isEnabled(int position)
      {
         return true;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent)
      {
         if (position < 0 || position >= CustomData.GetNumProfiles())
         {
            Toast.makeText(GSCustom.this, "getItemViewType position " + position + " out" +
                                 " of bounds", Toast.LENGTH_LONG).show();
            TextView t = new TextView(GSCustom.instance);
            t.setLayoutParams(
                  new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                  )
            );
            t.setBackgroundColor(GSCustom.instance.getResources().getColor(R.color.colorPrimary));
            t.setText("position " + position + " out of bounds");
            return t;
         }
         LinearLayout ll = (LinearLayout) convertView;

         if (ll == null)
            ll = (LinearLayout) getLayoutInflater().inflate(R.layout.gamelist_gameentry, null);
         ImageView iv = (ImageView) ll.getChildAt(0);
         TextView tv = (TextView) ll.getChildAt(1);

         ll.setDrawingCacheBackgroundColor(GSCustom.instance.getResources()
                                                 .getColor(R.color.colorPrimary));

         if (CustomData.GetProfileType(position) == CustomData.RPG)
            iv.setImageResource(R.drawable.ic_rpg);
         else if (CustomData.GetProfileType(position) == CustomData.SHOOTER)
            iv.setImageResource(R.drawable.ic_shooter);
         else if (CustomData.GetProfileType(position) == CustomData.SPORTS)
            iv.setImageResource(R.drawable.ic_sports);
         tv.setText(CustomData.GetProfileName(position));

         return ll;
      }
   }
}