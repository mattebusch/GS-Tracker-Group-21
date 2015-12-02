package lee.gs_tracker.gsCustom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Joe Paul on 11/25/2015.
 *
 * creates an AlertDialog from a title string and a message string or custom view and randomizes
 *    all the background and text colors / text sizes of every nested viewgroup and textview
 *    in the dialog
 * designed so that it can be invoked in a single line and has its own listener
 *    (QuickQuestionDialog.Listener)
 *
 * see comments for Init() and Listener at bottom for detailed usage
 */
public class QuickQuestionDialog implements DialogInterface.OnClickListener
{
   private static Random rnd = null;
   private static int[] buttonWhiches = new int[]{DialogInterface.BUTTON_NEGATIVE,
         DialogInterface.BUTTON_NEUTRAL, DialogInterface.BUTTON_POSITIVE};

   public static int WARNING = 1;
   public static int EXCLAMATION = 2;
   public static int ERROR = 4;
   public static int OK = 8;
   public static int CANCEL = 16;

   private Context context;
   private HashMap<Integer, Object> buttonTags = null;
   private String name;
   private int response = 0;
   private ArrayList<Listener> listeners = null;

   // randomizes the colors / text sizes of a view and all its descendents
   private View RandomizeViewColors(View v)
   {
      int c = GenerateRandomColor();
      v.setBackgroundColor(c);
      if (v instanceof ViewGroup)
      {
         ViewGroup vg = (ViewGroup) v;
         for (int i = 0; i < vg.getChildCount(); i++)
            RandomizeViewColors(vg.getChildAt(i));
      }
      else if (v instanceof TextView)
         ((TextView) v).setTextColor(InvertColor(c));
      return v;
   }

   /*
   * private method to initialize data, called by all constructors
   * arguments:
   *     context:       passes the context from the calling activity
   *     name:          designates a name for this quickquestion dialog that is passed to the
   *                       listener
   *     listener:      a class that implements QuickQuestionDialog.Listener that is called when
   *                       the user clicks a button
   *     message:       the message to display in the body of the dialog
   *     contentview:   a custom view that can be used to create the content of the dialog
   *     title:         the title for the dialog, can be null
   *     messageImageIndex:   not used
   *     buttonTexts:   the texts to be displayed on the buttons, can be null and default button
   *                       texts will be used
   *     buttonValues:  must be specified; the values of the buttons, only negative, zero, and
   *                       positive ranges are significant (negative = no, zero = ok,
   *                       positive = yes) only buttonValues needs to be specified and buttonTexts
   *                       and buttonTags can be null
   *     buttonTags:    an object to be passed to the listener corresponding to each button, can
   *                       be null
   *
   *     only a context and buttonValues really need to be specified
   *
   *     can pass an array to it as an argument by writing Init(..., new int[]{-1, 1}, ...)
   *        for example, so can be used in a single line
   */
   private void Init(Context context, String name, Listener listener, String message,
                     View contentView, String title, int messageImageIndex, String[] buttonTexts,
                     int[] buttonValues, Object[] buttonTags)
   {
      // initialize data
      this.context = context;
      this.name = name;
      this.buttonTags = new HashMap<Integer, Object>();
      listeners = new ArrayList<Listener>();
      if (listener != null)
         listeners.add(listener);

      // create alertdialog builder
      AlertDialog.Builder b = new AlertDialog.Builder(context);
      int c;

      // if title is not null, add to alert dialog
      if (title != null && title.length() > 0)
      {
         // linear layout for title
         LinearLayout titleLayout = new LinearLayout(context);
         titleLayout.setLayoutParams
               (
                     new LinearLayout.LayoutParams
                           (
                                 LinearLayout.LayoutParams.WRAP_CONTENT,
                                 LinearLayout.LayoutParams.WRAP_CONTENT
                           )
               );
         titleLayout.setBackgroundColor(c = GenerateRandomColor());
         titleLayout.setOrientation(LinearLayout.VERTICAL);

         // textview for title text
         TextView titleView = new TextView(context);
         titleView.setLayoutParams
               (
                     new LinearLayout.LayoutParams
                           (
                                 LinearLayout.LayoutParams.WRAP_CONTENT,
                                 LinearLayout.LayoutParams.WRAP_CONTENT
                           )
               );
         titleView.setPadding(Dp(5), Dp(5), Dp(5), Dp(5));
         titleView.setTextColor(InvertColor(c));
         titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20 + rnd.nextInt(11));
         titleView.setText(title);

         // a divider to between title and content in dialog
         ImageView divider = new ImageView(context);
         divider.setLayoutParams
               (
                     new LinearLayout.LayoutParams
                           (
                                 LinearLayout.LayoutParams.MATCH_PARENT,
                                 Dp(8)
                           )
               );
         divider.setBackgroundColor(GenerateRandomColor());
         divider.setScaleType(ImageView.ScaleType.FIT_XY);
         divider.setPadding(0, Dp(5), 0, Dp(5));

         // set title view in dialog
         titleLayout.addView(titleView);
         titleLayout.addView(divider);
         b.setCustomTitle(titleLayout);
      }
      // passing init a custom view takes precedence over a string message
      if (contentView != null)
      {
         // randomize colors in the custom view
         b.setView(RandomizeViewColors(contentView));

         // if a custom view is specified, the view is passed back to the listener as the tag
         for (int i = -1; i <= 1; i++)
            this.buttonTags.put(i, contentView);
      }
      else if (message != null) // user specifies a string message, randomize and set view
      {
         TextView textView = new TextView(context);
         textView.setLayoutParams
               (
                     new LinearLayout.LayoutParams
                           (
                                 LinearLayout.LayoutParams.MATCH_PARENT,
                                 LinearLayout.LayoutParams.WRAP_CONTENT
                           )
               );
         textView.setTextColor(c = GenerateRandomColor());
         textView.setBackgroundColor(InvertColor(c));
         textView.setText(message);
         textView.setPadding(Dp(5), Dp(5), Dp(5), Dp(5));
         textView.setMinLines(1);
         textView.setMaxLines(4);
         textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16 + rnd.nextInt(7));

