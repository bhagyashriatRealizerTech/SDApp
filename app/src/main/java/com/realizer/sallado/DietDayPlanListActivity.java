package com.realizer.sallado;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.realizer.sallado.adapter.DietDayProgramListAdapter;
import com.realizer.sallado.databasemodel.DayProgram;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.Singleton;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Win on 14-06-2017.
 */

public class DietDayPlanListActivity extends AppCompatActivity {

    public ListView dayListView;
    public List<DayProgram> dayProgramList;
    Date inputDate;
    String programID,startdate;
    SimpleDateFormat simpleDateFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_list_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Constants.actionBarTitle(getIntent().getStringExtra("Title"), DietDayPlanListActivity.this));
        initiateView();
        dayProgramList = new ArrayList<>();
        dayProgramList = (List<DayProgram>) getIntent().getSerializableExtra("DietList");
        startdate = getIntent().getStringExtra("StartDate");
        programID = getIntent().getStringExtra("ProgramId");
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        setData();

    }

    public void setData(){
        if(dayProgramList.size()>0) {

            inputDate = null;
            try {
                inputDate = simpleDateFormat.parse(startdate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            DietDayProgramListAdapter dietDayProgramListAdapter = new DietDayProgramListAdapter(dayProgramList, inputDate,DietDayPlanListActivity.this);
            dayListView.setAdapter(dietDayProgramListAdapter);
        }
    }

    public void initiateView(){

        dayListView = (ListView) findViewById(R.id.doctorlistlayout);

        dayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(inputDate);
                calendar.add(Calendar.DATE,i);
                Intent intent = new Intent(DietDayPlanListActivity.this, DietDayPlanActivity.class);
                intent.putExtra("DayPlan", (Serializable) dayProgramList.get(i));
                intent.putExtra("IsB",getIntent().getBooleanExtra("B",true));
                intent.putExtra("IsL",getIntent().getBooleanExtra("L",true));
                intent.putExtra("IsS",getIntent().getBooleanExtra("S",true));
                intent.putExtra("IsD",getIntent().getBooleanExtra("D",true));
                intent.putExtra("DayDate",simpleDateFormat.format(calendar.getTime()));
                intent.putExtra("ProgramId", programID);
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
    protected void onPostResume() {
        super.onPostResume();
        if(Singleton.getInstance().isDayListChange()){
            Singleton.getInstance().setIsDayListChange(false);
            dayProgramList = Singleton.getInstance().getUserDietProgram().getDietProgram().getDayProgram();
            setData();
        }
    }
}

