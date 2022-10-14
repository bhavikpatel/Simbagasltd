package com.track.cylinderdelivery.ui.cylinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class EditCylinderActivity extends AppCompatActivity {

    Context context;
    HashMap<String, String> mapdata;
    Button btnCancel,btnSubmit;
    TextView txtCylinderNos;
    NiceSpinner spWarehouse;
    ArrayList<HashMap<String,String>> wharehouselist;
    private SharedPreferences settings;
    private int CompanyId;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000;
    EditText edtManufacturingDate,edtCompanyName,edtAddress1,edtAddress2,editCity,edtCountry;
    EditText edtZipCode,edtValveCompanyName,edtPurchaesDate,edtExpireDate,edtPaintExpireMonth,edtTestingPeriodMonth;
    Calendar myCalendar;
    int wharehouspos=-1;

    String Company,Address1;
    String ManufacturingDate;
    private String City,Country;
    private String ZipCode;
    private String ValueComanyName;
    private String PurchaeDate;
    private String ExpireDate;
    private String PaintExpireDays;
    private String TestingPeriodDays;
    private String Address2;
    private SharedPreferences cylinderEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cylinder);
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("editData");
        context=this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Edit Cylinder");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Edit Cylinder</font>"));
        txtCylinderNos=findViewById(R.id.txtCylinderNos);
        spWarehouse=findViewById(R.id.spWarehouse);
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        cylinderEdit=context.getSharedPreferences("cylinderEdit",MODE_PRIVATE);
        CompanyId= Integer.parseInt(settings.getString("companyId","1"));
        txtCylinderNos.setText(mapdata.get("cylinderNo"));
        edtManufacturingDate=findViewById(R.id.edtManufacturingDate);
        edtCompanyName=findViewById(R.id.edtCompanyName);
        edtAddress1=findViewById(R.id.edtAddress1);
        edtAddress2=findViewById(R.id.edtAddress2);
        editCity=findViewById(R.id.editCity);
        edtCountry=findViewById(R.id.edtCountry);
        edtZipCode=findViewById(R.id.edtZipCode);
        edtValveCompanyName=findViewById(R.id.edtValveCompanyName);
        edtPurchaesDate=findViewById(R.id.edtPurchaesDate);
        edtExpireDate=findViewById(R.id.edtExpireDate);
        edtPaintExpireMonth=findViewById(R.id.edtPaintExpireMonth);
        edtTestingPeriodMonth=findViewById(R.id.edtTestingPeriodMonth);
        btnSubmit=findViewById(R.id.btnSubmit);
        btnCancel=findViewById(R.id.btnCancel);

        if(isNetworkConnected()){
            GetWarehouseList();
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }

        edtManufacturingDate.setText(mapdata.get("manufacturingDateStr"));
        ManufacturingDate=mapdata.get("manufacturingDate");
        edtCompanyName.setText(mapdata.get("companyName"));
        edtAddress1.setText(mapdata.get("address1"));
        edtAddress2.setText(mapdata.get("address2"));
        editCity.setText(mapdata.get("city"));
        edtCountry.setText(mapdata.get("county"));
        edtZipCode.setText(mapdata.get("zipCode"));
        edtValveCompanyName.setText(mapdata.get("valveCompanyName"));
        edtPurchaesDate.setText(mapdata.get("purchaseDateStr"));
        PurchaeDate=mapdata.get("purchaseDate");
        edtExpireDate.setText(mapdata.get("expireDateStr"));
        ExpireDate=mapdata.get("expireDate");
        edtPaintExpireMonth.setText(mapdata.get("paintExpireDays"));
        edtTestingPeriodMonth.setText(mapdata.get("testingPeriodDays"));

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ManufacturingDate=edtManufacturingDate.getText().toString();
                Company=edtCompanyName.getText().toString();
                Address1=edtAddress1.getText().toString();
                Address2=edtAddress2.getText().toString();
                City=editCity.getText().toString();
                Country=edtCountry.getText().toString();
                ZipCode=edtZipCode.getText().toString();
                ValueComanyName=edtValveCompanyName.getText().toString();
                //PurchaeDate=edtPurchaesDate.getText().toString();
                //ExpireDate=edtExpireDate.getText().toString();
                PaintExpireDays=edtPaintExpireMonth.getText().toString();
                TestingPeriodDays=edtTestingPeriodMonth.getText().toString();
                if(validate()){
                    try {
                        callAddCylinder();
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
        DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelPurchaesDate();
            }
        };
        DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelExpireDate();
            }
        };
        edtManufacturingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        edtPurchaesDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        edtExpireDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        spWarehouse.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                wharehouspos=position;
            }
        });
    }

        private void callAddCylinder() throws JSONException {
            Log.d("Api Calling==>","Api Calling");
            btnSubmit.setEnabled(false);
            final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
            progressDialog.show();
            String url = MySingalton.getInstance().URL+"/Api/MobCylinder/AddEdit";
            final JSONObject jsonBody=new JSONObject();
            SharedPreferences setting= getSharedPreferences("setting",MODE_PRIVATE);
            jsonBody.put("CylinderId",Integer.parseInt(mapdata.get("cylinderId")+""));
            JSONArray jsonArrayCylList=new JSONArray();
            jsonArrayCylList.put(mapdata.get("cylinderNo"));
            jsonBody.put("CylinderNoList",jsonArrayCylList);
            jsonBody.put("WarehouseId",Integer.parseInt(wharehouselist.get(wharehouspos-1).get("warehouseId")));
            jsonBody.put("ManufacturingDate",ManufacturingDate);
            jsonBody.put("CompanyName",Company);
            jsonBody.put("Address1",Address1+"");
            jsonBody.put("Address2",Address2+"");
            jsonBody.put("City",City+"");
            jsonBody.put("County",Country+"");
            jsonBody.put("ZipCode",ZipCode+"");
            jsonBody.put("valveCompanyName",ValueComanyName+"");
            jsonBody.put("PurchaseDate",PurchaeDate+"");
            jsonBody.put("ExpireDate",ExpireDate+"");
            jsonBody.put("PaintExpireDays",Integer.parseInt(PaintExpireDays));
            jsonBody.put("TestingPeriodDays",Integer.parseInt(TestingPeriodDays));
            jsonBody.put("CreatedBy",Integer.parseInt(settings.getString("userId","1")));

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
                                    SharedPreferences.Editor editor = cylinderEdit.edit();
                                    editor.putBoolean("refresh",true);
                                    editor.commit();
                                    finish();
                                }else {
                                    //Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_SHORT).show();
                                    MySingalton.showOkDilog(context,jsonObject.getString("message").toString()+"", "Edit Cylinder");
                                }
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
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(jsonObjectRequest);
        }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void updateLabel() {
        //String myFormat = "MM/dd/yy"; //In which you need put here
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        //edtManufacturingDate.setText(sdf.format(myCalendar.getTime()));
        ManufacturingDate=sdf.format(myCalendar.getTime());

        String dmyFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat dsdf = new SimpleDateFormat(dmyFormat, Locale.US);
        edtManufacturingDate.setText(dsdf.format(myCalendar.getTime()));
    }

    private void updateLabelPurchaesDate() {
        //String myFormat = "MM/dd/yy"; //In which you need put here
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        //edtPurchaesDate.setText(sdf.format(myCalendar.getTime()));
        PurchaeDate=sdf.format(myCalendar.getTime());

        String dmyFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat dsdf = new SimpleDateFormat(dmyFormat, Locale.US);
        edtPurchaesDate.setText(dsdf.format(myCalendar.getTime()));
    }
    private void updateLabelExpireDate() {
        //String myFormat = "MM/dd/yy"; //In which you need put here
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        //edtExpireDate.setText(sdf.format(myCalendar.getTime()));
        ExpireDate=sdf.format(myCalendar.getTime());

        String dmyFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat dsdf = new SimpleDateFormat(dmyFormat, Locale.US);
        edtExpireDate.setText(dsdf.format(myCalendar.getTime()));
    }
    private void GetWarehouseList() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobWarehouse/GetWarehouseList?CompanyId="+CompanyId;
        Log.d("request==>",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                List<String> imtes=new ArrayList<>();
                imtes.add("Select");
                progressDialog.dismiss();
                Log.d("resonse ==>",Response+"");
                JSONObject j;
                try {
                    j = new JSONObject(Response);
                    if(j.getBoolean("status")){
                        JSONArray jsonArray=j.getJSONArray("data");
                        wharehouselist=new ArrayList<>();

                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("warehouseId", jsonArray.getJSONObject(i).getInt("warehouseId")+"");
                            map.put("name", jsonArray.getJSONObject(i).getString("name") + "");
                            if(mapdata.get("warehousename").equals(jsonArray.getJSONObject(i).getString("name"))){
                                wharehouspos=i+1;
                            }
                            imtes.add(jsonArray.getJSONObject(i).getString("name") + "");
                            wharehouselist.add(map);
                        }
                    }
                    spWarehouse.attachDataSource(imtes);
                    if(wharehouspos!=0){
                        spWarehouse.setSelectedIndex(wharehouspos);
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

        if (wharehouspos<=0) {
            spWarehouse.setError("Field is Required.");
            valid = false;
        } else {
            spWarehouse.setError(null);
        }
        if (ManufacturingDate.isEmpty()) {
            edtManufacturingDate.setError("Field is Required.");
            valid = false;
        } else {
            edtManufacturingDate.setError(null);
        }
        if(Company.isEmpty()){
            edtCompanyName.setError("Field is Required.");
            valid=false;
        }else {
            edtCompanyName.setError(null);
        }
/*        if(Address1.isEmpty()){
            edtAddress1.setError("Field is Required.");
            valid=false;
        }else {
            edtAddress1.setError(null);
        }*/
/*        if(Address2.isEmpty()){
            edtAddress2.setError("Field is Required.");
            valid=false;
        }else {
            edtAddress2.setError(null);
        }*/
/*        if(City.isEmpty()){
            editCity.setError("Field is Required.");
            valid=false;
        }else {
            editCity.setError(null);
        }*/
/*        if(Country.isEmpty()){
            edtCountry.setError("Field is Required.");
            valid=false;
        }else{
            edtCountry.setError(null);
        }*/
/*        if(ZipCode.isEmpty()){
            edtZipCode.setError("Field is Required.");
            valid=false;
        }else{
            edtZipCode.setError(null);
        }*/
        if(ValueComanyName.isEmpty()){
            edtValveCompanyName.setError("Field is Required.");
            valid=false;
        }else{
            edtValveCompanyName.setError(null);
        }
        if(PurchaeDate.isEmpty()){
            edtPurchaesDate.setError("Field is Required.");
            valid=false;
        }else{
            edtPurchaesDate.setError(null);
        }
        if(ExpireDate.isEmpty()){
            edtExpireDate.setError("Field is Required.");
            valid=false;
        }else{
            edtExpireDate.setError(null);
        }
        if(PaintExpireDays.isEmpty()){
            edtPaintExpireMonth.setError("Field is Required.");
            valid=false;
        }else {
            edtExpireDate.setError(null);
        }
        if(TestingPeriodDays.isEmpty()){
            edtTestingPeriodMonth.setError("Field is Required.");
            valid=false;
        }else {
            edtTestingPeriodMonth.setError(null);
        }
        return valid;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}