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
import com.realizer.sallado.databasemodel.Dish;
import com.realizer.sallado.databasemodel.OrderedFood;
import com.realizer.sallado.model.DietMenuModel;
import com.realizer.sallado.model.DietPlanModel;
import com.realizer.sallado.utils.ImageStorage;

import java.util.List;

/**
 * Created by Win on 09-05-2017.
 */

public class DietMenuListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    public View convertView;
    public  Context context;
    List<Dish> dietMenuList;
    int totalCost = 0;

    public DietMenuListAdapter(List<Dish> dietMenuList, Context context){
        this.context = context;
        this.dietMenuList = dietMenuList;
        layoutInflater =LayoutInflater.from(context);
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
            this.convertView = layoutInflater.inflate(R.layout.diet_menu_list_item_layout, null);

            holder = new ViewHolder();
            holder.menuImage = (ImageView) this.convertView.findViewById(R.id.img_menu);
            holder.typeImage = (ImageView) this.convertView.findViewById(R.id.img_menu_veg);
            holder.menuName = (TextView) this.convertView.findViewById(R.id.txt_menu_name);
            holder.menupPrice = (TextView) this.convertView.findViewById(R.id.txt_menu_price);
            holder.menuRatings = (TextView) this.convertView.findViewById(R.id.txt_menu_ratings);
            holder.count = (TextView) this.convertView.findViewById(R.id.txt_value);
            holder.add = (Button) this.convertView.findViewById(R.id.btn_add);
            holder.plus = (Button) this.convertView.findViewById(R.id.btn_plus);
            holder.minus = (Button) this.convertView.findViewById(R.id.btn_minus);
            holder.plusminus = (LinearLayout) this.convertView.findViewById(R.id.layout_plusminus);

            holder.add.setTag(position);
            holder.plus.setTag(position);
            holder.minus.setTag(position);
            holder.count.setTag(position);

            holder.menuName.setText(dietMenuList.get(position).getDishName());
            holder.menupPrice.setText("₹ "+dietMenuList.get(position).getDishPrice());
            holder.menuRatings.setText(dietMenuList.get(position).getDishRatings());

            if(dietMenuList.get(position).getDishType().equalsIgnoreCase("Veg")){
                Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.veg);
                holder.typeImage.setImageBitmap(largeIcon);
            }
            else {
                Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.nonveg);
                holder.typeImage.setImageBitmap(largeIcon);
            }

            if(!dietMenuList.get(position).getDishThumbnail().isEmpty()){
                ImageStorage.setThumbnail(holder.menuImage,dietMenuList.get(position).getDishThumbnail());
            }

            this.convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) this.convertView.getTag();
        }

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempPosition = (Integer) v.getTag();

                holder.add.setVisibility(View.GONE);
                holder.plusminus.setVisibility(View.VISIBLE);
                holder.count.setText("1");

                totalCost = totalCost + Integer.valueOf(dietMenuList.get(tempPosition).getDishPrice());

                if(context instanceof DietMenuListActivity){
                    ((DietMenuListActivity)context).changeCount(1,totalCost);
                }

                DietMenuModel orderedFood = new DietMenuModel();
                orderedFood.setMenuID(dietMenuList.get(tempPosition).getDishId());
                orderedFood.setQuantity("1");
                orderedFood.setMenuName(dietMenuList.get(tempPosition).getDishName());
                orderedFood.setMenuPrice(dietMenuList.get(tempPosition).getDishPrice());
                orderedFood.setMenuImage(dietMenuList.get(tempPosition).getDishThumbnail());
                orderedFood.setMenuType(dietMenuList.get(tempPosition).getDishType());
                if(context instanceof DietMenuListActivity){
                    ((DietMenuListActivity)context).orderItem(orderedFood,"add");
                }
            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempPosition = (Integer) v.getTag();

                int counter = Integer.valueOf(holder.count.getText().toString());

                if(counter == 0){
                    holder.plusminus.setVisibility(View.GONE);
                    holder.add.setVisibility(View.VISIBLE);
                    DietMenuModel orderedFood = new DietMenuModel();
                    orderedFood.setMenuID(dietMenuList.get(tempPosition).getDishId());
                    orderedFood.setQuantity(""+counter);
                    orderedFood.setMenuName(dietMenuList.get(tempPosition).getDishName());
                    orderedFood.setMenuPrice(dietMenuList.get(tempPosition).getDishPrice());
                    orderedFood.setMenuImage(dietMenuList.get(tempPosition).getDishThumbnail());
                    orderedFood.setMenuType(dietMenuList.get(tempPosition).getDishType());
                    if(context instanceof DietMenuListActivity){
                        ((DietMenuListActivity)context).orderItem(orderedFood,"remove");
                    }
                }
                else if(counter == 1){
                    counter = counter - 1;
                    holder.count.setText(""+counter);
                    holder.plusminus.setVisibility(View.GONE);
                    holder.add.setVisibility(View.VISIBLE);

                    DietMenuModel orderedFood = new DietMenuModel();
                    orderedFood.setMenuID(dietMenuList.get(tempPosition).getDishId());
                    orderedFood.setQuantity(""+counter);
                    orderedFood.setMenuName(dietMenuList.get(tempPosition).getDishName());
                    orderedFood.setMenuPrice(dietMenuList.get(tempPosition).getDishPrice());
                    orderedFood.setMenuImage(dietMenuList.get(tempPosition).getDishThumbnail());
                    orderedFood.setMenuType(dietMenuList.get(tempPosition).getDishType());
                    if(context instanceof DietMenuListActivity){
                        ((DietMenuListActivity)context).orderItem(orderedFood,"remove");
                    }

                    totalCost = totalCost - Integer.valueOf(dietMenuList.get(tempPosition).getDishPrice());
                }
                else {
                    counter = counter - 1;
                    holder.count.setText(""+counter);

                    DietMenuModel orderedFood = new DietMenuModel();
                    orderedFood.setMenuID(dietMenuList.get(tempPosition).getDishId());
                    orderedFood.setQuantity(""+counter);
                    orderedFood.setMenuName(dietMenuList.get(tempPosition).getDishName());
                    orderedFood.setMenuPrice(dietMenuList.get(tempPosition).getDishPrice());
                    orderedFood.setMenuImage(dietMenuList.get(tempPosition).getDishThumbnail());
                    orderedFood.setMenuType(dietMenuList.get(tempPosition).getDishType());
                    if(context instanceof DietMenuListActivity){
                        ((DietMenuListActivity)context).orderItem(orderedFood,"update");
                    }

                    totalCost = totalCost - Integer.valueOf(dietMenuList.get(tempPosition).getDishPrice());
                }


                if(context instanceof DietMenuListActivity){
                    ((DietMenuListActivity)context).changeCount(counter,totalCost);
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

                DietMenuModel orderedFood = new DietMenuModel();
                orderedFood.setMenuID(dietMenuList.get(tempPosition).getDishId());
                orderedFood.setQuantity(""+counter);
                orderedFood.setMenuName(dietMenuList.get(tempPosition).getDishName());
                orderedFood.setMenuPrice(dietMenuList.get(tempPosition).getDishPrice());
                orderedFood.setMenuImage(dietMenuList.get(tempPosition).getDishThumbnail());
                orderedFood.setMenuType(dietMenuList.get(tempPosition).getDishType());
                if(context instanceof DietMenuListActivity){
                    ((DietMenuListActivity)context).orderItem(orderedFood,"update");
                }

                totalCost = totalCost + Integer.valueOf(dietMenuList.get(tempPosition).getDishPrice());

                if(context instanceof DietMenuListActivity){
                    ((DietMenuListActivity)context).changeCount(counter,totalCost);
                }
            }
        });


        return this.convertView;
    }
    static class ViewHolder{

        ImageView menuImage,typeImage;
        TextView menuName,menupPrice,menuRatings,count;
        Button add,plus,minus;
        LinearLayout plusminus;
    }
}


