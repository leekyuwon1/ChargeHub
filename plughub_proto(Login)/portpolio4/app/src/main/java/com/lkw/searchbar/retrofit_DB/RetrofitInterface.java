package com.lkw.searchbar.retrofit_DB;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitInterface {
    @GET("/plug_data")
    Call<List<data_model>> getDatabaseItems();
}

