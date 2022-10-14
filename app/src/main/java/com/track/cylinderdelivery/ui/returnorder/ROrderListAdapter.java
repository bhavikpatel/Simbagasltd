package com.track.cylinderdelivery.ui.returnorder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ui.salesorder.EditSalesOrderActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ROrderListAdapter extends RecyclerView.Adapter<ROrderListAdapter.ViewHolder>{

    ArrayList<HashMap<String, String>> podetailList;
    Activity context;
    public ROrderListAdapter(ArrayList<HashMap<String, String>> dataList, Activity activity) {
        podetailList=dataList;
        context=activity;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPonumber;
        TextView txtUserName;
        ImageView imgArrow;
        TextView txtStatus;
        RelativeLayout rvBackground;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            txtPonumber = (TextView) view.findViewById(R.id.txtPonumber);
            txtUserName=(TextView)view.findViewById(R.id.txtUserName);
            imgArrow=(ImageView)view.findViewById(R.id.imgArrow);
            txtStatus=view.findViewById(R.id.txtStatus);
            rvBackground=(RelativeLayout)view.findViewById(R.id.rvBackground);
            rvBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= (int) rvBackground.getTag ();
                    if(podetailList.get(pos).get("status").equals("Draft")){
                        Intent intent=new Intent(context, EditReturnOrderActivity.class);
                        intent.putExtra("editData",podetailList.get(pos));
                        context.startActivity(intent);
                        context.overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);
                    }
                }
            });
            imgArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= (int) imgArrow.getTag ();
                    if(podetailList.get(pos).get("status").equals("Draft")){
                        Intent intent=new Intent(context, EditReturnOrderActivity.class);
                        intent.putExtra("editData",podetailList.get(pos));
                        context.startActivity(intent);
                        context.overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);
                    }
                }
            });
        }
    }



    @NonNull
    @Override
    public ROrderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_purchase_order_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ROrderListAdapter.ViewHolder holder, int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.imgArrow.setTag(position);
        holder.rvBackground.setTag(position);
        holder.txtPonumber.setText(podetailList.get(position).get("roNumber"));
        holder.txtUserName.setText("User name: "+podetailList.get(position).get("username")+"\n"+
                "Created by: "+podetailList.get(position).get("roGeneratedBy")+"\n"+
                "Created on: "+podetailList.get(position).get("strRODate"));
        holder.txtStatus.setText(podetailList.get(position).get("status"));
        if(podetailList.get(position).get("status").equals("Pending")){
            holder.imgArrow.setImageResource(R.drawable.ic_baseline_pending_24);
            holder.imgArrow.setVisibility(View.INVISIBLE);
            holder.txtStatus.setVisibility(View.VISIBLE);
        }else if(podetailList.get(position).get("status").equals("Draft")){
            holder.imgArrow.setImageResource(R.drawable.ic_baseline_edit_24);
            holder.imgArrow.setVisibility(View.VISIBLE);
            holder.txtStatus.setVisibility(View.VISIBLE);
        }else if(podetailList.get(position).get("status").equals("Completed")){
            holder.imgArrow.setImageResource(R.drawable.ic_baseline_edit_24);
            holder.imgArrow.setVisibility(View.INVISIBLE);
            holder.txtStatus.setVisibility(View.VISIBLE);
        }else {
            holder.imgArrow.setVisibility(View.GONE);
            holder.txtStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        //return localDataSet.length;
        return podetailList.size();
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
