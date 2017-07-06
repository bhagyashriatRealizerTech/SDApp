package com.realizer.sallado;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.realizer.sallado.DietMenuListActivity;
import com.realizer.sallado.R;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.FontManager;

public class AboutActivity extends AppCompatActivity {

    TextView title,subtitle,copyright;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Constants.actionBarTitle("About", this));
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

        title = (TextView) findViewById(R.id.txt_title);
        subtitle = (TextView) findViewById(R.id.txt_sub_title);
        copyright = (TextView) findViewById(R.id.txt_copyright);

        Typeface face= Typeface.createFromAsset(getAssets(), "fonts/font.ttf");
        title.setTypeface(face);
        subtitle.setTypeface(face);
        title.setText("Sallado");
        subtitle.setText("A healthy food, for a wealthy mood.");
    }
}
