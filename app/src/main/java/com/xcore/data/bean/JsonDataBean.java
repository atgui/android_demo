package com.xcore.data.bean;
import java.io.Serializable;

public class JsonDataBean implements Serializable{
    private int status;

    private String api;
    private String api1;
    private String api2;
    private String api3;
    private String api4;
    private String api5;
    private String api6;
//    private String api7;
//    private String api8;
//    private String api9;
//    private String api10;

    @Override
    public String toString() {
        return "JsonDataBean{" +
                "api='" + api + '\'' +
                ", api1='" + api1 + '\'' +
                ", api2='" + api2 + '\'' +
                ", api3='" + api3 + '\'' +
                ", api4='" + api4 + '\'' +
                ", api5='" + api5 + '\'' +
                ", api6='" + api6 + '\'' +
//                ", api7='" + api7 + '\'' +
//                ", api8='" + api8 + '\'' +
//                ", api9='" + api9 + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getApi1() {
        return api1;
    }

    public void setApi1(String api1) {
        this.api1 = api1;
    }

    public String getApi2() {
        return api2;
    }

    public void setApi2(String api2) {
        this.api2 = api2;
    }

    public String getApi3() {
        return api3;
    }

    public void setApi3(String api3) {
        this.api3 = api3;
    }

    public String getApi4() {
        return api4;
    }

    public void setApi4(String api4) {
        this.api4 = api4;
    }

    public String getApi5() {
        return api5;
    }

    public void setApi5(String api5) {
        this.api5 = api5;
    }

    public String getApi6() {
        return api6;
    }

    public void setApi6(String api6) {
        this.api6 = api6;
    }

//    public String getApi7() {
//        return api7;
//    }
//
//    public void setApi7(String api7) {
//        this.api7 = api7;
//    }
//
//    public String getApi8() {
//        return api8;
//    }
//
//    public void setApi8(String api8) {
//        this.api8 = api8;
//    }
//
//    public String getApi9() {
//        return api9;
//    }
//
//    public void setApi9(String api9) {
//        this.api9 = api9;
//    }

//    public String getApi10() {
//        return api10;
//    }
//
//    public void setApi10(String api10) {
//        this.api10 = api10;
//    }
}
