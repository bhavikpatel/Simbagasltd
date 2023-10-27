package com.track.cylinderdelivery.ui.Reconciliation;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ui.cylinder.CylinderQRActivity;
import com.track.cylinderdelivery.ui.returnorder.RODetailListAdapter;
import com.track.cylinderdelivery.utils.SignatureActivity;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddReconciliationActivity extends AppCompatActivity {

    private String REconNumber;
    private AddReconciliationActivity context;
    private EditText edtRoNumber,edtSoDate,edtSOGeneratedBy;
    private String roDate;
    private NiceSpinner NSCompany1,NSCompany2,NSWarehouse;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    private SharedPreferences settings;
    private ArrayList<HashMap<String,String>> activeCompanyList;
    private ArrayList<HashMap<String,String>> companyClientDataList;
    private ArrayList<HashMap<String,String>> wareHouseDataList;
    private int activeCompanyPosition=0;
    private int complyClientPosition=0;
    private int wareHousePosition=0;
    private String comp_value;
    private String comp_client_value;
    private String comp_client_text;
    private String comp_text;
    Button btnCancel,btnSaveAndContinue;
    SharedPreferences spSorting;
    LinearLayout lvTab1;
    RelativeLayout lvTab2;
    TextView txtLineinfoUnderline;
    TextView txtPurchasodUnderline;

    TextView txtClientInfo;
    TextView txtPurchaseOrderDetail;

    private int ROId=0;
    //NiceSpinner NSPendingSales;
    ImageView btnScanCylinders;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    TextView txtCylinderNos;
    private ArrayList<String> qrcodeList;
    private int pendingsalespos=0;
    List<String> imtes;
    private String CylinderStatus;
    private Button btnAdd,btnLastSubmit,btnSaveAsDraft;
   // private EditText edtRemark;
    private String Remark;
    private int totalRecord;
    private ArrayList<HashMap<String,String>> sODetailList;
    private REconDetailListAdapter sODetailListAdapter;
    RecyclerView recyclerView;
    //EditText edtVehicleno;
    String SignImage="",PhotoImage="";
    private String warehouse_value="";
    private String warehouse_text="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconcilation_order);
        REconNumber= getIntent().getStringExtra("RONumber");
        context=this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Add Return Order");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Add Return Order</font>"));
        edtRoNumber=findViewById(R.id.edtRoNumber);
        edtRoNumber.setText(REconNumber);
        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        //edtVehicleno=findViewById(R.id.edtVehicleno);
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        edtSoDate=findViewById(R.id.edtSoDate);
        Date c = Calendar.getInstance().getTime();
        Log.d("soDate==>",c+"");
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        edtSoDate.setText(formattedDate);
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        roDate=df1.format(c);
        NSCompany1=findViewById(R.id.NSCompany1);
        NSCompany2=findViewById(R.id.NSCompany2);
        NSWarehouse=findViewById(R.id.NSWarehouse);
        edtSOGeneratedBy=findViewById(R.id.edtPOGeneratedBy);
        edtSOGeneratedBy.setText(settings.getString("fullName",""));
        spSorting=context.getSharedPreferences("ROFilter",MODE_PRIVATE);
        btnCancel=findViewById(R.id.btnCancel);
        lvTab1=findViewById(R.id.lvTab1);
        lvTab2=findViewById(R.id.lvTab2);
        txtLineinfoUnderline=findViewById(R.id.txtLineinfoUnderline);
        txtPurchasodUnderline=findViewById(R.id.txtPurchasodUnderline);
        lvTab1.setVisibility(View.VISIBLE);
        lvTab2.setVisibility(View.GONE);
        txtLineinfoUnderline.setBackgroundColor(getResources().getColor(R.color.green));
        txtPurchasodUnderline.setBackgroundColor(getResources().getColor(R.color.lightGrey));
        txtClientInfo=findViewById(R.id.txtClientInfo);
        txtPurchaseOrderDetail=findViewById(R.id.txtPurchaseOrderDetail);
       // NSPendingSales=(NiceSpinner)findViewById(R.id.NSPendingSales);
        btnScanCylinders=findViewById(R.id.btnScanCylinders);
        btnSaveAndContinue=findViewById(R.id.btnSaveAndContinue);
        txtCylinderNos=findViewById(R.id.txtCylinderNos);
        qrcodeList=new ArrayList<String>();
        imtes=new ArrayList<>();
        btnAdd=findViewById(R.id.btnAdd);
       // edtRemark=findViewById(R.id.edtRemark);
        btnLastSubmit=findViewById(R.id.btnLastSubmit);
        btnSaveAsDraft=findViewById(R.id.btnSaveAsDraft);
        sODetailList=new ArrayList<>();


        if(isNetworkConnected()) {
            callGetActiveUserData();
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }


        btnSaveAsDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnLastSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()){
                    if(sODetailList.size()!=0){
                        //callSubmitSO();
                        Intent intent=new Intent(context, SignatureActivity.class);
                        intent.putExtra("SOId",ROId);
                        startActivityForResult(intent,222);
                    }else {
                        Toast.makeText(context, "Kindly scan cylinder first.", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Remark=edtRemark.getText().toString().trim();
                if(validate1()){
                    if(isNetworkConnected()){
                        try {
                            callAddSOCylinder();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
/*        NSPendingSales.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                NSPendingSales.setError(null);
                Log.d("checkedId==>",position+"");
                hideSoftKeyboard(view);
                pendingsalespos=position;
                if(position!=0) {
                   // dnDetailId=pendingsalesList.get(position-1).get("dnDetailId");
                    //productId=pendingsalesList.get(position-1).get("productId");
                    CylinderStatus=imtes.get(position);
                }else {
                    pendingsalespos=0;
                }
            }
        });
        NSPendingSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
            }
        });*/
        btnScanCylinders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddReconciliationActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }else {
                    openQrScan();
                }
            }
        });

        txtClientInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvTab1.setVisibility(View.VISIBLE);
                lvTab2.setVisibility(View.GONE);
                txtPurchaseOrderDetail.setTextColor(getResources().getColor(R.color.lightGrey));
                txtClientInfo.setTextColor(getResources().getColor(R.color.green));
                txtLineinfoUnderline.setBackgroundColor(getResources().getColor(R.color.green));
                txtPurchasodUnderline.setBackgroundColor(getResources().getColor(R.color.lightGrey));
            }
        });
        btnSaveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()){
                    SharedPreferences.Editor userFilterEditor = spSorting.edit();
                    userFilterEditor.putBoolean("refilter",true);
                    userFilterEditor.commit();
                    if(validate()){
                        try {
                            callAddEditSO();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        NSCompany1.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                NSCompany1.setError(null);
                Log.d("getSelectedItem()==>",NSCompany1.getSelectedItem()+"");
                hideSoftKeyboard(view);
                activeCompanyPosition=position;
                if(position!=0) {

                    for(int i=0;i<activeCompanyList.size();i++){
                        if(NSCompany1.getSelectedItem().equals(activeCompanyList.get(i).get("text"))){
                            comp_value=activeCompanyList.get(i).get("value");
                            comp_text=activeCompanyList.get(i).get("text");
                            callGetCompanyClientDataApi(comp_value);
                            callGetActiveWarehouseList(comp_value);
                            break;
                        }
                    }
                }else {
                    comp_value="";
                    comp_text="";
                }
            }
        });
        NSCompany1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
            }
        });

        NSCompany2.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                NSCompany2.setError(null);
                Log.d("getSelectedItem()==>",NSCompany2.getSelectedItem()+"");
                hideSoftKeyboard(view);
                complyClientPosition=position;
                if(position!=0) {

                    for(int i=0;i<companyClientDataList.size();i++){
                        if(NSCompany2.getSelectedItem().equals(companyClientDataList.get(i).get("fullName"))){
                            comp_client_value=companyClientDataList.get(i).get("userId");
                            comp_client_text=companyClientDataList.get(i).get("fullName");
                            NSWarehouse.setSelectedIndex(0);
                            wareHousePosition=0;
                            warehouse_value="0";
                            break;
                        }
                    }
                }else {
                    comp_client_value="";
                    comp_client_text="";
                }
            }
        });
        NSCompany2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
            }
        });

        NSWarehouse.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                NSWarehouse.setError(null);
                Log.d("getSelectedItem()==>",NSWarehouse.getSelectedItem()+"");
                hideSoftKeyboard(view);
                wareHousePosition=position;
                if(position!=0) {

                    for(int i=0;i<wareHouseDataList.size();i++){
                        if(NSWarehouse.getSelectedItem().equals(wareHouseDataList.get(i).get("name"))){
                            warehouse_value=wareHouseDataList.get(i).get("warehouseId");
                            warehouse_text=wareHouseDataList.get(i).get("name");
                            NSCompany2.setSelectedIndex(0);
                            complyClientPosition=0;
                            comp_client_value="0";
                            break;
                        }
                    }
                }else {
                    warehouse_value="";
                    warehouse_text="";
                }
            }
        });
        NSWarehouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
            }
        });
    }

    private void callGetActiveWarehouseList(String compValue) {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobReconciliation/GetActiveWarehouseList?CompanyId="+Integer.parseInt(compValue);
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
                        wareHouseDataList=new ArrayList<>();
                        JSONArray datalist=j.getJSONArray("data");
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<datalist.length();i++){
                            HashMap<String,String> map=new HashMap<>();
                            JSONObject dataobj=datalist.getJSONObject(i);
                            map.put("warehouseId",dataobj.getInt("warehouseId")+"");
                            map.put("name",dataobj.getString("name"));
                            imtes.add(dataobj.getString("name") + "");
                            wareHouseDataList.add(map);
                        }
                        NSWarehouse.attachDataSource(imtes);
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

    private void callGetCompanyClientDataApi(String compValue) {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobReconciliation/GetCompanyClientData?CompanyId="+Integer.parseInt(compValue);
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
                        companyClientDataList=new ArrayList<>();
                        JSONArray datalist=j.getJSONArray("data");
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<datalist.length();i++){
                            HashMap<String,String> map=new HashMap<>();
                            JSONObject dataobj=datalist.getJSONObject(i);
                            map.put("userId",dataobj.getInt("userId")+"");
                            map.put("fullName",dataobj.getString("fullName"));
                            imtes.add(dataobj.getString("fullName") + "");
                            companyClientDataList.add(map);
                        }
                        NSCompany2.attachDataSource(imtes);
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

    private void callSubmitSO() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL +"/Api/MobReturnOrder/SubmitRO?ROId="+ROId+
                "&UserId="+Integer.parseInt(settings.getString("userId","0"))+
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

    private void callAddSOCylinder() throws JSONException {
        //isLoading=true;
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        /*if(podetailList==null){
            isLoading=false;
            isLastPage=false;
            pageno=0;*/
        progressDialog.show();
        /*}else {
            progressBar.setVisibility(View.VISIBLE);
        }*/
        String url = MySingalton.getInstance().URL+"/Api/MobReconciliation/AddEditRECDetail";
        Log.d("url==>",url);
        final JSONObject jsonBody=new JSONObject();
        jsonBody.put("ReconciliationId",ROId);
        JSONArray jsonArrayCylList=new JSONArray(qrcodeList);
        jsonBody.put("CylinderNoList",jsonArrayCylList);
/*        jsonBody.put("CylinderStatus",CylinderStatus);
        jsonBody.put("Remark",Remark);*/
        jsonBody.put("CreatedBy",Integer.parseInt(settings.getString("userId","1")));
        Log.d("jsonRequest==>",jsonBody.toString()+"");

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,url,jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        //progressBar.setVisibility(View.GONE);
                        // isLoading=false;
                        Log.d("response==>",response.toString()+"");
                        try{
                            JSONObject jsonObject=response;
                            if(jsonObject.getBoolean("status")){
                                //Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_LONG).show();
                                JSONObject dataobj=jsonObject.getJSONObject("data");
                                totalRecord= dataobj.getInt("totalRecord");
                                JSONArray jsonArray=dataobj.getJSONArray("list");
                                Boolean flgfirstload=false;
                                if(sODetailList==null){
                                    sODetailList=new ArrayList<>();
                                    //  flgfirstload=true;
                                }else {
                                    sODetailList.clear();
                                }
                                for(int i=0;i<jsonArray.length();i++){
                                    HashMap<String,String> map=new HashMap<>();
                                    map.put("roDetailId", jsonArray.getJSONObject(i).getString("roDetailId"));
                                    map.put("roId", jsonArray.getJSONObject(i).getString("roId"));
                                    map.put("cylinderIds", jsonArray.getJSONObject(i).getString("cylinderIds"));
                                    map.put("cylinderStatus",jsonArray.getJSONObject(i).getString("cylinderStatus"));
                                    map.put("remark", jsonArray.getJSONObject(i).getString("remark"));
                                    map.put("cylinderNo",jsonArray.getJSONObject(i).getString("cylinderNo"));
                                    map.put("createdBy",jsonArray.getJSONObject(i).getString("createdBy"));
                                    sODetailList.add(map);
                                }

                                sODetailListAdapter=new REconDetailListAdapter(sODetailList,context);
                                recyclerView.setAdapter(sODetailListAdapter);

/*                                if(podetailList.size()>=totalRecord){
                                    isLastPage=true;
                                }
                                if(flgfirstload){
                                    flgfirstload=false;
                                    productAddListAdapter=new ProductAddListAdapter(podetailList,context);
                                    recyclerView.setAdapter(productAddListAdapter);
                                }else {
                                    productAddListAdapter.notifyDataSetChanged();
                                }*/
                                txtCylinderNos.setText("");
                                Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_SHORT).show();
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
                        /*progressBar.setVisibility(View.GONE);
                        isLoading=false;*/
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
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
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
                        callAddSOCylinder();
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
                callSubmitSO();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
    private boolean validate1() {
        boolean valid = true;
/*        if (qrcodeList.size()==0) {
            txtCylinderNos.setError("Field is Required.");
            valid = false;
        } else {
            txtCylinderNos.setError(null);
        }*/
/*        if (pendingsalespos<=0) {
            NSPendingSales.setError("Field is Required.");
            valid = false;
        } else {
            NSPendingSales.setError(null);
        }*/

/*        if(Remark.length()==0){
            edtRemark.setError("Field is Required.");
            valid=false;
        }else {
            edtRemark.setError(null);
        }*/
        return valid;
    }

    private void openQrScan() {
        if(validate1()){
            if(isNetworkConnected()){
                txtCylinderNos.setError(null);
                Intent intent=new Intent(context, CylinderQRActivity.class);
                intent.putExtra("scanlist",qrcodeList);
                startActivityForResult(intent,201);
            }else {
                Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void callAddEditSO() throws JSONException {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobReconciliation/AddEditREC";
        Log.d("url==>",url);
        final JSONObject jsonBody=new JSONObject();
/*        if(ROId==0){
            jsonBody.put("ROId",JSONObject.NULL);
        }else {
            jsonBody.put("ROId",ROId);
        }*/
        jsonBody.put("ReconciliationNumber",REconNumber);
        jsonBody.put("CompanyId",Integer.parseInt(comp_value));
        jsonBody.put("UserId",Integer.parseInt(comp_client_value));
        jsonBody.put("WarehouseId",Integer.parseInt(warehouse_value+""));
        jsonBody.put("ReconciliationDate",roDate+"");
        jsonBody.put("GeneratedBy",edtSOGeneratedBy.getText().toString());
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
                                ROId=jsonObject.getInt("data");
                                lvTab1.setVisibility(View.GONE);
                                lvTab2.setVisibility(View.VISIBLE);
                                txtPurchaseOrderDetail.setTextColor(getResources().getColor(R.color.green));
                                txtClientInfo.setTextColor(getResources().getColor(R.color.lightGrey));
                                txtLineinfoUnderline.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                                txtPurchasodUnderline.setBackgroundColor(getResources().getColor(R.color.green));
                                if(isNetworkConnected()){
                                  //  callROCylinderStatusList();

/*                                    imtes.add("Select");
                                    imtes.add("Empty");
                                    imtes.add("Damage");
                                    imtes.add("Missing");
                                    NSPendingSales.attachDataSource(imtes);*/


                                }else {
                                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                                }
                            }else {
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
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    private void callGetActiveUserData() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobReconciliation/GetActiveCompanyList?CompanyId="+Integer.parseInt(settings.getString("companyId","1"));
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
                        activeCompanyList=new ArrayList<>();
                        JSONArray datalist=j.getJSONArray("data");
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<datalist.length();i++){
                            HashMap<String,String> map=new HashMap<>();
                            JSONObject dataobj=datalist.getJSONObject(i);
                            map.put("value",dataobj.getInt("value")+"");
                            map.put("text",dataobj.getString("text"));
                            imtes.add(dataobj.getString("text") + "");
                            activeCompanyList.add(map);
                        }
                        NSCompany1.attachDataSource(imtes);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean validate() {
        boolean valid = true;
        if (REconNumber.isEmpty()) {
            edtRoNumber.setError("Field is Required.");
            valid = false;
        } else {
            edtRoNumber.setError(null);
        }
        if(roDate.isEmpty()){
            edtSoDate.setError("Field is Required.");
            valid=false;
        }else {
            edtSoDate.setError(null);
        }
        if(activeCompanyPosition<=0){
            NSCompany1.setError("Field is Required.");
            valid=false;
        }else {
            NSCompany1.setError(null);
        }
/*        if(edtVehicleno.getText().toString().trim().length()==0){
            edtVehicleno.setError("Field is Required.");
            valid = false;
        }else {
            edtVehicleno.setError(null);
        }*/
        if(edtSOGeneratedBy.getText().toString()==null){
            edtSOGeneratedBy.setError("Field is Required.");
            valid=false;
        }else {
            edtSOGeneratedBy.setError(null);
        }
        return valid;
    }

    public void callChangeCompanyStatus(String roDetailId) {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobReturnOrder/DeleteRODetail?RODetailId="+Integer.parseInt(roDetailId)+
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
                    if(j.getBoolean("status")){
                        for(int i=0;i<sODetailList.size();i++){
                            if(sODetailList.get(i).get("roDetailId").equals(roDetailId)){
                                sODetailList.remove(i);
                                break;
                            }
                        }
                        sODetailListAdapter.notifyDataSetChanged();
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