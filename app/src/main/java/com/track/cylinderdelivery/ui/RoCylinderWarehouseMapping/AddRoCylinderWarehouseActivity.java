package com.track.cylinderdelivery.ui.RoCylinderWarehouseMapping;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ui.CylinderWarehouseMapping.FilterEmptyCylActivity;
import com.track.cylinderdelivery.ui.CylinderWarehouseMapping.FilterFilledCylActivity;
import com.track.cylinderdelivery.ui.cylinder.CylinderQRActivity;
import com.track.cylinderdelivery.utils.SignatureActivity;
import com.track.cylinderdelivery.utils.SignatureActivityROCI;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AddRoCylinderWarehouseActivity extends AppCompatActivity {
    Context context;
    private ArrayList<HashMap<String, String>> userList;
    Button btnCancel,btnSubmit;
    TextView txtCylinderNos;
    ArrayList<String> qrcodeList;
    ImageView btnScanCylinders;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    NiceSpinner spUser,spToWarehouse;
    private int fromwharehouspos=-1;
    private int towarehousepos=-1;
    SharedPreferences spFilledFilter;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    SharedPreferences setting;
    private ArrayList<HashMap<String,String>> towhereHouseList;
    String SignImage="";
    private TextView txtSignaure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ro_cylinder_warehouse_mapping);
        context=this;
        setting= context.getSharedPreferences("setting",MODE_PRIVATE);
        //wharehouselist= (ArrayList<HashMap<String,String>>) getIntent().getSerializableExtra("wharehouselist");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Add RO Cylinder Warehouse Mapping");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Add RO Cylinder Warehouse Mapping</font>"));
        btnCancel=findViewById(R.id.btnCancel);
        txtCylinderNos=findViewById(R.id.txtCylinderNos);
        qrcodeList=new ArrayList<String>();
        btnScanCylinders=findViewById(R.id.btnScanCylinders);
        spUser=findViewById(R.id.spFromWarehouse);
        spToWarehouse=findViewById(R.id.spToWarehouse);
        btnSubmit=findViewById(R.id.btnSubmit);
        txtSignaure=findViewById(R.id.txtSignaure);
        spFilledFilter=context.getSharedPreferences("ROFilter",MODE_PRIVATE);

        call1GetActiveEmployeeData();

        txtSignaure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, SignatureActivityROCI.class);
                intent.putExtra("ROCI",0);
                startActivityForResult(intent,222);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    Intent intent=new Intent(context, SignatureActivityROCI.class);
                    intent.putExtra("ROCI",0);
                    startActivityForResult(intent,222);
               /*     if(isNetworkConnected()){
                            try {
                                callAddCylinderWarehouseMapping();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }*/
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor userFilterEditor = spFilledFilter.edit();
                userFilterEditor.putBoolean("refilter",true);
                if(fromwharehouspos>0){
                    userFilterEditor.putString("userId",userList.get(fromwharehouspos-1).get("userId"));
                }
                userFilterEditor.commit();
                finish();
            }
        });

        btnScanCylinders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddRoCylinderWarehouseActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }else {
                    openQrScan();
                }
            }
        });
        spUser.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                fromwharehouspos=position;
            }
        });
        spToWarehouse.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                towarehousepos=position;
            }
        });
    }

    private void call1GetActiveEmployeeData() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobROCylinderMapping/GetActiveEmployeeData?UserId="+
                Integer.parseInt(setting.getString("userId","0"))+
                "&CompanyId="+Integer.parseInt(setting.getString("companyId","0"))+
                "&UserType="+setting.getString("userType","");
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
                    if(j.getBoolean("status")){
                        JSONArray jsonArray=j.getJSONArray("data");
                        userList = new ArrayList<>();
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("userId", jsonArray.getJSONObject(i).getInt("userId")+"");
                            map.put("fullName", jsonArray.getJSONObject(i).getString("fullName") + "");
                            imtes.add(jsonArray.getJSONObject(i).getString("fullName") +"");
                            userList.add(map);
                        }
                        spUser.attachDataSource(imtes);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callGetWarehouseList1();
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
                callGetWarehouseList1();
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

    private void callAddCylinderWarehouseMapping() throws JSONException {
        Log.d("Api Calling==>","Api Calling");
        btnSubmit.setEnabled(false);
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL +"/Api/MobROCylinderMapping/AddEditUserCylinderMapping";
        Log.d("url==>",url);
        final JSONObject jsonBody=new JSONObject();
        SharedPreferences setting= getSharedPreferences("setting",MODE_PRIVATE);
       // jsonBody.put("FromWarehouseId",Integer.parseInt(wharehouselist.get(fromwharehouspos-1).get("warehouseId")));
     //   jsonBody.put("WarehouseId",Integer.parseInt(wharehouselist.get(towarehousepos-1).get("warehouseId")));
        JSONArray jsonArrayCylList=new JSONArray(qrcodeList);
        jsonBody.put("UserId",Integer.parseInt(userList.get(fromwharehouspos-1).get("userId")));
        jsonBody.put("WarehouseId",Integer.parseInt(towhereHouseList.get(towarehousepos-1).get("warehouseId")));
        jsonBody.put("CylinderList",jsonArrayCylList);
        jsonBody.put("CreatedBy",Integer.parseInt(setting.getString("userId","1")));
        jsonBody.put("TransferBy",setting.getString("fullName",""));
        jsonBody.put("SignImage",SignImage);
        Log.d("jsonRequest==>",jsonBody.toString()+"");
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,url,jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        btnSubmit.setEnabled(true);
                        progressDialog.dismiss();
                        Log.d("response==>",response.toString()+"");
                        try{
                            JSONObject jsonObject=response;
                            if(jsonObject.getBoolean("status")){
                                Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_LONG).show();
                                finish();
                            }else {
                                //openQrScan();
                                //Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_SHORT).show();
                                MySingalton.showOkDilog(context,jsonObject.getString("message").toString()+"", "Add RO Cylinder Warehouse Mapping");
                            }
                            SharedPreferences.Editor userFilterEditor = spFilledFilter.edit();
                            userFilterEditor.putBoolean("refilter",true);
                            if(fromwharehouspos>0){
                                userFilterEditor.putString("userId",userList.get(fromwharehouspos-1).get("userId"));
                            }
                            userFilterEditor.commit();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btnSubmit.setEnabled(true);
                        progressDialog.dismiss();
                        Log.d("response==>",error.toString()+"");
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        SharedPreferences.Editor userFilterEditor = spFilledFilter.edit();
        userFilterEditor.putBoolean("refilter",true);
        if(fromwharehouspos>0){
            userFilterEditor.putString("userId",userList.get(fromwharehouspos-1).get("userId"));
        }
        userFilterEditor.commit();
        onBackPressed();
        return true;
    }
    @SuppressLint("MissingPermission")
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void openQrScan() {
        txtCylinderNos.setError(null);
        Intent intent=new Intent(context, CylinderQRActivity.class);
        intent.putExtra("scanlist",qrcodeList);
        startActivityForResult(intent,201);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==201){
            try {
                qrcodeList = (ArrayList<String>) data.getSerializableExtra("scanlist");
                txtCylinderNos.setText(qrcodeList.toString()+"");
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(requestCode==222){
            try{
                SignImage="";
                SignImage=data.getStringExtra("imgUrl");
                txtSignaure.setText(SignImage);
            }catch (Exception e){
                e.printStackTrace();
            }
                if(isNetworkConnected()){
                    try {
                        if(SignImage.length()!=0) {
                            callAddCylinderWarehouseMapping();
                        }else{
                            Toast.makeText(context, "Signature not uploaded!", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }

        }
    }

    public boolean validate() {
        boolean valid = true;
        if (qrcodeList.size()==0) {
            txtCylinderNos.setError("Field is Required.");
            valid = false;
        } else {
            txtCylinderNos.setError(null);
        }
        if (towarehousepos<=0) {
            spToWarehouse.setError("Field is Required.");
            valid = false;
        } else {
            spToWarehouse.setError(null);
        }
        if (fromwharehouspos<=0) {
            spUser.setError("Field is Required.");
            valid = false;
        } else {
            spUser.setError(null);
        }
/*        if(SignImage==null || SignImage.length()==0){
            txtSignaure.setError("Signature is Required.");
            valid=false;
        }else {
            txtSignaure.setError(null);
        }*/
        return valid;
    }
    private void callGetWarehouseList1() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobWarehouse/GetWarehouseList?CompanyId="+
                Integer.parseInt(setting.getString("companyId","0"));
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
                    if(j.getBoolean("status")){
                        JSONArray jsonArray=j.getJSONArray("data");
                        towhereHouseList = new ArrayList<>();
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("warehouseId", jsonArray.getJSONObject(i).getInt("warehouseId")+"");
                            map.put("name", jsonArray.getJSONObject(i).getString("name") + "");
                            imtes.add(jsonArray.getJSONObject(i).getString("name"));
                            towhereHouseList.add(map);
                        }
                        spToWarehouse.attachDataSource(imtes);
                    }
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