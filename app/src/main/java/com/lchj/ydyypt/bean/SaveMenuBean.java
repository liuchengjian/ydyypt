package com.lchj.ydyypt.bean;

public class SaveMenuBean {
    private String userId;
    private String resId;
    private String appId;

    public SaveMenuBean(String userId, String resId, String appId) {
        this.userId = userId;
        this.resId = resId;
        this.appId = appId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
