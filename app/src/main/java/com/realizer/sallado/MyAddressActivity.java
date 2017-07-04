package com.realizer.sallado;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realizer.sallado.databasemodel.User;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.Singleton;
import com.realizer.sallado.view.ProgressWheel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Win on 22-06-2017.
 */

public class MyAddressActivity extends AppCompatActivity {

    RadioButton myLoc,existingLoc,addlLoc;
    Spinner locations;
    Button addnew,done;
    EditText adrs,landmark,city,pincode;
    String userId;
    FirebaseDatabase database;
    DatabaseReference myRef;
    SharedPreferences preferences;
    ProgressWheel loading;
    String address ="";
    User user;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_layout);

        preferences  = PreferenceManager.getDefaultSharedPreferences(MyAddressActivity.this);
        database = Singleton.getDatabase();
        myRef = database.getReference("User");
        myRef.keepSynced(true);

        userId = preferences.getString("UserID","");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Constants.actionBarTitle("Address", this));
        initiateView();
        setValue();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    public void setValue(){
        loading.setVisibility(View.VISIBLE);
        Query query = myRef.child(preferences.getString("UserID",""));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user  = dataSnapshot.getValue(User.class);
                    if(user.getUserAddress() != null){
                        if(user.getUserAddress().size()>0){
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyAddressActivity.this,
                                    android.R.layout.simple_spinner_item, user.getUserAddress());
                            adapter.setDropDownViewResource(R.layout.spinner_dropdown);
                            locations.setAdapter(adapter);
                            locations.setSelection(0);
                        }
                    }
                    loading.setVisibility(View.GONE);
                }
                else {
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
        myLoc = (RadioButton) findViewById(R.id.rd_mylocation);
        existingLoc = (RadioButton) findViewById(R.id.rd_existingLoc);
        addlLoc = (RadioButton) findViewById(R.id.rd_add);
        locations = (Spinner) findViewById(R.id.spin_adrs);
        addnew = (Button) findViewById(R.id.add);
        done = (Button) findViewById(R.id.done);
        adrs = (EditText) findViewById(R.id.edt_adrs);
        landmark = (EditText) findViewById(R.id.edt_landmark);
        city = (EditText) findViewById(R.id.edt_city);
        pincode = (EditText) findViewById(R.id.edt_pincode);
        loading = (ProgressWheel) findViewById(R.id.loading);

        existingLoc.setChecked(true);
        locations.setEnabled(true);
        myLoc.setChecked(false);
        setAddNew(false);

        existingLoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    existingLoc.setChecked(true);
                    locations.setEnabled(true);
                    myLoc.setChecked(false);
                    setAddNew(false);
                }
            }
        });

        myLoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    existingLoc.setChecked(false);
                    locations.setEnabled(false);
                    myLoc.setChecked(true);
                    setAddNew(false);
                    Intent intent = new Intent(MyAddressActivity.this,MyCurrentLocationActivity.class);
                    intent.putExtra("UserInfo",(Serializable) user);
                    startActivity(intent);
                }
            }
        });

        addlLoc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    existingLoc.setChecked(false);
                    locations.setEnabled(false);
                    myLoc.setChecked(false);
                    setAddNew(true);
                }
            }
        });

        locations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                address = (String) adapterView.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adrs.getText().toString().trim().length()<=0){

                }
                else if(landmark.getText().toString().trim().length()<=0){

                }
                else if(city.getText().toString().trim().length()<=0){

                }
                else if(pincode.getText().toString().trim().length()<=0){

                }
                else {

                    loading.setVisibility(View.VISIBLE);
                    address = adrs.getText().toString().trim()+",\n"+
                            landmark.getText().toString().trim()+", "+
                            city.getText().toString().trim()+".\nPincode: "+pincode.getText().toString().trim();

                    List<String> temp = new ArrayList<String>();
                    if(user.getUserAddress() == null){
                        temp.add(address);
                        user.setUserAddress(temp);
                    }
                    else {
                        temp = user.getUserAddress();
                        temp.add(address);
                        user.setUserAddress(temp);

                    }
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putString("Address",address);
                    edit.commit();
                    myRef.child(userId).setValue(user);
                    loading.setVisibility(View.GONE);
                    finish();
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("Address",address);
                edit.commit();
                finish();
            }
        });

    }
    public void setAddNew(boolean value){
        addlLoc.setChecked(value);
        adrs.setEnabled(value);
        landmark.setEnabled(value);
        city.setEnabled(value);
        pincode.setEnabled(value);
        addnew.setEnabled(value);

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

        if(Singleton.getInstance().isDoneClick()){
            Singleton.getInstance().setIsDoneClick(false);
            finish();
        }
    }
}
