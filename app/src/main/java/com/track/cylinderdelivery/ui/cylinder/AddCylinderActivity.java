package com.track.cylinderdelivery.ui.cylinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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

import static com.track.cylinderdelivery.Dashboard.MY_PERMISSIONS_REQUEST_CAMERA;

public class AddCylinderActivity extends AppCompatActivity {

    Context context;
    Button btnCancel,btnSubmit;
    ImageView btnScanCylinders;
    TextView txtCylinderNos;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    ArrayList<String> qrcodeList;
    NiceSpinner spWarehouse;
    int wharehouspos=-1;
    EditText edtManufacturingDate,edtCompanyName,edtAddress1,edtAddress2,editCity,edtCountry,edtZipCode;
    EditText edtValveCompanyName,edtPurchaesDate,edtExpireDate,edtPaintExpireMonth,edtTestingPeriodMonth;
    Calendar myCalendar;
    String Company,Address1;
    String ManufacturingDate="";
    private String City,Country;
    private String ZipCode;
    private String ValueComanyName;
    private String PurchaeDate="";
    private String ExpireDate="";
    private String PaintExpireDays;
    private String TestingPeriodDays;
    ArrayList<HashMap<String,String>> wharehouselist;
    private String Address2;
    private SharedPreferences settings;
    private SharedPreferences CompanyUpdate;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cylinder);
        context=this;
        wharehouselist= (ArrayList<HashMap<String,String>>) getIntent().getSerializableExtra("wharehouselist");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CompanyUpdate=getSharedPreferences("cylinderEdit",MODE_PRIVATE);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Add Cylinder");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Add Cylinder</font>"));
        btnCancel=findViewById(R.id.btnCancel);
        btnSubmit=findViewById(R.id.btnSubmit);
        txtCylinderNos=findViewById(R.id.txtCylinderNos);
        btnScanCylinders=findViewById(R.id.btnScanCylinders);
        qrcodeList=new ArrayList<String>();
        spWarehouse=findViewById(R.id.spWarehouse);
        List<String> imtes=new ArrayList<>();
        imtes.add("Select");
        for(int i=0;i<wharehouselist.size();i++){
            imtes.add(wharehouselist.get(i).get("name")+"");
        }
        spWarehouse.attachDataSource(imtes);
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
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);

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
        edtExpireDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date2, myCalendar
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
        edtManufacturingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, myCalendar
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
        btnScanCylinders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddCylinderActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }else {
                    openQrScan();
                }
            }
        });
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
                        if(isNetworkConnected()){
                            callAddCylinder();
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
        txtCylinderNos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtCylinderNos.setError(null);
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
        jsonBody.put("CylinderId",JSONObject.NULL);
        JSONArray jsonArrayCylList=new JSONArray(qrcodeList);
        Log.d("jsonArraycyl==>",jsonArrayCylList.toString()+"");
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
                                SharedPreferences.Editor editor = CompanyUpdate.edit();
                                editor.putBoolean("refresh",true);
                                editor.commit();
                                finish();
                            }else {
                                //openQrScan();
                                //Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_SHORT).show();
                                MySingalton.showOkDilog(context,jsonObject.getString("message").toString()+"", "Add Cylinder");
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

    private void updateLabelExpireDate() {
        //String myFormat = "MM/dd/yy"; //In which you need put here
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
       // edtExpireDate.setText(sdf.format(myCalendar.getTime()));
        ExpireDate=sdf.format(myCalendar.getTime());

        String dmyFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat dsdf = new SimpleDateFormat(dmyFormat, Locale.US);
        edtExpireDate.setText(dsdf.format(myCalendar.getTime()));
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

    private void updateLabel() {
        //String myFormat = "MM/dd/yy"; //In which you need put here
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        //edtManufacturingDate.setText(sdf.format(myCalendar.getTime()));
        ManufacturingDate=sdf.format(myCalendar.getTime());

        String dispFormat="dd/MM/yyyy";
        SimpleDateFormat dipsdf = new SimpleDateFormat(dispFormat, Locale.US);
        edtManufacturingDate.setText(dipsdf.format(myCalendar.getTime()));
    }
    private void openQrScan() {
        txtCylinderNos.setError(null);
        Intent intent=new Intent(context,CylinderQRActivity.class);
        intent.putExtra("scanlist",qrcodeList);
        startActivityForResult(intent,201);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    public boolean validate() {
        boolean valid = true;
        if (qrcodeList.size()==0) {
            txtCylinderNos.setError("Field is Required.");
            valid = false;
        } else {
            txtCylinderNos.setError(null);
        }
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
        /*if(Address1.isEmpty()){
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
 /*       if(City.isEmpty()){
            editCity.setError("Field is Required.");
            valid=false;
        }else {
            editCity.setError(null);
        }*/
/*          if(Country.isEmpty()){
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