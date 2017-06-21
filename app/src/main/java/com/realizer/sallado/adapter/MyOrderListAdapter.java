package com.realizer.sallado.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.realizer.sallado.MyOrderActivity;
import com.realizer.sallado.OrderMenuListActivity;
import com.realizer.sallado.R;
import com.realizer.sallado.databasemodel.DayProgram;
import com.realizer.sallado.databasemodel.OrderFood;
import com.realizer.sallado.databasemodel.OrderedFood;
import com.realizer.sallado.utils.Constants;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Win on 09-05-2017.
 */

public class MyOrderListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    public View convertView;
    public  Context context;
    List<OrderFood> orderFoods;

    public MyOrderListAdapter(List<OrderFood> orderFoods, Context context){
        this.context = context;
        this.orderFoods = orderFoods;
        layoutInflater =LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return orderFoods.size();
    }

    @Override
    public Object getItem(int position) {
        return orderFoods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return orderFoods.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        ViewHolder holder;
        this.convertView = convertView;


        if (convertView == null) {
            this.convertView = layoutInflater.inflate(R.layout.my_order_list_item_layout, null);

            holder = new ViewHolder();
            holder.day = (TextView) this.convertView.findViewById(R.id.txt_date);
            holder.name = (TextView) this.convertView.findViewById(R.id.txt_name);
            holder.view = (TextView) this.convertView.findViewById(R.id.txt_view);
            holder.reorder = (TextView) this.convertView.findViewById(R.id.txt_reorder);
            holder.price = (TextView) this.convertView.findViewById(R.id.txt_price);
            holder.view.setTag(position);
            holder.reorder.setTag(position);

            String title =  "";
            for(int i= 0;i<orderFoods.get(position).getOrderedFood().size();i++){
                if(title.length() == 0)
                title = orderFoods.get(position).getOrderedFood().get(i).getDishName()+"("+orderFoods.get(position).getOrderedFood().get(i).getDishQuantity()+"), ";
                else if (i == (orderFoods.get(position).getOrderedFood().size() - 1))
                    title = title + orderFoods.get(position).getOrderedFood().get(i).getDishName()+"("+orderFoods.get(position).getOrderedFood().get(i).getDishQuantity()+")";
                else
                    title = title + orderFoods.get(position).getOrderedFood().get(i).getDishName()+"("+orderFoods.get(position).getOrderedFood().get(i).getDishQuantity()+"), ";
            }
            holder.day.setText(orderFoods.get(position).getOrderDate().split(" ")[0]);
            holder.name.setText(title);
            holder.price.setText("₹ "+orderFoods.get(position).getOrderTotalPrice());

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, OrderMenuListActivity.class);
                    intent.putExtra("OrderedFood", (Serializable) orderFoods.get((Integer)view.getTag()));
                    intent.putExtra("FromWhere","MyOrder");
                    context.startActivity(intent);
                }
            });

            holder.reorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, OrderMenuListActivity.class);
                    intent.putExtra("OrderedFood", (Serializable) orderFoods.get((Integer)view.getTag()));
                    intent.putExtra("FromWhere","Reorder");
                    context.startActivity(intent);
                }
            });

            this.convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) this.convertView.getTag();
        }


        return this.convertView;
    }
    static class ViewHolder{

        TextView day,name,price,view,reorder;
    }
}


