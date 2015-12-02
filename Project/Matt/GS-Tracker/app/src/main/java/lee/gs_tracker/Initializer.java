package lee.gs_tracker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.app.Activity;
import android.app.ExpandableListActivity;
import java.util.ArrayList;

public class Initializer {


    public static Parent initialize_Blizz(){
        ArrayList<Child> Games = new ArrayList<Child>();

        Parent BlizzardGames = new Parent();
        BlizzardGames.setTitle("Blizzard Games");
        int i;
        Child WoW = new Child();
        WoW.Title = "World of Warcraft";
        WoW.ChildName = "WoW";
        Games.add(WoW);

        Child Diablo = new Child();
        Diablo.Title = "Diablo III";
        Diablo.ChildName = "DiabloIII";
        Games.add(Diablo);

        Child Starcraft = new Child();
        Starcraft.Title = "Starcraft II";
        Starcraft.ChildName = "StarcraftII";
        Games.add(Starcraft);



        //Games.add("World of Warcraft");
        //Games.add("Diablo III");
        //Games.add("Starcraft II");

        BlizzardGames.setArrayChildren(Games);

        return BlizzardGames;
    }



    public static void initialize_templates(ArrayList<Parent> Parents){




    }


}
