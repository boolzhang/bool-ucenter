package com.bool.ucenter.service.entity;

public class CenterUserBindKey {
    private Integer thirdPlatform;

    private String thirdUserId;

    public Integer getThirdPlatform() {
        return thirdPlatform;
    }

    public void setThirdPlatform(Integer thirdPlatform) {
        this.thirdPlatform = thirdPlatform;
    }

    public String getThirdUserId() {
        return thirdUserId;
    }

    public void setThirdUserId(String thirdUserId) {
        this.thirdUserId = thirdUserId == null ? null : thirdUserId.trim();
    }
}