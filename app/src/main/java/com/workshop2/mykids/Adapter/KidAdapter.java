package com.workshop2.mykids.adapter;

/**
 * Created by MingHan on 1/10/2016.
 */

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.workshop2.mykids.KidDetailActivity;
import com.workshop2.mykids.model.Kid;
import com.workshop2.mykids.R;
import com.workshop2.mykids.ScheduleActivity;

import java.util.ArrayList;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class KidAdapter extends RecyclerView.Adapter<KidAdapter.MyViewHolder> {

    Context mContext;
    private ArrayList<Kid> kids;
    private Kid kid;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, count, card_kid, card_gender;
        public ImageView thumbnail, overflow;
        public Button editButton;
        public LinearLayout kid_cardHolder;
        private View v;

        public MyViewHolder(View view) {
            super(view);
            this.v = view;

            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            card_kid = (TextView) view.findViewById(R.id.card_kid);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            card_gender = (TextView)view.findViewById(R.id.card_gender);
            kid_cardHolder = (LinearLayout)view.findViewById(R.id.kid_cardHolder);
        }

//        @Override
//        public void onClick(View v) {
//            if (mItemClickListener != null) {
//                mItemClickListener.onItemClick(itemView, getPosition());
//            }
//        }
    }


    public KidAdapter (Context mContext, ArrayList<Kid> kids) {
        this.mContext = mContext;
        this.kids = kids;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_child, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        kid = kids.get(position);
        holder.title.setText(kid.getKid_name());
        holder.count.setText(kid.getKid_date());
        holder.card_kid.setText(kid.getKid_id());
        holder.card_gender.setText(kid.getKid_gender());

        final Context context = mContext;

        Glide.with(mContext)
                .load(kid.getKid_image())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ScheduleActivity.class);
                intent.putExtra("kimage", kids.get(position).getKid_image());
                intent.putExtra("kname", kids.get(position).getKid_name());
                intent.putExtra("kdate", kids.get(position).getKid_date());
                intent.putExtra("kgender", kids.get(position).getKid_gender());
                intent.putExtra("kid", kids.get(position).getKid_id());

                //pass arraylist
                //make sure Object class implement Serializable
//                intent.putExtra("schedule", kids.get(position).getSchedule());

//                ActivityOptionsCompat options = ActivityOptionsCompat.
//                        makeSceneTransitionAnimation((Activity)mContext, holder.kid_cardHolder, "kidHolder");
//                context.startActivity(intent, options.toBundle());
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)mContext);
//                context.startActivity(intent, options.toBundle());
                context.startActivity(intent);
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });
    }

    private void showPopupMenu(View view, int pos) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.kid_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(pos));
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int itemPos;
        public MyMenuItemClickListener(int index) {
            itemPos = index;
            System.out.println("AAAA"+index);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.update:
                    Intent intent = new Intent(mContext, KidDetailActivity.class);
                    intent.putExtra("kimage", kids.get(itemPos).getKid_image());
                    intent.putExtra("kname", kids.get(itemPos).getKid_name());
                    intent.putExtra("kdate", kids.get(itemPos).getKid_date());
                    intent.putExtra("kgender", kids.get(itemPos).getKid_gender());
                    intent.putExtra("kid", kids.get(itemPos).getKid_id());
                    intent.putExtra("kstate", kids.get(itemPos).getKid_state());
                    mContext.startActivity(intent);
                    return true;

                case R.id.delete:
                    System.out.println("Delete item "+itemPos+" "+kids.get(itemPos).getKid_name());
                    deleteKid(kids.get(itemPos).getKid_id());
                    kids.remove(itemPos);
                    notifyDataSetChanged();
                    return true;
                default:
            }
            return true;
        }
    }

    @Override
    public int getItemCount() {
        return kids.size();
    }

    public void deleteKid(String id){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.keepSynced(true);
        databaseReference.child("kid").child(id).removeValue();
        Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
//        mRef.child("kid").child(id).runTransaction(new Transaction.Handler() {
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                mutableData.setValue(null); // This removes the node.
//                return Transaction.success(mutableData);
//            }
//            public void onComplete(FirebaseError error, boolean b, DataSnapshot data) {
//                Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
//            }
//        });

//        String url = "http://10.0.2.2:80/mykids/kidDelete.php?k_id="+id;
//
//        RequestQueue queue = Volley.newRequestQueue(mContext);
//        // prepare the Request
//        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
////                        progressDialog.dismiss();
//                        try {
//                            boolean success = response.getBoolean("success");
//                            // display response
//                            if (success) {
//                                CharSequence text = "Record had been deleted successfully.";
//                                int duration = Toast.LENGTH_LONG;
//                                Toast toast = Toast.makeText(mContext, text, duration);
//                                toast.show();
//                            } else {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                                builder.setMessage("Delete failed")
//                                        .setNegativeButton("Retry", null)
//                                        .create()
//                                        .show();
//                            }
//                        } catch (JSONException e) {
////                            progressDialog.dismiss();
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
////                        progressDialog.dismiss();
//                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                        builder.setMessage("Delete failed")
//                                .setNegativeButton("Retry", null)
//                                .create()
//                                .show();
//                    }
//                }
//        );
//
//        // add it to the RequestQueue
//        queue.add(getRequest);
    }

//    public interface OnItemClickListener {
//        void onItemClick(View view, int position);
//    }
//
//    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
//        this.mItemClickListener = mItemClickListener;
//    }

}
