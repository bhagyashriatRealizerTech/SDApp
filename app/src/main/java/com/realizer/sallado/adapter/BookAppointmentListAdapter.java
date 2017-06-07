package com.realizer.sallado.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.realizer.sallado.utils.ImageStorage;

import java.util.List;

/**
 * Created by Win on 09-05-2017.
 */

public class BookAppointmentListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    public View convertView;
    public  Context context;
    List<String> timeSlot;
    int totalCost = 0;

    public BookAppointmentListAdapter(List<String> timeSlot, Context context){
        this.context = context;
        this.timeSlot = timeSlot;
        layoutInflater =LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return timeSlot.size();
    }

    @Override
    public Object getItem(int position) {
        return timeSlot.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return timeSlot.size();
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
            this.convertView = layoutInflater.inflate(R.layout.book_apointment_grid_item_layout, null);

            holder = new ViewHolder();
            holder.slot = (TextView) this.convertView.findViewById(R.id.txt_slot);

            holder.slot.setText(timeSlot.get(position));

            this.convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) this.convertView.getTag();
        }


        return this.convertView;
    }
    static class ViewHolder{

        TextView slot;
    }
}


