package com.track.cylinderdelivery.ui.cylinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;

import java.util.HashMap;

public class CylinderDetailActivity extends AppCompatActivity {

    Context context;
    private HashMap<String, String> mapdata;
    TextView txtCylinderNo,txtManufactringDate,txtManufactCompany,txtvalveCompanyName,txtCreatedDate;
    TextView txtPurchaseDate,txtExpireDate,txtPaintExpireDays,txtTestingPeriodDays,txtCreatedby;
    RelativeLayout rvBlank;
    ImageView btnCancel,btnEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cylinder_detail);
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("editData");
        context=this;
        txtCylinderNo=findViewById(R.id.txtCylinderNo);
        txtManufactringDate=findViewById(R.id.txtManufactringDate);
        txtManufactCompany=findViewById(R.id.txtManufactCompany);
        txtvalveCompanyName=findViewById(R.id.txtvalveCompanyName);
        txtPurchaseDate=findViewById(R.id.txtPurchaseDate);
        txtExpireDate=findViewById(R.id.txtExpireDate);
        txtPaintExpireDays=findViewById(R.id.txtPaintExpireDays);
        txtTestingPeriodDays=findViewById(R.id.txtTestingPeriodDays);
        txtCreatedby=findViewById(R.id.txtCreatedby);
        txtCreatedDate=findViewById(R.id.txtCreatedDate);
        rvBlank=findViewById(R.id.rvBlank);
        btnCancel=findViewById(R.id.btnCancel);
        btnEdit=findViewById(R.id.btnEdit);

        txtCreatedDate.setText(MySingalton.convertString(mapdata.get("createdDateStr")));
        txtCreatedby.setText(MySingalton.convertString(mapdata.get("createdByName")));
        txtTestingPeriodDays.setText(MySingalton.convertString(mapdata.get("testingPeriodDays")));
        txtPaintExpireDays.setText(MySingalton.convertString(mapdata.get("paintExpireDays")));
        txtExpireDate.setText(MySingalton.convertString(mapdata.get("expireDateStr")));
        txtPurchaseDate.setText(MySingalton.convertString(mapdata.get("purchaseDateStr")));
        txtvalveCompanyName.setText(MySingalton.convertString(mapdata.get("valveCompanyName")));
        txtManufactCompany.setText(MySingalton.convertString(mapdata.get("companyName"))+
                ","+MySingalton.convertString(mapdata.get("address1"))+
                ","+MySingalton.convertString(mapdata.get("address2"))+
                ","+MySingalton.convertString(mapdata.get("city"))+
                ","+MySingalton.convertString(mapdata.get("county"))+
                ","+MySingalton.convertString(mapdata.get("zipCode")));
        txtManufactringDate.setText(MySingalton.convertString(mapdata.get("manufacturingDateStr")));
        txtCylinderNo.setText(MySingalton.convertString(mapdata.get("cylinderNo")));

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,EditCylinderActivity.class);
                intent.putExtra("editData",mapdata);
                startActivity(intent);
                finish();
            }
        });
        rvBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}