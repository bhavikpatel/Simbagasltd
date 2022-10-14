package com.track.cylinderdelivery.ui.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.track.cylinderdelivery.ui.BaseActivity;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserAcknoActivity extends BaseActivity {

    private HashMap<String, String> mapdata;
    Context context;
    EditText edtDate,edtRemark;
    private Calendar myCalendar;
    Button btnCancel,btnSubmit;
    private EditText edtCompany,edtUserName;
    private SharedPreferences settings;
    private SharedPreferences spUserFilter;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ackno);
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("editData");
        context=this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Add User");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Acknowledge </font>"));
        spUserFilter=context.getSharedPreferences("userFilter",MODE_PRIVATE);

        edtDate=findViewById(R.id.edtDate);
        btnCancel=findViewById(R.id.btnCancel);
        btnSubmit=findViewById(R.id.btnSubmit);
        edtCompany=findViewById(R.id.edtCompany);
        edtUserName=findViewById(R.id.edtUserName);
        edtRemark=findViewById(R.id.edtRemark);
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);

        edtCompany.setText(settings.getString("companyId","1"));
        edtUserName.setText(mapdata.get("fullName"));

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
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(context, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validate(edtCompany.getText().toString().trim(),edtUserName.getText().toString().trim(),
                        edtDate.getText().toString().trim(),edtRemark.getText().toString().trim())){
                    try {
                        if(isNetworkConnected()){
                            callAddUserApi(Integer.parseInt(mapdata.get("userId")),
                                    edtRemark.getText().toString().trim(),
                                    Integer.parseInt(settings.getString("userId","")),
                                    edtDate.getText().toString().trim());
                        }else {
                            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void callAddUserApi(int UserId, String Remark, int CreatedBy, String CreatedDate) throws JSONException {
        Log.d("Api Calling==>","Api Calling");
        btnSubmit.setEnabled(false);
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"MobAcknowledge/CreateAcknowledge";
        final JSONObject jsonBody=new JSONObject();
        jsonBody.put("UserId",UserId);
        jsonBody.put("Remark",Remark+"");
        jsonBody.put("CreatedBy",CreatedBy);
        jsonBody.put("CreatedDate",CreatedDate+"");

        final String v = jsonBody.toString();
        Log.d("parambers==>",v);

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
                               Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor userFilterEditor = spUserFilter.edit();
                                userFilterEditor.putBoolean("dofilter",false);
                                userFilterEditor.commit();
                               finish();
                            }else {
                                //Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_SHORT).show();
                                MySingalton.showOkDilog(context,jsonObject.getString("message").toString()+"", "Add User");
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

    private void updateLabel() {
        //String myFormat = "MM/dd/yy"; //In which you need put here
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edtDate.setText(sdf.format(myCalendar.getTime()));
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public boolean validate(String fullName,String username,String date,String remark) {
        boolean valid = true;

        if (fullName.isEmpty()) {
            edtCompany.setError("Field is Required.");
            valid = false;
        } else {
            edtCompany.setError(null);
        }
        if (username.isEmpty()) {
            edtUserName.setError("Field is Required.");
            valid = false;
        } else {
            edtUserName.setError(null);
        }
        if(date.isEmpty()){
            edtDate.setError("Field is Required.");
            valid=false;
        }else {
            edtDate.setError(null);
        }
        if(remark.isEmpty()){
            edtRemark.setError("Field is Required.");
            valid=false;
        }else{
            edtRemark.setError(null);
        }
        return valid;
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}