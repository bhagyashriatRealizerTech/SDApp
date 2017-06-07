package com.realizer.sallado;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realizer.sallado.utils.Constants;

public class CrashEatersActivity extends AppCompatActivity {

    LinearLayout breakfast,lunch,dinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_eaters_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Constants.actionBarTitle("Crash Eaters", this));
        initiateView();
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

        breakfast = (LinearLayout) findViewById(R.id.layout_breakfast);
        lunch = (LinearLayout) findViewById(R.id.layout_lunch);
        dinner = (LinearLayout) findViewById(R.id.layout_dinner);

        breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CrashEatersActivity.this, DietMenuListActivity.class);
                startActivity(intent);
            }
        });

        lunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CrashEatersActivity.this, DietMenuListActivity.class);
                startActivity(intent);
            }
        });

        dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CrashEatersActivity.this, DietMenuListActivity.class);
                startActivity(intent);
            }
        });



    }
}