         b.setView(textView);
      }

      // add buttons to dialog as specified, strings, values, and object tags
      for (int i = 0; i < buttonValues.length; i++)
      {
         String text = null;
         if (buttonTexts != null && i < buttonTexts.length && buttonTexts[i].length() > 0)
            text = buttonTexts [i];
         else if (buttonValues [i] < 0)
            text = "Cancel";
         else if (buttonValues [i] > 0)
            text = "Yes";
         else
            text = "OK";
         if (buttonValues [i] < 0)
            b.setNegativeButton(text, this);
         else if (buttonValues [i] > 0)
            b.setPositiveButton(text, this);
         else
            b.setNeutralButton(text, this);
         if (buttonTags != null && i < buttonTags.length)
            this.buttonTags.put(
                  buttonValues [i] > 0 ? 1 : buttonValues [i] < 0 ? -1 : 0,
                  buttonTags [i]
            );
      }

      // create and show dialog
      AlertDialog d = b.create();
      d.show();

      // randomize button colors, needs to be done after dialog is shown otherwise
      //    d.getButton will return null
      for (int i = 0; i < 3; i++)
      {
         Button bu;

         if ((bu = d.getButton(buttonWhiches [i])) != null)
         {
            bu.setTextColor(c = GenerateRandomColor());
            bu.setBackgroundColor(InvertColor(c));
            bu.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20 + rnd.nextInt(11));
            bu.invalidate();
         }
      }
   }

   // CONSTRUCTORS:

   // specifies a context, name, listener, message, title, button texts, values, and tags
   public QuickQuestionDialog(Context c, String name, Listener l,
                              String message, String title, String[] buttonTexts,
                              int[] buttonValues, Object[] buttonTags)
   {
      Init(c, name, l, message, null, title, -1, buttonTexts, buttonValues, buttonTags);
   }

   // does not specify button tags
   public QuickQuestionDialog(Context c, String name, Listener l, String message,
                              String title, String[] buttonTexts, int[] buttonValues)
   {
      Init(c, name, l, message, null, title, -1, buttonTexts, buttonValues, null);
   }

   // specifies a custom view instead of a message, view is automatically the tag sent to the
   //    listener
   public QuickQuestionDialog(Context c, String name, Listener l, View contentView,
                              String title, String[] buttonTexts, int[] buttonValues)
   {
      Init(c, name, l, null, contentView, title, -1, buttonTexts, buttonValues, null);
   }

   // does not specify button texts, default texts are used depending on the range of each
   //    button value in the buttonValues array (1 = "yes", 0 = "ok", -1 = "no");
   //    can pass an array to the constructor as new int[]{-1, 0} for ("no", "yes")
   public QuickQuestionDialog(Context c, String name, Listener l, String message,
                              String title, int[] buttonValues)
   {
      Init(c, name, l, message, null, title, -1, null, buttonValues, null);
   }

   // specifies a custom view instead of a message; name, listener, contentview (or message in
   //    constructor above), and title can all be null and will still work but quite pointless
   public QuickQuestionDialog(Context c, String name, Listener l, View contentView,
                              String title, int[] buttonValues)
   {
      Init(c, name, l, null, contentView, title, -1, null, buttonValues, null);
   }

   // adds additional listeners; can still be used in a single line as:
   //    new QuickQuestionDialog(...).AddListeners(...)
   public void AddListeners(Iterable<Listener> z)
   {
      for (Listener i : z)
         if (!listeners.contains(i))
               listeners.add(i);
   }

   // private method to generate a random color
   private int GenerateRandomColor()
   {
      if (rnd == null)
         rnd = new Random(System.currentTimeMillis());
      String colorString = "#";
      int i, d;
      for (i = d = 0; i < 6; i++)
         colorString += "0123456789ABCDEF".substring(d = rnd.nextInt(16), d + 1);
      return Color.parseColor(colorString);
   }

   // private method to invert a color
   private int InvertColor(int c)
   {
      return Color.rgb(255 - Color.red(c), 255 - Color.green(c), 255 - Color.blue(c));
   }

   // converts pixels to android device-independent pixels, method body is brief but Dp is much
   //    easier to remember
   private int Dp(int px)
   {
      return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 14,
            context.getResources().getDisplayMetrics());
   }

   // user selects a button; send response to all listeners
   @Override
   public void onClick(DialogInterface dialog, int which)
   {
      Object tag;
      if (which == DialogInterface.BUTTON_NEGATIVE)
         response = -1;
      else if (which == DialogInterface.BUTTON_POSITIVE)
         response = 1;
      else
         response = 0;
      for (Listener l : listeners)
         l.onQuickQuestionDialogClick(name, response, buttonTags.get(response));
   }

   // QuickQuestionDialog.Listener interface, method onQuickQuestionDialogClick will be called
   //    for each listener specified when creating the QuickQuestionDialog
   // will send the listener the name of the dialog, the response, (yes = 1, no = -1, ok = 0)
   //    and the tag (user specified for a message or the constent view if a custom view is
   //    specified)
   interface Listener
   {
      void onQuickQuestionDialogClick(String name, int response, Object tag);
   }
}