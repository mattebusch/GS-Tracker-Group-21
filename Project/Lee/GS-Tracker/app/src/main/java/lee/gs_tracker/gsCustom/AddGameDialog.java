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

/**
 * Created by Joe Paul on 11/25/2015.
 */
import lee.gs_tracker.R;
public class AddGameDialog extends DialogFragment implements DialogInterface.OnClickListener,
      AdapterView.OnItemClickListener
{
   private AddGameDialog instance;

   AlertDialog.Builder dialogBuilder = null;
   AlertDialog dialog = null;
   LinearLayout dialogView = null;
   ListView dialogList = null;
   String gameName = null;
   int gameType = -1;
   String gameTypeString = null;

   public AddGameDialog()
   {
      instance = this;
      onCreateDialog(null);
   }

   public Dialog onCreateDialog(Bundle savedInstanceState)
   {
      dialogBuilder = new AlertDialog.Builder(GSCustom.instance);

      dialogBuilder.setCustomTitle(GSCustom.instance.findViewById(R.id.dialog_add_title))
            .setView(
                  dialogView = (LinearLayout) GSCustom.instance.getLayoutInflater().inflate(
                        R.layout.add_game_dialog,
                        null
                  )
            )
            .setPositiveButton("OK", this)
            .setNegativeButton("Cancel", this);

      dialog = dialogBuilder.create();

      dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  |
                                          WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
      dialog.getWindow().setSoftInputMode(WindowManager.
                                                LayoutParams.SOFT_INPUT_STATE_VISIBLE);

      try
      {
         dialogList = (ListView) ((LinearLayout) dialogView.getChildAt(2)).getChildAt(1);
         ArrayAdapter<String> a = new ArrayAdapter<String>(GSCustom.instance,
                         R.layout.add_game_dialog_listentry, CustomData.GAMETYPE);
         dialogList.setAdapter(a);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      dialogList.setOnItemClickListener(this);

      if (dialog != null)
      {
         if (dialogList != null)
            for (int i = 0; i < dialogList.getChildCount(); i++)
               ((TextView) dialogList.getChildAt(i)).setTextColor(
                     GSCustom.instance.getResources().getColor(
                           R.color.colorPopupText
                     )
               );

         dialog.show();

         EditText t = (EditText) dialog.findViewById(R.id.dialog_add_game_name);
         t.requestFocus();
      }

      return dialog;
   }

   @Override
   public void onItemClick(AdapterView<?> parent, View view, int position, long id)
   {
      if (dialogList != null)
         for (int i = 0; i < dialogList.getChildCount(); i++)
            ((TextView)dialogList.getChildAt(i)).setTextColor(
                  GSCustom.instance.getResources().getColor(
                        R.color.colorPopupText
                  )
            );
      if (view != null && dialogList != null)
         ((TextView)dialogList.getChildAt(position)).setTextColor(
               GSCustom.instance.getResources().getColor(
                     R.color.colorPopupTextSelected
               )
         );
      if (view != null)
         gameType = position;
   }

   @Override
   public void onClick(DialogInterface dialog, int which)
   {
      if (which == DialogInterface.BUTTON_POSITIVE)
      {
         try
         {
            gameName = ((EditText)dialogView.findViewById(R.id.dialog_add_game_name)).getText().toString();
            if (GetGameName() != null && GetGameName().length() > 0 &&
                  GetGameType() != -1)
            {
               CustomData.AddProfile(GetGameName(), GetGameType());
               CustomData.WriteData();
               GSCustom.instance.gameListAdapter.notifyDataSetChanged();
            }
            else
               Toast.makeText(GSCustom.instance, "Invalid Entry: must enter a Game Name and " +
                     "make a selection.", Toast.LENGTH_SHORT).show();

         } catch (Exception e)
         {
            e.printStackTrace();
            /*
            Toast.makeText(
                  GSCustom.instance, "popup: ok, get name:\n" + e.toString(), Toast.LENGTH_LONG
            ).show();*/
         }
         /*Toast.makeText(GSCustom.instance, "name: " + gameName + "\ntype: " +
                              gameType, Toast.LENGTH_SHORT).show();*/
      }


      InputMethodManager imm = (InputMethodManager) GSCustom.instance
            .getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(
            instance.dialog.findViewById(R.id.dialog_add_game_name).getWindowToken(), 0);
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
