package com.track.cylinderdelivery.ui.purchaseorder;

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

public class PurchaseOrderDetailActivity extends AppCompatActivity {

    private HashMap<String, String> mapdata;
    private RelativeLayout rvBlank;
    private ImageView btnCancel,btnEdit,btnReport,btnDelete;
    private TextView txtPoNumber,txtUserName,txtClientPOReference;
    private TextView txtStatus,txtCreatedBy,txtCreatedDate;
    private Context context;
    String alretString;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    private SharedPreferences settings;
    private SharedPreferences CompanyUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_order_detail);
        context=this;
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("editData");
        CompanyUpdate=context.getSharedPreferences("POFilter",MODE_PRIVATE);
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        rvBlank=(RelativeLayout)findViewById(R.id.rvBlank);
        btnCancel=(ImageView)findViewById(R.id.btnCancel);
        txtPoNumber=findViewById(R.id.txtPoNumber);
        txtUserName=findViewById(R.id.txtUserName);
        txtClientPOReference=findViewById(R.id.txtClientPOReference);
        txtStatus=findViewById(R.id.txtStatus);
        txtCreatedBy=findViewById(R.id.txtCreatedBy);
        txtCreatedDate=findViewById(R.id.txtCreatedDate);
        btnEdit=findViewById(R.id.btnEdit);
        btnReport=findViewById(R.id.btnReport);
        btnDelete=findViewById(R.id.btnDelete);
        alretString="You are sure want to delete Purchase Order?";

        txtPoNumber.setText(mapdata.get("poNumber"));
        txtUserName.setText(mapdata.get("username"));
        txtClientPOReference.setText(MySingalton.convertString(mapdata.get("clientPOReference")));
        txtStatus.setText(mapdata.get("status"));
        txtCreatedBy.setText(mapdata.get("poGeneratedBy"));
        txtCreatedDate.setText(mapdata.get("strPODate"));

        if(mapdata.get("status").equals("Draft")){
            btnEdit.setVisibility(View.VISIBLE);
        }else {
            btnEdit.setVisibility(View.GONE);
        }
        /*if(mapdata.get("").equals("")){

        }*/

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
                ///Api/MobPurchaseOrder/DeletePO?POId=1&UserId=1&CompanyId=1&UserType=Admin
                adb.show();
            }
        });
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri url=Uri.parse(MySingalton.getInstance().URL+"/Api/MobPurchaseOrder/PrintPO?Id="+
                        mapdata.get("poId"));
                Log.d("user==>",url+"");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(MySingalton.getInstance().URL+"/Api/MobPurchaseOrder/PrintPO?Id="+
                                mapdata.get("poId")));
                startActivity(browserIntent);
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, EditPurchaseOrderActivity.class);
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
            String url = MySingalton.getInstance().URL+"/Api/MobPurchaseOrder/DeletePO?POId="+mapdata.get("poId")+
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
                        editor.putBoolean("dofilter",true);
                        editor.putString("text1","");
                        editor.putString("text2","Decinding");
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