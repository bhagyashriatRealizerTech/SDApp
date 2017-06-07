package com.realizer.sallado;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realizer.sallado.utils.Constants;

public class MonthlyDietActivity extends AppCompatActivity {

    LinearLayout ayurved,diet;
    TextView bookPkg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthly_diet_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Constants.actionBarTitle("Diet Programs", this));
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

        ayurved = (LinearLayout) findViewById(R.id.layout_ayurved);
        diet = (LinearLayout) findViewById(R.id.layout_dietician);
        bookPkg = (TextView) findViewById(R.id.book_pkg);

        ayurved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonthlyDietActivity.this, DoctorlistActivity.class);
                intent.putExtra("DoctorType","Ayurvedic");
                startActivity(intent);
            }
        });

        diet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonthlyDietActivity.this, DoctorlistActivity.class);
                intent.putExtra("DoctorType","Dietitian");
                startActivity(intent);
            }
        });

        bookPkg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonthlyDietActivity.this, DietPlanListActivity.class);
                startActivity(intent);
            }
        });



    }
}
