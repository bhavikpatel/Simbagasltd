package com.track.cylinderdelivery.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.track.cylinderdelivery.MySingalton;
import com.track.cylinderdelivery.R;
import com.williamww.silkysignature.views.SignaturePad;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignatureActivity extends AppCompatActivity {

    private SignaturePad mSilkySignaturePad;
    private Button mClearButton;
    private Button mSaveButton,btnDone;
    Context context;

    int SOId=0;
    ImageView imageView2;
    private static final int MY_SOCKET_TIMEOUT_MS = 100000;
    private String imgUrl="";
    boolean flgSigned=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        context=this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow =  ContextCompat.getDrawable(context, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(context, R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle("Customer Signature");
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#734CEA'>Customer Signature</font>"));
        SOId=getIntent().getIntExtra("SOId",0);
        mClearButton = findViewById(R.id.clear_button);
        mSaveButton = findViewById(R.id.save_button);
        imageView2=findViewById(R.id.imageView2);
        btnDone=findViewById(R.id.btnDone);

        mSilkySignaturePad = findViewById(R.id.signature_pad);

        mSilkySignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
               // Toast.makeText(SignatureActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
                flgSigned=true;
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
                flgSigned=true;
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
                flgSigned=false;
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("scanlist","Done");
                setResult(222,intent);
                finish();
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flgSigned) {
                    Bitmap signatureBitmap = mSilkySignaturePad.getSignatureBitmap();
                    if (signatureBitmap != null) {
                        uploadimage(signatureBitmap);
                    }else {
                        MySingalton.showOkDilog(context,"Kindly Sign on signature pad","Signature Pad");
                    }
                }else {
                    MySingalton.showOkDilog(context,"Kindly Sign on signature pad","Signature Pad");
                }
            }
        });
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSilkySignaturePad.clear();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void uploadimage(Bitmap signatureBitmap) {
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(context, R.drawable.loader);
        progressDialog.show();
        MarketPlaceApiInterface apiService = Apiclient.getClient().create(MarketPlaceApiInterface.class);
        File file = new File(getCacheDir().getPath() +"sigimg"+SOId+".jpg");
        try {
            OutputStream fOut = new FileOutputStream(file);
            signatureBitmap.compress(Bitmap.CompressFormat.JPEG,100,fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        Log.d("requestbody==>",requestFile+"");
//        RequestBody requestFile = RequestBody.create(MediaType.parse("text/plain"), file);
        MultipartBody.Part BusinessImage = MultipartBody.Part.
                createFormData("files", file.getName(), requestFile);
        apiService.UpdateMarketPlaceProducts(Collections.singletonList(BusinessImage))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        JSONObject j;
                        try {
                            imageView2.setImageBitmap(signatureBitmap);
                            j = new JSONObject(response.body().string()+"");
                             imgUrl=j.getString("data");
                            Log.d("onResponse==>", "" + imgUrl);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                        if(imgUrl!=null && imgUrl.length()!=0) {
                            Intent intent = new Intent();
                            intent.putExtra("imgUrl", imgUrl);
                            setResult(222, intent);
                            finish();
                        }else {
                            MySingalton.showOkDilog(context,"Signatue upload faile! Try again!","Signature Pad");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        imgUrl="";
                        Log.d("onFailure", "onResponse: " + t.getMessage());
                        progressDialog.dismiss();
                        MySingalton.showOkDilog(context,"Signatue upload faile! Try again!","Signature Pad");
                        /*Intent intent=new Intent();
                        intent.putExtra("scanlist","Error");
                        setResult(222,intent);
                        finish();*/
                    }
                });
    }
}