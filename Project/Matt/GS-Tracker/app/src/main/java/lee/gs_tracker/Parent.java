package lee.gs_tracker;
import java.util.ArrayList;

public class Parent {
    private String mTitle;
    private ArrayList<Child>  mArrayChildren;


    public String getTitle(){
        return mTitle;
    }

    public void setTitle(String title){
        mTitle = title;
    }

    public ArrayList<Child> getArrayChildren(){
        return mArrayChildren;
    }

    public void setArrayChildren(ArrayList<Child> arrayChildren){
        mArrayChildren = arrayChildren;

    }
}
