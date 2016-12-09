package com.workshop2.mykids;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.dd.CircularProgressButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.workshop2.mykids.model.Kid;
import com.workshop2.mykids.model.Schedule;
import com.workshop2.mykids.other.CircleTransform;
import com.workshop2.mykids.other.Receiver;

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

import gun0912.tedbottompicker.TedBottomPicker;

public class AddKidActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    public static final String EXTRA_PARAM_ID = "kid_id";
    public static String TAG = "AddKid";
    private static final int SELECT_PICTURE = 1;
    private ImageView imageView;
    private TextView lblPhoto;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private MaterialSpinner spinner;
    private String gender = "Male"
            , state = "Perlis"
            ,imageName
            ,selectedImagePath
            ,imagePath = "https://firebasestorage.googleapis.com/v0/b/mykidsapp-e0c43.appspot.com/o/kid%2Fkid_boy.png?alt=media&token=73561763-d890-49bb-a604-fd19939b241e";
    private EditText birthDate;
    private EditText etName;
    private EditText etState;
    private TextInputLayout tilName, tilDate, tilState;

    private ProgressDialog progressDialog;
    private Uri file;
    private Bitmap bp;
    private CircularProgressButton save;
    private Boolean newPhoto=false;
    private byte [] data_img;
    private ArrayList<Schedule> list;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kid_detail);

        tilName = (TextInputLayout)findViewById(R.id.tilName);
        tilDate = (TextInputLayout)findViewById(R.id.tilDate);
        tilState = (TextInputLayout)findViewById(R.id.tilState);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        imageView = (ImageView)findViewById(R.id.profileImage);

        Glide.with(this)
                .load(R.drawable.ic_action_camera)
                .bitmapTransform(new CircleTransform(this))
                .into(imageView);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
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

        Glide.with(AddKidActivity.this).load(R.drawable.kid_boy)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(AddKidActivity.this))
                .into(imageView);

        setupSpinner();

        birthDate = (EditText)findViewById(R.id.birthDate);
        etName = (EditText)findViewById(R.id.etName);
        etState = (EditText)findViewById(R.id.etState);

        etName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilName.setError(null);
                tilDate.setError(null);
                tilState.setError(null);
                save.setProgress(0);
            }
        });

        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilName.setError(null);
                tilDate.setError(null);
                tilState.setError(null);
                save.setProgress(0);

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

        etState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilName.setError(null);
                tilDate.setError(null);
                tilState.setError(null);
                save.setProgress(0);
                new MaterialDialog.Builder(AddKidActivity.this)
                        .title("State")
                        .items(R.array.state)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                state = String.valueOf(text);
                                etState.setText(text);
                                return true;
                            }
                        })
                        .positiveText("Choose")
                        .show();
            }
        });

        save = (CircularProgressButton)findViewById(R.id.btnSave);
        save.setIndeterminateProgressMode(true);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(AddKidActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
                connectedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean connected = snapshot.getValue(Boolean.class);
                        if (connected) {
                            if(!gotError()){
                                save.setProgress(50);
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
                            }else{
                                save.setProgress(-1);
                                progressDialog.dismiss();

                            }
                        }
                        else{
                            Snackbar.make(findViewById(R.id.coorLay), "No internet connection", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.err.println("Listener was cancelled at .info/connected");
                    }
                });
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date_str = dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
        birthDate.setText(date_str);
    }

    public void uploadImage(){
        //Initialize firebase storage
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://mykidsapp-e0c43.appspot.com/kid");
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
        }).start();
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

    private void addKid(final Kid newKid) throws ParseException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message="";
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user.getUid() != null) {
                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference()
                            .child("User").child(user.getUid()).child("kid");
                    DatabaseReference ref = mRef.push();
                    newKid.setKid_id(ref.getKey());
                    ref.setValue(newKid);
                    message = "New kid is added";
                    save.setProgress(100);
                    generateSchedule(newKid);

                    final String finalMessage = message;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etState.setText("");
                            etName.setText("");
                            birthDate.setText("");
                            progressDialog.dismiss();
                            Toast.makeText(AddKidActivity.this, finalMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                    message = "Failed to get current user";
            }
        }).start();
    }

    private void generateSchedule(final Kid k) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                list = new ArrayList<Schedule>();
                String dateInString = k.getKid_date();

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                Calendar c = Calendar.getInstance();
                Date date = null;
                try {
                    date = formatter.parse(dateInString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c.setTime(date);

                c.add(Calendar.MONTH, 0);
                list.add(new Schedule("BCG", formatter.format(c.getTime()), "1", "7:50 AM", 10, false));
                setNotify("BCG", c, 1);

                c.add(Calendar.MONTH, 0);
                list.add(new Schedule("HepB", formatter.format(c.getTime()), "2", "7:50 AM", 10, false));
                setNotify("Hepatitis B", c, 2);

                c.add(Calendar.MONTH, 1);
                list.add(new Schedule("HepB", formatter.format(c.getTime()), "3", "7:50 AM", 10, false));
                setNotify("Hepatitis B", c, 3);

                c.add(Calendar.MONTH, 1);
                list.add(new Schedule("DTaP", formatter.format(c.getTime()), "4", "7:50 AM", 10, false));
                setNotify("DTaP", c, 4);

                list.add(new Schedule("Hib", formatter.format(c.getTime()), "5", "7:50 AM", 10, false));
                setNotify("Hib", c, 5);

                list.add(new Schedule("IPV", formatter.format(c.getTime()), "6", "7:50 AM", 10, false));
                setNotify("Polio", c, 6);


                c.add(Calendar.MONTH, 1);
                list.add(new Schedule("DTaP", formatter.format(c.getTime()), "7", "7:50 AM", 10, false));
                setNotify("DTaP", c, 7);

                list.add(new Schedule("Hib", formatter.format(c.getTime()), "8", "7:50 AM", 10, false));
                setNotify("Hib", c, 8);

                list.add(new Schedule("IPV", formatter.format(c.getTime()), "9", "7:50 AM", 10, false));
                setNotify("Polio", c, 9);


                c.add(Calendar.MONTH, 2);
                list.add(new Schedule("DTaP", formatter.format(c.getTime()), "10", "7:50 AM", 10, false));
                setNotify("DTaP", c, 10);

                list.add(new Schedule("Hib", formatter.format(c.getTime()), "11", "7:50 AM", 10, false));
                setNotify("Hib", c, 11);

                list.add(new Schedule("IPV", formatter.format(c.getTime()), "12", "7:50 AM", 10, false));
                setNotify("Polio", c, 12);


                c.add(Calendar.MONTH, 1);
                list.add(new Schedule("HepB", formatter.format(c.getTime()), "13", "7:50 AM", 10, false));
                setNotify("Hepatitis B", c, 13);

                if(state.equals("Sabah")){
                    list.add(new Schedule("Measles", formatter.format(c.getTime()), "13", "7:50 AM", 10, false));
                    setNotify("Measles", c, 13);
                }

                c.add(Calendar.MONTH, 6);
                list.add(new Schedule("MMR", formatter.format(c.getTime()), "14", "7:50 AM", 10, false));
                setNotify("MMR", c, 14);

                c.add(Calendar.MONTH, 6);
                list.add(new Schedule("DTaP", formatter.format(c.getTime()), "15", "7:50 AM", 10, false));
                setNotify("DTaP", c, 15);

                list.add(new Schedule("Hib", formatter.format(c.getTime()), "16", "7:50 AM", 10, false));
                setNotify("Hib", c, 16);

                list.add(new Schedule("IPV", formatter.format(c.getTime()), "17", "7:50 AM", 10, false));
                setNotify("Polio", c, 17);


                c.add(Calendar.MONTH, 66);
                list.add(new Schedule("MMR", formatter.format(c.getTime()), "18", "7:50 AM", 10, false));
                setNotify("Measles / MR", c, 18);

                list.add(new Schedule("DT", formatter.format(c.getTime()), "19", "7:50 AM", 10, false));
                setNotify("DT", c, 19);

                list.add(new Schedule("OPV", formatter.format(c.getTime()), "20", "7:50 AM", 10, false));
                setNotify("Polio (OPV)", c, 20);

                c.add(Calendar.MONTH, 72);
                list.add(new Schedule("HPV", formatter.format(c.getTime()), "21", "7:50 AM", 10, false));
                setNotify("HPV", c, 21);

                c.add(Calendar.MONTH, 24);
                list.add(new Schedule("TT", formatter.format(c.getTime()), "22", "7:50 AM", 10, false));
                setNotify("TT", c, 22);

                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                DatabaseReference scheduleRef = ref.child("User").child(user.getUid()).child("kid").child(k.getKid_id()).child("schedule");
                scheduleRef.setValue(list);
            }
        }).start();
    }

    private void setNotify(final String title, final Calendar cal, final int id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(!cal.before(Calendar.getInstance())){
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MINUTE, 25);
                    cal.set(Calendar.HOUR_OF_DAY, 18);
//                    cal.add(Calendar.DAY_OF_MONTH, -2);

                    AlarmManager alarms = (AlarmManager)getSystemService(ALARM_SERVICE);
                    Intent intent = new Intent(AddKidActivity.this, Receiver.class);
                    intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    intent.putExtra("title", title);
                    PendingIntent operation = PendingIntent.getBroadcast(AddKidActivity.this, id+(int)cal.getTimeInMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarms.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), operation);
                }
            }
        }).start();
    }

    public boolean gotError() {
        boolean error = false;
        if(etName.getText().toString().matches("")) {
            tilName.setError("Cannot be empty");
            error = true;
        }
        if(etState.getText().toString().matches("")) {
            tilState.setError("Cannot be empty");
            error = true;
        }
        if(birthDate.getText().toString().matches("")) {
            tilDate.setError("Cannot be empty");
            error = true;
        }
        return error;
    }

    private void setupSpinner(){
        spinner = (MaterialSpinner)findViewById(R.id.spinner_gender);
        spinner.setItems("Male", "Female");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                tilName.setError(null);
                tilDate.setError(null);
                tilState.setError(null);
                save.setProgress(0);
                gender = item;
                if(!newPhoto){
                    if(gender.equals("Male")){
                        imagePath = "https://firebasestorage.googleapis.com/v0/b/mykidsapp-e0c43.appspot.com/o/kid%2Fkid_boy.png?alt=media&token=73561763-d890-49bb-a604-fd19939b241e";
                        Glide.with(AddKidActivity.this).load(R.drawable.kid_boy)
                                .crossFade()
                                .thumbnail(0.5f)
                                .bitmapTransform(new CircleTransform(AddKidActivity.this))
                                .into(imageView);
                    }
                    else{
                        imagePath = "https://firebasestorage.googleapis.com/v0/b/mykidsapp-e0c43.appspot.com/o/kid%2Fkid_girl.png?alt=media&token=c9bfdf7e-8236-4157-a075-a9d4366d52d5";
                        Glide.with(AddKidActivity.this).load(R.drawable.kid_girl)
                                .crossFade()
                                .thumbnail(0.5f)
                                .bitmapTransform(new CircleTransform(AddKidActivity.this))
                                .into(imageView);
                    }
                }
            }
        });
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        finish(); // close this activity as oppose to navigating up
//        return false;
//    }
}
