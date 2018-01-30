package com.bool.ucenter.service.entity;

import java.util.Date;

public class CenterUserBind extends CenterUserBindKey {
    private Integer userId;

    private Date bindTime;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getBindTime() {
        return bindTime;
    }

    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }
}