package com.lkw.searchbar.retrofit_DB;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.lkw.searchbar.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class db_Act extends AppCompatActivity {

    TextView textView;
    Call<List<data_model>> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        textView = findViewById(R.id.txt_view);

        // Retrofit 객체 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:7852") //안드로이드 에뮬레이터로 접근 하기위한 특수 주소 (network_security_config 에 추가 완료.)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Retrofit 인터페이스 생성
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        // 데이터베이스 에서 데이터 조회
        call = retrofitInterface.getDatabaseItems();
        call.enqueue(new Callback<List<data_model>>() {
            @Override
            public void onResponse(Call<List<data_model>> call, Response<List<data_model>> response) {
                if (response.isSuccessful()) {
                    List<data_model> resultList = response.body();
                    if (resultList != null && !resultList.isEmpty()) {
                        data_model results = resultList.get(0);

                        // 데이터 처리
                        String str =
                            results.getDepartment() + "\n" + //본부
                            results.getBusinessUnit() + "\n" +  //사업소
                            results.getChargingStationName() + "\n" + //충전소 명

                            results.getCharger() + "\n" +    //충전기
                            results.getChargerID() + "\n" +  //충전기 ID
                            results.getAddress() + "\n" +    //주소

                            results.getChargerType() + "\n" + //충전기 구분
                            results.getCapacity() + "\n" + //충전 용량
                            results.getChargedAmount() + "\n" + //충전량

                            results.getChargingTime() + "\n" + //충전 시간
                            results.getChargingMinute() + "\n" +  //충전 분

                            results.getChargingEndDate() + "\n" +  //충전 종료일자
                            results.getChargingStartTime() + "\n" +  //충전 시작시간
                            results.getChargingEndTime();   //충전 종료시각

                        textView.setText(str);
                    }
                } else {
                    // 서버 응답 실패
                    Log.e("Retrofit", "Server response error");
                }
            }

            @Override
            public void onFailure(Call<List<data_model>> call, Throwable t) {
                // 통신 실패
                Log.e("Retrofit", "Communication failure: " + t.getMessage());
            }
        });
    }
}