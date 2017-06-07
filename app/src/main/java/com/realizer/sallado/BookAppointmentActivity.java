package com.realizer.sallado;

import android.os.AsyncTask;
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
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.realizer.sallado.adapter.BookAppointmentListAdapter;
import com.realizer.sallado.databasemodel.BookAppointment;
import com.realizer.sallado.databasemodel.DoctorAvalability;
import com.realizer.sallado.model.BookApointmentModel;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.view.ProgressWheel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookAppointmentActivity extends AppCompatActivity {

    GridView slotGrid;
    List<BookApointmentModel> appoinmentList;
    TextView noData;
    TextView days[] ;
    List<DoctorAvalability> doctorAvalabilityList;
    FirebaseDatabase database;
    DatabaseReference appointmenteRef;
    Button book;
    String userid,doctorid,slot,date;
    int globPos;
    ProgressWheel loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        appointmenteRef = database.getReference("DoctorAppintments");

        setContentView(R.layout.book_appointment_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Constants.actionBarTitle("Book Appointment", BookAppointmentActivity.this));
        initiateView();
        doctorAvalabilityList = new ArrayList<>();
        appoinmentList = new ArrayList<>();

        doctorAvalabilityList = (ArrayList<DoctorAvalability>) getIntent().getSerializableExtra("Avalability");
        doctorid = getIntent().getStringExtra("DoctorID");
        userid = PreferenceManager.getDefaultSharedPreferences(BookAppointmentActivity.this).getString("UserID","");

         new CreateData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void initiateView(){

        days = new TextView[7];

        slotGrid = (GridView) findViewById(R.id.grid_slot);
        days[0] = (TextView) findViewById(R.id.mon);
        days[1] = (TextView) findViewById(R.id.tue);
        days[2] = (TextView) findViewById(R.id.wed);
        days[3] = (TextView) findViewById(R.id.thu);
        days[4] = (TextView) findViewById(R.id.fri);
        days[5] = (TextView) findViewById(R.id.sat);
        days[6] = (TextView) findViewById(R.id.sun);
        noData = (TextView) findViewById(R.id.txt_noData);
        book = (Button) findViewById(R.id.btn_book_appointment);
        loading =(ProgressWheel) findViewById(R.id.loading);

        slotGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                slot = appoinmentList.get(globPos).getSlotList().get(position);
            }
        });

        days[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSlotAdapter(0);

            }
        });
        days[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSlotAdapter(1);
            }
        });

        days[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSlotAdapter(2);
            }
        });

        days[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSlotAdapter(3);
            }
        });

        days[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSlotAdapter(4);
            }
        });

        days[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSlotAdapter(5);
            }
        });

        days[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSlotAdapter(6);
            }
        });


        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = appointmenteRef.push();
                BookAppointment bookAppointment = new BookAppointment();
                bookAppointment.setDate(date);
                bookAppointment.setSlot(slot);
                bookAppointment.setDoctorId(doctorid);
                bookAppointment.setUserId(userid);
                ref.setValue(bookAppointment);
                Constants.alertDialog(BookAppointmentActivity.this, "Appointment", "Your Appointment Booked Successfully.You will received confirmation message on Your registered mobile number shortly");
            }
        });

    }


    public void  setSlotAdapter(int position){

        for(int i=0;i<7;i++){
            if(position == i){
                days[i].setBackgroundResource(R.drawable.layout_green_curved);
            }
            else {
                days[i].setBackgroundResource(R.drawable.layout_white_curved);
            }
        }
        date = appoinmentList.get(position).getDate().split("-")[0];
        globPos = position;
        if(appoinmentList.get(position).getSlotList().size()>0) {
            slotGrid.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            BookAppointmentListAdapter bookAppointmentListAdapter =
                    new BookAppointmentListAdapter(appoinmentList.get(position).getSlotList(), BookAppointmentActivity.this);
            slotGrid.setAdapter(bookAppointmentListAdapter);
        }
        else {
            slotGrid.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
        loading.setVisibility(View.GONE);

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

    public class CreateData extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            loading.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String currentdate = Constants.getCurrentDateTime();
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy-HH:mm a");
            Date current = null;
            try {
                current = df.parse(currentdate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(current);

            for(int i=0;i<7;i++){
                int day = cal.get(Calendar.DAY_OF_WEEK);
                String dayOfWeek = Constants.getDayOfWeek(day);

                BookApointmentModel bookApointmentModel = new BookApointmentModel();
                bookApointmentModel.setDate(df.format(cal.getTime()).split("-")[0]);
                bookApointmentModel.setDay(dayOfWeek);
                appoinmentList.add(bookApointmentModel);

                cal.add(Calendar.DATE,1);
            }

            for(int i=0;i<doctorAvalabilityList.size();i++){
                    int start = Constants.getDayOfWeekDiff(doctorAvalabilityList.get(i).getWeekDayStart());
                    int end = Constants.getDayOfWeekDiff(doctorAvalabilityList.get(i).getWeekDayEnd());

                    if(start == end){
                        for(int j=0;j<appoinmentList.size();j++) {
                            if (doctorAvalabilityList.get(i).getWeekDayStart().equalsIgnoreCase(appoinmentList.get(j).getDay())) {
                                appoinmentList.get(j).setStartTime(doctorAvalabilityList.get(i).getAvailableStartTime());
                                appoinmentList.get(j).setEndTime(doctorAvalabilityList.get(i).getAvailableEndTime());
                                appoinmentList.get(j).setStartTime2(doctorAvalabilityList.get(i).getAvailableStart2Time());
                                appoinmentList.get(j).setEndTime2(doctorAvalabilityList.get(i).getAvailableEn2Time());
                                break;
                            }
                        }
                    }
                    else {
                        int limit;
                        if(start > end){
                            if(end == 1){
                                limit = (8-start);
                            }
                            else {
                                limit = end;
                            }

                        }
                        else {
                            limit = Math.abs(end - start);
                        }

                        int temp=start;
                        for(int k=0;k<=limit;k++){

                            for(int j=0;j<appoinmentList.size();j++) {
                                if (Constants.getDayOfWeek(temp).equalsIgnoreCase(appoinmentList.get(j).getDay())) {
                                    appoinmentList.get(j).setStartTime(doctorAvalabilityList.get(i).getAvailableStartTime());
                                    appoinmentList.get(j).setEndTime(doctorAvalabilityList.get(i).getAvailableEndTime());
                                    appoinmentList.get(j).setStartTime2(doctorAvalabilityList.get(i).getAvailableStart2Time());
                                    appoinmentList.get(j).setEndTime2(doctorAvalabilityList.get(i).getAvailableEn2Time());
                                    if(temp == Calendar.SATURDAY){
                                        temp = Calendar.SUNDAY;
                                    }
                                    else {
                                        temp = temp +1;
                                    }
                                    break;
                                }
                            }
                        }
                    }
            }

            for(int i=0;i<appoinmentList.size();i++){
                List<String> list1 = Constants.getTimeSlot(appoinmentList.get(i).getStartTime(),appoinmentList.get(i).getEndTime());
                List<String> list2 = Constants.getTimeSlot(appoinmentList.get(i).getStartTime2(),appoinmentList.get(i).getEndTime2());
                List<String> finalList = new ArrayList<>();
                finalList.addAll(list1);
                finalList.addAll(list2);
                appoinmentList.get(i).setSlotList(finalList);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            days[0].setText(appoinmentList.get(0).getDay()+"\n"+appoinmentList.get(0).getDate().split(" ")[0] + " " +appoinmentList.get(0).getDate().split(" ")[1]);
            days[1].setText(appoinmentList.get(1).getDay()+"\n"+appoinmentList.get(1).getDate().split(" ")[0] + " " +appoinmentList.get(1).getDate().split(" ")[1]);
            days[2].setText(appoinmentList.get(2).getDay()+"\n"+appoinmentList.get(2).getDate().split(" ")[0] + " " +appoinmentList.get(2).getDate().split(" ")[1]);
            days[3].setText(appoinmentList.get(3).getDay()+"\n"+appoinmentList.get(3).getDate().split(" ")[0] + " " +appoinmentList.get(3).getDate().split(" ")[1]);
            days[4].setText(appoinmentList.get(4).getDay()+"\n"+appoinmentList.get(4).getDate().split(" ")[0] + " " +appoinmentList.get(4).getDate().split(" ")[1]);
            days[5].setText(appoinmentList.get(5).getDay()+"\n"+appoinmentList.get(5).getDate().split(" ")[0] + " " +appoinmentList.get(5).getDate().split(" ")[1]);
            days[6].setText(appoinmentList.get(6).getDay()+"\n"+appoinmentList.get(6).getDate().split(" ")[0] + " " +appoinmentList.get(6).getDate().split(" ")[1]);

            if(appoinmentList.get(0).getSlotList().size()>0) {
                setSlotAdapter(0);
            }
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
