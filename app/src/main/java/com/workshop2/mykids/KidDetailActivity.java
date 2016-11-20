package com.workshop2.mykids;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
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
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.workshop2.mykids.model.Kid;
import com.workshop2.mykids.other.CircleTransform;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import gun0912.tedbottompicker.TedBottomPicker;

public class KidDetailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, Validator.ValidationListener{
    public static final String EXTRA_PARAM_ID = "kid_id";
    private static final int SELECT_PICTURE = 1;
    private ImageView imageView;
    private TextView lblPhoto;
    private CollapsingToolbarLayout collapsingToolbar;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private MaterialSpinner spinner, spinner_city;
    private String gender = "Male", state = "Perlis",imageName,selectedImagePath, kid;
    @NotEmpty
    private EditText birthDate;
    @NotEmpty
    private EditText etName;
    private EditText etState;
    private TextInputLayout tilName, tilDate;
    private Validator validator;
    private DatabaseReference mRef;
    private ProgressDialog progressDialog;
    private Uri file;
    private Bitmap bp;
    private Button save;
    private Boolean newPhoto=false;
    private byte[] data_img;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kid_detail);

        validator = new Validator(this);
        validator.setValidationListener(this);
        tilName = (TextInputLayout)findViewById(R.id.tilName);
        tilDate = (TextInputLayout)findViewById(R.id.tilDate);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        imageView = (ImageView)findViewById(R.id.profileImage);
        final Intent intent = getIntent();
        String kimage = intent.getStringExtra("kimage");
        String kname = intent.getStringExtra("kname");
        String kdate = intent.getStringExtra("kdate");
        String kgender = intent.getStringExtra("kgender");
        String kstate = intent.getStringExtra("kstate");
        kid = intent.getStringExtra("kid");

        Glide.with(this)
                .load(kimage)
                .bitmapTransform(new CircleTransform(this))
                .into(imageView);

        setupSpinner(kstate, kgender);
        setupPhoto();

        birthDate = (EditText)findViewById(R.id.birthDate);
        etName = (EditText)findViewById(R.id.etName);
        etState = (EditText)findViewById(R.id.etState);

        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        KidDetailActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        birthDate.setText(kdate);
        etName.setText(kname);
        etState.setText(kstate);
        etState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(KidDetailActivity.this)
                        .title("State")
                        .items(R.array.state)
                        .itemsCallbackSingleChoice(index, new MaterialDialog.ListCallbackSingleChoice() {
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

        save = (Button)findViewById(R.id.btnSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(KidDetailActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                validator.validate();
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
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://firebase-mykids.appspot.com/kid");
        StorageReference kidRef = storageRef.child(imageName);

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
                kid.setKid_state(state);
                kid.setKid_image(taskSnapshot.getDownloadUrl().toString());
                Map<String, Object> kidUpdate = kid.toMap_new();
//                addKid(kid);
                updateKid(kidUpdate);
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
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == 0) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                imageView.setEnabled(true);
//            }
//        }
//    }
//
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
////                        imageView.setImageBitmap(bp);
//                        Glide.with(this)
//                                .load(data_img)
//                                .asBitmap()
//                                .transform(new CircleTransform(this))
//                                .into(imageView);
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

    private void updateKid(final Map<String, Object> kidUpdate){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    mRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference userRef = mRef.child("User").child(user.getUid()).child("kid");
                    userRef.child(kid).updateChildren(kidUpdate);
                    Toast.makeText(KidDetailActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                }
                else{
                    mRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference userRef = mRef.child("User").child(user.getUid()).child("kid");
                    userRef.child(kid).onDisconnect().updateChildren(kidUpdate);
//                    Toast.makeText(KidDetailActivity.this, "Saved", Toast.LENGTH_SHORT).show();
//
                    Snackbar.make(findViewById(R.id.coorLay), "Changes will be made when internet connection is available.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(KidDetailActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user.getUid() != null) {
//            mRef = FirebaseDatabase.getInstance().getReference();
//            DatabaseReference userRef = mRef.child("User").child(user.getUid()).child("kid");
//            userRef.child(kid).updateChildren(kidUpdate);
//            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
//        }
//        else
//            Log.d("FB", "failed to get current user");
        progressDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSpinner(final String kstate, String kgender){
        spinner = (MaterialSpinner)findViewById(R.id.spinner_gender);
        spinner.setItems("Male", "Female");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                gender = item;
            }
        });
        spinner.setSelectedIndex(1);
        if(kgender.equals("Male")){
            gender = "Male";
            spinner.setSelectedIndex(0);
        }
        else{
            gender = "Female";
            spinner.setSelectedIndex(1);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                index = 0;
                if(kstate.equals("Perlis")){
                    index = 0;
                }
                else if(kstate.equals("Penang")){
                    index = 1;
                }
                else if(kstate.equals("Perak")){
                    index = 2;
                }
                else if(kstate.equals("Pahang")){
                    index = 3;
                }
                else if(kstate.equals("Selangor")){
                    index = 4;
                }
                else if(kstate.equals("Kedah")){
                    index = 5;
                }
                else if(kstate.equals("Kelantan")){
                    index = 6;
                }
                else if(kstate.equals("Terengganu")){
                    index = 7;
                }
                else if(kstate.equals("Negeri Sembilan")){
                    index = 8;
                }
                else if(kstate.equals("Melaka")){
                    index = 9;
                }
                else if(kstate.equals("Johor")){
                    index = 10;
                }
                else if(kstate.equals("Sabah")){
                    index = 11;
                }
                else {
                    index = 12;
                }
            }
        }).start();
    }

    private void setupPhoto(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                imageView.setEnabled(true);
                final TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(KidDetailActivity.this)
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

                                    Glide.with(KidDetailActivity.this)
                                            .load(data_img)
                                            .asBitmap()
                                            .transform(new CircleTransform(KidDetailActivity.this))
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

            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    @Override
    public void onValidationSucceeded() {
        if(newPhoto){
            newPhoto = false;
            uploadImage();
        }
        else{
            Kid kid = new Kid();
            kid.setKid_name(etName.getText().toString());
            kid.setKid_date(birthDate.getText().toString());
            kid.setKid_gender(gender);
            kid.setKid_state(state);
            Map<String, Object> kidUpdate = kid.toMap();
            updateKid(kidUpdate);
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
//                ((EditText) view).setError(message);
                tilName.setError(message);
                tilDate.setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
        progressDialog.dismiss();
    }
}
