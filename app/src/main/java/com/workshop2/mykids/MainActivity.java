package com.workshop2.mykids;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.fabtransitionactivity.SheetLayout;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaeger.library.StatusBarUtil;
import com.workshop2.mykids.other.CircleTransform;
import com.workshop2.mykids.other.PrefManager;

public class MainActivity extends AppCompatActivity implements KidFragment.OnKidFragmentListener, VaccineFragment.OnVaccineFragmentListener, SheetLayout.OnFabAnimationEndListener
,EventFragment.OnFragmentInteractionListener{
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private FirebaseUser firebaseUser;
    private String[] activityTitles;
    private TextView txtName, txtEmail;
    private View navHeader;
    private ImageView imgProfile, imageView;
    private Handler mHandler;
    private SheetLayout mSheetLayout;
    private  GoogleApiClient mGoogleApiClient;

    public static int navItemIndex = 0;
    private static final String TAG_KID = "Kid";
    private static final String TAG_VACCINE = "Vaccine";
    private static final String TAG_EVENT = "Event";
    public static String CURRENT_TAG = TAG_KID;
    private boolean shouldLoadHomeFragOnBackPress = true;
    private static final int REQUEST_CODE = 1;


    public CollapsingToolbarLayout getCollapsingToolbar() {
        return collapsingToolbarLayout;
    }

//    private void syncFrags() {
//        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
//        if (fragment instanceof VaccineFragment) {
//            disableCollapse();
//        } else if (fragment instanceof KidFragment) {
//            enableCollapse();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                savetoPref();
            }
        }).start();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBarLayout = (AppBarLayout)findViewById(R.id.appbar_main);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton)findViewById(R.id.fab_add_kid);
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        navHeader = navigationView.getHeaderView(0);
        imageView = (ImageView)findViewById(R.id.header);
        txtName = (TextView) navHeader.findViewById(R.id.navName);
        txtEmail = (TextView) navHeader.findViewById(R.id.navWebsite);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        mHandler = new Handler();

        mSheetLayout = (SheetLayout)findViewById(R.id.bottom_sheet);
        mSheetLayout.setFab(fab);
        mSheetLayout.setFabAnimationEndListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, AddKidActivity.class));
                mSheetLayout.expandFab();
            }
        });
        setUpNavigationView();
        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_KID;
            loadHomeFragment();
        }
        loadNavHeader();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(AppIndex.API).build();
    }

    private void loadNavHeader() {
        // name, website
        txtName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        txtEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        // Loading profile image
        Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(imgProfile);
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                KidFragment kidFragment = new KidFragment();
                collapsingToolbarLayout.setTitleEnabled(false);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.kid));
                collapsingToolbarLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.colorPrimaryDark));
                collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorPrimary));
                return kidFragment;
            case 1:
                VaccineFragment vaccineFragment = new VaccineFragment();
                collapsingToolbarLayout.setTitleEnabled(false);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.vaccine_));
                collapsingToolbarLayout.setBackgroundColor(getResources().getColor(R.color.vaccine));
                collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.vaccineDark));
                collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.vaccine));
                return vaccineFragment;
            case 2:
                EventFragment eventFragment = new EventFragment();
                collapsingToolbarLayout.setTitleEnabled(false);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.coming));
                collapsingToolbarLayout.setBackgroundColor(getResources().getColor(R.color.blue));
                collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(R.color.darkBlue));
                collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.blue));

//                Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.coming);
//                Palette.generateAsync(bitmap,
//                        new Palette.PaletteAsyncListener() {
//                            @Override
//                            public void onGenerated(Palette palette) {
//                                Palette.Swatch vibrant =
//                                        palette.getVibrantSwatch();
//                                int mutedColor = palette.getVibrantSwatch().getRgb();
//                                if (vibrant != null) {
//                                    // If we have a vibrant color
//                                    // update the title TextView
//                                    collapsingToolbarLayout.setBackgroundColor(mutedColor);
//                                    //  mutedColor = palette.getMutedColor(R.attr.colorPrimary);
//                                    collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(mutedColor));
//                                    collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(mutedColor));
//
//                                }
//                            }
//                        });

                return eventFragment;
            default:
                return new KidFragment();
        }
    }


    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame_container, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
//        syncFrags();
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_kid:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_KID;
                        break;
                    case R.id.nav_coming:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_EVENT;
                        break;
                    case R.id.nav_vaccine:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_VACCINE;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        drawer.closeDrawers();
                        return true;
//                    case R.id.nav_privacy_policy:
//                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
//                        drawer.closeDrawers();
//                        return true;
                    default:
                        navItemIndex = 0;
                }

                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);
                loadHomeFragment();
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        else if(navItemIndex != 0){
            navItemIndex = 0;
            CURRENT_TAG = TAG_KID;
            loadHomeFragment();
//            syncFrags();
            return;
        }
        else{
//        MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this)
//                .title("Are you sure")
//                .content("You are leaving MyKids App right now. Make sure do check your kid's schedule frequently. ")
//                .positiveText("Yes")
//                .negativeText("No")
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        finish();
//                    }
//                })
//                .onNegative(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        return;
//                    }
//                });
//
//        MaterialDialog dialog = builder.build();
//        dialog.show();
            super.onBackPressed();
            finish();
        }
    }

//    @Override
//    public void disableCollapse() {
////        imageView.setVisibility(View.GONE);
//        imageView.setImageDrawable(getResources().getDrawable(R.drawable.vaccine_));
////        collapsingToolbarLayout.setTitleEnabled(false);
////        toolbar.setTitle("");
//    }

//    @Override
//    public void enableCollapse() {
////        imageView.setVisibility(View.VISIBLE);
//        imageView.setImageDrawable(getResources().getDrawable(R.drawable.kid));
////        collapsingToolbarLayout.setTitleEnabled(false);
////        toolbar.setTitle("MyKids");
//    }

    private void savetoPref(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("uid", user.getUid());
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks
//        int id = item.getItemId();
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_logout) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
////                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
//                    FirebaseAuth.getInstance().signOut();
//                }
//            }).start();
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    PrefManager prefManager =  new PrefManager(MainActivity.this);
//                    prefManager.setFirstTimeLaunch(true);
//                }
//            }).start();
//            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
//            return true;
//        }

        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

//                FirebaseAuth.getInstance().signOut();
                if(mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        FirebaseAuth.getInstance().signOut();
                                        PrefManager prefManager =  new PrefManager(MainActivity.this);
                                        prefManager.setFirstTimeLaunch(true);
                                    }
                                }).start();
                                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }else{
                    Toast.makeText(MainActivity.this, "WTF", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("Logout", "Google API Client Connection Suspended");
            }
        });


        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }

    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(MainActivity.this, AddKidActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            mSheetLayout.contractFab();
        }
    }

//    public void checkInternet(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
//                connectedRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot snapshot) {
//                        boolean connected = snapshot.getValue(Boolean.class);
//                        if (!connected) {
//                            Snackbar.make(findViewById(R.id.coorLay), "No internet connection", Snackbar.LENGTH_LONG).show();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError error) {
//                        System.err.println("Listener was cancelled at .info/connected");
//                    }
//                });
//            }
//        }).start();
//    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}