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



        /*DoctorListModel doctorListModel = new DoctorListModel();

        doctorListModel.setImageUrl("https://images1-fabric.practo.com/53ae87f902b95611e23eaf4a64d2f06f86c7485a55e78.jpg");
        doctorListModel.setDoctorName("Mrs. Geeta Dharmatti");
        doctorListModel.setClinicName("DOSS Clinic");
        doctorListModel.setDoctorDeggre("MSc - Dietitics / Nutrition , BSc - Dietitics / Nutrition");
        doctorListModel.setDoctorExperience("18 years of Experience");
        doctorListModel.setFees("INR 600");
        doctorListModel.setTimings("THU => 5:00 PM - 8:00 PM");
        doctorListModel.setAdrs("Dhole Patil Road, Pune");
        doctorListModel.setDoctordesc("DOSS Clinic  and 2 more clinics");
        doctorListModel.setDetailAddress("409-410, Sohrab Hall, Sasoon Road,\nLandmark : Opposite Jehangir Hospital,\nPune");
        doctorListModel.setDetaildescription("Mrs. Geeta Dharmatti is a Dietitian/Nutritionist in Dhole Patil Road, Pune. Mrs. Geeta Dharmatti practices at DOSS Clinic in Dhole Patil Road, Pune, Nutrition Clinic in Aundh, Pune and Aditya Birla Memorial Hospital in Chinchwad, Pune. She completed MSc - Dietitics / Nutrition and BSc - Dietitics / Nutrition.She is a member of Indian Dietetic Association. Service provided by the therapist is: Diet Therapy." +
              "You can book an instant appointment with Mrs. Geeta Dharmatti on Practo.com.");
        doctorListModels.add(doctorListModel);

        DoctorListModel doctorListModel1 = new DoctorListModel();
        doctorListModel1.setImageUrl("https://images1-fabric.practo.com/dr-namrata-anand-1482842689-5862624126c91.jpg");
        doctorListModel1.setDoctorName("Dr. Namrata Anand");
        doctorListModel1.setClinicName("Pulsetila Health Clinic");
        doctorListModel1.setDoctorDeggre("BHMS , DNHE , Homeopath\"");
        doctorListModel1.setDoctorExperience("16 Years Experience");
        doctorListModel1.setFees("INR 250");
        doctorListModel1.setTimings("TUE - WED, FRI => 5:00 PM - 8:00 PM\nSUN => 10:30 AM - 1:30 PM");
        doctorListModel1.setAdrs("Pimple Saudagar, Pune");
        doctorListModel1.setDoctordesc("Pulsetila Health Clinic  and 2 more clinics");
        doctorListModel1.setDetailAddress("Pimple Saudagar,\nLandmark: Near Govind Garden & Opp.Palm Breeze,\nPune");
        doctorListModel1.setDetaildescription("Dr. Namrata Anand is a Homeopathic Doctor and Dietitian. She having her clinics in Pimpri, Sector 25, Nigdi. You can book an instant appointment with Dr. Namrata Anand on Practo.com.");
        doctorListModels.add(doctorListModel1);

        DoctorListModel doctorListModel2 = new DoctorListModel();
        doctorListModel2.setImageUrl("https://images1-fabric.practo.com/dr-jaya-dewani-1488712031-58bbf15f1964b.jpg");
        doctorListModel2.setDoctorName("Dr. Jaya Dewani");
        doctorListModel2.setClinicName("JRD Diet Center");
        doctorListModel2.setDoctorDeggre("MBBS , Post Graduation Diploma in Dietetics , Certificate Course in Fitness Training");
        doctorListModel2.setDoctorExperience("16 Years Experience");
        doctorListModel2.setFees("INR 700");
        doctorListModel2.setTimings("MON - FRI => 8:30 AM - 1:30 PM");
        doctorListModel2.setAdrs("Bhosari Industrial Estate, Pune");
        doctorListModel2.setDoctordesc("JRD Diet Center");
        doctorListModel2.setDetailAddress("C/O Dental Care Multi specialty,\nSector 6, Plot Number 37, Flat Number C 001,\nRiddhi Siddhi Sanskruti Apartment,\nBhosari Industrial Estate,\nLandmark : Near Spine City Mall,\nPune");
        doctorListModel2.setDetaildescription("A certified physician and a certified dietitian. When it comes to the field of nutrition Dr. Jaya enjoys this rare distinction. To add to her qualification, she also has a rich experience of having worked as a clinical nutritionist for well over a decade. During her extensive professional career, Dr. Jaya has emphasized on converting scientific information into practical advice. She has dealt with clients suffering from peculiar medical conditions, including diabetes, obesity, arthritis, hyper cholesterolemia, cardiovascular diseases, infectious diseases and various aspects of infertility." +
              "After completing her MBBS from University of Pune, Dr. Jaya also finished a Post Graduate Diploma in Dietetics from SNDT University in Mumbai. During her 16-plus years’ career, she has worked with some of the most reputed hospitals and wellness organisations in the state. She has held several key positions in the field of nutrition, including clinical nutritionist, research dietitian, medical officer and consultant dietitian. You can get the phone number of Dr. Jaya Dewani on Practo.com");
        doctorListModels.add(doctorListModel2);

*/


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
