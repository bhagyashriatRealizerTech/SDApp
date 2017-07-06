package com.realizer.sallado;

import android.*;
import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realizer.sallado.databasemodel.DishGroup;
import com.realizer.sallado.databasemodel.DishGroupItem;
import com.realizer.sallado.databasemodel.User;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.FontManager;
import com.realizer.sallado.utils.Singleton;
import com.realizer.sallado.view.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

public class LoginActivity extends AppCompatActivity {
    Button btn_login;
    TextView signup,skip;
    CallbackManager mFacebookCallbackManager;
    LoginButton mFacebookSignInButton;
    SignInButton mGoogleSignInButton;
    private static final int RC_SIGN_IN = 9001;
    GoogleApiClient mGoogleApiClient;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String email,fromWhere,name;
    Button button;
    EditText mobileno;
    ProgressWheel loading;
    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mFacebookCallbackManager = CallbackManager.Factory.create();

        if(Singleton.getDatabase() == null)
        Singleton.setDatabase(FirebaseDatabase.getInstance());

        database = Singleton.getDatabase();
        myRef = database.getReference("User");
        myRef.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.login_activity);
        getSupportActionBar().hide();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission())
            {
                requestForSpecificPermission();
                //sleeptime=4000;

            }
        }



        mFacebookSignInButton = (LoginButton)findViewById(R.id.facebook_sign_in_button);
        mFacebookSignInButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        mFacebookSignInButton.registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());

                                        // Application code
                                        try {
                                            email = object.getString("email");
                                            String birthday = object.getString("birthday"); // 01/31/1980 format
                                            name = object.getString("name");
                                            LoginManager.getInstance().logOut();
                                            fromWhere = "facebook";
                                            userLogin();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                        loading.setVisibility(View.VISIBLE);
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        loading.setVisibility(View.GONE);
                        handleSignInResult(null);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        loading.setVisibility(View.GONE);
                        Log.d(LoginActivity.class.getCanonicalName(), error.getMessage());
                        handleSignInResult(null);
                    }
                }
        );

        mGoogleSignInButton = (SignInButton)findViewById(R.id.google_sign_in_button);
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                signInWithGoogle();
            }
        });
        initiateView();
    }

    private void signInWithGoogle() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void handleSignInResult(Callable<Void> callable) {}


    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.WAKE_LOCK,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_NETWORK_STATE

                }, 101);
    }

    public void initiateView(){

        btn_login = (Button) findViewById(R.id.btn_Submit);
        signup = (TextView) findViewById(R.id.txt_signup);
        skip = (TextView) findViewById(R.id.txt_skip);
        mobileno = (EditText) findViewById(R.id.edt_mobileno);
        loading =(ProgressWheel) findViewById(R.id.loading);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromWhere = "button";
                loading.setVisibility(View.VISIBLE);
                userLogin();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();

            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putBoolean("IsSkip",true);
                edit.commit();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void writeNewPost(User newUser) {
        DatabaseReference ref = myRef.push();
        ref.setValue(newUser);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("UserID",ref.getKey());
        edit.putBoolean("IsSkip",false);
        edit.commit();

        loading.setVisibility(View.GONE);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void userLogin(){
        if(fromWhere.equalsIgnoreCase("button")) {
            if(mobileno.getText().toString().trim().length() == 10) {
                Query query = myRef.orderByChild("mobileNo").equalTo(mobileno.getText().toString().trim());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // dataSnapshot is the "issue" node with all children with id 0
                            for (DataSnapshot issue : dataSnapshot.getChildren()) {
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                SharedPreferences.Editor edit = preferences.edit();
                                User user = issue.getValue(User.class);
                                edit.putString("UserName", user.getUserName());
                                edit.putString("DietType", user.getUserDietType());
                                edit.putString("Email", user.getEmailId());
                                edit.putString("MobNo", user.getMobileNo());
                                edit.putString("UserID", issue.getKey());
                                edit.putString("IsLogin", "true");
                                edit.putBoolean("IsSkip",false);
                                edit.apply();

                            }
                            loading.setVisibility(View.GONE);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            loading.setVisibility(View.GONE);
                            Constants.alertDialog(LoginActivity.this, "Login", "Invalid Credentials\nPlease try again with valid credentials.\n");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        loading.setVisibility(View.GONE);
                    }
                });
            }
            else {
                loading.setVisibility(View.GONE);
                Constants.alertDialog(LoginActivity.this, "Login", "Invalid Mobile Number.\nPlease Enter Valid Mobile Number\n");
            }
        }
        else {
            Query query = myRef.orderByChild("userLoginID").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // dataSnapshot is the "issue" node with all children with id 0
                        for (DataSnapshot issue : dataSnapshot.getChildren()) {
                            // do something with the individual "issues"
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            SharedPreferences.Editor edit = preferences.edit();
                            User user  = issue.getValue(User.class);
                            edit.putString("UserName",user.getUserName());
                            edit.putString("DietType",user.getUserDietType());
                            edit.putString("Email",user.getEmailId());
                            edit.putString("MobNo", user.getMobileNo());
                            edit.putString("UserID",issue.getKey());
                            edit.putString("IsLogin","true");
                            edit.putBoolean("IsSkip",false);
                            edit.apply();

                            loading.setVisibility(View.GONE);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                    else {
                        loading.setVisibility(View.GONE);
                        loginInfo();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    loading.setVisibility(View.GONE);
                }
            });
        }
    }

    public void upDateLable(TextView textView) {
        String myFormat = "dd MMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        textView.setText(sdf.format(myCalendar.getTime()));
    }

    public final void bdyClick( ) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(LoginActivity.this, date, year, month, day);
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year-18);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }


    public  void loginInfo() {
        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialoglayout = inflater.inflate(R.layout.login_social_remainig_info, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(dialoglayout);

        Button buttonok= (Button) dialoglayout.findViewById(R.id.btn_signup);
        final TextView bdy=(TextView) dialoglayout.findViewById(R.id.txt_bday);
        final EditText mobno=(EditText) dialoglayout.findViewById(R.id.edt_mobno);
        final RadioButton male=(RadioButton) dialoglayout.findViewById(R.id.male);
        final RadioButton female=(RadioButton) dialoglayout.findViewById(R.id.female);
        final RadioButton veg=(RadioButton) dialoglayout.findViewById(R.id.veg);
        final RadioButton nonveg=(RadioButton) dialoglayout.findViewById(R.id.nonveg);
        final RadioButton both=(RadioButton) dialoglayout.findViewById(R.id.both);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);

        veg.setChecked(true);
        female.setChecked(true);

        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                upDateLable(bdy);
            }

        };



        bdy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bdyClick();
            }
        });


        buttonok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(bdy.getText().toString().trim().length() <=0){

                    Constants.alertDialog(LoginActivity.this,"Login","Birthday can not be Empty.\nPlease Add Birthday");
                }
                else if(mobno.getText().toString().trim().length() <10){
                    Constants.alertDialog(LoginActivity.this,"Login","Mobile Number can not be Empty.\nPlease Add Mobile Number");
                }
                else {
                    loading.setVisibility(View.VISIBLE);

                    String dietType = "Veg";
                    String gender = "Female";
                    if(nonveg.isChecked()){
                       dietType = "NonVeg";
                    }
                    else if(both.isChecked()) {
                        dietType = "Both";
                    }

                    if(male.isChecked()){
                        gender = "Male";
                    }
                    User newUser = new User();
                    newUser.setUserName(name);
                    newUser.setEmailId(email);
                    newUser.setUserId(UUID.randomUUID().toString());
                    newUser.setUserLoginID(email);
                    newUser.setPassword("test1");
                    newUser.setActive(true);
                    newUser.setFirstLogin(true);
                    newUser.setUserType("Customer");
                    newUser.setMobileNo(mobno.getText().toString().trim());
                    newUser.setUserDietType(dietType);
                    newUser.setBirthday(bdy.getText().toString());
                    newUser.setGender(gender);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putString("UserName",newUser.getUserName());
                    edit.putString("MobNo", newUser.getMobileNo());
                    edit.putString("DietType",newUser.getUserDietType());
                    edit.putString("Email",newUser.getEmailId());
                    edit.putString("IsLogin","true");
                    edit.apply();
                    writeNewPost(newUser);
                    alertDialog.dismiss();
                }

            }
        });


        alertDialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            email = result.getSignInAccount().getEmail();
            name = result.getSignInAccount().getDisplayName();
            fromWhere = "google";
            if(result.isSuccess()) {
                final GoogleApiClient client = mGoogleApiClient;
               /* Auth.GoogleSignInApi.signOut(client).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                // ...
                            }
                        });*/
                userLogin();
                //handleSignInResult(...)
            } else {
                //handleSignInResult(...);
            }
        } else {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);

            // Handle other values for requestCode
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
