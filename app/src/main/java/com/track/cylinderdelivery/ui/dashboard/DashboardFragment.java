package com.track.cylinderdelivery.ui.dashboard;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.track.cylinderdelivery.LoginActivity;
import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ui.acknowledge.AcknowledgeListAdapter;
import com.track.cylinderdelivery.ui.cylinderproductmapping.AddCylinderProductMappingActivity;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashboardFragment extends Fragment {

    //private DashboardViewModel dashboardViewModel;
    Context context;
    private SharedPreferences settings;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    String search="";
    int pageno=0;
    int totalinpage=10;
    int flgDash=1;
    LinearLayout lvSortingParent;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    private int totalRecord;
    private ArrayList<HashMap<String,String>> dataList;
    private String homeview;
    private DashboardListAdapter dashboardListAdapter;
    ProgressBar progressBar;
    Boolean isLoading=false;
    Boolean isLastPage=false;
    private DashboardList2Adapter dashboardList2Adapter;
    private Button btnReceivCyl,btnHoldCyl;
    private DashboardList3Adapter dashboardList3Adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        context=getActivity();
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Dashboard");
        recyclerView=root.findViewById(R.id.rvDashboard);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        dataList=new ArrayList<HashMap<String,String>>();
        progressBar=root.findViewById(R.id.progressBar);
        homeview=settings.getString("homeView","");
        Log.d("homeView==>",homeview+"");
        btnReceivCyl=(Button)root.findViewById(R.id.btnReceivCyl);
        btnHoldCyl=(Button)root.findViewById(R.id.btnHoldCyl);
        if(homeview.equals("SuperAdmin")){
            dataList=null;
            callReceivableCylinder();
        }else {
            dataList=null;
            callClientHoldedCylinder();
            btnReceivCyl.setVisibility(View.GONE);
            btnHoldCyl.setVisibility(View.GONE);
        }
        logRegToken();
        btnReceivCyl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnReceivCyl.setTextColor(getResources().getColor(R.color.white));
                btnHoldCyl.setTextColor(getResources().getColor(R.color.black));

                btnReceivCyl.setBackgroundColor(getResources().getColor(R.color.green));
                btnHoldCyl.setBackgroundColor(getResources().getColor(R.color.lightGreen));

                dataList=null;
                callReceivableCylinder();
            }
        });
        btnHoldCyl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnReceivCyl.setTextColor(getResources().getColor(R.color.black));
                btnHoldCyl.setTextColor(getResources().getColor(R.color.white));


                btnReceivCyl.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                btnHoldCyl.setBackgroundColor(getResources().getColor(R.color.green));

                dataList=null;
                callHoldedCylinder();
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
                            if(flgDash==1){
                                callReceivableCylinder();
                            }else if(flgDash==2){
                                callHoldedCylinder();
                            }else if(flgDash==3){
                                callClientHoldedCylinder();
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

    private void callClientHoldedCylinder() {
        Log.d("Api Calling==>","Api Calling");
        flgDash=3;
        isLoading=true;
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        if(dataList==null){
            isLoading=false;
            isLastPage=false;
            pageno=0;
            progressDialog.show();
        }else {
            progressBar.setVisibility(View.VISIBLE);
        }
        String url = MySingalton.getInstance().URL+"/Api/MobHome/GetClientCylinderHolded?search="+search+
                "&pageno="+pageno+"&totalinpage="+totalinpage+
                "&CompanyId="+settings.getString("companyId","")+
                "&UserId="+settings.getString("userId","");
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
                        map.put("cylinderId", jsonArray.getJSONObject(i).getString("cylinderId") + "");
                        map.put("cylinderNo", jsonArray.getJSONObject(i).getString("cylinderNo") + "");
                        map.put("soNumber", jsonArray.getJSONObject(i).getString("soNumber") + "");
                        map.put("deliverDate", jsonArray.getJSONObject(i).getString("deliverDate") + "");
                        map.put("holdingCreditLastDate", jsonArray.getJSONObject(i).getString("holdingCreditLastDate") + "");
                        map.put("strDeliverDate", jsonArray.getJSONObject(i).getString("strDeliverDate") + "");
                        map.put("strHoldingCreditLastDate", jsonArray.getJSONObject(i).getString("strHoldingCreditLastDate") + "");
                        map.put("holdDays",jsonArray.getJSONObject(i).getString("holdDays")+"");
                        map.put("holdingExcited", jsonArray.getJSONObject(i).getString("holdingExcited") + "");

                        dataList.add(map);
                    }
                    if(dataList.size()>=totalRecord){
                        isLastPage=true;
                    }
                    if(flgfirstload){
                        flgfirstload=false;
                        dashboardList3Adapter=new DashboardList3Adapter(dataList,getActivity());
                        recyclerView.setAdapter(dashboardList3Adapter);
                        dashboardList3Adapter.notifyDataSetChanged();
                    }else {
                        dashboardList3Adapter.notifyDataSetChanged();
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

    private void callHoldedCylinder() {
        Log.d("Api Calling==>","Api Calling");
        flgDash=2;
        isLoading=true;
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        if(dataList==null){
            isLoading=false;
            isLastPage=false;
            pageno=0;
            progressDialog.show();
        }else {
            progressBar.setVisibility(View.VISIBLE);
        }
        String url = MySingalton.getInstance().URL+"/Api/MobHome/GetEmployeeCylinderHolded?search="+search+"&pageno="+pageno+
                "&totalinpage="+totalinpage+"&UserId="+settings.getString("userId","");
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
                        map.put("cylinderId", jsonArray.getJSONObject(i).getString("cylinderId") + "");
                        map.put("cylinderNo", jsonArray.getJSONObject(i).getString("cylinderNo") + "");
                        map.put("soNumber", jsonArray.getJSONObject(i).getString("soNumber") + "");
                        map.put("deliverDate", jsonArray.getJSONObject(i).getString("deliverDate") + "");
                        map.put("holdingCreditLastDate", jsonArray.getJSONObject(i).getString("holdingCreditLastDate") + "");
                        map.put("strDeliverDate", jsonArray.getJSONObject(i).getString("strDeliverDate") + "");
                        map.put("strHoldingCreditLastDate", jsonArray.getJSONObject(i).getString("strHoldingCreditLastDate") + "");
                        map.put("holdDays",jsonArray.getJSONObject(i).getString("holdDays")+"");
                        map.put("holdingExcited", jsonArray.getJSONObject(i).getString("holdingExcited") + "");
                        map.put("dnNumber",jsonArray.getJSONObject(i).getString("dnNumber")+"");
                        map.put("roNumber", jsonArray.getJSONObject(i).getString("roNumber") + "");
                        map.put("strHoledDate", jsonArray.getJSONObject(i).getString("strHoledDate") + "");

                        dataList.add(map);
                    }
                    if(dataList.size()>=totalRecord){
                        isLastPage=true;
                    }
                    if(flgfirstload){
                        flgfirstload=false;
                        dashboardList2Adapter=new DashboardList2Adapter(dataList,getActivity());
                        recyclerView.setAdapter(dashboardList2Adapter);
                        dashboardList2Adapter.notifyDataSetChanged();
                    }else {
                        dashboardList2Adapter.notifyDataSetChanged();
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

    private void callReceivableCylinder() {
        Log.d("Api Calling==>","Api Calling");
        flgDash=1;
        isLoading=true;
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        if(dataList==null){
            isLoading=false;
            isLastPage=false;
            pageno=0;
            progressDialog.show();
        }else {
            progressBar.setVisibility(View.VISIBLE);
        }
        String url = MySingalton.getInstance().URL+"/Api/MobHome/GetCylinderHoldLastDay?search="+search+
                "&pageno="+pageno+"&totalinpage="+totalinpage+
                "&CompanyId="+settings.getString("companyId","");
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
                        map.put("cylinderId", jsonArray.getJSONObject(i).getString("cylinderId") + "");
                        map.put("cylinderNo", jsonArray.getJSONObject(i).getString("cylinderNo") + "");
                        map.put("companyName", jsonArray.getJSONObject(i).getString("companyName") + "");
                        map.put("address1", jsonArray.getJSONObject(i).getString("address1") + "");
                        map.put("address2", jsonArray.getJSONObject(i).getString("address2") + "");
                        map.put("city", jsonArray.getJSONObject(i).getString("city") + "");
                        map.put("county", jsonArray.getJSONObject(i).getString("county") + "");
                        map.put("zipCode",jsonArray.getJSONObject(i).getString("zipCode")+"");
                        map.put("phone", jsonArray.getJSONObject(i).getString("phone") + "");
                        map.put("secondaryPhone",jsonArray.getJSONObject(i).getString("secondaryPhone")+"");
                        map.put("email", jsonArray.getJSONObject(i).getString("email") + "");
                        map.put("secondaryEmail", jsonArray.getJSONObject(i).getString("secondaryEmail") + "");
                        map.put("deliverDate", jsonArray.getJSONObject(i).getString("deliverDate") + "");
                        map.put("holdingCreditLastDate", jsonArray.getJSONObject(i).getString("holdingCreditLastDate") + "");
                        map.put("strDeliverDate", jsonArray.getJSONObject(i).getString("strDeliverDate") + "");
                        map.put("strHoldingCreditLastDate", jsonArray.getJSONObject(i).getString("strHoldingCreditLastDate") + "");
                        map.put("holdDays", jsonArray.getJSONObject(i).getString("holdDays") + "");
                        map.put("holdingExcited", jsonArray.getJSONObject(i).getString("holdingExcited") + "");
                        dataList.add(map);
                    }
                    if(dataList.size()>=totalRecord){
                        isLastPage=true;
                    }
                    if(flgfirstload){
                        flgfirstload=false;
                        dashboardListAdapter=new DashboardListAdapter(dataList,getActivity());
                        recyclerView.setAdapter(dashboardListAdapter);
                        dashboardListAdapter.notifyDataSetChanged();
                    }else {
                        dashboardListAdapter.notifyDataSetChanged();
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
    public void onResume() {
        super.onResume();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void logRegToken() {
        // [START log_reg_token]
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM==>", "Fetching FCM registration token failed==>", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = "FCM Registration token: " + token;
                        Log.d("FCM"+"==>", msg);
                        //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // [END log_reg_token]
    }
}