package com.track.cylinderdelivery.ui.Reconciliation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.track.cylinderdelivery.R;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.LinkedList;
import java.util.List;

public class ReconFilterActivity extends AppCompatActivity {
    Context context;
    RelativeLayout rvBlank;
    Button btnCancel,btnApply;
    NiceSpinner spinnerCompany;
    List<String> companyDataSet;
    int indexCompanySelected=0;
    private int CompanyPosition=0;
    private SharedPreferences spPurchaseOrderFilter;
    String selectedtext="ALL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconfilter);
        context=this;
        selectedtext=getIntent().getStringExtra("selectedtext");
        rvBlank=findViewById(R.id.rvBlank);
        btnCancel=findViewById(R.id.btnCancel);
        btnApply=findViewById(R.id.btnApply);
        spPurchaseOrderFilter=context.getSharedPreferences("ReconFilter",MODE_PRIVATE);
        spinnerCompany = (NiceSpinner) findViewById(R.id.spinnerCompany);
        companyDataSet = new LinkedList<>();
        companyDataSet.add("ALL");
        companyDataSet.add("Reconcilate");
        companyDataSet.add("NotReconcilate");
        //companyDataSet.add("Completed");
        spinnerCompany.attachDataSource(companyDataSet);
        for(int i=0;i<companyDataSet.size();i++){
            if(companyDataSet.get(i).equals(selectedtext)){
                CompanyPosition=i;
                break;
            }
        }
        spinnerCompany.setSelectedIndex(CompanyPosition);

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
                SharedPreferences.Editor userFilterEditor = spPurchaseOrderFilter.edit();
                userFilterEditor.putBoolean("refilter",true);
                userFilterEditor.putInt("findex",CompanyPosition);
                userFilterEditor.putString("ftext",companyDataSet.get(CompanyPosition)+"");
                userFilterEditor.commit();
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor userFilterEditor = spPurchaseOrderFilter.edit();
                userFilterEditor.putBoolean("refilter",false);
                userFilterEditor.commit();
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