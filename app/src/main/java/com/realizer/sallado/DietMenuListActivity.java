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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        dishRef = database.getReference("Dish");

        setContentView(R.layout.diet_menu_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Constants.actionBarTitle("Menu", this));
        initiateView();

        dietMenuModels = new ArrayList<>();
        orderedFoodList = new ArrayList<>();

/*
        Dish diet = new Dish();
        diet.setDishName("Roasted Vegetable Medley");
        diet.setDishPrice("150");
        diet.setDishRatings("99%");
        diet.setDishType("Veg");
        diet.setDishContent("1");
        diet.setDishDescription("1");
        diet.setDishId("1");
        diet.setDishIngredients("1");
        diet.setFoodPreparationTime("1");
        diet.setDishThumbnail("http://cdn.hercampus.com/s3fs-public/styles/full_width_embed/public/2016/02/12/Roasted-Vegetables-with-Fresh-Herbs-3862-640x428.jpg");
        dietMenuModels.add(diet);

        Dish diet1 = new Dish();
        diet1.setDishName("Herbed Chiken");
        diet1.setDishPrice("200");
        diet1.setDishRatings("95%");
        diet1.setDishType("NonVeg");
        diet1.setDishContent("1");
        diet1.setDishDescription("1");
        diet1.setDishId("1");
        diet1.setDishIngredients("1");
        diet1.setFoodPreparationTime("1");
        diet1.setDishThumbnail("http://www.recipehearth.com/wp-content/uploads/2013/09/fc86in001-03_xlg.jpg");
        dietMenuModels.add(diet1);

        Dish diet2 = new Dish();
        diet2.setDishName("Pork Chops");
        diet2.setDishPrice("350");
        diet2.setDishRatings("99%");
        diet2.setDishType("NonVeg");
        diet2.setDishContent("1");
        diet2.setDishDescription("1");
        diet2.setDishId("1");
        diet2.setDishIngredients("1");
        diet2.setFoodPreparationTime("1");
        diet2.setDishThumbnail("https://images-gmi-pmc.edge-generalmills.com/b7d13b4b-ce88-4149-80b8-52630d8c2318.jpg");
        dietMenuModels.add(diet2);

        Dish diet3 = new Dish();
        diet3.setDishName("Carrot Oatmeal Kebab");
        diet3.setDishPrice("200");
        diet3.setDishRatings("99%");
        diet3.setDishType("Veg");
        diet3.setDishContent("1");
        diet3.setDishDescription("1");
        diet3.setDishId("1");
        diet3.setDishIngredients("1");
        diet3.setFoodPreparationTime("1");
        diet3.setDishThumbnail("https://s-media-cache-ak0.pinimg.com/600x315/a9/82/2f/a9822f217a7eeb0cbbc31f47dae760b2.jpg");
        dietMenuModels.add(diet3);

        Dish diet0 = new Dish();
        diet0.setDishName("Biryani with Cashews");
        diet0.setDishPrice("250");
        diet0.setDishRatings("99%");
        diet0.setDishType("Veg");
        diet0.setDishContent("1");
        diet0.setDishDescription("1");
        diet0.setDishId("1");
        diet0.setDishIngredients("1");
        diet0.setFoodPreparationTime("1");
        diet0.setDishThumbnail("http://www.vegrecipesofindia.com/wp-content/uploads/2015/12/mughlai-veg-biryani-recipe40.jpg");
        dietMenuModels.add(diet0);

        Dish diet5 = new Dish();
        diet5.setDishName("Backed Chicken Nuggets");
        diet5.setDishPrice("180");
        diet5.setDishRatings("99%");
        diet5.setDishType("NonVeg");
        diet5.setDishContent("1");
        diet5.setDishDescription("1");
        diet5.setDishId("1");
        diet5.setDishIngredients("1");
        diet5.setFoodPreparationTime("1");
        diet5.setDishThumbnail("http://crescenthalal.com/wp-content/uploads/2014/01/crunchy-chicken-nugget-recipe-photo-420x420.jpg");
        dietMenuModels.add(diet5);

        for(int i=0;i<dietMenuModels.size();i++){
            DatabaseReference ref = dishRef.push();
            ref.setValue(dietMenuModels.get(i));
        }*/


        loading.setVisibility(View.VISIBLE);

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
