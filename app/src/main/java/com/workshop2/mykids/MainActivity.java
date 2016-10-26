package com.workshop2.mykids;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;
import com.github.fabtransitionactivity.SheetLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.workshop2.mykids.Other.CircleTransform;

public class MainActivity extends AppCompatActivity implements KidFragment.OnKidFragmentListener, VaccineFragment.OnVaccineFragmentListener, SheetLayout.OnFabAnimationEndListener{
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

    public static int navItemIndex = 0;
    private static final String TAG_KID = "Kid";
    private static final String TAG_VACCINE = "Vaccine";
    public static String CURRENT_TAG = TAG_KID;
    private boolean shouldLoadHomeFragOnBackPress = true;
    private static final int REQUEST_CODE = 1;


    public CollapsingToolbarLayout getCollapsingToolbar() {
        return collapsingToolbarLayout;
    }

    private void syncFrags() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (fragment instanceof VaccineFragment) {
            disableCollapse();
        } else if (fragment instanceof KidFragment) {
            enableCollapse();
        }
    }


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
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                KidFragment kidFragment = new KidFragment();
                return kidFragment;
            case 1:
                VaccineFragment vaccineFragment = new VaccineFragment();
                return vaccineFragment;
            default:
                return new KidFragment();
        }
    }


    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
//            toggleFab();
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
//        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
        syncFrags();
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
//                    case R.id.nav_kid:
//                        navItemIndex = 1;
//                        CURRENT_TAG = "";
////                        startActivity(new Intent(MainActivity.this, KidActivity.class));
//                        break;
                    case R.id.nav_vaccine:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_VACCINE;
                        break;
//                    case R.id.nav_about_us:
//                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
//                        drawer.closeDrawers();
//                        return true;
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
        super.onBackPressed();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        if (shouldLoadHomeFragOnBackPress) {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_KID;
                loadHomeFragment();
                return;
            }
        }
        syncFrags();
    }

    @Override
    public void disableCollapse() {
        imageView.setVisibility(View.GONE);
        collapsingToolbarLayout.setTitleEnabled(false);
    }

    @Override
    public void enableCollapse() {
        imageView.setVisibility(View.VISIBLE);
        collapsingToolbarLayout.setTitleEnabled(true);
    }

    private void savetoPref(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("uid", user.getUid());
                editor.commit();
            }
        }).start();
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
        // Handle action bar item clicks
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));

//                AddKidDialogFragment addKidDialogFragment = new AddKidDialogFragment();
//                addKidDialogFragment.show(getSupportFragmentManager(), "Add Kid");
            return true;
        }
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
}
