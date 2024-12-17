package com.track.cylinderdelivery.ui.purchaseorder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.track.cylinderdelivery.utils.Apiclient;
import com.track.cylinderdelivery.utils.CustomSpinner;
import com.track.cylinderdelivery.utils.FileUtils;
import com.track.cylinderdelivery.utils.MarketPlaceApiInterface;
import com.track.cylinderdelivery.utils.MarketPlaceApiInterfacePO;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class AddPurchaseOrderActivity extends AppCompatActivity {

    AddPurchaseOrderActivity context;

    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    TextView txtClientInfo;
    private SharedPreferences settings;
    String PONumber;
    EditText edtPoNumber,edtPoDate,edtPOGeneratedBy,edtClientPOReference;
    Calendar myCalendar;
    ///NiceSpinner NSUserName;
    CustomSpinner NSUserName1;
    
    private int userpos=0;
    private String sel_username="";
    Button btnCancel,btnSaveAndContinue;
    private String PoNumber;
    private String PoDate;
    private ArrayList<HashMap<String,String>> userList;
    private int UserId;
    private String POGeneratedBy;
    LinearLayout lvTab1;
    TextView txtLineinfoUnderline,txtClientPOUpload;

    RelativeLayout lvTab2;
    TextView txtPurchaseOrderDetail;
    TextView txtPurchasodUnderline;
    private int prodpos=0;
    EditText edtQuantity;
    Button btnAdd;
    private int quantity=0;
    NiceSpinner NSProduct;
    ArrayList<HashMap<String,String>> productList;
    private int productid=0;
    private int POId=0;
    ArrayList<HashMap<String,String>> podetailList;
    RecyclerView recyclerView;
    ProductAddListAdapter productAddListAdapter;
    Button btnLastSubmit;
    SharedPreferences spSorting;
    Button btnSaveAsDraft;

    private ProgressBar progressBar;
    Boolean isLoading=false;
    Boolean isLastPage=false;
    private int totalRecord;
    private int pageno=0;
    private EditText edtgasqty;
    private int qtygaskg;
    TextView txtUnit;
    String unit="KG";
    private int GALLERY = 1, CAMERA = 2;

    private static final int CAMERA_REQUEST = 1888;
   // private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private String imgUrl;

    private static final int REQUEST_IMAGE_PICK = 102;
    private static final int REQUEST_CODE_SELECT_DOCUMENT=103;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_purchase_order);
        context=this;
        PONumber= getIntent().getStringExtra("PONumber");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Add Purchase Order");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Add Purchase Order</font>"));
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        txtClientInfo=findViewById(R.id.txtClientInfo);
        edtPoNumber=findViewById(R.id.edtPoNumber);
        edtPoDate=findViewById(R.id.edtPoDate);
        Date c = Calendar.getInstance().getTime();
        Log.d("soDate==>",c+"");
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        edtPoDate.setText(formattedDate);
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        PoDate=df1.format(c);
        spSorting=context.getSharedPreferences("POFilter",MODE_PRIVATE);
        NSUserName1=findViewById(R.id.NSUserName1);
        NSUserName1.setText("Select");
        edtPOGeneratedBy=findViewById(R.id.edtPOGeneratedBy);
        btnCancel=findViewById(R.id.btnCancel);
        btnSaveAndContinue=findViewById(R.id.btnSaveAndContinue);
        txtClientPOUpload=findViewById(R.id.txtClientPOUpload);
        lvTab1=findViewById(R.id.lvTab1);
        txtLineinfoUnderline=findViewById(R.id.txtLineinfoUnderline);
        progressBar=findViewById(R.id.progressBar);
        edtClientPOReference=findViewById(R.id.edtClientPOReference);

        lvTab2=findViewById(R.id.lvTab2);
        txtPurchaseOrderDetail=findViewById(R.id.txtPurchaseOrderDetail);
        txtPurchasodUnderline=findViewById(R.id.txtPurchasodUnderline);
        edtQuantity=findViewById(R.id.edtQuantity);
        btnAdd=findViewById(R.id.btnAdd);
        NSProduct=findViewById(R.id.NSProduct);
        txtUnit=findViewById(R.id.txtUnit);
        btnLastSubmit=findViewById(R.id.btnLastSubmit);
        btnSaveAsDraft=findViewById(R.id.btnSaveAsDraft);
        edtgasqty=findViewById(R.id.edtgasqty);

        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        lvTab1.setVisibility(View.VISIBLE);
        lvTab2.setVisibility(View.GONE);
        txtLineinfoUnderline.setBackgroundColor(getResources().getColor(R.color.green));
        txtPurchasodUnderline.setBackgroundColor(getResources().getColor(R.color.lightGrey));
        edtPoNumber.setText(PONumber);
        edtPOGeneratedBy.setText(settings.getString("fullName",""));
        if(!edtPOGeneratedBy.getText().equals("")){
            edtPOGeneratedBy.setEnabled(false);
        }
        if(isNetworkConnected()){
            callGetActiveUserData();
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }
        myCalendar = Calendar.getInstance();

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
        txtClientPOUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
                //dispatchTakePictureIntent();
            }
        });
