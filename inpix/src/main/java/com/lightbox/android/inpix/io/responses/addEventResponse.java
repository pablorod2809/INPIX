package com.lightbox.android.inpix.io.responses;

public class addEventResponse {
    private String user;
    private String eventCode;

    public String getUser() {
        return user;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setUser(String pUser) {
        this.user = pUser;
    }

    public void setEventCode(String pEventCode) {
        this.eventCode = pEventCode;
    }
}
