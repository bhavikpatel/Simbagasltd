package com.track.cylinderdelivery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;

public class SplashScreen extends AppCompatActivity {

    private Handler handler;
    private SharedPreferences settings;
    private boolean loggedIN;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context=this;
        settings=getSharedPreferences("setting",MODE_PRIVATE);
        loggedIN=settings.getBoolean("loggedIN",false);

       // runtimeEnableAutoInit();
       /* if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("SplashScreen", "Key: " + key + " Value: " + value);
            }
        }*/

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!loggedIN){
                    Intent intent=new Intent(context,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent=new Intent(context,Dashboard.class);
                    intent.putExtra("Activity","");
                    startActivity(intent);
                    finish();
                }
            }
        },3000);

    }
   /* public void runtimeEnableAutoInit() {
        // [START fcm_runtime_enable_auto_init]
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        // [END fcm_runtime_enable_auto_init]
    }*/
}