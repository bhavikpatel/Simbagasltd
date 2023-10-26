package com.track.cylinderdelivery.ui.Reconciliation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ui.returnorder.AddReturnOrderActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class REconDetailListAdapter extends RecyclerView.Adapter<REconDetailListAdapter.ViewHolder>{

    ArrayList<HashMap<String, String>> podetailList;
    AddReconciliationActivity context;
    public REconDetailListAdapter(ArrayList<HashMap<String, String>> dataList, AddReconciliationActivity activity) {
        podetailList=dataList;
        context=activity;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName;
        TextView txtQuantity;
        ImageView imgArrow;
        RelativeLayout rv_row;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            txtProductName = (TextView) view.findViewById(R.id.txtProductName);
            txtQuantity=(TextView)view.findViewById(R.id.txtQuantity);
            imgArrow=(ImageView)view.findViewById(R.id.imgArrow);
            rv_row=(RelativeLayout)view.findViewById(R.id.rv_row);
            rv_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= (int) rv_row.getTag ();
                    /*Intent intent=new Intent(context, CylinderDetailActivity.class);
                    intent.putExtra("editData",cylinderList.get(pos));
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);*/
                    AlertDialog.Builder adb = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    //adb.setView(alertDialogView);
                    adb.setTitle("You are sure won't be Delete this Sales Order Detail!");
                    adb.setIcon(R.drawable.ic_baseline_delete_24);
                    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(isNetworkConnected()){
                                context.callChangeCompanyStatus(podetailList.get(pos).get("roDetailId"));
                            }else {
                                Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
                    adb.show();
                }
            });
            imgArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= (int) imgArrow.getTag ();
                    /*Intent intent=new Intent(context, CylinderDetailActivity.class);
                    intent.putExtra("editData",cylinderList.get(pos));
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);*/
                    AlertDialog.Builder adb = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    //adb.setView(alertDialogView);
                    adb.setTitle("You are sure won't be Delete this Sales Order Detail!");
                    adb.setIcon(R.drawable.ic_baseline_delete_24);
                    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(isNetworkConnected()){
                                context.callChangeCompanyStatus(podetailList.get(pos).get("roDetailId"));
                            }else {
                                Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });
                    adb.show();
                }
            });
        }
    }



    @NonNull
    @Override
    public REconDetailListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_podetail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull REconDetailListAdapter.ViewHolder holder, int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.imgArrow.setTag(position);
        holder.rv_row.setTag(position);
        holder.txtProductName.setText(podetailList.get(position).get("cylinderNo"));
        holder.txtQuantity.setText(podetailList.get(position).get("cylinderStatus"));
    }

    @Override
    public int getItemCount() {
        //return localDataSet.length;
        return podetailList.size();
    }
    @SuppressLint("MissingPermission")
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
