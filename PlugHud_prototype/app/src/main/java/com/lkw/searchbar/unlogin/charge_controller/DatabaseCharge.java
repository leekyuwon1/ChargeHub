package com.lkw.searchbar.unlogin.charge_controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.lkw.searchbar.unlogin.UnLoginMainActivity;
import com.lkw.searchbar.unlogin.bottomsheet.ArrayInfoManager;
import com.lkw.searchbar.unlogin.model.charge.CharService;
import com.lkw.searchbar.unlogin.model.charge.ChargeDocuments;
import com.lkw.searchbar.unlogin.model.charge.retrodb.DataClient;
import com.lkw.searchbar.unlogin.states.ChargeType;
import com.lkw.searchbar.unlogin.states.SpeedType;
import com.lkw.searchbar.unlogin.states.UsingType;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseCharge {
    private int markerCount;
    private Context context;
    private MapView mapView;
    Map<Integer, String> arrayInfo = new HashMap<>();
    private Call<List<ChargeDocuments>> call;

    public DatabaseCharge(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
    }
    public void updateData(
            UsingType usingType,
            SpeedType speedType,
            ChargeType chargeType,
            OnMarkerCountReceivedListener listener
    ) {
        CharService chargeInterface = DataClient.getInstance().create(CharService.class);
        call = chargeInterface.getDatabaseItems2(usingType.ordinal(), speedType.ordinal(), chargeType.getNum());
        call.enqueue(new Callback<List<ChargeDocuments>>() {
            @Override
            public void onResponse(Call<List<ChargeDocuments>> call, Response<List<ChargeDocuments>> response) {
                if (response.isSuccessful()) {
                    List<ChargeDocuments> resultList = response.body();
                    if (resultList != null && !resultList.isEmpty()) {
                                int count = 0;
                                for (ChargeDocuments results : resultList) {
                                    Integer cpTp = results.getCpTp();
                                    Integer chargeType = results.getChargeTp();
                                    Double chargeLat = results.getLat();
                                    Double chargeLon = results.getLongi();
                                    String addrNm = results.getAddr();
                                    String chargeNm = results.getCpNm();
                                    Integer chargeId = results.getCpId();
                                    Integer chargeState = results.getCpStat();
                                    Integer csId = results.getCsId();
                                    String csNm = results.getCsNm();
                                    String statUpdateDatetime = results.getStatUpdateDatetime();
                                    arrayInfo.put(count, String.format("%s,%f,%f,%s,%d,%d,%d,%d,%d, %s, %s",
                                            addrNm, chargeLat, chargeLon, chargeNm, chargeType, chargeId, chargeState, cpTp, csId, csNm, statUpdateDatetime));
                                    MapPOIItem marker = new MapPOIItem();
                                    ArrayInfoManager.setArrayInfo(arrayInfo);
                                    marker.setTag(count);
                                    MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(chargeLat, chargeLon);

                                    marker.setItemName(chargeNm);
                                    marker.setShowCalloutBalloonOnTouch(true);
                                    marker.setMapPoint(mapPoint);
                                    marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                                    marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                                    marker.setDraggable(false);
                                    mapView.addPOIItem(marker);
                                    count++;
                                    Log.d(UnLoginMainActivity.class.getName(), "Marker Create");

                                    if (count >= 10) {
                                        break;
                                    }
                                }
                                markerCount = count;
                                listener.onMarkerCountReceived(markerCount);

                    } else {
                        Log.d("Retrofit", "No results found");
                    }
                } else {
                    Log.d("Retrofit", "Server response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ChargeDocuments>> call, Throwable t) {
                Log.d("Retrofit", "Communication failure: " + t.getMessage());
            }
        });
    }

    public interface OnMarkerCountReceivedListener {
        void onMarkerCountReceived(int markerCount);
    }
}