package com.track.cylinderdelivery.ui.user;

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
import android.view.WindowManager;
import android.widget.Button;
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

public class UserDetailActivity extends AppCompatActivity {

    RelativeLayout rvBlank;
    TextView txtAdminName,txtAddress,txtPinNumber,txtPhoneNo;
    TextView txtEmail,txtCreatedby,txtCreatedDate,txtCompanyName;
    TextView txtNameofCompany,txtCompanyCategory,txtSecPhoneNo;
    TextView txtSecdEmail,txtPermonthReq,txtHoldingCapacity,txtHoldingcreditday;
    TextView txtReferenceby,txtDepositAmount,txtStatus;
    private HashMap<String, String> mapdata;
    Context context;
    ImageView btnCancel,btnAckno,btnEdit,btnActive;
    LinearLayout lvCompanyName;
    private String CompanyStatus;
    private String alretString;
    private String Status;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    private SharedPreferences settings,CompanyUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_detail);
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("editData");
        context=this;
        rvBlank=findViewById(R.id.rvBlank);
        txtCompanyName=findViewById(R.id.txtCompanyName);
        txtAdminName=findViewById(R.id.txtAdminName);
        txtAddress=findViewById(R.id.txtAddress);
        txtPinNumber=findViewById(R.id.txtPinNumber);
        txtPhoneNo=findViewById(R.id.txtPhoneNo);
        txtEmail=findViewById(R.id.txtEmail);
        txtSecdEmail=findViewById(R.id.txtSecdEmail);
        txtCreatedby=findViewById(R.id.txtCreatedby);
        txtCreatedDate=findViewById(R.id.txtCreatedDate);
        btnCancel=findViewById(R.id.btnCancel);
        btnAckno=findViewById(R.id.btnAckno);
        btnEdit=findViewById(R.id.btnEdit);
        lvCompanyName=findViewById(R.id.lvCompanyName);
        btnActive=findViewById(R.id.btnActive);
        txtNameofCompany=findViewById(R.id.txtNameofCompany);
        txtCompanyCategory=findViewById(R.id.txtCompanyCategory);
        txtSecPhoneNo=findViewById(R.id.txtSecPhoneNo);
        txtPermonthReq=findViewById(R.id.txtPermonthReq);
        txtHoldingCapacity=findViewById(R.id.txtHoldingCapacity);
        txtHoldingcreditday=findViewById(R.id.txtHoldingcreditday);
        txtReferenceby=findViewById(R.id.txtReferenceby);
        txtDepositAmount=findViewById(R.id.txtDepositAmount);
        txtStatus=findViewById(R.id.txtStatus);
        CompanyUpdate=context.getSharedPreferences("userFilter",MODE_PRIVATE);
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);

        CompanyStatus=mapdata.get("status");
        if(CompanyStatus.equals("Active")){
            btnActive.setImageResource(R.drawable.ic_baseline_unpublished_24);
            alretString="You are sure want to In-Active Company?";
            Status="InActive";
        }else {
            btnActive.setImageResource(R.drawable.ic_baseline_public_24);
            alretString="You are sure want to Active Company?";
            Status="Active";
        }
        txtCompanyName.setText(MySingalton.convertString(mapdata.get("fullName"))+"");
        /*if(mapdata.get("companyName").equals("null") || mapdata.get("companyName").equals("")){
            txtAdminName.setVisibility(View.GONE);
            lvCompanyName.setVisibility(View.GONE);
        }*/
        txtAdminName.setText(MySingalton.convertString(mapdata.get("userAlias"))+"");
        String address=mapdata.get("address1")+","+mapdata.get("address2")+
                ","+mapdata.get("city")+","+mapdata.get("county");
        txtAddress.setText(address);
        txtNameofCompany.setText(MySingalton.convertString(mapdata.get("nameOfCompany")));
        txtPinNumber.setText(MySingalton.convertString(mapdata.get("taxNumber"))+"");
        txtPhoneNo.setText(MySingalton.convertString(mapdata.get("phone"))+"");
        txtEmail.setText(MySingalton.convertString(mapdata.get("email"))+"");
        txtCreatedby.setText(MySingalton.convertString(mapdata.get("createdByName"))+"");
        txtCreatedDate.setText(MySingalton.convertString(mapdata.get("createdDateStr"))+"");
        txtSecdEmail.setText(MySingalton.convertString(mapdata.get("secondaryEmail"))+"");
        txtCompanyCategory.setText(MySingalton.convertString(mapdata.get("companyCategory")));
        txtSecPhoneNo.setText(MySingalton.convertString(mapdata.get("secondaryPhone")));
        txtPermonthReq.setText(MySingalton.convertString(mapdata.get("perMonthRequirement")));
        txtHoldingCapacity.setText(MySingalton.convertString(mapdata.get("holdingCapacity")));
        txtHoldingcreditday.setText(MySingalton.convertString(mapdata.get("cylinderHoldingCreditDays")));
        txtReferenceby.setText(MySingalton.convertString(mapdata.get("referenceBy")));
        txtDepositAmount.setText(MySingalton.convertString(mapdata.get("depositAmount")));
        txtStatus.setText(MySingalton.convertString(mapdata.get("status")));

        if(mapdata.get("accNo").equals("0") && (!mapdata.get("userType").equals("Employee"))){
            btnAckno.setVisibility(View.VISIBLE);
        }else {
            btnAckno.setVisibility(View.GONE);
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
        btnAckno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,UserAcknoActivity.class);
                intent.putExtra("editData",mapdata);
                startActivity(intent);
                finish();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,EditUserActivity.class);
                intent.putExtra("editData",mapdata);
                startActivity(intent);
                finish();
            }
        });
        rvBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
  //      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
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
            //Api/MobUser/UserStatus?Id=2&Status=Active&UserId=1
            String url = MySingalton.getInstance().URL+"/Api/MobUser/ChangeUserStatus?Id="+mapdata.get("userId")+"&Status="+Status+
                    "&UserId="+settings.getString("userId","1");
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
                    progressDialog.dismiss();
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