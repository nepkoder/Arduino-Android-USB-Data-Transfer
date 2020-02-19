package com.fazlagida.burak.smartscaleusb.Api;

import com.fazlagida.burak.smartscaleusb.model.Barcode;
import com.fazlagida.burak.smartscaleusb.model.Flavour;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by newarbhai on 7/3/18.
 */

public interface ApiService {

    @POST("barcodeCheck")
    @FormUrlEncoded
    Call<Barcode> barcode(@Field("barCode") String barcode);

    @POST("getFlavour")
    Call<List<Flavour>> flavour();

}
