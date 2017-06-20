package com.realizer.sallado;

import android.content.Intent;
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
import android.widget.TextView;

import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.FontManager;

public class MyAccountActivity extends AppCompatActivity {

    EditText userName,userEmail,userMobile,userAddress;
    TextView myPlan,myOrder,edit;
    ImageView userImage;
    LinearLayout info;
    SharedPreferences preferences;
    Button done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Constants.actionBarTitle("My Account", this));
        preferences  = PreferenceManager.getDefaultSharedPreferences(MyAccountActivity.this);
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
        userName.setText(preferences.getString("UserName",""));
        userEmail.setText(preferences.getString("Email",""));
        userMobile.setText(preferences.getString("MobNo",""));
        userAddress.setText(preferences.getString("Address","A-601, Mega Center\nHadapsar, Pune\n411028"));
        myPlan.setText("My Monthly Plans");
        myOrder.setText("My Orders");
    }

    public void initiateView(){

        userName = (EditText) findViewById(R.id.txt_username);
        userEmail = (EditText) findViewById(R.id.txt_useremail);
        userMobile = (EditText) findViewById(R.id.txt_usermobile);
        userAddress = (EditText) findViewById(R.id.txt_adrs);
        myPlan = (TextView) findViewById(R.id.txt_myplan);
        myOrder = (TextView) findViewById(R.id.txt_myorder);
        edit = (TextView) findViewById(R.id.txt_edit);
        done = (Button) findViewById(R.id.btn_done);
        userImage = (ImageView) findViewById(R.id.img_user);
        info = (LinearLayout) findViewById(R.id.layout_info);

        edit.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));

        enableDisable(false);

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

        if(value){
            userName.setBackground(getResources().getDrawable(R.drawable.dashboard_icon_background));
            userEmail.setBackground(getResources().getDrawable(R.drawable.dashboard_icon_background));
            userMobile.setBackground(getResources().getDrawable(R.drawable.dashboard_icon_background));
            userAddress.setBackground(getResources().getDrawable(R.drawable.dashboard_icon_background));
            done.setVisibility(View.VISIBLE);
            info.setBackgroundColor(Color.TRANSPARENT);
        }
        else {
            userName.setTextColor(Color.BLACK);
            userEmail.setTextColor(Color.BLACK);
            userMobile.setTextColor(Color.BLACK);
            userAddress.setTextColor(Color.BLACK);
            userName.setBackgroundColor(Color.TRANSPARENT);
            userEmail.setBackgroundColor(Color.TRANSPARENT);
            userMobile.setBackgroundColor(Color.TRANSPARENT);
            userAddress.setBackgroundColor(Color.TRANSPARENT);
            done.setVisibility(View.GONE);
            info.setBackground(getResources().getDrawable(R.drawable.dashboard_icon_background));
        }
    }
}
