package com.realizer.sallado;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.realizer.sallado.view.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Callable;

public class SignUpActivity extends AppCompatActivity {


    Button signup;
    FirebaseDatabase database;
    DatabaseReference myRef;
    EditText fname,lname,emailid,mobno;
    TextView bdy;
    ProgressWheel loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();


        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User");

        setContentView(R.layout.signup_activity);
        initiateView();
    }


    public void initiateView(){

        signup = (Button) findViewById(R.id.btn_signup);
        fname = (EditText) findViewById(R.id.edt_fname);
        lname = (EditText) findViewById(R.id.edt_lname);
        emailid = (EditText) findViewById(R.id.edt_emailid);
        mobno = (EditText) findViewById(R.id.edt_mobno);
        bdy = (TextView) findViewById(R.id.txt_bday);
        loading =(ProgressWheel) findViewById(R.id.loading);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);

                User newUser = new User();
                newUser.setUserName(fname.getText().toString().trim()+" "+ lname.getText().toString().trim());
                newUser.setEmailId(emailid.getText().toString().trim());
                newUser.setUserId(UUID.randomUUID().toString());
                newUser.setUserLoginID(emailid.getText().toString().trim());
                newUser.setPassword("test1");
                newUser.setActive(true);
                newUser.setFirstLogin(true);
                newUser.setUserType("Customer");
                newUser.setMobileNo(mobno.getText().toString().trim());
                newUser.setEmailId("test1");
                newUser.setUserDietType("");



                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("UserName",newUser.getUserName());
                edit.putString("DietType",newUser.getUserDietType());
                edit.putString("Email",newUser.getEmailId());
                edit.putString("UserID",newUser.getUserId());
                edit.putString("IsLogin","true");
                edit.commit();

                writeNewPost(newUser);

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

        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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
