package lee.gs_tracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.ExpandableListView;
import android.view.ViewGroup;
import android.app.Activity;
import android.app.ExpandableListActivity;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ExpandableListView mExpandableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        mExpandableList = (ExpandableListView) findViewById(R.id.gameStatContent);

        ArrayList<Parent> arrayParents = new ArrayList<Parent>();
        ArrayList<String> arrayChildren = new ArrayList<String>();


        int i;
        arrayParents.add(Initializer.initialize_Blizz());


        Parent FPS = new Parent();
        FPS.setTitle("FPS");
        ArrayList<Child> FPSChildren = new ArrayList<>();

        for (i = 0; i < 3; i++) {
            Child FPSChild = new Child();
            FPSChild.ChildName = "Template" + (i + 1);
            FPSChild.Title = "Template" + (i+1);
            FPSChildren.add(FPSChild);
            //FPSChildren.add("Template" + (i + 1));
        }

        FPS.setArrayChildren(FPSChildren);
        arrayParents.add(FPS);

        //here we set the parents and the children
        /*for (int i = 0; i < 10; i++){
            //for each "i" create a new Parent object to set the title and the children
            Parent parent = new Parent();
            parent.setTitle("Parent " + i);

            arrayChildren = new ArrayList<String>();
            for (int j = 0; j < 10; j++) {
                arrayChildren.add("Child " + j);
            }
            parent.setArrayChildren(arrayChildren);

            //in this array we add the Parent object. We will use the arrayParents at the setAdapter
            arrayParents.add(parent);
        }*/

        //sets the adapter that provides data to the list.
        mExpandableList.setAdapter(new MyCustomAdapter(MainActivity.this, arrayParents));

    }
    public void goToStat(View view){
        String TagName = view.getTag().toString();
        if(TagName == "WoW") {
            Intent intent = new Intent(this, WoWCredentials.class);
            startActivity(intent);
        }
        if(TagName == "DiabloIII"){
            Intent intent = new Intent(this, DiabloCredentials.class);
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

