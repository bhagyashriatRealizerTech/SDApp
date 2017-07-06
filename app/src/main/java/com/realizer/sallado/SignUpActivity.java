package com.realizer.sallado;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.realizer.sallado.databasemodel.User;
import com.realizer.sallado.utils.Constants;
import com.realizer.sallado.utils.FontManager;
import com.realizer.sallado.utils.Singleton;
import com.realizer.sallado.view.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
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
    FirebaseStorage storage;
    StorageReference imageRef;
    ImageView profPic;
    boolean isPic = false;
    private static Bitmap convertedImage;
    Uri picUri;
    final int CROP_PIC = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        if(Singleton.getDatabase() == null)
            Singleton.setDatabase(FirebaseDatabase.getInstance());

        database = Singleton.getDatabase();
        storage = FirebaseStorage.getInstance();
        imageRef = storage.getReference("UserProfileImage");
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
        profPic = (ImageView) findViewById(R.id.img_user);
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

                if(isPic)
                uploadImage(profPic);
                else
                    addData("");
            }
        });

        profPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    public void addData(String profUrl){
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
            newUser.setThumbnailUrl(profUrl);

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
    private void writeNewPost(User newUser) {
        DatabaseReference ref = myRef.push();
        ref.setValue(newUser);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("UserID",ref.getKey());
        edit.putBoolean("IsSkip",false);
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
                Log.d("Test",exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("URI",downloadUrl.toString());
                addData(downloadUrl.toString());
            }
        });
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

    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    try {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
                        File imageFile = new File(imageFilePath);
                        picUri = Uri.fromFile(imageFile); // convert path to Uri
                        takePictureIntent.putExtra( MediaStore.EXTRA_OUTPUT,  picUri );
                        startActivityForResult(takePictureIntent, 1);
                    } catch(ActivityNotFoundException anfe){
                        //display an error message
                        String errorMessage = "Whoops - your device doesn't support capturing images!";
                        // Toast.makeText(UserProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Constants.alertDialog(SignUpActivity.this, "Error",errorMessage+".");
                    }
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 2);//one can be replaced with any action code
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            String path="";
            if (requestCode == 2) {
                picUri = data.getData();
                try
                {
                    convertedImage = MediaStore.Images.Media.getBitmap(getContentResolver() , picUri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    convertedImage.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                }
                catch (Exception e)
                {
                    //handle exception
                }
                performCrop();

            }
            else if (requestCode == 1) {
                //get the Uri for the captured image
                try
                {
                    convertedImage = MediaStore.Images.Media.getBitmap(getContentResolver() , picUri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    convertedImage.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                }
                catch (Exception e)
                {
                    //handle exception
                }
                //carry out the crop operation
                performCrop();
            }
            else if (requestCode == CROP_PIC) {
                Bundle extras = data.getExtras();
                if(extras != null ) {
                    Bitmap photo = extras.getParcelable("data");
                    profPic.setImageBitmap(photo);
                }
                else
                {

                    profPic.setImageBitmap(convertedImage);
                }
                isPic = true;
                uploadImage(profPic);
            }
        } else if (resultCode == RESULT_CANCELED) {

            // user cancelled Image capture
            //Toast.makeText(getApplicationContext(),"User cancelled image capture", Toast.LENGTH_SHORT).show();
            if (requestCode != CROP_PIC) {
                Constants.alertDialog(this, "Image Cancelled", "User cancelled image capture.");
            }

        } else {
            // failed to capture image
            //Toast.makeText(getApplicationContext(),"Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            Constants.alertDialog(this, "Image Error","Sorry! Failed to capture image.");
        }

    }

    /**
     * this function does the crop operation.
     */
    private void performCrop(){

        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 0);
            cropIntent.putExtra("aspectY", 0);
            //indicate output X and Y
            cropIntent.putExtra("outputX",512);
            cropIntent.putExtra("outputY", 512);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!.Setting original image to profile.";
//            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
//            toast.show();
            Constants.alertDialog(SignUpActivity.this, "Error",errorMessage+".");
            profPic.setImageBitmap(convertedImage);
            isPic = true;
            uploadImage(profPic);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            profPic.setImageBitmap(convertedImage);
            isPic = true;
            uploadImage(profPic);
        }
    }


}
