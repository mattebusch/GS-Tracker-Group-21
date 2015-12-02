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

   private void Init(Context context, String name, Listener listener, String message,
                     View contentView, String title, int messageImageIndex, String[] buttonTexts,
                     int[] buttonValues, Object[] buttonTags)
   {
      this.context = context;
      this.name = name;
      this.buttonTags = new HashMap<Integer, Object>();
      listeners = new ArrayList<Listener>();
      if (listener != null)
         listeners.add(listener);

      AlertDialog.Builder b = new AlertDialog.Builder(context);
      int c;

      if (title != null && title.length() > 0)
      {
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

         titleLayout.addView(titleView);
         titleLayout.addView(divider);

         b.setCustomTitle(titleLayout);
      }
      if (contentView != null)
      {
         b.setView(RandomizeViewColors(contentView));
         for (int i = -1; i <= 1; i++)
            this.buttonTags.put(i, contentView);
      }
      else if (message != null)
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

      AlertDialog d = b.create();
      d.show();

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

   public QuickQuestionDialog(Context c, String name, Listener l,
                              String message, String title, String[] buttonTexts,
                              int[] buttonValues, Object[] buttonTags)
   {
      Init(c, name, l, message, null, title, -1, buttonTexts, buttonValues, buttonTags);
   }

   public QuickQuestionDialog(Context c, String name, Listener l, String message,
                              String title, String[] buttonTexts, int[] buttonValues)
   {
      Init(c, name, l, message, null, title, -1, buttonTexts, buttonValues, null);
   }

   public QuickQuestionDialog(Context c, String name, Listener l, View contentView,
                              String title, String[] buttonTexts, int[] buttonValues)
   {
      Init(c, name, l, null, contentView, title, -1, buttonTexts, buttonValues, null);
   }

   public QuickQuestionDialog(Context c, String name, Listener l, String message,
                              String title, int[] buttonValues)
   {
      Init(c, name, l, message, null, title, -1, null, buttonValues, null);
   }

   public QuickQuestionDialog(Context c, String name, Listener l, View contentView,
                              String title, int[] buttonValues)
   {
      Init(c, name, l, null, contentView, title, -1, null, buttonValues, null);
   }

   public void AddListeners(Iterable<Listener> z)
   {
      for (Listener i : z)
         if (!listeners.contains(i))
               listeners.add(i);
   }

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

   private int InvertColor(int c)
   {
      return Color.rgb(255 - Color.red(c), 255 - Color.green(c), 255 - Color.blue(c));
   }

   private int Dp(int px)
   {
      return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 14,
            context.getResources().getDisplayMetrics());
   }

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

   interface Listener
   {
      void onQuickQuestionDialogClick(String name, int response, Object tag);
   }
}