package com.track.cylinderdelivery.utils;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MarketPlaceApiInterface {
    @Multipart
    @POST("UploadSalesImage")
    Call<ResponseBody> UpdateMarketPlaceProducts
            (@Part List<MultipartBody.Part> file);

}
