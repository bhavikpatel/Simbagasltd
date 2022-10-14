package com.track.cylinderdelivery.ui.purchaseorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ui.cylinderproductmapping.DetailCylinderProductMapping;

import java.util.ArrayList;
import java.util.HashMap;

public class PurchaseOrderListAdapter extends RecyclerView.Adapter<PurchaseOrderListAdapter.ViewHolder>{

    ArrayList<HashMap<String, String>> podetailList;
    Activity context;
    public PurchaseOrderListAdapter(ArrayList<HashMap<String, String>> dataList, Activity activity) {
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
            txtPonumber = (TextView) view.findViewById(R.id.txtPonumber);
            txtUserName=(TextView)view.findViewById(R.id.txtUserName);
            imgArrow=(ImageView)view.findViewById(R.id.imgArrow);
            txtStatus=view.findViewById(R.id.txtStatus);
            rvBackground=(RelativeLayout)view.findViewById(R.id.rvBackground);
            rvBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= (int) rvBackground.getTag ();
                    Intent intent=new Intent(context, PurchaseOrderDetailActivity.class);
                    intent.putExtra("editData",podetailList.get(pos));
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);
                }
            });
            imgArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= (int) imgArrow.getTag ();
                    Intent intent=new Intent(context, PurchaseOrderDetailActivity.class);
                    intent.putExtra("editData",podetailList.get(pos));
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);
                }
            });
        }
    }



    @NonNull
    @Override
    public PurchaseOrderListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_purchase_order_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseOrderListAdapter.ViewHolder holder, int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.imgArrow.setTag(position);
        holder.rvBackground.setTag(position);
        holder.txtPonumber.setText(podetailList.get(position).get("poNumber"));
        /*String data="User name: "+ MySingalton.convertString(podetailList.get(position).get("username"))+"\n"+
                "Client PO Reference: "+MySingalton.convertString(podetailList.get(position).get(""))*/
        holder.txtUserName.setText(podetailList.get(position).get("username"));
        holder.txtStatus.setText(podetailList.get(position).get("status"));
        holder.imgArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
        if(podetailList.get(position).get("status").equals("Pending")){
                //holder.imgArrow.setImageResource(R.drawable.ic_baseline_pending_24);
           // holder.imgArrow.setVisibility(View.INVISIBLE);
            //holder.txtStatus.setVisibility(View.VISIBLE);
            holder.rvBackground.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        }else{// if(podetailList.get(position).get("status").equals("Draft")){
            //holder.imgArrow.setVisibility(View.VISIBLE);
            //holder.txtStatus.setVisibility(View.VISIBLE);
            holder.rvBackground.setBackgroundColor(context.getResources().getColor(R.color.white));
        }/*else {
            //holder.imgArrow.setVisibility(View.GONE);
            //holder.txtStatus.setVisibility(View.GONE);
            holder.rvBackground.setBackgroundColor(context.getResources().getColor(R.color.white));
        }*/
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
