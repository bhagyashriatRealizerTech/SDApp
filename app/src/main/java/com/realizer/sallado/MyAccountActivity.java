package com.realizer.sallado;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

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
import com.realizer.sallado.utils.FontManager;
import com.realizer.sallado.utils.ImageStorage;
import com.realizer.sallado.utils.Singleton;
import com.realizer.sallado.view.ProgressWheel;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MyAccountActivity extends AppCompatActivity {

    EditText userName,userEmail,userMobile,userAddress;
    TextView edit,bdy;
    RadioButton male,female,veg,nonveg,both;
    ImageView userImage;
    LinearLayout info;
    SharedPreferences preferences;
    ProgressWheel loading;
    FirebaseDatabase database;
    DatabaseReference myRef;
    User user;
    String gender,dietType;
    MenuItem done;
    private static Bitmap convertedImage;
    Uri picUri;
    final int CROP_PIC = 3;
    FirebaseStorage storage;
    StorageReference imageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(Constants.actionBarTitle("My Account", this));
        preferences  = PreferenceManager.getDefaultSharedPreferences(MyAccountActivity.this);
        database = Singleton.getDatabase();
        myRef = database.getReference("User");
        myRef.keepSynced(true);

        storage = FirebaseStorage.getInstance();
        imageRef = storage.getReference("UserProfileImage");

        initiateView();
        setValue();

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }



    public void setValue(){
        final String[] imageUrl = {""};
        Query query = myRef.child(preferences.getString("UserID",""));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                        // do something with the individual "issues"
                        SharedPreferences.Editor edit = preferences.edit();
                         user  = dataSnapshot.getValue(User.class);
                        imageUrl[0] = user.getThumbnailUrl();
                        edit.putString("UserName",user.getUserName());
                        edit.putString("DietType",user.getUserDietType());
                        edit.putString("Email",user.getEmailId());
                        edit.putString("MobNo", user.getMobileNo());
                        edit.putString("UserID",dataSnapshot.getKey());
                        edit.putString("IsLogin","true");
                        edit.apply();

                        loading.setVisibility(View.GONE);

                        userName.setText(user.getUserName());
                        userEmail.setText(user.getEmailId());
                        userMobile.setText(user.getMobileNo());
                        bdy.setText(user.getBirthday());
                        userAddress.setText(preferences.getString("Address",""));
                    if(userAddress.getText().toString().length()<=0)
                        userAddress.setText("Address Not Available");
                    if(user.getUserDietType().equalsIgnoreCase("Veg"))
                        veg.setChecked(true);
                    else if(user.getUserDietType().equalsIgnoreCase("NonVeg"))
                        nonveg.setChecked(true);
                    else
                        both.setChecked(true);

                    if(user.getGender().equalsIgnoreCase("Female"))
                        female.setChecked(true);
                    else
                        male.setChecked(true);

                    if(!imageUrl[0].isEmpty()){
                        ImageStorage.setThumbnail(userImage,imageUrl[0]);
                    }

                    enableDisable(false);

                }
                else {
                    loading.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loading.setVisibility(View.GONE);
            }
        });

    }

    public void initiateView(){

        userName = (EditText) findViewById(R.id.txt_username);
        userEmail = (EditText) findViewById(R.id.txt_useremail);
        userMobile = (EditText) findViewById(R.id.txt_usermobile);
        userAddress = (EditText) findViewById(R.id.txt_adrs);
        edit = (TextView) findViewById(R.id.txt_edit);
        bdy = (TextView) findViewById(R.id.txt_birthday);
        userImage = (ImageView) findViewById(R.id.img_user);
        info = (LinearLayout) findViewById(R.id.layout_info);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        veg = (RadioButton) findViewById(R.id.veg);
        nonveg = (RadioButton) findViewById(R.id.nonveg);
        both = (RadioButton) findViewById(R.id.both);
        loading = (ProgressWheel) findViewById(R.id.loading);

        edit.setTypeface(FontManager.getTypeface(this, FontManager.FONTAWESOME));

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableDisable(true);
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

    }
    public void updateValue(){
        if(userName.getText().toString().trim().length()<=0){
            Constants.alertDialog(MyAccountActivity.this,"My Account","Name can not be Empty.\nPlease Enter Name");
        }
        else if(userEmail.getText().toString().trim().length()<=0){
            Constants.alertDialog(MyAccountActivity.this,"My Account","Email can not be Empty.\nPlease Enter Email");
        }
        else if(userMobile.getText().toString().trim().length()<=0){
            Constants.alertDialog(MyAccountActivity.this,"My Account","Mobile Number can not be Empty.\nPlease Enter Mobile Number");
        }
        else {
            loading.setVisibility(View.VISIBLE);
            User tempUser = new User();
            if(female.isChecked())
                gender = "female";
            else
            gender = "male";

            if(veg.isChecked())
                dietType = "Veg";
            else if(nonveg.isChecked())
                dietType = "NonVeg";
            else
                dietType = "Both";

            tempUser = user;
            if (!user.getUserName().equalsIgnoreCase(userName.getText().toString().trim())) {
                tempUser.setUserName(userName.getText().toString().trim());
            }
            if (!user.getEmailId().equalsIgnoreCase(userEmail.getText().toString().trim())) {
                tempUser.setEmailId(userEmail.getText().toString().trim());
            }
            if (!user.getMobileNo().equalsIgnoreCase(userMobile.getText().toString().trim())) {
                tempUser.setMobileNo(userMobile.getText().toString().trim());
            }
            if (!user.getGender().equalsIgnoreCase(gender)) {
                tempUser.setGender(gender);
            }
            if (!user.getUserDietType().equalsIgnoreCase(dietType)) {
                tempUser.setUserDietType(dietType);
            }
            myRef.child(preferences.getString("UserID", "")).setValue(tempUser);
            loading.setVisibility(View.GONE);
            enableDisable(false);
            Constants.alertDialog(MyAccountActivity.this, "My Account", "Your Profile Information Updated Successfully.\n");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        done = menu.findItem(R.id.action_done);
        done.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            case R.id.action_done:
                // app icon in action bar clicked; go home
                updateValue();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void enableDisable(boolean value){
        userName.setEnabled(value);
        userEmail.setEnabled(value);
        userMobile.setEnabled(value);
        userAddress.setEnabled(false);
        male.setEnabled(value);
        female.setEnabled(value);
        veg.setEnabled(value);
        nonveg.setEnabled(value);
        both.setEnabled(value);
        if(!value){
            if(male.isChecked())
                male.setEnabled(true);
            else
                female.setEnabled(true);

            if(veg.isChecked())
                veg.setEnabled(true);
            else if(nonveg.isChecked())
                nonveg.setEnabled(true);
            else
                both.setEnabled(true);
        }

        if(value){

            if(done != null)
                done.setVisible(true);
            userName.setBackgroundResource(R.drawable.dashboard_icon_background);
            userEmail.setBackgroundResource(R.drawable.dashboard_icon_background);
            userMobile.setBackgroundResource(R.drawable.dashboard_icon_background);
            userAddress.setBackgroundResource(R.drawable.dashboard_icon_background);
            bdy.setBackgroundResource(R.drawable.dashboard_icon_background);
            info.setBackgroundColor(Color.TRANSPARENT);
        }
        else {

            if(done != null)
                done.setVisible(false);
            userName.setTextColor(Color.BLACK);
            userEmail.setTextColor(Color.BLACK);
            userMobile.setTextColor(Color.BLACK);
            userAddress.setTextColor(Color.BLACK);
            male.setTextColor(Color.BLACK);
            female.setTextColor(Color.BLACK);
            veg.setTextColor(Color.BLACK);
            nonveg.setTextColor(Color.BLACK);
            both.setTextColor(Color.BLACK);
            bdy.setTextColor(Color.BLACK);

            userName.setBackgroundColor(Color.TRANSPARENT);
            userEmail.setBackgroundColor(Color.TRANSPARENT);
            userMobile.setBackgroundColor(Color.TRANSPARENT);
            userAddress.setBackgroundColor(Color.TRANSPARENT);
            bdy.setBackgroundColor(Color.TRANSPARENT);

            info.setBackgroundResource(R.drawable.dashboard_icon_background);
        }
    }


    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MyAccountActivity.this);
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
                        Constants.alertDialog(MyAccountActivity.this, "Error",errorMessage+".");
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
                    userImage.setImageBitmap(photo);
                }
                else
                {

                    userImage.setImageBitmap(convertedImage);
                }

                uploadImage(userImage);
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
                user.setThumbnailUrl(downloadUrl.toString());
                myRef.child(preferences.getString("UserID", "")).setValue(user);
                loading.setVisibility(View.GONE);
                Constants.alertDialog(MyAccountActivity.this, "My Account", "Your Profile Picture Updated Successfully.\n");
            }
        });
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
            Constants.alertDialog(MyAccountActivity.this, "Error",errorMessage+".");
            userImage.setImageBitmap(convertedImage);
            uploadImage(userImage);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            userImage.setImageBitmap(convertedImage);
            uploadImage(userImage);
        }
    }


}
