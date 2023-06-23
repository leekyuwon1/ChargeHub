package com.lkw.searchbar.bottomsheet;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapView;

public class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView address;
    private TextView csNm;
    private TextView cpTp;
    private TextView chargeTp;
    private TextView spStat;
    private TextView statUpdateDatetime;
    private MapView mapView;
    private Context context;

    //    private View calloutBalloonLayout; // 커스텀 콜아웃 뷰의 레이아웃
    public CustomCalloutBalloonAdapter(
            BottomSheetBehavior bottomSheetBehavior,
            TextView address, TextView csNm, TextView cpTp, TextView chargeTp, TextView spStat, TextView statUpdateDatetime,
            MapView mapView, Context context) {
        // 바텀 시트
        this.bottomSheetBehavior = bottomSheetBehavior;
        // 바텀 시트에 TextView
        this.address = address;
        this.csNm = csNm;
        this.cpTp = cpTp;
        this.chargeTp = chargeTp;
        this.spStat = spStat;
        this.statUpdateDatetime = statUpdateDatetime;
        // 메인 화면에 지도
        this.mapView = mapView;
        // 메인 화면에 상태
        this.context = context;

//        // 커스텀 콜아웃 뷰의 레이아웃 초기화
//        LayoutInflater inflater = (LayoutInflater) mapView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        calloutBalloonLayout = inflater.inflate(R.layout.bottom, null);
    }

//    public CustomCalloutBalloonAdapter(LayoutInflater inflater) {
//        mInflater = inflater;
//        //mCalloutBalloon = mInflater.inflate(R.layout.custom_callout_balloon, null);
//    }

    //    @Override
//    public View getCalloutBalloon(MapPOIItem poiItem) {
//        return null;
//        ((ImageView) mCalloutBalloon.findViewById(R.id.badge)).setImageResource(R.drawable.search_icon);
//        ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(poiItem.getItemName());
//        ((TextView) mCalloutBalloon.findViewById(R.id.desc)).setText("Custom CalloutBalloon");
//        return mCalloutBalloon;
//    }

    private void updateUIOnMainThread(Runnable runnable) {
        // 메인 화면 상태 값에 접근
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(runnable);
        }
    }

    @Override
    public View getCalloutBalloon(MapPOIItem mapPOIItem) {
        return null;
    }

    @Override
    public View getPressedCalloutBalloon(MapPOIItem poiItem) {
        Log.e("jhuijio", "getPressedCalloutBalloon");
        // 마커 클릭 시 바텀시트의 정보 변경
        if (poiItem.getTag() == 1) {
            address.setText("ex) 주소 (현재 위치 정보)");
            csNm.setText("ex) 충전소 명칭 (마커 1)");
            cpTp.setText("ex) 충전 방식");
            chargeTp.setText("ex) 급속 충전/완속 충전");
            spStat.setText("ex) 충전기 상태 코드");
            statUpdateDatetime.setText("ex) 충전기 상태 갱신 시각");
        } else if (poiItem.getTag() == 3) {
            updateUIOnMainThread(new Runnable() {
                @Override
                public void run() {
                    address.setText(Save.getInstance().getAddr());
                    // 해당 주고 값 저장
                    csNm.setText(Save.getInstance().getCsNm());
                    // 해당 충전소 명칭 저장

                    switch (Save.getInstance().getCpTp()) {
                        // 해당 충전 방식 저장
                        case "1":
                            cpTp.setText("B타입(5핀)");
                            break;
                        case "2":
                            cpTp.setText("C타입(5핀)");
                            break;
                        case "3":
                            cpTp.setText("BC타입(5핀)");
                            break;
                        case "4":
                            cpTp.setText("BC타입(7핀)");
                            break;
                        case "5":
                            cpTp.setText("DC차데모");
                            break;
                        case "6":
                            cpTp.setText("AC3상");
                            break;
                        case "7":
                            cpTp.setText("DC콤보");
                            break;
                        case "8":
                            cpTp.setText("DC차데모 +DC콤보");
                            break;
                        case "9":
                            cpTp.setText("DC차데모 +AC3상");
                            break;
                        case "10":
                            cpTp.setText("DC차데모 +DC콤보 +AC3상");
                            break;
                    }

                    switch (Save.getInstance().getchargeTp()) {
                        // 해당 충전기 타입 저장
                        case "1":
                            chargeTp.setText("완속");
                            break;
                        case "2":
                            chargeTp.setText("급속");
                            break;
                    }

                    switch (Save.getInstance().getSpStat()) {
                        // 해당 충전기 상태 저장
                        case "0":
                            spStat.setText("상태확인불가");
                            break;
                        case "1":
                            spStat.setText("충전가능");
                            break;
                        case "2":
                            spStat.setText("충전중");
                            break;
                        case "3":
                            spStat.setText("고장/점검");
                            break;
                        case "4":
                            spStat.setText("통신장애");
                            break;
                        case "9":
                            spStat.setText("충전예약");
                            break;
                    }

                    // 해당 충전기 상태 갱신 시각 저장
                    statUpdateDatetime.setText(Save.getInstance().getStatUpdateDatetime());

                }
            });
        }
        // 바텀 시트 상태 변경
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // 마커 선택 해제
        mapView.deselectPOIItem(poiItem);

        // 커스텀 콜아웃 뷰의 레이아웃 반환
        return null;
    }
}
