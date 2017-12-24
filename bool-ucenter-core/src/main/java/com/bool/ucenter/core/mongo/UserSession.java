package com.bool.ucenter.core.mongo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.annotation.Id;

public class UserSession implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//用户唯一的会话存储
	@Id
	private String token;
	//用户ID
	private Integer userId;
	//用户手机号，统一用手机号登陆
	private String mobile;
	//用户昵称
	private String nickName;
	//用户头像
	private String avatar;
	//会话创建时间
	private Date sessionTime;
	//会话失效时间
	private Date sessionOverDue;
	
	
	public UserSession() {
		
	}
	
	


	public UserSession(Integer userId, String mobile, String nickName, String avatar) {
		super();
		this.userId = userId;
		this.mobile = mobile;
		this.nickName = nickName;
		this.avatar = avatar;
		this.token = RandomStringUtils.randomAlphanumeric(32);
		this.sessionTime = new Date();
		
		//失效时间2天
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		cl.add(Calendar.DATE, 2);
		
		this.sessionOverDue = cl.getTime();
	}




	public String getToken() {
		return token;
	}


	public void setToken(String token) {
		this.token = token;
	}


	public Integer getUserId() {
		return userId;
	}


	public void setUserId(Integer userId) {
		this.userId = userId;
	}


	public String getMobile() {
		return mobile;
	}


	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public String getNickName() {
		return nickName;
	}


	public void setNickName(String nickName) {
		this.nickName = nickName;
	}


	public String getAvatar() {
		return avatar;
	}


	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public Date getSessionTime() {
		return sessionTime;
	}


	public void setSessionTime(Date sessionTime) {
		this.sessionTime = sessionTime;
	}


	public Date getSessionOverDue() {
		return sessionOverDue;
	}


	public void setSessionOverDue(Date sessionOverDue) {
		this.sessionOverDue = sessionOverDue;
	}
	
	

}
