package lee.gs_tracker.gsCustom;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import lee.gs_tracker.R;

/**
 * Created by Joe Paul on 11/30/2015.
 *
 * manages and displays a shooter profile
 *
 * displays player's name, weapon of choice, allows them to increment and reset num kills/deaths
 *    and computes a kill/death ratio
 */

public class Shooter extends AppCompatActivity implements View.OnClickListener,
      TextWatcher
{
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.shooter_activity);

      // show back button in action bar
      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null)
         actionBar.setDisplayHomeAsUpEnabled(true);

      // set actionbar title to profile name received from GSCustom
      if (getIntent().getStringExtra("gamename") != null)
         setTitle(getIntent().getStringExtra("gamename"));

      // set onclick listeners for buttons
      findViewById(R.id.shooter_kill).setOnClickListener(this);
      findViewById(R.id.shooter_die).setOnClickListener(this);
      findViewById(R.id.shooter_reset).setOnClickListener(this);

      // load name/weapon from CustomData, add text changed listeners to update name/weapon
      //    back to CustomData on change
      EditText editName = (EditText)findViewById(R.id.shooter_name);
      EditText editWeapon = (EditText)findViewById(R.id.shooter_weapon);
      CustomData.Shooter.LoadInit();
      if (CustomData.Shooter.playerName != null && CustomData.Shooter.playerName.length() != 0)
         editName.setText(CustomData.Shooter.playerName);
      if (CustomData.Shooter.weapon != null && CustomData.Shooter.weapon.length() != 0)
         editWeapon.setText(CustomData.Shooter.weapon);
      editName.addTextChangedListener(this);
      editWeapon.addTextChangedListener(this);

      // update UI
      Update();
   }

   // updates all UI fields
   public void Update()
   {
      ((TextView)findViewById(R.id.shooter_num_deaths)).setText(CustomData.Shooter.numDeaths + "");
      ((TextView)findViewById(R.id.shooter_num_kills)).setText(CustomData.Shooter.numKills + "");
      if (CustomData.Shooter.numDeaths == 0)
         ((TextView)findViewById(R.id.shooter_ratio)).setText(null);
      else
         ((TextView)findViewById(R.id.shooter_ratio)).setText(
               ((float)CustomData.Shooter.numKills / CustomData.Shooter.numDeaths) + ""
         );
   }

   @Override
   public void onClick(View v)
   {
      if (v.getId() == R.id.shooter_die) // increment # deaths
         CustomData.Shooter.numDeaths++;
      else if (v.getId() == R.id.shooter_kill) // increment # kills
         CustomData.Shooter.numKills++;
      else if (v.getId() == R.id.shooter_reset) // reset # deats, # kills
         CustomData.Shooter.numKills = CustomData.Shooter.numDeaths = 0;
      Update();
   }

   @Override
   public void afterTextChanged(Editable s) // update name/weapon in CustomData on change
   {
      EditText e;
      if ((e = (EditText) findViewById(R.id.shooter_name)).isFocused())
         CustomData.Shooter.playerName = e.getText().toString();
      else if ((e = (EditText) findViewById(R.id.shooter_weapon)).isFocused())
         CustomData.Shooter.weapon = e.getText().toString();
   }

   @Override
   public void onTextChanged(CharSequence s, int start, int before, int count){}

   @Override
   public void beforeTextChanged(CharSequence s, int start, int count, int after){}

   @Override
   protected void onResume() // reload profile data onResume
   {
      super.onResume();

      CustomData.Shooter.LoadInit();
      /*Toast.makeText(
            this, "loaded " + (CustomData.Shooter.playerName + "") +
                  "'s victim count", Toast.LENGTH_SHORT
      ).show();*/
   }

   @Override
   protected void onStop() // UpdateUnload profile data onStop
   {
      super.onStop();

      boolean writeSuccess = CustomData.Shooter.UpdateUnload();
      /*Toast.makeText(this, "write shooter data: " + (writeSuccess ? "success" : "failure"),
                     Toast.LENGTH_SHORT).show();*/
   }
}
