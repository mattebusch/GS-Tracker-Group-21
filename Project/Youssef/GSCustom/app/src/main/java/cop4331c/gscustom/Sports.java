package cop4331c.gscustom;

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

   static Sports instance = null;

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
      instance = this;

      addGameView = null;
      gamesListView = null;
      gameStatsView = null;
      teamAutoCompleteAdapter = null;
      gamesListAdapter = null;
      gameStatsAdapter = null;

      CustomData.Sports.LoadInit();

      if (getIntent().getStringExtra("gamename") != null)
         setTitle(getIntent().getStringExtra("gamename"));

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
         Toast.makeText(this, "failed to create back button;\n" + e.toString(), Toast.LENGTH_LONG)
               .show();
      }

      // Create the adapter that will return a fragment for each of the three
      // primary sections of the activity.
      mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

      // Set up the ViewPager with the sections adapter.
      mViewPager = (ViewPager) findViewById(R.id.container);
      mViewPager.setAdapter(mSectionsPagerAdapter);
   }

   @Override
   protected void onResume()
   {
      super.onResume();

      CustomData.Sports.LoadInit();
      Toast.makeText(this, "loaded " + CustomData.Sports.GetGamesList().size() + " sports game " +
            "entries", Toast.LENGTH_SHORT).show();
   }

   @Override
   protected void onStop()
   {
      super.onStop();

      int numWritten = CustomData.Sports.UpdateUnload();
      Toast.makeText(this, "written " + numWritten + " sports games entries", Toast.LENGTH_SHORT)
            .show();
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

   @Override
   public void onClick(View v)
   {
      if (v.getId() == R.id.sports_add_date)
      {
         DatePicker dp = new DatePicker(this);
         dp.setCalendarViewShown(false);
         dp.setSpinnersShown(true);
         new QuickQuestionDialog(this, "getdate", this, dp, "Enter a Date...", new int[]{-1, 0});
      }
      else if (v.getId() == R.id.sports_add_add)
      {
         String yourTeam = null, opponentTeam = null;
         int day = -1, month = -1, year = -1, yourScore = -1, opponentScore = -1;
         Boolean valid = true;
         try
         {
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
         if (yourTeam == null || yourTeam.length() == 0 || opponentTeam == null ||
               opponentTeam.length() == 0 || yourScore < 0 || opponentScore < 0 ||
               day == -1 || month == -1 || year == -1)
            valid = false;
         if (valid)
         {
            CustomData.Sports.AddGame(
                  month, day, year, yourTeam, opponentTeam, yourScore,
                  opponentScore
            );
            Toast.makeText(
                  Sports.instance, "Added " + year + "-" + month + "-" + day + ": " +
                        yourTeam + " VS. " + opponentTeam, Toast.LENGTH_SHORT
            ).show();
            ((Button)addGameView.findViewById(R.id.sports_add_date))
                  .setText("[Click To Set Date]");
            ((EditText)addGameView.findViewById(R.id.sports_add_your_team)).setText("");
            ((EditText)addGameView.findViewById(R.id.sports_add_opponent_team)).setText("");
            ((EditText)addGameView.findViewById(R.id.sports_add_your_score)).setText("");
            ((EditText)addGameView.findViewById(R.id.sports_add_opponent_score)).setText("");

            findViewById(R.id.sports_add_date).setTag(null);

            PostInvalidateAll();
         }
         else
            new QuickQuestionDialog(this, null, null, "Invalid Data.", "uh-oh...", new int[]{0});
      }
   }

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
         InvalidateRec(gamesListView);
      }
      if (gameStatsView != null)
      {
         gameStatsAdapter.notifyDataSetInvalidated();
         gameStatsAdapter.notifyDataSetChanged();
         InvalidateRec(gameStatsView);
      }
   }

   @Override
   public void onQuickQuestionDialogClick(String name, int response, Object tag)
   {
      if (name.equals("getdate") && response >= 0)
      {
         Button dateButt = (Button) addGameView.findViewById(R.id.sports_add_date);
         DatePicker dp = (DatePicker) tag;
         dateButt.setText((dp.getMonth() + 1) + " / " + dp.getDayOfMonth() + " / " + dp.getYear());
         dateButt.setTextColor(getResources().getColor(R.color.colorSportsText));
         dateButt.setTag(tag);
         findViewById(R.id.sports_add_root).postInvalidate();
      }
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

         PostInvalidateAll();
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

      private int Dp(int px)
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
            case 0:
            {
               if (gamesListView != null)
                  rootView = addGameView;
               else
               {
                  rootView = addGameView = inflater.inflate(R.layout.sports_add_game,
                                                              container, false);
               }

               Button dateButt = (Button) rootView.findViewById(R.id.sports_add_date);
               dateButt.setText("[Select Date]");
               dateButt.setOnClickListener(Sports.instance);

               AutoCompleteTextView yTeamName = (AutoCompleteTextView) rootView
                     .findViewById(R.id.sports_add_your_team);
               AutoCompleteTextView oTeamName = (AutoCompleteTextView) rootView
                     .findViewById(R.id.sports_add_opponent_team);

               teamAutoCompleteAdapter = new ArrayAdapter<String>
                     (
                           getContext(), android.R.layout.simple_list_item_1,
                           CustomData.Sports.GetAllTeams()
                     );

               yTeamName.setAdapter(teamAutoCompleteAdapter);
               yTeamName.setThreshold(1);
               yTeamName.setText("");
               oTeamName.setAdapter(teamAutoCompleteAdapter);
               oTeamName.setThreshold(1);
               oTeamName.setText("");

               ((EditText) rootView.findViewById(R.id.sports_add_your_score)).setText("");
               ((EditText) rootView.findViewById(R.id.sports_add_opponent_score)).setText("");

               Button addButt = (Button) rootView.findViewById(R.id.sports_add_add);
               addButt.setOnClickListener(Sports.instance);

               break;
            }
            case 1:
            {
               if (gamesListView != null)
                  rootView = gamesListView;
               else
               {
                  rootView = gamesListView = inflater.inflate(R.layout.sports_game_list,
                                                              container, false);
                  GridView gv = (GridView) rootView.findViewById(R.id.sports_game_list_list_labels);
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

                  gv = (GridView) rootView.findViewById(R.id.sports_game_list_list);
                  gamesListAdapter = new GamesListAdapter();
                  gv.setAdapter(gamesListAdapter);
               }

               break;
            }
            case 2:
            {
               rootView = gameStatsView = inflater.inflate(R.layout.sports_stats,
                                                           container, false);
               GridView gv = (GridView) rootView.findViewById(R.id.sports_stat_grid);
               gv.setAdapter(gameStatsAdapter = new StatAdapter());

               break;
            }
            default:
            {
               rootView = null;
            }
         }

         if (rootView == null)
         {
            rootView = new TextView(getContext());
            rootView.setLayoutParams(
                  new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                  )
            );
            rootView.setPadding(Dp(10), Dp(10), Dp(10), Dp(10));
            rootView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            ((TextView)rootView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
            ((TextView)rootView).setTextColor(Color.parseColor("#FFFFFF"));
            ((TextView)rootView).setText("section #" + getArguments().getInt(ARG_SECTION_NUMBER)
                                               + " does not exist");
         }

         return rootView;
      }
   }

   static class GamesListAdapter extends BaseAdapter implements ListAdapter,
         View.OnLongClickListener
   {
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
   public class SectionsPagerAdapter extends FragmentPagerAdapter
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
         return 3;
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
   }
}
