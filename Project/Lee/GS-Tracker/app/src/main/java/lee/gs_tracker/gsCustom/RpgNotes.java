package lee.gs_tracker.gsCustom;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
public class RpgNotes extends AppCompatActivity implements View.OnClickListener,
      TextWatcher
{
   private static ArrayAdapter<String> questNameAdapter = null;
   private static CustomData.Rpg.Quest activeQuest = null;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.notes_activity);

      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null)
         actionBar.setDisplayHomeAsUpEnabled(true);

      if (getIntent().getStringExtra("gamename") != null)
         setTitle(getIntent().getStringExtra("gamename"));

      CustomData.Rpg.LoadInit();

      AutoCompleteTextView questName = (AutoCompleteTextView) findViewById(R.id.notes_quest_name);
      questNameAdapter = new ArrayAdapter<String>
            (
                  this, android.R.layout.simple_list_item_1,
                  CustomData.Rpg.GetAllQuestNames()
            );
      questName.setAdapter(questNameAdapter);
      questName.addTextChangedListener(this);

      ((EditText)findViewById(R.id.notes_quest_notes)).addTextChangedListener(new NoteWatcher());

      ((Button) findViewById(R.id.notes_quest_add)).setOnClickListener(this);
      ((Button) findViewById(R.id.notes_quest_delete)).setOnClickListener(this);

      ((CheckBox) findViewById(R.id.notes_quest_complete)).setOnClickListener(this);

      activeQuest = null;
      Update();
   }

   void Update()
   {
      try
      {
         questNameAdapter.clear();
         questNameAdapter.addAll(CustomData.Rpg.GetAllQuestNames());
      } catch (Exception e){}
      ((TextView) findViewById(R.id.notes_quest_count)).setText(
            CustomData.Rpg.GetNumQuests() + ""
      );
      ((TextView) findViewById(R.id.notes_quest_count_complete)).setText(
            CustomData.Rpg.GetNumCompleteQuests() + ""
      );
      EditText notesEdit = ((EditText) findViewById(R.id.notes_quest_notes));
      CheckBox questCheck = (CheckBox) findViewById(R.id.notes_quest_complete);
      if (activeQuest == null)
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
      } else
      {
         notesEdit.setEnabled(true);
         notesEdit.setFocusable(true);
         notesEdit.setFocusableInTouchMode(true);
         if (activeQuest.note == null || activeQuest.note.length() == 0)
            notesEdit.setText(null);
         else
            notesEdit.setText(activeQuest.note);
         notesEdit.setHint(
               "[Enter Quest Notes]"
         );
         notesEdit.setBackgroundColor(Color.parseColor("#414041"));
         questCheck.setEnabled(true);
         questCheck.setChecked(activeQuest.complete);
         questCheck.setTextColor(getResources().getColor(R.color.colorSportsText));
      }
      notesEdit.postInvalidate();
      questCheck.postInvalidate();
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
   protected void onResume()
   {
      super.onResume();

      CustomData.Rpg.LoadInit();
      Toast.makeText(
            this, "loaded " + CustomData.Rpg.GetNumQuests() + " RPG quest " +
                  "notes", Toast.LENGTH_SHORT
      ).show();
   }

   @Override
   protected void onStop()
   {
      super.onStop();

      int numWritten = CustomData.Rpg.UpdateUnload();
      Toast.makeText(this, "written " + numWritten + " RPG quest notes", Toast.LENGTH_SHORT)
            .show();
   }

   @Override
   public void onClick(View v)
   {
      String qName = ((AutoCompleteTextView)findViewById(R.id.notes_quest_name))
            .getText().toString();
      if (v.getId() == R.id.notes_quest_add)
      {
         if (qName == null || qName.length() == 0)
            new QuickQuestionDialog(this, null, null, "QUEST MUST HAVE A NAME", "error...",
                                    new int[]{0});
         else if (!CustomData.Rpg.AddQuest(qName))
            new QuickQuestionDialog(this, null, null, "QUEST ALREADY EXISTS", "error...",
                                    new int[]{0});
         else
         {
            activeQuest = CustomData.Rpg.GetQuest(qName);
            Update();
         }
      }
      else if (v.getId() == R.id.notes_quest_delete)
      {
         if (!CustomData.Rpg.RemoveQuest(qName))
            new QuickQuestionDialog(this, null, null, "QUEST DOES NOT EXIST", "error...",
                                    new int[]{0});
         else
         {
            activeQuest = CustomData.Rpg.GetQuest(qName);
            Update();
         }
      }
      else if (v.getId() == R.id.notes_quest_complete)
      {
         if (activeQuest != null)
            activeQuest.complete = ((CheckBox)v).isChecked();
         Update();
      }
   }

   @Override
   public void afterTextChanged(Editable s)
   {
      EditText notesEdit = ((EditText)findViewById(R.id.notes_quest_notes));
      if (findViewById(R.id.notes_quest_name).isFocused())
      {
         activeQuest = CustomData.Rpg.GetQuest(s.toString());
         Update();
      }
   }

   @Override
   public void beforeTextChanged(CharSequence s, int start, int count, int after){}

   @Override
   public void onTextChanged(CharSequence s, int start, int before, int count){}

   class NoteWatcher implements TextWatcher
   {
      @Override
      public void afterTextChanged(Editable s)
      {
         if (activeQuest != null)
            activeQuest.note = s.toString();
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count){}

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after){}
   }
}
