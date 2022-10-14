package com.track.cylinderdelivery.ui.diliverynote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ui.CylinderWarehouseMapping.AddCylinderWarehouseMapping;
import com.track.cylinderdelivery.ui.cylinder.CylinderQRActivity;
import com.track.cylinderdelivery.utils.SignatureActivity;
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

public class DNCylinderActivity extends AppCompatActivity {
    DNCylinderActivity context;
    private String DNNumber;
    Button btnSaveAsDraft;
    ImageView btnScanCylinders;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    TextView txtCylinderNos;
    ArrayList<String> qrcodeList;
    EditText edtDNnumber;
    private HashMap<String, String> mapdata;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    SharedPreferences settings;
    ArrayList<HashMap<String, String>> whereHouseList;
    NiceSpinner NSwarehouse;
    private int warehousepos=0;
    private int warehouseId;
    private ArrayList<HashMap<String,String>> pendingcyliList;
    private ArrayList<String> cylinderList1;
    NiceSpinner NSPendingcyl;
    Button btnAdd;
    private int pendingcylpos=0;
    private int productID;
    private String needtoaddcylinder;
    private ArrayList<HashMap<String,String>> AddEditDNList;
    private DNCylinderDetailListAdapter dNCylinderDetailListAdapter;
    RecyclerView recyclerView;
    private TransparentProgressDialog progressDialog;
    private Button btnLastSubmit;
    private EditText edtVehicleno;
    SharedPreferences spSorting;
    private String SignImage="";
    private String PhotoImage="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d_n_cylinder);
        context=this;
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("editData");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("DNCylinder");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>DNCylinder</font>"));
        btnSaveAsDraft=findViewById(R.id.btnSaveAsDraft);
        btnScanCylinders=findViewById(R.id.btnScanCylinders);
        txtCylinderNos=findViewById(R.id.txtCylinderNos);
        qrcodeList=new ArrayList<String>();
        cylinderList1=new ArrayList<String>();
        edtDNnumber=findViewById(R.id.edtDNnumber);
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        NSwarehouse=findViewById(R.id.NSClinetList);
        NSPendingcyl=findViewById(R.id.NSClientPenPurDet);
        btnAdd=findViewById(R.id.btnAdd);
        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        btnLastSubmit=findViewById(R.id.btnLastSubmit);
        edtVehicleno=findViewById(R.id.edtVehicleno);
        spSorting=context.getSharedPreferences("DNFilter",MODE_PRIVATE);

