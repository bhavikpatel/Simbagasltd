package com.track.cylinderdelivery.ui.user;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
import com.track.cylinderdelivery.Dashboard;
import com.track.cylinderdelivery.LoginActivity;
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

public class AddUserActivity extends BaseActivity {


    Context context;
    EditText edtName,edtAddress1,edtAddress2,editCity,edtCountry,edtZipCode,edtCompanyName;
    EditText edtMobile,edtSecondaryMobile,edtEmail,edtPassword,edtSecondaryEmail,edtPinNumber;
    EditText edtAlias,edtReferenceby,edtDepositAmount;
    Button btnSubmit,btnCancel;
    private SharedPreferences spUserFilter;
    NiceSpinner NsCompanyCategory;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    private EditText edtHoldingCapacity,edtPerMonReq,edtCylHolCreDay;
    private ArrayList<HashMap<String,String>> companyCatList;
    private int companycatpos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        context=this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        spUserFilter=getSharedPreferences("userFilter",MODE_PRIVATE);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Add Customer");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Add Customer</font>"));
        //RefreshUserList=true;
        edtName=(EditText)findViewById(R.id.edtName);
        edtAddress1=(EditText)findViewById(R.id.edtAddress1);
        edtAddress2=(EditText)findViewById(R.id.edtAddress2);
        editCity=(EditText)findViewById(R.id.editCity);
        edtCountry=(EditText)findViewById(R.id.edtCountry);
        edtZipCode=(EditText)findViewById(R.id.edtZipCode);
        edtMobile=(EditText)findViewById(R.id.edtMobile);
        edtSecondaryMobile=(EditText)findViewById(R.id.edtSecondaryMobile);
        edtEmail=(EditText)findViewById(R.id.edtEmail);
        edtPassword=(EditText)findViewById(R.id.edtPassword);
        edtSecondaryEmail=(EditText)findViewById(R.id.edtSecondaryEmail);
        btnSubmit=(Button)findViewById(R.id.btnSubmit);
        btnCancel=(Button)findViewById(R.id.btnCancel);
        NsCompanyCategory=findViewById(R.id.NsCompanyCategory);
        edtHoldingCapacity=findViewById(R.id.edtHoldingCapacity);
        edtPinNumber=findViewById(R.id.edtPinNumber);
        edtCompanyName=findViewById(R.id.edtCompanyName);
        edtPerMonReq=findViewById(R.id.edtPerMonReq);
        edtCylHolCreDay=findViewById(R.id.edtCylHolCreDay);
        edtAlias=findViewById(R.id.edtAlias);
        edtReferenceby=findViewById(R.id.edtReferenceby);
        edtDepositAmount=findViewById(R.id.edtDepositAmount);
        if(isNetworkConnected()){
            getCompanyCategoryList();
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FullName=edtName.getText().toString().trim();
                int CompanyId=1;
                String Address1=edtAddress1.getText().toString().trim();
                String Address2=edtAddress2.getText().toString().trim();
                String City=editCity.getText().toString().trim();
                String County=edtCountry.getText().toString().trim();
                String ZipCode=edtZipCode.getText().toString().trim();
                String Phone=edtMobile.getText().toString().trim();
                String SecondaryPhone=edtSecondaryMobile.getText().toString().trim();
                String Email=edtEmail.getText().toString().trim();
                String SecondaryEmail=edtSecondaryEmail.getText().toString().trim();
                String EmailPassword=edtPassword.getText().toString().trim();
                String UserType="Client";
                String PinNumber=edtPinNumber.getText().toString().trim();
                String NameOfCompany=edtCompanyName.getText().toString().trim();
                int CreatedBy=1;
                int ModifiedBy=1;
                int HoldingCapacity=10;

                if(edtHoldingCapacity.getText().toString().trim().length()!=0){
                    HoldingCapacity=Integer.parseInt(edtHoldingCapacity.getText().toString());
                }

                if(validate(FullName,Address1,City,County,ZipCode,Phone,Email,EmailPassword,
                        Address2,SecondaryPhone,SecondaryEmail,PinNumber,NameOfCompany,
                        edtPerMonReq.getText().toString().trim(),edtCylHolCreDay.getText().toString().trim(),
                        edtAlias.getText().toString().trim())){
                    try {
                        if(isNetworkConnected()){
                            callAddUserApi(FullName,CompanyId,Address1,Address2,City,County,ZipCode,Phone,
                                    SecondaryPhone,HoldingCapacity,Email,SecondaryEmail,EmailPassword,
                                    UserType,CreatedBy,ModifiedBy,PinNumber,NameOfCompany,
                                    edtPerMonReq.getText().toString().trim(),edtCylHolCreDay.getText().toString().trim(),
                                    edtAlias.getText().toString().trim(),edtReferenceby.getText().toString().trim(),
                                    edtDepositAmount.getText().toString().trim());
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

                            imtes.add(jsonArray.getJSONObject(i).getString("value") + "");
                            companyCatList.add(map);
                        }
                        NsCompanyCategory.attachDataSource(imtes);
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
    private void callAddUserApi(String FullName, int CompanyId, String Address1,
                                String Address2, String City, String County,
                                String ZipCode, String Phone, String SecondaryPhone,
                                int HoldingCapacity, String Email, String SecondaryEmail,
                                String EmailPassword, String UserType, int CreatedBy
            , int ModifiedBy, String pinNumber, String NameOfCompany, String reqPerMon, String cylHoldCap,
                                String alias,String referenceby,String depositAmount) throws JSONException {
        Log.d("Api Calling==>","Api Calling");
        btnSubmit.setEnabled(false);
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(AddUserActivity.this, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/api/MobUser/AddEdit";
        Log.d("requestUrl==>",url);
        final JSONObject jsonBody=new JSONObject();
        jsonBody.put("UserId",JSONObject.NULL);
        jsonBody.put("FullName",FullName+"");
        jsonBody.put("UserAlias",alias+"");
        jsonBody.put("CompanyId",CompanyId);
        jsonBody.put("Address1",Address1+"");
        jsonBody.put("Address2",Address2+"");
        jsonBody.put("City",City+"");
        jsonBody.put("County",County+"");
        jsonBody.put("ZipCode",ZipCode+"");
        jsonBody.put("Phone",Phone+"");
        jsonBody.put("SecondaryPhone",SecondaryPhone+"");
        jsonBody.put("HoldingCapacity",HoldingCapacity);
        jsonBody.put("TaxNumber",pinNumber);
        jsonBody.put("Email",Email+"");
        jsonBody.put("SecondaryEmail",SecondaryEmail+"");
        jsonBody.put("EmailPassword",EmailPassword+"");
        //jsonBody.put("AccNo",AccNo);
        jsonBody.put("UserType","Client");
        jsonBody.put("CreatedBy",CreatedBy);
        jsonBody.put("ModifiedBy",ModifiedBy);
        jsonBody.put("PerMonthRequirement",Integer.parseInt(reqPerMon));
        jsonBody.put("CylinderHoldingCreditDays",Integer.parseInt(cylHoldCap));

        jsonBody.put("NameOfCompany",NameOfCompany);
        jsonBody.put("CompanyCategory",companyCatList.get(companycatpos-1).get("value"));
        jsonBody.put("ReferenceBy",referenceby);
        if(depositAmount==null || depositAmount.equals("") || depositAmount.equals("null")){
            depositAmount="0";
        }
        jsonBody.put("DepositAmount",Float.parseFloat(depositAmount));

        final String v = jsonBody.toString();
        Log.d("request==>",v);
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
                                userFilterEditor.putBoolean("dofilter",true);
                                userFilterEditor.commit();
                                finish();
                            }else {
                               // Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_SHORT).show();
                                MySingalton.showOkDilog(context,jsonObject.getString("message").toString()+"", "Add Customer");
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

    public boolean validate(String fullName, String Address1, String City, String County,
                            String ZipCode, String Phone, String Email, String EmailPassword,
                            String Address2, String SecondaryPhone, String SecondaryEmail,
                            String pinNumber, String nameOfCompany, String reqPerMon, String cylHoldDay,
                            String alias) {
        boolean valid = true;

/*
        if (SecondaryPhone.isEmpty()) {
            edtSecondaryMobile.setError("Field is Required.");
            valid = false;
        } else {
            edtSecondaryMobile.setError(null);
        }*/
/*        if (Address2.isEmpty()) {
            edtAddress2.setError("Field is Required.");
            valid = false;
        } else {
            edtAddress2.setError(null);
        }*/

        if (cylHoldDay.isEmpty()) {
            edtHoldingCapacity.setError("Field is Required.");
            valid = false;
        } else {
            edtHoldingCapacity.setError(null);
        }
        if (reqPerMon.isEmpty()) {
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
       // String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String emailPattern = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$";
        if (edtEmail.getText().toString().matches(emailPattern)) {
            edtEmail.setError(null);
        }else{
            edtEmail.setError("Invalid email address.");
            valid=false;
        }
        if (companycatpos<=0) {
            NsCompanyCategory.setError("Field is Required.");
            valid = false;
        } else {
            NsCompanyCategory.setError(null);
        }
/*        if (SecondaryEmail.isEmpty()) {
            edtSecondaryEmail.setError("Field is Required.");
            valid = false;
        } else {
            edtSecondaryEmail.setError(null);
        }
        if (edtSecondaryEmail.getText().toString().matches(emailPattern)) {
            edtSecondaryEmail.setError(null);
        }else{
            edtSecondaryEmail.setError("valid email address.");
            valid=false;
        }*/
        if(EmailPassword.isEmpty()){
            edtPassword.setError("Field is Required.");
            valid=false;
        }else{
            edtPassword.setError(null);
        }

        if(alias.isEmpty()){
            edtAlias.setError("Field is Required.");
            valid=false;
        }else{
            edtAlias.setError(null);
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