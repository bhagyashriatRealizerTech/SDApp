package com.realizer.sallado;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.realizer.sallado.databasemodel.User;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.FontManager;
import com.realizer.sallado.utils.Singleton;
import com.realizer.sallado.view.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Callable;

public class SignUpActivity extends AppCompatActivity {


    Button signup;
    FirebaseDatabase database;
    DatabaseReference myRef;
    EditText fname,lname,emailid,mobno;
    RadioButton male,female,veg,nonveg,both;
    TextView bdy;
    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar;
    ProgressWheel loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        if(Singleton.getDatabase() == null)
            Singleton.setDatabase(FirebaseDatabase.getInstance());

        database = Singleton.getDatabase();
        myRef = database.getReference("User");
        myRef.keepSynced(true);

        setContentView(R.layout.signup_activity);

        initiateView();
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                upDateLable();
            }

        };

    }

    public void upDateLable( ) {
        String myFormat = "dd MMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        bdy.setText(sdf.format(myCalendar.getTime()));
    }

    public final void bdyClick( ) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this, date, year, month, day);
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year-18);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }


    public void initiateView(){

        signup = (Button) findViewById(R.id.btn_signup);
        fname = (EditText) findViewById(R.id.edt_fname);
        lname = (EditText) findViewById(R.id.edt_lname);
        emailid = (EditText) findViewById(R.id.edt_emailid);
        mobno = (EditText) findViewById(R.id.edt_mobno);
        bdy = (TextView) findViewById(R.id.txt_bday);
        loading =(ProgressWheel) findViewById(R.id.loading);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        veg = (RadioButton) findViewById(R.id.veg);
        nonveg = (RadioButton) findViewById(R.id.nonveg);
        both = (RadioButton) findViewById(R.id.both);
        female.setChecked(true);
        veg.setChecked(true);

        bdy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bdyClick();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fname.getText().toString().trim().length() <=0){

                    Constants.alertDialog(SignUpActivity.this,"Login","First Name can not be Empty.\nPlease Enter First Name");
                }
                else if(lname.getText().toString().trim().length() <=0){
                    Constants.alertDialog(SignUpActivity.this,"Login","Last Name can not be Empty.\nPlease Enter First Name");
                }
               else if(bdy.getText().toString().trim().length() <=0){

                    Constants.alertDialog(SignUpActivity.this,"Login","Birthday can not be Empty.\nPlease Add Birthday");
                }
                else if(emailid.getText().toString().trim().length() <=0){
                    Constants.alertDialog(SignUpActivity.this,"Login","Email ID can not be Empty.\nPlease Add Email Id");
                }
                else if(mobno.getText().toString().trim().length() <=0){
                    Constants.alertDialog(SignUpActivity.this,"Login","Mobile Number can not be Empty.\nPlease Add Mobile Number");
                }
                else {
                loading.setVisibility(View.VISIBLE);
                User newUser = new User();
                newUser.setUserName(fname.getText().toString().trim() + " " + lname.getText().toString().trim());
                newUser.setEmailId(emailid.getText().toString().trim());
                newUser.setUserId(UUID.randomUUID().toString());
                newUser.setUserLoginID(emailid.getText().toString().trim());
                newUser.setPassword("test1");
                newUser.setActive(true);
                newUser.setFirstLogin(true);
                newUser.setUserType("Customer");
                newUser.setBirthday(bdy.getText().toString());
                newUser.setMobileNo(mobno.getText().toString().trim());

                if (veg.isChecked())
                    newUser.setUserDietType("Veg");
                else if (nonveg.isChecked())
                    newUser.setUserDietType("NonVeg");
                else if (both.isChecked())
                    newUser.setUserDietType("Both");
                else
                    newUser.setUserDietType("");

                if (male.isChecked())
                    newUser.setGender("Male");
                else if (female.isChecked())
                    newUser.setGender("Female");
                else
                    newUser.setGender("");

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("UserName", newUser.getUserName());
                edit.putString("DietType", newUser.getUserDietType());
                edit.putString("Email", newUser.getEmailId());
                edit.putString("UserID", newUser.getUserId());
                edit.putString("MobNo", newUser.getMobileNo());
                edit.putString("IsLogin", "true");
                edit.commit();

                writeNewPost(newUser);
            }

            }
        });
    }
    private void writeNewPost(User newUser) {
        DatabaseReference ref = myRef.push();
        ref.setValue(newUser);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("UserID",ref.getKey());
        edit.commit();

        loading.setVisibility(View.GONE);

        alertDialog();

    }

    public  void alertDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialoglayout = inflater.inflate(R.layout.custom_dialogbox, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setView(dialoglayout);

        Button buttonok= (Button) dialoglayout.findViewById(R.id.alert_btn_ok);
        TextView titleName=(TextView) dialoglayout.findViewById(R.id.alert_dialog_title);
        TextView alertMsg=(TextView) dialoglayout.findViewById(R.id.alert_dialog_message);
        TextView close=(TextView) dialoglayout.findViewById(R.id.txt_close);
        close.setTypeface(FontManager.getTypeface(SignUpActivity.this, FontManager.FONTAWESOME));

        close.setVisibility(View.GONE);

        final AlertDialog alertDialog = builder.create();

        buttonok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                alertDialog.dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        titleName.setText("SignUp");
        alertMsg.setText("You are Registered Successfully\nEnjoy the healthy food.");
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
