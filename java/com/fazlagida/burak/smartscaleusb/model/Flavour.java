package com.fazlagida.burak.smartscaleusb.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Flavour {

    @SerializedName("f1")
    @Expose
    private String fav1;

    @SerializedName("f2")
    @Expose
    private String fav2;

    @SerializedName("f3")
    @Expose
    private String fav3;

    @SerializedName("f4")
    @Expose
    private String fav4;

    @SerializedName("f5")
    @Expose
    private String fav5;

    @SerializedName("f6")
    @Expose
    private String fav6;

    @SerializedName("qty")
    @Expose
    private String perPlate;

    public String getFav1() {
        return fav1;
    }

    public void setFav1(String fav1) {
        this.fav1 = fav1;
    }

    public String getFav2() {
        return fav2;
    }

    public void setFav2(String fav2) {
        this.fav2 = fav2;
    }

    public String getFav3() {
        return fav3;
    }

    public void setFav3(String fav3) {
        this.fav3 = fav3;
    }

    public String getFav4() {
        return fav4;
    }

    public void setFav4(String fav4) {
        this.fav4 = fav4;
    }

    public String getFav5() {
        return fav5;
    }

    public void setFav5(String fav5) {
        this.fav5 = fav5;
    }

    public String getFav6() {
        return fav6;
    }

    public void setFav6(String fav6) {
        this.fav6 = fav6;
    }

    public String getPerPlate() {
        return perPlate;
    }

    public void setPerPlate(String perPlate) {
        this.perPlate = perPlate;
    }
}
