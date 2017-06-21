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
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realizer.sallado.adapter.DoctorListAdapter;
import com.realizer.sallado.adapter.MyOrderListAdapter;
import com.realizer.sallado.databasemodel.OrderFood;
import com.realizer.sallado.model.MedicalPanelListModel;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.Singleton;
import com.realizer.sallado.view.ProgressWheel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MyOrderActivity extends AppCompatActivity {

    ListView orderList;
    List<OrderFood> orderFoodList;
    FirebaseDatabase database;
    DatabaseReference orderRef;
    ProgressWheel loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_list_activity);

        if(Singleton.getDatabase() == null)
            Singleton.setDatabase(FirebaseDatabase.getInstance());

        database = Singleton.getDatabase();
        orderRef = database.getReference("OrderFood");
        orderRef.keepSynced(true);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Constants.actionBarTitle("My Orders", this));
        initiateView();

        orderFoodList = new ArrayList<>();

        loading.setVisibility(View.VISIBLE);

        String userId = PreferenceManager.getDefaultSharedPreferences(MyOrderActivity.this).getString("UserID","");


        Query query = orderRef.orderByChild("userID").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    orderFoodList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        OrderFood orderFood = snapshot.getValue(OrderFood.class);
                        orderFoodList.add(orderFood);
                    }

                    Collections.reverse(orderFoodList);

                    if(orderFoodList.size() > 0) {
                        MyOrderListAdapter doctorListAdapter = new MyOrderListAdapter(orderFoodList, MyOrderActivity.this);
                        orderList.setAdapter(doctorListAdapter);
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

        orderList = (ListView) findViewById(R.id.doctorlistlayout);
        loading =(ProgressWheel) findViewById(R.id.loading);

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
