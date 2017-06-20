package com.realizer.sallado;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.realizer.sallado.databasemodel.User;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.Singleton;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    LinearLayout monthlydiet,crasheaters,daaruchakna,myDiet;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseStorage storage;
    StorageReference imageRef;

    int[] resources = {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Singleton.getDatabase() == null)
            Singleton.setDatabase(FirebaseDatabase.getInstance());

        database = Singleton.getDatabase();
        myRef = database.getReference("User");
        storage = FirebaseStorage.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Constants.actionBarTitle("Sallado", this));
        setContentView(R.layout.dashboard_activity);
        initiateView();

        // Add all the images to the ViewFlipper
        for (int i = 0; i < resources.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(resources[i]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
             mViewFlipper.addView(imageView);
            //imageRef = storage.getReference().child(Constants.ACTIVE_DASHBOARd_IMAGE+"image"+i+".png");
           // uploadImage(imageView);
        }

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);

       // mViewFlipper.setAutoStart(true);
        //mViewFlipper.setFlipInterval(5000); // flip every 2 seconds (2000ms)

    }

    public void uploadImage(ImageView imageView){

        Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("URI",downloadUrl.toString());
            }
        });
    }

    public void initiateView(){

        monthlydiet = (LinearLayout) findViewById(R.id.layout_monthly);
        crasheaters = (LinearLayout) findViewById(R.id.layout_crasheater);
        daaruchakna = (LinearLayout) findViewById(R.id.layout_chakana);
        myDiet = (LinearLayout) findViewById(R.id.layout_myplan);
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        // Set in/out flipping animations
        mViewFlipper.setInAnimation(MainActivity.this, R.anim.left_in);
        mViewFlipper.setOutAnimation(MainActivity.this, R.anim.left_out);
        mViewFlipper.setAutoStart(true);
        mViewFlipper.setFlipInterval(5000);

        monthlydiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MonthlyDietActivity.class);
                startActivity(intent);
            }
        });

        crasheaters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DietMenuListActivity.class);
                startActivity(intent);
            }
        });

        daaruchakna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DietMenuListActivity.class);
                startActivity(intent);
            }
        });

        myDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DietPlanListActivity.class);
                intent.putExtra("From","MyDiet");
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_setting, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.user_profile:
                Intent intent=new Intent(MainActivity.this, MyAccountActivity.class);
                startActivity(intent);
                return true;

            case R.id.user_logout:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString("UserName","");
                edit.putString("DietType","");
                edit.putString("Email","");
                edit.putString("UserID","");
                edit.putString("IsLogin","false");
                edit.commit();
                Intent intent1=new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
                return true;

            case R.id.about_us:
                Intent intent2 = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent2);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Swipe left (next)
            if (e1.getX() > e2.getX()) {
                mViewFlipper.setInAnimation(MainActivity.this, R.anim.left_in);
                mViewFlipper.setOutAnimation(MainActivity.this, R.anim.left_out);
                mViewFlipper.showNext();
            }

            // Swipe right (previous)
            if (e1.getX() < e2.getX()) {
                mViewFlipper.setInAnimation(MainActivity.this, R.anim.right_in);
                mViewFlipper.setOutAnimation(MainActivity.this, R.anim.right_out);
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
}

