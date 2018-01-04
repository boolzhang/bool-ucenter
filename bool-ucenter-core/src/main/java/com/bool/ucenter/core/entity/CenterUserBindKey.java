package com.bool.ucenter.core.entity;

import java.io.Serializable;

public class CenterUserBindKey implements Serializable {
    private Integer thirdPlatform;

    private String thirdUserId;

    private static final long serialVersionUID = 1L;

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