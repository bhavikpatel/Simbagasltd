package com.track.cylinderdelivery.ui.purchaseorder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAddListAdapter extends RecyclerView.Adapter<ProductAddListAdapter.ViewHolder>{

    ArrayList<HashMap<String, String>> podetailList;
    AddPurchaseOrderActivity context;
    public ProductAddListAdapter(ArrayList<HashMap<String, String>> dataList, AddPurchaseOrderActivity activity) {
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
                    int pos= (int) imgArrow.getTag ();
                    /*Intent intent=new Intent(context, CylinderDetailActivity.class);
                    intent.putExtra("editData",cylinderList.get(pos));
                    context.startActivity(intent);
                    context.overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);*/
                    AlertDialog.Builder adb = new AlertDialog.Builder(context,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    //adb.setView(alertDialogView);
                    adb.setTitle("You are sure won't be Delete this PO Detail!");
                    adb.setIcon(R.drawable.ic_baseline_delete_24);
                    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(isNetworkConnected()){
                                context.callChangeCompanyStatus(podetailList.get(pos).get("podetailid"));
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
                    adb.setTitle("You are sure won't be Delete this PO Detail!");
                    adb.setIcon(R.drawable.ic_baseline_delete_24);
                    adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(isNetworkConnected()){
                                context.callChangeCompanyStatus(podetailList.get(pos).get("podetailid"));
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
    public ProductAddListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_podetail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAddListAdapter.ViewHolder holder, int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.imgArrow.setTag(position);
        holder.rv_row.setTag(position);
        holder.txtProductName.setText(podetailList.get(position).get("productName"));
        holder.txtQuantity.setText("Quantity: "+ podetailList.get(position).get("Quantity"));
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
