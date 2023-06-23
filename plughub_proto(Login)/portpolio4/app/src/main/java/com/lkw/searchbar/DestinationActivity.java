package com.lkw.searchbar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lkw.searchbar.adapter.LocationAdapter;
import com.lkw.searchbar.api.keyword.AddrSearchService;
import com.lkw.searchbar.api.retronet.RetrofitNet;
import com.lkw.searchbar.key.Const;
import com.lkw.searchbar.model.category_search.CategoryResult;
import com.lkw.searchbar.model.category_search.Document;
import com.lkw.searchbar.model.mapdto.AdressDto;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DestinationActivity extends AppCompatActivity {
    private EditText searchView;
    private RecyclerView addressRecyclerView;
    private LocationAdapter locationAdapter;
    private AddrSearchService addrSearchService;

//    ArrayList<Document> bigMartList = new ArrayList<>(); //대형마트 MT1
//    ArrayList<Document> gs24List = new ArrayList<>(); //편의점 CS2
//    ArrayList<Document> schoolList = new ArrayList<>(); //학교 SC4
//    ArrayList<Document> academyList = new ArrayList<>(); //학원 AC5
//    ArrayList<Document> subwayList = new ArrayList<>(); //지하철 SW8
//    ArrayList<Document> bankList = new ArrayList<>(); //은행 BK9
//    ArrayList<Document> hospitalList = new ArrayList<>(); //병원 HP8
//    ArrayList<Document> pharmacyList = new ArrayList<>(); //약국 PM9
//    ArrayList<Document> cafeList = new ArrayList<>(); //카페

    ArrayList<Document> addressData = new ArrayList<>(); //지역명 검색 결과 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination); // 뷰를 구현
        initView();
    }
    private void initView(){
        searchView = findViewById(R.id.searchbar);
        addressRecyclerView = findViewById(R.id.result_search);

        LocationAdapter locationAdapter = new LocationAdapter(
                addressData,
                getApplicationContext(),
                searchView,
                addressRecyclerView
        );
        // 리사이클러 뷰 안에 있는 아이템을 어떻게 배치할지 결정
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // 아래 구분선 셋팅
        addressRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        // 레이아웃 매니저를 리사이클러 뷰 안에 초기화
        addressRecyclerView.setLayoutManager(layoutManager);

        // 리사이클러 뷰 안에 어댑터를 설정
        addressRecyclerView.setAdapter(locationAdapter);
        locationAdapter.setItemClickListener(new LocationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int index, Document document) {
                Intent intent = new Intent();
                intent.putExtra("index", index);
                intent.putExtra("document", document);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        // 검색 텍스쳐 이벤트
        //텍스트 입력 위젯의 텍스트 변경 이벤트를 모니터링하는 인터페이스 ( TextWatcher )
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 입력하기 전에
                addressRecyclerView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= 1) {
                    // if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {

                    addressData.clear();
                    locationAdapter.clear();
                    locationAdapter.notifyDataSetChanged();// 새로운 데이터를 가지고와서 화면에 반영한다.
                    AddrSearchService apiInterface = RetrofitNet.getApiClient().create(AddrSearchService.class);

                    Call<CategoryResult> call = apiInterface.getSearchLocation("KakaoAK " + Const.KAKAO_MAP_REST_API_APP_KEY,charSequence.toString(),15);
                    call.enqueue(new Callback<CategoryResult>() {
                        @Override
                        public void onResponse(@NotNull Call<CategoryResult> call, @NotNull Response<CategoryResult> response) {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                for (Document document : response.body().getDocuments()) {
                                    locationAdapter.addItem(document);
                                }
                                locationAdapter.notifyDataSetChanged();
                            }else {
                                // API 호출이 실패한 경우
                                Toast.makeText(getApplicationContext(), "API 호출 실패", Toast.LENGTH_SHORT).show();
                                Log.e("API Error", "API 호출 실패: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<CategoryResult> call, @NotNull Throwable t) {
                            // 네트워크 오류 또는 예외 발생
                            Toast.makeText(getApplicationContext(), "API 호출 실패", Toast.LENGTH_SHORT).show();
                            Log.e("API Error", "API 호출 실패: " + t.getMessage());
                        }
                    });
                    //}
                    //mLastClickTime = SystemClock.elapsedRealtime();
                } else {
                    if (charSequence.length() == 0) {
                        addressRecyclerView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 입력이 끝났을 때
            }
        });
    }
}

