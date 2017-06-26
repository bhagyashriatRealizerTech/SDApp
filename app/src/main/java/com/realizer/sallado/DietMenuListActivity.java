package com.realizer.sallado;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.realizer.sallado.adapter.DietMenuListAdapter;
import com.realizer.sallado.adapter.DietPlanListAdapter;
import com.realizer.sallado.adapter.DoctorListAdapter;
import com.realizer.sallado.databasemodel.Dish;
import com.realizer.sallado.databasemodel.OrderedFood;
import com.realizer.sallado.model.DietMenuModel;
import com.realizer.sallado.model.DietPlanModel;
import com.realizer.sallado.model.MedicalPanelListModel;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.Singleton;
import com.realizer.sallado.view.ProgressWheel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DietMenuListActivity extends AppCompatActivity {

    GridView dietgrid;
    List<Dish> dietMenuModels;
    TextView count,price,order;
    FirebaseDatabase database;
    DatabaseReference dishRef;
    List<DietMenuModel> orderedFoodList;
    ProgressWheel loading;
    int counter= 0;
    String from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Singleton.getDatabase() == null)
            Singleton.setDatabase(FirebaseDatabase.getInstance());

        database = Singleton.getDatabase();
        dishRef = database.getReference("Dish");
        dishRef.keepSynced(true);

        setContentView(R.layout.diet_menu_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Constants.actionBarTitle("Menu", this));
        initiateView();

        dietMenuModels = new ArrayList<>();
        orderedFoodList = new ArrayList<>();
        loading.setVisibility(View.VISIBLE);
        from = getIntent().getStringExtra("FromWhere");

        if(from != null){
            if(from.equalsIgnoreCase("DC"))
                Constants.redAlertDialog(DietMenuListActivity.this,"Information","We do not promote drinking alcoholic beverages." ,
                        "\nHowever in order to reduce side effects of salty foods(Chakhna) consumed along with alcoholic beverages," +
                        "\nWe do suggest to have these healthy alternatives.");
        }

        dishRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    dietMenuModels.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Dish dish = snapshot.getValue(Dish.class);
                        dietMenuModels.add(dish);
                    }

                    if(dietMenuModels.size() > 0) {
                        DietMenuListAdapter dietListAdapter = new DietMenuListAdapter(dietMenuModels, DietMenuListActivity.this);
                        dietgrid.setAdapter(dietListAdapter);
                    }
                    loading.setVisibility(View.GONE);
                }
                else {
                    loading.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                loading.setVisibility(View.GONE);
            }
        });

    }

    public void initiateView(){

        dietgrid = (GridView) findViewById(R.id.grid_dietmenu);
        count = (TextView) findViewById(R.id.txt_menu_count);
        price = (TextView) findViewById(R.id.txt_menu_price);
        order = (TextView) findViewById(R.id.txt_order);
        loading =(ProgressWheel) findViewById(R.id.loading);

        /*dietgrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                *//*Intent intent = new Intent(DietPlanListActivity.this, DoctorsdetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Photo",doctorListModels.get(position).getImageUrl());
                bundle.putString("Name",doctorListModels.get(position).getDoctorName());
                bundle.putString("Degree",doctorListModels.get(position).getDoctorDeggre());
                bundle.putString("Experience",doctorListModels.get(position).getDoctorExperience());
                bundle.putString("Ratings","99%");
                bundle.putString("Description",doctorListModels.get(position).getDetaildescription());
                bundle.putString("ClinicName",doctorListModels.get(position).getClinicName());
                bundle.putString("Address",doctorListModels.get(position).getDetailAddress());
                bundle.putString("Time",doctorListModels.get(position).getTimings());
                bundle.putString("fees",doctorListModels.get(position).getFees());
               intent.putExtras(bundle);
                startActivity(intent);*//*
            }
        });*/


        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderedFoodList.size()>0) {
                    Intent intent = new Intent(DietMenuListActivity.this, OrderMenuListActivity.class);
                    intent.putExtra("OrderedFood", (Serializable) orderedFoodList);
                    intent.putExtra("FromWhere","MenuLIst");
                    intent.putExtra("TotalPrice", price.getText());
                    startActivity(intent);
                }
                else {
                    Constants.alertDialog(DietMenuListActivity.this,"Order","Please select at least one item");
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

    public void changeCount(int index,int pric){
        counter = counter + 1;
        count.setText(""+index+" Item in cart");
        price.setText("₹ "+pric);
    }

    public void orderItem(DietMenuModel orderedFood,String type){
        if(type.equalsIgnoreCase("add")){
            orderedFoodList.add(orderedFood);
        }
        else if(type.equalsIgnoreCase("remove")){
            for(int i=0;i<orderedFoodList.size();i++){
                if(orderedFood.getMenuID().equalsIgnoreCase(orderedFoodList.get(i).getMenuID())){
                    orderedFoodList.remove(i);
                    break;
                }
            }
        }
        else {
            for(int i=0;i<orderedFoodList.size();i++){
                if(orderedFood.getMenuID().equalsIgnoreCase(orderedFoodList.get(i).getMenuID())){
                    orderedFoodList.set(i,orderedFood);
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
