package lee.gs_tracker.gsCustom;

import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import lee.gs_tracker.R;

/**
 * Created by Joe Paul on 11/25/2015.
 *
 * manages and displays a sports game profile
 *
 * allows the user to track a list of games; the user specifies the date the game is played,
 *    the team the user is playing as, the team the user is playing against, the user's score,
 *    and the opposing teams score
 * displays the list of all games played and computes some basic statistics over the list of games
 * allows the user to delete a single game entry by long click on the entry in the games list,
 *    but would have to delete the profile in GSCustom if wants to clear them all at once
 */

public class Sports extends AppCompatActivity implements View.OnClickListener,
      QuickQuestionDialog.Listener
{
   /**
    * The {@link android.support.v4.view.PagerAdapter} that will provide
    * fragments for each of the sections. We use a
    * {@link FragmentPagerAdapter} derivative, which will keep every
    * loaded fragment in memory. If this becomes too memory intensive, it
    * may be best to switch to a
    * {@link android.support.v4.app.FragmentStatePagerAdapter}.
    */

   // static instance of Sports object
   static Sports instance = null;

   // views / objects in class score for ease/necessity of accessibility
   static View addGameView;
   static View gamesListView;
   static View gameStatsView;
   static ArrayAdapter<String> teamAutoCompleteAdapter;
   static GamesListAdapter gamesListAdapter;
   static StatAdapter gameStatsAdapter;

   private SectionsPagerAdapter mSectionsPagerAdapter;

   /**
    * The {@link ViewPager} that will host the section contents.
    */
   private ViewPager mViewPager;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.sports_activity);
      instance = this; // set instance to current Sports object

      // initialize all static views/objects to null upon new instance
      addGameView = null;
      gamesListView = null;
      gameStatsView = null;
      teamAutoCompleteAdapter = null;
      gamesListAdapter = null;
      gameStatsAdapter = null;

      CustomData.Sports.LoadInit(); // load sports profile data

      // set game profile name as title as string received from GSCustom
      if (getIntent().getStringExtra("gamename") != null)
         setTitle(getIntent().getStringExtra("gamename"));

      // add a back button to the toolbar
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

      // Create the adapter that will return a fragment for each of the three
      // primary sections of the activity.
      mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

      // Set up the ViewPager with the sections adapter.
      mViewPager = (ViewPager) findViewById(R.id.container);
      mViewPager.setAdapter(mSectionsPagerAdapter);
      mViewPager.addOnPageChangeListener(mSectionsPagerAdapter);
      if (CustomData.Sports.GetTotalGamesPlayed() == 0)
         mViewPager.setCurrentItem(1);
      else
         mViewPager.setCurrentItem(3);
   }

   @Override
   protected void onResume() // reload profile data from CustomData
   {
      super.onResume();

      CustomData.Sports.LoadInit();
      /*Toast.makeText(this, "loaded " + CustomData.Sports.GetGamesList().size() + " sports game " +
            "entries", Toast.LENGTH_SHORT).show();*/
   }

   @Override
   protected void onStop() // write out profile data to CustomData
   {
      super.onStop();

      int numWritten = CustomData.Sports.UpdateUnload();
      /*Toast.makeText(this, "written " + numWritten + " sports games entries", Toast.LENGTH_SHORT)
            .show();*/
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.sports_menu, menu);
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
      if (id == android.R.id.home)
      {
         NavUtils.navigateUpFromSameTask(this);
      }

      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onClick(View v)
   {
      // button click to select data, creates a DatePicker view and passes it as a custom view
      //    to QuickQuestionDialog, handled further down in onQuickQuestionDialogListener
      if (v.getId() == R.id.sports_add_date)
      {
         DatePicker dp = new DatePicker(this);
         dp.setCalendarViewShown(false);
         dp.setSpinnersShown(true);
         new QuickQuestionDialog(this, "getdate", this, dp, "Enter a Date...", new int[]{-1, 0});
      }
      else if (v.getId() == R.id.sports_add_add) // user hits add game button
      {
         String yourTeam = null, opponentTeam = null;
         int day = -1, month = -1, year = -1, yourScore = -1, opponentScore = -1;
         Boolean valid = true;
         try
         {
            // retrieve user input from android view elements
            yourTeam = ((EditText) addGameView.findViewById(R.id.sports_add_your_team))
                  .getText().toString();
            opponentTeam = ((EditText) addGameView.findViewById(
                  R.id.sports_add_opponent_team
            )).getText().toString();
            yourScore = Integer.parseInt(
                  ((EditText) addGameView.findViewById(R.id.sports_add_your_score))
                        .getText().toString()
            );
            opponentScore = Integer.parseInt(
                  ((EditText) addGameView.findViewById(R.id.sports_add_opponent_score))
                        .getText().toString()
            );

            DatePicker dp = (DatePicker) findViewById(R.id.sports_add_date).getTag();
            if (dp != null)
            {
               day = dp.getDayOfMonth();
               month = dp.getMonth() + 1;
               year = dp.getYear();
            }
         }
         catch (Exception e) {valid = false;}

         // validate user input, all fields must be filled, not null, date must be picked
         if (yourTeam == null || yourTeam.length() == 0 || opponentTeam == null ||
               opponentTeam.length() == 0 || yourScore < 0 || opponentScore < 0 ||
               day == -1 || month == -1 || year == -1)
            valid = false;
         if (valid)
         {
            // add game to CustomData.Sports
            CustomData.Sports.AddGame(
                  month, day, year, yourTeam, opponentTeam, yourScore,
                  opponentScore
            );
            Toast.makeText(
                  Sports.instance, "Added " + year + "-" + month + "-" + day + ": " +
                        yourTeam + " VS. " + opponentTeam, Toast.LENGTH_SHORT
            ).show();

            // clear all input view elements
            ((Button)addGameView.findViewById(R.id.sports_add_date))
                  .setText("[Click To Set Date]");
            ((EditText)addGameView.findViewById(R.id.sports_add_your_team)).setText("");
            ((EditText)addGameView.findViewById(R.id.sports_add_opponent_team)).setText("");
            ((EditText)addGameView.findViewById(R.id.sports_add_your_score)).setText("");
            ((EditText)addGameView.findViewById(R.id.sports_add_opponent_score)).setText("");
            findViewById(R.id.sports_add_date).setTag(null);

            PostInvalidateAll(); // refresh all UI elements
         }
         // else if data invalid, notify user of invalid data
         else
            new QuickQuestionDialog(this, null, null, "Invalid Data.", "uh-oh...", new int[]{0});
      }
   }

   // updates all UI elements
   private void PostInvalidateAll()
   {
      if (teamAutoCompleteAdapter != null)
      {
         teamAutoCompleteAdapter.clear();
         teamAutoCompleteAdapter.addAll(CustomData.Sports.GetAllTeams());
      }
      if (gamesListView != null)
      {
         gamesListAdapter.notifyDataSetInvalidated();
         gamesListAdapter.notifyDataSetChanged();
         GSCustom.InvalidateRec(gamesListView);
      }
      if (gameStatsView != null)
      {
         gameStatsAdapter.notifyDataSetInvalidated();
         gameStatsAdapter.notifyDataSetChanged();
         GSCustom.InvalidateRec(gameStatsView);
      }
   }

   @Override
   public void onQuickQuestionDialogClick(String name, int response, Object tag)
   {
      // user has selected a date, extract date from DatePicker view object as QuickQuestionDialog
      //    tag and set Date Button text to date selected
      if (name.equals("getdate") && response >= 0)
      {
         Button dateButt = (Button) addGameView.findViewById(R.id.sports_add_date);
         DatePicker dp = (DatePicker) tag;
         dateButt.setText((dp.getMonth() + 1) + " / " + dp.getDayOfMonth() + " / " + dp.getYear());
         dateButt.setTextColor(getResources().getColor(R.color.colorSportsText));
         dateButt.setTag(tag);
         findViewById(R.id.sports_add_root).postInvalidate(); // refresh date button ui
      }
      // confirmed request to delete a game entry, delete the entry
      else if (name.equals("deletesportsgameentry") && response == 1)
      {
         Toast.makeText(
               Sports.instance, "Removed " + CustomData.Sports.GetGamesList().get((int)tag).year +
                     "-" + CustomData.Sports.GetGamesList().get((int)tag).month + "-" +
                     CustomData.Sports.GetGamesList().get((int)tag).day + ": " +
                     CustomData.Sports.GetGamesList().get((int)tag).yourTeam + " VS. " +
                     CustomData.Sports.GetGamesList().get((int)tag).opponentTeam,
               Toast.LENGTH_SHORT).show();
         CustomData.Sports.DeleteIndex((int) tag);

         PostInvalidateAll(); // refresh ui
      }
   }

   /**
    * A placeholder fragment containing a simple view.
    */
   public static class PlaceholderFragment extends Fragment
   {
      /**
       * The fragment argument representing the section number for this
       * fragment.
       */
      private static final String ARG_SECTION_NUMBER = "section_number";

      public PlaceholderFragment()
      {
      }

      /**
       * Returns a new instance of this fragment for the given section
       * number.
       */
      public static PlaceholderFragment newInstance(int sectionNumber)
      {
         PlaceholderFragment fragment = new PlaceholderFragment();
         Bundle args = new Bundle();
         args.putInt(ARG_SECTION_NUMBER, sectionNumber);
         fragment.setArguments(args);
         return fragment;
      }

      private int Dp(int px) // converts pixels to android device-independent pixels
      {
         return (int) TypedValue.applyDimension(
               TypedValue.COMPLEX_UNIT_DIP, 14,
               getResources().getDisplayMetrics()
         );
      }

      @Override
      public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
      )
      {
         /*
         View rootView = inflater.inflate(R.layout.sports_fragment, container, false);
         TextView textView = (TextView) rootView.findViewById(R.id.section_label);
         textView.setText(
               getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER))
         );*/

         View rootView = null;

         /*Toast.makeText(Sports.instance, "section arg number: " +
                              getArguments().getInt(ARG_SECTION_NUMBER),
                                       Toast.LENGTH_SHORT).show();*/

         switch (getArguments().getInt(ARG_SECTION_NUMBER))
         {
            // section 1 - add game, inflate, initialize all view elements and data
            case 1:
            {
               if (addGameView != null)
                  rootView = addGameView;
               else
               {
                  rootView = addGameView = inflater.inflate(R.layout.sports_add_game,
                                                              container, false);
               }

               Button dateButt = (Button) rootView.findViewById(R.id.sports_add_date);
               dateButt.setText("[Select Date]");
               dateButt.setOnClickListener(Sports.instance);

               // autocompletetextviews for your team and opponent team allows team suggestions
               //    as the user types
               AutoCompleteTextView yTeamName = (AutoCompleteTextView) rootView
                     .findViewById(R.id.sports_add_your_team);
               AutoCompleteTextView oTeamName = (AutoCompleteTextView) rootView
                     .findViewById(R.id.sports_add_opponent_team);

               // adapter for autocompletetext views set to CustomData.Sports.GetAllTeams()
               teamAutoCompleteAdapter = new ArrayAdapter<String>
                     (
                           getContext(), android.R.layout.simple_list_item_1,
                           CustomData.Sports.GetAllTeams()
                     );

               // set adapter for autocompletetextviews
               yTeamName.setAdapter(teamAutoCompleteAdapter);
               yTeamName.setThreshold(1); // can't quite remember what this does
               yTeamName.setText("");
               oTeamName.setAdapter(teamAutoCompleteAdapter);
               oTeamName.setThreshold(1);
               oTeamName.setText("");

               // clear score edittexts
               ((EditText) rootView.findViewById(R.id.sports_add_your_score)).setText("");
               ((EditText) rootView.findViewById(R.id.sports_add_opponent_score)).setText("");

               // set add button onclick listener
               Button addButt = (Button) rootView.findViewById(R.id.sports_add_add);
               addButt.setOnClickListener(Sports.instance);

               break;
            }

            // section 2 - list of games
            case 2:
            {
               if (gamesListView != null)
                  rootView = gamesListView;
               else
               {
                  rootView = gamesListView = inflater.inflate(R.layout.sports_game_list,
                                                              container, false);
                  GridView gv = (GridView) rootView.findViewById(R.id.sports_game_list_list_labels);

                  // adapter for header gridview overrides 2 methods so that header list items
                  //    are not selectable
                  ArrayAdapter<String> aa =
                     new ArrayAdapter<String>(getContext(), R.layout.sports_game_list_label,
                                              new String[]{"Date", "Your Team", "Your Score",
                                                    "Opponent Team" , "Opponent Score"}){
                        @Override
                        public boolean isEnabled(int position)
                        {
                           return false;
                        }

                        @Override
                        public boolean areAllItemsEnabled()
                        {
                           return false;
                        }
                     };
                  gv.setAdapter(aa);

                  // gridview for list
                  gv = (GridView) rootView.findViewById(R.id.sports_game_list_list);
                  gamesListAdapter = new GamesListAdapter();
                  gv.setAdapter(gamesListAdapter);
               }

               break;
            }

            // section 3 - stats
            case 3:
            {
               if (gameStatsView != null)
                  rootView = gameStatsView;
               else
               {
                  rootView = gameStatsView = inflater.inflate(
                        R.layout.sports_stats,
                        container, false
                  );
                  GridView gv = (GridView) rootView.findViewById(R.id.sports_stat_grid);
                  gv.setAdapter(gameStatsAdapter = new StatAdapter());
               }

               break;
            }
            default:
            {
               rootView = null;
            }
         }

         // error view returned in case none of above apply (was placeholder mid-development)
         // now dummy view for sections 0 && 4:
         //    created onpagechangelistener for ViewPager fragment container so that automatically
         //    scrolls to page 3 from page 0 and to page 1 from page 4 so that the pages
         //    defined above (1-3) would "rotate"
         if (rootView == null)
         {
            rootView = new TextView(getContext());
            rootView.setLayoutParams(
                  new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                  )
            );
            rootView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            ((TextView)rootView).setTextColor(Color.parseColor("#C00000"));
            ((TextView)rootView).setText("");
         }

         return rootView;
      }
   }

   // adapter class for games list
   static class GamesListAdapter extends BaseAdapter implements ListAdapter,
         View.OnLongClickListener
   {
      // user deletes item in games list by long click, display QuickQuestionDialog confirmation,
      //    handle response with onQuickQuestionDialog listener further down
      @Override
      public boolean onLongClick(View v)
      {
         CustomData.Sports.Game g = CustomData.Sports.GetGamesList()
               .get(((Integer)v.getTag()).intValue());
         new QuickQuestionDialog(instance, "deletesportsgameentry", instance, "Are you sure you " +
            "want to delete entry " + g.yourTeam + " VS. " + g.opponentTeam + " on " +
               g.month + "/" + g.day + "/" + g.year + "?", "confirmation",
                                 new String[]{"Yes", "No"}, new int[]{1, -1},
                                       new Object[]{v.getTag(), v.getTag()});
         return true;
      }

      // each row corresponds to a single game entry and has 5 colums, a date, your team name,
      //    your score, opponent team name, and opponent score;
      // a dummy row is added (+1) as a workaround because the item in the last row is displayed
      //    off screen (i suppose because of the way android lays out gridviews, i toyed with it
      //    and there doesn't seem to be a way to change their height; they seem to have to
      //    occupy their entire parent and since I also have a label and a header gridview in the
      //    view parent, it pushes the list off screen)
      @Override
      public int getCount()
      {
         return 5 * (CustomData.Sports.GetGamesList().size() + 1);
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent)
      {
         AbsListView.LayoutParams params =
               new AbsListView.LayoutParams
                     (
                           AbsListView.LayoutParams.WRAP_CONTENT,
                           AbsListView.LayoutParams.WRAP_CONTENT
                     );

         TextView tv = new TextView(parent.getContext());
         convertView = tv;
         tv.setLayoutParams(params);

         // set text of view element appropriately according to column for
         //    views within range of data
         if (position < 5 * CustomData.Sports.GetGamesList().size())
         {
            CustomData.Sports.Game g = CustomData.Sports.GetGamesList().get(position / 5);
            if (position % 5 == 0)
               tv.setText(g.month + " / " + g.day + " / " + g.year);
            else if (position % 5 == 1)
               tv.setText(g.yourTeam);
            else if (position % 5 == 2)
               tv.setText(g.yourScore + "");
            else if (position % 5 == 3)
               tv.setText(g.opponentTeam);
            else if (position % 5 == 4)
               tv.setText(g.opponentScore + "");
            tv.setTag(position / 5);
            tv.setOnLongClickListener(this);
            tv.setTextColor(instance.getResources().getColor(R.color.colorSportsTextHint));
         }
         // otherwise if view position is out of data range, create dummy view
         else
         {
            tv.setText("");
            tv.setEnabled(false);
         }
         tv.setLines(3);

         return convertView;
      }

      @Override
      public boolean hasStableIds()
      {
         return false;
      }

      @Override
      public boolean isEnabled(int position)
      {
         return true;
      }

      @Override
      public boolean areAllItemsEnabled()
      {
         return true;
      }

      @Override
      public boolean isEmpty()
      {
         return CustomData.Sports.GetGamesList() == null ||
               CustomData.Sports.GetGamesList().size() == 0;
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
      public long getItemId(int position)
      {
         return position;
      }

      @Override
      public Object getItem(int position)
      {
         return position < 5 * CustomData.Sports.GetGamesList().size() ?
               CustomData.Sports.GetGamesList().get(position / 5) : null;
      }
   }

   // an adapter for the gridview that displays stats in section 3
   static class StatAdapter extends BaseAdapter
   {
      private final int numStats = 11;

      @Override
      public int getCount()
      {
         try
         {
            return CustomData.Sports.GetGamesList().size() > 0 ? 2 * numStats : 0;
         }
         catch (Exception e)
         {
            return 0;
         }
      }

      @Override
      public int getViewTypeCount()
      {
         return 1;
      }

      @Override
      public Object getItem(int position)
      {
         return position / 2;
      }

      @Override
      public long getItemId(int position)
      {
         return position / 2;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent)
      {
         TextView tv = new TextView(parent.getContext());
         tv.setLayoutParams
               (
                  new AbsListView.LayoutParams
                        (
                              AbsListView.LayoutParams.WRAP_CONTENT,
                              AbsListView.LayoutParams.WRAP_CONTENT
                        )
               );

         // labels, first column in gridview
         if (position % 2 == 0)
         {
            String[] labels = new String[]
                  {
                        "Total Games", "Total Teams", "Num. Teams Played", "Total Wins",
                        "Total Losses", "Total Ties", "Min Score", "Max Score", "Average Score",
                        "Ave. Opponent Score", "Ave. Score Ratio"
                  };

            if (position / 2 < labels.length)
               tv.setText(labels [position / 2]);
            else
               tv.setText("errpr");
            tv.setTextColor(Color.parseColor("#D0D0D0"));
         }
         // numeric field, second column in gridview
         else
         {
            tv.setTextColor(Color.parseColor("#B0B0B0"));
            String displayString = "";
            switch (position / 2)
            {
               case 0:
                  displayString = CustomData.Sports.GetGamesList().size() + "";
                  break;
               case 1:
                  displayString = CustomData.Sports.GetAllTeams().length + "";
                  break;
               case 2:
                  displayString = CustomData.Sports.GetAllTeamsPlayedAs().length + "";
                  break;
               case 3:
                  displayString = CustomData.Sports.GetTotalWins(null, null) + "";
                  break;
               case 4:
                  displayString = CustomData.Sports.GetTotalLosses(null, null) + "";
                  break;
               case 5:
                  displayString = CustomData.Sports.GetTotalDraws(null, null) + "";
                  break;
               case 6:
                  displayString = CustomData.Sports.GetMinScore(null, null) + "";
                  break;
               case 7:
                  displayString = CustomData.Sports.GetMaxScore(null, null) + "";
                  break;
               case 8:
                  displayString = CustomData.Sports.GetAveScore() + "";
                  break;
               case 9:
                  displayString = CustomData.Sports.GetAveOppScore() + "";
                  break;
               case 10:
                  displayString = CustomData.Sports.GetScoreRatio() + "";
                  break;
               default:
                  displayString = "out of bounds";
            }

            tv.setText(displayString);
         }

         return convertView = tv;
      }
   }

   /**
    * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
    * one of the sections/tabs/pages.
    */
   public class SectionsPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener
   {
      public SectionsPagerAdapter(FragmentManager fm)
      {
         super(fm);
      }

      @Override
      public Fragment getItem(int position)
      {
         // getItem is called to instantiate the fragment for the given page.
         // Return a PlaceholderFragment (defined as a static inner class below).
         return PlaceholderFragment.newInstance(position);
      }

      @Override
      public int getCount()
      {
         // Show 3 total pages.
         return 5;
      }

      @Override
      public CharSequence getPageTitle(int position)
      {
         switch (position)
         {
            case 0:
               return "Add Game";
            case 1:
               return "Games List";
            case 2:
               return "Stat Viewer";
         }
         return null;
      }

      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}

      @Override
      public void onPageScrollStateChanged(int state){}

      // go to page 3 from page 0 and to page 1 from page 4
      @Override
      public void onPageSelected(int position)
      {
         if (position < 1)
            position = 3;
         if (position > 3)
            position = 1;
         ((ViewPager)Sports.instance.findViewById(R.id.container))
               .setCurrentItem(position);
      }
   }
}
