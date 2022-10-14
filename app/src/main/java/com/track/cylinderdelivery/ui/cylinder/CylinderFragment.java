package com.track.cylinderdelivery.ui.cylinder;

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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ui.company.AddCompany;
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

public class CylinderFragment extends Fragment {


    private Context context;
    private LinearLayout lvSortingParent;
    private RecyclerView recyclerView;
    private SearchView searchViewCylinder;
    private SharedPreferences settings;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000;
    private int CompanyId;
    ArrayList<HashMap<String,String>> cylinderList;
    CylinderListAdapter cylinderListAdapter;
    private SharedPreferences cylinderEdit;
    String search="";
    int pageno=0;
    int totalinpage=10;
    String SortBy="";
    String Sort="desc";
    SharedPreferences spSorting;
    private String CyldType;

    private int totalRecord;
    ProgressBar progressBar;
    Boolean isLoading=false;
    Boolean isLastPage=false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cylinder, container, false);
        context=getActivity();
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.cylinder_list));
        lvSortingParent=(LinearLayout)root.findViewById(R.id.lvSortingParent);
        recyclerView=root.findViewById(R.id.rv_cylinder_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        searchViewCylinder=root.findViewById(R.id.searchViewCylinder);
        progressBar=(ProgressBar)root.findViewById(R.id.progressBar);
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
       // cylinderList=new ArrayList<HashMap<String, String>>();
        cylinderEdit=context.getSharedPreferences("cylinderEdit",MODE_PRIVATE);
        spSorting=context.getSharedPreferences("cylinderListsort",MODE_PRIVATE);

        if(isNetworkConnected()){
            callGetCylinderList();
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }

        lvSortingParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, CylinderListSortingActivity.class);
                startActivity(intent);
            }
        });
        searchViewCylinder.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(isNetworkConnected()){
                    search="";
                    cylinderList.clear();
                    cylinderList=null;
                    callGetCylinderList();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
        searchViewCylinder.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(isNetworkConnected()){
                    search=query;
                    cylinderList.clear();
                    cylinderList=null;
                    callGetCylinderList();
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
                            callGetCylinderList();
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
        if(isNetworkConnected()){
            if(cylinderEdit.getBoolean("refresh",false)){
                SharedPreferences.Editor editor = cylinderEdit.edit();
                editor.putBoolean("refresh",false);
                editor.commit();
                cylinderList.clear();
                cylinderList=null;
                callGetCylinderList();
            }else if(spSorting.getBoolean("dofilter",false)){
                //CompanyId= Integer.parseInt(settings.getString("companyId","1"));
                CyldType = spSorting.getString("text","");
                SortBy=spSorting.getString("text1","");
                if(spSorting.getString("text2","Decinding").equals("Decinding")){
                    Sort="desc";
                }else{
                    Sort="asc";
                }
                SharedPreferences.Editor companyFilterEditor = spSorting.edit();
                companyFilterEditor.putBoolean("dofilter",false);
                companyFilterEditor.commit();
                cylinderList.clear();
                cylinderList=null;
                callGetCylinderList();
            }
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }
    }

    private void callGetCylinderList() {
        isLoading=true;
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        if(cylinderList==null){
            isLoading=false;
            isLastPage=false;
            pageno=0;
            progressDialog.show();
        }else {
            progressBar.setVisibility(View.VISIBLE);
        }
        String url = MySingalton.getInstance().URL+"/Api/MobCylinder/GetCylinderList?search="+search+
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
                    j = new JSONObject(Response);
                    JSONArray jsonArray=j.getJSONArray("list");
                        totalRecord=j.getInt("totalRecord");
                        Boolean flgfirstload=false;
                        if(cylinderList==null){
                            cylinderList=new ArrayList<>();
                            flgfirstload=true;
                        }
                        for(int i=0;i<jsonArray.length();i++) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("cylinderId", jsonArray.getJSONObject(i).getInt("cylinderId")+"");
                            map.put("cylinderNoList", jsonArray.getJSONObject(i).getString("cylinderNoList") + "");
                            map.put("cylinderNo", jsonArray.getJSONObject(i).getString("cylinderNo") + "");
                            map.put("holdingCapacity", jsonArray.getJSONObject(i).getString("holdingCapacity") + "");
                            map.put("manufacturingDate", jsonArray.getJSONObject(i).getString("manufacturingDate") + "");
                            map.put("manufacturingDateStr", jsonArray.getJSONObject(i).getString("manufacturingDateStr") + "");
                            map.put("companyName", jsonArray.getJSONObject(i).getString("companyName") + "");
                            map.put("address1", jsonArray.getJSONObject(i).getString("address1") + "");
                            map.put("address2", jsonArray.getJSONObject(i).getString("address2") + "");
                            map.put("city", jsonArray.getJSONObject(i).getString("city") + "");
                            map.put("county", jsonArray.getJSONObject(i).getString("county") + "");
                            map.put("zipCode", jsonArray.getJSONObject(i).getString("zipCode") + "");
                            map.put("valveCompanyName", jsonArray.getJSONObject(i).getString("valveCompanyName") + "");
                            map.put("purchaseDate", jsonArray.getJSONObject(i).getString("purchaseDate") + "");
                            map.put("purchaseDateStr", jsonArray.getJSONObject(i).getString("purchaseDateStr") + "");
                            map.put("expireDate", jsonArray.getJSONObject(i).getString("expireDate") + "");
                            map.put("expireDateStr", jsonArray.getJSONObject(i).getString("expireDateStr") + "");
                            map.put("paintExpireDays", jsonArray.getJSONObject(i).getInt("paintExpireDays") + "");
                            map.put("testingPeriodDays", jsonArray.getJSONObject(i).getInt("testingPeriodDays") + "");
                            map.put("warehouseId", jsonArray.getJSONObject(i).getInt("warehouseId") + "");
                           // map.put("createdBy", jsonArray.getJSONObject(i).getInt("createdBy") + "");
                           // map.put("modifiedBy", jsonArray.getJSONObject(i).getInt("modifiedBy") + "");
                            map.put("createdByName", jsonArray.getJSONObject(i).getString("createdByName") + "");
                            map.put("createdDateStr", jsonArray.getJSONObject(i).getString("createdDateStr") + "");
                           // map.put("paintExpireDateStr", jsonArray.getJSONObject(i).getString("paintExpireDateStr") + "");
                           // map.put("testingPeriodDateStr", jsonArray.getJSONObject(i).getString("testingPeriodDateStr") + "");
                           // map.put("days", jsonArray.getJSONObject(i).getString("days") + "");
                           // map.put("paintExpireDate", jsonArray.getJSONObject(i).getString("paintExpireDate") + "");
                           // map.put("dt", jsonArray.getJSONObject(i).getString("dt") + "");
                            map.put("warehousename", jsonArray.getJSONObject(i).getString("warehousename") + "");
                            cylinderList.add(map);
                        }
                        if(cylinderList.size()>=totalRecord){
                            isLastPage=true;
                        }
                        if(flgfirstload){
                            flgfirstload=false;
                            cylinderListAdapter=new CylinderListAdapter(cylinderList,getActivity());
                            recyclerView.setAdapter(cylinderListAdapter);
                        }else {
                            cylinderListAdapter.notifyDataSetChanged();
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
        inflater.inflate(R.menu.cylinder_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_addCylinder:
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
                    ArrayList<HashMap<String,String>> whereHouseList=new ArrayList<>();

                    for(int i=0;i<jsonArray.length();i++) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("warehouseId", jsonArray.getJSONObject(i).getInt("warehouseId")+"");
                        map.put("name", jsonArray.getJSONObject(i).getString("name") + "");
                        whereHouseList.add(map);
                    }
                        Intent intent=new Intent(getActivity(),AddCylinderActivity.class);
                        intent.putExtra("wharehouselist",whereHouseList);
                        getActivity().startActivity(intent);
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