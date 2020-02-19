package com.fazlagida.burak.smartscaleusb.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Barcode {

    @SerializedName("qty")
    @Expose
    private String qty;
    @SerializedName("status")
    @Expose
    private String status;

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
