package com.track.cylinderdelivery.ui.diliverynote;

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
import com.track.cylinderdelivery.ui.product.AddProductActivity;
import com.track.cylinderdelivery.ui.purchaseorder.AddPurchaseOrderActivity;
import com.track.cylinderdelivery.ui.purchaseorder.PurchaseOrderListAdapter;
import com.track.cylinderdelivery.ui.purchaseorder.purchaseOrderSorting;
import com.track.cylinderdelivery.ui.salesorder.SOFilterActivity;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class DeliveryNoteFragment extends Fragment {

    Context context;
    private SharedPreferences settings;
    private static final int MY_SOCKET_TIMEOUT_MS = 10000;
    private String search="";
    private int pageno=0;
    private int totalinpage=10;
    private String Sort="desc";
    private String SortBy;
    private ProgressBar progressBar;
    private ArrayList<HashMap<String,String>> deliveryNoteList;
    private int totalRecord;
    private boolean isLoading=false;
    private boolean isLastPage=false;
    private DeliveryNoteListAdapter deliveryNoteListAdapter;
    RecyclerView recyclerView;
    SearchView svSearch;
    LinearLayout lvSortingParent,lvFilterParent;
    SharedPreferences spSorting;
    private String ftext="ALL";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_delivery_note, container, false);
        context=getActivity();
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.Delivery_Note_List
        ));

        recyclerView=root.findViewById(R.id.rv_delivery_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        settings=context.getSharedPreferences("setting",MODE_PRIVATE);
        progressBar=root.findViewById(R.id.progressBar);
        svSearch=root.findViewById(R.id.svSearch);
        lvSortingParent=root.findViewById(R.id.lvSortingParent);
        lvFilterParent=root.findViewById(R.id.lvFilterParent);
        spSorting=context.getSharedPreferences("DNFilter",MODE_PRIVATE);

        if(isNetworkConnected()){
            callGetDeliveryNoteList();
        }else {
            Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
        }

        lvFilterParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, DNFilterActivity.class);
                intent.putExtra("selectedtext",ftext);
                startActivity(intent);
            }
        });
        lvSortingParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, DeliveryNoteSortingActivity.class);
                startActivity(intent);
            }
        });
        svSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(isNetworkConnected()){
                    search="";
                    deliveryNoteList=null;
                    callGetDeliveryNoteList();
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
                    deliveryNoteList=null;
                    callGetDeliveryNoteList();
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
                            callGetDeliveryNoteList();
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
            SortBy=spSorting.getString("text1","1");
            if(spSorting.getString("text2","Decinding").equals("Decinding")){
                Sort="desc";
            }else{
                Sort="asc";
            }
            ftext=spSorting.getString("ftext","ALL");
            if(isNetworkConnected()){
                deliveryNoteList=null;
                callGetDeliveryNoteList();
            }else {
                Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void callGetDeliveryNoteList() {
        isLoading=true;
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        if(deliveryNoteList==null){
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
        String url = MySingalton.getInstance().URL+"/Api/MobDeliveryNote/GetDeliveryNoteList?search="+search+
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
                    Boolean flgfirstload=false;
                    if(deliveryNoteList==null){
                        deliveryNoteList=new ArrayList<>();
                        flgfirstload=true;
                    }
                    j = new JSONObject(Response);
                    totalRecord=j.getInt("totalRecord");
                    JSONArray jsonArray=j.getJSONArray("list");

                    for(int i=0;i<jsonArray.length();i++){
                        HashMap<String,String> map=new HashMap<>();
                        map.put("dnId",jsonArray.getJSONObject(i).getInt("dnId")+"");
                        map.put("dnNumber",jsonArray.getJSONObject(i).getString("dnNumber")+"");
                        map.put("userId",jsonArray.getJSONObject(i).getString("userId")+"");
                        map.put("status",jsonArray.getJSONObject(i).getString("status")+"");
                        map.put("dnDate",jsonArray.getJSONObject(i).getString("dnDate")+"");
                        map.put("dnGeneratedBy",jsonArray.getJSONObject(i).getString("dnGeneratedBy")+"");
                        map.put("createdBy",jsonArray.getJSONObject(i).getString("createdBy")+"");
                        map.put("strDNDate",jsonArray.getJSONObject(i).getString("strDNDate")+"");
                        map.put("username",jsonArray.getJSONObject(i).getString("username")+"");
                        map.put("userEmail",jsonArray.getJSONObject(i).getString("userEmail"));
                        map.put("quantity",jsonArray.getJSONObject(i).getString("quantity")+"");
                        map.put("dndm",jsonArray.getJSONObject(i).getString("dndm")+"");
                        map.put("dnrcm",jsonArray.getJSONObject(i).getString("dnrcm"));
                        map.put("toCompanyId",jsonArray.getJSONObject(i).getString("toCompanyId"));
                        map.put("isDeletable",jsonArray.getJSONObject(i).getString("isDeletable"));
                        deliveryNoteList.add(map);
                    }

                    if(deliveryNoteList.size()>=totalRecord){
                        isLastPage=true;
                    }
                    if(flgfirstload){
                        flgfirstload=false;
                        deliveryNoteListAdapter=new DeliveryNoteListAdapter(deliveryNoteList,getActivity());
                        recyclerView.setAdapter(deliveryNoteListAdapter);
                    }else {
                        deliveryNoteListAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    deliveryNoteListAdapter=new DeliveryNoteListAdapter(deliveryNoteList,getActivity());
                    recyclerView.setAdapter(deliveryNoteListAdapter);
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
        inflater.inflate(R.menu.deliverynote_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_addDiliverNote:
                // do stuff, like showing settings fragment
                if(isNetworkConnected()){
                    callGetDNNo();
                }else {
                    Toast.makeText(context, "Kindly check your internet connectivity.", Toast.LENGTH_LONG).show();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callGetDNNo() {
        Log.d("Api Calling==>","Api Calling");
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        String url = MySingalton.getInstance().URL+"/Api/MobDeliveryNote/GetDNNo?CompanyId="+Integer.parseInt(settings.getString("companyId","1"));
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
                        String DNNumber=j.getString("message");
                        Intent intent=new Intent(getActivity(), AddDeliveryNoteActivity.class);
                        intent.putExtra("DNNumber",DNNumber);
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}