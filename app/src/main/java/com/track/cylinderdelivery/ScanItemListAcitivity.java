package com.track.cylinderdelivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ScanItemListAcitivity extends AppCompatActivity {

    TextView textView;
    ArrayList<String> scanlist;
    String datatoShow;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_item_list_acitivity);
        scanlist= (ArrayList<String>) getIntent().getSerializableExtra("scanlist");
        context=this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Scanned List");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Scanned List</font>"));
        textView=(TextView)findViewById(R.id.textView);
        for(int i=0;i<scanlist.size();i++){
            datatoShow+=scanlist.get(i)+"\n";
        }
        textView.setText(datatoShow);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}