package lee.gs_tracker.gsCustom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import lee.gs_tracker.R;

/**
 * Created by Joe Paul on 11/25/2015.
 *
 * displays the dialog to add a custom profile when the user clicks on the action button
 * in the GSCustom activity
 */

public class AddGameDialog extends DialogFragment implements DialogInterface.OnClickListener,
      AdapterView.OnItemClickListener, View.OnClickListener
{
   private AddGameDialog instance;

   // objects / views stored as part of the extending class for ability / conveience of
   // referencing them
   AlertDialog.Builder dialogBuilder = null;
   AlertDialog dialog = null;
   LinearLayout dialogView = null;
   ListView dialogList = null;
   String gameName = null;
   int gameType = -1;
   String gameTypeString = null;

   // creates a new AddGameDialog instance
   public AddGameDialog()
   {
      instance = this;
      onCreateDialog(null); // call DialogFragment.onCreateDialog
   }

   public Dialog onCreateDialog(Bundle savedInstanceState)
   {
      // create new AlertDialogBuilder
      dialogBuilder = new AlertDialog.Builder(GSCustom.instance);

      // set the dialog custom title and content views
      dialogBuilder.setCustomTitle(GSCustom.instance.findViewById(R.id.dialog_add_title))
            .setView(
                  dialogView = (LinearLayout) GSCustom.instance.getLayoutInflater().inflate(
                        R.layout.add_game_dialog,
                        null
                  )
            )
            .setPositiveButton("OK", this)
            .setNegativeButton("Cancel", this);

      // set dialoglist to listview enumerating game types, set adapter for it, item
      //    click listener, set colors on all list items (color of text changes on selection)
      dialogList = (ListView) ((LinearLayout) dialogView.getChildAt(2)).getChildAt(1);
      ArrayAdapter<String> a = new ArrayAdapter<String>(GSCustom.instance,
                      R.layout.add_game_dialog_listentry, CustomData.GAMETYPE);
      dialogList.setAdapter(a);
      dialogList.setOnItemClickListener(this);
      for (int i = 0; i < dialogList.getChildCount(); i++)
         ((TextView) dialogList.getChildAt(i)).setTextColor(
               GSCustom.instance.getResources().getColor(
                     R.color.colorPopupText
               )
         );

      // had a hard time getting keyboard to show up for edittext in alertdialog
      dialog = dialogBuilder.create();
      // one of the recommendations i found on a website, don't know if it is critical to
      //    showing the virtual keyboard, but does no harm
      dialog.getWindow().clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                  WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
      );
      // set the virtual keyboard visible when dialog shows
      dialog.getWindow().setSoftInputMode(
            WindowManager.
                  LayoutParams.SOFT_INPUT_STATE_VISIBLE
      );
      dialog.show();

      // add onclick listener to edittext to game profile name (necessary to restore virtual
      //    keyboard every time the user clicks on the edittext incase they accidentally
      //    dismiss it by pressing back button otherwise would have to cancel dialog and reopen
      //    it to get keyboard back)
      EditText t = (EditText) dialog.findViewById(R.id.dialog_add_game_name);
      t.setOnClickListener(this);
      t.requestFocus();

      return dialog;
   }

   // user clicks on game profile type in listview
   @Override
   public void onItemClick(AdapterView<?> parent, View view, int position, long id)
   {
      // set colors for all items in list to default color
      if (dialogList != null)
         for (int i = 0; i < dialogList.getChildCount(); i++)
            ((TextView)dialogList.getChildAt(i)).setTextColor(
                  GSCustom.instance.getResources().getColor(
                        R.color.colorPopupText
                  )
            );
      // set color to selected of selected item to different color
      if (view != null && dialogList != null)
         ((TextView)dialogList.getChildAt(position)).setTextColor(
               GSCustom.instance.getResources().getColor(
                     R.color.colorPopupTextSelected
               )
         );
      // set gametype to position of selected item
      if (view != null)
         gameType = position;
   }

   @Override
   public void onClick(DialogInterface dialog, int which)
   {
      // only if user clicks "ok"
      if (which == DialogInterface.BUTTON_POSITIVE)
      {
         try
         {
            gameName = ((EditText)dialogView.findViewById(R.id.dialog_add_game_name))
                  .getText().toString();

            // if gamename is >= 1 characters && gametype selected, add profile, write modified
            //    profile list, otherwise display error
            if (GetGameName() != null && GetGameName().length() > 0 &&
                  GetGameType() != -1)
            {
               CustomData.AddProfile(GetGameName(), GetGameType());
               CustomData.WriteData();
               GSCustom.instance.gameListAdapter.notifyDataSetChanged();
            }
            else
               new QuickQuestionDialog(GSCustom.instance, null, null, "Invalid Entry: must enter " +
                     "a Game Name and make a selection.", "error...", new int[]{0});
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }

      // must manually dismiss keyboard or it will remain after the dialog exits
      InputMethodManager imm = (InputMethodManager) GSCustom.instance
            .getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(
            instance.dialog.findViewById(R.id.dialog_add_game_name).getWindowToken(), 0);
   }

   // restore virtual keyboard every time user clicks on edittext to type game name
   @Override
   public void onClick(View v)
   {
      if (v.getId() == R.id.dialog_add_game_name)
      {
         InputMethodManager imm = (InputMethodManager) GSCustom.instance
               .getSystemService(Context.INPUT_METHOD_SERVICE);
         imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
      }
   }

   public String GetGameName()
   {
      return gameName;
   }

   public int GetGameType()
   {
      return gameType;
   }

   public String GetGameTypeString()
   {
      return gameTypeString;
   }
}
