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
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realizer.sallado.adapter.DoctorListAdapter;
import com.realizer.sallado.databasemodel.DoctorAvalability;
import com.realizer.sallado.databasemodel.DoctorHolidays;
import com.realizer.sallado.databasemodel.DoctorLocation;
import com.realizer.sallado.databasemodel.MedicalPanel;
import com.realizer.sallado.model.DoctorListModel;
import com.realizer.sallado.model.MedicalPanelListModel;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.Singleton;
import com.realizer.sallado.view.ProgressWheel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class  DoctorlistActivity extends AppCompatActivity {

    ListView doctorList;
    List<MedicalPanelListModel> doctorListModels;
    FirebaseDatabase database;
    DatabaseReference medicalPanelRef;
    ProgressWheel loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Singleton.getDatabase() == null)
            Singleton.setDatabase(FirebaseDatabase.getInstance());

        database = Singleton.getDatabase();
        medicalPanelRef = database.getReference("MedicalPanelData");
        medicalPanelRef.keepSynced(true);

        String type = getIntent().getStringExtra("DoctorType");
        setContentView(R.layout.doctor_list_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(type.equalsIgnoreCase("Ayurvedic"))
            actionBar.setTitle(Constants.actionBarTitle("Ayurvedacharya", this));
        else
            actionBar.setTitle(Constants.actionBarTitle("Dietitian", this));
        initiateView();

        doctorListModels = new ArrayList<>();

        loading.setVisibility(View.VISIBLE);


        Query query = medicalPanelRef.orderByChild("doctorType").equalTo(type);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    doctorListModels.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MedicalPanelListModel medicalPanelListModel = snapshot.getValue(MedicalPanelListModel.class);
                        medicalPanelListModel.setKey(snapshot.getKey());
                        doctorListModels.add(medicalPanelListModel);
                    }

                    if(doctorListModels.size() > 0) {
                        DoctorListAdapter doctorListAdapter = new DoctorListAdapter(doctorListModels, DoctorlistActivity.this);
                        doctorList.setAdapter(doctorListAdapter);
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

        doctorList = (ListView) findViewById(R.id.doctorlistlayout);
        loading =(ProgressWheel) findViewById(R.id.loading);

        doctorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DoctorlistActivity.this, DoctorsdetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("DoctorID",doctorListModels.get(position).getKey());
                intent.putExtras(bundle);
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
