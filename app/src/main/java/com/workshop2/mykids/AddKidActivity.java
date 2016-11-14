package com.workshop2.mykids;

import android.*;
import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.workshop2.mykids.Model.Kid;
import com.workshop2.mykids.Model.Schedule;
import com.workshop2.mykids.Other.CircleTransform;
import com.workshop2.mykids.Other.Receiver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import gun0912.tedbottompicker.TedBottomPicker;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.ALL;

public class AddKidActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    public static final String EXTRA_PARAM_ID = "kid_id";
    public static String TAG = "AddKid";
    private static final int SELECT_PICTURE = 1;
    private ImageView imageView;
    private TextView lblPhoto;
    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private MaterialSpinner spinner, spinner_city;
    private String gender = "Male"
            , state = "Perlis"
            ,imageName
            ,selectedImagePath
            ,imagePath = "https://firebasestorage.googleapis.com/v0/b/firebase-mykids.appspot.com/o/kid%2Fkid_boy.png?alt=media&token=b24c572d-039a-4c40-ab96-b2578da443ee";
    private EditText birthDate, etName;
    private Firebase mRef;
    private ProgressDialog progressDialog;
    private Uri file;
    private Bitmap bp;
    private Button save;
    private Boolean newPhoto=false;
    private byte [] data_img;
    private ArrayList<Schedule> list;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kid_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Kid");
        imageView = (ImageView)findViewById(R.id.profileImage);

        Glide.with(this)
                .load(R.drawable.ic_action_camera)
                .bitmapTransform(new CircleTransform(this))
                .into(imageView);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                Toast.makeText(AddKidActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                imageView.setEnabled(true);
                final TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(AddKidActivity.this)
                        .showCameraTile(true)
                        .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                            @Override
                            public void onImageSelected(Uri uri) {
                                selectedImagePath = uri.getPath();
                                imageName = new File(selectedImagePath).getName();
                                ParcelFileDescriptor parcelFileDescriptor;
                                try {
                                    parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                                    FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                                    bp = BitmapFactory.decodeFileDescriptor(fileDescriptor);

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                                    data_img = baos.toByteArray();

                                    parcelFileDescriptor.close();

                                    Glide.with(AddKidActivity.this)
                                            .load(data_img)
                                            .asBitmap()
                                            .transform(new CircleTransform(AddKidActivity.this))
                                            .into(imageView);

                                    newPhoto = true;

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        })
                        .create();

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                takePictureFromGallery();
                        tedBottomPicker.show(getSupportFragmentManager());

                    }
                });

                lblPhoto = (TextView)findViewById(R.id.lblPhoto);
                lblPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                takePictureFromGallery();
                        tedBottomPicker.show(getSupportFragmentManager());

                    }
                });
            }
            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(AddKidActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

        spinner = (MaterialSpinner)findViewById(R.id.spinner_gender);
        spinner.setItems("Male", "Female");

        Glide.with(AddKidActivity.this).load(R.drawable.kid_boy)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(AddKidActivity.this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                gender = item;
                if(!newPhoto){
                    if(gender.equals("Male")){
                        imagePath = "https://firebasestorage.googleapis.com/v0/b/firebase-mykids.appspot.com/o/kid%2Fkid_boy.png?alt=media&token=b24c572d-039a-4c40-ab96-b2578da443ee";
                        Glide.with(AddKidActivity.this).load(R.drawable.kid_boy)
                                .crossFade()
                                .thumbnail(0.5f)
                                .bitmapTransform(new CircleTransform(AddKidActivity.this))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView);
                    }
                    else{
                        imagePath = "https://firebasestorage.googleapis.com/v0/b/firebase-mykids.appspot.com/o/kid%2Fkid_girl.png?alt=media&token=27e7fd08-e63a-416f-b35f-adb8e26c134d";
                        Glide.with(AddKidActivity.this).load(R.drawable.kid_girl)
                                .crossFade()
                                .thumbnail(0.5f)
                                .bitmapTransform(new CircleTransform(AddKidActivity.this))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(imageView);
                    }
                }
            }
        });
        spinner_city = (MaterialSpinner)findViewById(R.id.spinner_city);
        spinner_city.setItems("Perlis","Penang","Perak","Pahang","Selangor","Kedah","Kelantan","Terengganu","Negeri Sembilan","Melaka","Johor","Sabah","Sarawak");
        spinner_city.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                state = item;
            }
        });
        birthDate = (EditText)findViewById(R.id.birthDate);
        etName = (EditText)findViewById(R.id.etName);
        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        AddKidActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        mRef = new Firebase("https://fir-mykids.firebaseio.com/");

        save = (Button)findViewById(R.id.kdSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(AddKidActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                if(newPhoto){
                    newPhoto = false;
                    uploadImage();
                }
                else{
                    Kid kid = new Kid();
                    kid.setKid_name(etName.getText().toString());
                    kid.setKid_date(birthDate.getText().toString());
                    kid.setKid_gender(gender);
                    kid.setKid_image(imagePath);
                    kid.setKid_state(state);
                    try {
                        addKid(kid);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

//
//        Calendar c = Calendar.getInstance();
//        c.add(Calendar.SECOND, 10);
//
//        AlarmManager alarms = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
//        Receiver receiver = new Receiver();
//        IntentFilter filter = new IntentFilter("ALARM_ACTION");
//        registerReceiver(receiver, filter);
//        Intent intent = new Intent("ALARM_ACTION");
//        intent.putExtra("param", "My scheduled action");
//        PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent, 0);
//        alarms.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), operation) ;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date_str = dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
        birthDate.setText(date_str);
    }

    public void uploadImage(){
        //Initialize firebase storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://firebase-mykids.appspot.com/kid");
        StorageReference kidRef = storageRef.child(imageName);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
//        byte[] data = baos.toByteArray();

        UploadTask uploadTask = kidRef.putBytes(data_img);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Kid kid = new Kid();
                kid.setKid_name(etName.getText().toString());
                kid.setKid_date(birthDate.getText().toString());
                kid.setKid_gender(gender);
                kid.setKid_image(taskSnapshot.getDownloadUrl().toString());
                kid.setKid_state(state);
                try {
                    addKid(kid);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    public void takePhoto(){
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        file = Uri.fromFile(getOutputMediaFile());
//        //intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
//        startActivityForResult(intent, 100);
//    }

//    private static File getOutputMediaFile(){
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "MyKids");
//        if (!mediaStorageDir.exists()){
//            if (!mediaStorageDir.mkdirs()){
//                Log.d("CameraDemo", "failed to create directory");
//                return null;
//            }
//        }
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        return new File(mediaStorageDir.getPath() + File.separator +
//                "IMG_"+ timeStamp + ".jpg");
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == 0) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                imageView.setEnabled(true);
//            }
//        }
//    }

//    private void takePictureFromGallery() {
//        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i, SELECT_PICTURE);
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            if (requestCode == SELECT_PICTURE) {
//                newPhoto = true;
//                Uri selectedImageUri = data.getData();
//                selectedImagePath = getPath(selectedImageUri);
//                imageName = new File(selectedImagePath).getName();
//                if (Build.VERSION.SDK_INT < 19) {
//                    bp = BitmapFactory.decodeFile(selectedImagePath);
//
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
//                    data_img = baos.toByteArray();
//
//                    imageName = new File(selectedImagePath).getName();
//                    Glide.with(this)
//                            .load(data_img)
//                            .bitmapTransform(new CircleTransform(this))
//                            .into(imageView);
//                }
//                else {
//                    ParcelFileDescriptor parcelFileDescriptor;
//                    try {
//                        parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImageUri, "r");
//                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
//                        bp = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
//                        data_img = baos.toByteArray();
//
//                        parcelFileDescriptor.close();
//
//                        Glide.with(this)
//                                .load(data_img)
//                                .asBitmap()
//                                .transform(new CircleTransform(this))
//                                .into(imageView);
//
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//
//    public String getPath(Uri uri) {
//        if( uri == null ) {
//            return null;
//        }
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//        if( cursor != null ){
//            int column_index = cursor
//                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        }
//        return uri.getPath();
//    }

    private void addKid(Kid newKid) throws ParseException {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getUid() != null) {
            Firebase userRef = mRef.child("User").child(user.getUid()).child("kid");
            Firebase ref = userRef.push();
            newKid.setKid_id(ref.getKey());
            ref.setValue(newKid);
            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
        }
        else
            Log.d("FB", "failed to get current user");
        generateSchedule(newKid);
        progressDialog.dismiss();
    }

    private void generateSchedule(Kid k) throws ParseException {
        list = new ArrayList<Schedule>();

        String dateInString = k.getKid_date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        Date date = formatter.parse(dateInString);
        c.setTime(date);
        c.add(Calendar.MONTH, 0);
        list.add(new Schedule("BCG", formatter.format(c.getTime()), "1", "7:50 AM", 10, false));
        setNotify("BCG", c, 1);

        c.add(Calendar.MONTH, 0);
        list.add(new Schedule("Hepatitis B", formatter.format(c.getTime()), "2", "7:50 AM", 10, false));
        setNotify("Hepatitis B", c, 2);

        c.add(Calendar.MONTH, 1);
        list.add(new Schedule("Hepatitis B", formatter.format(c.getTime()), "3", "7:50 AM", 10, false));
        setNotify("Hepatitis B", c, 3);

        c.add(Calendar.MONTH, 1);
        list.add(new Schedule("DTaP", formatter.format(c.getTime()), "4", "7:50 AM", 10, false));
        setNotify("DTaP", c, 4);

        list.add(new Schedule("Hib", formatter.format(c.getTime()), "5", "7:50 AM", 10, false));
        setNotify("Hib", c, 5);

        list.add(new Schedule("Polio", formatter.format(c.getTime()), "6", "7:50 AM", 10, false));
        setNotify("Polio", c, 6);


        c.add(Calendar.MONTH, 1);
        list.add(new Schedule("DTaP", formatter.format(c.getTime()), "7", "7:50 AM", 10, false));
        setNotify("DTaP", c, 7);

        list.add(new Schedule("Hib", formatter.format(c.getTime()), "8", "7:50 AM", 10, false));
        setNotify("Hib", c, 8);

        list.add(new Schedule("Polio", formatter.format(c.getTime()), "9", "7:50 AM", 10, false));
        setNotify("Polio", c, 9);


        c.add(Calendar.MONTH, 2);
        list.add(new Schedule("DTaP", formatter.format(c.getTime()), "10", "7:50 AM", 10, false));
        setNotify("DTaP", c, 10);

        list.add(new Schedule("Hib", formatter.format(c.getTime()), "11", "7:50 AM", 10, false));
        setNotify("Hib", c, 11);

        list.add(new Schedule("Polio", formatter.format(c.getTime()), "12", "7:50 AM", 10, false));
        setNotify("Polio", c, 12);


        c.add(Calendar.MONTH, 1);
        list.add(new Schedule("Hepatitis B", formatter.format(c.getTime()), "13", "7:50 AM", 10, false));
        setNotify("Hepatitis B", c, 13);

        c.add(Calendar.MONTH, 6);
        list.add(new Schedule("MMR", formatter.format(c.getTime()), "14", "7:50 AM", 10, false));
        setNotify("MMR", c, 14);

        c.add(Calendar.MONTH, 6);
        list.add(new Schedule("DTaP", formatter.format(c.getTime()), "15", "7:50 AM", 10, false));
        setNotify("DTaP", c, 15);

        list.add(new Schedule("Hib", formatter.format(c.getTime()), "16", "7:50 AM", 10, false));
        setNotify("Hib", c, 16);

        list.add(new Schedule("Polio", formatter.format(c.getTime()), "17", "7:50 AM", 10, false));
        setNotify("Polio", c, 17);


        c.add(Calendar.MONTH, 66);
        list.add(new Schedule("Measles / MR", formatter.format(c.getTime()), "18", "7:50 AM", 10, false));
        setNotify("Measles / MR", c, 18);

        list.add(new Schedule("DT", formatter.format(c.getTime()), "19", "7:50 AM", 10, false));
        setNotify("DT", c, 19);

        list.add(new Schedule("Polio (OPV)", formatter.format(c.getTime()), "20", "7:50 AM", 10, false));
        setNotify("Polio (OPV)", c, 20);


        c.add(Calendar.MONTH, 72);
        list.add(new Schedule("HPV", formatter.format(c.getTime()), "21", "7:50 AM", 10, false));
        setNotify("HPV", c, 21);

        c.add(Calendar.MONTH, 24);
        list.add(new Schedule("ATT", formatter.format(c.getTime()), "22", "7:50 AM", 10, false));
        setNotify("ATT", c, 22);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        Firebase ref = mRef.child("User").child(user.getUid()).child("kid").child(k.getKid_id()).child("schedule");
        ref.setValue(list);
    }

    private void setNotify(final String title, final Calendar cal, final int id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!cal.before(Calendar.getInstance())){

                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MINUTE, 50);
                    cal.set(Calendar.HOUR_OF_DAY, 7);
                    cal.add(Calendar.DAY_OF_MONTH, -2);

                    AlarmManager alarms = (AlarmManager)getSystemService(ALARM_SERVICE);
                    Receiver receiver = new Receiver();
                    IntentFilter filter = new IntentFilter("ALARM_ACTION");
                    registerReceiver(receiver, filter);
                    Intent intent = new Intent("ALARM_ACTION");
                    intent.putExtra("title", title);
                    PendingIntent operation = PendingIntent.getBroadcast(AddKidActivity.this, id, intent, 0);
                    alarms.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), operation) ;
                }
            }
        }).start();
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        finish(); // close this activity as oppose to navigating up
//        return false;
//    }
}
