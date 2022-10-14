package com.track.cylinderdelivery.ui.dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;

import java.util.HashMap;

public class Detial3Activity extends AppCompatActivity {
    private HashMap<String, String> mapdata;
    private ImageView btnCancel;
    private TextView txtUserName,txtRonumber,txtDNNumber,txtHoledDate,txtHoldDays1,txtHoldingOverdue1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detial3);
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("editData");
        btnCancel=findViewById(R.id.btnCancel);
        txtUserName=findViewById(R.id.txtUserName);
        txtRonumber=findViewById(R.id.txtRonumber);
        txtDNNumber=findViewById(R.id.txtDNNumber);
        txtHoledDate=findViewById(R.id.txtHoledDate);
        txtHoldDays1=findViewById(R.id.txtHoldDays1);
        txtHoldingOverdue1=findViewById(R.id.txtHoldingOverdue1);

        txtUserName.setText(MySingalton.convertString(mapdata.get("cylinderNo")));
        txtRonumber.setText(MySingalton.convertString(mapdata.get("soNumber")));
        txtDNNumber.setText(MySingalton.convertString(mapdata.get("strDeliverDate")));
        txtHoledDate.setText(MySingalton.convertString(mapdata.get("strHoldingCreditLastDate")));
        txtHoldDays1.setText(MySingalton.convertString(mapdata.get("holdDays")));
        txtHoldingOverdue1.setText(MySingalton.convertString(mapdata.get("holdingExcited")));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}