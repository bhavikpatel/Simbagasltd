package com.track.cylinderdelivery.ui.CylinderWarehouseMapping;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ui.cylinder.CylinderQRActivity;
import com.track.cylinderdelivery.ui.cylinderproductmapping.AddCylinderProductMappingActivity;
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

public class AddCylinderWarehouseMapping extends AppCompatActivity {
    Context context;
    private ArrayList<HashMap<String, String>> wharehouselist;
    Button btnCancel,btnSubmit;
    TextView txtCylinderNos;
    ArrayList<String> qrcodeList;
    ImageView btnScanCylinders;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    NiceSpinner spFromWarehouse,spToWarehouse;
    private int fromwharehouspos=-1;
    private int towarehousepos=-1;
    SharedPreferences spFilledFilter;
    String SignImage="";
    private TextView txtSignaure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cylinder_warehouse_mapping);
        context=this;
        wharehouselist= (ArrayList<HashMap<String,String>>) getIntent().getSerializableExtra("wharehouselist");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Add Cylinder Warehouse Mapping");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Add Cylinder Warehouse Mapping</font>"));
        btnCancel=findViewById(R.id.btnCancel);
        txtCylinderNos=findViewById(R.id.txtCylinderNos);
        qrcodeList=new ArrayList<String>();
        btnScanCylinders=findViewById(R.id.btnScanCylinders);
        spFromWarehouse=findViewById(R.id.spFromWarehouse);
        spToWarehouse=findViewById(R.id.spToWarehouse);
        btnSubmit=findViewById(R.id.btnSubmit);
        txtSignaure=findViewById(R.id.txtSignaure);
        spFilledFilter=context.getSharedPreferences("filledCylFilter",MODE_PRIVATE);
        List<String> imtes=new ArrayList<>();
        imtes.add("Select");
        for(int i=0;i<wharehouselist.size();i++){
            imtes.add(wharehouselist.get(i).get("name")+"");
        }
        spFromWarehouse.attachDataSource(imtes);
        spToWarehouse.attachDataSource(imtes);

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
/*                    if(isNetworkConnected()){
                        if(fromwharehouspos!=towarehousepos){
                            try {
                                callAddCylinderWarehouseMapping();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(context, "Same Whare house!.", Toast.LENGTH_LONG).show();
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
                finish();
            }
        });

        btnScanCylinders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddCylinderWarehouseMapping.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }else {
                    openQrScan();
                }
            }
        });
        spFromWarehouse.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
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

    private void callAddCylinderWarehouseMapping() throws JSONException {
        Log.d("Api Calling==>","Api Calling");
        btnSubmit.setEnabled(false);
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL +"/Api/MobCylinderWarehouseMapping/AddCylinderWarehouseMapping";
        Log.d("url==>",url);
        final JSONObject jsonBody=new JSONObject();
        SharedPreferences setting= getSharedPreferences("setting",MODE_PRIVATE);
        jsonBody.put("FromWarehouseId",Integer.parseInt(wharehouselist.get(fromwharehouspos-1).get("warehouseId")));
        jsonBody.put("WarehouseId",Integer.parseInt(wharehouselist.get(towarehousepos-1).get("warehouseId")));
        JSONArray jsonArrayCylList=new JSONArray(qrcodeList);
        jsonBody.put("CylinderList",jsonArrayCylList);
        jsonBody.put("CompanyId",Integer.parseInt(setting.getString("CompanyId","1")));
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
                                SharedPreferences.Editor userFilterEditor = spFilledFilter.edit();
                                userFilterEditor.putBoolean("dofilter",true);
                                userFilterEditor.commit();
                                finish();
                            }else {
                                //openQrScan();
                               // Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_SHORT).show();
                                MySingalton.showOkDilog(context,jsonObject.getString("message").toString()+"", "Add Cylinder Warehouse Mapping");
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
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
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
                if(fromwharehouspos!=towarehousepos){
                    try {
                        if(SignImage.length()!=0) {
                            callAddCylinderWarehouseMapping();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(context, "Same Whare house!.", Toast.LENGTH_LONG).show();
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
            spFromWarehouse.setError("Field is Required.");
            valid = false;
        } else {
            spFromWarehouse.setError(null);
        }
        /*if(SignImage==null || SignImage.length()==0){
            txtSignaure.setError("Signature is Required.");
            valid=false;
        }else {
            txtSignaure.setError(null);
        }*/
        return valid;
    }
}