package com.track.cylinderdelivery;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.track.cylinderdelivery.ui.BaseActivity;
import com.track.cylinderdelivery.ui.acknowledge.AcknowledgeListAdapter;
import com.track.cylinderdelivery.utils.TransparentProgressDialog;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Dashboard extends BaseActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private Context context;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private TextView txtUserName,txtEmail,txtLogout,txtVersion;
    private SharedPreferences settings;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    JSONArray accessMOdelarray;
    String menuNumber="";
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        context=this;
        String activitylog=getIntent().getStringExtra("Activity");
        Log.d("Activity==>",activitylog+"");
        if(activitylog!=null || !activitylog.equals("")){
            char[] charArray =activitylog.toCharArray();

            for(int i=0;i<charArray.length;i++){
                if(Character.isDigit(charArray[i]))
                    menuNumber+=charArray[i];
            }
            Log.d("menuNumber==>",menuNumber);
        }
        settings=getSharedPreferences("setting",MODE_PRIVATE);
        String  accessModel=settings.getString("accessModel","");
        if(accessModel!=null && !accessModel.equals("")){
            try {
                accessMOdelarray=new JSONArray(accessModel);
                //"accessModel":[{"id":"1","parent":"#","text":"Dashboard"},
                // {"id":"12","parent":"#","text":"Purchase Order"}]}}
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(context,"Kindly Contact Administrator of Application",Toast.LENGTH_SHORT).show();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             /*   Snackbar.make(view, "We will put universal QR scan", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Dashboard.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }else {
                    openQrScan();
                }
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        txtUserName=(TextView)hView.findViewById(R.id.txtUserName);
        txtEmail=(TextView)hView.findViewById(R.id.txtEmail);
        txtLogout=(TextView)hView.findViewById(R.id.txtLogout);
        txtVersion=(TextView)hView.findViewById(R.id.txtVersion);
        txtUserName.setText(settings.getString("fullName",""));
        txtEmail.setText(settings.getString("email",""));
        //int versionCode = BuildConfig.VERSION_CODE;
        //String versionName = BuildConfig.VERSION_NAME;
        String versionName=getVersionName(context);
        double versionNamecheck=Double.parseDouble(versionName);
        txtVersion.setText("Version: "+ versionName);
        Log.d("versionNameCheck==>",versionNamecheck+"");

        callVersionCheckAPI(versionNamecheck);

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = settings.edit();
                //editor.putBoolean("loggedIN",false);
                editor.clear();
                editor.apply();
                editor.commit();
                finish();
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard)
                .setDrawerLayout(drawer)
                .build();*/
         mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_company, R.id.nav_user,R.id.nav_cylinder,
                R.id.nav_product,R.id.nav_warehouse,R.id.nav_acknowledge,R.id.nav_purchaseorder,
                 R.id.nav_cylinderproductmapping,R.id.nav_report,R.id.nav_cylinderWarehouseMapping,
                 R.id.nav_permssion,R.id.nav_delivery_note,R.id.nav_sales_order,R.id.nav_return_order,
                 R.id.nav_ro_cylinder_warehouse_mapping,R.id.nav_reconciliatoin)
                .setDrawerLayout(drawer)
                .build();

/*        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_company, R.id.nav_user,R.id.nav_acknowledge,
                R.id.nav_cylinderproductmapping,R.id.nav_cylinderWarehouseMapping,
                R.id.nav_purchaseorder,R.id.nav_delivery_note,R.id.nav_sales_order,
                R.id.nav_return_order,R.id.nav_cylinder,
                R.id.nav_product,R.id.nav_warehouse,R.id.nav_report,R.id.nav_permssion)
                .setDrawerLayout(drawer)
                .build();*/

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

       JSONObject menuJsonobj=new JSONObject();
       Menu menu = navigationView.getMenu();

/*        SubMenu subMenu = menu.addSubMenu(getString(R.string.company));
        subMenu.add(0, Menu.FIRST, Menu.FIRST, getString(R.string.company_list))
                .setIcon(R.drawable.ic_menu_gallery);
        subMenu.add(1, Menu.FIRST + 1, Menu.FIRST, getString(R.string.company_list))
                .setIcon(R.drawable.ic_menu_camera);*/

       addMenuDynamic(menu,menuJsonobj);


        /*navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==1){
                    Toast.makeText(context,getText(R.string.dashboard),Toast.LENGTH_LONG).show();
                }else if(item.getItemId()==2){
                    Toast.makeText(context,getText(R.string.company),Toast.LENGTH_LONG).show();
                }
                drawer.closeDrawers();
                return false;
            }
        });*/

        if(menuNumber.equals("3")){
            navController.navigate(R.id.action_dashbord_company);
        }else if(menuNumber.equals("4")){
            navController.navigate(R.id.action_dashbord_user);
        }else if(menuNumber.equals("9")){
            //menu.findItem(R.id.nav_acknowledge).setVisible(true);
            navController.navigate(R.id.action_dashbord_acknowledge);
        }else if(menuNumber.equals("6")){
            //menu.findItem(R.id.nav_cylinder).setVisible(true);
            navController.navigate(R.id.action_dashbord_cylinder);
        }else if(menuNumber.equals("13")){
            //menu.findItem(R.id.nav_cylinderproductmapping).setVisible(true);
            navController.navigate(R.id.action_dashbord_cylinderproductmapping);
        }else if(menuNumber.equals("14")){
            //menu.findItem(R.id.nav_cylinderWarehouseMapping).setVisible(true);
            navController.navigate(R.id.action_dashbord_cylinderWarehouseMapping);
        }else if(menuNumber.equals("12")){
            //menu.findItem(R.id.nav_purchaseorder).setVisible(true);
            navController.navigate(R.id.action_dashbord_purchaseorder);
        }else if(menuNumber.equals("16")){
            //menu.findItem(R.id.nav_delivery_note).setVisible(true);
            navController.navigate(R.id.action_dashbord_delivery_note);
        }else if(menuNumber.equals("16")){
            //menu.findItem(R.id.nav_sales_order).setVisible(true);
            navController.navigate(R.id.action_dashbord_sales_order);
        }else if(menuNumber.equals("18")){
            //menu.findItem(R.id.nav_return_order).setVisible(true);
            navController.navigate(R.id.action_dashbord_return_order);
        }else if(menuNumber.equals("19")){
            //menu.findItem(R.id.nav_ro_cylinder_warehouse_mapping).setVisible(true);
            navController.navigate(R.id.action_dashbord_ro_cylinder_warehouse_mapping);
        }else if(menuNumber.equals("32")){
            //menu.findItem(R.id.nav_ro_cylinder_warehouse_mapping).setVisible(true);
            navController.navigate(R.id.action_reconciliation);
        }
        menuNumber="";
    }

    private void callVersionCheckAPI(double versionNamecheck) {

        String url = MySingalton.getInstance().URL+"/api/mobhome/checkversion";
        Log.d("request==>",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,new Response.Listener<String>() {
            @Override
            public void onResponse(String Response) {

                Log.d("resonse ==>",Response+"");
                JSONObject j;
                try {
                    j = new JSONObject(Response);
                    if(j.getBoolean("status")==true){
                        double updatedVersion=j.getDouble("data");
                        Log.d("updatedVersion==>",updatedVersion+"");
                        if(updatedVersion>versionNamecheck){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Update new version!")
                                    .setCancelable(false)
                                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.track.cylinderdelivery"));
                                            startActivity(intent);
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

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

    private void openQrScan() {
        Intent intent = new Intent(context, ContinuousCaptureActivity.class);
        startActivity(intent);
    }

    private void addMenuDynamic(Menu menu, JSONObject menuJsonobj) {
        //menu.findItem(R.id.nav_company).getSubMenu().addSubMenu(getResources().getString(R.string.company));
        menu.findItem(R.id.nav_product).setVisible(false);
        menu.findItem(R.id.nav_warehouse).setVisible(false);
        menu.findItem(R.id.nav_permssion).setVisible(false);
        menu.findItem(R.id.nav_report).setVisible(false);
       // menu.findItem(R.id.nav_return_order).setVisible(false);
       /* R.id.nav_dashboard, R.id.nav_company, R.id.nav_user,R.id.nav_cylinder,
                R.id.nav_product,R.id.nav_warehouse,R.id.nav_acknowledge,R.id.nav_purchaseorder,
                R.id.nav_cylinderproductmapping,R.id.nav_report,R.id.nav_cylinderWarehouseMapping,
                R.id.nav_permssion,R.id.nav_delivery_note,R.id.nav_sales_order,R.id.nav_return_order)*/
        menu.findItem(R.id.nav_dashboard).setVisible(false);
        menu.findItem(R.id.nav_company).setVisible(false);
        menu.findItem(R.id.nav_user).setVisible(false);
        menu.findItem(R.id.nav_acknowledge).setVisible(false);
        menu.findItem(R.id.nav_cylinder).setVisible(false);
        menu.findItem(R.id.nav_cylinderproductmapping).setVisible(false);
        menu.findItem(R.id.nav_cylinderWarehouseMapping).setVisible(false);
        menu.findItem(R.id.nav_purchaseorder).setVisible(false);
        menu.findItem(R.id.nav_delivery_note).setVisible(false);
        menu.findItem(R.id.nav_sales_order).setVisible(false);
        menu.findItem(R.id.nav_return_order).setVisible(false);
        menu.findItem(R.id.nav_ro_cylinder_warehouse_mapping).setVisible(false);
        menu.findItem(R.id.nav_reconciliatoin).setVisible(false);
        for(int i=0;i<accessMOdelarray.length();i++){
            try {
                JSONObject obj=accessMOdelarray.getJSONObject(i);
                if(obj.get("id").equals("1")){
                    menu.findItem(R.id.nav_dashboard).setVisible(true);
                }
                if(obj.get("id").equals("3")){
                    menu.findItem(R.id.nav_company).setVisible(true);
                }
                if(obj.get("id").equals("4")){
                    menu.findItem(R.id.nav_user).setVisible(true);
                }
                if(obj.get("id").equals("9")){
                    menu.findItem(R.id.nav_acknowledge).setVisible(true);
                }
                if(obj.get("id").equals("6")){
                    menu.findItem(R.id.nav_cylinder).setVisible(true);
                }
                if(obj.get("id").equals("13")){
                    menu.findItem(R.id.nav_cylinderproductmapping).setVisible(true);
                }
                if(obj.get("id").equals("14")){
                    menu.findItem(R.id.nav_cylinderWarehouseMapping).setVisible(true);
                }
                if(obj.get("id").equals("12")){
                    menu.findItem(R.id.nav_purchaseorder).setVisible(true);
                }
                if(obj.get("id").equals("16")){
                    menu.findItem(R.id.nav_delivery_note).setVisible(true);
                }
                if(obj.get("id").equals("16")){
                    menu.findItem(R.id.nav_sales_order).setVisible(true);
                }
                if(obj.get("id").equals("18")){
                    menu.findItem(R.id.nav_return_order).setVisible(true);
                }
                if(obj.get("id").equals("19")){
                    menu.findItem(R.id.nav_ro_cylinder_warehouse_mapping).setVisible(true);
                }
                if(obj.get("id").equals("32")){
                    menu.findItem(R.id.nav_reconciliatoin).setVisible(true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

     //   getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                Log.i("Camera", "G : " + grantResults[0]);
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted,
                    openQrScan();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.CAMERA)) {
                        //showAlert();
                    } else {

                    }
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public String getVersionName(Context context) {
        try {
            // Get PackageManager instance
            PackageManager packageManager = context.getPackageManager();

            // Get the PackageInfo object for the app's package
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);

            // Return the version name from the PackageInfo object
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null; // Or handle the exception as needed
        }
    }
}