package com.lightbox.android.inpix.io.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by pablorodriguez on 20/6/18.
 */

public class eventListResponse {
    private String language;
    @SerializedName("events")
    private List<eventResponse> lstEvt;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String lang) {
        this.language = lang;
    }

    public List<eventResponse> getList() {
        return lstEvt;
    }

    public void setList(List<eventResponse> events) {
        this.lstEvt = events;
    }

}

