package com.workshop2.mykids;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.workshop2.mykids.adapter.HospitalAdapter;
import com.workshop2.mykids.model.Hospital;
import com.workshop2.mykids.model.Vaccine;
import com.workshop2.mykids.task.HospitalAsyncTask;

import java.util.ArrayList;

/**
 * Created by MingHan on 18/11/2016.
 */

public class VaccineBottomSheetDialog extends BottomSheetDialogFragment {

    private static String sName;
    private TextView vTitle, vDes, vDis, vSym;
    private ImageView vImg, vLetterView;
    private HospitalAdapter adapter;
    private RecyclerView rvHospital;

    public static VaccineBottomSheetDialog getInstance(String sName2){
        sName = sName2;
        return new VaccineBottomSheetDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.bottom_sheet_vaccine, container, false);
        vTitle = (TextView)view.findViewById(R.id.vTitle);
        vDes = (TextView)view.findViewById(R.id.v_des);
        vDis = (TextView)view.findViewById(R.id.v_dis);
        vSym = (TextView)view.findViewById(R.id.v_sym);
        vImg = (ImageView)view.findViewById(R.id.vImg);
        vLetterView = (ImageView)view.findViewById(R.id.vLetterView);
        rvHospital = (RecyclerView)view.findViewById(R.id.rv_Hospital);

        setVaccine();
        ArrayList<Hospital> hospitals = new ArrayList<>();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
//        final int height = displayMetrics.heightPixels;

        adapter = new HospitalAdapter(getContext(), hospitals, deviceWidth);

        HospitalAsyncTask hospitalAsyncTask = new HospitalAsyncTask(adapter, rvHospital, getContext(), deviceWidth);
        hospitalAsyncTask.execute();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.HORIZONTAL, false);
        rvHospital.setLayoutManager(layoutManager);
        rvHospital.setHasFixedSize(true);
        rvHospital.addItemDecoration(new VaccineBottomSheetDialog.GridSpacingItemDecoration(1, dpToPx(10), true));
        rvHospital.setItemAnimator(new DefaultItemAnimator());
        rvHospital.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }

    private void setVaccine(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("vaccines");
        mRef.keepSynced(true);

        final Vaccine[] vaccine = new Vaccine[1];

        if (user.getUid() != null) {
            mRef.orderByChild("vaccineAbb").equalTo(sName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        vaccine[0] = postSnapshot.getValue(Vaccine.class);
                    }
//                    Toast.makeText(ScheduleDetailActivity.this, vaccine.getVaccineFunction(), Toast.LENGTH_LONG).show();
                    vTitle.setText(vaccine[0].getVaccineName());
                    vDes.setText(vaccine[0].getVaccineFunction());
                    vDis.setText(vaccine[0].getVaccineDisease());
                    vSym.setText(vaccine[0].getVaccineDiseaseSymptom());

                    Glide.with(VaccineBottomSheetDialog.this)
                            .load(vaccine[0].getVaccineImage())
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .into(vImg);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int bgColor = getMatColor("600");
                            final TextDrawable drawable1 = TextDrawable.builder()
                                    .buildRound(sName.substring(0,1), bgColor); // radius in px
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    vLetterView.setImageDrawable(drawable1);

                                }
                            });
                        }
                    }).start();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

//    private ArrayList<Hospital> getHospitals(){
//        final ArrayList<Hospital> hospitals = new ArrayList<>();
//        Firebase mRef = new Firebase("https://fir-mykids.firebaseio.com/").child("hospitals");
//        mRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                hospitals.clear();
//                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                    Hospital hospital = postSnapshot.getValue(Hospital.class);
//                    hospitals.add(hospital);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//            }
//        });
//        return hospitals;
//    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
                outRect.top = spacing;
//                if (position < spanCount) { // top edge
//                    outRect.top = spacing;
//                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private int getMatColor(String typeColor)
    {
        int returnColor = Color.BLACK;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getActivity().getPackageName());

        if (arrayId != 0)
        {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.BLACK);
            colors.recycle();
        }
        return returnColor;
    }
}
