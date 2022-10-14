package com.track.cylinderdelivery.ui.cylinderproductmapping;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.Button;
import android.widget.DatePicker;
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
import com.track.cylinderdelivery.ui.cylinder.AddCylinderActivity;
import com.track.cylinderdelivery.ui.cylinder.CylinderQRActivity;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddCylinderProductMappingActivity extends AppCompatActivity {
    Context context;
    ImageView btnScanCylinders;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    TextView txtCylinderNos;
    ArrayList<String> qrcodeList;
    private ArrayList<HashMap<String, String>> wharehouselist;
    ArrayList<HashMap<String,String>> prowhereHouseList,productlist;
    NiceSpinner spFromWarehouse;
    NiceSpinner spToWarehouse,spProduct;
    int fromwharehouspos=-1;
    int towarehousepos=-1;
    int productpos=-1;
    int ProdutId=0;
    private int CompanyId;
    private SharedPreferences settings;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    EditText edtFillingDate;
    Calendar myCalendar;
    Button btnCancel,btnSubmit;
    private String CylinderNos;
    private String FillingDate="";
    EditText edtQuantity,edtPurity,edtGaugePressure,edtImpurities;
    private String Quantity;
    private String Purity;
    private String GaugePressure;
    private String Impurities;

    SharedPreferences cylindeMappingActivity;
    EditText edtUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cylinder_product_mapping);
        context=this;
        wharehouselist= (ArrayList<HashMap<String,String>>) getIntent().getSerializableExtra("wharehouselist");
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Add Cylinder Product Mapping");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Add Cylinder Product Mapping</font>"));
        btnScanCylinders=findViewById(R.id.btnScanCylinders);
        txtCylinderNos=findViewById(R.id.txtCylinderNos);
        spFromWarehouse=findViewById(R.id.spFromWarehouse);
        spToWarehouse=findViewById(R.id.spToWarehouse);
        edtFillingDate=findViewById(R.id.edtFillingDate);
        btnCancel=findViewById(R.id.btnCancel);
        btnSubmit=findViewById(R.id.btnSubmit);
        spProduct=findViewById(R.id.spProduct);
        edtQuantity=findViewById(R.id.edtQuantity);
        edtPurity=findViewById(R.id.edtPurity);
        edtGaugePressure=findViewById(R.id.edtGaugePressure);
        edtImpurities=findViewById(R.id.edtImpurities);
        cylindeMappingActivity=context.getSharedPreferences("CPMSoring",MODE_PRIVATE);
        edtUnit=findViewById(R.id.edtUnit);

        qrcodeList=new ArrayList<String>();
        List<String> imtes=new ArrayList<>();
        imtes.add("Select");
        for(int i=0;i<wharehouselist.size();i++){
            imtes.add(wharehouselist.get(i).get("name")+"");
        }
        spFromWarehouse.attachDataSource(imtes);

        CompanyId= Integer.parseInt(settings.getString("companyId","1"));
        if(isNetworkConnected()){
            GetProductionWarehouseList();
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
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = cylindeMappingActivity.edit();
                editor.putBoolean("dofilter",true);
                editor.commit();

                CylinderNos=txtCylinderNos.getText().toString();
                //FillingDate=edtFillingDate.getText().toString();
                Quantity=edtQuantity.getText().toString();
                Purity=edtPurity.getText().toString();
                GaugePressure=edtGaugePressure.getText().toString();
                Impurities=edtImpurities.getText().toString();
                if(validate()){
                    try {
                        if(isNetworkConnected()){
                            callAddCylinderProductMapping();
                        }else {
                            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edtFillingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        spToWarehouse.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                towarehousepos=position;
            }
        });
        spFromWarehouse.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                fromwharehouspos=position;
            }
        });
        spProduct.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                productpos=position;
                if(position!=0) {
                    edtUnit.setText(productlist.get(position - 1).get("unit"));
                    ProdutId=Integer.parseInt(productlist.get(position-1).get("productId"));
                }
            }
        });
        btnScanCylinders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddCylinderProductMappingActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }else {
                    openQrScan();
                }
            }
        });
        txtCylinderNos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtCylinderNos.setError(null);
            }
        });
    }

    private void callAddCylinderProductMapping() throws JSONException {
        Log.d("Api Calling==>","Api Calling");
        btnSubmit.setEnabled(false);

        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();

        String url = MySingalton.getInstance().URL+"/Api/MobCylinderProductMapping/AddCylinderProductMapping";
        Log.d("url==>",url+"");
        final JSONObject jsonBody=new JSONObject();
        SharedPreferences setting= getSharedPreferences("setting",MODE_PRIVATE);
        jsonBody.put("CompanyId",Integer.parseInt(setting.getString("CompanyId","1")));
        JSONArray jsonArrayCylList=new JSONArray(qrcodeList);
        jsonBody.put("CylinderList",jsonArrayCylList);
        jsonBody.put("FromWarehouseId",Integer.parseInt(wharehouselist.get(fromwharehouspos-1).get("warehouseId")));
        jsonBody.put("WarehouseId",Integer.parseInt(prowhereHouseList.get(towarehousepos-1).get("warehouseId")));
        jsonBody.put("ProductId",ProdutId);
        jsonBody.put("Quantity",Integer.parseInt(Quantity));
        jsonBody.put("Purity",Purity);
        jsonBody.put("Impurities",Impurities);
        jsonBody.put("GaugePressure",GaugePressure);
        jsonBody.put("FillingDate",FillingDate);
        jsonBody.put("CreatedBy",Integer.parseInt(setting.getString("userId","1")));
        jsonBody.put("Unit",edtUnit.getText().toString());

        Log.d("jsonRequest==>",jsonBody.toString()+"");

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,url,jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //btnSubmit.setEnabled(true);
                        progressDialog.dismiss();
                        Log.d("response==>",response.toString()+"");
                        try{
                            JSONObject jsonObject=response;
                            if(jsonObject.getBoolean("status")){
                                Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = cylindeMappingActivity.edit();
                                editor.putBoolean("refresh",true);
                                editor.commit();
                                finish();
                            }else {
                                //openQrScan();
                                //Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_SHORT).show();
                                btnSubmit.setEnabled(true);
                                MySingalton.showOkDilog(context,jsonObject.getString("message").toString()+"", "Add Cylinder Product Mapping");
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            btnSubmit.setEnabled(true);
                            MySingalton.showOkDilog(context,"Error Generated!", "Add Cylinder Product Mapping");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btnSubmit.setEnabled(true);
                        progressDialog.dismiss();
                        Log.d("response==>",error.toString()+"");
                        MySingalton.showOkDilog(context,error.toString()+" Error Generated!", "Add Cylinder Product Mapping");
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
            spFromWarehouse.setError("Field is Required.");
            valid = false;
        } else {
            spFromWarehouse.setError(null);
        }
        if(FillingDate.isEmpty()){
            edtFillingDate.setError("Field is Required.");
            valid=false;
        }else {
            edtFillingDate.setError(null);
        }
        if(productpos<=0){
            spProduct.setError("Field is Required.");
            valid=false;
        }else {
            spProduct.setError(null);
        }
        if(Quantity.isEmpty()){
            edtQuantity.setError("Field is Required.");
            valid=false;
        }else {
            edtQuantity.setError(null);
        }
        if(Purity.isEmpty()){
            edtPurity.setError("Field is Required.");
            valid=false;
        }else{
            edtPurity.setError(null);
        }

        if(GaugePressure.isEmpty()){
            edtGaugePressure.setError("Field is Required.");
            valid=false;
        }else{
            edtGaugePressure.setError(null);
        }
        if(Impurities.isEmpty()){
            edtImpurities.setError("Field is Required.");
            valid=false;
        }else{
            edtImpurities.setError(null);
        }
        return valid;
    }
    private void updateLabel() {
        //String myFormat = "MM/dd/yy"; //In which you need put here
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        FillingDate=sdf.format(myCalendar.getTime());
        //edtFillingDate.setText(sdf.format(myCalendar.getTime()));

        String dmyFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat dsdf = new SimpleDateFormat(dmyFormat, Locale.US);
        edtFillingDate.setText(dsdf.format(myCalendar.getTime()));
    }
    private void GetProductionWarehouseList() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobWarehouse/GetProdutionWarehouseList?CompanyId="+CompanyId;
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
                        prowhereHouseList = new ArrayList<>();
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("warehouseId", jsonArray.getJSONObject(i).getInt("warehouseId")+"");
                            map.put("name", jsonArray.getJSONObject(i).getString("name") + "");
                            imtes.add(jsonArray.getJSONObject(i).getString("name") + "");
                            prowhereHouseList.add(map);
                        }
                        spToWarehouse.attachDataSource(imtes);
                    }else {
                        //Toast.makeText(context,j.getString("message").toString()+"",Toast.LENGTH_SHORT).show();
                        MySingalton.showOkDilog(context,j.getString("message").toString()+"", "Add Cylinder Product Mapping");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callGetProductList();
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
                        productlist = new ArrayList<>();
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("productId", jsonArray.getJSONObject(i).getInt("productId")+"");
                            map.put("productName", jsonArray.getJSONObject(i).getString("productName") + "");
                            map.put("unit",jsonArray.getJSONObject(i).getString("unit"));
                            imtes.add(jsonArray.getJSONObject(i).getString("productName") + "");
                            productlist.add(map);
                        }
                        Log.d("unitlist==>",productlist.toString()+"");
                        spProduct.attachDataSource(imtes);

                    }else {

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
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @SuppressLint("MissingPermission")
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}