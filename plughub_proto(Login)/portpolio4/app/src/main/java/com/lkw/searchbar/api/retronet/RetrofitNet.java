package com.lkw.searchbar.api.retronet;

import com.lkw.searchbar.key.Const;
import com.lkw.searchbar.api.keyword.AddrSearchService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitNet {
    private static Retrofit retrofit;

    public static Retrofit getApiClient(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(Const.RETROFIT_KAKAO_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return  retrofit;
    }

}
