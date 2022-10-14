package com.track.cylinderdelivery.ui.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.track.cylinderdelivery.ui.BaseActivity;
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

public class EditUserActivity extends BaseActivity {

    Context context;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    EditText edtName,edtAddress1,edtAddress2,editCity,edtCountry,edtZipCode;
    EditText edtMobile,edtSecondaryMobile,edtEmail,edtPassword,edtSecondaryEmail;
    EditText edtHoldingCapacity,edtCompanyName,edtPinNumber,edtPerMonReq,edtCylHolCreDay;
    EditText edtAlias,edtReferenceby,edtDepositAmount;
    Button btnSubmit,btnCancel;
    HashMap<String, String> mapdata;
    private SharedPreferences spUserFilter;
    int holdingCapacity=10;
    NiceSpinner NsCompanyCategory;
    private ArrayList<HashMap<String,String>> companyCatList;
    private int companycatpos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        context=this;
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("editData");
        Log.d("mapdata==>",mapdata+"");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        spUserFilter=getSharedPreferences("userFilter",MODE_PRIVATE);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Edit Customer");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Edit Customer</font>"));
        //RefreshUserList=true;
        //spinner_name=(AppCompatSpinner)findViewById(R.id.spinner_name);
        edtName= findViewById(R.id.edtName);
        edtDepositAmount=findViewById(R.id.edtDepositAmount);
        edtAddress1= findViewById(R.id.edtAddress1);
        edtAddress2= findViewById(R.id.edtAddress2);
        edtReferenceby=findViewById(R.id.edtReferenceby);
        editCity= findViewById(R.id.editCity);
        edtCountry= findViewById(R.id.edtCountry);
        edtZipCode= findViewById(R.id.edtZipCode);
        edtMobile= findViewById(R.id.edtMobile);
        edtSecondaryMobile= findViewById(R.id.edtSecondaryMobile);
        edtEmail= findViewById(R.id.edtEmail);
        edtPassword= findViewById(R.id.edtPassword);
        edtSecondaryEmail= findViewById(R.id.edtSecondaryEmail);
        btnSubmit= findViewById(R.id.btnSubmit);
        btnCancel= findViewById(R.id.btnCancel);
        edtHoldingCapacity=findViewById(R.id.edtHoldingCapacity);
        edtPerMonReq=findViewById(R.id.edtPerMonReq);
        edtCylHolCreDay=findViewById(R.id.edtCylHolCreDay);
        NsCompanyCategory=findViewById(R.id.NsCompanyCategory);
        edtCompanyName=findViewById(R.id.edtCompanyName);
        edtPinNumber=findViewById(R.id.edtPinNumber);
        edtAlias=findViewById(R.id.edtAlias);

        edtName.setText(mapdata.get("fullName"));
        edtAlias.setText(mapdata.get("userAlias"));
        edtAddress1.setText(mapdata.get("address1"));
        edtAddress2.setText(mapdata.get("address2"));
        editCity.setText(mapdata.get("city"));
        edtCountry.setText(mapdata.get("county"));
        edtZipCode.setText(mapdata.get("zipCode"));
        edtMobile.setText(mapdata.get("phone"));
        edtSecondaryMobile.setText(mapdata.get("secondaryPhone"));
        edtEmail.setText(mapdata.get("email"));
        edtSecondaryEmail.setText(mapdata.get("secondaryEmail"));
        edtPassword.setText(mapdata.get("emailPassword"));
        edtHoldingCapacity.setText(mapdata.get("holdingCapacity"));
        edtPerMonReq.setText(mapdata.get("perMonthRequirement"));
        edtCylHolCreDay.setText(mapdata.get("cylinderHoldingCreditDays"));
        edtCompanyName.setText(mapdata.get("nameOfCompany")+"");
        edtPinNumber.setText(mapdata.get("taxNumber")+"");
        edtReferenceby.setText(mapdata.get("referenceBy"));
        edtDepositAmount.setText(mapdata.get("depositAmount"));

        if(isNetworkConnected()){
            getCompanyCategoryList();
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate(edtName.getText().toString().trim(),edtAddress1.getText().toString().trim(),
                        editCity.getText().toString(),edtCountry.getText().toString().trim(),
                        edtZipCode.getText().toString().trim(),edtMobile.getText().toString().trim(),
                        edtEmail.getText().toString().trim(),edtPassword.getText().toString().trim(),
                        edtAddress2.getText().toString().trim(),edtSecondaryMobile.getText().toString().trim(),
                        edtSecondaryEmail.getText().toString().trim(),edtCompanyName.getText().toString().trim(),
                        edtPinNumber.getText().toString().trim(),edtPerMonReq.getText().toString().trim(),
                        edtCylHolCreDay.getText().toString().trim(),
                        edtAlias.getText().toString().trim())){
                    try {
                        if(isNetworkConnected()){
                            if(edtHoldingCapacity.getText().toString().trim().length()!=0){
                                holdingCapacity=Integer.parseInt(edtHoldingCapacity.getText().toString());
                                callEditUserApi();
                            }
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
        NsCompanyCategory.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                hideSoftKeyboard(view);
                companycatpos=position;
            }
        });
        NsCompanyCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
            }
        });
    }

    private void getCompanyCategoryList() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobCompany/CompanyCategoryList";
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
                        companyCatList = new ArrayList<>();
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("disabled", jsonArray.getJSONObject(i).getString("disabled")+"");
                            map.put("group", jsonArray.getJSONObject(i).getString("group") + "");
                            map.put("selected",jsonArray.getJSONObject(i).getBoolean("selected")+"");
                            map.put("text",jsonArray.getJSONObject(i).getString("text")+"");
                            map.put("value",jsonArray.getJSONObject(i).getString("value")+"");
                            if(mapdata.get("companyCategory").equals(jsonArray.getJSONObject(i).getString("value"))){
                                companycatpos=i+1;
                            }
                            imtes.add(jsonArray.getJSONObject(i).getString("value") + "");
                            companyCatList.add(map);
                        }
                        NsCompanyCategory.attachDataSource(imtes);
                        NsCompanyCategory.setSelectedIndex(companycatpos);
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
        queue.add(stringRequest);
    }

    private void callEditUserApi() throws JSONException {
        Log.d("Api Calling==>","Api Calling");
        btnSubmit.setEnabled(false);
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(EditUserActivity.this, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/api/MobUser/AddEdit";
        final JSONObject jsonBody=new JSONObject();
        SharedPreferences setting= getSharedPreferences("setting",MODE_PRIVATE);
        jsonBody.put("UserId",Integer.parseInt(mapdata.get("userId")));
        jsonBody.put("FullName",edtName.getText().toString().trim()+"");
        jsonBody.put("UserAlias",edtAlias.getText().toString().trim()+"");
        jsonBody.put("CompanyId",Integer.parseInt(mapdata.get("companyId")));
        jsonBody.put("Address1",edtAddress1.getText().toString().trim()+"");
        jsonBody.put("Address2",edtAddress2.getText().toString().trim()+"");
        jsonBody.put("City",editCity.getText().toString().trim()+"");
        jsonBody.put("County",editCity.getText().toString().trim()+"");
        jsonBody.put("ZipCode",edtZipCode.getText().toString().trim()+"");
        jsonBody.put("Phone",edtMobile.getText().toString().trim()+"");
        jsonBody.put("SecondaryPhone",edtSecondaryMobile.getText().toString().trim()+"");
        jsonBody.put("HoldingCapacity",holdingCapacity);
        jsonBody.put("TaxNumber",edtPinNumber.getText().toString().trim()+"");
        jsonBody.put("Email",edtEmail.getText().toString().trim()+"");
        jsonBody.put("SecondaryEmail",edtSecondaryEmail.getText().toString().trim()+"");
        jsonBody.put("EmailPassword",edtPassword.getText().toString().trim()+"");
        //jsonBody.put("accNo",Integer.parseInt(mapdata.get("accNo")));
        jsonBody.put("UserType","Client");
        jsonBody.put("CreatedBy",Integer.parseInt(setting.getString("userId","")));
        jsonBody.put("ModifiedBy",Integer.parseInt(setting.getString("userId","")));
        jsonBody.put("NameOfCompany",edtCompanyName.getText().toString().trim()+"");
        jsonBody.put("CompanyCategory",companyCatList.get(companycatpos-1).get("value"));
        jsonBody.put("PerMonthRequirement",Integer.parseInt(edtPerMonReq.getText().toString().trim()));
        jsonBody.put("CylinderHoldingCreditDays",Integer.parseInt(edtCylHolCreDay.getText().toString().trim()));
        jsonBody.put("ReferenceBy",edtReferenceby.getText().toString().trim());
        String depositAmount=edtDepositAmount.getText().toString().trim();
        if(depositAmount==null || depositAmount.equals("") || depositAmount.equals("null")){
            depositAmount="0";
        }
        jsonBody.put("DepositAmount",Float.parseFloat(depositAmount));


        Log.d("jsonRequest==>",jsonBody.toString()+"");

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,url,jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        btnSubmit.setEnabled(false);
                        progressDialog.dismiss();
                        Log.d("response==>",response.toString()+"");
                        try{
                            JSONObject jsonObject=response;
                            if(jsonObject.getBoolean("status")){
                                Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor userFilterEditor = spUserFilter.edit();
                                userFilterEditor.putBoolean("dofilter",true);
                                userFilterEditor.commit();
                                finish();
                            }else {
                                //Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_SHORT).show();
                                MySingalton.showOkDilog(context,jsonObject.getString("message").toString()+"", "Edit Customer");
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
    public boolean validate(String fullName, String Address1, String City, String County,
                            String ZipCode, String Phone, String Email, String EmailPassword,
                            String address2, String SecondaryMobile, String SecondaryEmail,
                            String nameOfCompany, String pinNumber, String PerMonReq, String cylHoldDay,
                            String alias) {
        boolean valid = true;

        if (alias.isEmpty()) {
            edtAlias.setError("Field is Required.");
            valid = false;
        } else {
            edtAlias.setError(null);
        }
        if (cylHoldDay.isEmpty()) {
            edtCylHolCreDay.setError("Field is Required.");
            valid = false;
        } else {
            edtCylHolCreDay.setError(null);
        }
        if (PerMonReq.isEmpty()) {
            edtPerMonReq.setError("Field is Required.");
            valid = false;
        } else {
            edtPerMonReq.setError(null);
        }

        if (nameOfCompany.isEmpty()) {
            edtCompanyName.setError("Field is Required.");
            valid = false;
        } else {
            edtCompanyName.setError(null);
        }

        if (pinNumber.isEmpty()) {
            edtPinNumber.setError("Field is Required.");
            valid = false;
        } else {
            edtPinNumber.setError(null);
        }
/*        if (SecondaryEmail.isEmpty()) {
            edtSecondaryEmail.setError("Field is Required.");
            valid = false;
        } else {
            edtSecondaryEmail.setError(null);
        }*/
/*        if (SecondaryMobile.isEmpty()) {
            edtSecondaryMobile.setError("Field is Required.");
            valid = false;
        } else {
            edtSecondaryMobile.setError(null);
        }*/
/*        if (address2.isEmpty()) {
            edtAddress2.setError("Field is Required.");
            valid = false;
        } else {
            edtAddress2.setError(null);
        }*/
        if (companycatpos<=0) {
            NsCompanyCategory.setError("Field is Required.");
            valid = false;
        } else {
            NsCompanyCategory.setError(null);
        }
        if (fullName.isEmpty()) {
            edtName.setError("Field is Required.");
            valid = false;
        } else {
            edtName.setError(null);
        }
        if (Address1.isEmpty()) {
            edtAddress1.setError("Field is Required.");
            valid = false;
        } else {
            edtAddress1.setError(null);
        }
        if(City.isEmpty()){
            editCity.setError("Field is Required.");
            valid=false;
        }else {
            editCity.setError(null);
        }
        if(County.isEmpty()){
            edtCountry.setError("Field is Required.");
            valid=false;
        }else{
            edtCountry.setError(null);
        }
        if(ZipCode.isEmpty()){
            edtZipCode.setError("Field is Required.");
            valid=false;
        }else{
            edtZipCode.setError(null);
        }
        if(Phone.isEmpty()){
            edtMobile.setError("Field is Required.");
            valid=false;
        }else{
            edtMobile.setError(null);
        }
        if(Email.isEmpty()){
            edtEmail.setError("Field is Required.");
            valid=false;
        }else{
            edtEmail.setError(null);
        }
        String emailPattern = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        if (edtEmail.getText().toString().matches(emailPattern)) {
            edtEmail.setError(null);
        }else{
            edtEmail.setError("Invalid email address.");
            valid=false;
        }
        if(EmailPassword.isEmpty()){
            edtPassword.setError("Field is Required.");
            valid=false;
        }else{
            edtPassword.setError(null);
        }
        return valid;
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
