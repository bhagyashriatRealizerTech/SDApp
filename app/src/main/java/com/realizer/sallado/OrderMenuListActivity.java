package com.realizer.sallado;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realizer.sallado.adapter.DietMenuListAdapter;
import com.realizer.sallado.adapter.DietPlanChangeListAdapter;
import com.realizer.sallado.adapter.OrderMenuListAdapter;
import com.realizer.sallado.databasemodel.Dish;
import com.realizer.sallado.databasemodel.DoctorAvalability;
import com.realizer.sallado.databasemodel.OrderFood;
import com.realizer.sallado.databasemodel.OrderedFood;
import com.realizer.sallado.databasemodel.User;
import com.realizer.sallado.model.DietMenuModel;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.Singleton;
import com.realizer.sallado.view.ProgressWheel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrderMenuListActivity extends AppCompatActivity {

    ListView menulist;
    List<DietMenuModel> dietMenuModels;
    List<OrderedFood> tempList;
    TextView address,price;
    Button proceed;
    int total,counter;
    String userid;
    FirebaseDatabase database;
    DatabaseReference orderRef,dishRef;
    ProgressWheel loading;
    OrderFood orderFood;
    List<OrderedFood> orderedFoodList;
    String from;
    OrderMenuListAdapter dietListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.order_menu_list_activity);
        if(Singleton.getDatabase() == null)
            Singleton.setDatabase(FirebaseDatabase.getInstance());

        database = Singleton.getDatabase();
        orderRef = database.getReference("OrderFood");
        dishRef = database.getReference("Dish");

        orderRef.keepSynced(true);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        dietMenuModels = new ArrayList<>();
        orderedFoodList = new ArrayList<>();
        userid = PreferenceManager.getDefaultSharedPreferences(OrderMenuListActivity.this).getString("UserID", "");
        from = getIntent().getStringExtra("FromWhere");

        initiateView();
        loading.setVisibility(View.VISIBLE);

        if(from.equalsIgnoreCase("MyOrder") || from.equalsIgnoreCase("Reorder")){
            actionBar.setTitle(Constants.actionBarTitle("My Order", this));
            orderFood = (OrderFood) getIntent().getSerializableExtra("OrderedFood");
            tempList = orderFood.getOrderedFood();
            if(orderFood.getOrderTotalPrice().split(" ").length >1)
                total = Integer.valueOf(orderFood.getOrderTotalPrice().split(" ")[1]) - 67;
            else
                total = Integer.valueOf(orderFood.getOrderTotalPrice()) - 67;

            for(int i=0;i<tempList.size();i++){
                setChangeData(tempList.get(i).getDishId(),i);
            }
        }
        else {
            actionBar.setTitle(Constants.actionBarTitle("Confirm Order", this));
            dietMenuModels = (ArrayList<DietMenuModel>) getIntent().getSerializableExtra("OrderedFood");
            total = Integer.valueOf(getIntent().getStringExtra("TotalPrice").split(" ")[1]);
            if(dietMenuModels.size() > 0) {
                dietListAdapter = new OrderMenuListAdapter(dietMenuModels, OrderMenuListActivity.this,total,from);
                menulist.setAdapter(dietListAdapter);
            }
            loading.setVisibility(View.GONE);
        }

        price.setText("₹ "+total+"\nTax: ₹ 67\nGrandTotal:  ₹ "+(total+67));


    }

    public void setChangeData(final String dishId, final int i){

        Query query = dishRef.orderByChild("dishId").equalTo(dishId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Dish dish = snapshot.getValue(Dish.class);
                        DietMenuModel dietMenuModel = new DietMenuModel();
                        dietMenuModel.setMenuName(dish.getDishName());
                        dietMenuModel.setMenuType(dish.getDishType());
                        dietMenuModel.setQuantity(tempList.get(i).getDishQuantity());
                        dietMenuModel.setMenuImage(dish.getDishThumbnail());
                        dietMenuModel.setMenuDetail(dish.getDishDescription());
                        dietMenuModel.setMenuID(dish.getDishId());
                        dietMenuModel.setMenuImp(dish.getDishContent());
                        dietMenuModel.setMenuPrice(dish.getDishPrice());
                        dietMenuModel.setMenuRatings(dish.getDishRatings());
                        dietMenuModels.add(dietMenuModel);
                    }

                    if(dietMenuModels.size() == tempList.size()){
                        dietListAdapter = new OrderMenuListAdapter(dietMenuModels, OrderMenuListActivity.this,total,from);
                        menulist.setAdapter(dietListAdapter);
                        loading.setVisibility(View.GONE);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
            }
        });

    }

    public void initiateView(){

        menulist = (ListView) findViewById(R.id.lst_menu);
        address = (TextView) findViewById(R.id.txt_address);
        price = (TextView) findViewById(R.id.txt_total);
        proceed = (Button) findViewById(R.id.btn_proceed);
        loading =(ProgressWheel) findViewById(R.id.loading);
        if(from.equalsIgnoreCase("MyOrder")){
            proceed.setVisibility(View.GONE);
        }
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dietMenuModels.size() > 0) {
                    loading.setVisibility(View.VISIBLE);
                    for (int i = 0; i < dietMenuModels.size(); i++) {
                        if(Integer.valueOf(dietMenuModels.get(i).getQuantity()) > 0) {
                            OrderedFood orderedFood = new OrderedFood();
                            orderedFood.setDishId(dietMenuModels.get(i).getMenuID());
                            orderedFood.setDishQuantity(dietMenuModels.get(i).getQuantity());
                            orderedFood.setDishName(dietMenuModels.get(i).getMenuName());
                            orderedFoodList.add(orderedFood);
                        }
                    }

                    String orderdate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());
                    int totalPrice = total + 67;

                    OrderFood orderFood = new OrderFood();
                    orderFood.setUserID(userid);
                    orderFood.setOrderDate(orderdate);
                    orderFood.setOrderedFood(orderedFoodList);
                    orderFood.setOrderId(UUID.randomUUID().toString());
                    orderFood.setOrderLastUpdate(orderdate);
                    orderFood.setOrderStatus("Placed");
                    orderFood.setOrderTax("67");
                    orderFood.setOrderTotalPrice("" + totalPrice);

                    DatabaseReference ref = orderRef.push();
                    ref.setValue(orderFood);

                    loading.setVisibility(View.GONE);
                    Constants.alertDialog(OrderMenuListActivity.this, "Order", "Your Order Placed Successfully.\n\nOrder Time: " + orderFood.getOrderDate() + "\nPrice: ₹ " + orderFood.getOrderTotalPrice());
                }
                else {
                    Constants.alertDialog(OrderMenuListActivity.this, "Order", "Please Add at least one item to cart\n");
                }
            }

        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderMenuListActivity.this,MyAddressActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changePrice(int pric){

        total = pric;
        price.setText("₹ "+total+"\nTax: ₹ 67\nGrandTotal:  ₹ "+(total+67));
    }

    public void orderItem(DietMenuModel orderedFood,String type){
        if(type.equalsIgnoreCase("add")){
            dietMenuModels.add(orderedFood);
        }
        else if(type.equalsIgnoreCase("remove"
        )){
            for(int i=0;i<dietMenuModels.size();i++){
                if(orderedFood.getMenuID().equalsIgnoreCase(dietMenuModels.get(i).getMenuID())){
                    dietMenuModels.remove(i);
                    break;
                }
            }
        }
        else {
            for(int i=0;i<dietMenuModels.size();i++){
                if(orderedFood.getMenuID().equalsIgnoreCase(dietMenuModels.get(i).getMenuID())){
                    dietMenuModels.set(i,orderedFood);
                    break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String addres = PreferenceManager.getDefaultSharedPreferences(OrderMenuListActivity.this).getString("Address", "");
        if(addres.length()<=0)
            address.setText("Tap Here To Add Address");
        else
            address.setText(addres);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // condition to lock the screen at the time of refreshing
        if ((loading != null && loading.isShown())) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    //Loading Screen
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // condition to lock the screen at the time of refreshing
        if ((loading != null && loading.isShown())) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
