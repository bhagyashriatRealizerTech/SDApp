package com.realizer.sallado;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import com.realizer.sallado.utils.FontManager;
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
    TextView heading;
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
            if(from.equalsIgnoreCase("DC")) {
                heading.setVisibility(View.GONE);
                Constants.redAlertDialog(DietMenuListActivity.this, "Information", "We do not promote drinking alcoholic beverages.",
                        "However in order to reduce side effects of salty foods(Chakhna) consumed along with alcoholic beverages," +
                                " we do suggest to have these healthy alternatives.");
            }
            else {
                heading.setVisibility(View.VISIBLE);
                heading.setText("Crash Eater Definition\nMin. Food Preparation + Delivery Time: 2 Hrs");
            }
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
        heading = (TextView) findViewById(R.id.txtHeading);
        order = (TextView) findViewById(R.id.txt_order);
        loading =(ProgressWheel) findViewById(R.id.loading);


        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DietMenuListActivity.this);
                if(preferences.getBoolean("IsSkip",false))
                {
                    alertDialogLogin(DietMenuListActivity.this,"Login","Please Login to place Order.");
                }
                else {

                    if(Constants.isConnectingToInternet(DietMenuListActivity.this))
                    {
                        if (orderedFoodList.size() > 0) {
                            Intent intent = new Intent(DietMenuListActivity.this, OrderMenuListActivity.class);
                            intent.putExtra("OrderedFood", (Serializable) orderedFoodList);
                            intent.putExtra("FromWhere", "MenuLIst");
                            intent.putExtra("TotalPrice", price.getText());
                            startActivity(intent);
                        } else {
                            Constants.alertDialog(DietMenuListActivity.this, "Order", "Please select at least one item");
                        }
                    }

                    else
                        Constants.alertDialog(DietMenuListActivity.this,"Network Error","Your device not connected to internet");
            }
            }
        });
    }
    public  void alertDialogLogin(final Context context, String title, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialoglayout = inflater.inflate(R.layout.custom_dialogbox, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialoglayout);


        Button buttonok= (Button) dialoglayout.findViewById(R.id.alert_btn_ok);
        TextView titleName=(TextView) dialoglayout.findViewById(R.id.alert_dialog_title);
        TextView alertMsg=(TextView) dialoglayout.findViewById(R.id.alert_dialog_message);
        TextView close=(TextView) dialoglayout.findViewById(R.id.txt_close);
        close.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
        buttonok.setText("Login");
        close.setVisibility(View.GONE);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);


        buttonok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DietMenuListActivity.this, LoginActivity.class);
                startActivity(intent);
                alertDialog.dismiss();
                finishAffinity();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        titleName.setText(title);
        alertMsg.setText(message);

        alertDialog.show();

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
