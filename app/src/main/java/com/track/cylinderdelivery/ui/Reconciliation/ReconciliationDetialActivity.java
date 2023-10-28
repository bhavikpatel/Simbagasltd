package com.track.cylinderdelivery.ui.Reconciliation;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import com.track.cylinderdelivery.ui.returnorder.EditReturnOrderActivity;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReconciliationDetialActivity extends AppCompatActivity {

    private HashMap<String, String> mapdata;
    private ImageView btnCancel,btnEdit,btnReport,btnDelete;
    private RelativeLayout rvBlank;
    private TextView txtPoNumber,txtUserName,txtCreatedBy,txtStatus,txtCreatedDate;
    private Context context;
    String alretString;
    private SharedPreferences settings;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    private SharedPreferences CompanyUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconciliation_detial);
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("editData");
        context=this;
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        CompanyUpdate=context.getSharedPreferences("ROFilter",MODE_PRIVATE);
        Log.d("data==>",mapdata.toString()+"");
        btnCancel=(ImageView)findViewById(R.id.btnCancel);
        rvBlank=(RelativeLayout)findViewById(R.id.rvBlank);
        txtPoNumber=(TextView)findViewById(R.id.txtPoNumber);
        txtUserName=findViewById(R.id.txtUserName);
        txtStatus=findViewById(R.id.txtStatus);
        txtCreatedBy=findViewById(R.id.txtCreatedBy);
        txtCreatedDate=findViewById(R.id.txtCreatedDate);
        btnEdit=findViewById(R.id.btnEdit);
        btnReport=findViewById(R.id.btnReport);
        btnDelete=findViewById(R.id.btnDelete);

        if(mapdata.get("isDeletable").equals("ture") || Boolean.parseBoolean(mapdata.get("isDeletable"))){
            btnDelete.setVisibility(View.VISIBLE);
        }else {
            btnDelete.setVisibility(View.GONE);
       }
        if(mapdata.get("isEditable").equals("isEditable") || Boolean.parseBoolean(mapdata.get("isEditable"))){
            btnEdit.setVisibility(View.VISIBLE);
        }else {
            btnEdit.setVisibility(View.GONE);
        }
        txtPoNumber.setText(mapdata.get("reconciliationNumber"));
        txtUserName.setText(mapdata.get("username"));
        txtStatus.setText(mapdata.get("status"));
        txtCreatedBy.setText(mapdata.get("createdBy"));
        txtCreatedDate.setText(mapdata.get("strReconciliationDate"));
        alretString="You are sure want to delete Return Order?";
        AlertDialog.Builder adb = new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

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
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///Api/MobPurchaseOrder/DeletePO?POId=1&UserId=1&CompanyId=1&UserType=Admin
                adb.show();
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri url=Uri.parse(MySingalton.getInstance().URL+"/Api/MobReconciliation/PrintREC?Id="+
                        mapdata.get("reconciliationId"));
                Log.d("user==>",url+"");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(MySingalton.getInstance().URL+"/Api/MobReconciliation/PrintREC?Id="+
                                mapdata.get("reconciliationId")));
                startActivity(browserIntent);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, EditReconciliationActivity.class);
                intent.putExtra("editData",mapdata);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rvBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    private void callDelete() {
        {
            Log.d("Api Calling==>","Api Calling");
            final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
            progressDialog.show();
            String url = MySingalton.getInstance().URL+"/Api/MobReconciliation/DeleteREC?RECId="+mapdata.get("reconciliationId")+
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
                        editor.putBoolean("refilter",true);
                        editor.putString("text1","");
                        editor.putString("text2","Decinding");
                        editor.apply();
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
    }
}