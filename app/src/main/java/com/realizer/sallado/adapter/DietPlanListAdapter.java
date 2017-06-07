package com.realizer.sallado.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.realizer.sallado.DietPlanDetailActivity;
import com.realizer.sallado.R;
import com.realizer.sallado.databasemodel.DietProgram;
import com.realizer.sallado.model.DietPlanModel;
import com.realizer.sallado.model.DoctorListModel;
import com.realizer.sallado.utils.ImageStorage;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Win on 09-05-2017.
 */

public class DietPlanListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    public View convertView;
    public  Context context;
    List<DietProgram> dietList;

    public DietPlanListAdapter(List<DietProgram> dietList, Context context){
        this.context = context;
        this.dietList = dietList;
        layoutInflater =LayoutInflater.from(context);
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
            this.convertView = layoutInflater.inflate(R.layout.diet_program_list_item_layout, null);

            holder = new ViewHolder();
            holder.dietImage = (ImageView) this.convertView.findViewById(R.id.img_pkg);
            holder.name = (TextView) this.convertView.findViewById(R.id.txt_pkgname);
            holder.price = (TextView) this.convertView.findViewById(R.id.txt_price);
            holder.ratings = (TextView) this.convertView.findViewById(R.id.txt_ratings);
            holder.includes = (TextView) this.convertView.findViewById(R.id.txt_includes);
            holder.detail = (Button) this.convertView.findViewById(R.id.btn_detail);
            holder.detail.setTag(position);
            holder.book = (Button) this.convertView.findViewById(R.id.btn_book);

            holder.name.setText(dietList.get(position).getProgramName());
            holder.price.setText(dietList.get(position).getProgramPrice());
            holder.ratings.setText(dietList.get(position).getProgramRatings());

/*            if(dietList.get(position).getProgramName().equalsIgnoreCase("Weight Gain")){
                Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.weightgain);
                holder.dietImage.setImageBitmap(largeIcon);
            }
            else if(dietList.get(position).getProgramName().equalsIgnoreCase("Weight Loss")){
                Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.weightloss);
                holder.dietImage.setImageBitmap(largeIcon);
            }
            else {
                Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.athlit);
                holder.dietImage.setImageBitmap(largeIcon);
            }*/

            if(!dietList.get(position).getProgramThumbnailUrl().isEmpty()){
                ImageStorage.setThumbnail(holder.dietImage,dietList.get(position).getProgramThumbnailUrl());
            }


            this.convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) this.convertView.getTag();
        }

        holder.detail.setVisibility(View.GONE);
        holder.book.setVisibility(View.GONE);

       /* holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tempPosition = (Integer) view.getTag();
                Intent intent = new Intent(context, DietPlanDetailActivity.class);
                intent.putExtra("ProgramId",dietList.get(tempPosition).getProgramId());
                context.startActivity(intent);
            }
        });*/

        return this.convertView;
    }
    static class ViewHolder{

        ImageView dietImage;
        TextView name,price,ratings,includes;
        Button detail,book;
    }
}


