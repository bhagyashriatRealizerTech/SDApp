package com.realizer.sallado;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.realizer.sallado.adapter.DietPlanCalenderListAdapter;
import com.realizer.sallado.databasemodel.DietProgram;
import com.realizer.sallado.databasemodel.Dish;
import com.realizer.sallado.databasemodel.User;
import com.realizer.sallado.databasemodel.UserDietProgram;
import com.realizer.sallado.model.DietPlanCalenderModel;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.FontManager;
import com.realizer.sallado.utils.ImageStorage;
import com.realizer.sallado.utils.Singleton;
import com.realizer.sallado.view.ExpandableHeightGridView;
import com.realizer.sallado.view.ProgressWheel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DietPlanDetailActivity extends AppCompatActivity {

    ExpandableHeightGridView dietgrid;
    List<DietPlanCalenderModel> dietPlanCalenderModels;
    TextView name,detail,dayChange,prev,next;
    TextView breakfast,lunch,dinner,snacks;
    ImageView imgBreakfast,imgLunch,imgDinner,imgSnacks;
    LinearLayout layoutB,layoutL,layoutS,layoutD;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    FirebaseDatabase database;
    DatabaseReference dishRef;
    DatabaseReference userDietProgramRef;
    ScrollView scrollView;
    DietProgram dietProgram;
    int counter = 1;
    LayoutInflater inflater;
    ProgressWheel loading;
    Button book_plan;
    String userId;
    ActionBar actionBar;
    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Singleton.getDatabase() == null)
            Singleton.setDatabase(FirebaseDatabase.getInstance());

        database = Singleton.getDatabase();
        dishRef = database.getReference("Dish");
        DatabaseReference programRef = database.getReference("StandardDietProgram");
        userDietProgramRef = database.getReference("UserDietProgram");

        setContentView(R.layout.diet_program_detail_activity);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Constants.actionBarTitle("Diet Program", this));
        initiateView();

        String programId = getIntent().getStringExtra("ProgramId");
        userId = PreferenceManager.getDefaultSharedPreferences(DietPlanDetailActivity.this).getString("UserID","");

        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        loading.setVisibility(View.VISIBLE);

        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLable();
            }

        };

        Query query = programRef.orderByChild("programId").equalTo(programId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                        dietProgram = snapshot.getValue(DietProgram.class);
                        for(int i=0;i<dietProgram.getDayProgram().size();i++){

                            View view = inflater.inflate(R.layout.diet_plan_fliper_layout, null);

                            breakfast = (TextView) view.findViewById(R.id.txt_breakfast);
                            lunch = (TextView) view.findViewById(R.id.txt_lunch);
                            dinner = (TextView) view.findViewById(R.id.txt_dinner);
                            snacks = (TextView) view.findViewById(R.id.txt_snacks);

                            imgBreakfast = (ImageView) view.findViewById(R.id.img_breakfast);
                            imgLunch = (ImageView) view.findViewById(R.id.img_lunch);
                            imgSnacks = (ImageView) view.findViewById(R.id.img_snacks);
                            imgDinner = (ImageView) view.findViewById(R.id.img_dinner);

                            layoutB = (LinearLayout) view.findViewById(R.id.layout_breakfast);
                            layoutL = (LinearLayout) view.findViewById(R.id.layout_lunch);
                            layoutS = (LinearLayout) view.findViewById(R.id.layout_snacks);
                            layoutD = (LinearLayout) view.findViewById(R.id.layout_dinner);

                            setValue(view,i);

                        }

                        name.setText(dietProgram.getProgramName());
                        name.setVisibility(View.GONE);
                        actionBar.setTitle(Constants.actionBarTitle(dietProgram.getProgramName(), DietPlanDetailActivity.this));
                        detail.setText(dietProgram.getProgramDescription());
                        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
                        mGestureDetector = new GestureDetector(DietPlanDetailActivity.this, customGestureDetector);
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

        dietPlanCalenderModels = new ArrayList<>();


    }

    public void setValue(View view,int i){

        DietPlanCalenderModel dietPlanCalenderModel = new DietPlanCalenderModel();
        dietPlanCalenderModel.setDay(dietProgram.getDayProgram().get(i).getDay());
        String bDishId = dietProgram.getDayProgram().get(i).getBreakfastDishID();
        String lDishId = dietProgram.getDayProgram().get(i).getLunchDishID();
        String sDishId = dietProgram.getDayProgram().get(i).getSnacksDishId();
        String dDishId = dietProgram.getDayProgram().get(i).getDinnerDishId();

        if(dietProgram.isBreakfastInclude()){
            layoutB.setVisibility(View.VISIBLE);
            Query query = dishRef.orderByChild("dishId").equalTo(bDishId);
            setData(query,"Breakfast",breakfast,imgBreakfast);

        }
        else {
            layoutB.setVisibility(View.GONE);
        }

        if(dietProgram.isLunchInclude()){
            layoutL.setVisibility(View.VISIBLE);
            Query query = dishRef.orderByChild("dishId").equalTo(lDishId);
            setData(query,"Lunch",lunch,imgLunch);

        }
        else {
            layoutL.setVisibility(View.GONE);
        }


        if(dietProgram.isSnacksInclude()){
            layoutS.setVisibility(View.VISIBLE);
            Query query = dishRef.orderByChild("dishId").equalTo(sDishId);
            setData(query,"Snacks",snacks,imgSnacks);

        }
        else {
            layoutS.setVisibility(View.GONE);
        }

        if(dietProgram.isDinnerInclude()){
            layoutD.setVisibility(View.VISIBLE);
            Query query = dishRef.orderByChild("dishId").equalTo(dDishId);
            setData(query,"Dinner",dinner,imgDinner);

        }
        else {
            layoutD.setVisibility(View.GONE);
        }

        dietPlanCalenderModels.add(dietPlanCalenderModel);
        mViewFlipper.addView(view);

    }

    public void setData(Query query, final String type, final TextView textView, final ImageView imageView){

        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        int end = 0;
                        Dish dish = snapshot.getValue(Dish.class);
                        String bText = type+": "+dish.getDishName
                                ()+"\n"+dish.getDishContent();
                        end = bText.split(":")[0].length();

                        final SpannableStringBuilder sb = new SpannableStringBuilder(bText);
                        sb.setSpan(bss, 0, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        textView.setText(sb);

                        if(!dish.getDishThumbnail().isEmpty()){
                            ImageStorage.setThumbnail(imageView,dish.getDishThumbnail());
                        }

                    }

                    //loading.setVisibility(View.GONE);

                }
                //loading.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // loading.setVisibility(View.GONE);
            }
        });

    }

    public void initiateView(){

        /*dietgrid = (ExpandableHeightGridView) findViewById(R.id.grid_calender);
        dietgrid.setExpanded(true);
        name = (TextView) findViewById(R.id.txt_planName);
        detail = (TextView) findViewById(R.id.txt_plan_desc);

        scrollView = (ScrollView) findViewById(R.id.scroll);
        dayChange = (TextView) findViewById(R.id.txt_daychange);

        dietgrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                dayChange.setText("Day "+(position+1));
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                      //  scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

            }
        });*/

        name = (TextView) findViewById(R.id.txt_planName);
        detail = (TextView) findViewById(R.id.txt_plan_desc);
        dayChange = (TextView) findViewById(R.id.txt_daychange);
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        prev = (TextView) findViewById(R.id.txtprev);
        next = (TextView) findViewById(R.id.txtnext);
        loading =(ProgressWheel) findViewById(R.id.loading);
        book_plan = (Button) findViewById(R.id.btn_bookplan);


        // Set in/out flipping animations
        mViewFlipper.setInAnimation(DietPlanDetailActivity.this, R.anim.left_in);
        mViewFlipper.setOutAnimation(DietPlanDetailActivity.this, R.anim.left_out);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(counter == 1) {
                    counter = (dietPlanCalenderModels.size());
                }
                else {
                    counter = counter - 1;
                }

                dayChange.setText("Day " + counter);

                mViewFlipper.setInAnimation(DietPlanDetailActivity.this, R.anim.right_in);
                mViewFlipper.setOutAnimation(DietPlanDetailActivity.this, R.anim.right_out);
                mViewFlipper.showPrevious();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(counter == dietPlanCalenderModels.size()) {
                    counter = 1;
                }
                else {
                    counter = counter + 1;
                }

                dayChange.setText("Day " + counter);

                mViewFlipper.setInAnimation(DietPlanDetailActivity.this, R.anim.left_in);
                mViewFlipper.setOutAnimation(DietPlanDetailActivity.this, R.anim.left_out);
                mViewFlipper.showNext();
            }
        });

        book_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(DietPlanDetailActivity.this);
                if(preferences.getBoolean("IsSkip",false))
                {
                    alertDialogLogin(DietPlanDetailActivity.this,"Login","Please Login to book this Plan");
                }
                else {
                    if(Constants.isConnectingToInternet(DietPlanDetailActivity.this))
                    planClick();
                    else
                        Constants.alertDialog(DietPlanDetailActivity.this,"Network Error","Your device not connected to internet");
                }

            }
        });
    }


    public  void alertDialogLogin(final Context context, String title, String message) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialoglayout = inflater.inflate(R.layout.custom_dialogbox, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialoglayout);


        Button buttonok= (Button) dialoglayout.findViewById(R.id.alert_btn_ok);
        TextView titleName=(TextView) dialoglayout.findViewById(R.id.alert_dialog_title);
        TextView alertMsg=(TextView) dialoglayout.findViewById(R.id.alert_dialog_message);
        TextView close=(TextView) dialoglayout.findViewById(R.id.txt_close);
        close.setTypeface(FontManager.getTypeface(context, FontManager.FONTAWESOME));
        buttonok.setText("Login");
        close.setVisibility(View.GONE);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);


        buttonok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DietPlanDetailActivity.this, LoginActivity.class);
                startActivity(intent);
                alertDialog.dismiss();
                finishAffinity();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        titleName.setText(title);
        alertMsg.setText(message);

        alertDialog.show();

    }

    public final void planClick( ) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(DietPlanDetailActivity.this, date, year, month, day);
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, day+1);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    public void updateLable(){

        Date d = myCalendar.getTime();
        SimpleDateFormat simpleDateFormat  = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        String startDate = simpleDateFormat.format(calendar.getTime());
        calendar.add(Calendar.DATE,dietProgram.getProgramDays());
        String endDate = simpleDateFormat.format(calendar.getTime());

        UserDietProgram userDietProgram = new UserDietProgram();
        userDietProgram.setUserId(userId);
        userDietProgram.setDietProgram(dietProgram);
        userDietProgram.setStartDate(startDate);
        userDietProgram.setEndDate(endDate);
        userDietProgram.setDeliveryPoint("Hadapsar,Pune");

        DatabaseReference ref = userDietProgramRef.push();
        ref.setValue(userDietProgram);

        Constants.alertDialog(DietPlanDetailActivity.this, "Book Plan", "You booked Plan Successfully.\nEnjoy the Healthy and Tasty Food");
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
    }
    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Swipe left (next)
            if (e1.getX() > e2.getX()) {
                mViewFlipper.setInAnimation(DietPlanDetailActivity.this, R.anim.left_in);
                mViewFlipper.setOutAnimation(DietPlanDetailActivity.this, R.anim.left_out);
                mViewFlipper.showNext();
            }

            // Swipe right (previous)
            if (e1.getX() < e2.getX()) {
                mViewFlipper.setInAnimation(DietPlanDetailActivity.this, R.anim.right_in);
                mViewFlipper.setOutAnimation(DietPlanDetailActivity.this, R.anim.right_out);
                mViewFlipper.showPrevious();
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
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
