package com.workshop2.mykids;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.workshop2.mykids.adapter.KidAdapter;
import com.workshop2.mykids.model.Kid;
import com.workshop2.mykids.task.KidAsyncTask;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link KidFragment.OnKidFragmentListener} interface
 * to handle interaction events.
 * Use the {@link KidFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KidFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnKidFragmentListener mListener;
    public static RecyclerView recyclerView;
    private ArrayList<Kid> kidList;
    private KidAdapter kidAdapter;
    private LinearLayout layoutEmpty;

    public KidFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment KidFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KidFragment newInstance(String param1, String param2) {
        KidFragment fragment = new KidFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_kid, container, false);
        layoutEmpty = (LinearLayout)view.findViewById(R.id.layoutEmpty);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            kidAdapter = new KidAdapter(getContext(), kidList);
            recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
//            recyclerView.setAdapter(kidAdapter);
        }

        setupWindowAnimations();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new KidAsyncTask(getContext(), kidAdapter, recyclerView, layoutEmpty).execute();
//        mListener.enableCollapse();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnKidFragmentListener) {
            mListener = (OnKidFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnKidFragmentListener");
        }
    }

//    KidAdapter.OnItemClickListener onItemClickListener = new KidAdapter.OnItemClickListener() {
//        @Override
//        public void onItemClick(View view, int position) {
//            Intent intent = new Intent(getActivity(), KidDetailActivity.class);
//            ImageView imageView = (ImageView) view.findViewById(R.id.thumbnail);
//            intent.putExtra("kimage", kidList.get(position).getKid_image());
//            intent.putExtra("kname", kidList.get(position).getKid_name());
//            intent.putExtra("kdate", kidList.get(position).getKid_date());
//            intent.putExtra("kgender", kidList.get(position).getKid_gender());
//            intent.putExtra("kid", kidList.get(position).getKid_id());
//
//            ActivityOptionsCompat options = ActivityOptionsCompat.
//                    makeSceneTransitionAnimation(getActivity(), view, "kid_thumbnail");
//            ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
//
//        }
//    };

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public interface OnKidFragmentListener {
//        void enableCollapse();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("MyKids");
    }

//    public ArrayList<Kid> loadKids(FirebaseUser user){
//        Firebase.setAndroidContext(getContext());
//        mRef = new Firebase("https://fir-mykids.firebaseio.com/");
//        kidList = new ArrayList<Kid>();
//        Firebase userRef = mRef.child("User").child(user.getUid()).child("kid");
//        userRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                kidList.clear();
//                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                    Kid kid = postSnapshot.getValue(Kid.class);
//                    kidList.add(kid);
//                }
//                kidAdapter.notifyDataSetChanged();
//                System.out.println("NOTIFY DATA SET CHANGED");
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//                System.out.println("The read failed: " + firebaseError.getMessage());
//            }
//        });
//        return kidList;
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

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
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

    private void setupWindowAnimations() {
        Transition exitTrans = new Explode();
        getActivity().getWindow().setExitTransition(exitTrans);

        Transition reenterTrans = new Slide();
        getActivity().getWindow().setReenterTransition(reenterTrans);
    }
}
