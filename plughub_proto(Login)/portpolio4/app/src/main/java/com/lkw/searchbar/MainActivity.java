package com.lkw.searchbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.kakao.sdk.common.KakaoSdk;
import com.lkw.searchbar.bottomsheet.CustomCalloutBalloonAdapter;
import com.lkw.searchbar.model.category_search.Document;
import com.lkw.searchbar.utils.BusProvider;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

// 컴포넌트, parcelable
public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, MapView.CurrentLocationEventListener, MapView.MapViewEventListener {
    private boolean isCheckboxVisible = false;
    private boolean isUsingTrueChecked = false;
    private boolean isUsingFalseChecked = false;
    private boolean isSpeedFast = false;
    private boolean isSpeedFull = false;
    private boolean ischargeTypeDc = false;
    private boolean ischargeTypeAc = false;
    private boolean ischargeTypecomb = false;
    private boolean ischargeTypeAcFull = false;


    private boolean isTrackingMode = false;
    private double mCurrentLng;
    private double mCurrentLat;
    private MapPoint currentMapPoint;
    private double mSearchLng = -1;
    private double mSearchLat = -1;
    private String mSearchName;
    MapPOIItem searchMarker = new MapPOIItem();
    Bus bus = BusProvider.getInstance();
//    private TextView searchText;
    private EditText searchText;


    private PopupWindow popupWindow;
    private CheckBox checkBoxPos;
    private CheckBox checkBoxImps;
    private CheckBox speedBoxPos;
    private CheckBox speedBoxImps;
    private CheckBox chargeTypeDc;
    private CheckBox chargeTypeAc;
    private CheckBox chargeTypecomb;
    private CheckBox chargeTypeAcFull;
    private MapView mapView;
    private ViewGroup mapViewContainer;
    public Activity activity;
    // ------------ Bottom Sheet ------------
    private LinearLayout bottomSheet;

    private View bottomSheetView;
    //
    private TextView address;
    private TextView csNm;
    private TextView cpTp;
    private TextView chargeTp;
    private TextView spStat;
    private TextView statUpdateDatetime;
    private BottomSheetBehavior bottomSheetBehavior;
    //------------------------


    public void setStatusBarTransparent(Activity activity) {
        Window window = activity.getWindow();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        if (Build.VERSION.SDK_INT >= 30) { // API 30에 적용
            WindowCompat.setDecorFitsSystemWindows(window, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent(this);

        bus.register(this); // 정류소 등록

        setContentView(R.layout.search);
        findViewById(R.id.more_btn).setOnClickListener(this);

        searchText = findViewById(R.id.searchbar);

        int permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.INTERNET);

        int permission2 = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permission3 = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);

        // 권한이 열려있는지 확인
        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED || permission3 == PackageManager.PERMISSION_DENIED) {
            // 마쉬멜로우 이상버전부터 권한을 물어본다
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                requestPermissions(
                        new String[]{android.Manifest.permission.INTERNET, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        1000);
            }
            return;
        }

        // Kakao SDK 초기화
        KakaoSdk.init(this, "{058aba7c75990149427e6a9956137af0}");
        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);


        Chip using = findViewById(R.id.using_btn);
        using.setOnClickListener(this);

        Chip using1 = findViewById(R.id.type_btn);
        using1.setOnClickListener(this);

        Chip using2 = findViewById(R.id.speed_btn);
        using2.setOnClickListener(this);

        searchText.setOnClickListener(this);
        activity = this;
        // ------------------------
        bottomSheetView = findViewById(R.id.viewBottomSheet); // 바텀 시트의 레이아웃
        // 바텀 시트에서 텍스트를 설정할 TextView
        address = bottomSheetView.findViewById(R.id.address); // 주소 값 초기화
        csNm = bottomSheetView.findViewById(R.id.address_road);
        cpTp = bottomSheetView.findViewById(R.id.cpTp);
        chargeTp = bottomSheetView.findViewById(R.id.charge_val);
        spStat = bottomSheetView.findViewById(R.id.spStat);
        statUpdateDatetime = bottomSheetView.findViewById(R.id.Datetime);


        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter(
                bottomSheetBehavior,
                address, csNm, cpTp, chargeTp, spStat, statUpdateDatetime,
                mapView, MainActivity.this));

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // 바텀 시트 슬라이드 동작 감지
            }
        });
        // 화면 Click 시 바텀시트가 보였다가 다시 누르면 사라지기
