package com.realizer.sallado;

import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realizer.sallado.databasemodel.User;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.FontManager;
import com.realizer.sallado.utils.Singleton;
import com.realizer.sallado.view.ProgressWheel;

public class MyAccountActivity extends AppCompatActivity {

    EditText userName,userEmail,userMobile,userAddress;
    TextView edit,bdy;
    RadioButton male,female,veg,nonveg,both;
    ImageView userImage;
    LinearLayout info;
    SharedPreferences preferences;
    Button done;
    ProgressWheel loading;
    FirebaseDatabase database;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Constants.actionBarTitle("My Account", this));
        preferences  = PreferenceManager.getDefaultSharedPreferences(MyAccountActivity.this);
        database = Singleton.getDatabase();
        myRef = database.getReference("User");
        myRef.keepSynced(true);
        initiateView();
        setValue();

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
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

    public void setValue(){
        Query query = myRef.child(preferences.getString("UserID",""));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                        // do something with the individual "issues"
                        SharedPreferences.Editor edit = preferences.edit();
                        User user  = dataSnapshot.getValue(User.class);
                        edit.putString("UserName",user.getUserName());
                        edit.putString("DietType",user.getUserDietType());
                        edit.putString("Email",user.getEmailId());
                        edit.putString("MobNo", user.getMobileNo());
                        edit.putString("UserID",dataSnapshot.getKey());
                        edit.putString("IsLogin","true");
                        edit.apply();

                        loading.setVisibility(View.GONE);

                        userName.setText(user.getUserName());
                        userEmail.setText(user.getEmailId());
                        userMobile.setText(user.getMobileNo());
                        bdy.setText(user.getBirthday());
                        userAddress.setText(preferences.getString("Address",""));
                    if(userAddress.getText().toString().length()<=0)
                        userAddress.setText("Address Not Available");
                    if(user.getUserDietType().equalsIgnoreCase("Veg"))
                        veg.setChecked(true);
                    else if(user.getUserDietType().equalsIgnoreCase("NonVeg"))
                        nonveg.setChecked(true);
                    else
                        both.setChecked(true);

                    if(user.getGender().equalsIgnoreCase("Female"))
                        female.setChecked(true);
                    else
                        male.setChecked(true);

                    enableDisable(false);

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

        userName = (EditText) findViewById(R.id.txt_username);
        userEmail = (EditText) findViewById(R.id.txt_useremail);
        userMobile = (EditText) findViewById(R.id.txt_usermobile);
        userAddress = (EditText) findViewById(R.id.txt_adrs);
        edit = (TextView) findViewById(R.id.txt_edit);
        bdy = (TextView) findViewById(R.id.txt_birthday);
        done = (Button) findViewById(R.id.btn_done);
        userImage = (ImageView) findViewById(R.id.img_user);
        info = (LinearLayout) findViewById(R.id.layout_info);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        veg = (RadioButton) findViewById(R.id.veg);
        nonveg = (RadioButton) findViewById(R.id.nonveg);
        both = (RadioButton) findViewById(R.id.both);
        loading = (ProgressWheel) findViewById(R.id.loading);

        edit.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableDisable(true);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableDisable(false);
            }
        });

    }

    public void enableDisable(boolean value){
        userName.setEnabled(value);
        userEmail.setEnabled(value);
        userMobile.setEnabled(value);
        userAddress.setEnabled(value);
        male.setEnabled(value);
        female.setEnabled(value);
        veg.setEnabled(value);
        nonveg.setEnabled(value);
        both.setEnabled(value);
        if(!value){
            if(male.isChecked())
                male.setEnabled(true);
            else
                female.setEnabled(true);

            if(veg.isChecked())
                veg.setEnabled(true);
            else if(nonveg.isChecked())
                nonveg.setEnabled(true);
            else
                both.setEnabled(true);
        }

        if(value){
            userName.setBackgroundResource(R.drawable.dashboard_icon_background);
            userEmail.setBackgroundResource(R.drawable.dashboard_icon_background);
            userMobile.setBackgroundResource(R.drawable.dashboard_icon_background);
            userAddress.setBackgroundResource(R.drawable.dashboard_icon_background);
            bdy.setBackgroundResource(R.drawable.dashboard_icon_background);
            done.setVisibility(View.VISIBLE);
            info.setBackgroundColor(Color.TRANSPARENT);
        }
        else {
            userName.setTextColor(Color.BLACK);
            userEmail.setTextColor(Color.BLACK);
            userMobile.setTextColor(Color.BLACK);
            userAddress.setTextColor(Color.BLACK);
            male.setTextColor(Color.BLACK);
            female.setTextColor(Color.BLACK);
            veg.setTextColor(Color.BLACK);
            nonveg.setTextColor(Color.BLACK);
            both.setTextColor(Color.BLACK);
            bdy.setTextColor(Color.BLACK);

            userName.setBackgroundColor(Color.TRANSPARENT);
            userEmail.setBackgroundColor(Color.TRANSPARENT);
            userMobile.setBackgroundColor(Color.TRANSPARENT);
            userAddress.setBackgroundColor(Color.TRANSPARENT);
            bdy.setBackgroundColor(Color.TRANSPARENT);

            done.setVisibility(View.GONE);
            info.setBackgroundResource(R.drawable.dashboard_icon_background);
        }
    }
}
