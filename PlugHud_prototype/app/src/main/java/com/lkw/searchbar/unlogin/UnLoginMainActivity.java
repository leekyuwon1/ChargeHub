package com.lkw.searchbar.unlogin;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.kakao.sdk.common.KakaoSdk;
import com.lkw.searchbar.R;
import com.lkw.searchbar.unlogin.bottomsheet.CustomCalloutBalloonAdapter;
import com.lkw.searchbar.unlogin.button_opt.ChargeButtonOption;
import com.lkw.searchbar.unlogin.button_opt.SpeedButtonOption;
import com.lkw.searchbar.unlogin.button_opt.UsingButtonOption;
import com.lkw.searchbar.unlogin.charge_controller.DatabaseCharge;
import com.lkw.searchbar.unlogin.model.category_search.Document;
import com.lkw.searchbar.unlogin.states.ChargeType;
import com.lkw.searchbar.unlogin.states.SpeedType;
import com.lkw.searchbar.unlogin.states.UsingType;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.Map;

// 컴포넌트, parcelable
public class UnLoginMainActivity extends AppCompatActivity implements
    View.OnClickListener,
    View.OnFocusChangeListener,
    MapView.CurrentLocationEventListener,
    MapView.MapViewEventListener,
    UsingButtonOption.Listener,
    SpeedButtonOption.SpeedListener,
    ChargeButtonOption.TypeListener {

    private MapView mapView;
    private int markerCount;
    // DataBase
    private DatabaseCharge databaseCharge;
    private boolean isCheckboxVisible = false;

    //ㅡㅡㅡㅡㅡㅡ
    private boolean isTrackingMode = false;
    private double mCurrentLng;
    private double mCurrentLat;
    private MapPoint currentMapPoint;
    private double mSearchLng = -1;
    private double mSearchLat = -1;
    private String mSearchName;
    MapPOIItem searchMarker;
    private EditText searchText;
    private Button currentBtn;
    private ViewGroup mapViewContainer;

    // ------------ Bottom Sheet ------------
    private LinearLayout bottomSheet;

    private View bottomSheetView;
    //
    private TextView address;
    private TextView csNm;
    private TextView cpTp;
    private TextView cpId;
    private TextView csId;
    private TextView cpNm;
    private TextView chargeTp;
    private TextView spStat;
    private TextView statUpdateDatetime;
    private BottomSheetBehavior bottomSheetBehavior;
    //------------------------
    private Map<Integer, String> info;
    private UsingButtonOption usingButtonOption;
    private SpeedButtonOption speedButtonOption;
    private ChargeButtonOption chargeButtonOption;
    private HorizontalScrollView scrollView;
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

        setContentView(R.layout.unlogin_search);

        findViewById(R.id.unlogin_more_btn).setOnClickListener(UnLoginMainActivity.this);

        searchText = findViewById(R.id.unlogin_searchbar);

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

        scrollView = findViewById(R.id.scroll_view);

        bottomSheetView = findViewById(R.id.viewBottomSheet);
        address = bottomSheetView.findViewById(R.id.address);
        csNm = bottomSheetView.findViewById(R.id.address_road);
        cpTp = bottomSheetView.findViewById(R.id.cpTp);
        chargeTp = bottomSheetView.findViewById(R.id.charge_val);
        spStat = bottomSheetView.findViewById(R.id.spStat);
        statUpdateDatetime = bottomSheetView.findViewById(R.id.Datetime);
        csId = bottomSheetView.findViewById(R.id.csi);
        cpId = bottomSheetView.findViewById(R.id.cpi);
        cpNm = bottomSheetView.findViewById(R.id.cpn);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // 바텀 시트 슬라이드 동작 감지
            }
        });

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

        KakaoSdk.init(this, "{058aba7c75990149427e6a9956137af0}");
        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode. TrackingModeOnWithoutHeading);
        mapView.setCalloutBalloonAdapter(
            new CustomCalloutBalloonAdapter(
                bottomSheetBehavior,
                address, csNm, cpTp, chargeTp, spStat, statUpdateDatetime,
                csId, cpId, cpNm,
                mapView, UnLoginMainActivity.this, info
            )
        );

        usingButtonOption = new UsingButtonOption(
            findViewById(R.id.unlogin_using_btn),
            scrollView,
            this
        );

        speedButtonOption = new SpeedButtonOption(
            findViewById(R.id.unlogin_speed_btn),
            scrollView,
            this,
            this
        );

        chargeButtonOption = new ChargeButtonOption(
            findViewById(R.id.unlogin_type_btn),
            scrollView,
            this,
            this
        );

        updateMarker();

        searchText.setOnClickListener(this);
    }

    @Override
    public void onUsingTypeChanged(UsingType using) {
        updateMarker();
    }

    @Override
    public void onSpeedTypeChanged(SpeedType speedType) {
        updateMarker();
    }

    @Override
    public void onChargeTypeChanged(ChargeType chargeType) {
        updateMarker();
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.unlogin_more_btn:
                showCheckBox();
                break;
            case R.id.unlogin_searchbar:
                if (searchText.length() > 0) {
                    searchText.getText().clear();
                    searchText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search_icon, 0);
                    markerCount -= 1;
                    mapView.removePOIItem(searchMarker);
                } else {
                    startDestination();
                }
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        startDestination();
    }
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent review = result.getData();
                    if (review != null) {
                        Document document = review.getParcelableExtra("document");
                        searchText.setText(document.getPlaceName());

                        mSearchName = document.getPlaceName();
                        mSearchLng = Double.parseDouble(document.getX());
                        mSearchLat = Double.parseDouble(document.getY());
                        if (!isTrackingMode) {
                            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                            currentBtn = findViewById(R.id.current_btn);
                            currentBtn.setVisibility(View.VISIBLE);
                            currentBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                                    currentBtn.setVisibility(View.GONE);
                                }
                            });
                        }

                        if(searchMarker == null) {
                            searchMarker = new MapPOIItem();
                            markerCount += 1;
                        }

                        searchMarker.setItemName(mSearchName);
                        searchMarker.setTag(markerCount);
                        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mSearchLat, mSearchLng), true);
                        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(mSearchLat, mSearchLng);
                        searchMarker.setMapPoint(mapPoint);
                        mapView.addPOIItem(searchMarker);
                        searchMarker.setMarkerType(MapPOIItem.MarkerType.RedPin);
                        searchMarker.setSelectedMarkerType(MapPOIItem.MarkerType.YellowPin);
                        //마커 드래그 가능하게 설정
                        searchMarker.setDraggable(false);
                    }
                }
            });


    void startDestination() {
        if (!searchText.equals("")) {
            searchText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.seach_clear, 0);
        }
        Intent intent = new Intent(this, UnLoginDestinationActivity.class);
        launcher.launch(intent);
    }
    private void showCheckBox() {
        Chip speedBtn = findViewById(R.id.unlogin_speed_btn);
        Chip typeBtn = findViewById(R.id.unlogin_type_btn);
        Chip usingBtn = findViewById(R.id.unlogin_using_btn);

        isCheckboxVisible = !isCheckboxVisible;
        speedBtn.setVisibility(isCheckboxVisible ? View.VISIBLE : View.GONE);
        typeBtn.setVisibility(isCheckboxVisible ? View.VISIBLE : View.GONE);
        usingBtn.setVisibility(isCheckboxVisible ? View.VISIBLE : View.GONE);
    }

    public void updateMarker() {
        if(databaseCharge == null) {
            databaseCharge = new DatabaseCharge(this,  mapView);
        }

        mapView.removeAllPOIItems(); // 모든 마커 제거
        databaseCharge.updateData(
            this.usingButtonOption.getUsingType(),
            this.speedButtonOption.getSpeedType(),
            this.chargeButtonOption.getChargeType(),
            new DatabaseCharge.OnMarkerCountReceivedListener() {
                @Override
                public void onMarkerCountReceived(int markerCount) {
                   UnLoginMainActivity.this.markerCount = markerCount;
                }
            }
        );
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude, mapPointGeo.longitude);
        mapView.setMapCenterPoint(currentMapPoint, true);
        mCurrentLat = mapPointGeo.latitude;
        mCurrentLng = mapPointGeo.longitude;
        if (!isTrackingMode) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        }
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
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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