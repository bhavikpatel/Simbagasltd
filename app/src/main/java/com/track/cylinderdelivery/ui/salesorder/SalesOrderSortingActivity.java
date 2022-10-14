package com.track.cylinderdelivery.ui.salesorder;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.track.cylinderdelivery.R;

import java.util.ArrayList;

public class SalesOrderSortingActivity extends AppCompatActivity {
    Context context;
    RelativeLayout rvBlank;
    Button btnCancel,btnApply;
    RadioGroup userSortRadioGroup,userRadioGroupAscDsc;
    SharedPreferences spSorting;

    ArrayList<String> sortbyName;
    ArrayList<String> sortbyAscDsc;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_sorting);
        context=this;
        rvBlank=(RelativeLayout)findViewById(R.id.rvBlank);
        btnCancel=(Button)findViewById(R.id.btnCancel);
        btnApply=findViewById(R.id.btnApply);
        userSortRadioGroup=findViewById(R.id.companyRadioGroup);
        userRadioGroupAscDsc=findViewById(R.id.userRadioGroupAscDsc);
        spSorting=context.getSharedPreferences("SOFilter",MODE_PRIVATE);

        sortbyName=new ArrayList<>();
        sortbyName.add("Product Name");
        sortbyName.add("Quantity");
        for(int i=0;i<sortbyName.size();i++){
            RadioButton radioButton=new RadioButton(context);
            radioButton.setId(1+i);
            RadioGroup.LayoutParams rprms = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            radioButton.setText(sortbyName.get(i));
            radioButton.setTextColor(getResources().getColor(R.color.black));
            radioButton.setTextSize(getResources().getDimension(R.dimen.radiolbl));
            radioButton.setButtonTintList(getRadioButtonColors());
            userSortRadioGroup.addView(radioButton,i,rprms);
            if(sortbyName.get(i).equals(spSorting.getString("text1","").trim())){
                radioButton.setChecked(true);
            }
        }

        sortbyAscDsc=new ArrayList<>();
        sortbyAscDsc.add("Decinding");
        sortbyAscDsc.add("Ascending");
        for(int i=0;i<sortbyAscDsc.size();i++){
            RadioButton radioButton=new RadioButton(context);
            radioButton.setId(1+i);
            RadioGroup.LayoutParams rprms = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            radioButton.setText(sortbyAscDsc.get(i)+"");
            radioButton.setTextColor(getResources().getColor(R.color.black));
            radioButton.setTextSize(getResources().getDimension(R.dimen.radiolbl));
            radioButton.setButtonTintList(getRadioButtonColors());
            userRadioGroupAscDsc.addView(radioButton,i,rprms);
            if(sortbyAscDsc.get(i).equals(spSorting.getString("text2","Decinding").trim())){
                radioButton.setChecked(true);
            }
        }

        userSortRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("checkedId==>",checkedId+"");
                SharedPreferences.Editor editor = spSorting.edit();
                editor.putInt("index1",checkedId);
                editor.putString("text1",sortbyName.get(checkedId-1));
                editor.commit();
            }
        });
        userRadioGroupAscDsc.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("checkedId==>",checkedId+"");
                SharedPreferences.Editor editor = spSorting.edit();
                editor.putInt("index2",checkedId);
                editor.putString("text2",sortbyAscDsc.get(checkedId-1));
                editor.commit();
            }
        });
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = spSorting.edit();
                editor.putBoolean("sofilter",true);
                editor.commit();
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = spSorting.edit();
                editor.putBoolean("sofilter",false);
                editor.commit();
                finish();
            }
        });
        rvBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = spSorting.edit();
                editor.putBoolean("sofilter",false);
                editor.commit();
                finish();
            }
        });
    }
    private ColorStateList getRadioButtonColors() {
        return new ColorStateList (
                new int[][] {
                        new int[] {android.R.attr.state_checked}, // checked
                        new int[] {android.R.attr.state_enabled} // unchecked
                },
                new int[] {
                        Color.GREEN, // checked
                        Color.BLUE   // unchecked
                }
        );
    }
}