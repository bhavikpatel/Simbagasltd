package com.track.cylinderdelivery.ui.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.track.cylinderdelivery.ui.company.CompanyListAdapter;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class UserFragment extends Fragment {

    private static final int MY_SOCKET_TIMEOUT_MS = 50000;
    RecyclerView recyclerView;
    SearchView svUser;
   Context context;
    String search="";
    int pageno=0;
    int totalinpage=10;
    int CompanyId=1;
    String UserType="Client"; //""Admin";
    private SharedPreferences settings;
    LinearLayout lvFilterParent,lvSortingParent;
    private ArrayList<HashMap<String,String>> userTypeList;
    private ArrayList<HashMap<String, String>> companyList;
    private SharedPreferences spUserFilter;
    private SharedPreferences spCompanyFilter;
    private String SortBy="";
    private String Sort="desc";
    ArrayList<HashMap<String,String>> dataList;

    ProgressBar progressBar;
    Boolean isLoading=false;
    Boolean isLastPage=false;
    int totalRecord;
    private UserListAdapter userListAdapter;
    boolean userTypeListApifirsttime=true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);
        setHasOptionsMenu(true);
        context=getActivity();
        userTypeList=new ArrayList<>();
        companyList=new ArrayList<>();
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        CompanyId= Integer.parseInt(settings.getString("companyId","1"));
        //UserType=settings.getString("userType","");
        recyclerView=(RecyclerView)root.findViewById(R.id.rv_user_list);
        svUser=(SearchView)root.findViewById(R.id.svUser);
        lvFilterParent=(LinearLayout)root.findViewById(R.id.lvFilterParent);
        lvSortingParent=(LinearLayout)root.findViewById(R.id.lvSortingParent);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        progressBar=(ProgressBar)root.findViewById(R.id.progressBar);

        spUserFilter=context.getSharedPreferences("userFilter",MODE_PRIVATE);
        spCompanyFilter=context.getSharedPreferences("companyFilter",MODE_PRIVATE);

        try {
            if(isNetworkConnected()){
                callUserListApi();
            }else {
                Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        lvSortingParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,UserListSortingActivity.class);
                startActivity(intent);
            }
        });
        lvFilterParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userTypeList.size()!=0 && companyList.size()!=0){
                    Intent intent=new Intent(context,FilterUserListActivity.class);
                    intent.putExtra("userTypeList",userTypeList);
                    intent.putExtra("companyList",companyList);
                    startActivity(intent);
                }else{
                    if(isNetworkConnected()){
                        userTypeListApi();
                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        svUser.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                try {
                    if(isNetworkConnected()){
                        search="";
                        dataList=null;
                        callUserListApi();
                        hideSoftKeyboard(svUser);
                        svUser.clearFocus();
                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        svUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    if(isNetworkConnected()){
                        search=query;
                        dataList=null;
                        callUserListApi();
                        hideSoftKeyboard(svUser);
                        svUser.clearFocus();
                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search=newText;
                return true;
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
                                callUserListApi();
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
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(spUserFilter.getBoolean("dofilter",false)
                || spCompanyFilter.getBoolean("dofilter",false)){
            SharedPreferences.Editor userFilterEditor = spUserFilter.edit();
            userFilterEditor.putBoolean("dofilter",false);
            userFilterEditor.commit();

            SharedPreferences.Editor companyFilterEditor = spCompanyFilter.edit();
            companyFilterEditor.putBoolean("dofilter",false);
            companyFilterEditor.commit();

            CompanyId= spCompanyFilter.getInt("companyId",1);
            //UserType = spUserFilter.getString("text","");

            SortBy=spUserFilter.getString("text1","");
            if(spUserFilter.getString("text2","Decinding").equals("Decinding")){
                Sort="desc";
            }else{
                Sort="asc";
            }
            try {
                if(isNetworkConnected()){
                    dataList=null;
                    callUserListApi();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.user_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_adduser:
                // do stuff, like showing settings fragment
                Intent intent=new Intent(getActivity(),AddUserActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callUserListApi() throws JSONException {
        isLoading=true;
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        if(dataList==null){
            isLoading=false;
            isLastPage=false;
            pageno=0;
            progressDialog.show();
        }else {
            progressBar.setVisibility(View.VISIBLE);
        }
       String url = MySingalton.getInstance().URL +"/Api/MobUser/UserList?search="+search+"&pageno="+pageno+
               "&totalinpage="+totalinpage+"&CompanyId="+CompanyId+"&UserType="+UserType+"&SortBy="+SortBy+"&Sort="+Sort;

        Log.d("request==>",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
                progressDialog.dismiss();
                progressBar.setVisibility(View.GONE);
                isLoading=false;
                Log.d("resonse ==>",Response+"");
                JSONObject j;
                try {
                    j = new JSONObject(Response);
                        totalRecord=j.getInt("totalRecord");
                        JSONArray jsonArray=j.getJSONArray("list");
                        Boolean flgfirstload=false;
                        if(dataList==null){
                            dataList=new ArrayList<>();
                            flgfirstload=true;
                        }
                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("userId", jsonArray.getJSONObject(i).getString("userId") + "");
                            map.put("fullName", jsonArray.getJSONObject(i).getString("fullName") + "");
                            map.put("userAlias",jsonArray.getJSONObject(i).getString("userAlias")+"");
                            map.put("nameOfCompany",jsonArray.getJSONObject(i).getString("nameOfCompany"));
                            map.put("companyId", jsonArray.getJSONObject(i).getString("companyId") + "");
                            map.put("address1", jsonArray.getJSONObject(i).getString("address1") + "");
                            map.put("address2", jsonArray.getJSONObject(i).getString("address2") + "");
                            map.put("city", jsonArray.getJSONObject(i).getString("city") + "");
                            map.put("county", jsonArray.getJSONObject(i).getString("county") + "");
                            map.put("zipCode", jsonArray.getJSONObject(i).getString("zipCode") + "");
                            map.put("phone", jsonArray.getJSONObject(i).getString("phone") + "");
                            map.put("secondaryPhone", jsonArray.getJSONObject(i).getString("secondaryPhone") + "");
                            map.put("perMonthRequirement",jsonArray.getJSONObject(i).getString("perMonthRequirement")+"");
                            map.put("holdingCapacity", jsonArray.getJSONObject(i).getString("holdingCapacity") + "");
                            map.put("cylinderHoldingCreditDays",jsonArray.getJSONObject(i).getString("cylinderHoldingCreditDays")+"");
                            map.put("taxNumber", jsonArray.getJSONObject(i).getString("taxNumber") + "");
                            map.put("email", jsonArray.getJSONObject(i).getString("email") + "");
                            map.put("secondaryEmail", jsonArray.getJSONObject(i).getString("secondaryEmail") + "");
                            map.put("emailPassword", jsonArray.getJSONObject(i).getString("emailPassword") + "");
                            map.put("accNo", jsonArray.getJSONObject(i).getString("accNo") + "");
                            map.put("userType", jsonArray.getJSONObject(i).getString("userType") + "");
                            map.put("createdBy", jsonArray.getJSONObject(i).getString("createdBy") + "");
                            map.put("createdByName", jsonArray.getJSONObject(i).getString("createdByName") + "");
                            map.put("createdDate", jsonArray.getJSONObject(i).getString("createdDate") + "");
                            map.put("createdDateStr", jsonArray.getJSONObject(i).getString("createdDateStr") + "");
                            map.put("modifiedBy", jsonArray.getJSONObject(i).getString("modifiedBy") + "");
                            map.put("companyCategory",jsonArray.getJSONObject(i).getString("companyCategory")+"");
                            map.put("companyName", jsonArray.getJSONObject(i).getString("companyName") + "");
                            map.put("status", jsonArray.getJSONObject(i).getString("status") + "");
                            map.put("referenceBy",jsonArray.getJSONObject(i).getString("referenceBy")+"");
                            map.put("depositAmount",jsonArray.getJSONObject(i).getString("depositAmount")+"");
                            dataList.add(map);
                        }
                        if(dataList.size()>=totalRecord){
                            isLastPage=true;
                        }
                        if(flgfirstload){
                            flgfirstload=false;
                            userListAdapter=new UserListAdapter(dataList,getActivity());
                            recyclerView.setAdapter(userListAdapter);
                            userListAdapter.notifyDataSetChanged();
                            if(userTypeListApifirsttime){
                                userTypeListApi();
                            }
                            hideSoftKeyboard(recyclerView);
                        }else {
                            userListAdapter.notifyDataSetChanged();
                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                progressBar.setVisibility(View.GONE);
                isLoading=false;
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
                if(userTypeListApifirsttime){
                    userTypeListApi();
                }
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

    private void activeCompanyListAPI() {
        Log.d("Api Calling==>","Api Calling");
        //final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        //progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobUser/ActiveCompanyList?CompayId="+Integer.parseInt(settings.getString("companyId","1"));;
        Log.d("request==>",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
              //  progressDialog.dismiss();
                Log.d("resonse ==>",Response+"");
                JSONObject j;
                try {
                    j = new JSONObject(Response);
                    JSONArray jsonArray=j.getJSONArray("data");
                    companyList=new ArrayList<>();
                    ArrayList companyNameList = new ArrayList<String>();
                    for(int i=0;i<jsonArray.length();i++) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("disabled", jsonArray.getJSONObject(i).getBoolean("disabled") + "");
                        map.put("group", jsonArray.getJSONObject(i).getString("group") + "");
                        map.put("selected", jsonArray.getJSONObject(i).getBoolean("selected") + "");
                        companyNameList.add(jsonArray.getJSONObject(i).getString("text")+"");
                        map.put("text", jsonArray.getJSONObject(i).getString("text") + "");
                        map.put("value", jsonArray.getJSONObject(i).getString("value") + "");
                        companyList.add(map);
                    }
                    for(int i=0;i<companyNameList.size();i++){
                        if(CompanyId==Integer.parseInt(companyList.get(i).get("value").toString().trim())){
                            SharedPreferences.Editor editor = spCompanyFilter.edit();
                            editor.putInt("index",i);
                            editor.putString("text",companyNameList.get(i).toString());
                            editor.putBoolean("dofilter",false);
                            editor.commit();
                            break;
                        }
                    }
                    userTypeListApifirsttime=false;
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

    private void userTypeListApi() {
        Log.d("Api Calling==>","Api Calling");
        //final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        //progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobUser/UserTypeList";
        Log.d("request==>",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {
               // progressDialog.dismiss();
                Log.d("resonse ==>",Response+"");
                JSONObject j;
                try {
                    j = new JSONObject(Response);
                    JSONArray jsonArray=j.getJSONArray("data");
                    userTypeList=new ArrayList<>();
                    ArrayList usertype = new ArrayList<String>();
                    for(int i=0;i<jsonArray.length();i++) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("disabled", jsonArray.getJSONObject(i).getBoolean("disabled") + "");
                        map.put("group", jsonArray.getJSONObject(i).getString("group") + "");
                        map.put("selected", jsonArray.getJSONObject(i).getBoolean("selected") + "");
                        map.put("text", jsonArray.getJSONObject(i).getString("text") + "");
                        usertype.add(jsonArray.getJSONObject(i).getString("text")+"");
                        map.put("value", jsonArray.getJSONObject(i).getString("value") + "");
                        userTypeList.add(map);
                    }
                    for(int i=0;i<usertype.size();i++){
                        if(usertype.get(i).toString().trim().equals(UserType)){
                             SharedPreferences.Editor editor = spUserFilter.edit();
                            editor.putInt("index",i);
                            editor.putString("text",UserType);
                            editor.putBoolean("dofilter",false);
                            editor.commit();
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                activeCompanyListAPI();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                activeCompanyListAPI();
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
    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}