        DNNumber= mapdata.get("dnNumber");
        edtDNnumber.setText(DNNumber);
        if(isNetworkConnected()){
            callGetWarehouseList();
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }
        btnLastSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()){
                    SharedPreferences.Editor userFilterEditor = spSorting.edit();
                    userFilterEditor.putBoolean("dofilter",true);
                    userFilterEditor.commit();
                    Intent intent=new Intent(context, SignatureActivity.class);
                    intent.putExtra("SOId",DNNumber);
                    startActivityForResult(intent,222);
                   // callReadyforDelivery();
                }else {
                    //openQrScan();
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()){
                    try {
                        if(validate()) {
                            callAddDNCylinder();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
            }
        });

        NSPendingcyl.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                hideSoftKeyboard(view);
                pendingcylpos=position;
                if(position!=0) {
                    productID = Integer.parseInt(pendingcyliList.get(position-1).get("productID"));
                    cylinderList1.add(pendingcyliList.get(position-1).get("cylinderList"));
                    needtoaddcylinder = pendingcyliList.get(position - 1).get("cylinderList");
                    /*if(isNetworkConnected()){
                        // callGetDeliveryNotePOList();
                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }*/
                }
            }
        });
        NSPendingcyl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NSPendingcyl.setError(null);
                hideSoftKeyboard(v);
            }
        });

        NSwarehouse.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                hideSoftKeyboard(view);
                warehousepos=position;
                if(position!=0) {
                    warehouseId = Integer.parseInt(whereHouseList.get(position - 1).get("warehouseId"));
                   /* if(isNetworkConnected()){
                       // callGetDeliveryNotePOList();
                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }*/
                }
            }
        });
        NSwarehouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NSwarehouse.setError(null);
                hideSoftKeyboard(v);
            }
        });
        btnScanCylinders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DNCylinderActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }else {
                    openQrScan();
                }
            }
        });
        btnSaveAsDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void callReadyforDelivery() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL +"/Api/MobDeliveryNote/ReadyforDelivery?DNId="+Integer.parseInt(mapdata.get("dnId"))+
                "&UserId="+Integer.parseInt(settings.getString("userId","1"))+
                "&DriverVehicleNo="+edtVehicleno.getText().toString().trim()+
                "&SignImage="+SignImage+"&PhotoImage="+PhotoImage;
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
                        Toast.makeText(context, j.getString("message")+"", Toast.LENGTH_LONG).show();
                        finish();
                    }else {
                        openQrScan();
                        Toast.makeText(context, j.getString("message")+"", Toast.LENGTH_LONG).show();
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

    private void callAddDNCylinder() throws JSONException {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobDeliveryNote/AddDNCylinder";
        Log.d("url==>",url);
        final JSONObject jsonBody=new JSONObject();
        jsonBody.put("DNId",Integer.parseInt(mapdata.get("dnId")));
       // jsonBody.put("WarehouseId",warehouseId);
        jsonBody.put("ProductId",productID);
        JSONArray jsonArrayCylList=new JSONArray(qrcodeList);
        jsonBody.put("CylinderList",jsonArrayCylList);
        jsonBody.put("CreatedBy",Integer.parseInt(settings.getString("userId","1")));

        Log.d("jsonRequest==>",jsonBody.toString()+"");

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,url,jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Log.d("response==>",response.toString()+"");
                        try{
                            JSONObject jsonObject=response;
                            if(jsonObject.getBoolean("status")){
                                Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_LONG).show();
                                JSONObject data=jsonObject.getJSONObject("data");
                                JSONArray jsonArray=data.getJSONArray("list");
                                AddEditDNList=new ArrayList<>();
                                for(int i=0;i<jsonArray.length();i++){
                                    HashMap<String,String> map=new HashMap<>();
                                    map.put("dnCylinderId", jsonArray.getJSONObject(i).getString("dnCylinderId"));
                                    map.put("dnId", jsonArray.getJSONObject(i).getString("dnId"));
                                    map.put("dnNo", jsonArray.getJSONObject(i).getString("dnNo"));
                                    map.put("cylinderProductMappingId",jsonArray.getJSONObject(i).getString("cylinderProductMappingId"));
                                   // map.put("warehouseId", jsonArray.getJSONObject(i).getString("warehouseId"));
                                    map.put("cylinderID",jsonArray.getJSONObject(i).getString("cylinderID"));
                                    map.put("productID",jsonArray.getJSONObject(i).getString("productID"));
                                    map.put("createdBy",jsonArray.getJSONObject(i).getString("createdBy"));
                                    map.put("createdDate",jsonArray.getJSONObject(i).getString("createdDate"));
                                    map.put("strCreatedDate",jsonArray.getJSONObject(i).getString("strCreatedDate"));
                                    map.put("createdByName",jsonArray.getJSONObject(i).getString("createdByName"));
                                    map.put("productName",jsonArray.getJSONObject(i).getString("productName"));
                                    map.put("warehouseName",jsonArray.getJSONObject(i).getString("warehouseName"));
                                    map.put("cylinderList",jsonArray.getJSONObject(i).getString("cylinderList"));
                                    map.put("driverVehicleNo",jsonArray.getJSONObject(i).getString("driverVehicleNo"));
                                    AddEditDNList.add(map);
                                }
                                dNCylinderDetailListAdapter=new DNCylinderDetailListAdapter(AddEditDNList,context);
                                recyclerView.setAdapter(dNCylinderDetailListAdapter);
                            }else {
                                openQrScan();
                                Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
    void callGetDeliveryNoteCylinder(){
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobDeliveryNote/GetDeliveryNoteCylinder?search=&pageno=0&totalinpage="+
                Integer.MAX_VALUE+"&SortBy=&Sort=desc&DNid="+Integer.parseInt(mapdata.get("dnId"));
        Log.d("request==>",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Log.d("response==>",response.toString()+"");
                try{
                    JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("list");
                        AddEditDNList=new ArrayList<>();
                        for(int i=0;i<jsonArray.length();i++){
                            HashMap<String,String> map=new HashMap<>();
                            map.put("dnCylinderId", jsonArray.getJSONObject(i).getString("dnCylinderId"));
                            map.put("dnId", jsonArray.getJSONObject(i).getString("dnId"));
                            map.put("dnNo", jsonArray.getJSONObject(i).getString("dnNo"));
                            map.put("cylinderProductMappingId",jsonArray.getJSONObject(i).getString("cylinderProductMappingId"));
                           // map.put("warehouseId", jsonArray.getJSONObject(i).getString("warehouseId"));
                            map.put("cylinderID",jsonArray.getJSONObject(i).getString("cylinderID"));
                            map.put("productID",jsonArray.getJSONObject(i).getString("productID"));
                            map.put("createdBy",jsonArray.getJSONObject(i).getString("createdBy"));
                            map.put("createdDate",jsonArray.getJSONObject(i).getString("createdDate"));
                            map.put("strCreatedDate",jsonArray.getJSONObject(i).getString("strCreatedDate"));
                            map.put("createdByName",jsonArray.getJSONObject(i).getString("createdByName"));
                            map.put("productName",jsonArray.getJSONObject(i).getString("productName"));
                            map.put("warehouseName",jsonArray.getJSONObject(i).getString("warehouseName"));
                            map.put("cylinderList",jsonArray.getJSONObject(i).getString("cylinderList"));
                            map.put("driverVehicleNo",jsonArray.getJSONObject(i).getString("driverVehicleNo")+"");

                            AddEditDNList.add(map);
                        }
                        dNCylinderDetailListAdapter=new DNCylinderDetailListAdapter(AddEditDNList,context);
                        recyclerView.setAdapter(dNCylinderDetailListAdapter);
                }catch (Exception e){
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
    private void callGetWarehouseList() {
/*        Log.d("Api Calling==>","Api Calling");
        progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobWarehouse/GetWarehouseList?CompanyId="+
                Integer.parseInt(settings.getString("CompanyId","1"));
        Log.d("request==>",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                //progressDialog.dismiss();
                Log.d("resonse ==>",Response+"");
                JSONObject j;
                try {
                    j = new JSONObject(Response);
                    if(j.getBoolean("status")){
                        JSONArray jsonArray=j.getJSONArray("data");
                        whereHouseList = new ArrayList<>();
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("warehouseId", jsonArray.getJSONObject(i).getInt("warehouseId")+"");
                            map.put("name", jsonArray.getJSONObject(i).getString("name") + "");
                            imtes.add(jsonArray.getJSONObject(i).getString("name") + "");
                            whereHouseList.add(map);
                        }
                        NSwarehouse.attachDataSource(imtes);

                    }else {

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
        queue.add(stringRequest);*/


        callGetDeliveryNotePendingCylinder();
    }

    private void callGetDeliveryNotePendingCylinder() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobDeliveryNote/GetDeliveryNotePendingCylinder?search=&pageno=0&totalinpage="+Integer.MAX_VALUE+
                "&SortBy=&Sort=desc&DNid="+Integer.parseInt(mapdata.get("dnId"));
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
                        JSONArray jsonArray=j.getJSONArray("list");
                        pendingcyliList = new ArrayList<>();
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("productName", jsonArray.getJSONObject(i).getString("productName")+"");
                            map.put("quantity", jsonArray.getJSONObject(i).getString("quantity") + "");
                            map.put("remainingQuantity", jsonArray.getJSONObject(i).getString("remainingQuantity") + "");
                            map.put("productID", jsonArray.getJSONObject(i).getString("productID") + "");
                            map.put("cylinderList", jsonArray.getJSONObject(i).getString("cylinderList") + "");
                            map.put("dndid", jsonArray.getJSONObject(i).getString("dndid") + "");
                            cylinderList1.add(jsonArray.getJSONObject(i).getString("cylinderList") + "");
                            imtes.add(jsonArray.getJSONObject(i).getString("productName")+"/"+
                                    jsonArray.getJSONObject(i).getString("cylinderList") + "/"+
                                    jsonArray.getJSONObject(i).getString("quantity") );
                            pendingcyliList.add(map);
                        }
                    NSPendingcyl.attachDataSource(imtes);
                    callGetDeliveryNoteCylinder();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==201){
            try {
                qrcodeList = (ArrayList<String>) data.getSerializableExtra("scanlist");
                txtCylinderNos.setText(qrcodeList.toString()+"");
                if(!qrcodeList.isEmpty()){
                    try {
                        if(validate()) {
                            callAddDNCylinder();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(requestCode==222){
            try{
                SignImage=data.getStringExtra("imgUrl");
                callReadyforDelivery();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                Log.i("Camera", "G : " + grantResults[0]);
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted,
                    openQrScan();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.CAMERA)) {
                        //showAlert();
                    } else {

                    }
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    private void openQrScan() {
        if(isNetworkConnected()){
            if(validate()) {
                txtCylinderNos.setError(null);
                Intent intent=new Intent(context, CylinderQRActivity.class);
                intent.putExtra("scanlist",qrcodeList);
                intent.putExtra("cylList",needtoaddcylinder+"");
                startActivityForResult(intent,201);
            }
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }

    }
    public boolean validate() {
        boolean valid = true;
/*        if (qrcodeList.size()==0) {
            txtCylinderNos.setError("Field is Required.");
            valid = false;
        } else {
            txtCylinderNos.setError(null);
        }*/
        if(edtVehicleno.getText().toString().trim().length()==0){
            edtVehicleno.setError("Field is Required.");
            valid = false;
        }else {
            edtVehicleno.setError(null);
        }
/*        if (warehousepos<=0) {
            NSwarehouse.setError("Field is Required.");
            valid = false;
        } else {
            NSwarehouse.setError(null);
        }*/
        if(pendingcylpos<=0){
            NSPendingcyl.setError("Field is Required.");
            valid = false;
        }else {
            NSPendingcyl.setError(null);
        }
        return valid;
    }

    public void callDeleteDNCylinder(String dnDetailId) {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobDeliveryNote/DeleteDNCylinder?DNCylinderId="+dnDetailId;
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
                        for(int i=0;i<AddEditDNList.size();i++){
                            if(AddEditDNList.get(i).get("dnCylinderId").equals(dnDetailId)){
                                AddEditDNList.remove(i);
                                break;
                            }
                        }
                        dNCylinderDetailListAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(context, j.getString("message")+"", Toast.LENGTH_LONG).show();
                    }
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