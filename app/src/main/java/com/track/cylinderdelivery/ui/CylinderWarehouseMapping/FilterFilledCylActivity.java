package com.track.cylinderdelivery.ui.CylinderWarehouseMapping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.track.cylinderdelivery.R;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FilterFilledCylActivity extends AppCompatActivity {
    Context context;
    RelativeLayout rvBlank;
    RadioGroup userRadioGroup,companyRadioGroup;
    Button btnCancel,btnApply;
    private SharedPreferences spCompanyFilter;
    NiceSpinner spinnerCompany;
    List<String> companyDataSet;
    int indexCompanySelected=0;
    private int CompanyPosition;
    private int userPosition;
    ArrayList<HashMap<String,String>> wharehouselist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_filled_cyl);
        context=this;
        wharehouselist= (ArrayList<HashMap<String,String>>) getIntent().getSerializableExtra("wharehouselist");
        rvBlank=(RelativeLayout)findViewById(R.id.rvBlank);
        userRadioGroup=findViewById(R.id.userRadioGroup);
        companyRadioGroup=findViewById(R.id.companyRadioGroup);
        btnCancel=findViewById(R.id.btnCancel);
        btnApply=findViewById(R.id.btnApply);
        spCompanyFilter=context.getSharedPreferences("filledCylFilter",MODE_PRIVATE);
        spinnerCompany = findViewById(R.id.spinnerCompany);
        companyDataSet = new LinkedList<>();
        companyDataSet.add("Select");
        for(int i=0;i<wharehouselist.size();i++){
            companyDataSet.add(wharehouselist.get(i).get("name"));
            if(wharehouselist.get(i).get("name").trim().equals(spCompanyFilter.getString("text","").trim())){
                indexCompanySelected=i;
            }
        }
        spinnerCompany.attachDataSource(companyDataSet);
        spinnerCompany.setSelectedIndex(indexCompanySelected);
        spinnerCompany.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Log.d("checkedId==>",position+"");
                CompanyPosition=position;
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor companyFilterEditor = spCompanyFilter.edit();
                companyFilterEditor.putBoolean("dofilter",true);
                companyFilterEditor.putInt("warhousepos",CompanyPosition);
                companyFilterEditor.putString("warhousename",companyDataSet.get(CompanyPosition));
                companyFilterEditor.putInt("warehouseId", Integer.parseInt(wharehouselist.get(CompanyPosition-1).get("warehouseId")));
                companyFilterEditor.commit();
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
                SharedPreferences.Editor companyFilterEditor = spCompanyFilter.edit();
                companyFilterEditor.putBoolean("dofilter",false);
                companyFilterEditor.commit();
                finish();
            }
        });
    }
}