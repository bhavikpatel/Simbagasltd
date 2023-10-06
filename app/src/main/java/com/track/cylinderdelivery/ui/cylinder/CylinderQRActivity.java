package com.track.cylinderdelivery.ui.cylinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.track.cylinderdelivery.R;
import com.track.cylinderdelivery.ScanItemListAcitivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CylinderQRActivity extends AppCompatActivity {

    //private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    ArrayList<String> qcodeList;
    TextView txtCount,textView;
    private Context context;
    //private ImageView imgScanList;
    private Button btnSubmit,btnAddQR;
    private ImageView btnScanCylinders;
   // private TextView textView2;
    RecyclerView recyclerView;
    EditText edtQrCode;
    private String cylList="";
    CylinderScanedListAdapter cylinderScanedListAdapter;
/*    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }
            for(int i=0;i<qcodeList.size();i++){
                if(result.getText().equals(qcodeList.get(i))){
                    return;
                }
            }
            lastText = result.getText();
            qcodeList.add(lastText);
            txtCount.setText(qcodeList.size()+"");
            //barcodeView.setStatusText(result.getText());
            //barcodeView.setStatusText(qcodeList.toString()+"");
            beepManager.playBeepSoundAndVibrate();
            //Added preview of scanned barcode
           // ImageView imageView = findViewById(R.id.barcodePreview);
           // imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cylinder_q_r);
        context=this;
        btnScanCylinders=findViewById(R.id.btnScanCylinders);
        //textView2=findViewById(R.id.textView2);
        recyclerView=(RecyclerView)findViewById(R.id.rv_qr_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CylinderQRActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        qcodeList=new ArrayList<String>();
        qcodeList= (ArrayList<String>) getIntent().getSerializableExtra("scanlist");

        cylList=getIntent().getStringExtra("cylList");
        edtQrCode=findViewById(R.id.edtQrCode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Scan Cylinders");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Scan Cylinders</font>"));

        //barcodeView = findViewById(R.id.barcode_scanner);
        txtCount=(TextView)findViewById(R.id.txtCount);
        textView=(TextView)findViewById(R.id.textView);
        if(cylList==null){
            cylList="";
        }
        textView.setText("Add QR Code: "+cylList);
        btnSubmit=findViewById(R.id.btnSubmit);
        btnAddQR=findViewById(R.id.btnAddQR);
       // imgScanList=findViewById(R.id.imgScanList);
        //Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        /*Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.QR_CODE);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);*/
        beepManager = new BeepManager(this);

        cylinderScanedListAdapter=new CylinderScanedListAdapter(qcodeList,this);
        recyclerView.setAdapter(cylinderScanedListAdapter);

        if(qcodeList.size()!=0){
            txtCount.setText(qcodeList.size()+"");
           // barcodeView.setStatusText(qcodeList.toString()+"");
        }

        scanBarcode();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("scanlist",qcodeList);
                setResult(201,intent);
                finish();
            }
        });
        btnAddQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate(edtQrCode.getText().toString())){
                    for(int i=0;i<qcodeList.size();i++){
                        if(edtQrCode.getText().toString().equals(qcodeList.get(i))){
                            Toast.makeText(context, "QR Code already exist!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    qcodeList.add(edtQrCode.getText().toString());
                    cylinderScanedListAdapter.notifyDataSetChanged();
                    edtQrCode.setText("");
                    txtCount.setText(qcodeList.size()+"");
                }
            }
        });
/*        txtCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getScanedList();
            }
        });*/
/*        imgScanList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getScanedList();
            }
        });*/
        btnScanCylinders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode();
            }
        });
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
    private void scanBarcode() {
        GmsBarcodeScanner gmsBarcodeScanner = GmsBarcodeScanning.getClient(this);
        gmsBarcodeScanner
                .startScan()
                .addOnSuccessListener(
                        barcode -> txtCount.setText(getSuccessfulMessage(barcode)))
                .addOnFailureListener(
                        e -> txtCount.setText(getErrorMessage((MlKitException) e)));
    }

    private String getSuccessfulMessage(Barcode barcode) {
/*    String barcodeValue =
        String.format(
            Locale.US,
            "Display Value: %s\nRaw Value: %s\nFormat: %s\nValue Type: %s",
            barcode.getDisplayValue(),
            barcode.getRawValue(),
            barcode.getFormat(),
            barcode.getValueType());*/

        for(int i=0;i<qcodeList.size();i++){
            if(barcode.getDisplayValue().equals(qcodeList.get(i))){
                scanBarcode();
                return getString(R.string.barcode_result, qcodeList.toString());
            }
        }
        beepManager.playBeepSoundAndVibrate();
        qcodeList.add(barcode.getDisplayValue());
        scanBarcode();
        //return getString(R.string.barcode_result, barcodeValue);
        return qcodeList.toString();
    }
    @SuppressLint("SwitchIntDef")
    private String getErrorMessage(MlKitException e) {
        switch (e.getErrorCode()) {
            case MlKitException.CODE_SCANNER_CANCELLED:
                //return getString(R.string.error_scanner_cancelled);
                return qcodeList.toString();
            case MlKitException.CODE_SCANNER_CAMERA_PERMISSION_NOT_GRANTED:
                //return getString(R.string.error_camera_permission_not_granted);
                return qcodeList.toString();
            case MlKitException.CODE_SCANNER_APP_NAME_UNAVAILABLE:
                //return getString(R.string.error_app_name_unavailable);
                return qcodeList.toString();
            default:
                //return getString(R.string.error_default_message, e);
                return qcodeList.toString();
        }
    }
/*    private void getScanedList() {
        Intent intent=new Intent(context, CylinderScanListActivity.class);
        intent.putExtra("scanlist",qcodeList);
        startActivityForResult(intent,200);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        try {
            txtCount.setText(qcodeList.size() + "");
        }catch (Exception e){
            e.printStackTrace();
        }
        cylinderScanedListAdapter.notifyDataSetChanged();
       // barcodeView.resume();
    }
    @Override
    protected void onPause() {
        super.onPause();
       // barcodeView.pause();
    }
/*    public void pause(View view) {
        barcodeView.pause();
    }*/

    /*public void resume(View view) {
        barcodeView.resume();
    }*/

    /*public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }*/

  /*  @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }*/
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 200
        if(requestCode==200)
        {
            try {
                qcodeList = (ArrayList<String>) data.getSerializableExtra("scanlist");
                txtCount.setText(qcodeList.size() + "");
               // barcodeView.setStatusText(qcodeList.toString() + "");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }*/
}