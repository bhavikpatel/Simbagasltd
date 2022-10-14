package com.track.cylinderdelivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class ContinuousCaptureActivity extends AppCompatActivity {

        private DecoratedBarcodeView barcodeView;
        private BeepManager beepManager;
        private String lastText;
        private ArrayList<String> qcodeList;
        private TextView txtCount;
        private Context context;

       private BarcodeCallback callback = new BarcodeCallback() {
           @Override
           public void barcodeResult(BarcodeResult result) {
               if(result.getText() == null || result.getText().equals(lastText)) {
                   // Prevent duplicate scans
                   return;
               }

               lastText = result.getText();
               qcodeList.add(lastText);
               txtCount.setText(qcodeList.size()+"");
               barcodeView.setStatusText(result.getText());

               beepManager.playBeepSoundAndVibrate();

               //Added preview of scanned barcode
               ImageView imageView = findViewById(R.id.barcodePreview);
               imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
           }

           @Override
           public void possibleResultPoints(List<ResultPoint> resultPoints) {

           }
       };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continuous_capture);
        context=this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("QR Code Scanner");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>QR Code Scanner</font>"));
        qcodeList=new ArrayList<>();
        barcodeView = findViewById(R.id.barcode_scanner);
        txtCount=(TextView)findViewById(R.id.txtCount);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);
        beepManager = new BeepManager(this);
        txtCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ScanItemListAcitivity.class);
                intent.putExtra("scanlist",qcodeList);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }
    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}