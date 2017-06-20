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
import com.realizer.sallado.adapter.DietPlanListAdapter;
import com.realizer.sallado.adapter.DoctorListAdapter;
import com.realizer.sallado.databasemodel.DayProgram;
import com.realizer.sallado.databasemodel.DietProgram;
import com.realizer.sallado.databasemodel.User;
import com.realizer.sallado.databasemodel.UserDietProgram;
import com.realizer.sallado.model.DietPlanModel;
import com.realizer.sallado.model.MedicalPanelListModel;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.Singleton;
import com.realizer.sallado.view.ProgressWheel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DietPlanListActivity extends AppCompatActivity {

    ListView dietList;
    List<DietProgram> dietProgramList;
    List<UserDietProgram> userDietProgramList;
    FirebaseDatabase database;
    DatabaseReference dietProgramRef;
    ProgressWheel loading;
    String fromWhere;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.diet_program_list_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initiateView();
        dietProgramList = new ArrayList<>();
        userDietProgramList = new ArrayList<>();

        if(Singleton.getDatabase() == null)
            Singleton.setDatabase(FirebaseDatabase.getInstance());

        database = Singleton.getDatabase();

        loading.setVisibility(View.VISIBLE);

        fromWhere = getIntent().getStringExtra("From");

        if(fromWhere.equalsIgnoreCase("Diet")) {

            actionBar.setTitle(Constants.actionBarTitle("Diet Program", this));
            dietProgramRef = database.getReference("StandardDietProgram");
            dietProgramRef.keepSynced(true);
            dietProgramRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // dataSnapshot is the "issue" node with all children with id 0
                        dietProgramList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DietProgram dietProgram = snapshot.getValue(DietProgram.class);
                            dietProgramList.add(dietProgram);
                        }

                        if (dietProgramList.size() > 0) {
                            DietPlanListAdapter dietListAdapter = new DietPlanListAdapter(dietProgramList, DietPlanListActivity.this);
                            dietList.setAdapter(dietListAdapter);
                        }

                        loading.setVisibility(View.GONE);

                    } else {
                        loading.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    loading.setVisibility(View.GONE);
                }
            });
        }
        else {
            actionBar.setTitle(Constants.actionBarTitle("My Diet Program", this));
            setData();

        }




/*
        List<DayProgram> dayProgramList = new ArrayList<>();
        for(int i=0;i<15;i++){
            DayProgram dayProgram = new DayProgram();
            dayProgram.setDay(""+(i+1));
            dayProgram.setBreakfastDishID("f1a01156-3fba-11e7-a919-92ebcb67fe33");
            dayProgram.setLunchDishID("f1a01494-3fba-11e7-a919-92ebcb67fe33");
            dayProgram.setSnacksDishId("f1a0178c-3fba-11e7-a919-92ebcb67fe33");
            dayProgram.setDinnerDishId("f1a0191c-3fba-11e7-a919-92ebcb67fe33");

            dayProgramList.add(dayProgram);
        }

        DietProgram dietProgram = new DietProgram();
        dietProgram.setCustomized(true);
        dietProgram.setDayProgram(dayProgramList);
        dietProgram.setProgramDescription("If you’re trying to lose weight and keep it off, you’ve come to the right place. We’ve got recipes for breakfast, lunch, dinner and snacks (plus dessert) that fit into your diet. Plus, get tips for overcoming your biggest diet challenges to lose weight successfully. Find the Right Calorie Level and Diet Meal Plan for You »");
        dietProgram.setProgramId(UUID.randomUUID().toString());
        dietProgram.setProgramName("Weight Loss");
        dietProgram.setProgramPrice("₹ 2500/M");
        dietProgram.setProgramRatings("99%");
        dietProgram.setProgramThumbnailUrl("http://www.rd.com/wp-content/uploads/sites/2/2012/03/13-things-weight-loss-experts-05-diet-exercise-sl.jpg");
        dietProgram.setProgramType("Dietitian");
        dietProgram.setProgramDays(dayProgramList.size());

        DatabaseReference ref = dietProgramRef.push();
        ref.setValue(dietProgram);*/
       /* dietPlanModels = new ArrayList<>();

        DietPlanModel diet = new DietPlanModel();
        diet.setDietName("Weight Gain");
        diet.setDietPrice("2000/M INR");
        diet.setDietRatings("99%");
        diet.setDietIncludes("Breakfast, Lunch, Dinner");
        dietPlanModels.add(diet);

        DietPlanModel diet1 = new DietPlanModel();
        diet1.setDietName("Weight Loss");
        diet1.setDietPrice("2500/M INR");
        diet1.setDietRatings("100%");
        diet1.setDietIncludes("Breakfast, Lunch, Dinner");
        dietPlanModels.add(diet1);

        DietPlanModel diet2 = new DietPlanModel();
        diet2.setDietName("Athelatic");
        diet2.setDietPrice("3000/M INR");
        diet2.setDietRatings("99%");
        diet2.setDietIncludes("Breakfast, Lunch, Dinner");
        dietPlanModels.add(diet2);*/




    }

    public void setData(){

        dietProgramRef = database.getReference("UserDietProgram");
        dietProgramRef.keepSynced(true);
        String userid = PreferenceManager.getDefaultSharedPreferences(DietPlanListActivity.this).getString("UserID","");
        Query query = dietProgramRef.orderByChild("userId").equalTo(userid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        dietProgramList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserDietProgram dietProgram = snapshot.getValue(UserDietProgram.class);
                            dietProgram.getDietProgram().setKey(snapshot.getKey());
                            dietProgramList.add(dietProgram.getDietProgram());
                            userDietProgramList.add(dietProgram);
                        }

                        if (dietProgramList.size() > 0) {
                            DietPlanListAdapter dietListAdapter = new DietPlanListAdapter(dietProgramList, DietPlanListActivity.this);
                            dietList.setAdapter(dietListAdapter);
                        }

                        loading.setVisibility(View.GONE);

                    }

                } else {
                    loading.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
            }
        });
    }

    public void initiateView(){

        dietList = (ListView) findViewById(R.id.dietpkglist);
        loading =(ProgressWheel) findViewById(R.id.loading);

        dietList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(fromWhere.equalsIgnoreCase("Diet")) {
                    Intent intent = new Intent(DietPlanListActivity.this, DietPlanDetailActivity.class);
                    intent.putExtra("ProgramId", dietProgramList.get(i).getProgramId());
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(DietPlanListActivity.this, DietDayPlanListActivity.class);
                    intent.putExtra("DietList", (Serializable) dietProgramList.get(i).getDayProgram());
                    intent.putExtra("B",dietProgramList.get(i).isBreakfastInclude());
                    intent.putExtra("L",dietProgramList.get(i).isLunchInclude());
                    intent.putExtra("S",dietProgramList.get(i).isSnacksInclude());
                    intent.putExtra("D",dietProgramList.get(i).isDinnerInclude());
                    intent.putExtra("Title",dietProgramList.get(i).getProgramName());
                    intent.putExtra("StartDate",userDietProgramList.get(i).getStartDate());
                    intent.putExtra("Days",dietProgramList.get(i).getProgramDays());
                    intent.putExtra("ProgramId", dietProgramList.get(i).getKey());
                    startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        if(Singleton.getInstance().isDietProgramChange()){
            loading.setVisibility(View.VISIBLE);
            Singleton.getInstance().setIsDietProgramChange(false);
            setData();
        }

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
