package com.track.cylinderdelivery.ui.CylinderWarehouseMapping;

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
import com.track.cylinderdelivery.ui.acknowledge.AcknowledgeListAdapter;
import com.track.cylinderdelivery.ui.cylinderproductmapping.AddCylinderProductMappingActivity;
import com.track.cylinderdelivery.ui.user.UserListSortingActivity;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class CylinderWarehouseMappingFragment extends Fragment {

    private Context context;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    private String search="";
    private int pageno=0;
    private int totalinpage=10;
    private String SortBy="";
    private String Sort="desc";
    private int WarehouseId;
    SharedPreferences setting;
    int totalRecord=0;
    ArrayList<HashMap<String,String>> FillCylinderWarehouseMappingList;
    ArrayList<HashMap<String,String>> EmptyCylinderWarehouseMappingList;
    FilledCylinderListAdapter filledCylinderListAdapter;
    EmptyCylinderListAdapter emptyCylinderListAdapter;
    RecyclerView recyclerView;
    Button btnFilledCyl,btnEmptyCyl;
    private String search1="";
    private int pageno1=0;
    private int totalinpage1=10;
    private String SortBy1="";
    private String Sort1="desc";
    private int WarehouseId1;
    private int totalRecord1;
    private LinearLayout lvSortingParent,lvFilterParent;
    boolean flgFillNotFill=true;
    SharedPreferences spFilledFilter,spEmptyFiter;
    ProgressBar progressBar;
    Boolean isLoading=false;
    Boolean isLastPage=false;
    int flgAck=1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cylinder_warehouse_mapping, container, false);
        context=getActivity();
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.cylinder_warehouse_mapping));
        setting= context.getSharedPreferences("setting",MODE_PRIVATE);
        spFilledFilter=context.getSharedPreferences("filledCylFilter",MODE_PRIVATE);
        spEmptyFiter=context.getSharedPreferences("emptyCylFilter",MODE_PRIVATE);
        recyclerView=root.findViewById(R.id.rvcylinders);
        btnFilledCyl=root.findViewById(R.id.btnFilledCyl);
        btnEmptyCyl=root.findViewById(R.id.btnEmptyCyl);
        lvSortingParent=root.findViewById(R.id.lvSortingParent);
        lvFilterParent=root.findViewById(R.id.lvFilterParent);
        progressBar=root.findViewById(R.id.progressBar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        if(isNetworkConnected()){
            callGetFillCylinderWarehouseMappingData();
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }

        lvFilterParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(isNetworkConnected()){
                        callGetWarehouseList1();
                    }else {
                        Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                    }
            }
        });
        lvSortingParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flgFillNotFill){
                    Intent intent=new Intent(context, SortCylinderMappingActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent=new Intent(context, SortEmptyCylMappingActivity.class);
                    startActivity(intent);
                }
            }
        });
        btnFilledCyl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()){
                    btnFilledCyl.setBackgroundColor(getResources().getColor(R.color.green));
                    btnEmptyCyl.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                    btnFilledCyl.setTextColor(getResources().getColor(R.color.white));
                    btnEmptyCyl.setTextColor(getResources().getColor(R.color.black));
                    flgFillNotFill=true;
                    FillCylinderWarehouseMappingList=null;
                    callGetFillCylinderWarehouseMappingData();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnEmptyCyl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()){

                    btnFilledCyl.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                    btnEmptyCyl.setBackgroundColor(getResources().getColor(R.color.green));
                    btnFilledCyl.setTextColor(getResources().getColor(R.color.black));
                    btnEmptyCyl.setTextColor(getResources().getColor(R.color.white));
                    flgFillNotFill=false;
                    EmptyCylinderWarehouseMappingList=null;
                    callGetEmptyCylinderWarehouseMappingData();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
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
                            if(flgAck==1){
                                callGetFillCylinderWarehouseMappingData();
                            }else if(flgAck==2){
                                callGetEmptyCylinderWarehouseMappingData();
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
        if(flgFillNotFill){
            if(spFilledFilter.getBoolean("dofilter",false)){
                SharedPreferences.Editor userFilterEditor = spFilledFilter.edit();
                userFilterEditor.putBoolean("dofilter",false);
                userFilterEditor.commit();
                SortBy=spFilledFilter.getString("text1","");
                if(spFilledFilter.getString("text2","Decinding").equals("Decinding")){
                    Sort="desc";
                }else{
                    Sort="asc";
                }
               // WarehouseId=spFilledFilter.getInt("warhousepos",0);
                WarehouseId=spFilledFilter.getInt("warehouseId",0);
                if(isNetworkConnected()){
                    FillCylinderWarehouseMappingList=null;
                    callGetFillCylinderWarehouseMappingData();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
            }
        }else {
            if(spEmptyFiter.getBoolean("dofilter",false)) {
                SharedPreferences.Editor userFilterEditor = spEmptyFiter.edit();
                userFilterEditor.putBoolean("dofilter", false);
                userFilterEditor.commit();
                SortBy1 = spEmptyFiter.getString("text1", "");
                if (spEmptyFiter.getString("text2", "Decinding").equals("Decinding")) {
                    Sort1 = "desc";
                } else {
                    Sort1 = "asc";
                }
                //WarehouseId1=spEmptyFiter.getInt("warhousepos",0);
                WarehouseId1=spEmptyFiter.getInt("warehouseId",0);
                if (isNetworkConnected()) {
                    EmptyCylinderWarehouseMappingList=null;
                    callGetEmptyCylinderWarehouseMappingData();
                } else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.cylinderwarehousemappiing_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_addCylinderWarehouseMapping:
                // do stuff, like showing settings fragment
                //CompanyId= Integer.parseInt(settings.getString("companyId","1"));
/*                if(isNetworkConnected()){
                    GetWarehouseList();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }*/
                if(isNetworkConnected()){
                    callGetWarehouseList();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callGetWarehouseList() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobWarehouse/GetWarehouseList?CompanyId="+Integer.parseInt(setting.getString("CompanyId","1"));
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
                         ArrayList<HashMap<String, String>> whereHouseList = new ArrayList<>();

                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("warehouseId", jsonArray.getJSONObject(i).getInt("warehouseId")+"");
                            map.put("name", jsonArray.getJSONObject(i).getString("name") + "");
                            whereHouseList.add(map);
                        }
                        Intent intent=new Intent(getActivity(), AddCylinderWarehouseMapping.class);
                        intent.putExtra("wharehouselist",whereHouseList);
                        startActivity(intent);
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

    private void callGetWarehouseList1() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobWarehouse/GetWarehouseList?CompanyId="+Integer.parseInt(setting.getString("CompanyId","1"));
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
                         ArrayList<HashMap<String, String>> whereHouseList = new ArrayList<>();

                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("warehouseId", jsonArray.getJSONObject(i).getInt("warehouseId")+"");
                            map.put("name", jsonArray.getJSONObject(i).getString("name") + "");
                            whereHouseList.add(map);
                        }
                        if(flgFillNotFill) {
                            Intent intent = new Intent(getActivity(), FilterFilledCylActivity.class);
                            intent.putExtra("wharehouselist", whereHouseList);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(getActivity(), FilterEmptyCylActivity.class);
                            intent.putExtra("wharehouselist", whereHouseList);
                            startActivity(intent);
                        }
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
    private void callGetEmptyCylinderWarehouseMappingData() {
        {
            flgAck=2;
            isLoading=true;
            Log.d("Api Calling==>","Api Calling");
            final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
            if(EmptyCylinderWarehouseMappingList==null){
                isLoading=false;
                isLastPage=false;
                pageno=0;
                progressDialog.show();
            }else {
                progressBar.setVisibility(View.VISIBLE);
            }
            String url = MySingalton.getInstance().URL+"/Api/MobCylinderWarehouseMapping/GetEmptyCylinderWarehouseMappingData?"+
                    "search="+search1+"&pageno="+pageno1+"&totalinpage="+totalinpage1+
                    "&SortBy="+SortBy1+"&Sort="+Sort1+"&WarehouseId="+WarehouseId1+
                    "&CompanyId="+Integer.parseInt(setting.getString("CompanyId","1"));
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
                        totalRecord1=j.getInt("totalRecord");
                        totalRecord=totalRecord1;
                        JSONArray jsonArray=j.getJSONArray("list");
                        Boolean flgfirstload=false;
                        if(EmptyCylinderWarehouseMappingList==null){
                            EmptyCylinderWarehouseMappingList=new ArrayList<>();
                            flgfirstload=true;
                        }

                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("cylinderWarehouseMappingId", jsonArray.getJSONObject(i).getInt("cylinderWarehouseMappingId")+"");
                            //map.put("fromWarehouseId", jsonArray.getJSONObject(i).getInt("fromWarehouseId") + "");
                            //map.put("warehouseId", jsonArray.getJSONObject(i).getInt("warehouseId") + "");
                            map.put("warehouseName", jsonArray.getJSONObject(i).getString("warehouseName") + "");
                            //map.put("userId", jsonArray.getJSONObject(i).getInt("userId") + "");
                            map.put("userName", jsonArray.getJSONObject(i).getString("userName") + "");
                            map.put("cylinderList", jsonArray.getJSONObject(i).getString("cylinderList") + "");
                            //map.put("cylinderId", jsonArray.getJSONObject(i).getInt("cylinderId") + "");
                            map.put("cylinderNo", jsonArray.getJSONObject(i).getString("cylinderNo") + "");
                            map.put("createdBy", jsonArray.getJSONObject(i).getString("createdBy") + "");
                            //map.put("dnId", jsonArray.getJSONObject(i).getInt("dnId") + "");
                            //map.put("soId", jsonArray.getJSONObject(i).getInt("soId") + "");
                            //map.put("cylinderProductMappingId", jsonArray.getJSONObject(i).getInt("cylinderProductMappingId") + "");
                            map.put("createdDate", jsonArray.getJSONObject(i).getString("createdDate") + "");
                            map.put("createdDateStr", jsonArray.getJSONObject(i).getString("createdDateStr") + "");
                            map.put("createdByName", jsonArray.getJSONObject(i).getString("createdByName") + "");
                            map.put("productName", jsonArray.getJSONObject(i).getString("productName") + "");
                            EmptyCylinderWarehouseMappingList.add(map);
                        }
                        if(EmptyCylinderWarehouseMappingList.size()>=totalRecord){
                            isLastPage=true;
                        }
                        if(flgfirstload){
                            flgfirstload=false;
                            emptyCylinderListAdapter=new EmptyCylinderListAdapter(EmptyCylinderWarehouseMappingList,getActivity());
                            recyclerView.setAdapter(emptyCylinderListAdapter);
                        }else {
                            emptyCylinderListAdapter.notifyDataSetChanged();
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
    }

    private void callGetFillCylinderWarehouseMappingData() {
        {
            flgAck=1;
            isLoading=true;
            Log.d("Api Calling==>","Api Calling");
            final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
            if(FillCylinderWarehouseMappingList==null){
                isLoading=false;
                isLastPage=false;
                pageno=0;
                progressDialog.show();
            }else {
                progressBar.setVisibility(View.VISIBLE);
            }
            String url = MySingalton.getInstance().URL+"/Api/MobCylinderWarehouseMapping/GetFillCylinderWarehouseMappingData?"+
                    "search="+search+"&pageno="+pageno+"&totalinpage="+totalinpage+
                    "&SortBy="+SortBy+"&Sort="+Sort+"&WarehouseId="+
                    WarehouseId+"&CompanyId="+Integer.parseInt(setting.getString("CompanyId","1"));
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
                        if(FillCylinderWarehouseMappingList==null){
                            FillCylinderWarehouseMappingList=new ArrayList<>();
                            flgfirstload=true;
                        }

                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("cylinderWarehouseMappingId", jsonArray.getJSONObject(i).getInt("cylinderWarehouseMappingId")+"");
                            map.put("fromWarehouseId", jsonArray.getJSONObject(i).getString("fromWarehouseId") + "");
                            map.put("warehouseId", jsonArray.getJSONObject(i).getString("warehouseId") + "");
                            map.put("warehouseName", jsonArray.getJSONObject(i).getString("warehouseName") + "");
                            map.put("userId",jsonArray.getJSONObject(i).getString("userId")+"");
                            map.put("userName",jsonArray.getJSONObject(i).getString("userName")+"");
                            map.put("cylinderList", jsonArray.getJSONObject(i).getString("cylinderList") + "");
                            map.put("cylinderId", jsonArray.getJSONObject(i).getString("cylinderId") + "");
                            map.put("cylinderNo", jsonArray.getJSONObject(i).getString("cylinderNo") + "");
                            map.put("createdBy", jsonArray.getJSONObject(i).getString("createdBy") + "");
                            map.put("dnId",jsonArray.getJSONObject(i).getString("dnId")+"");
                            map.put("soId",jsonArray.getJSONObject(i).getString("soId")+"");
                            map.put("roId",jsonArray.getJSONObject(i).getString("roId")+"");
                            map.put("cylinderProductMappingId",jsonArray.getJSONObject(i).getString("cylinderProductMappingId")+"");
                            map.put("createdDate",jsonArray.getJSONObject(i).getString("createdDate")+"");
                            map.put("createdDateStr", jsonArray.getJSONObject(i).getString("createdDateStr") + "");
                            map.put("createdByName", jsonArray.getJSONObject(i).getString("createdByName") + "");
                            map.put("productName", jsonArray.getJSONObject(i).getString("productName") + "");

                            FillCylinderWarehouseMappingList.add(map);
                        }
                        if(FillCylinderWarehouseMappingList.size()>=totalRecord){
                            isLastPage=true;
                        }
                        if(flgfirstload){
                            flgfirstload=false;
                            filledCylinderListAdapter=new FilledCylinderListAdapter(FillCylinderWarehouseMappingList,getActivity());
                            recyclerView.setAdapter(filledCylinderListAdapter);
                        }else {
                            filledCylinderListAdapter.notifyDataSetChanged();
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
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}