//        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED && bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                } else {
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                }
//            }
//        });
        // 바텀시트가 최소 크기일때 누르면 최대 크기로 확장하기
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        // ------------------------
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.more_btn:
                showCheckBox();
                break;
            case R.id.searchbar:
                startDestination();
                break;
            case R.id.using_btn: // 이용시간
                viewOnClickUsing(v);
                break;
            case R.id.type_btn: //  충전 타입
                viewOnClickType(v);
                break;
            case R.id.speed_btn: // 충전 속도
                viewOnClickSpeed(v);
                break;
            default:
                hidecheckbox();
                break;

        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        startDestination();
    }


    //활동 결과 가져오기
    // ActivityResultLauncher 는 ActivityResultContracts를 실행 프로세스를 시작하기 위해 미리 설정해놔야 하는 추상 클래스임
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
    // getResultCode() 에서 Result_OK 가져와졌다면
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent review = result.getData();
            if(review != null){
                Document document = review.getParcelableExtra("document");
                searchText.setText(document.getPlaceName());
                address.setText(document.getPlaceName());

            }
        }
    });
    void startDestination() {
        // launcher를 이용해서 화면 시작하기

        if(!searchText.equals("")){  //텍스트가 존재할 시
            searchText.getText().clear();
        }

        Intent intent = new Intent(this, DestinationActivity.class);
        launcher.launch(intent);

        //단방향
//        Intent intent = new Intent(this, DestinationActivity.class);
//        startActivity(intent);
    }


    private void showCheckBox() {
        Chip speedBtn = findViewById(R.id.speed_btn);
        Chip typeBtn = findViewById(R.id.type_btn);
        Chip usingBtn = findViewById(R.id.using_btn);

        isCheckboxVisible = !isCheckboxVisible;
        speedBtn.setVisibility(isCheckboxVisible ? View.VISIBLE : View.GONE);
        typeBtn.setVisibility(isCheckboxVisible ? View.VISIBLE : View.GONE);
        usingBtn.setVisibility(isCheckboxVisible ? View.VISIBLE : View.GONE);

    }

    private void hidecheckbox() {
        if (isCheckboxVisible) {
            showCheckBox();
        }
    }


    private void viewOnClickUsing(View v) {
        Chip chip = (Chip) v;
        HorizontalScrollView scrollView = findViewById(R.id.scroll_view);
        int maxScrollX = scrollView.getChildAt(0).getWidth() - scrollView.getWidth();
        scrollView.scrollTo(-maxScrollX, 0);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.using_checkmenu, null);
        popupWindow = new PopupWindow(popupView, 230, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);

        checkBoxPos = popupView.findViewById(R.id.using_true);
        checkBoxImps = popupView.findViewById(R.id.using_false);

        // 체크박스 상태 설정
        checkBoxPos.setChecked(isUsingTrueChecked);
        checkBoxImps.setChecked(isUsingFalseChecked);

        // 체크박스 클릭 리스너 설정
        checkBoxPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUsingTrueChecked = checkBoxPos.isChecked();
                updateUsingChip(chip);
            }
        });

        checkBoxImps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUsingFalseChecked = checkBoxImps.isChecked();
                updateUsingChip(chip);
            }
        });

        // 초기 상태 설정
        updateUsingChip(chip);

        popupWindow.showAsDropDown(v);
    }

    // Chip 뷰의 상태를 업데이트하는 메서드
    private void updateUsingChip(@NonNull Chip chip) {
        boolean isAnyChecked = isUsingTrueChecked || isUsingFalseChecked;
        chip.setChecked(isAnyChecked);
    }


    private void viewOnClickType(View v) {
        Chip chip = (Chip) v;
        HorizontalScrollView scrollView = findViewById(R.id.scroll_view);
        int maxScrollX = scrollView.getChildAt(0).getWidth() - scrollView.getWidth();
        int centerX = maxScrollX / 2;
        scrollView.scrollTo(-centerX, 0);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.using_typemenu, null);

        // 지도 띄울 시, 지도 텍스트와 겹쳐지는 현상을 대응
        popupWindow = new PopupWindow(popupView, 270, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);


        chargeTypeDc= popupView.findViewById(R.id.speed_dc);
        chargeTypeAc= popupView.findViewById(R.id.speed_ac);
        chargeTypecomb = popupView.findViewById(R.id.speed_dccomb);
        chargeTypeAcFull = popupView.findViewById(R.id.ac_full);

        chargeTypeDc.setChecked(ischargeTypeDc);
        chargeTypeAc.setChecked(ischargeTypeAc);
        chargeTypecomb.setChecked(ischargeTypecomb);
        chargeTypeAcFull.setChecked(ischargeTypeAcFull);

        chargeTypeDc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ischargeTypeDc = chargeTypeDc.isChecked();
                updateTypeChip(chip);
            }
        });
        chargeTypeAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ischargeTypeAc = chargeTypeAc.isChecked();
                updateTypeChip(chip);
            }
        });
        chargeTypecomb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ischargeTypecomb = chargeTypecomb.isChecked();
                updateTypeChip(chip);
            }
        });
        chargeTypeAcFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ischargeTypeAcFull = chargeTypeAcFull.isChecked();
                updateTypeChip(chip);
            }
        });

        updateTypeChip(chip);
        popupWindow.showAsDropDown(v);
    }

    private void updateTypeChip(@NonNull Chip chip) {
        boolean isAnyChecked = chargeTypeDc.isChecked()
                || chargeTypeAc.isChecked()
                || chargeTypecomb.isChecked()
                || chargeTypeAcFull.isChecked();
        chip.setChecked(isAnyChecked);
    }

    private void viewOnClickSpeed(View v) {
        Chip chip = (Chip) v;
        HorizontalScrollView scrollView = findViewById(R.id.scroll_view);
        int maxScrollX = scrollView.getChildAt(0).getWidth() - scrollView.getWidth();
        scrollView.scrollTo(maxScrollX, 0);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.using_speed, null);
        popupWindow = new PopupWindow(popupView, 170, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);

        speedBoxPos = popupView.findViewById(R.id.char_full);
        speedBoxImps = popupView.findViewById(R.id.char_speed);

        speedBoxPos.setChecked(isSpeedFast);
        speedBoxImps.setChecked(isSpeedFull);

        speedBoxPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSpeedFast = speedBoxPos.isChecked();
                updateSpeedChip(chip);
            }
        });

        speedBoxImps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSpeedFull = speedBoxImps.isChecked();
                updateSpeedChip(chip);
            }
        });
        updateSpeedChip(chip);
        int offsetX = 15;
        int offsetY = 0;

        popupWindow.showAsDropDown(v,offsetX,offsetY);
    }

    private void updateSpeedChip(@NonNull Chip chip) {
        boolean isAnyChecked = isSpeedFast || isSpeedFull;
        chip.setChecked(isAnyChecked);
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();

        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
        //이 좌표로 지도 중심 이동
        mapView.setMapCenterPoint(currentMapPoint, true);
        //전역변수로 현재 좌표 저장
        mCurrentLat = mapPointGeo.latitude;
        mCurrentLng = mapPointGeo.longitude;
        //트래킹 모드가 아닌 단순 현재위치 업데이트일 경우, 한번만 위치 업데이트하고 트래킹을 중단시키기 위한 로직
        if (!isTrackingMode) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        }
    }

    @Subscribe //검색예시 클릭시 이벤트 오토버스
    public void search(Document document) {//public항상 붙여줘야함
        mSearchName = document.getPlaceName();
        mSearchLng = Double.parseDouble(document.getX());
        mSearchLat = Double.parseDouble(document.getY());
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mSearchLat, mSearchLng), true);
        mapView.removePOIItem(searchMarker);
        searchMarker.setItemName(mSearchName);
        searchMarker.setTag(10000);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(mSearchLat, mSearchLng);
        searchMarker.setMapPoint(mapPoint);
        searchMarker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        searchMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        //마커 드래그 가능하게 설정
        searchMarker.setDraggable(false);
        mapView.addPOIItem(searchMarker);
    }

    @Override
    public void finish() {
        super.finish();
        bus.unregister(this); //이액티비티 떠나면 정류소 해제해줌
    }
    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mapView.setShowCurrentLocationMarker(false);
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else{
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}