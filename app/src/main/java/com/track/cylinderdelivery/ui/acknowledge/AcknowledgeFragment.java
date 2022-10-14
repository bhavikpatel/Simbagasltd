package com.track.cylinderdelivery.ui.acknowledge;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.track.cylinderdelivery.ui.company.CompanyListAdapter;
import com.track.cylinderdelivery.ui.user.UserListAdapter;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class AcknowledgeFragment extends Fragment {

    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    Button btnApproved,btnRejected,btnPanding;
    RecyclerView rvAcknowledge;
    Context context;
    private String search="";
    private int pageno=0;
    private int totalinpage=10;
    private int CompanyId;
    private SharedPreferences settings;
    private SharedPreferences shpreRefresh;
    Boolean isLoading=false;
    Boolean isLastPage=false;
    ProgressBar progressBar;
    int totalRecord;
    int flgAck=1;
    ArrayList<HashMap<String,String>> dataList;
    private AcknowledgeListAdapter acknowledgeListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_acknowledge, container, false);
        context=getActivity();
         setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Acknowledge List");
        rvAcknowledge=root.findViewById(R.id.rvAcknowledge);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvAcknowledge.setLayoutManager(layoutManager);
        btnPanding=root.findViewById(R.id.btnPanding);
        btnApproved=root.findViewById(R.id.btnApproved);
        btnRejected=root.findViewById(R.id.btnRejected);
        progressBar=root.findViewById(R.id.progressBar);
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        CompanyId= Integer.parseInt(settings.getString("companyId","1"));
        shpreRefresh=context.getSharedPreferences("ackRefresh",MODE_PRIVATE);

        SharedPreferences.Editor userFilterEditor = shpreRefresh.edit();
        userFilterEditor.putBoolean("dofilter",false);
        userFilterEditor.putInt("fromlist",1);
        userFilterEditor.commit();

        callPandingAckApi();

        btnPanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor userFilterEditor = shpreRefresh.edit();
                //userFilterEditor.putBoolean("dofilter",false);
                userFilterEditor.putInt("fromlist",1);
                userFilterEditor.commit();
                btnPanding.setTextColor(getResources().getColor(R.color.white));
                btnApproved.setTextColor(getResources().getColor(R.color.black));
                btnRejected.setTextColor(getResources().getColor(R.color.black));

                btnPanding.setBackgroundColor(getResources().getColor(R.color.green));
                btnApproved.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                btnRejected.setBackgroundColor(getResources().getColor(R.color.lightGreen));

                dataList=null;
                callPandingAckApi();
            }
        });
        btnApproved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor userFilterEditor = shpreRefresh.edit();
                //userFilterEditor.putBoolean("dofilter",false);
                userFilterEditor.putInt("fromlist",2);
                userFilterEditor.commit();

                btnPanding.setTextColor(getResources().getColor(R.color.black));
                btnApproved.setTextColor(getResources().getColor(R.color.white));
                btnRejected.setTextColor(getResources().getColor(R.color.black));

                btnPanding.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                btnApproved.setBackgroundColor(getResources().getColor(R.color.green));
                btnRejected.setBackgroundColor(getResources().getColor(R.color.lightGreen));

                dataList=null;
                callApprovedAckApi();
            }
        });
        btnRejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor userFilterEditor = shpreRefresh.edit();
                //userFilterEditor.putBoolean("dofilter",false);
                userFilterEditor.putInt("fromlist",3);
                userFilterEditor.commit();

                btnPanding.setTextColor(getResources().getColor(R.color.black));
                btnApproved.setTextColor(getResources().getColor(R.color.black));
                btnRejected.setTextColor(getResources().getColor(R.color.white));

                btnPanding.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                btnApproved.setBackgroundColor(getResources().getColor(R.color.lightGreen));
                btnRejected.setBackgroundColor(getResources().getColor(R.color.green));

                dataList=null;
                callRejectedAckApi();
            }
        });

        rvAcknowledge.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                callPandingAckApi();
                            }else if(flgAck==2){
                                callApprovedAckApi();
                            }else if(flgAck==3){
                                callRejectedAckApi();
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
        if(shpreRefresh.getBoolean("dofilter",false)){
            SharedPreferences.Editor userFilterEditor = shpreRefresh.edit();
            userFilterEditor.putBoolean("dofilter",false);
            userFilterEditor.putInt("fromlist",1);
            userFilterEditor.commit();

            btnPanding.setTextColor(getResources().getColor(R.color.white));
            btnApproved.setTextColor(getResources().getColor(R.color.black));
            btnRejected.setTextColor(getResources().getColor(R.color.black));

            btnPanding.setBackgroundColor(getResources().getColor(R.color.green));
            btnApproved.setBackgroundColor(getResources().getColor(R.color.lightGreen));
            btnRejected.setBackgroundColor(getResources().getColor(R.color.lightGreen));

            dataList=null;
            callPandingAckApi();
        }
    }

    private void callRejectedAckApi() {
        flgAck=3;
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
        String url = MySingalton.getInstance().URL +"/Api/MobAcknowledge/RejectedAcknowledgeList?search="+search+"&pageno="+pageno+"&totalinpage="+totalinpage+"&CompanyId="+CompanyId;
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
                        map.put("acknowledgeId", jsonArray.getJSONObject(i).getString("acknowledgeId") + "");
                        map.put("userId", jsonArray.getJSONObject(i).getString("userId") + "");
                        map.put("companyId", jsonArray.getJSONObject(i).getString("companyId") + "");
                        map.put("userName", jsonArray.getJSONObject(i).getString("userName") + "");
                        map.put("companyName", jsonArray.getJSONObject(i).getString("companyName") + "");
                        map.put("remark", jsonArray.getJSONObject(i).getString("remark") + "");
                        map.put("achnowledgeRemark", jsonArray.getJSONObject(i).getString("achnowledgeRemark") + "");
                        map.put("holdingCapacity", jsonArray.getJSONObject(i).getString("holdingCapacity") + "");
                        map.put("acknowledgeBy", jsonArray.getJSONObject(i).getString("acknowledgeBy") + "");
                        map.put("acknowledgeDate", jsonArray.getJSONObject(i).getString("acknowledgeDate") + "");
                        map.put("status", jsonArray.getJSONObject(i).getString("status") + "");
                        map.put("createdBy", jsonArray.getJSONObject(i).getString("createdBy") + "");
                        map.put("createdByName", jsonArray.getJSONObject(i).getString("createdByName") + "");
                        map.put("createdDate", jsonArray.getJSONObject(i).getString("createdDate") + "");
                        map.put("createdDateStr", jsonArray.getJSONObject(i).getString("createdDateStr") + "");
                        map.put("modifiedBy", jsonArray.getJSONObject(i).getString("modifiedBy") + "");
                        map.put("modifiedDate", jsonArray.getJSONObject(i).getString("modifiedDate") + "");
                        map.put("modifiedDateStr", jsonArray.getJSONObject(i).getString("modifiedDateStr") + "");
                        map.put("acknowledgeByName", jsonArray.getJSONObject(i).getString("acknowledgeByName") + "");
                        map.put("acknowledgeDateStr", jsonArray.getJSONObject(i).getString("acknowledgeDateStr") + "");
                        map.put("userDetails", jsonArray.getJSONObject(i).getString("userDetails") + "");
                        dataList.add(map);
                    }

                    if(dataList.size()>=totalRecord){
                        isLastPage=true;
                    }
                    if(flgfirstload){
                        flgfirstload=false;
                        acknowledgeListAdapter=new AcknowledgeListAdapter(dataList,getActivity());
                        rvAcknowledge.setAdapter(acknowledgeListAdapter);
                        acknowledgeListAdapter.notifyDataSetChanged();
                    }else {
                        acknowledgeListAdapter.notifyDataSetChanged();
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

    private void callApprovedAckApi() {
        flgAck=2;
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
        String url = MySingalton.getInstance().URL +"/Api/MobAcknowledge/ApproveAcknowledgeList?search="+search+
                "&pageno="+pageno+"&totalinpage="+totalinpage+"&CompanyId="+CompanyId;
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
                        map.put("acknowledgeId", jsonArray.getJSONObject(i).getString("acknowledgeId") + "");
                        map.put("userId", jsonArray.getJSONObject(i).getString("userId") + "");
                        map.put("companyId", jsonArray.getJSONObject(i).getString("companyId") + "");
                        map.put("userName", jsonArray.getJSONObject(i).getString("userName") + "");
                        map.put("companyName", jsonArray.getJSONObject(i).getString("companyName") + "");
                        map.put("remark", jsonArray.getJSONObject(i).getString("remark") + "");
                        map.put("achnowledgeRemark", jsonArray.getJSONObject(i).getString("achnowledgeRemark") + "");
                        map.put("holdingCapacity", jsonArray.getJSONObject(i).getString("holdingCapacity") + "");
                        map.put("acknowledgeBy", jsonArray.getJSONObject(i).getString("acknowledgeBy") + "");
                        map.put("acknowledgeDate", jsonArray.getJSONObject(i).getString("acknowledgeDate") + "");
                        map.put("status", jsonArray.getJSONObject(i).getString("status") + "");
                        map.put("createdBy", jsonArray.getJSONObject(i).getString("createdBy") + "");
                        map.put("createdByName", jsonArray.getJSONObject(i).getString("createdByName") + "");
                        map.put("createdDate", jsonArray.getJSONObject(i).getString("createdDate") + "");
                        map.put("createdDateStr", jsonArray.getJSONObject(i).getString("createdDateStr") + "");
                        map.put("modifiedBy", jsonArray.getJSONObject(i).getString("modifiedBy") + "");
                        map.put("modifiedDate", jsonArray.getJSONObject(i).getString("modifiedDate") + "");
                        map.put("modifiedDateStr", jsonArray.getJSONObject(i).getString("modifiedDateStr") + "");
                        map.put("acknowledgeByName", jsonArray.getJSONObject(i).getString("acknowledgeByName") + "");
                        map.put("acknowledgeDateStr", jsonArray.getJSONObject(i).getString("acknowledgeDateStr") + "");
                        map.put("userDetails", jsonArray.getJSONObject(i).getString("userDetails") + "");
                        dataList.add(map);
                    }
                    if(dataList.size()>=totalRecord){
                        isLastPage=true;
                    }
                    if(flgfirstload){
                        flgfirstload=false;
                        acknowledgeListAdapter=new AcknowledgeListAdapter(dataList,getActivity());
                        rvAcknowledge.setAdapter(acknowledgeListAdapter);
                        acknowledgeListAdapter.notifyDataSetChanged();
                    }else {
                        acknowledgeListAdapter.notifyDataSetChanged();
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

    private void callPandingAckApi() {
        flgAck=1;
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
        String url = MySingalton.getInstance().URL+"/Api/MobAcknowledge/PendingAcknowledgeList?search="+search+"&pageno="+pageno+"&totalinpage="+totalinpage+"&CompanyId="+CompanyId;
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
                        map.put("acknowledgeId", jsonArray.getJSONObject(i).getString("acknowledgeId") + "");
                        map.put("userId", jsonArray.getJSONObject(i).getString("userId") + "");
                        map.put("companyId", jsonArray.getJSONObject(i).getString("companyId") + "");
                        map.put("userName", jsonArray.getJSONObject(i).getString("userName") + "");
                        map.put("companyName", jsonArray.getJSONObject(i).getString("companyName") + "");
                        map.put("remark", jsonArray.getJSONObject(i).getString("remark") + "");
                        map.put("achnowledgeRemark", jsonArray.getJSONObject(i).getString("achnowledgeRemark") + "");
                        map.put("perMonthRequirement",jsonArray.getJSONObject(i).getString("perMonthRequirement")+"");
                        map.put("holdingCapacity", jsonArray.getJSONObject(i).getString("holdingCapacity") + "");
                        map.put("cylinderHoldingCreditDays",jsonArray.getJSONObject(i).getString("cylinderHoldingCreditDays")+"");
                        map.put("acknowledgeBy", jsonArray.getJSONObject(i).getString("acknowledgeBy") + "");
                        map.put("acknowledgeDate", jsonArray.getJSONObject(i).getString("acknowledgeDate") + "");
                        map.put("status", jsonArray.getJSONObject(i).getString("status") + "");
                        map.put("createdBy", jsonArray.getJSONObject(i).getString("createdBy") + "");
                        map.put("createdByName", jsonArray.getJSONObject(i).getString("createdByName") + "");
                        map.put("createdDate", jsonArray.getJSONObject(i).getString("createdDate") + "");
                        map.put("createdDateStr", jsonArray.getJSONObject(i).getString("createdDateStr") + "");
                        map.put("modifiedBy", jsonArray.getJSONObject(i).getString("modifiedBy") + "");
                        map.put("modifiedDate", jsonArray.getJSONObject(i).getString("modifiedDate") + "");
                        map.put("modifiedDateStr", jsonArray.getJSONObject(i).getString("modifiedDateStr") + "");
                        map.put("acknowledgeByName", jsonArray.getJSONObject(i).getString("acknowledgeByName") + "");
                        map.put("acknowledgeDateStr", jsonArray.getJSONObject(i).getString("acknowledgeDateStr") + "");
                        map.put("userDetails", jsonArray.getJSONObject(i).getString("userDetails") + "");
                        dataList.add(map);
                    }
                    if(dataList.size()>=totalRecord){
                        isLastPage=true;
                    }
                    if(flgfirstload){
                        flgfirstload=false;
                        acknowledgeListAdapter=new AcknowledgeListAdapter(dataList,getActivity());
                        rvAcknowledge.setAdapter(acknowledgeListAdapter);
                        acknowledgeListAdapter.notifyDataSetChanged();
                    }else {
                        acknowledgeListAdapter.notifyDataSetChanged();
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}