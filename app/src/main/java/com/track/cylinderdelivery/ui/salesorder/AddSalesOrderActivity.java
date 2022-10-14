package com.track.cylinderdelivery.ui.salesorder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ui.cylinder.CylinderQRActivity;
import com.track.cylinderdelivery.ui.diliverynote.DNDetailListAdapter;
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

public class AddSalesOrderActivity extends AppCompatActivity {

    AddSalesOrderActivity context;
    private String SNNumber;
    EditText edtSoNumber,edtSoDate,edtSOGeneratedBy;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    private SharedPreferences settings;
    NiceSpinner NSDeloryNote,NSClient;
    private ArrayList<HashMap<String,String>> deliveryList;
    private int delnotepos=0;
    private String dnNumber;
    private String dnId="0";
    private ArrayList<HashMap<String,String>> customerList;
    Button btnCancel,btnSaveAndContinue;
    SharedPreferences spSorting;
    private String soDate;
    private int clientpos=0;
    private String clintvalue;
    private String clinttext;
    LinearLayout lvTab1;
    RelativeLayout lvTab2;
    TextView txtPurchaseOrderDetail;
    TextView txtPurchasodUnderline;
    TextView txtClientInfo;
    TextView txtLineinfoUnderline;
    private int SOId=0;
    NiceSpinner NSPendingSales;
    private ArrayList<HashMap<String,String>> pendingsalesList;
    ImageView btnScanCylinders;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final int MY_PERMISSIONS_REQUEST_READ_WRITE_FILE = 101;
    private ArrayList<String> qrcodeList;
    TextView txtCylinderNos;
    private Button btnAdd,btnLastSubmit,btnSaveAsDraft;
    private int pendingsalespos=0;
    private String dnDetailId="0";
    private String productId="0";
    private int totalRecord;
    private ArrayList<HashMap<String,String>> sODetailList;
    private SODetailListAdapter sODetailListAdapter;
    RecyclerView recyclerView;
    TextView txtUserName11;
    NiceSpinner NSWarehouse;
    private int wareHousepos=0;
    private ArrayList<HashMap<String,String>> warehouseList;
    Button btnSignature;
    private int CylinderHoldingCreditDays=0;
    private EditText edtCylinderHoldingCreditDays;
    private String PODetailId="0";
    private String warehouseId="0";
    private String SignImage="";
    private String PhotoImage="";
    private String DeliveryAddress="";
    private EditText edtDiliveryAdd;
    String cylinderList="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sales_order);
        SNNumber= getIntent().getStringExtra("SNNumber");
        context=this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Add Sales Order");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Add Sales Order</font>"));
        edtSoNumber=findViewById(R.id.edtSoNumber);
        edtSoDate=findViewById(R.id.edtSoDate);
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        NSDeloryNote=findViewById(R.id.NSUserName);
        NSClient=findViewById(R.id.NSClient);
        edtSOGeneratedBy=findViewById(R.id.edtPOGeneratedBy);
        edtDiliveryAdd=findViewById(R.id.edtDiliveryAdd);
        btnCancel=findViewById(R.id.btnCancel);
        btnSaveAndContinue=findViewById(R.id.btnSaveAndContinue);
        spSorting=context.getSharedPreferences("SOFilter",MODE_PRIVATE);
        lvTab1=findViewById(R.id.lvTab1);
        lvTab2=findViewById(R.id.lvTab2);
        txtPurchaseOrderDetail=findViewById(R.id.txtPurchaseOrderDetail);
        txtPurchasodUnderline=findViewById(R.id.txtPurchasodUnderline);
        txtClientInfo=findViewById(R.id.txtClientInfo);
        txtLineinfoUnderline=findViewById(R.id.txtLineinfoUnderline);
        NSPendingSales=findViewById(R.id.NSPendingSales);
        lvTab1.setVisibility(View.VISIBLE);
        lvTab2.setVisibility(View.GONE);
        txtLineinfoUnderline.setBackgroundColor(getResources().getColor(R.color.green));
        txtPurchasodUnderline.setBackgroundColor(getResources().getColor(R.color.lightGrey));
        btnScanCylinders=findViewById(R.id.btnScanCylinders);
        txtCylinderNos=findViewById(R.id.txtCylinderNos);
        qrcodeList=new ArrayList<String>();
        btnAdd=findViewById(R.id.btnAdd);
        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        btnLastSubmit=findViewById(R.id.btnLastSubmit);
        btnSaveAsDraft=findViewById(R.id.btnSaveAsDraft);
        txtUserName11=findViewById(R.id.txtUserName11);
        NSWarehouse=findViewById(R.id.NSWarehouse);
        NSWarehouse.setVisibility(View.GONE);
        txtUserName11.setVisibility(View.GONE);
        btnSignature=findViewById(R.id.btnSignature);
        edtCylinderHoldingCreditDays=findViewById(R.id.edtCylinderHoldingCreditDays);
        sODetailList=new ArrayList<>();

        Date c = Calendar.getInstance().getTime();
        Log.d("soDate==>",c+"");
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        edtSoDate.setText(formattedDate);
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        soDate=df1.format(c);
        edtSoNumber.setText(SNNumber);
        edtSOGeneratedBy.setText(settings.getString("fullName",""));

        if(isNetworkConnected()) {
            callGetReadyforDeliveryDeliveryList();
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }

        NSWarehouse.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                NSWarehouse.setError(null);
                Log.d("checkedId==>",position+"");
                hideSoftKeyboard(view);
                wareHousepos=position;
                if(position!=0) {
                    warehouseId=warehouseList.get(position-1).get("warehouseId");
                }else {
                    //clintvalue="";
                    //clinttext="";
                }
            }
        });
        NSWarehouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
            }
        });
        btnSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, SignatureActivity.class);
                intent.putExtra("SOId",SOId);
                startActivity(intent);
            }
        });
        NSWarehouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
            }
        });
        btnSaveAsDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor userFilterEditor = spSorting.edit();
                userFilterEditor.putBoolean("sofilter",true);
                userFilterEditor.commit();
                finish();
            }
        });
        btnLastSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sODetailList.size()==0){
                    Toast.makeText(context, "Kindly scan cylinder first.", Toast.LENGTH_LONG).show();
                }else if(isNetworkConnected()){
                    Intent intent=new Intent(context, SignatureActivity.class);
                    intent.putExtra("SOId",SOId);
                    startActivityForResult(intent,222);
                   // callSubmitSO();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        btnScanCylinders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddSalesOrderActivity.this,
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
                    userFilterEditor.putBoolean("sofilter",true);
                    userFilterEditor.commit();
                    DeliveryAddress=edtDiliveryAdd.getText().toString();
                    if(validate()){
                        try {
                            CylinderHoldingCreditDays=Integer.parseInt(edtCylinderHoldingCreditDays.getText().toString().trim());
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

        NSPendingSales.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                NSPendingSales.setError(null);
                Log.d("checkedId==>",position+"");
                hideSoftKeyboard(view);
                pendingsalespos=position;
                if(position!=0) {
                    if(pendingsalesList.get(position-1).get("dnDetailId").equals("null") ||
                            pendingsalesList.get(position-1).get("dnDetailId").equals(null) ||
                            pendingsalesList.get(position-1).get("dnDetailId").toString().length()==0){
                            dnDetailId="0";
                    }else {
                        dnDetailId=pendingsalesList.get(position-1).get("dnDetailId");
                    }
                    if(pendingsalesList.get(position-1).get("productId").equals("null") ||
                            pendingsalesList.get(position-1).get("productId").equals(null) ||
                            pendingsalesList.get(position-1).get("productId").toString().length()==0){
                            productId="0";
                    }else {
                        productId = pendingsalesList.get(position - 1).get("productId");
                    }
                    if(pendingsalesList.get(position-1).get("poDetailId").equals("null") ||
                            pendingsalesList.get(position-1).get("poDetailId").equals(null) ||
                            pendingsalesList.get(position-1).get("poDetailId").toString().length()==0){
                        PODetailId="0";
                    }else {
                        PODetailId = pendingsalesList.get(position - 1).get("poDetailId");
                    }
                    cylinderList=pendingsalesList.get(position-1).get("cylinderList")+"";
                }else {

                }
            }
        });
        NSPendingSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
            }
        });

        NSClient.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                NSClient.setError(null);
                Log.d("checkedId==>",position+"");
                hideSoftKeyboard(view);
                clientpos=position;
                if(position!=0) {
                    clintvalue=customerList.get(position-1).get("value");
                    clinttext=customerList.get(position-1).get("text");
                    if(customerList.get(position-1).get("cylinderHoldingCreditDays").equals("null") ||
                            customerList.get(position-1).get("cylinderHoldingCreditDays").equals("")){
                        edtCylinderHoldingCreditDays.setText("0");
                    }else{
                        edtCylinderHoldingCreditDays.setText(customerList.get(position-1).get("cylinderHoldingCreditDays"));
                    }
                    if(isNetworkConnected()){
                         if(customerList.get(position-1).get("isAdmin").equals("true")){
                             callGetUserWarehouse(clintvalue);
                         }else {
                             NSWarehouse.setVisibility(View.GONE);
                             txtUserName11.setVisibility(View.GONE);
                         }
                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }
                }else {
                    clintvalue="";
                    clinttext="";
                }
            }
        });
        NSClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
            }
        });

        NSDeloryNote.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                NSDeloryNote.setError(null);
                Log.d("checkedId==>",position+"");
                hideSoftKeyboard(view);
                delnotepos=position;
                if(position!=0) {
                     if(deliveryList.get(position-1).get("dnNumber").equals(null) ||
                            deliveryList.get(position-1).get("dnNumber").equals("null") ||
                            deliveryList.get(position-1).get("dnNumber").length()==0){
                            dnNumber="0";
                    }else {
                         dnNumber=deliveryList.get(position-1).get("dnNumber");
                     }
                    if(deliveryList.get(position-1).get("dnId").equals(null) ||
                            deliveryList.get(position-1).get("dnId").equals("null") ||
                            deliveryList.get(position-1).get("dnId").length()==0){
                        dnId="0";
                    }else {
                        dnId=deliveryList.get(position-1).get("dnId");
                    }
                    NSWarehouse.setVisibility(View.GONE);
                    txtUserName11.setVisibility(View.GONE);
                    if(isNetworkConnected()){
                        if(dnId!=null){
                            callgetDeliveryNoteCustomerList(dnId);
                        }
                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }
                }else {
                    dnId="0";
                    List<String> imtes=new ArrayList<>();
                    imtes.add("Select");
                    NSClient.attachDataSource(imtes);
                    NSClient.setSelectedIndex(0);
                    if(isNetworkConnected()){
                            callgetDeliveryNoteCustomerList("");
                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        NSDeloryNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
            }
        });
    }
    private void callGetUserWarehouse(String clintvalue) {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL +"/Api/MobWarehouse/GetUserWarehouse?UserId="+Integer.parseInt(clintvalue);
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
                        warehouseList=new ArrayList<>();
                        JSONArray datalist=j.getJSONArray("data");
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<datalist.length();i++){
                            HashMap<String,String> map=new HashMap<>();
                            JSONObject dataobj=datalist.getJSONObject(i);
                            map.put("warehouseId",dataobj.getString("warehouseId")+"");
                            map.put("name",dataobj.getString("name"));

                            imtes.add(dataobj.getString("name") + "");
                            warehouseList.add(map);
                        }
                        if(warehouseList.size()!=0){
                            NSWarehouse.setVisibility(View.VISIBLE);
                            txtUserName11.setVisibility(View.VISIBLE);
                            NSWarehouse.attachDataSource(imtes);
                        }
                        //NSWarehouse.setSelectedIndex(wareHousepos+1);
                    }else {
                        String msg=j.getString("message");
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
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
        String url = MySingalton.getInstance().URL+"/Api/MobSalesOrder/SubmitSO?SOId="+SOId+
                "&UserId="+settings.getString("userId","1")+
                "&SignImage="+SignImage;//+"&PhotoImage="+PhotoImage;
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
                        SharedPreferences.Editor userFilterEditor = spSorting.edit();
                        userFilterEditor.putBoolean("sofilter",true);
                        userFilterEditor.commit();
                        Toast.makeText(context, j.getString("message")+"", Toast.LENGTH_LONG).show();
                        finish();
                    }else {
                        //openQrScan();
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
        String url = MySingalton.getInstance().URL+"/Api/MobSalesOrder/AddSOCylinder";
        final JSONObject jsonBody=new JSONObject();
        jsonBody.put("SOId",SOId);
        jsonBody.put("ProductId",Integer.parseInt(productId));
        jsonBody.put("DNDetailId",Integer.parseInt(dnDetailId));
        jsonBody.put("DNid",Integer.parseInt(dnId));
        JSONArray jsonArrayCylList=new JSONArray(qrcodeList);
        jsonBody.put("CylinderList",jsonArrayCylList);
        jsonBody.put("CylinderHoldingCreditDays",CylinderHoldingCreditDays);
        jsonBody.put("PODetailId",Integer.parseInt(PODetailId));
        jsonBody.put("CreatedBy",Integer.parseInt(settings.getString("userId","0")));
        jsonBody.put("CompanyId",Integer.parseInt(settings.getString("companyId","0")));
        Log.d("jsonRequest==>",jsonBody.toString()+"/"+url);

        /*jsonBody.put("CreatedBy",Integer.parseInt(settings.getString("userId","1")));
        jsonBody.put("CylinderHoldingCreditDays",CylinderHoldingCreditDays);
        */

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,url,jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                       // progressBar.setVisibility(View.GONE);
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
                                }
                                for(int i=0;i<jsonArray.length();i++){
                                    HashMap<String,String> map=new HashMap<>();
                                    map.put("soDetailId", jsonArray.getJSONObject(i).getString("soDetailId"));
                                    map.put("soId", jsonArray.getJSONObject(i).getString("soId"));
                                    map.put("productId", jsonArray.getJSONObject(i).getString("productId"));
                                    map.put("productName",jsonArray.getJSONObject(i).getString("productName"));
                                    map.put("cylinderProductMappingId", jsonArray.getJSONObject(i).getString("cylinderProductMappingId"));
                                    map.put("dnDetailId",jsonArray.getJSONObject(i).getString("dnDetailId"));
                                    map.put("cylinderID",jsonArray.getJSONObject(i).getString("cylinderID"));
                                    map.put("cylinderList",jsonArray.getJSONObject(i).getString("cylinderList"));
                                    map.put("createdBy",jsonArray.getJSONObject(i).getString("createdBy"));
                                    map.put("createdDate",jsonArray.getJSONObject(i).getString("createdDate"));
                                    map.put("dNid",jsonArray.getJSONObject(i).getString("dNid"));
                                    map.put("createdByName",jsonArray.getJSONObject(i).getString("createdByName"));
                                    map.put("createdDateStr",jsonArray.getJSONObject(i).getString("createdDateStr"));
                                    sODetailList.add(map);
                                }
                                sODetailListAdapter=new SODetailListAdapter(sODetailList,context);
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
                            }else {
                                //Toast.makeText(getApplicationContext(),jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                                String msg=jsonObject.getString("message");
                                Log.d("msg=>",msg);
                                openQrScan();
                                Toast.makeText(getBaseContext(),msg,Toast.LENGTH_LONG).show();
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

    private boolean validate1() {
        boolean valid = true;
/*        if (qrcodeList.size()==0) {
            txtCylinderNos.setError("Field is Required.");
            valid = false;
        } else {
            txtCylinderNos.setError(null);
        }*/
        if (pendingsalespos<=0) {
            NSPendingSales.setError("Field is Required.");
            valid = false;
        } else {
            NSPendingSales.setError(null);
        }
        return valid;
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
    private void openQrScan() {
        if(validate1()){
            if(isNetworkConnected()){
                txtCylinderNos.setError(null);
                Intent intent=new Intent(context, CylinderQRActivity.class);
                intent.putExtra("scanlist",qrcodeList);
                intent.putExtra("cylList",cylinderList);
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
        String url = MySingalton.getInstance().URL+"/Api/MobSalesOrder/AddEditSO";
        Log.d("url==>",url);
        final JSONObject jsonBody=new JSONObject();

/*        if(SOId==0){
            jsonBody.put("SOId","");
        }else {
            jsonBody.put("SOId",SOId);
        }*/
        jsonBody.put("SOId", SOId);
/*        if(Integer.parseInt(dnId)!=0) {
            jsonBody.put("DNId", Integer.parseInt(dnId));
        }else {
            jsonBody.put("DNId", "");
        }*/
        jsonBody.put("DNId", Integer.parseInt(dnId));
        jsonBody.put("SONumber",SNNumber);
        jsonBody.put("UserId",Integer.parseInt(clintvalue));
        jsonBody.put("SODate",soDate+"");
        jsonBody.put("SOGeneratedBy",edtSOGeneratedBy.getText().toString()+"");
        if(!warehouseId.equals("0")){
            jsonBody.put("WarehouseId",Integer.parseInt(warehouseId));
        }
        jsonBody.put("CreatedBy",Integer.parseInt(settings.getString("userId","0")));
        jsonBody.put("CylinderHoldingCreditDays",CylinderHoldingCreditDays);
        jsonBody.put("DeliveryAddress",DeliveryAddress);

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
                                SOId=jsonObject.getInt("data");
                                lvTab1.setVisibility(View.GONE);
                                lvTab2.setVisibility(View.VISIBLE);
                                txtPurchaseOrderDetail.setTextColor(getResources().getColor(R.color.green));
                                txtClientInfo.setTextColor(getResources().getColor(R.color.lightGrey));
                                txtLineinfoUnderline.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                                txtPurchasodUnderline.setBackgroundColor(getResources().getColor(R.color.green));
                                if(isNetworkConnected()){
                                   callGetSalesOrderDetail();
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
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    private void callGetSalesOrderDetail() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
/*        int dnid= (int) JSONObject.NULL;
        if(Integer.parseInt(dnId)!=0){
            dnid=Integer.parseInt(dnId);
        }*/
        String url = MySingalton.getInstance().URL+"/Api/MobSalesOrder/GetSalesOrderDetail?search=&pageno=0&totalinpage="+Integer.MAX_VALUE+
                "&SortBy=&Sort=desc&SOId="+SOId+"&DNId="+Integer.parseInt(dnId)+"&UserId="+Integer.parseInt(clintvalue);
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
                        pendingsalesList=new ArrayList<>();
                        JSONArray datalist=j.getJSONArray("list");
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<datalist.length();i++){
                            HashMap<String,String> map=new HashMap<>();
                            JSONObject dataobj=datalist.getJSONObject(i);
                            map.put("dnDetailId",dataobj.getString("dnDetailId")+"");
                            map.put("dnId",dataobj.getString("dnId"));
                            map.put("companyId",dataobj.getString("companyId"));
                            map.put("userId",dataobj.getString("userId"));
                            map.put("productId",dataobj.getString("productId"));
                            map.put("productName",dataobj.getString("productName"));
                            map.put("poDetailId",dataobj.getString("poDetailId"));
                            map.put("quantity",dataobj.getString("quantity"));
                            map.put("createdBy",dataobj.getString("createdBy"));
                            map.put("createdDate",dataobj.getString("createdDate"));
                            map.put("createdByName",dataobj.getString("createdByName"));
                            map.put("createdDateStr",dataobj.getString("createdDateStr"));
                            map.put("userName",dataobj.getString("userName"));
                            map.put("deliveryNoteNo",dataobj.getString("deliveryNoteNo"));
                            map.put("poNo",dataobj.getString("poNo"));
                            map.put("cylinderList",dataobj.getString("cylinderList"));
                            map.put("cylinders",dataobj.getString("cylinders"));

                            String cylinderList1=dataobj.getString("cylinderList");
                            if(cylinderList1.equals("null")||cylinderList1.equals(null)||cylinderList1.length()==0){
                                cylinderList1="";
                            }
                            imtes.add(dataobj.getString("productName") +
                                    "/"+dataobj.getString("quantity")+
                                    "/"+dataobj.getString("userName")+
                                    "/"+cylinderList1);
                            pendingsalesList.add(map);
                        }
                        NSPendingSales.attachDataSource(imtes);
                        NSPendingSales.setSelectedIndex(0);

                        callGetSalesOrderDetailList();
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

                callGetSalesOrderDetailList();
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

    private void callGetSalesOrderDetailList() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobSalesOrder/GetSalesOrderDetailList?search=&pageno=0&totalinpage="+Integer.MAX_VALUE
                +"&SortBy=&Sort=desc&SOId="+SOId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                progressDialog.dismiss();
                Log.d("resonse ==>",Response+"");
                if(sODetailList==null){
                    sODetailList=new ArrayList<>();
                    //  flgfirstload=true;
                }
                JSONObject j;
                try {
                    j = new JSONObject(Response);

                    JSONArray datalist=j.getJSONArray("list");
                    for(int i=0;i<datalist.length();i++){
                        HashMap<String,String> map=new HashMap<>();
                        JSONObject dataobj=datalist.getJSONObject(i);
                        map.put("soDetailId",dataobj.getString("soDetailId")+"");
                        map.put("soId",dataobj.getString("soId"));
                        map.put("productId",dataobj.getString("productId"));
                        map.put("productName",dataobj.getString("productName"));
                        map.put("cylinderProductMappingId",dataobj.getString("cylinderProductMappingId"));
                        map.put("dnDetailId",dataobj.getString("dnDetailId"));
                        map.put("cylinderID",dataobj.getString("cylinderID"));
                        map.put("cylinderList",dataobj.getString("cylinderList"));
                        map.put("createdBy",dataobj.getString("createdBy"));
                        map.put("createdDate",dataobj.getString("createdDate"));
                        map.put("dNid",dataobj.getString("dNid"));
                        map.put("createdByName",dataobj.getString("createdByName"));
                        map.put("createdDateStr",dataobj.getString("createdDateStr"));
                        map.put("poDetailId",dataobj.getString("poDetailId"));
                        map.put("companyId",dataobj.getString("companyId"));
                        sODetailList.add(map);
                    }
                    if(sODetailListAdapter==null) {
                        sODetailListAdapter = new SODetailListAdapter(sODetailList, context);
                        recyclerView.setAdapter(sODetailListAdapter);
                    }else {
                        sODetailListAdapter.notifyDataSetChanged();
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
                if(sODetailListAdapter==null) {
                    sODetailListAdapter = new SODetailListAdapter(sODetailList, context);
                    recyclerView.setAdapter(sODetailListAdapter);
                }else {
                    sODetailListAdapter.notifyDataSetChanged();
                }
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

    public boolean validate() {
        boolean valid = true;
        if (SNNumber.isEmpty()) {
            edtSoNumber.setError("Field is Required.");
            valid = false;
        } else {
            edtSoNumber.setError(null);
        }
        if(soDate.isEmpty()){
            edtSoDate.setError("Field is Required.");
            valid=false;
        }else {
            edtSoDate.setError(null);
        }
        /*if(delnotepos<=0){
            NSDeloryNote.setError("Field is Required.");
            valid=false;
        }else {
            NSDeloryNote.setError(null);
        }*/
        if(clientpos<=0){
            NSClient.setError("Field is Required.");
            valid=false;
        }else {
            NSClient.setError(null);
        }
        if(edtCylinderHoldingCreditDays.getText().toString().trim().length()==0 ||
                edtCylinderHoldingCreditDays.getText().toString().equals("null") ||
                edtCylinderHoldingCreditDays.getText().toString().equals("0")){

            edtCylinderHoldingCreditDays.setError("Minimum 1 day required.");
            valid=false;
        }else {
            edtCylinderHoldingCreditDays.setError(null);
        }
        if(edtSOGeneratedBy.getText().toString()==null){
            edtSOGeneratedBy.setError("Field is Required.");
            valid=false;
        }else {
            edtSOGeneratedBy.setError(null);
        }
        return valid;
    }
    private void callgetDeliveryNoteCustomerList(String dnId) {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobSalesOrder/getDeliveryNoteCustomerList?DNId="+dnId+
                "&CompanyId="+Integer.parseInt(settings.getString("companyId","0"));
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
                    NSClient.attachDataSource(new ArrayList<>());
                    if(j.getBoolean("status")){
                        customerList=new ArrayList<>();
                        JSONArray datalist=j.getJSONArray("data");
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<datalist.length();i++){
                            HashMap<String,String> map=new HashMap<>();
                            JSONObject dataobj=datalist.getJSONObject(i);
                            map.put("value",dataobj.getInt("value")+"");
                            map.put("text",dataobj.getString("text"));
                            map.put("isAdmin",dataobj.getBoolean("isAdmin")+"");
                            map.put("cylinderHoldingCreditDays",dataobj.getString("cylinderHoldingCreditDays")+"");
                            imtes.add(dataobj.getString("text") + "");
                            customerList.add(map);
                        }
                        NSClient.attachDataSource(imtes);
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

    private void callGetReadyforDeliveryDeliveryList() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobSalesOrder/GetReadyforDeliveryDeliveryList?CompanyId="+
                Integer.parseInt(settings.getString("companyId","1"));
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
                        deliveryList=new ArrayList<>();
                        JSONArray datalist=j.getJSONArray("data");
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<datalist.length();i++){
                            HashMap<String,String> map=new HashMap<>();
                            JSONObject dataobj=datalist.getJSONObject(i);
                            map.put("dnId",dataobj.getInt("dnId")+"");
                            map.put("dnNumber",dataobj.getString("dnNumber"));
                            imtes.add(dataobj.getString("dnNumber") + "");
                            deliveryList.add(map);
                        }
                        NSDeloryNote.attachDataSource(imtes);
                    }else {
                        Toast.makeText(context, j.getString("message")+"", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(isNetworkConnected()){
                       //if(dnId!=null){
                           callgetDeliveryNoteCustomerList("");
                       //}
                }else {
                      Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
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

    @SuppressLint("MissingPermission")
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void callChangeCompanyStatus(String soDetailId) {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobSalesOrder/DeleteSODetail?SODetailId="+Integer.parseInt(soDetailId)+
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
                            if(sODetailList.get(i).get("soDetailId").equals(soDetailId)){
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