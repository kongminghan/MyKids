package com.workshop2.mykids;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.workshop2.mykids.adapter.HospitalAdapter;
import com.workshop2.mykids.databinding.ActivityVaccineDetailBinding;
import com.workshop2.mykids.model.Hospital;
import com.workshop2.mykids.task.HospitalAsyncTask;

import java.util.ArrayList;

public class VaccineDetailActivity extends AppCompatActivity{

    ActivityVaccineDetailBinding binding;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView imageView;
    private TextView des, sym, fun;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vaccine_detail);
        binding.collapsingToolbar.setTitle(getIntent().getStringExtra("title"));
        binding.vDes.setText(getIntent().getStringExtra("func"));
        binding.vDis.setText(getIntent().getStringExtra("dis"));
        binding.vSym.setText(getIntent().getStringExtra("sym"));
        binding.vTitle.setText(getIntent().getStringExtra("name"));

        Glide.with(VaccineDetailActivity.this)
                .load(getIntent().getStringExtra("image"))
                .centerCrop()
                .into(binding.vaccineHeader);

        ArrayList<Hospital> hospitals = new ArrayList<>();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        HospitalAdapter adapter = new HospitalAdapter(this, hospitals, deviceWidth);

        HospitalAsyncTask hospitalAsyncTask = new HospitalAsyncTask(adapter, binding.rvHospital, this, deviceWidth);
        hospitalAsyncTask.execute();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        binding.rvHospital.setLayoutManager(layoutManager);
        binding.rvHospital.setHasFixedSize(true);
        binding.rvHospital.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        binding.rvHospital.setItemAnimator(new DefaultItemAnimator());
        binding.rvHospital.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

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
}
