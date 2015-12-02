package lee.gs_tracker.gsCustom;

import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import lee.gs_tracker.R;

/**
 * Created by Joe Paul on 11/29/2015.
 *
 * manages and displays an rpg profile
 *
 * will allow the user to take notes for each quest by entering adding a quest name, editing a
 *    note for that quest, and letting the user mark quests off for completion
 * all modifications to notes will be reflected upon CustomData.Rpg with each character change
 * will store last active quest for retrieval upon exiting activity
 * will display total number of quests and number of completed quests
 */

public class RpgNotes extends AppCompatActivity implements View.OnClickListener,
      TextWatcher
{
   private static ArrayAdapter<String> questNameAdapter = null;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.notes_activity);

      // set action bar back button to return to parent GSCustom activity
      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null)
         actionBar.setDisplayHomeAsUpEnabled(true);

      // set title bar to game profile name passed from GSCustom
      if (getIntent().getStringExtra("gamename") != null)
         setTitle(getIntent().getStringExtra("gamename"));

      // initialize the data at the active profile index in CustomData set by GSCustom
      CustomData.Rpg.LoadInit();

      // initialize AutoCompleteTextView for quest names
      AutoCompleteTextView questName = (AutoCompleteTextView) findViewById(R.id.notes_quest_name);
      questNameAdapter = new ArrayAdapter<String>
            (
                  this, android.R.layout.simple_list_item_1,
                  CustomData.Rpg.GetAllQuestNames()
            );
      questName.setAdapter(questNameAdapter);
      questName.addTextChangedListener(this);

      // add listeners to edittext (character change), and buttons, quest complete checkbox
      EditText questNotes = ((EditText)findViewById(R.id.notes_quest_notes));
      questNotes.addTextChangedListener(new NoteWatcher());

      findViewById(R.id.notes_quest_add).setOnClickListener(this);
      findViewById(R.id.notes_quest_delete).setOnClickListener(this);
      findViewById(R.id.notes_quest_complete).setOnClickListener(this);

      // if there is an active quest in CustomData.Rpg, load its name into the
      //    AutoCompleteTextView questName and place focus on the EditText questNotes
      String n;
      if ((n = CustomData.Rpg.GetName()) != null)
      {
         questName.setText(n);
         questNotes.requestFocus();
      }

      // update ui elements
      Update();
   }

   // updates all the UI elements in the class upon quest name change
   void Update()
   {
      // refresh AutoComplete data
      // had to enclose this in a try cause queestNameAdapter.clear() threw a strange
      //    exception
      try
      {
         questNameAdapter.clear();
         questNameAdapter.addAll(CustomData.Rpg.GetAllQuestNames());
      } catch (Exception e){}

      // refresh num quests, num complete quests
      ((TextView) findViewById(R.id.notes_quest_count)).setText(
            CustomData.Rpg.GetNumQuests() + ""
      );
      ((TextView) findViewById(R.id.notes_quest_count_complete)).setText(
            CustomData.Rpg.GetNumCompleteQuests() + ""
      );
      EditText notesEdit = ((EditText) findViewById(R.id.notes_quest_notes));
      CheckBox questCheck = (CheckBox) findViewById(R.id.notes_quest_complete);

      // if the active quest is -1, (no quest stored in CustomData for quest name displayed
      //    in AutoCompleteTextView) then disable and clear the EditText for notes
      if (CustomData.Rpg.GetActiveQuest() == -1)
      {
         notesEdit.setEnabled(false);
         notesEdit.setFocusable(false);
         notesEdit.setFocusableInTouchMode(false);
         notesEdit.setText(null);
         notesEdit.setHint("[Select an existing quest or add a new quest to modify notes]");
         notesEdit.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
         questCheck.setEnabled(false);
         questCheck.setChecked(false);
         questCheck.setTextColor(getResources().getColor(R.color.colorSportsTextHint));
      }
      // otherwise, if there is an entry for that quest name, load the note for that quest
      else
      {
         notesEdit.setEnabled(true);
         notesEdit.setFocusable(true);
         notesEdit.setFocusableInTouchMode(true);
         String note = CustomData.Rpg.GetNote();
         if (note == null || note.length() == 0)
            notesEdit.setText(null);
         else
            notesEdit.setText(note);
         notesEdit.setHint(
               "[Enter Quest Notes]"
         );
         notesEdit.setBackgroundColor(Color.parseColor("#414041"));
         questCheck.setEnabled(true);
         questCheck.setChecked(CustomData.Rpg.GetComplete());
         questCheck.setTextColor(getResources().getColor(R.color.colorSportsText));
      }

      notesEdit.postInvalidate();
      questCheck.postInvalidate();
   }

   // only used for back button in action bar
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

   // on resure activity, reload data
   @Override
   protected void onResume()
   {
      super.onResume();

      CustomData.Rpg.LoadInit();
      /*Toast.makeText(
            this, "loaded " + CustomData.Rpg.GetNumQuests() + " RPG quest " +
                  "notes", Toast.LENGTH_SHORT
      ).show();*/
   }

   // on stop activity, write data
   @Override
   protected void onStop()
   {
      super.onStop();

      int numWritten = CustomData.Rpg.UpdateUnload();
      /*Toast.makeText(this, "written " + numWritten + " RPG quest notes", Toast.LENGTH_SHORT)
            .show();*/
   }

   // handle clicks to add, delete quests and checkbox to mark them for completion
   @Override
   public void onClick(View v)
   {
      String qName = ((AutoCompleteTextView)findViewById(R.id.notes_quest_name))
            .getText().toString();
      if (v.getId() == R.id.notes_quest_add) // quest add request, name >= 1 char, no dupes
      {
         if (qName == null || qName.length() == 0)
            new QuickQuestionDialog(this, null, null, "QUEST MUST HAVE A NAME", "error...",
                                    new int[]{0});
         else if (!CustomData.Rpg.AddQuest(qName))
            new QuickQuestionDialog(this, null, null, "QUEST ALREADY EXISTS", "error...",
                                    new int[]{0});
         else
         {
            CustomData.Rpg.SetActiveQuest(qName);
            Update();
            findViewById(R.id.notes_quest_notes).requestFocus();
         }
      }
      else if (v.getId() == R.id.notes_quest_delete) // quest delete, must exist in CustomData
      {
         if (!CustomData.Rpg.RemoveActiveQuest())
            new QuickQuestionDialog(this, null, null, "QUEST DOES NOT EXIST", "error...",
                                    new int[]{0});
         else
            Update();
      }
      else if (v.getId() == R.id.notes_quest_complete) // toggle quest completion
      {
         CustomData.Rpg.SetComplete(((CheckBox)v).isChecked());
         Update();
      }
   }

   // upon change in active quest name, refresh notes field
   @Override
   public void afterTextChanged(Editable s)
   {
      EditText notesEdit = ((EditText)findViewById(R.id.notes_quest_notes));
      if (findViewById(R.id.notes_quest_name).isFocused())
      {
         CustomData.Rpg.SetActiveQuest(s.toString());
         Update();
      }
   }

   @Override
   public void beforeTextChanged(CharSequence s, int start, int count, int after){}

   @Override
   public void onTextChanged(CharSequence s, int start, int before, int count){}

   // upon change in note field, write out to CustomData, sole purpose for NoteWatcher class;
   //    used to have functionality merged with RpgNotes class in its TextWatcher interface, but
   //    ran into a complication when loading the last active quest in onCreate causing
   //    infinite recursion, so had to be handled by different listeners
   class NoteWatcher implements TextWatcher
   {
      @Override
      public void afterTextChanged(Editable s)
      {
         CustomData.Rpg.SetNote(s.toString());
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count){}

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after){}
   }
}
