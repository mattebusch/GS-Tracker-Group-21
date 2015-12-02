package lee.gs_tracker.gsCustom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import lee.gs_tracker.R;

/**
 * Created by Joe Paul on 11/24/2015.
 *
 * creates a menu that enumerates all created profiles and mediates between the profile listings
 *    and the classes that manage each profile type
 */

public class GSCustom extends AppCompatActivity implements QuickQuestionDialog.Listener
{
   // static instance of active GSCustom object
   public static GSCustom instance;

   // Objects stored at class scope for convenience / necessity
   ListView gameList = null;
   GameListAdapter gameListAdapter = null;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      instance = this;
      CustomData.SetContext(this);
      CustomData.ReadDataInit();

      setContentView(R.layout.activity_gscustom);

      // initialize gameList
      gameList = (ListView) findViewById(R.id.gamelist);
      gameListAdapter = new GameListAdapter();
      gameList.setAdapter(gameListAdapter);
      gameList.setOnItemClickListener(gameListAdapter);
      gameList.setOnItemLongClickListener(gameListAdapter);

      // when deleting a game profile, used to leave a black box where the profile entry used
      //    to be in the list, suggestion online to use background color cache hinting did no
      //    good but left it anyway, ultimately solved by sending a PostInvalidate to the
      //    view containing the gameList (cause the gamelist has vertical LayoutParams of
      //    WRAP_CONTENT and so contracts when removing a view and the black box that remains
      //    is part of the parent view's ui)
      gameList.setDrawingCacheBackgroundColor(GSCustom.instance.getResources()
                                                    .getColor(R.color.colorPrimary));

      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      toolbar.setTitle(R.string.title_gs_custom);
      setSupportActionBar(toolbar);
      try
      {
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
         getSupportActionBar().setBackgroundDrawable(getResources()
                                                           .getDrawable(R.drawable.color_primary));
      }
      catch (Exception e)
      {
         e.printStackTrace();
         /*Toast.makeText(this, "failed to create back button;\n" + e.toString(), Toast.LENGTH_LONG)
               .show();*/
      }

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
   }

   // write out CustomData to disk
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

   // delete all is the only menu option
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
         // display confirmation, "deleteall" handled by onQuickQuestionDialogClick further down
         new QuickQuestionDialog(this, "deleteall", this,
                                      "Are you sure you want to delete all profiles?",
                                      "confirmation...", new String[]{"yes", "no"},
                                      new int[]{1, -1});
         return true;
      }

      return super.onOptionsItemSelected(item);
   }

   // handle deletions from confirmations by QuickQuestionDialog
   @Override
   public void onQuickQuestionDialogClick(String name, int response, Object tag)
   {
      /*Toast.makeText(this, "name: \"" + name + "\", response: " + response + ", tag: " +
                           (tag != null ? tag.toString() : "null"), Toast.LENGTH_SHORT).show();*/

      if (name.equals("deleteone") && response == 1 && tag != null) // delete one profile
      {
         CustomData.RemoveProfile(((Integer)tag).intValue());
         gameListAdapter.notifyDataSetInvalidated();     // update game list adapter
         gameListAdapter.notifyDataSetChanged();
         findViewById(R.id.gscustom_root).postInvalidate(); // upate game list ui
      }
      else if (name.equals("deleteall") && response == 1)   // delete all profiles
      {
         CustomData.RemoveAll();
         gameListAdapter.notifyDataSetInvalidated();
         gameListAdapter.notifyDataSetChanged();
         findViewById(R.id.gscustom_root).postInvalidate();
      }
   }

   // GameListAdapter populates the gameList
   class GameListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener,
         AdapterView.OnItemLongClickListener
   {
      // item click on a gameList item opens that item, makes a call to the activity
      //    corresponding to the profile type for that game
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
            // sends the game name and game type as arguments to the calling activity, gamename
            //    is used by each called activity to set as the title for that activity in the
            //    action bar
            intent.putExtra("gamename", CustomData.GetProfileName(position));
            intent.putExtra("gametype", CustomData.GetProfileType(position));
            GSCustom.instance.startActivity(intent);
            CustomData.WriteData();
         }
      }

      // on long click on a gameList item, display a QuickQuestionDialog confirmation for profile
      //    deletion
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

      // get count for total number of profiles from CustomData
      @Override
      public int getCount()
      {
         return CustomData.GetNumProfiles();
      }

      // not used, but must be overridden to implement BaseAdapter interface
      @Override
      public Object getItem(int position)
      {
         return position >= 0 && position < CustomData.GetNumProfiles() ? position : null;
      }

      // id of game profile in the listview is its position, corresponding to its position in
      //    the JSONArray CustomData.data
      @Override
      public long getItemId(int position)
      {
         return position;
      }

      // ? seems to work fine
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

      // type of view in the list (only 1 type of view - a gamelist profile)
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

      // creates the view for each gamelist profile entry
      @Override
      public View getView(int position, View convertView, ViewGroup parent)
      {
         // this never happened but here as a safeguard
         if (position < 0 || position >= CustomData.GetNumProfiles())
         {
            /*Toast.makeText(GSCustom.this, "getItemViewType position " + position + " out" +
                                 " of bounds", Toast.LENGTH_LONG).show();*/
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

         // black rectangle problem when view is removed, background caching, background hint,
         //    blah blah blah searching for a solution leaving harmless code artifacts
         ll.setDrawingCacheBackgroundColor(GSCustom.instance.getResources()
                                                 .getColor(R.color.colorPrimary));

         // set the picture corresponding to the profile type
         if (CustomData.GetProfileType(position) == CustomData.RPG)
            iv.setImageResource(R.drawable.ic_rpg);
         else if (CustomData.GetProfileType(position) == CustomData.SHOOTER)
            iv.setImageResource(R.drawable.ic_shooter);
         else if (CustomData.GetProfileType(position) == CustomData.SPORTS)
            iv.setImageResource(R.drawable.ic_sports);
         tv.setText(CustomData.GetProfileName(position));  // sets the profile display name

         return ll;
      }
   }

   // recursively PostInvalidate to a view and all its descendents
   public static void InvalidateRec(View v)
   {
      if (v != null)
      {
         if (v instanceof ViewGroup)
         {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++)
               InvalidateRec(vg.getChildAt(i));
         }
         v.postInvalidate();
      }
   }
}