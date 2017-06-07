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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realizer.sallado.DietMenuListActivity;
import com.realizer.sallado.OrderMenuListActivity;
import com.realizer.sallado.R;
import com.realizer.sallado.model.DietMenuModel;
import com.realizer.sallado.utils.ImageStorage;

import java.util.List;

/**
 * Created by Win on 09-05-2017.
 */

public class OrderMenuListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    public View convertView;
    public  Context context;
    List<DietMenuModel> dietMenuList;
    int totalCost = 0;

    public OrderMenuListAdapter(List<DietMenuModel> dietMenuList, Context context,int totalcost){
        this.context = context;
        this.dietMenuList = dietMenuList;
        layoutInflater =LayoutInflater.from(context);
        this.totalCost = totalcost;
    }
    @Override
    public int getCount() {
        return dietMenuList.size();
    }

    @Override
    public Object getItem(int position) {
        return dietMenuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return dietMenuList.size();
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
            this.convertView = layoutInflater.inflate(R.layout.order_menu_list_item_layout, null);

            holder = new ViewHolder();
            holder.menuImage = (ImageView) this.convertView.findViewById(R.id.img_menuImage);
            holder.typeImage = (ImageView) this.convertView.findViewById(R.id.img_type);
            holder.menuName = (TextView) this.convertView.findViewById(R.id.txt_menu_name);
            holder.menupPrice = (TextView) this.convertView.findViewById(R.id.txt_price);
            holder.count = (TextView) this.convertView.findViewById(R.id.txt_value);
            holder.plus = (Button) this.convertView.findViewById(R.id.btn_plus);
            holder.minus = (Button) this.convertView.findViewById(R.id.btn_minus);

            holder.plus.setTag(position);
            holder.minus.setTag(position);
            holder.count.setTag(position);

            int total = Integer.valueOf(dietMenuList.get(position).getMenuPrice()) * (Integer.valueOf(dietMenuList.get(position).getQuantity()));

            holder.menuName.setText(dietMenuList.get(position).getMenuName());
            holder.menupPrice.setText("₹ "+total);
            holder.count.setText(""+dietMenuList.get(position).getQuantity());

            if(dietMenuList.get(position).getMenuType().equalsIgnoreCase("Veg")){
                Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.veg);
                holder.typeImage.setImageBitmap(largeIcon);
            }
            else {
                Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.nonveg);
                holder.typeImage.setImageBitmap(largeIcon);
            }

            if(!dietMenuList.get(position).getMenuImage().isEmpty()){
                ImageStorage.setThumbnail(holder.menuImage,dietMenuList.get(position).getMenuImage());
            }

            this.convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) this.convertView.getTag();
        }


        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempPosition = (Integer) v.getTag();

                int counter = Integer.valueOf(holder.count.getText().toString());

                if(counter == 0){
                    /*dietMenuList.get(tempPosition).setQuantity(""+counter);
                    if(context instanceof DietMenuListActivity){
                        ((OrderMenuListActivity)context).orderItem(dietMenuList.get(tempPosition),"remove");
                    }*/
                }
                else if(counter == 1){
                    counter = counter - 1;
                    holder.count.setText(""+counter);

                    dietMenuList.get(tempPosition).setQuantity(""+counter);
                    if(context instanceof DietMenuListActivity){
                        ((OrderMenuListActivity)context).orderItem(dietMenuList.get(tempPosition),"update");
                    }

                    holder.menupPrice.setText("₹ "+(Integer.valueOf(dietMenuList.get(tempPosition).getMenuPrice()) * counter));
                    totalCost = totalCost - Integer.valueOf(dietMenuList.get(tempPosition).getMenuPrice());
                }
                else {
                    counter = counter - 1;
                    holder.count.setText(""+counter);

                    dietMenuList.get(tempPosition).setQuantity(""+counter);
                    if(context instanceof DietMenuListActivity){
                        ((OrderMenuListActivity)context).orderItem(dietMenuList.get(tempPosition),"update");
                    }

                    holder.menupPrice.setText("₹ "+(Integer.valueOf(dietMenuList.get(tempPosition).getMenuPrice()) * counter));
                    totalCost = totalCost - Integer.valueOf(dietMenuList.get(tempPosition).getMenuPrice());
                }
                if(context instanceof OrderMenuListActivity){
                    ((OrderMenuListActivity)context).changePrice(totalCost);
                }

            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempPosition = (Integer) v.getTag();
                int counter = Integer.valueOf(holder.count.getText().toString());
                    counter = counter + 1;
                    holder.count.setText(""+counter);
                holder.menupPrice.setText("₹ "+(Integer.valueOf(dietMenuList.get(tempPosition).getMenuPrice()) * counter));

                dietMenuList.get(tempPosition).setQuantity(""+counter);
                if(context instanceof DietMenuListActivity){
                    ((OrderMenuListActivity)context).orderItem(dietMenuList.get(tempPosition),"update");
                }

                totalCost = totalCost + Integer.valueOf(dietMenuList.get(tempPosition).getMenuPrice());
                if(context instanceof OrderMenuListActivity){
                    ((OrderMenuListActivity)context).changePrice(totalCost);
                }
            }
        });


        return this.convertView;
    }
    static class ViewHolder{


        ImageView menuImage,typeImage;
        TextView menuName,menupPrice,count;
        Button plus,minus;
    }
}



