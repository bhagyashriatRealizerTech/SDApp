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
import android.widget.ListView;
import android.widget.TextView;

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
import com.realizer.sallado.utils.FontManager;
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
    TextView noData;
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
                            noData.setVisibility(View.GONE);
                            DietPlanListAdapter dietListAdapter = new DietPlanListAdapter(dietProgramList, DietPlanListActivity.this);
                            dietList.setAdapter(dietListAdapter);
                        }
                        else {
                            noData.setVisibility(View.VISIBLE);
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
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DietPlanListActivity.this);
            if(preferences.getBoolean("IsSkip",false))
            {
                alertDialogLogin(DietPlanListActivity.this,"Login","Please Login to use this feature");
            }
            else {
                setData();
            }

        }


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

                Intent intent = new Intent(DietPlanListActivity.this, LoginActivity.class);
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
                            noData.setVisibility(View.GONE);
                            DietPlanListAdapter dietListAdapter = new DietPlanListAdapter(dietProgramList, DietPlanListActivity.this);
                            dietList.setAdapter(dietListAdapter);
                        }
                        else {
                            noData.setVisibility(View.VISIBLE);
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
        noData = (TextView) findViewById(R.id.Nodata);

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
