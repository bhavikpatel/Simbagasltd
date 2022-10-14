package com.track.cylinderdelivery.ui.purchaseorder;

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
import com.track.cylinderdelivery.ui.CylinderWarehouseMapping.FilledCylinderListAdapter;
import com.track.cylinderdelivery.ui.company.AddCompany;
import com.track.cylinderdelivery.ui.product.AddProductActivity;
import com.track.cylinderdelivery.ui.user.UserListSortingActivity;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class PurchaseOrderFragment extends Fragment {

    Context context;
    RecyclerView recyclerView;
    private ProgressBar progressBar;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    private SharedPreferences settings;
    private String search="";
    private int pageno=0;
    private int totalinpage=10;
    private String SortBy="";
    private String Sort="desc";
    private String ftext="ALL";
    private int totalRecord;
    private PurchaseOrderListAdapter purchaseOrderListAdapter;
    private ArrayList<HashMap<String, String>> purchaseOrderList;
    SearchView svUser;
    LinearLayout lvSortingParent,lvFilterParent;
    SharedPreferences spSorting;

    Boolean isLoading=false;
    Boolean isLastPage=false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_purchaseorder, container, false);
        context=getActivity();
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.purchaseorder));
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        progressBar=root.findViewById(R.id.progressBar);
        svUser=root.findViewById(R.id.svUser);
        spSorting=context.getSharedPreferences("POFilter",MODE_PRIVATE);
        lvSortingParent=root.findViewById(R.id.lvSortingParent);
        lvFilterParent=(LinearLayout)root.findViewById(R.id.lvFilterParent);
        recyclerView=root.findViewById(R.id.rv_product_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        if(isNetworkConnected()){
            callGetPurchaseOrderList();
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }

        lvSortingParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, purchaseOrderSorting.class);
                startActivity(intent);
            }
        });

        lvFilterParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,PurchaseOrderFilterActivity.class);
                intent.putExtra("selectedtext",ftext);
                startActivity(intent);
            }
        });
        svUser.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(isNetworkConnected()){
                    search="";
                    purchaseOrderList=null;
                    callGetPurchaseOrderList();
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
                    purchaseOrderList=null;
                    callGetPurchaseOrderList();
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
                            callGetPurchaseOrderList();
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
        if(spSorting.getBoolean("dofilter",false)){
            SharedPreferences.Editor userFilterEditor = spSorting.edit();
            userFilterEditor.putBoolean("dofilter",false);
            userFilterEditor.commit();
            SortBy=spSorting.getString("text1","");
            ftext=spSorting.getString("ftext","ALL");
            if(spSorting.getString("text2","Decinding").equals("Decinding")){
                Sort="desc";
            }else{
                Sort="asc";
            }
            if(isNetworkConnected()){
                purchaseOrderList=null;
                callGetPurchaseOrderList();
            }else {
                Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void callGetPurchaseOrderList() {
        isLoading=true;
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        if(purchaseOrderList==null){
            isLoading=false;
            isLastPage=false;
            pageno=0;
            progressDialog.show();
        }else {
            progressBar.setVisibility(View.VISIBLE);
        }
        String pftext="";
        if(ftext.equals("ALL")){
            pftext="";
        }else {
            pftext=ftext;
        }
        String url = MySingalton.getInstance().URL+"/Api/MobPurchaseOrder/GetPurchaseOrderList?search="+search+
                "&pageno="+pageno+"&totalinpage="+totalinpage+
                "&SortBy="+SortBy+"&Sort="+Sort+
                "&UserId="+Integer.parseInt(settings.getString("userId","1"))+
                "&CompanyId="+Integer.parseInt(settings.getString("companyId","1"))+
                "&UserType="+settings.getString("userType","1")+
                "&SerachStatus="+pftext;
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
                    if(purchaseOrderList==null){
                        purchaseOrderList=new ArrayList<>();
                        flgfirstload=true;
                    }

                    for(int i=0;i<jsonArray.length();i++){
                        HashMap<String,String> map=new HashMap<>();
                        map.put("poId",jsonArray.getJSONObject(i).getInt("poId")+"");
                        map.put("poNumber",jsonArray.getJSONObject(i).getString("poNumber")+"");
                        map.put("toCompanyId",jsonArray.getJSONObject(i).getInt("poId")+"");
                        map.put("userId",jsonArray.getJSONObject(i).getInt("userId")+"");
                        map.put("status",jsonArray.getJSONObject(i).getString("status")+"");
                        map.put("poDate",jsonArray.getJSONObject(i).getString("poDate")+"");
                        map.put("poGeneratedBy",jsonArray.getJSONObject(i).getString("poGeneratedBy")+"");
                        map.put("createdBy",jsonArray.getJSONObject(i).getString("createdBy")+"");
                        map.put("strPODate",jsonArray.getJSONObject(i).getString("strPODate")+"");
                        map.put("username",jsonArray.getJSONObject(i).getString("username")+"");
                        map.put("userEmail",jsonArray.getJSONObject(i).getString("userEmail"));
                        map.put("podm",jsonArray.getJSONObject(i).getString("podm")+"");
                        map.put("isDeletable",jsonArray.getJSONObject(i).getString("isDeletable"));
                        map.put("filePath",jsonArray.getJSONObject(i).getString("filePath"));
                        map.put("clientPOReference",jsonArray.getJSONObject(i).getString("clientPOReference"));
                        purchaseOrderList.add(map);
                    }

                    if(purchaseOrderList.size()>=totalRecord){
                        isLastPage=true;
                    }
                    if(flgfirstload){
                        flgfirstload=false;
                        purchaseOrderListAdapter=new PurchaseOrderListAdapter(purchaseOrderList,getActivity());
                        recyclerView.setAdapter(purchaseOrderListAdapter);
                    }else {
                        purchaseOrderListAdapter.notifyDataSetChanged();
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
        inflater.inflate(R.menu.purchaseorder_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_addPurchaseOrder:
                // do stuff, like showing settings fragment
                if(isNetworkConnected()){
                    callGetPONo();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    private void callGetPONo() {
        {
            Log.d("Api Calling==>","Api Calling");
            final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
            progressDialog.show();
            String url = MySingalton.getInstance().URL+"/Api/MobPurchaseOrder/GetPONo?CompanyId="+Integer.parseInt(settings.getString("companyId","1"));
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
                            String PONumber=j.getString("data");
                            Intent intent=new Intent(getActivity(), AddPurchaseOrderActivity.class);
                            intent.putExtra("PONumber",PONumber);
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
    }
}