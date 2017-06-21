package com.realizer.sallado.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.realizer.sallado.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Win on 22-05-2017.
 */

public class Constants {
    public static String ACTIVE_DASHBOARD_IMAGE = "ActiveDashboardImages/";

    /**
     * @param title to set
     * @return title SpannableString
     */
    public static SpannableString actionBarTitle(String title, Context context) {
        SpannableString s = new SpannableString(title);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/font.ttf");
        s.setSpan(new CustomTypefaceSpan("", face), 0, s.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return s;
    }

    public static Date convertTime(String time){
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        Date date = null;
        try {
             date = parseFormat.parse(time);
             String outdate = displayFormat.format(date);
             date = displayFormat.parse(outdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getTimeString(Date time){
        String outTime="";
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
        Date date;
        outTime = inputFormat.format(time);
        String outdate = "";
        try {
            date = inputFormat.parse(outTime);
             outdate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outdate;
    }

    public static int timeDiffernce(String startTime, String endtime){
        int outTime=0;
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        Date date1,date2;
        try {
            date1 = parseFormat.parse(startTime);
            date2 = parseFormat.parse(endtime);
            long millis = date2.getTime() - date1.getTime();
            int Hours = (int) millis/(1000 * 60 * 60);
            int Mins =(int)  millis % (1000*60*60);
           outTime = (Hours * 3) + (((int)(Mins/60)) * 3);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outTime;
    }

    public static List<String> getTimeSlot(String startTime, String endtime){
        List<String> slot = new ArrayList<>();
        if(startTime == null || endtime == null){

        }
        else {
            int limit = timeDiffernce(startTime, endtime);
            Date d = convertTime(startTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            for (int i = 0; i < limit; i++) {
                slot.add(getTimeString(cal.getTime()));
                cal.add(Calendar.MINUTE, 20);
            }
        }
        return slot;
    }

    public static String getCurrentDateTime(){
        String outdate="";
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy-HH:mm a");
        outdate = df.format(new Date());
        return outdate;
    }

    public static String getMediumDate(Date date){
        String outdate="";
        SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy");
        outdate = output.format(date);
        return outdate;
    }

    public static String getMedDate(String date){
        String outdate="";
        SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat output = new SimpleDateFormat("dd MMM");

        try {
            Date date1 = input.parse(date);
            outdate = output.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //outdate = output.format(date);
        return outdate;
    }

    public static String getDayOfWeek(int day){
        switch (day) {
            case Calendar.SUNDAY:
                return "Sun";

            case Calendar.MONDAY:
                return "Mon";

            case Calendar.TUESDAY:
                return "Tue";

            case Calendar.WEDNESDAY:
                return "Wed";

            case Calendar.THURSDAY:
                return "Thu";

            case Calendar.FRIDAY:
                return "Fri";

            case Calendar.SATURDAY:
                return "Sat";
        }
        return "Worng Day";
    }

    public static int getDayOfWeekDiff(String day){
        switch (day) {
            case "Sunday":
                return Calendar.SUNDAY;

            case "Monday":
                return Calendar.MONDAY;

            case "Tuesday":
                return Calendar.TUESDAY;

            case "Wednesday":
                return Calendar.WEDNESDAY;

            case "Thursday":
                return Calendar.THURSDAY;

            case "Friday":
                return Calendar.FRIDAY;

            case "Saturday":
                return Calendar.SATURDAY;
        }
        return 0;
    }

    public static void alertDialog(final Context context, String title, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialoglayout = inflater.inflate(R.layout.custom_dialogbox, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialoglayout);

        RelativeLayout relativeLayout = (RelativeLayout) dialoglayout.findViewById(R.id.layout_buttton);
        Button buttonok= (Button) dialoglayout.findViewById(R.id.alert_btn_ok);
        TextView titleName=(TextView) dialoglayout.findViewById(R.id.alert_dialog_title);
        TextView alertMsg=(TextView) dialoglayout.findViewById(R.id.alert_dialog_message);
        TextView close=(TextView) dialoglayout.findViewById(R.id.txt_close);
        close.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));

        relativeLayout.setVisibility(View.GONE);

        final AlertDialog alertDialog = builder.create();

        buttonok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        titleName.setText(title);
        alertMsg.setText(message);

        alertDialog.show();

    }

}
