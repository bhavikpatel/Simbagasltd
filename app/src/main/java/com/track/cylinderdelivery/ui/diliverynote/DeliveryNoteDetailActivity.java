package com.track.cylinderdelivery.ui.diliverynote;

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
import com.track.cylinderdelivery.utils.WebViewActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DeliveryNoteDetailActivity extends AppCompatActivity {

    private TextView txtDNNumber,txtAllocatedEmployee,txtQtyofCylinder;
    private TextView txtStatus,txtCreatedBy,txtCreatedDate;
    private HashMap<String, String> mapdata;
    private ImageView btnCancel,btnDelete,btnReport,btnEdit;
    private RelativeLayout rvBlank;
    private Context context;
    String alretString;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    private SharedPreferences settings;
    private SharedPreferences CompanyUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_note_detail);
        context=this;
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("editData");
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        CompanyUpdate=context.getSharedPreferences("DNFilter",MODE_PRIVATE);
        txtDNNumber=findViewById(R.id.txtDNNumber);
        txtAllocatedEmployee=findViewById(R.id.txtAllocatedEmployee);
        txtQtyofCylinder=findViewById(R.id.txtQtyofCylinder);
        txtStatus=findViewById(R.id.txtStatus);
        txtCreatedBy=findViewById(R.id.txtCreatedBy);
        txtCreatedDate=findViewById(R.id.txtCreatedDate);
        btnCancel=findViewById(R.id.btnCancel);
        rvBlank=findViewById(R.id.rvBlank);
        btnDelete=findViewById(R.id.btnDelete);
        btnReport=findViewById(R.id.btnReport);
        btnEdit=findViewById(R.id.btnEdit);
        alretString="You are sure want to delete Delivery Note?";

        txtDNNumber.setText(MySingalton.convertString(mapdata.get("dnNumber")));
        txtAllocatedEmployee.setText(MySingalton.convertString(mapdata.get("username")));
        txtQtyofCylinder.setText(MySingalton.convertString(mapdata.get("quantity")));
        txtStatus.setText(MySingalton.convertString(mapdata.get("status")));
        txtCreatedBy.setText(MySingalton.convertString(mapdata.get("dnGeneratedBy")));
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

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     if(mapdata.get("status").equals("Draft")){
                        Intent intent=new Intent(context, EditDeliveryNoteActivity.class);
                        intent.putExtra("editData",mapdata);
                        context.startActivity(intent);
                        overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);
                        finish();
                    }else if(mapdata.get("status").equals("Pending")){
                        Intent intent=new Intent(context, DNCylinderActivity.class);
                        intent.putExtra("editData",mapdata);
                        context.startActivity(intent);
                        overridePendingTransition(R.anim.enter_from_bottom, R.anim.hold_top);
                        finish();
                    }
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Uri url1=Uri.parse(MySingalton.getInstance().URL+"/Api/MobDeliveryNote/PrintDN?Id="+
                        mapdata.get("dnId"));
                Log.d("user==>",url1+"");
                 Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(MySingalton.getInstance().URL+"/Api/MobDeliveryNote/PrintDN?Id="+
                                mapdata.get("dnId")));
                startActivity(browserIntent);

                /*String url=MySingalton.getInstance().URL+"/Api/MobDeliveryNote/PrintDN?Id="+
                        mapdata.get("dnId");
                Intent intent=new Intent(context, WebViewActivity.class);
                intent.putExtra("URL",url);
                startActivity(intent);*/
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
        rvBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void callDelete() {
        {
            Log.d("Api Calling==>","Api Calling");
            final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
            progressDialog.show();
            String url = MySingalton.getInstance().URL+"/Api/MobDeliveryNote/DeleteDN?DNId="+mapdata.get("dnId")+
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}