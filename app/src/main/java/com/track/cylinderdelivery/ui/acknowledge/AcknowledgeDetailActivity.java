package com.track.cylinderdelivery.ui.acknowledge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AcknowledgeDetailActivity extends AppCompatActivity {

    RelativeLayout rvBlank;
    Context context;
    ImageView btnCancel;
    private HashMap<String, String> mapdata;
    TextView txtUserName,txtAdminName,txtRemark,txtStatus;
    TextView txtAddress,txtPermonthReq,txtHoldingcreditday,txtPinNumber;
    TextView txtEmail,txtSecondaryEmail,txtMobile,txtSecondaryMobile,txtReferenceby;
    TextView txtDepositAmount;
    LinearLayout lvHoldingCapacity,lvAcknowledgeReamark;
    TextView txtHoldCap,txtCreatedby,txtUserStatus;
    TextView txtCreatedDate,txtCompanyCategory;
    LinearLayout lvAckBy,lvAckDate;
    ImageView btnAction;
    private SharedPreferences shpreRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledge_detail);
        context=this;
        mapdata= (HashMap<String, String>) getIntent().getSerializableExtra("editData");
        rvBlank=findViewById(R.id.rvBlank);
        btnCancel=findViewById(R.id.btnCancel);
        txtUserName=findViewById(R.id.txtUserName);
        txtAdminName=findViewById(R.id.txtAdminName);
        txtRemark=findViewById(R.id.txtRemark);
        txtStatus=findViewById(R.id.txtStatus);
        txtPinNumber=findViewById(R.id.txtPinNumber);
        txtSecondaryEmail=findViewById(R.id.txtSecondaryEmail);
        txtDepositAmount=findViewById(R.id.txtDepositAmount);
        txtReferenceby=findViewById(R.id.txtReferenceby);
        txtSecondaryMobile=findViewById(R.id.txtSecondaryMobile);
        txtEmail=findViewById(R.id.txtEmail);
        txtMobile=findViewById(R.id.txtMobile);
        txtHoldingcreditday=findViewById(R.id.txtHoldingcreditday);
        txtPermonthReq=findViewById(R.id.txtPermonthReq);
        txtAddress=findViewById(R.id.txtAddress);
        txtCompanyCategory=findViewById(R.id.txtCompanyCategory);
        lvHoldingCapacity=findViewById(R.id.lvHoldingCapacity);
        txtHoldCap=findViewById(R.id.txtHoldCap);
        txtUserStatus=findViewById(R.id.txtUserStatus);

        txtCreatedby=findViewById(R.id.txtCreatedby);
        txtCreatedDate=findViewById(R.id.txtCreatedDate);

        btnAction=findViewById(R.id.btnAction);
        shpreRefresh=context.getSharedPreferences("ackRefresh",MODE_PRIVATE);

        if(shpreRefresh.getInt("fromlist",0)==2){
            btnAction.setVisibility(View.GONE);
        }
        if(shpreRefresh.getInt("fromlist",0)==3){
            btnAction.setVisibility(View.GONE);
        }
        if(shpreRefresh.getInt("fromlist",0)==4){
            btnAction.setVisibility(View.VISIBLE);
        }
        txtUserName.setText(MySingalton.convertString(mapdata.get("userName")));
        txtAdminName.setText(MySingalton.convertString(mapdata.get("companyName")));
        txtRemark.setText(MySingalton.convertString(mapdata.get("remark")));
        txtStatus.setText(MySingalton.convertString(mapdata.get("status")));
        txtCreatedby.setText(MySingalton.convertString(mapdata.get("createdByName")));
        txtCreatedDate.setText(MySingalton.convertString(mapdata.get("createdDateStr")));

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(mapdata.get("userDetails"));
            txtCompanyCategory.setText(MySingalton.convertString(jsonObject.getString("companyCategory")));
            txtAddress.setText(MySingalton.convertString(jsonObject.getString("address1"))+","
                    +MySingalton.convertString(jsonObject.getString("address2"))+","+
                    MySingalton.convertString(jsonObject.getString("city"))+","+
                    MySingalton.convertString(jsonObject.getString("county")));
            txtPermonthReq.setText(MySingalton.convertString(jsonObject.getString("perMonthRequirement")));
            txtHoldingcreditday.setText(MySingalton.convertString(jsonObject.getString("cylinderHoldingCreditDays")));
            txtPinNumber.setText(MySingalton.convertString(jsonObject.getString("taxNumber")));
            txtEmail.setText(MySingalton.convertString(jsonObject.getString("email")));
            txtSecondaryEmail.setText(MySingalton.convertString(jsonObject.getString("secondaryEmail")));
            txtSecondaryMobile.setText(MySingalton.convertString(jsonObject.getString("secondaryPhone")));
            txtMobile.setText(MySingalton.convertString(jsonObject.getString("phone")));
            txtReferenceby.setText(MySingalton.convertString(jsonObject.getString("referenceBy")));
            txtDepositAmount.setText(MySingalton.convertString(jsonObject.getString("depositAmount")));
            txtHoldCap.setText(MySingalton.convertString(jsonObject.getString("holdingCapacity")));
            txtUserStatus.setText(MySingalton.convertString(jsonObject.getString("status")));
        }catch (JSONException err){
            Log.d("Error", err.toString());
        }

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ApproveAcknowledgeActivity.class);
                intent.putExtra("dataMap",mapdata);
                startActivity(intent);
                finish();
            }
        });

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