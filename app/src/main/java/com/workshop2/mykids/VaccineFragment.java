package com.workshop2.mykids;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.workshop2.mykids.adapter.VaccineAdapter;
import com.workshop2.mykids.model.Vaccine;
import com.workshop2.mykids.task.VaccineAsyncTask;

import java.util.ArrayList;

public class VaccineFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnVaccineFragmentListener mListener;
    private RecyclerView recyclerView;
    private ArrayList<Vaccine> vaccines;
    private VaccineAdapter vaccineAdapter;

    public VaccineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VaccineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VaccineFragment newInstance(String param1, String param2) {
        VaccineFragment fragment = new VaccineFragment();
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
        View view  =  inflater.inflate(R.layout.fragment_vaccine, container, false);
//        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbar);
//        toolbar.setBackgroundColor(getResources().getColor(R.color.vaccine));
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
//        vaccines = getVaccines();
        vaccineAdapter = new VaccineAdapter(getContext(), vaccines);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(vaccineAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new VaccineAsyncTask(getActivity(), vaccineAdapter, recyclerView).execute();
//        mListener.disableCollapse();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVaccineFragmentListener) {
            mListener = (OnVaccineFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnVaccineFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnVaccineFragmentListener {
//        void disableCollapse();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Vaccine");
    }

//    private ArrayList<Vaccine> getVaccines(){
//        Firebase firebase = new Firebase("https://fir-mykids.firebaseio.com/")
//                .child("vaccine");
//        firebase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                vaccines.clear();
//                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                    Vaccine vaccine = postSnapshot.getValue(Vaccine.class);
//                    vaccines.add(vaccine);
//                }
//                vaccineAdapter.notifyDataSetChanged();
//                System.out.println("NOTIFY DATA SET CHANGED");
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//        return vaccines;
//    }
}
