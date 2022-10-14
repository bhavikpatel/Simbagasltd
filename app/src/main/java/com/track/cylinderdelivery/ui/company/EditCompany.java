package com.track.cylinderdelivery.ui.company;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.SearchView;
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
import com.track.cylinderdelivery.ui.user.EditUserActivity;
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

public class EditCompany extends AppCompatActivity {

    Context context;
    private HashMap<String, String> mapdata;
    Button btnCancel,btnSubmit;
    EditText edtName,edtContactPerName,edtAddress1,edtAddress2,editCity,edtCountry,edtZipCode;
    EditText edtTexNumber,edtEmail,edtSecondaryEmail,edtMobile,edtSecondaryMobile,edtPerMonReq;
    EditText edtHoldingCapacity,edtCylHolCreDay,edtAlias,edtReferenceby,edtDepositAmount;
    NiceSpinner spCompanyType,spCompanyCatergory;
    private SharedPreferences settings,CompanyUpdate;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    ArrayList<HashMap<String,String>> companyTypeList;
    private int companytypepos=0,companycatpos=0;
    private ArrayList<HashMap<String,String>> companyCatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_company);
        context=this;
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("editData");
        Log.d("mapdata==>",mapdata+"");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Edit Distributor");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Edit Distributor</font>"));
        btnCancel=(Button)findViewById(R.id.btnCancel);
        btnSubmit=(Button)findViewById(R.id.btnSubmit);
        edtHoldingCapacity=findViewById(R.id.edtHoldingCapacity);
        edtDepositAmount=findViewById(R.id.edtDepositAmount);
        edtName=findViewById(R.id.edtName);
        edtContactPerName=findViewById(R.id.edtContactPerName);
        edtAlias=findViewById(R.id.edtAlias);
        edtAddress1=findViewById(R.id.edtAddress1);
        edtAddress2=findViewById(R.id.edtAddress2);
        editCity=findViewById(R.id.editCity);
        edtCountry=findViewById(R.id.edtCountry);
        edtZipCode=findViewById(R.id.edtZipCode);
        edtTexNumber=findViewById(R.id.edtTexNumber);
        edtEmail=findViewById(R.id.edtEmail);
        edtSecondaryEmail=findViewById(R.id.edtSecondaryEmail);
        edtMobile=findViewById(R.id.edtMobile);
        edtSecondaryMobile=findViewById(R.id.edtSecondaryMobile);
        spCompanyType=findViewById(R.id.spCompanyType);
        spCompanyCatergory=findViewById(R.id.spCompanyCatergory);
        edtPerMonReq=findViewById(R.id.edtPerMonReq);
        edtCylHolCreDay=findViewById(R.id.edtCylHolCreDay);
        edtReferenceby=findViewById(R.id.edtReferenceby);
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        CompanyUpdate=context.getSharedPreferences("companyUpdate",MODE_PRIVATE);

        edtName.setText(mapdata.get("companyName")+"");
        edtContactPerName.setText(mapdata.get("adminName")+"");
        edtAddress1.setText(mapdata.get("address1")+"");
        edtAddress2.setText(mapdata.get("address2")+"");
        editCity.setText(mapdata.get("city")+"");
        edtCountry.setText(mapdata.get("county")+"");
        edtZipCode.setText(mapdata.get("zipCode")+"");
        edtTexNumber.setText(mapdata.get("taxNumber")+"");
        edtEmail.setText(mapdata.get("email")+"");
        edtAlias.setText(mapdata.get("companyAlias"));
        edtReferenceby.setText(mapdata.get("referenceBy"));
        edtDepositAmount.setText(mapdata.get("depositAmount"));
        if(mapdata.get("perMonthRequirement").equals(null) || mapdata.get("perMonthRequirement").equals("null")){
            edtPerMonReq.setText("");
        }else {
            edtPerMonReq.setText(mapdata.get("perMonthRequirement")+"");
        }
        if(mapdata.get("holdingCapacity").equals(null) || mapdata.get("holdingCapacity").equals("null")){
            edtHoldingCapacity.setText("");
        }else {
            edtHoldingCapacity.setText(mapdata.get("holdingCapacity")+"");
        }
        if(mapdata.get("cylinderHoldingCreditDays").equals(null) || mapdata.get("cylinderHoldingCreditDays").equals("null")){
            edtCylHolCreDay.setText("");
        }else {
            edtCylHolCreDay.setText(mapdata.get("cylinderHoldingCreditDays")+"");
        }

        String secondaryEmail=mapdata.get("secondaryEmail")+"";
        if(secondaryEmail.isEmpty() || secondaryEmail.equals("null")){
            secondaryEmail="";
        }
        edtSecondaryEmail.setText(secondaryEmail);
        edtMobile.setText(mapdata.get("phone"));
        String secondaryPhone=mapdata.get("secondaryPhone");
        if(secondaryPhone.isEmpty() || secondaryPhone.equals("null")){
         secondaryPhone="";
        }
        edtSecondaryMobile.setText(secondaryPhone);
        if(isNetworkConnected()){
            getCompanyTypeList();
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate(edtName.getText().toString().trim(),
                        edtContactPerName.getText().toString().trim(),
                        edtAddress1.getText().toString().trim(),
                        editCity.getText().toString(),edtCountry.getText().toString().trim(),
                        edtZipCode.getText().toString().trim(),edtMobile.getText().toString().trim(),
                        edtEmail.getText().toString().trim(),
                        edtTexNumber.getText().toString().trim(),edtSecondaryEmail.getText().toString().trim(),
                        edtSecondaryMobile.getText().toString().trim(),
                        edtAddress2.getText().toString().trim(),
                        edtPerMonReq.getText().toString().trim(),
                        edtHoldingCapacity.getText().toString().trim(),
                        edtCylHolCreDay.getText().toString().trim(),
                        edtAlias.getText().toString().trim())){
                    try {
                        callEditCompanyApi();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        spCompanyCatergory.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                companycatpos=position;
            }
        });
        spCompanyCatergory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
            }
        });

        spCompanyType.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                companytypepos=position;
            }
        });
        spCompanyType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(v);
            }
        });
    }
    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void callEditCompanyApi() throws JSONException {
        Log.d("Api Calling==>","Api Calling");
        btnSubmit.setEnabled(false);
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/api/MobCompany/AddEdit";
        final JSONObject jsonBody=new JSONObject();
        SharedPreferences setting= getSharedPreferences("setting",MODE_PRIVATE);
        jsonBody.put("CompanyId",Integer.parseInt(mapdata.get("companyId")));
        jsonBody.put("AdminName",edtContactPerName.getText().toString().trim()+"");
        jsonBody.put("CompanyName",edtName.getText().toString().trim()+"");
        jsonBody.put("CompanyAlias",edtAlias.getText().toString().trim()+"");
        jsonBody.put("Address1",edtAddress1.getText().toString().trim()+"");
        jsonBody.put("Address2",edtAddress2.getText().toString().trim()+"");
        jsonBody.put("City",editCity.getText().toString().trim()+"");
        jsonBody.put("County",edtCountry.getText().toString().trim()+"");
        jsonBody.put("ZipCode",edtZipCode.getText().toString().trim()+"");
        jsonBody.put("Phone",edtMobile.getText().toString().trim()+"");
        jsonBody.put("SecondaryPhone",edtSecondaryMobile.getText().toString().trim()+"");
        jsonBody.put("Email",edtEmail.getText().toString().trim()+"");
        jsonBody.put("SecondaryEmail",edtSecondaryEmail.getText().toString().trim()+"");
        jsonBody.put("CreatedBy",Integer.parseInt(settings.getString("userId","1")));
        jsonBody.put("ModifiedBy",Integer.parseInt(settings.getString("userId","1")));
        jsonBody.put("CompanyType",companyTypeList.get(companytypepos-1).get("value"));
        jsonBody.put("TaxNumber",edtTexNumber.getText().toString().trim()+"");
       // jsonBody.put("CompanyCategory",companyCatList.get(companycatpos-1).get("value")+"");
        jsonBody.put("CompanyCategory","");
        jsonBody.put("PerMonthRequirement",Integer.parseInt(edtPerMonReq.getText().toString().trim()));
        jsonBody.put("HoldingCapacity",Integer.parseInt(edtHoldingCapacity.getText().toString().trim()));
        jsonBody.put("CylinderHoldingCreditDays",Integer.parseInt(edtCylHolCreDay.getText().toString().trim()));
        jsonBody.put("ReferenceBy",edtReferenceby.getText().toString().trim()+"");
        if(edtDepositAmount.getText().toString().trim().equals("null") || edtDepositAmount.getText().toString().trim().equals("")){
            jsonBody.put("DepositAmount",Float.parseFloat("0"));
        }else {
            jsonBody.put("DepositAmount",Float.parseFloat(edtDepositAmount.getText().toString().trim()+""));
        }

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
                                Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = CompanyUpdate.edit();
                                editor.putBoolean("refresh",true);
                                editor.commit();
                                finish();
                            }else {
                                //Toast.makeText(context,jsonObject.getString("message").toString()+"",Toast.LENGTH_SHORT).show();
                                MySingalton.showOkDilog(context,jsonObject.getString("message").toString()+"", "Edit Distributor");
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

    public boolean validate(String fullName, String ContactPerName, String Address1, String City, String County,
                            String ZipCode, String Phone, String Email, String EmailPassword,
                            String secondaryEmail, String secondaryPhone, String address2, String perMonReq,
                            String holdingCap, String cylholcredday,String alias) {
        boolean valid = true;

        if (cylholcredday.isEmpty()) {
            edtCylHolCreDay.setError("Field is Required.");
            valid = false;
        } else {
            edtCylHolCreDay.setError(null);
        }
        if (holdingCap.isEmpty()) {
            edtHoldingCapacity.setError("Field is Required.");
            valid = false;
        } else {
            edtHoldingCapacity.setError(null);
        }
        if (perMonReq.isEmpty()) {
            edtPerMonReq.setError("Field is Required.");
            valid = false;
        } else {
            edtPerMonReq.setError(null);
        }
        if (address2.isEmpty()) {
            edtAddress2.setError("Field is Required.");
            valid = false;
        } else {
            edtAddress2.setError(null);
        }
/*        if (secondaryPhone.isEmpty()) {
            edtSecondaryMobile.setError("Field is Required.");
            valid = false;
        } else {
            edtSecondaryMobile.setError(null);
        }*/
/*        if (secondaryEmail.isEmpty()) {
            edtSecondaryEmail.setError("Field is Required.");
            valid = false;
        } else {
            edtSecondaryEmail.setError(null);
        }*/
        if (fullName.isEmpty()) {
            edtName.setError("Field is Required.");
            valid = false;
        } else {
            edtName.setError(null);
        }
        if (ContactPerName.isEmpty()) {
            edtContactPerName.setError("Field is Required.");
            valid = false;
        } else {
            edtContactPerName.setError(null);
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
            edtTexNumber.setError("Field is Required.");
            valid=false;
        }else{
            edtTexNumber.setError(null);
        }
        if (companytypepos<=0) {
            spCompanyType.setError("Field is Required.");
            valid = false;
        } else {
            spCompanyType.setError(null);
        }
        if(alias.isEmpty()){
            edtAlias.setError("Field is Required.");
            valid=false;
        }else{
            edtAlias.setError(null);
        }
/*        if (companycatpos<=0) {
            spCompanyCatergory.setError("Field is Required.");
            valid = false;
        } else {
            spCompanyCatergory.setError(null);
        }*/
        return valid;
    }

    private void getCompanyTypeList() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobCompany/CompanyTypeList";
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
                        companyTypeList = new ArrayList<>();
                        List<String> imtes=new ArrayList<>();
                        imtes.add("Select");
                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("value", jsonArray.getJSONObject(i).getString("value")+"");
                            map.put("text", jsonArray.getJSONObject(i).getString("text") + "");
                            map.put("disabled",jsonArray.getJSONObject(i).getBoolean("disabled")+"");
                            map.put("group",jsonArray.getJSONObject(i).getString("group")+"");
                            map.put("selected",jsonArray.getJSONObject(i).getBoolean("selected")+"");
                            if(mapdata.get("companyType").equals(jsonArray.getJSONObject(i).getString("text"))){
                                companytypepos=i+1;
                            }

                            imtes.add(jsonArray.getJSONObject(i).getString("value") + "");
                            companyTypeList.add(map);
                        }
                        spCompanyType.attachDataSource(imtes);
                        spCompanyType.setSelectedIndex(companytypepos);
                    }else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(isNetworkConnected()){
                   // getCompanyCatList();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
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

    private void getCompanyCatList() {
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
                        spCompanyCatergory.attachDataSource(imtes);
                        spCompanyCatergory.setSelectedIndex(companycatpos);

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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}