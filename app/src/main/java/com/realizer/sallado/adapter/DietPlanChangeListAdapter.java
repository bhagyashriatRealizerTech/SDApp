package com.realizer.sallado.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realizer.sallado.DietChangePlanActivity;
import com.realizer.sallado.R;
import com.realizer.sallado.databasemodel.DayProgram;
import com.realizer.sallado.databasemodel.Dish;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.ImageStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Win on 09-05-2017.
 */

public class DietPlanChangeListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    public View convertView;
    public  Context context;
    List<Dish> dietList;
    List<Boolean> isChecked;

    public DietPlanChangeListAdapter(List<Dish> dietList, Context context){
        this.context = context;
        this.dietList = dietList;
        layoutInflater =LayoutInflater.from(context);
        isChecked = new ArrayList<>();
        for(int i=0;i <= this.dietList.size();i++)
        {
            isChecked.add(false);
        }

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
    public View getView(int position, final View convertView, ViewGroup parent) {
        ViewHolder holder;
        this.convertView = convertView;


        if (convertView == null) {
            this.convertView = layoutInflater.inflate(R.layout.diet_plan_change_list_layout, null);

            holder = new ViewHolder();

            holder.title = (TextView) this.convertView.findViewById(R.id.txt_item);
            holder.desc = (TextView) this.convertView.findViewById(R.id.txt_itemDesc);
            holder.imageView= (ImageView) this.convertView.findViewById(R.id.img_item);
            holder.checkBox = (CheckBox) this.convertView.findViewById(R.id.chk_item);
            holder.root = (LinearLayout) this.convertView.findViewById(R.id.rootlayout);
            holder.root.setTag(position);

            this.convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) this.convertView.getTag();
        }


        holder.title.setText(dietList.get(position).getDishName());
        holder.desc.setText(dietList.get(position).getDishDescription());

        holder.checkBox.setChecked(isChecked.get(position));

        if(!dietList.get(position).getDishThumbnail().isEmpty()){
            ImageStorage.setThumbnail(holder.imageView,dietList.get(position).getDishThumbnail());
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (Integer) view.getTag();

                if(isChecked.get(pos))
                    isChecked.set(pos,false);
                else {
                    for(int i=0;i<isChecked.size();i++)
                    {
                        if(i == pos) {
                            isChecked.set(i, true);
                            if(context instanceof DietChangePlanActivity)
                                ((DietChangePlanActivity)context).setDish(dietList.get(pos).getDishId());
                        }
                        else
                            isChecked.set(i,false);
                    }

                }
                notifyDataSetChanged();

            }
        });


        return this.convertView;
    }
    static class ViewHolder{

        TextView title,desc;
        ImageView imageView;
        CheckBox checkBox;
        LinearLayout root;
    }
}


