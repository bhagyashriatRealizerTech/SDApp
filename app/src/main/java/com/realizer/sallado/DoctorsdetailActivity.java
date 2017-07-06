package com.realizer.sallado;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realizer.sallado.adapter.DoctorListAdapter;
import com.realizer.sallado.model.MedicalPanelListModel;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.FontManager;
import com.realizer.sallado.utils.ImageStorage;
import com.realizer.sallado.utils.Singleton;
import com.realizer.sallado.view.ProgressWheel;

import java.io.Serializable;

public class DoctorsdetailActivity extends AppCompatActivity {
    ImageView profileImage;
    TextView name,degree,experience,ratings,descrption,clinicName,address,timings,fees;
    FirebaseDatabase database;
    DatabaseReference medicalPanelRef;
    Button book;
    ProgressWheel loading;
    MedicalPanelListModel medicalPanelListModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Singleton.getDatabase() == null)
            Singleton.setDatabase(FirebaseDatabase.getInstance());

        database = Singleton.getDatabase();
        medicalPanelRef = database.getReference("MedicalPanelData");

        setContentView(R.layout.doctor_detail_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initiateView();
        setValue();

    }

    public void setValue(){
        loading.setVisibility(View.VISIBLE);
        Bundle bundle = getIntent().getExtras();
        Query query = medicalPanelRef.child(bundle.getString("DoctorID",""));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                         medicalPanelListModel = dataSnapshot.getValue(MedicalPanelListModel.class);

                        name.setText(medicalPanelListModel.getMedicalPanel().getDoctorName());
                        degree.setText(medicalPanelListModel.getMedicalPanel().getDoctorDegree());
                        experience.setText(medicalPanelListModel.getMedicalPanel().getDoctorExperience());
                        ratings.setText("99%");
                        descrption.setText(medicalPanelListModel.getMedicalPanel().getDoctorDetailDescrption());
                        clinicName.setText(medicalPanelListModel.getDoctorLocations().get(0).getClinicName());
                        address.setText(medicalPanelListModel.getDoctorLocations().get(0).getAddress());
                        String time = medicalPanelListModel.getDoctorAvalabilities().get(0).getWeekDayStart() + " To " +
                                medicalPanelListModel.getDoctorAvalabilities().get(0).getWeekDayEnd() + "\n" + medicalPanelListModel.getDoctorAvalabilities().get(0).getAvailableStartTime()
                                + " - " + medicalPanelListModel.getDoctorAvalabilities().get(0).getAvailableEndTime();
                        timings.setText(time);
                        fees.setText(medicalPanelListModel.getMedicalPanel().getDoctorConsultationFess());
                        if (!medicalPanelListModel.getMedicalPanel().getDoctorThumbnail().isEmpty()) {
                            ImageStorage.setThumbnail(profileImage, medicalPanelListModel.getMedicalPanel().getDoctorThumbnail());
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

    public void initiateView(){
        profileImage = (ImageView) findViewById(R.id.img_doctorPic);
        name = (TextView) findViewById(R.id.txt_name);
        degree = (TextView) findViewById(R.id.txt_degree);
        experience = (TextView) findViewById(R.id.txt_experience);
        ratings = (TextView) findViewById(R.id.txt_ratings);
        descrption = (TextView) findViewById(R.id.txt_description);
        clinicName = (TextView) findViewById(R.id.txt_Clicnicname);
        address = (TextView) findViewById(R.id.txt_Address);
        timings = (TextView) findViewById(R.id.txt_time);
        fees = (TextView) findViewById(R.id.txt_fees);
        book = (Button) findViewById(R.id.btn_book);
        loading =(ProgressWheel) findViewById(R.id.loading);



        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DoctorsdetailActivity.this);
                if(preferences.getBoolean("IsSkip",false))
                {
                    alertDialogLogin(DoctorsdetailActivity.this,"Login","Please Login to book appointment with expert.");
                }
                else {
                    if(Constants.isConnectingToInternet(DoctorsdetailActivity.this))
                    {
                        Intent intent = new Intent(DoctorsdetailActivity.this,BookAppointmentActivity.class);
                        intent.putExtra("Avalability", (Serializable) medicalPanelListModel.getDoctorAvalabilities());
                        intent.putExtra("DoctorID",getIntent().getExtras().getString("DoctorID",""));
                        startActivity(intent);
                    }

                    else
                        Constants.alertDialog(DoctorsdetailActivity.this,"Network Error","Your device not connected to internet");
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

                Intent intent = new Intent(DoctorsdetailActivity.this, LoginActivity.class);
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
