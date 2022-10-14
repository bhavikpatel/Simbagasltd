package com.track.cylinderdelivery.ui.cylinderproductmapping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;

import java.util.HashMap;

public class DetailCylinderProductMapping extends AppCompatActivity {

    private HashMap<String, String> mapdata;
    RelativeLayout rvBlank;
    ImageView btnCancel,btnReport;
    TextView txtProductName,txtCylinderNo,txtUnit,txtQtyofGas,txtPurity;
    TextView txtCreatedBy,txtCreatedDate,txtGaugepressure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_cylinder_product_mapping);
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("displaydata");
        rvBlank=(RelativeLayout)findViewById(R.id.rvBlank);
        btnCancel=(ImageView) findViewById(R.id.btnCancel);
        btnReport=(ImageView)findViewById(R.id.btnReport);
        txtProductName=(TextView)findViewById(R.id.txtProductName);
        txtCylinderNo=(TextView)findViewById(R.id.txtCylinderNo);
        txtUnit=(TextView)findViewById(R.id.txtUnit);
        txtQtyofGas=(TextView)findViewById(R.id.txtQtyofGas);
        txtPurity=(TextView)findViewById(R.id.txtPurity);
        txtCreatedBy=(TextView)findViewById(R.id.txtCreatedBy);
        txtCreatedDate=(TextView)findViewById(R.id.txtCreatedDate);
        txtGaugepressure=findViewById(R.id.txtGaugepressure);

        txtProductName.setText(mapdata.get("productName"));
        txtCylinderNo.setText(mapdata.get("cylinderNo"));
        txtUnit.setText(mapdata.get("unit"));
        txtQtyofGas.setText(mapdata.get("quantity"));
        txtPurity.setText(mapdata.get("purity"));
        txtCreatedBy.setText(mapdata.get("createdByName"));
        txtCreatedDate.setText(mapdata.get("createdDateStr"));
        txtGaugepressure.setText(mapdata.get("gaugePressure"));

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(MySingalton.getInstance().URL+"/Api/MobCylinderProductMapping/GenerateCylinderReport?Id="+
                                mapdata.get("cylinderProductMappingId")));
                startActivity(browserIntent);
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