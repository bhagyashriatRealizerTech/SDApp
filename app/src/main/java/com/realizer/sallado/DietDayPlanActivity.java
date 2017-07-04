package com.realizer.sallado;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realizer.sallado.databasemodel.DayProgram;
import com.realizer.sallado.databasemodel.Dish;
import com.realizer.sallado.databasemodel.DishGroup;
import com.realizer.sallado.model.DietPlanCalenderModel;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.FontManager;
import com.realizer.sallado.utils.ImageStorage;
import com.realizer.sallado.utils.Singleton;
import com.realizer.sallado.view.ProgressWheel;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Win on 14-06-2017.
 */

public class DietDayPlanActivity extends AppCompatActivity {

    public ImageView imgB,imgL,imgS,imgD;
    public TextView  txtB,txtL,txtS,txtD;
    public TextView  txtTrackB,txtTrackL,txtTrackS,txtTrackD;
    public TextView  txtBdesc,txtLdesc,txtSdesc,txtDdesc;
    public TextView txtCb,txtCl,txtCs,txtCd,txtClegend;
    LinearLayout layoutB,layoutL,layoutS,layoutD;
    DayProgram dayProgram;
    DatabaseReference dishRef,groupRef;
    FirebaseDatabase database;

    ProgressWheel loading;
    Date currentdate,dayDate;
    SimpleDateFormat df,df1;
    String day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diet_day_plan_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        dayProgram = (DayProgram) getIntent().getSerializableExtra("DayPlan");
        actionBar.setTitle(Constants.actionBarTitle(dayProgram.getTitle(), DietDayPlanActivity.this));
        if(Singleton.getDatabase() == null)
            Singleton.setDatabase(FirebaseDatabase.getInstance());

