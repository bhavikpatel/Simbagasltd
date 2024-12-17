package com.track.cylinderdelivery.ui.diliverynote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.track.cylinderdelivery.ui.cylinder.AddCylinderActivity;
import com.track.cylinderdelivery.ui.cylinder.CylinderQRActivity;
import com.track.cylinderdelivery.ui.purchaseorder.ProductAddListAdapter;
import com.track.cylinderdelivery.utils.CustomSpinner;
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

public class AddDeliveryNoteActivity extends AppCompatActivity {

    AddDeliveryNoteActivity context;
    /////////////////////////////////////1 part
    EditText edtDNnumber,edtDNDate,edtDNGeneratedBy;
    Calendar myCalendar;
    private SharedPreferences settings;
    CustomSpinner NSAllocatedEmployee;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000;
    private int userpos=0;
    private int UserId;
    private ArrayList<HashMap<String, String>> userList;
    private String DNNumber;
    private String DNDate;
    private Button btnSaveAndContinue,btnCancel;
    private String DNGeneratedBy;
    SharedPreferences spSorting;

    private int DNId=0;
    /////////////////////////////////////2 part
    ImageView btnScanCylinders;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    TextView txtCylinderNos;
    ArrayList<String> qrcodeList;
    LinearLayout lvTab1;
    RelativeLayout lvTab2;
    TextView txtPurchaseOrderDetail,txtClientInfo,txtLineinfoUnderline;
    TextView txtPurchasodUnderline;
    private ArrayList<HashMap<String,String>> PendingPOUserList;
    private NiceSpinner NSClinetList,NSClientPenPurDet;
    private int ClintUserId;
    private int clintuserpos=0;
    private ArrayList<HashMap<String,String>> DeliveryNotePOList;
    private Button btnAdd;
    private int ClientPenPurDetpos=0;
    private int ProductId;
    private int PODetailId;
    private int remainingQty=0;
    private int totalRecord=0;
    private ArrayList<HashMap<String,String>> dNDetailList;
    private DNDetailListAdapter dNDetailListAdapter;
    RecyclerView recyclerView;
    Button btnSaveAsDraft,btnLastSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_note);
        context=this;
        DNNumber= getIntent().getStringExtra("DNNumber");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Add Delivery Note");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Add Delivery Note</font>"));
        /////////////////////////////////////////1 part
        edtDNnumber=findViewById(R.id.edtDNnumber);
        edtDNnumber.setText(DNNumber);
        edtDNDate=findViewById(R.id.edtDNDate);
        Date c = Calendar.getInstance().getTime();
        Log.d("soDate==>",c+"");
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        edtDNDate.setText(formattedDate);
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        DNDate=df1.format(c);
        myCalendar = Calendar.getInstance();
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        NSAllocatedEmployee=findViewById(R.id.NSAllocatedEmployee);
        NSAllocatedEmployee.setText("Select");
        edtDNGeneratedBy=findViewById(R.id.edtDNGeneratedBy);
        edtDNGeneratedBy.setText(settings.getString("fullName",""));
        btnSaveAndContinue=findViewById(R.id.btnSaveAndContinue);
        btnCancel=findViewById(R.id.btnCancel);
        spSorting=context.getSharedPreferences("DNFilter",MODE_PRIVATE);
        ///////////////////////////////////////2..part
        btnScanCylinders=findViewById(R.id.btnScanCylinders);
        txtCylinderNos=findViewById(R.id.txtCylinderNos);
        qrcodeList=new ArrayList<String>();
        lvTab1=findViewById(R.id.lvTab1);
        lvTab2=findViewById(R.id.lvTab2);
        txtPurchaseOrderDetail=findViewById(R.id.txtPurchaseOrderDetail);
        txtClientInfo=findViewById(R.id.txtClientInfo);
        txtLineinfoUnderline=findViewById(R.id.txtLineinfoUnderline);
        txtPurchasodUnderline=findViewById(R.id.txtPurchasodUnderline);
        NSClinetList=findViewById(R.id.NSClinetList);
        NSClientPenPurDet=findViewById(R.id.NSClientPenPurDet);
        btnAdd=findViewById(R.id.btnAdd);
        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        btnSaveAsDraft=findViewById(R.id.btnSaveAsDraft);
        btnLastSubmit=findViewById(R.id.btnLastSubmit);

        lvTab1.setVisibility(View.VISIBLE);
        lvTab2.setVisibility(View.GONE);
        txtLineinfoUnderline.setBackgroundColor(getResources().getColor(R.color.green));
        txtPurchasodUnderline.setBackgroundColor(getResources().getColor(R.color.lightGrey));
        dNDetailList=new ArrayList<>();

        if(isNetworkConnected()){
            callGetActiveEmployeeData();
        }else{
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }

        btnLastSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dNDetailList.size()!=0){
                    if(isNetworkConnected()){
                        callSubmitDN();
                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context, "DN Detail are pending.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnSaveAsDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                //NSClinetList.setSelected(false);
                //NSClientPenPurDet.setSelected(false);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    if(isNetworkConnected()){
                        try {
                            callAddDNCylinder();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        btnSaveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()){
                    SharedPreferences.Editor userFilterEditor = spSorting.edit();
                    userFilterEditor.putBoolean("dofilter",true);
                    userFilterEditor.commit();
                    DNNumber=edtDNnumber.getText().toString();
                    DNGeneratedBy=edtDNGeneratedBy.getText().toString();
                    userpos=0;
                    for(int i=0;i<userList.size();i++){
                        if(userList.get(i).get("fullName").equals(NSAllocatedEmployee.getText().toString())){
                            UserId = Integer.parseInt(userList.get(i).get("userId"));
                            userpos=i+1;
                            break;
                        }
                    }
                    if(validate1()){
                        try {
                            callAddEditDN();
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
       /* NSAllocatedEmployee.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                hideSoftKeyboard(view);
                userpos=position;
                if(position!=0) {
                    UserId = Integer.parseInt(userList.get(position - 1).get("userId"));
                }
            }
        });*/
/*        NSAllocatedEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
            }
        });*/
        NSClientPenPurDet.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                hideSoftKeyboard(view);
                ClientPenPurDetpos=position;
                if(position!=0) {
                    ProductId = Integer.parseInt(DeliveryNotePOList.get(position - 1).get("productId"));
                    PODetailId= Integer.parseInt(DeliveryNotePOList.get(position - 1).get("poDetailId"));
                    remainingQty= Integer.parseInt(DeliveryNotePOList.get(position - 1).get("remainingQty"));
                }
            }
        });
        NSClientPenPurDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NSClientPenPurDet.setError(null);
                hideSoftKeyboard(v);
            }
        });
        NSClinetList.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                hideSoftKeyboard(view);
                clintuserpos=position;
                if(position!=0) {
                    ClintUserId = Integer.parseInt(PendingPOUserList.get(position - 1).get("userId"));
                    if(isNetworkConnected()){
                        callGetDeliveryNotePOList();
                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        NSClinetList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NSClinetList.setError(null);
                hideSoftKeyboard(v);
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
/*        edtDNDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });*/
        txtCylinderNos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtCylinderNos.setError(null);
            }
        });
        btnScanCylinders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddDeliveryNoteActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }else {
                    openQrScan();
                }
            }
        });
    }

    private void callAddDNCylinder() throws JSONException {
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
        String url = MySingalton.getInstance().URL+"/Api/MobDeliveryNote/AddEditDNDetail";
        final JSONObject jsonBody=new JSONObject();
        jsonBody.put("DNDetailId",JSONObject.NULL);
        jsonBody.put("DNId",DNId);
        jsonBody.put("CompanyId",Integer.parseInt(settings.getString("companyId","1")));
        jsonBody.put("UserId",ClintUserId);
        jsonBody.put("ProductId",ProductId);
        jsonBody.put("PODetailId",PODetailId);
        jsonBody.put("Quantity",remainingQty);
        JSONArray jsonArrayCylList=new JSONArray(qrcodeList);
        jsonBody.put("Cylinders",jsonArrayCylList);
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
                                if(dNDetailList==null){
                                    dNDetailList=new ArrayList<>();
                                  //  flgfirstload=true;
                                }
                                for(int i=0;i<jsonArray.length();i++){
                                    HashMap<String,String> map=new HashMap<>();
                                    map.put("dnDetailId", jsonArray.getJSONObject(i).getString("dnDetailId"));
                                    map.put("dnId", jsonArray.getJSONObject(i).getString("dnId"));
                                    map.put("companyId", jsonArray.getJSONObject(i).getString("companyId"));
                                    map.put("userId",jsonArray.getJSONObject(i).getString("userId"));
                                    map.put("productId", jsonArray.getJSONObject(i).getString("productId"));
                                    map.put("productName",jsonArray.getJSONObject(i).getString("productName"));
                                    map.put("poDetailId",jsonArray.getJSONObject(i).getString("poDetailId"));
                                    map.put("quantity",jsonArray.getJSONObject(i).getString("quantity"));
                                    map.put("createdBy",jsonArray.getJSONObject(i).getString("createdBy"));
                                    map.put("createdDate",jsonArray.getJSONObject(i).getString("createdDate"));
                                    map.put("createdByName",jsonArray.getJSONObject(i).getString("createdByName"));
                                    map.put("createdDateStr",jsonArray.getJSONObject(i).getString("createdDateStr"));
                                    map.put("userName",jsonArray.getJSONObject(i).getString("userName"));
                                    map.put("deliveryNoteNo",jsonArray.getJSONObject(i).getString("deliveryNoteNo"));
                                    map.put("poNo",jsonArray.getJSONObject(i).getString("poNo"));
                                    map.put("cylinderList",jsonArray.getJSONObject(i).getString("cylinderList"));
                                    map.put("cylinders",jsonArray.getJSONObject(i).getString("cylinders"));

                                    dNDetailList.add(map);
                                }
                                dNDetailListAdapter=new DNDetailListAdapter(dNDetailList,context);
                                recyclerView.setAdapter(dNDetailListAdapter);
                                qrcodeList.clear();
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
    public void callDeleteDNDetail(String dndetailid) {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL +"/Api/MobDeliveryNote/DeleteDNDetail?DNDetailId="+Integer.parseInt(dndetailid)+
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
                        for(int i=0;i<dNDetailList.size();i++){
                            if(dNDetailList.get(i).get("dnDetailId").equals(dndetailid)){
                                dNDetailList.remove(i);
                                break;
                            }
                        }
                        dNDetailListAdapter.notifyDataSetChanged();
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
    private void callGetDeliveryNotePOList() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobDeliveryNote/GetDeliveryNotePOList?search=&pageno=0&totalinpage="+
                Integer.MAX_VALUE+"&SortBy=&Sort=desc&UserId="+
                ClintUserId+"&PONo=";
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
                        DeliveryNotePOList=new ArrayList<>();
                        JSONArray datalist=j.getJSONArray("list");
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<datalist.length();i++){
                            HashMap<String,String> map=new HashMap<>();
                            JSONObject dataobj=datalist.getJSONObject(i);
                            map.put("poDetailId",dataobj.getInt("poDetailId")+"");
                            map.put("productId",dataobj.getString("productId"));
                            map.put("quantity",dataobj.getString("quantity"));
                            map.put("createdBy",dataobj.getString("createdBy"));
                            map.put("createdByName",dataobj.getString("createdByName"));
                            map.put("createdDateStr",dataobj.getString("createdDateStr"));
                            map.put("productName",dataobj.getString("productName"));
                            map.put("poNumber",dataobj.getString("poNumber"));
                            map.put("remainingQty",dataobj.getString("remainingQty"));
                            map.put("userId",dataobj.getString("userId"));

                            imtes.add(dataobj.getString("poNumber") + "/"+
                                    dataobj.getString("productName")+"/"+
                                    dataobj.getString("remainingQty"));
                            DeliveryNotePOList.add(map);
                        }
                    NSClientPenPurDet.attachDataSource(imtes);
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

    private void callAddEditDN() throws JSONException {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobDeliveryNote/AddEditDN";
        final JSONObject jsonBody=new JSONObject();
        if(DNId==0){
            jsonBody.put("DNId",JSONObject.NULL);
        }else {
            jsonBody.put("DNId",DNId);
        }
        jsonBody.put("DNNumber",DNNumber+"");
        jsonBody.put("UserId",UserId);
        jsonBody.put("DNDate",DNDate);
        jsonBody.put("DNGeneratedBy",DNGeneratedBy);
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
                                DNId=jsonObject.getInt("data");
                                lvTab1.setVisibility(View.GONE);
                                lvTab2.setVisibility(View.VISIBLE);
                                txtPurchaseOrderDetail.setTextColor(getResources().getColor(R.color.green));
                                txtClientInfo.setTextColor(getResources().getColor(R.color.lightGrey));
                                txtLineinfoUnderline.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                                txtPurchasodUnderline.setBackgroundColor(getResources().getColor(R.color.green));
                                if(isNetworkConnected()){
                                    callGetPendingPOUserList();
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

    private void callGetPendingPOUserList() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobDeliveryNote/GetPendingPOUserList?CompanyId="+
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
                        PendingPOUserList=new ArrayList<>();
                        JSONArray datalist=j.getJSONArray("data");
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<datalist.length();i++){
                            HashMap<String,String> map=new HashMap<>();
                            JSONObject dataobj=datalist.getJSONObject(i);
                            map.put("userId",dataobj.getInt("userId")+"");
                            map.put("fullName",dataobj.getString("fullName"));
                            imtes.add(dataobj.getString("fullName") + "");
                            PendingPOUserList.add(map);
                        }
                        NSClinetList.attachDataSource(imtes);

                    }else{
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

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void callGetActiveEmployeeData() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobUser/GetActiveEmployeeData?CompanyId="+
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
                        userList=new ArrayList<>();
                        JSONArray datalist=j.getJSONArray("data");
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<datalist.length();i++){
                            HashMap<String,String> map=new HashMap<>();
                            JSONObject dataobj=datalist.getJSONObject(i);
                            map.put("userId",dataobj.getInt("userId")+"");
                            map.put("fullName",dataobj.getString("fullName"));
                            imtes.add(dataobj.getString("fullName") + "");
                            userList.add(map);
                        }
                        ArrayAdapter<String> customSpinnerAdapter = new ArrayAdapter<>(
                                context,
                                android.R.layout.simple_spinner_dropdown_item,
                                imtes
                        );
                        NSAllocatedEmployee.setAdapter(customSpinnerAdapter);

                       // NSAllocatedEmployee.attachDataSource(imtes);
                    }else{
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
                        callAddDNCylinder();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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
        if(validate()){
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
    public boolean validate() {
        boolean valid = true;
/*        if (qrcodeList.size()==0) {
            txtCylinderNos.setError("Field is Required.");
            valid = false;
        } else {
            txtCylinderNos.setError(null);
        }*/
       if (clintuserpos<=0) {
           NSClinetList.setError("Field is Required.");
            valid = false;
        } else {
           NSClinetList.setError(null);
        }
       if(ClientPenPurDetpos<=0){
           NSClientPenPurDet.setError("Field is Required.");
           valid = false;
       }else {
           NSClientPenPurDet.setError(null);
       }
        return valid;
    }
    public boolean validate1() {
        boolean valid = true;

        if (userpos<=0) {
            NSAllocatedEmployee.setError("Field is Required.");
            valid = false;
        } else {
            NSAllocatedEmployee.setError(null);
        }

        return valid;
    }
    @SuppressLint("MissingPermission")
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        DNDate=sdf.format(myCalendar.getTime());
        String myFormat1 = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1, Locale.US);
        edtDNDate.setText(sdf1.format(myCalendar.getTime()));
    }
    private void callSubmitDN() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobDeliveryNote/SubmitDN?DNId="+DNId+
                "&UserId="+Integer.parseInt(settings.getString("userId","1"));
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
}