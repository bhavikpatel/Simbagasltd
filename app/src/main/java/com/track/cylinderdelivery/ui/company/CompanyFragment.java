package com.track.cylinderdelivery.ui.company;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ui.user.AddUserActivity;
import com.track.cylinderdelivery.ui.user.UserListAdapter;
import com.track.cylinderdelivery.ui.user.UserListSortingActivity;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class CompanyFragment extends Fragment {

    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    Context context;
    String search="";
    int pageno=0;
    int totalinpage=10;
    RecyclerView recyclerView;
    SearchView svUser;
    SharedPreferences CompanyUpdate,spCompanyFilter;
    LinearLayout lvSortingParent;
    private String SortBy="";
    private String Sort="desc";
    ArrayList<HashMap<String,String>> dataList;
    int totalRecord;
    LinearLayoutManager layoutManager;
    Boolean isLoading=false;
    Boolean isLastPage=false;
    ProgressBar progressBar;
    CompanyListAdapter companyListAdapter;
    // Button btnAddCompany;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_company, container, false);
        context=getActivity();
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.company_list));
        lvSortingParent=(LinearLayout)root.findViewById(R.id.lvSortingParent);
        recyclerView=root.findViewById(R.id.rv_company_list);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        svUser=root.findViewById(R.id.svUser);
        progressBar=root.findViewById(R.id.progressBar);
        CompanyUpdate = context.getSharedPreferences("companyUpdate", MODE_PRIVATE);
        spCompanyFilter = context.getSharedPreferences("CompanySortign", MODE_PRIVATE);
        if(isNetworkConnected()){
            callCompanyListApi();
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }

        lvSortingParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, CompanyListSortingActivity.class);
                startActivity(intent);
            }
        });
        svUser.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                search="";
                if(isNetworkConnected()){
                    dataList=null;
                    callCompanyListApi();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
        svUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                search=query;
                if(isNetworkConnected()){
                    dataList=null;
                    callCompanyListApi();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
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
                            callCompanyListApi();
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
        if(CompanyUpdate.getBoolean("refresh",false)){
            if(isNetworkConnected()){
                dataList=null;
                callCompanyListApi();
            }else {
                Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
            }
            SharedPreferences.Editor editor=CompanyUpdate.edit();
            editor.putBoolean("refresh",false);
            editor.commit();
        }

        if(spCompanyFilter.getBoolean("dofilter",false)){
            SharedPreferences.Editor companyFilterEditor = spCompanyFilter.edit();
            companyFilterEditor.putBoolean("dofilter",false);
            companyFilterEditor.commit();

            SortBy=spCompanyFilter.getString("text1","");
            if(spCompanyFilter.getString("text2","Decinding").equals("Decinding")){
                Sort="desc";
            }else{
                Sort="asc";
            }
            if(isNetworkConnected()){
                dataList=null;
                callCompanyListApi();
            }else {
                Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
            }
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    private void callCompanyListApi() {
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

        String url = MySingalton.getInstance().URL+"/Api/MobCompany/GetCompanyList?search="+search+
                "&pageno="+pageno+"&totalinpage="+totalinpage+
                "&SortBy="+SortBy+"&Sort="+Sort;
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
                        map.put("companyId", jsonArray.getJSONObject(i).getString("companyId") + "");
                        map.put("companyName", jsonArray.getJSONObject(i).getString("companyName") + "");
                        map.put("companyAlias",jsonArray.getJSONObject(i).getString("companyAlias")+"");
                        map.put("adminName", jsonArray.getJSONObject(i).getString("adminName") + "");
                        map.put("companyType", jsonArray.getJSONObject(i).getString("companyType") + "");
                        map.put("companyCategory", jsonArray.getJSONObject(i).getString("companyCategory") + "");
                        map.put("address1", jsonArray.getJSONObject(i).getString("address1") + "");
                        map.put("address2", jsonArray.getJSONObject(i).getString("address2") + "");
                        map.put("city", jsonArray.getJSONObject(i).getString("city") + "");
                        map.put("county", jsonArray.getJSONObject(i).getString("county") + "");
                        map.put("zipCode", jsonArray.getJSONObject(i).getString("zipCode") + "");
                        map.put("phone", jsonArray.getJSONObject(i).getString("phone") + "");
                        map.put("secondaryPhone", jsonArray.getJSONObject(i).getString("secondaryPhone") + "");
                        map.put("email", jsonArray.getJSONObject(i).getString("email") + "");
                        map.put("secondaryEmail", jsonArray.getJSONObject(i).getString("secondaryEmail") + "");
                        map.put("taxNumber", jsonArray.getJSONObject(i).getString("taxNumber") + "");
                        map.put("createdBy", jsonArray.getJSONObject(i).getString("createdBy") + "");
                        map.put("modifiedBy", jsonArray.getJSONObject(i).getString("modifiedBy") + "");
                        map.put("status", jsonArray.getJSONObject(i).getString("status") + "");
                        map.put("createdByName", jsonArray.getJSONObject(i).getString("createdByName") + "");
                        map.put("createdDateStr", jsonArray.getJSONObject(i).getString("createdDateStr") + "");
                        map.put("perMonthRequirement", jsonArray.getJSONObject(i).getString("perMonthRequirement") + "");
                        map.put("holdingCapacity", jsonArray.getJSONObject(i).getString("holdingCapacity") + "");
                        map.put("cylinderHoldingCreditDays", jsonArray.getJSONObject(i).getString("cylinderHoldingCreditDays") + "");
                        map.put("referenceBy",jsonArray.getJSONObject(i).getString("referenceBy")+"");
                        map.put("depositAmount",jsonArray.getJSONObject(i).getString("depositAmount")+"");
                        dataList.add(map);
                    }
                    if(dataList.size()>=totalRecord){
                        isLastPage=true;
                    }
                    if(flgfirstload){
                        flgfirstload=false;
                        companyListAdapter=new CompanyListAdapter(dataList,getActivity());
                        recyclerView.setAdapter(companyListAdapter);
                        companyListAdapter.notifyDataSetChanged();
                    }else {
                        companyListAdapter.notifyDataSetChanged();
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.company_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_addCompany:
                // do stuff, like showing settings fragment
                Intent intent=new Intent(getActivity(), AddCompany.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}