/*        edtPoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });*/
/*        NSUserName1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                //hideSoftKeyboard(view);
                userpos=position;
                if(position!=0) {
                    UserId = Integer.parseInt(userList.get(position - 1).get("userId"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/



       /* NSUserName1.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
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
        NSUserName1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  hideSoftKeyboard(v);
            }
        });
        NSProduct.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
               // hideSoftKeyboard(view);
                prodpos=position;
                if(position!=0) {
                    productid = Integer.parseInt(productList.get(position - 1).get("productId"));
                    unit=productList.get(position-1).get("unit");
                    txtUnit.setText(unit);
                }
            }
        });
        NSProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hideSoftKeyboard(v);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
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
            }
        });
        btnSaveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()){
                    SharedPreferences.Editor userFilterEditor = spSorting.edit();
                    userFilterEditor.putBoolean("dofilter",true);
                    userFilterEditor.commit();
                    PoNumber=edtPoNumber.getText().toString();
                    //PoDate=edtPoDate.getText().toString();
                    POGeneratedBy=edtPOGeneratedBy.getText().toString();
                    sel_username=NSUserName1.getText().toString();
                    userpos=0;
                    for(int i=0;i<userList.size();i++){
                        if(sel_username.equals(userList.get(i).get("fullName"))){
                            UserId=Integer.parseInt(userList.get(i).get("userId"));
                            userpos=i+1;
                        }
                    }
                    if(validate()){
                        try {
                            callAddEditPO();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hideSoftKeyboard(v);
                if(validate1()){
                    quantity=Integer.parseInt(edtQuantity.getText().toString()+"");
                    qtygaskg=Integer.parseInt(edtgasqty.getText().toString()+"");
                    if(isNetworkConnected()){
                        try {
                            podetailList=null;
                            callAddEditPODetail();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        btnLastSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(podetailList.size()!=0){
                    if(isNetworkConnected()){
                        callSubmitPO();
                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(context, "PO Detail are pending.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnSaveAsDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                Log.d("visibleItemCount==>",visibleItemCount+"");
                Log.d("totalItemCount==>",totalItemCount+"");
                Log.d("firstvisibleitmpos==>",firstVisibleItemPosition+"");
                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount < totalRecord) {
                        if (isNetworkConnected()) {
                            pageno++;
                            Log.d("pageno==>", pageno + "");
                            try {
                                callAddEditPODetail();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                        }
                        Log.d("callapi==>", "callapi");
                    }
                }
            }
        });
    }

    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an Option");
        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dispatchTakePictureIntent();
            }
        });
        builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkStoragePermission();
            }
        });
        builder.setNeutralButton("Document", new DialogInterface.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                    checkStoragePermission1();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("NewApi")
    private void checkStoragePermission() {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }else{

        }
        openImagePicker();

    }


    @RequiresApi(api = Build.VERSION_CODES. M)
    @SuppressLint("NewApi")
    private void checkStoragePermission1() {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            openDocPicker();
        }else{
            openDocPicker();
        }
    }

    private void openDocPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf"); // You can specify file type like "application/pdf" for PDFs
        intent.addCategory(Intent.CATEGORY_OPENABLE); // Ensures the file picker can open files
        startActivityForResult(Intent.createChooser(intent, "Select Document"), REQUEST_CODE_SELECT_DOCUMENT);

    }

    private void openImagePicker() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(pickIntent, "Select Picture"), REQUEST_IMAGE_PICK);
    }

    private void callSubmitPO() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL +"/Api/MobPurchaseOrder/SubmitPO?POId="+POId+
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

    private void callAddEditPODetail() throws JSONException {
        isLoading=true;
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        if(podetailList==null){
            isLoading=false;
            isLastPage=false;
            pageno=0;
            progressDialog.show();
        }else {
            progressBar.setVisibility(View.VISIBLE);
        }
        String url = MySingalton.getInstance().URL+"/Api/MobPurchaseOrder/AddEditPODetail";
        final JSONObject jsonBody=new JSONObject();
        jsonBody.put("POId",POId);
        jsonBody.put("ProductId",productid);
        jsonBody.put("Quantity",quantity);
        jsonBody.put("CreatedBy",Integer.parseInt(settings.getString("userId","1")));
        jsonBody.put("QuantityOfGas",qtygaskg);
        jsonBody.put("Unit",unit);

        Log.d("jsonRequest==>",jsonBody.toString()+"");

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,url,jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        progressBar.setVisibility(View.GONE);
                        isLoading=false;
                        Log.d("response==>",response.toString()+"");
                        try{
                            JSONObject jsonObject=response;
                            if(jsonObject.getBoolean("status")){
                                //Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_LONG).show();
                                JSONObject dataobj=jsonObject.getJSONObject("data");
                                totalRecord= dataobj.getInt("totalRecord");
                                JSONArray jsonArray=dataobj.getJSONArray("list");
                                Boolean flgfirstload=false;
                                if(podetailList==null){
                                    podetailList=new ArrayList<>();
                                    flgfirstload=true;
                                }
                                for(int i=0;i<jsonArray.length();i++){
                                    HashMap<String,String> map=new HashMap<>();
                                    map.put("podetailid", String.valueOf(jsonArray.getJSONObject(i).getInt("poDetailId")));
                                    map.put("POId", String.valueOf(jsonArray.getJSONObject(i).getInt("poId")));
                                    map.put("ProductId", String.valueOf(jsonArray.getJSONObject(i).getInt("productId")));
                                    map.put("productName",jsonArray.getJSONObject(i).getString("productName"));
                                    map.put("Quantity", String.valueOf(jsonArray.getJSONObject(i).getInt("quantity")));
                                    podetailList.add(map);
                                }
                                if(podetailList.size()>=totalRecord){
                                    isLastPage=true;
                                }
                                if(flgfirstload){
                                    flgfirstload=false;
                                    productAddListAdapter=new ProductAddListAdapter(podetailList,context);
                                    recyclerView.setAdapter(productAddListAdapter);
                                }else {
                                    productAddListAdapter.notifyDataSetChanged();
                                }
                                edtQuantity.setText("");
                                edtgasqty.setText("");
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
                        progressBar.setVisibility(View.GONE);
                        isLoading=false;
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

    private void callAddEditPO() throws JSONException {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobPurchaseOrder/AddEditPO";
        final JSONObject jsonBody=new JSONObject();
        if(POId==0){
            jsonBody.put("POId",JSONObject.NULL);
        }else {
            jsonBody.put("POId",POId);
        }

        jsonBody.put("POGeneratedBy",POGeneratedBy);
        jsonBody.put("PODate",PoDate);
        jsonBody.put("UserId",UserId);
        jsonBody.put("PONumber",PONumber+"");
        jsonBody.put("CreatedBy",Integer.parseInt(settings.getString("userId","1")));
        jsonBody.put("ClientPOReference",edtClientPOReference.getText().toString()+"");
        if(txtClientPOUpload.getText().toString().equals(getResources().getString(R.string.NoFileChosen))){
            jsonBody.put("FilePath","");
        }else {
            jsonBody.put("FilePath",txtClientPOUpload.getText().toString()+"");
        }

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
                                POId=jsonObject.getInt("data");
                                lvTab1.setVisibility(View.GONE);
                                lvTab2.setVisibility(View.VISIBLE);
                                txtPurchaseOrderDetail.setTextColor(getResources().getColor(R.color.green));
                                txtClientInfo.setTextColor(getResources().getColor(R.color.lightGrey));
                                txtLineinfoUnderline.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                                txtPurchasodUnderline.setBackgroundColor(getResources().getColor(R.color.green));
                                if(isNetworkConnected()){
                                    callGetProductList();
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

    private void callGetProductList() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/api/MobProduct/GetProductList";
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
                        productList=new ArrayList<>();
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<jsonArray.length();i++){
                            HashMap<String,String> map=new HashMap<>();
                            map.put("productId",jsonArray.getJSONObject(i).getString("productId"));
                            map.put("productName",jsonArray.getJSONObject(i).getString("productName"));
                            map.put("unit",jsonArray.getJSONObject(i).getString("unit"));
                            imtes.add(jsonArray.getJSONObject(i).getString("productName"));
                            productList.add(map);
                        }
                        NSProduct.attachDataSource(imtes);
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

    public boolean validate() {
        boolean valid = true;

        if (PoNumber.isEmpty()) {
            edtPoNumber.setError("Field is Required.");
            valid = false;
        } else {
            edtPoNumber.setError(null);
        }
        if(PoDate.isEmpty()){
            edtPoDate.setError("Field is Required.");
            valid=false;
        }else {
            edtPoDate.setError(null);
        }
        if(userpos<=0){
            NSUserName1.setError("Field is Required.");
            valid=false;
        }else {
            NSUserName1.setError(null);
        }
        if(POGeneratedBy.isEmpty()){
            edtPOGeneratedBy.setError("Field is Required.");
            valid=false;
        }else {
            edtPOGeneratedBy.setError(null);
        }
        return valid;
    }

    public boolean validate1() {
        boolean valid = true;
        if(prodpos<=0){
            NSProduct.setError("Field is Required.");
            valid=false;
        }else {
            NSProduct.setError(null);
        }
        if(edtgasqty.getText().toString().isEmpty()){
            edtgasqty.setError("Field is Required.");
            valid=false;
        }else {
            edtgasqty.setError(null);
        }
        if(edtQuantity.getText().toString().isEmpty()){
            edtQuantity.setError("Field is Required.");
            valid=false;
        }else {
            edtQuantity.setError(null);
        }
        return valid;
    }
    private void callGetActiveUserData() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobUser/GetActiveUserData?CompanyId="+Integer.parseInt(settings.getString("companyId","1"))+
                "&UserType="+settings.getString("userType","1")+
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
                        NSUserName1.setAdapter(customSpinnerAdapter);

                        //NSUserName1.attachDataSource(imtes);

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
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        PoDate=sdf.format(myCalendar.getTime());
        String myFormat1 = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf1 = new SimpleDateFormat(myFormat1, Locale.US);
        edtPoDate.setText(sdf1.format(myCalendar.getTime()));
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
    public void callChangeCompanyStatus(String podetailid) {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobPurchaseOrder/DeletePODetail?PODetailId="+Integer.parseInt(podetailid)+
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
                        for(int i=0;i<podetailList.size();i++){
                            if(podetailList.get(i).get("podetailid").equals(podetailid)){
                                podetailList.remove(i);
                                break;
                            }
                        }
                        productAddListAdapter.notifyDataSetChanged();
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
    @SuppressLint("NewApi")
    private void dispatchTakePictureIntent() {
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }else{
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Log.d("result==>", "success");
            if(photo!=null) {
                uploadimage(photo,100);
            }
        }else if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            Bitmap photo = uriToBitmap(selectedImageUri);
            if(photo!=null) {
                uploadimage(photo,100);
            }
        }else if(requestCode == REQUEST_CODE_SELECT_DOCUMENT && resultCode == RESULT_OK) {
            Uri documentUri = data.getData();
            Log.d("documentUri==>",documentUri.toString()+"");
           // if (documentUri != null) {
                //String documentPath = getFilePath(documentUri);
                if (documentUri != null) {
                    // Upload the document to the server
                    uploadDocument(documentUri);
                } else {
                    Toast.makeText(this, "Unable to get the file path.", Toast.LENGTH_SHORT).show();
                }
            //}
        }
    }

    private void uploadDocument(Uri documentPath) {
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();

        // Create the file object using the file path
        File file = FileUtils.convertUriToFile(this, documentPath);

        if (!file.exists()) {
            Toast.makeText(context, "Document not found", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        // Setup Retrofit API client and interface
        MarketPlaceApiInterfacePO apiService = Apiclient.getClientPO().create(MarketPlaceApiInterfacePO.class);

        // Prepare the request body for the document file
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/*"), file);  // Adjust the MIME type for your document (e.g., application/pdf)

        // Create MultipartBody.Part for file upload
        MultipartBody.Part documentPart = MultipartBody.Part.createFormData("files", file.getName(), requestFile);

        // Make the API call to upload the document
        apiService.UpdateMarketPlaceProducts(Collections.singletonList(documentPart))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        try {
                            // Handle the server response and get the document URL
                            JSONObject j = new JSONObject(response.body().string());
                            String documentUrl = j.getString("data");
                            txtClientPOUpload.setText(documentUrl);  // You can display the URL or other response data here
                            Log.d("onResponse==>", "" + documentUrl);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("onFailure", "onResponse: " + t.getMessage());
                        progressDialog.dismiss();
                    }
                });
    }


    // Function to get the file path from the URI
    private String getFilePath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA }; // We use this for file paths in the media store
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;  // You can handle other types of URIs like content URIs here if needed
    }



    private void uploadimage(Bitmap signatureBitmap, int quality) {
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        MarketPlaceApiInterfacePO apiService = Apiclient.getClientPO().create(MarketPlaceApiInterfacePO.class);
        File file = new File(getCacheDir().getPath() +"photo"+PONumber+".jpg");
        try {
            OutputStream fOut = new FileOutputStream(file);
            // Directly write the Bitmap as PNG without compression
            signatureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // 100% quality (no compression)

            fOut.flush();
            fOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        Log.d("requestbody==>",requestFile+"");
        MultipartBody.Part BusinessImage = MultipartBody.Part.
                createFormData("files", file.getName(), requestFile);
        apiService.UpdateMarketPlaceProducts(Collections.singletonList(BusinessImage))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        JSONObject j;
                        try {

                            j = new JSONObject(response.body().string()+"");
                            imgUrl=j.getString("data");
                            txtClientPOUpload.setText(imgUrl);
                            Log.d("onResponse==>", "" + imgUrl);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        imgUrl="";
                        Log.d("onFailure", "onResponse: " + t.getMessage());
                        progressDialog.dismiss();

                    }
                });
    }
    private Bitmap uriToBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}