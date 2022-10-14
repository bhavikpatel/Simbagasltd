package com.track.cylinderdelivery.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;

import java.util.HashMap;

public class Detail1Activity extends AppCompatActivity {

    Context context;
    private HashMap<String, String> mapdata;
    private TextView txtUserName;
    private ImageView btnCancel;
    private RelativeLayout rvBlank;
    private TextView txtHoldedCompany,txtAddress,txtEmail,txtSecondaryEmail,txtMobile;
    private TextView txtSecondaryMobile,txtDeliverDate,txtHoldingcreditdate,txtHoldDays,txtHoldingOverdue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail1);
        context=this;
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("editData");
        txtUserName=findViewById(R.id.txtUserName);
        btnCancel=findViewById(R.id.btnCancel);
        rvBlank=findViewById(R.id.rvBlank);
        txtHoldedCompany=findViewById(R.id.txtHoldedCompany);
        txtEmail=findViewById(R.id.txtEmail);
        txtAddress=findViewById(R.id.txtAddress);
        txtSecondaryEmail=findViewById(R.id.txtSecondaryEmail);
        txtMobile=findViewById(R.id.txtMobile);
        txtSecondaryMobile=findViewById(R.id.txtSecondaryMobile);
        txtDeliverDate=findViewById(R.id.txtDeliverDate);
        txtHoldingcreditdate=findViewById(R.id.txtHoldingcreditdate);
        txtHoldDays=findViewById(R.id.txtHoldDays);
        txtHoldingOverdue=findViewById(R.id.txtHoldingOverdue);

        txtUserName.setText(MySingalton.convertString(mapdata.get("cylinderNo")));
        txtHoldedCompany.setText(MySingalton.convertString(mapdata.get("companyName")));
        txtAddress.setText(MySingalton.convertString(mapdata.get("address1"))+","+
                MySingalton.convertString(mapdata.get("address2"))+","+
                MySingalton.convertString(mapdata.get("city"))+","+
                MySingalton.convertString(mapdata.get("county"))+","+
                MySingalton.convertString(mapdata.get("zipCode")));
        txtEmail.setText(MySingalton.convertString(mapdata.get("email")));
        txtSecondaryEmail.setText(MySingalton.convertString(mapdata.get("secondaryEmail")));
        txtMobile.setText(MySingalton.convertString(mapdata.get("phone")));
        txtSecondaryMobile.setText(MySingalton.convertString(mapdata.get("secondaryPhone")));
        txtDeliverDate.setText(MySingalton.convertString(mapdata.get("strDeliverDate")));
        txtHoldingcreditdate.setText(MySingalton.convertString(mapdata.get("strHoldingCreditLastDate")));
        txtHoldDays.setText(MySingalton.convertString(mapdata.get("holdDays")));
        txtHoldingOverdue.setText(MySingalton.convertString(mapdata.get("holdingExcited")));


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rvBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}