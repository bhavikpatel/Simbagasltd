package com.track.cylinderdelivery.ui.cylinderproductmapping;

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
import com.track.cylinderdelivery.ui.cylinder.AddCylinderActivity;
import com.track.cylinderdelivery.ui.diliverynote.DeliveryNoteListAdapter;
import com.track.cylinderdelivery.ui.product.AddProductActivity;
import com.track.cylinderdelivery.ui.purchaseorder.purchaseOrderSorting;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class CylinderProductMappingFragment extends Fragment {

    private Context context;
    private int CompanyId;
    private SharedPreferences settings;
    ArrayList<HashMap<String,String>> whereHouseList;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    private String Sort="desc";
    private String SortBy="";
    private int totalinpage=10;
    private int pageno=0;
    private boolean isLoading=false;
    private boolean isLastPage=false;
    private int totalRecord=0;
    private ProgressBar progressBar;
    private ArrayList<HashMap<String, String>> cylinderProductMappingList;
    RecyclerView recyclerView;
    private CylinderProductMapingListAdapter cylinderProductMapingListAdapter;
    private String search="";
    SharedPreferences cylindeMappingActivity;
    SearchView svSearch;
    LinearLayout lvSortingParent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cylinderproductmapping, container, false);
        context=getActivity();
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Cylinder Product Mapping List");
        recyclerView=root.findViewById(R.id.rv_cylinder_list);
        progressBar=root.findViewById(R.id.progressBar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        cylindeMappingActivity=context.getSharedPreferences("CPMSoring",MODE_PRIVATE);
        svSearch=root.findViewById(R.id.searchViewCylinder);
        lvSortingParent=root.findViewById(R.id.lvSortingParent);

       if(isNetworkConnected()){
            callGetCylinderProductMappingList();
       }else {
           Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
       }

        lvSortingParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, SortingCylinderProductMappingActivity.class);
                startActivity(intent);
            }
        });
        svSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(isNetworkConnected()){
                    search="";
                    cylinderProductMappingList=null;
                    callGetCylinderProductMappingList();
                    hideSoftKeyboard(svSearch);
                    svSearch.clearFocus();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(isNetworkConnected()){
                    search=query;
                    cylinderProductMappingList=null;
                    callGetCylinderProductMappingList();
                    hideSoftKeyboard(svSearch);
                    svSearch.clearFocus();
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
                            callGetCylinderProductMappingList();
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
        if(cylindeMappingActivity.getBoolean("dofilter",false)){
            SharedPreferences.Editor editor = cylindeMappingActivity.edit();
            editor.putBoolean("dofilter",false);
            editor.commit();
            SortBy=cylindeMappingActivity.getString("text1","");
            if(cylindeMappingActivity.getString("text2","Decinding").equals("Decinding")){
                Sort="desc";
            }else{
                Sort="asc";
            }
            if(isNetworkConnected()){
                cylinderProductMappingList=null;
                callGetCylinderProductMappingList();
            }else {
                Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void callGetCylinderProductMappingList() {
        isLoading=true;
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        if(cylinderProductMappingList==null){
            isLoading=false;
            isLastPage=false;
            pageno=0;
            progressDialog.show();
        }else {
            progressBar.setVisibility(View.VISIBLE);
        }
        String url = MySingalton.getInstance().URL+"/Api/MobCylinderProductMapping/GetCylinderProductMappingList?search="+search+
                "&pageno="+pageno+"&totalinpage="+totalinpage+"&SortBy="+SortBy+"&Sort="+Sort;
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
                    Boolean flgfirstload=false;
                    if(cylinderProductMappingList==null){
                        cylinderProductMappingList=new ArrayList<>();
                        flgfirstload=true;
                    }
                    j = new JSONObject(Response);
                    totalRecord=j.getInt("totalRecord");
                    JSONArray jsonArray=j.getJSONArray("list");

                    for(int i=0;i<jsonArray.length();i++){
                        HashMap<String,String> map=new HashMap<>();
                        map.put("cylinderProductMappingId",jsonArray.getJSONObject(i).getInt("cylinderProductMappingId")+"");
                        map.put("fromWarehouseId",jsonArray.getJSONObject(i).getString("fromWarehouseId")+"");
                        map.put("warehouseId",jsonArray.getJSONObject(i).getString("warehouseId")+"");
                        map.put("cylinderId",jsonArray.getJSONObject(i).getString("cylinderId")+"");
                        map.put("productId",jsonArray.getJSONObject(i).getString("productId")+"");
                        map.put("unit",jsonArray.getJSONObject(i).getString("unit")+"");
                        map.put("quantity",jsonArray.getJSONObject(i).getString("quantity")+"");
                        map.put("purity",jsonArray.getJSONObject(i).getString("purity")+"");
                        map.put("impurities",jsonArray.getJSONObject(i).getString("impurities")+"");
                        map.put("gaugePressure",jsonArray.getJSONObject(i).getString("gaugePressure")+"");
                        map.put("fillingDate",jsonArray.getJSONObject(i).getString("fillingDate")+"");
                        map.put("createdBy",jsonArray.getJSONObject(i).getString("createdBy"));
                        map.put("createdDate",jsonArray.getJSONObject(i).getString("createdDate"));
                        map.put("modifiedDate",jsonArray.getJSONObject(i).getString("modifiedDate"));
                        map.put("cylinderNo",jsonArray.getJSONObject(i).getString("cylinderNo"));
                        map.put("cylinderList",jsonArray.getJSONObject(i).getString("cylinderList"));
                        map.put("productName",jsonArray.getJSONObject(i).getString("productName"));
                        map.put("createdByName",jsonArray.getJSONObject(i).getString("createdByName"));
                        map.put("createdDateStr",jsonArray.getJSONObject(i).getString("createdDateStr"));
                        map.put("productDetail",jsonArray.getJSONObject(i).getString("productDetail"));
                        map.put("cylinderDetail",jsonArray.getJSONObject(i).getString("cylinderDetail"));

                        cylinderProductMappingList.add(map);
                    }

                    if(cylinderProductMappingList.size()>=totalRecord){
                        isLastPage=true;
                    }
                    if(flgfirstload){
                        flgfirstload=false;
                        cylinderProductMapingListAdapter=new CylinderProductMapingListAdapter(cylinderProductMappingList,getActivity());
                        recyclerView.setAdapter(cylinderProductMapingListAdapter);
                    }else {
                        cylinderProductMapingListAdapter.notifyDataSetChanged();
                    }
                    hideSoftKeyboard(svSearch);
                    svSearch.clearFocus();
                } catch (JSONException e) {
                    e.printStackTrace();
                    cylinderProductMapingListAdapter=new CylinderProductMapingListAdapter(cylinderProductMappingList,getActivity());
                    recyclerView.setAdapter(cylinderProductMapingListAdapter);
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
        inflater.inflate(R.menu.cylinderproductmapping_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_addCylinderProductMapping:
                // do stuff, like showing settings fragment
                CompanyId= Integer.parseInt(settings.getString("companyId","1"));
                if(isNetworkConnected()){
                    GetWarehouseList();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void GetWarehouseList() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobWarehouse/GetWarehouseList?CompanyId="+CompanyId;
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
                       whereHouseList=new ArrayList<>();

                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("warehouseId", jsonArray.getJSONObject(i).getInt("warehouseId")+"");
                            map.put("name", jsonArray.getJSONObject(i).getString("name") + "");
                            whereHouseList.add(map);
                        }
                        Intent intent=new Intent(getActivity(),AddCylinderProductMappingActivity.class);
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
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}