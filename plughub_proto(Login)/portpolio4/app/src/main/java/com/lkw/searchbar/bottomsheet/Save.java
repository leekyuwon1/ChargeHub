package com.lkw.searchbar.bottomsheet;

public class Save {
    private static Save instance;
    private String addr = null; // 주소값
    private String csNm = null;
    private String cpTp = null;
    private String chargeTp = null;
    private String spStat = null;
    private String statUpdateDatetime = null;
    private String lat = null; // 위도
    private String longi = null; // 경도

    Save() {
        // 인스턴스화 방지를 위한 private 생성자
    }

    public static synchronized Save getInstance() {
        if (instance == null) {
            instance = new Save();
        }
        return instance;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getAddr() {
        return addr;
    }

    public void setCsNm(String csNm) {
        this.csNm = csNm;
    }

    public String getCsNm() {
        return csNm;
    }

    public void setCpTp(String cpTp) {
        this.cpTp = cpTp;
    }

    public String getCpTp() {
        return cpTp;
    }

    public void setchargeTp(String cpNm) {
        this.chargeTp = cpNm;
    }

    public String getchargeTp() {
        return chargeTp;
    }


    public void setSpStat(String spStat) {
        this.spStat = spStat;
    }

    public String getSpStat() {
        return spStat;
    }

    public void setStatUpdateDatetime(String statUpdateDatetime) {
        this.statUpdateDatetime = statUpdateDatetime;
    }

    public String getStatUpdateDatetime() {
        return statUpdateDatetime;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLat() {
        return lat;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getLongi() {
        return longi;
    }
}
