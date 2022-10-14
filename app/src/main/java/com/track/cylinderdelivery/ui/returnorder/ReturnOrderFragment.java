package com.track.cylinderdelivery.ui.returnorder;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.FragmentActivity;
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
import com.track.cylinderdelivery.ui.product.AddProductActivity;
import com.track.cylinderdelivery.ui.purchaseorder.PurchaseOrderFilterActivity;
import com.track.cylinderdelivery.ui.salesorder.AddSalesOrderActivity;
import com.track.cylinderdelivery.ui.salesorder.SalesOrderListAdapter;
import com.track.cylinderdelivery.ui.salesorder.SalesOrderSortingActivity;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ReturnOrderFragment extends Fragment {

    private RecyclerView recyclerView;
    Context context;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    private boolean isLoading=false;
    private boolean isLastPage=false;
    private int pageno;
    private ProgressBar progressBar;
    private int totalinpage=10;
    private ArrayList<HashMap<String,String>> ROrderList;
    private String search="";
    private String SortBy="";
    private String Sort="desc";
    private SharedPreferences settings;
    private int totalRecord;
    private ROrderListAdapter ROrderListAdapter;
    SharedPreferences spSorting;
    SearchView svUser;
    LinearLayout lvSortingParent,lvFilterParent;
    private String ftext="ALL";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_return_order, container, false);
        context=getActivity();
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Return Order List");
        recyclerView=root.findViewById(R.id.rv_product_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        progressBar=root.findViewById(R.id.progressBar);
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        spSorting=context.getSharedPreferences("ROFilter",MODE_PRIVATE);
        svUser=root.findViewById(R.id.svUser);
        lvSortingParent=root.findViewById(R.id.lvSortingParent);
        lvFilterParent=(LinearLayout)root.findViewById(R.id.lvFilterParent);
        if(isNetworkConnected()){
            callGetReturnOrderList();
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }

        lvSortingParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ROSortingActivity.class);
                startActivity(intent);
            }
        });
        lvFilterParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ROFilterActivity.class);
                intent.putExtra("selectedtext",ftext);
                startActivity(intent);
            }
        });
        svUser.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(isNetworkConnected()){
                    search="";
                    ROrderList=null;
                    callGetReturnOrderList();
                    svUser.clearFocus();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
        svUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(isNetworkConnected()){
                    search=query;
                    ROrderList=null;
                    callGetReturnOrderList();
                    svUser.clearFocus();
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
                            callGetReturnOrderList();
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
        if(spSorting.getBoolean("refilter",false)){
            SharedPreferences.Editor userFilterEditor = spSorting.edit();
            userFilterEditor.putBoolean("refilter",false);
            userFilterEditor.commit();
            SortBy=spSorting.getString("text1","");
            if(spSorting.getString("text2","Decinding").equals("Decinding")){
                Sort="desc";
            }else{
                Sort="asc";
            }
            ftext=spSorting.getString("ftext","ALL");
            if(isNetworkConnected()){
                ROrderList=null;
                callGetReturnOrderList();
            }else {
                Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.returnorder_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_addReturnOrde:
                // do stuff, like showing settings fragment
                if(isNetworkConnected()){
                   callGetRONo();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callGetRONo() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobReturnOrder/GetRONo?CompanyId="+Integer.parseInt(settings.getString("companyId","1"));
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
                        String PONumber=j.getString("message");
                        Intent intent=new Intent(getActivity(), AddReturnOrderActivity.class);
                        intent.putExtra("RONumber",PONumber);
                        startActivity(intent);
                    }else {
                        Toast.makeText(context, j.getString("message")+"", Toast.LENGTH_LONG).show();
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


    private void callGetReturnOrderList() {
        isLoading=true;
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        if(ROrderList==null){
            isLoading=false;
            isLastPage=false;
            pageno=0;
            progressDialog.show();
        }else {
            progressBar.setVisibility(View.VISIBLE);
        }
        String ptext="";
        if(ftext.equals("ALL")){
            ptext="";
        }else {
            ptext=ftext;
        }
        String url = MySingalton.getInstance().URL+"/Api/MobReturnOrder/GetReturnOrderList?search="+search+
                "&pageno="+pageno+"&totalinpage="+totalinpage+
                "&SortBy="+SortBy+"&Sort="+Sort+
                "&UserId="+Integer.parseInt(settings.getString("userId","1"))+
                "&CompanyId="+Integer.parseInt(settings.getString("companyId","1"))+
                "&UserType="+settings.getString("userType","1")+
                "&SerachStatus="+ptext;
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
                    if(ROrderList==null){
                        ROrderList=new ArrayList<>();
                        flgfirstload=true;
                    }

                    for(int i=0;i<jsonArray.length();i++){
                        HashMap<String,String> map=new HashMap<>();
                        map.put("roId",jsonArray.getJSONObject(i).getString("roId")+"");
                        map.put("roNumber",jsonArray.getJSONObject(i).getString("roNumber")+"");
                        map.put("toCompanyId",jsonArray.getJSONObject(i).getString("toCompanyId")+"");
                        map.put("userId",jsonArray.getJSONObject(i).getString("userId")+"");
                        map.put("status",jsonArray.getJSONObject(i).getString("status")+"");
                        map.put("roDate",jsonArray.getJSONObject(i).getString("roDate")+"");
                        map.put("roGeneratedBy",jsonArray.getJSONObject(i).getString("roGeneratedBy")+"");
                        map.put("driverVehicleNo",jsonArray.getJSONObject(i).getString("driverVehicleNo")+"");
                        map.put("createdBy",jsonArray.getJSONObject(i).getString("createdBy")+"");
                        map.put("createdDate",jsonArray.getJSONObject(i).getString("createdDate")+"");
                        map.put("modifiedBy",jsonArray.getJSONObject(i).getString("modifiedBy")+"");
                        map.put("modifiedDate",jsonArray.getJSONObject(i).getString("modifiedDate")+"");
                        map.put("strRODate",jsonArray.getJSONObject(i).getString("strRODate")+"");
                        map.put("username",jsonArray.getJSONObject(i).getString("username")+"");
                        map.put("rodm",jsonArray.getJSONObject(i).getString("rodm")+"");

                        ROrderList.add(map);
                    }

                    if(ROrderList.size()>=totalRecord){
                        isLastPage=true;
                    }
                    if(flgfirstload){
                        flgfirstload=false;
                        ROrderListAdapter=new ROrderListAdapter(ROrderList,getActivity());
                        recyclerView.setAdapter(ROrderListAdapter);
                    }else {
                        ROrderListAdapter.notifyDataSetChanged();
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

    @SuppressLint("MissingPermission")
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}