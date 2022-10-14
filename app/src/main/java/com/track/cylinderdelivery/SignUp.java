package com.track.cylinderdelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.track.cylinderdelivery.ui.BaseActivity;

public class SignUp extends BaseActivity {

    private TextView txtBackLogin;
    private Context context;
    private Button btn_signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context=this;
        txtBackLogin=(TextView)findViewById(R.id.txtBackLogin);
        btn_signup=(Button)findViewById(R.id.btn_signup);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupActivity=new Intent(context,Dashboard.class);
                startActivity(signupActivity);
                finish();
            }
        });
        txtBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupActivity=new Intent(context,LoginActivity.class);
                startActivity(signupActivity);
                finish();
            }
        });
    }
}