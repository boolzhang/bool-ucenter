package com.bool.ucenter.core.entity;

import java.io.Serializable;
import java.util.Date;

public class CenterUserBind extends CenterUserBindKey implements Serializable {
    private Integer userId;

    private Date bindTime;

    private static final long serialVersionUID = 1L;

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