package com.track.cylinderdelivery.ui.cylinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.track.cylinderdelivery.R;

import java.util.ArrayList;

public class CylinderScanListActivity extends AppCompatActivity {

    TextView textView;
    ArrayList<String> scanlist;
    String datatoShow;
    Context context;
    RecyclerView recyclerView;
    Button btnAddQR,btnCancel,btnSubmit;
    EditText edtQrCode;
    CylinderScanedListAdapter cylinderScanedListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cylinder_scan_list);
        scanlist= (ArrayList<String>) getIntent().getSerializableExtra("scanlist");
        context=this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Scanned Cylinder List");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Scanned Cylinder List</font>"));
        textView=(TextView)findViewById(R.id.textView);
        recyclerView=(RecyclerView)findViewById(R.id.rv_qr_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CylinderScanListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        btnAddQR=findViewById(R.id.btnAddQR);
        edtQrCode=findViewById(R.id.edtQrCode);
        btnCancel=findViewById(R.id.btnCancel);
        btnSubmit=findViewById(R.id.btnSubmit);

       // textView.setText(scanlist.toString()+"");
        /*cylinderScanedListAdapter=new CylinderScanedListAdapter(scanlist,this);
        recyclerView.setAdapter(cylinderScanedListAdapter);*/

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("scanlist",scanlist);
                setResult(200,intent);
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnAddQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate(edtQrCode.getText().toString())){
                    for(int i=0;i<scanlist.size();i++){
                        if(edtQrCode.getText().toString().equals(scanlist.get(i))){
                            Toast.makeText(context, "QR Code already exist!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    scanlist.add(edtQrCode.getText().toString());
                    cylinderScanedListAdapter.notifyDataSetChanged();
                    edtQrCode.setText("");
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public boolean validate(String qrcode) {
        boolean valid = true;
        if (qrcode.isEmpty()) {
            edtQrCode.setError("Field is Required.");
            valid = false;
        } else {
            edtQrCode.setError(null);
        }
        return valid;
    }
}