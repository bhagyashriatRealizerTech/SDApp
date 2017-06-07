package com.realizer.sallado.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realizer.sallado.DietMenuListActivity;
import com.realizer.sallado.R;
import com.realizer.sallado.model.DietMenuModel;
import com.realizer.sallado.model.DietPlanCalenderModel;
import com.realizer.sallado.utils.ImageStorage;

import java.util.List;

/**
 * Created by Win on 09-05-2017.
 */

public class DietPlanCalenderListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    public View convertView;
    public  Context context;
    List<DietPlanCalenderModel> dietPlanCalenderList;
    int totalCost = 0;

    public DietPlanCalenderListAdapter(List<DietPlanCalenderModel> dietPlanCalenderList, Context context){
        this.context = context;
        this.dietPlanCalenderList = dietPlanCalenderList;
        layoutInflater =LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return dietPlanCalenderList.size();
    }

    @Override
    public Object getItem(int position) {
        return dietPlanCalenderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return dietPlanCalenderList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        final ViewHolder holder;
        this.convertView = convertView;

        if (convertView == null) {
            this.convertView = layoutInflater.inflate(R.layout.diet_program_calender_list_item, null);

            holder = new ViewHolder();
            holder.day = (TextView) this.convertView.findViewById(R.id.txt_day);
            holder.breakfast = (TextView) this.convertView.findViewById(R.id.txt_breakfast);
            holder.lunch = (TextView) this.convertView.findViewById(R.id.txt_lunch);
            holder.dinner = (TextView) this.convertView.findViewById(R.id.txt_dinner);


            final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
            final StyleSpan iss = new StyleSpan(android.graphics.Typeface.ITALIC); //Span to make text italic


                int end = 0;
                final SpannableStringBuilder sb = new SpannableStringBuilder(dietPlanCalenderList.get(position).getBreakfast());
                end = dietPlanCalenderList.get(position).getBreakfast().split(":")[0].length();
                sb.setSpan(bss, 0, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                holder.breakfast.setText(sb);

            final SpannableStringBuilder sb1 = new SpannableStringBuilder(dietPlanCalenderList.get(position).getLunch());
            end = dietPlanCalenderList.get(position).getLunch().split(":")[0].length();
            sb1.setSpan(bss, 0, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                holder.lunch.setText(sb1);

            final SpannableStringBuilder sb2 = new SpannableStringBuilder(dietPlanCalenderList.get(position).getDinner());
            end = dietPlanCalenderList.get(position).getDinner().split(":")[0].length();
            sb2.setSpan(bss, 0, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                holder.dinner.setText(sb2);

                holder.day.setText(dietPlanCalenderList.get(position).getDay());

            this.convertView.setTag(holder);
        }


        else {
            holder = (ViewHolder) this.convertView.getTag();
        }


        return this.convertView;
    }
    static class ViewHolder{

        TextView breakfast,lunch,dinner,day;
    }
}


