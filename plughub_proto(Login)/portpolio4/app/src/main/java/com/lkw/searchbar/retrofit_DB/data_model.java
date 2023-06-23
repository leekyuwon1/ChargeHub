package com.lkw.searchbar.retrofit_DB;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class data_model {
    @SerializedName("본부")
    @Expose
    private String 본부;

    @SerializedName("사업소")
    @Expose
    private String 사업소;

    @SerializedName("충전소명")
    @Expose
    private String 충전소명;

    @SerializedName("충전기")
    @Expose
    private String 충전기;

    @SerializedName("충전기ID")
    @Expose
    private String 충전기ID;

    @SerializedName("주소")
    @Expose
    private String 주소;

    @SerializedName("충전기구분")
    @Expose
    private String 충전기구분;

    @SerializedName("충전용량")
    @Expose
    private Integer 충전용량;

    @SerializedName("충전량")
    @Expose
    private Double 충전량;

    @SerializedName("충전시간")
    @Expose
    private Integer 충전시간;

    @SerializedName("충전분")
    @Expose
    private Integer 충전분;

    @SerializedName("충전종료일자")
    @Expose
    private String 충전종료일자;

    @SerializedName("충전시작시각")
    @Expose
    private String 충전시작시각;

    @SerializedName("충전종료시각")
    @Expose
    private String 충전종료시각;


//Getter 에 해당하는 부분
    public String getDepartment(){ return 본부; }
    public String getBusinessUnit(){ return 사업소; }
    public String getChargingStationName(){ return 충전소명; }
    public String getCharger(){ return 충전기; }
    public String getChargerID(){ return 충전기ID; }
    public String getAddress(){ return 주소; }
    public String getChargerType(){ return 충전기구분; }
    public Integer getCapacity(){ return 충전용량; }
    public Double getChargedAmount(){ return 충전량; }
    public Integer getChargingTime(){ return 충전시간; }
    public Integer getChargingMinute(){ return 충전분; }
    public String getChargingEndDate(){ return 충전종료일자; }
    public String getChargingStartTime(){ return 충전시작시각; }
    public String getChargingEndTime(){ return 충전종료시각; }


}