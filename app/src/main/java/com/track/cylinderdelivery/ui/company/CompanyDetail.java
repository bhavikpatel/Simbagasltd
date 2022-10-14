package com.track.cylinderdelivery.ui.company;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyDetail extends AppCompatActivity {

    Context context;
    RelativeLayout rvBlank;
    ImageView btnCancel,btnEdit;
    TextView txtCompanyName,txtAdminName,txtAddress,txtPinNumber,txtPhoneNo,txtEmail,txtCreatedby;
    TextView txtcompanyAlias,txtPermonthReq,txtHoldingCapacity,txtHoldingcreditday;
    TextView txtCreatedDate,txtCompanytype,txtCompanyCategory,txtSecondaryEmail;
    TextView txtSecondaryMobile,txtReferenceby,txtDepositAmount,txtStatus;
    private HashMap<String, String> mapdata;
    ImageView btnActive;
    String CompanyStatus;
    String alretString;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    private SharedPreferences settings,CompanyUpdate;
    private String Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("editData");
        context=this;
        rvBlank=(RelativeLayout)findViewById(R.id.rvBlank);
        btnCancel=(ImageView)findViewById(R.id.btnCancel);
        btnActive=(ImageView)findViewById(R.id.btnActive);
        btnEdit=findViewById(R.id.btnEdit);
        txtCompanyName=findViewById(R.id.txtCompanyName);
        txtAdminName=findViewById(R.id.txtAdminName);
        txtcompanyAlias=findViewById(R.id.txtcompanyAlias);
        txtAddress=findViewById(R.id.txtAddress);
        txtPinNumber=findViewById(R.id.txtPinNumber);
        txtPhoneNo=findViewById(R.id.txtPhoneNo);
        txtSecondaryEmail=findViewById(R.id.txtSecondaryEmail);
        txtEmail=findViewById(R.id.txtEmail);
        txtSecondaryMobile=findViewById(R.id.txtSecondaryMobile);
        txtPermonthReq=findViewById(R.id.txtPermonthReq);
        txtCreatedby=findViewById(R.id.txtCreatedby);
        txtCreatedDate=findViewById(R.id.txtCreatedDate);
        txtCompanytype=findViewById(R.id.txtCompanytype);
        txtHoldingcreditday=findViewById(R.id.txtHoldingcreditday);
        txtDepositAmount=findViewById(R.id.txtDepositAmount);
        txtStatus=findViewById(R.id.txtStatus);
        txtReferenceby=findViewById(R.id.txtReferenceby);
        txtHoldingCapacity=findViewById(R.id.txtHoldingCapacity);
        txtCompanyCategory=findViewById(R.id.txtCompanyCategory);
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        CompanyUpdate=context.getSharedPreferences("companyUpdate",MODE_PRIVATE);

        txtCompanyName.setText(MySingalton.convertString(mapdata.get("companyName")));
        txtAdminName.setText(MySingalton.convertString(mapdata.get("adminName")));
        txtcompanyAlias.setText(MySingalton.convertString(mapdata.get("companyAlias")));
        txtCompanytype.setText(MySingalton.convertString(mapdata.get("companyType")));
        txtPermonthReq.setText(MySingalton.convertString(mapdata.get("perMonthRequirement")));
        txtCompanyCategory.setText(MySingalton.convertString(mapdata.get("companyCategory")));
        txtHoldingCapacity.setText(MySingalton.convertString(mapdata.get("holdingCapacity")));
        txtHoldingcreditday.setText(MySingalton.convertString(mapdata.get("cylinderHoldingCreditDays")));
        txtSecondaryEmail.setText(MySingalton.convertString(mapdata.get("secondaryEmail")));
        txtSecondaryMobile.setText(MySingalton.convertString(mapdata.get("secondaryPhone")));
        txtReferenceby.setText(MySingalton.convertString(mapdata.get("referenceBy")));
        txtDepositAmount.setText(MySingalton.convertString(mapdata.get("depositAmount")));
        txtStatus.setText(MySingalton.convertString(mapdata.get("status")));

        String address2=mapdata.get("address2");
        if(address2.isEmpty() || address2.equals("null")){
            address2="";
        }else {
            address2+=",";
        }
        txtAddress.setText(mapdata.get("address1")+","+address2+mapdata.get("city")+","+mapdata.get("county")+","+mapdata.get("zipCode"));

        txtPinNumber.setText(MySingalton.convertString(mapdata.get("taxNumber")));

/*        String secondaryPhone=MySingalton.convertString(mapdata.get("secondaryPhone"));
        if(secondaryPhone.isEmpty() || secondaryPhone.equals("null")){
            secondaryPhone="";
        }else {
            secondaryPhone=","+secondaryPhone;
        }*/

        txtPhoneNo.setText(MySingalton.convertString(mapdata.get("phone")));

/*        String secondaryEmail=mapdata.get("secondaryEmail");
        if(secondaryEmail.isEmpty() || secondaryEmail.equals("null")){
            secondaryEmail="";
        }else {
            secondaryEmail=","+secondaryEmail;
        }*/
        txtEmail.setText(MySingalton.convertString(mapdata.get("email")));
        txtCreatedby.setText(MySingalton.convertString(mapdata.get("createdByName")));
        txtCreatedDate.setText(MySingalton.convertString(mapdata.get("createdDateStr")));

        CompanyStatus=mapdata.get("status");
        if(CompanyStatus.equals("Active")){
            btnActive.setImageResource(R.drawable.ic_baseline_unpublished_24);
            alretString="You are sure want to In-Active Distributor?";
            Status="InActive";
        }else {
            btnActive.setImageResource(R.drawable.ic_baseline_public_24);
            alretString="You are sure want to Active Distributor?";
            Status="Active";
        }

        AlertDialog.Builder adb = new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        //adb.setView(alertDialogView);
        adb.setTitle(alretString);
        adb.setIcon(R.drawable.ic_baseline_device_unknown_24);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(isNetworkConnected()){
                    callChangeCompanyStatus();
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

        btnActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adb.show();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,EditCompany.class);
                intent.putExtra("editData",mapdata);
                startActivity(intent);
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
    private void callChangeCompanyStatus() {
        {
            Log.d("Api Calling==>","Api Calling");
            final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
            progressDialog.show();
            String url = MySingalton.getInstance().URL+"/Api/MobCompany/ChangeCompanyStatus?Id="+mapdata.get("companyId")+"&Status="+Status+"&UserId="+settings.getString("userId","1");
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
                        editor.putBoolean("refresh",true);
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