        database = Singleton.getDatabase();
        dishRef = database.getReference("Dish");
        groupRef = database.getReference("DishGroup");
        groupRef.keepSynced(true);
        day = dayProgram.getDay();
        initiateView();
        loading.setVisibility(View.VISIBLE);
        setValue();
        setChangeButton();
    }

    public void setChangeData(final String groupId,final int type){
        loading.setVisibility(View.VISIBLE);
        Query query = groupRef.orderByChild("groupID").equalTo(groupId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DishGroup dishGroup = new DishGroup();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        dishGroup = snapshot.getValue(DishGroup.class);

                    }
                    loading.setVisibility(View.GONE);
                    Intent intent = new Intent(DietDayPlanActivity.this, DietChangePlanActivity.class);
                    intent.putExtra("ChangeType",type);
                    intent.putExtra("Day",dayProgram.getDay());
                    intent.putExtra("ProgramId",getIntent().getStringExtra("ProgramId"));
                    intent.putExtra("DishGroup", dishGroup);
                    startActivity(intent);
                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
            }
        });

    }

    public void setChangeButton(){
        df = new SimpleDateFormat("dd/MM/yyyy");

        df1 = new SimpleDateFormat("HH:mm");
        dayDate = null;
        currentdate = null;


        try {
            dayDate = df.parse(getIntent().getStringExtra("DayDate"));
            String temp = df.format(new Date());
            currentdate = df.parse(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(dayDate != null){
            if(currentdate.equals(dayDate)){
                setChangeButtonVisibility("07:00",txtCb,txtTrackB);
                setChangeButtonVisibility("11:00",txtCl,txtTrackL);
                setChangeButtonVisibility("14:00",txtCs,txtTrackS);
                setChangeButtonVisibility("17:00",txtCd,txtTrackS);
            }
            else  if(currentdate.before(dayDate)){
                txtCb.setVisibility(View.VISIBLE);
                txtCl.setVisibility(View.VISIBLE);
                txtCs.setVisibility(View.VISIBLE);
                txtCd.setVisibility(View.VISIBLE);

                txtTrackB.setVisibility(View.VISIBLE);
                txtTrackL.setVisibility(View.VISIBLE);
                txtTrackS.setVisibility(View.VISIBLE);
                txtTrackD.setVisibility(View.VISIBLE);
            }
            else {
                txtCb.setVisibility(View.GONE);
                txtCl.setVisibility(View.GONE);
                txtCs.setVisibility(View.GONE);
                txtCd.setVisibility(View.GONE);

                txtTrackB.setVisibility(View.GONE);
                txtTrackL.setVisibility(View.GONE);
                txtTrackS.setVisibility(View.GONE);
                txtTrackD.setVisibility(View.GONE);
            }
        }

    }

    public void setChangeButtonVisibility(String time,TextView textView,TextView textView1){
        try {
            dayDate = df1.parse(time);
            String temp = df1.format(new Date());
            currentdate = df1.parse(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(currentdate.equals(dayDate) || currentdate.before(dayDate)){
            textView.setVisibility(View.VISIBLE);
            textView1.setVisibility(View.VISIBLE);
        }
        else {
            textView.setVisibility(View.GONE);
            textView1.setVisibility(View.GONE);
        }
    }

    public void initiateView(){

        imgB = (ImageView) findViewById(R.id.img_breakfast);
        imgL = (ImageView) findViewById(R.id.img_lunch);
        imgS = (ImageView) findViewById(R.id.img_snacks);
        imgD = (ImageView) findViewById(R.id.img_dinner);

        txtB = (TextView) findViewById(R.id.txt_breakfast);
        txtL = (TextView) findViewById(R.id.txt_lunch);
        txtS = (TextView) findViewById(R.id.txt_snacks);
        txtD = (TextView) findViewById(R.id.txt_dinner);

        txtBdesc = (TextView) findViewById(R.id.txt_breakfastDesc);
        txtLdesc = (TextView) findViewById(R.id.txt_lunchDesc);
        txtSdesc = (TextView) findViewById(R.id.txt_snacksDesc);
        txtDdesc = (TextView) findViewById(R.id.txt_dinnerDesc);

        txtCb = (TextView) findViewById(R.id.txt_changeB);
        txtCl = (TextView) findViewById(R.id.txt_changeL);
        txtCs = (TextView) findViewById(R.id.txt_changeS);
        txtCd = (TextView) findViewById(R.id.txt_changeD);

        txtTrackB = (TextView) findViewById(R.id.txt_trackB);
        txtTrackL = (TextView) findViewById(R.id.txt_trackL);
        txtTrackS = (TextView) findViewById(R.id.txt_trackS);
        txtTrackD = (TextView) findViewById(R.id.txt_trackD);

        txtClegend = (TextView) findViewById(R.id.txt_change_legend);

        layoutB = (LinearLayout) findViewById(R.id.layout_breakfast);
        layoutL = (LinearLayout) findViewById(R.id.layout_lunch);
        layoutS = (LinearLayout) findViewById(R.id.layout_snacks);
        layoutD = (LinearLayout) findViewById(R.id.layout_dinner);

        loading =(ProgressWheel) findViewById(R.id.loading);

        txtCl.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));
        txtCb.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));
        txtCd.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));
        txtCs.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));
        txtClegend.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));

        txtTrackB.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));
        txtTrackL.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));
        txtTrackS.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));
        txtTrackD.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));


        txtCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 setChangeData(dayProgram.getBreakfastGroupID(),0);
            }
        });

        txtCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChangeData(dayProgram.getLunchGroupID(),1);
            }
        });

        txtCs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setChangeData(dayProgram.getSnacksGroupID(),2);
            }
        });

        txtCd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 setChangeData(dayProgram.getDinnerGroupID(),3);
            }
        });


        txtTrackB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               trackOrder("B",getIntent().getStringExtra("DayDate"));
            }
        });

        txtTrackL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackOrder("L",getIntent().getStringExtra("DayDate"));
            }
        });

        txtTrackS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackOrder("S",getIntent().getStringExtra("DayDate"));
            }
        });

        txtTrackD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackOrder("D",getIntent().getStringExtra("DayDate"));
            }
        });
    }

    public void trackOrder(String type,String date){
        df = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
        String outDate = null;
        try {
           Date tempDate = df.parse(date);
            outDate = simpleDateFormat.format(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(outDate != null) {
            Intent intent = new Intent(DietDayPlanActivity.this, TrackMyOrder.class);
            intent.putExtra("DishType", type);
            intent.putExtra("DishDate", outDate);
            startActivity(intent);
        }
    }

    public void setValue(){

        DietPlanCalenderModel dietPlanCalenderModel = new DietPlanCalenderModel();
        dietPlanCalenderModel.setDay(dayProgram.getDay());
        String bDishId = dayProgram.getBreakfastDishID();
        String lDishId = dayProgram.getLunchDishID();
        String sDishId = dayProgram.getSnacksDishId();
        String dDishId = dayProgram.getDinnerDishId();

        if(getIntent().getBooleanExtra("IsB",true)){
            layoutB.setVisibility(View.VISIBLE);
            Query query = dishRef.orderByChild("dishId").equalTo(bDishId);
            setData(query,"Breakfast",txtB,txtBdesc,imgB);

        }
        else {
            layoutB.setVisibility(View.GONE);
        }

        if(getIntent().getBooleanExtra("IsL",true)){
            layoutL.setVisibility(View.VISIBLE);
            Query query = dishRef.orderByChild("dishId").equalTo(lDishId);
            setData(query,"Lunch",txtL,txtLdesc,imgL);

        }
        else {
            layoutL.setVisibility(View.GONE);
        }


        if(getIntent().getBooleanExtra("IsS",true)){
            layoutS.setVisibility(View.VISIBLE);
            Query query = dishRef.orderByChild("dishId").equalTo(sDishId);
            setData(query,"Snacks",txtS,txtSdesc,imgS);

        }
        else {
            layoutS.setVisibility(View.GONE);
        }

        if(getIntent().getBooleanExtra("IsD",true)){
            layoutD.setVisibility(View.VISIBLE);
            Query query = dishRef.orderByChild("dishId").equalTo(dDishId);
            setData(query,"Dinner",txtD,txtDdesc,imgD);

        }
        else {
            layoutD.setVisibility(View.GONE);
        }


    }

    public void setData(Query query, final String type, final TextView textView, final TextView textViewDesc,final ImageView imageView){

        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        int end = 0;
                        Dish dish = snapshot.getValue(Dish.class);
                        String bText = type+": "+dish.getDishName();
                        end = bText.split(":")[0].length();

                        final SpannableStringBuilder sb = new SpannableStringBuilder(bText);
                        sb.setSpan(bss, 0, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        textView.setText(sb);

                        textViewDesc.setText("Dish Ingredients: "+dish.getDishIngredients()+"\nDish Contents: "+dish.getDishContent());

                        if(!dish.getDishThumbnail().isEmpty()){
                            ImageStorage.setThumbnail(imageView,dish.getDishThumbnail());
                        }

                    }

                    loading.setVisibility(View.GONE);

                }
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                 loading.setVisibility(View.GONE);
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
        if(Singleton.getInstance().isChange()){
            Singleton.getInstance().setIsChange(false);
            loading.setVisibility(View.VISIBLE);
            dayProgram = Singleton.getInstance().getUserDietProgram().getDietProgram().getDayProgram().get((Integer.valueOf(day) - 1));
            setValue();
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

