package com.track.cylinderdelivery.ui.salesorder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SalesOrderDetailActivity extends AppCompatActivity {

    private ImageView btnCancel,btnDelete,btnReport,btnEdit;
    private RelativeLayout rvBlank;
    private TextView txtSoNumber,txtUserName,txtDNNumber,txtStatus,txtCreatedDate,txtDriverName,txtVehicleno;
    LinearLayout lvDriverName,lvVehicalNo;
    private TextView txtCreatedBy;
    private HashMap<String, String> mapdata;
    private Context context;
    String alretString;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    private SharedPreferences settings;
    private SharedPreferences CompanyUpdate;
    private SharedPreferences spSorting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_detail);
        context=this;
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("editData");
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        CompanyUpdate=context.getSharedPreferences("SOFilter",MODE_PRIVATE);
        btnCancel=findViewById(R.id.btnCancel);
        btnDelete=findViewById(R.id.btnDelete);
        btnReport=findViewById(R.id.btnReport);
        btnEdit=findViewById(R.id.btnEdit);
        rvBlank=findViewById(R.id.rvBlank);
        txtSoNumber=findViewById(R.id.txtSoNumber);
        txtUserName=findViewById(R.id.txtUserName);
        txtDNNumber=findViewById(R.id.txtDNNumber);
        txtStatus=findViewById(R.id.txtStatus);
        txtCreatedBy=findViewById(R.id.txtCreatedBy);
        txtCreatedDate=findViewById(R.id.txtCreatedDate);
        txtDriverName=findViewById(R.id.txtDriverName);
        txtVehicleno=findViewById(R.id.txtVehicleno);
        lvDriverName=findViewById(R.id.lvDriverName);
        lvVehicalNo=findViewById(R.id.lvVehicalNo);
        alretString="You are sure want to delete Client Delivery Note?";
        spSorting=context.getSharedPreferences("SOFilter",MODE_PRIVATE);

        txtSoNumber.setText(MySingalton.convertString(mapdata.get("soNumber")));
        txtUserName.setText(MySingalton.convertString(mapdata.get("username")));
        txtDNNumber.setText(MySingalton.convertString(mapdata.get("quantity")));
        txtStatus.setText(MySingalton.convertString(mapdata.get("status")));
        txtCreatedBy.setText(MySingalton.convertString(mapdata.get("soGeneratedBy")));
        txtCreatedDate.setText(MySingalton.convertString(mapdata.get("strDNDate")));

        if(mapdata.get("status").equals("Completed")){
            btnEdit.setVisibility(View.GONE);

        }else {
            btnEdit.setVisibility(View.VISIBLE);

        }
        if(mapdata.get("isDeletable").equals("true")){
            btnDelete.setVisibility(View.VISIBLE);

        }else {
            btnDelete.setVisibility(View.GONE);

        }
        txtDriverName.setText(mapdata.get("driverName"));
        if(mapdata.get("driverName").equals("") || mapdata.get("driverName").equals("null") ||
                mapdata.get("driverName").equals(null)){
            txtDriverName.setVisibility(View.GONE);
            lvDriverName.setVisibility(View.GONE);
        }else {
            txtDriverName.setVisibility(View.VISIBLE);
            lvDriverName.setVisibility(View.VISIBLE);
        }
        txtVehicleno.setText(mapdata.get("driverVehicleNo"));
        if(mapdata.get("driverVehicleNo").equals("") || mapdata.get("driverVehicleNo").equals("null") ||
                mapdata.get("driverVehicleNo").equals(null)){
            txtVehicleno.setVisibility(View.GONE);
            lvVehicalNo.setVisibility(View.GONE);
        }else {
            txtVehicleno.setVisibility(View.VISIBLE);
            lvVehicalNo.setVisibility(View.VISIBLE);
        }

        rvBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, EditSalesOrderActivity.class);
                intent.putExtra("editData",mapdata);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);
            }
        });
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri url=Uri.parse(MySingalton.getInstance().URL+"/Api/MobSalesOrder/PrintSO?Id="+
                        mapdata.get("soId"));
                Log.d("user==>",url+"");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(MySingalton.getInstance().URL+"/Api/MobSalesOrder/PrintSO?Id="+
                                mapdata.get("soId")));
                startActivity(browserIntent);
            }
        });
        AlertDialog.Builder adb = new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        //adb.setView(alertDialogView);
        adb.setTitle(alretString);
        adb.setIcon(R.drawable.ic_baseline_device_unknown_24);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(isNetworkConnected()){
                    //finish();
                    callDelete();
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
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adb.show();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(spSorting.getBoolean("sofilter",false)){
            finish();
        }
    }

    private void callDelete() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobSalesOrder/DeleteSO?SOId="+mapdata.get("soId")+
                "&UserId="+settings.getString("userId","1")+"&CompanyId="+settings.getString("companyId","1")+
                "&UserType="+settings.getString("userType","Admin");
        Log.d("request==>",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                progressDialog.dismiss();
                Log.d("resonse ==>",Response+"");
                JSONObject j;
                try {
                    j = new JSONObject(Response);
                    String message = null;
                    if(j.getBoolean("status")){
                        message=j.getString("message");
                    }else {
                        try {
                            message = j.getString("message");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = CompanyUpdate.edit();
                    editor.putBoolean("sofilter",true);
                    editor.commit();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDialog.dismiss();
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                Log.d("error==>",message+"");
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap map=new HashMap();
                map.put("content-type","application/json");
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}