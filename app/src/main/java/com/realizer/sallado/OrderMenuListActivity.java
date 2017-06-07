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
import com.realizer.sallado.adapter.OrderMenuListAdapter;
import com.realizer.sallado.databasemodel.Dish;
import com.realizer.sallado.databasemodel.DoctorAvalability;
import com.realizer.sallado.databasemodel.OrderFood;
import com.realizer.sallado.databasemodel.OrderedFood;
import com.realizer.sallado.databasemodel.User;
import com.realizer.sallado.model.DietMenuModel;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.view.ProgressWheel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrderMenuListActivity extends AppCompatActivity {

    ListView menulist;
    List<DietMenuModel> dietMenuModels;
    List<OrderedFood> orderedFoodList;
    TextView address,price;
    Button proceed;
    int total,counter;
    String userid;
    FirebaseDatabase database;
    DatabaseReference orderRef;
    DatabaseReference dishRef;
    ProgressWheel loading;
    OrderMenuListAdapter dietListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        database = FirebaseDatabase.getInstance();
        orderRef = database.getReference("OrderFood");
        dishRef = database.getReference("Dish");

        setContentView(R.layout.order_menu_list_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Constants.actionBarTitle("Confirm Order", this));
        initiateView();

        dietMenuModels = new ArrayList<>();
        orderedFoodList = new ArrayList<>();

        dietMenuModels = (ArrayList<DietMenuModel>) getIntent().getSerializableExtra("OrderedFood");
        total = Integer.valueOf(getIntent().getStringExtra("TotalPrice").split(" ")[1]);
        userid = PreferenceManager.getDefaultSharedPreferences(OrderMenuListActivity.this).getString("UserID","");

       /* DietMenuModel diet = new DietMenuModel();
        diet.setMenuName("Roasted Vegetable Medley");
        diet.setMenuPrice("150");
        diet.setMenuRatings("99%");
        diet.setMenuType("Veg");
        diet.setQuantity(2);
        diet.setMenuImage("http://cdn.hercampus.com/s3fs-public/styles/full_width_embed/public/2016/02/12/Roasted-Vegetables-with-Fresh-Herbs-3862-640x428.jpg");
        dietMenuModels.add(diet);

        DietMenuModel diet1 = new DietMenuModel();
        diet1.setMenuName("Herbed Chiken");
        diet1.setMenuPrice("200");
        diet1.setMenuRatings("95%");
        diet1.setMenuType("NonVeg");
        diet1.setQuantity(1);
        diet1.setMenuImage("http://www.recipehearth.com/wp-content/uploads/2013/09/fc86in001-03_xlg.jpg");
        dietMenuModels.add(diet1);

        DietMenuModel diet2 = new DietMenuModel();
        diet2.setMenuName("Pork Chops");
        diet2.setMenuPrice("350");
        diet2.setMenuRatings("99%");
        diet2.setMenuType("NonVeg");
        diet2.setQuantity(3);
        diet2.setMenuImage("https://images-gmi-pmc.edge-generalmills.com/b7d13b4b-ce88-4149-80b8-52630d8c2318.jpg");
        dietMenuModels.add(diet2);

        DietMenuModel diet3 = new DietMenuModel();
        diet3.setMenuName("Carrot Oatmeal Kebab");
        diet3.setMenuPrice("200");
        diet3.setMenuRatings("99%");
        diet3.setMenuType("Veg");
        diet3.setQuantity(1);
        diet3.setMenuImage("https://s-media-cache-ak0.pinimg.com/600x315/a9/82/2f/a9822f217a7eeb0cbbc31f47dae760b2.jpg");
        dietMenuModels.add(diet3);*/


        address.setText("A-601, Mega Center\nHadapsar, Pune");
        price.setText("₹ "+total+"\nTax: ₹ 67\nGrandTotal:  ₹ "+(total+67));

        if(dietMenuModels.size() > 0) {
            dietListAdapter = new OrderMenuListAdapter(dietMenuModels, OrderMenuListActivity.this,total);
            menulist.setAdapter(dietListAdapter);
        }
    }

    public void initiateView(){

        menulist = (ListView) findViewById(R.id.lst_menu);
        address = (TextView) findViewById(R.id.txt_address);
        price = (TextView) findViewById(R.id.txt_total);
        proceed = (Button) findViewById(R.id.btn_proceed);
        loading =(ProgressWheel) findViewById(R.id.loading);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dietMenuModels.size() > 1) {
                    loading.setVisibility(View.VISIBLE);
                    for (int i = 0; i < dietMenuModels.size(); i++) {
                        OrderedFood orderedFood = new OrderedFood();
                        orderedFood.setDishId(dietMenuModels.get(i).getMenuID());
                        orderedFood.setDishQuantity(dietMenuModels.get(i).getQuantity());

                        orderedFoodList.add(orderedFood);
                    }

                    String orderdate = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());
                    int totalPrice = total + 67;

                    OrderFood orderFood = new OrderFood();
                    orderFood.setUserID(userid);
                    orderFood.setOrderDate(orderdate);
                    orderFood.setOrderedFood(orderedFoodList);
                    orderFood.setOrderId(UUID.randomUUID().toString());
                    orderFood.setOrderLastUpdate(orderdate);
                    orderFood.setOrderStatus("Ordered");
                    orderFood.setOrderTax("67");
                    orderFood.setOrderTotalPrice("" + totalPrice);

                    DatabaseReference ref = orderRef.push();
                    ref.setValue(orderFood);

                    loading.setVisibility(View.GONE);
                    Constants.alertDialog(OrderMenuListActivity.this, "Order", "Your Order Placed Successfully.\n\nOrder Time: " + orderFood.getOrderDate() + "\nPrice: ₹ " + orderFood.getOrderTotalPrice());
                }
                else {
                    Constants.alertDialog(OrderMenuListActivity.this, "Order", "Please Add at least one item");
                }
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
        price.setText("₹ "+total);
    }

    public void orderItem(DietMenuModel orderedFood,String type){
        if(type.equalsIgnoreCase("add")){
            dietMenuModels.add(orderedFood);
        }
        else if(type.equalsIgnoreCase("remove")){
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
