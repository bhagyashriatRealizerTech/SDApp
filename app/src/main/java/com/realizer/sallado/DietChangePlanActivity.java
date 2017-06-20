package com.realizer.sallado;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realizer.sallado.adapter.DietPlanChangeListAdapter;
import com.realizer.sallado.databasemodel.Dish;
import com.realizer.sallado.databasemodel.DishGroup;
import com.realizer.sallado.databasemodel.UserDietProgram;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.Singleton;
import com.realizer.sallado.view.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Win on 14-06-2017.
 */

public class DietChangePlanActivity extends AppCompatActivity {

    public ListView changeListView;
    public List<Dish> dishList;
    DatabaseReference dishRef,userDietRef;
    FirebaseDatabase database;
    private ProgressWheel loading;
    DishGroup dishGroup;
    String dishId,day;
    int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diet_plan_change_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Constants.actionBarTitle("Change Dish", DietChangePlanActivity.this));
        initiateView();
        if(Singleton.getDatabase() == null)
            Singleton.setDatabase(FirebaseDatabase.getInstance());

        database = Singleton.getDatabase();

        dishRef = database.getReference("Dish");
        userDietRef = database.getReference("UserDietProgram");
        dishList = new ArrayList<>();
        day = getIntent().getStringExtra("Day");
        type = getIntent().getIntExtra("ChangeType",0);
        dishGroup = (DishGroup) getIntent().getSerializableExtra("DishGroup");

        loading.setVisibility(View.VISIBLE);
        for(int i=0;i<dishGroup.getDishItems().size();i++){
            setChangeData(dishGroup.getDishItems().get(i).getDishID(),i);
        }


    }

    public void initiateView(){

        changeListView = (ListView) findViewById(R.id.changeList);
        loading = (ProgressWheel) findViewById(R.id.loading);
        changeListView.setSelection(AbsListView.CHOICE_MODE_SINGLE);

    }

    public void setChangeData(final String dishId, final int i){

        Query query = dishRef.orderByChild("dishId").equalTo(dishId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Dish dish = snapshot.getValue(Dish.class);
                        dishList.add(dish);
                    }

                    if(dishGroup.getDishItems().size() == dishList.size()){
                        DietPlanChangeListAdapter dietPlanChangeListAdapter = new DietPlanChangeListAdapter(dishList,DietChangePlanActivity.this);
                        changeListView.setAdapter(dietPlanChangeListAdapter);
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

    public void setDish(String dishId){
        this.dishId = dishId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            case R.id.action_done:
                // app icon in action bar clicked; go home
                loading.setVisibility(View.VISIBLE);
                setChangeData();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setChangeData(){
        Singleton.getInstance().setIsChange(true);

            Query query = userDietRef.child(getIntent().getStringExtra("ProgramId"));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        UserDietProgram userDietProgram = null;
                        userDietProgram = dataSnapshot.getValue(UserDietProgram.class);

                        if(type == 0)
                            userDietProgram.getDietProgram().getDayProgram().get((Integer.valueOf(day) - 1)).setBreakfastDishID(dishId);

                        else if(type == 1)
                            userDietProgram.getDietProgram().getDayProgram().get((Integer.valueOf(day) - 1)).setLunchDishID(dishId);

                        else if(type == 2)
                            userDietProgram.getDietProgram().getDayProgram().get((Integer.valueOf(day) - 1)).setSnacksDishId(dishId);

                        else if(type == 3)
                            userDietProgram.getDietProgram().getDayProgram().get((Integer.valueOf(day) - 1)).setDinnerDishId(dishId);


                        userDietRef.child(getIntent().getStringExtra("ProgramId")).setValue(userDietProgram);
                        Singleton.getInstance().setUserDietProgram(userDietProgram);
                        Singleton.getInstance().setIsChange(true);
                        Singleton.getInstance().setIsDayListChange(true);
                        Singleton.getInstance().setIsDietProgramChange(true);
                        loading.setVisibility(View.GONE);
                        finish();
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    loading.setVisibility(View.GONE);
                }
            });

    }
}

