package com.realizer.sallado.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.sallado.R;
import com.realizer.sallado.databasemodel.DayProgram;
import com.realizer.sallado.databasemodel.DietProgram;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.ImageStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Win on 09-05-2017.
 */

public class DietDayProgramListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    public View convertView;
    public  Context context;
    List<DayProgram> dietList;
    Calendar calendar;
    Typeface face;
    String currentDate;
    boolean flag;

    public DietDayProgramListAdapter(List<DayProgram> dietList, Date startdate, Context context){
        this.context = context;
        this.dietList = dietList;
        layoutInflater =LayoutInflater.from(context);
        calendar = Calendar.getInstance();
        calendar.setTime(startdate);
        flag = false;
        face = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");
        currentDate = Constants.getMediumDate(new Date());


    }
    @Override
    public int getCount() {
        return dietList.size();
    }

    @Override
    public Object getItem(int position) {
        return dietList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return dietList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        ViewHolder holder;
        this.convertView = convertView;


        if (convertView == null) {
            this.convertView = layoutInflater.inflate(R.layout.my_plan_day_list_item, null);

            holder = new ViewHolder();
            holder.day = (TextView) this.convertView.findViewById(R.id.txt_dayNo);
            holder.title = (TextView) this.convertView.findViewById(R.id.txt_Dayname);
            holder.desc = (TextView) this.convertView.findViewById(R.id.txt_dayDesc);
            holder.done = (TextView) this.convertView.findViewById(R.id.txt_dayDone);
            holder.date = (TextView) this.convertView.findViewById(R.id.txt_date);
            holder.done.setTypeface(face);

            holder.day.setText("Day "+dietList.get(position).getDay());
            holder.title.setText(dietList.get(position).getTitle());
            holder.desc.setText(dietList.get(position).getDesc());
            holder.date.setText(Constants.getMediumDate(calendar.getTime()));

            if(currentDate.equalsIgnoreCase(holder.date.getText().toString()))
                flag = true;

            if(flag)
                holder.done.setVisibility(View.GONE);
            else {
                holder.done.setVisibility(View.VISIBLE);
                holder.done.setText(R.string.fa_check_circle);
            }

            calendar.add(Calendar.DATE,1);

            this.convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) this.convertView.getTag();
        }


        return this.convertView;
    }
    static class ViewHolder{

        TextView day,title,desc,done,date;
    }
}


