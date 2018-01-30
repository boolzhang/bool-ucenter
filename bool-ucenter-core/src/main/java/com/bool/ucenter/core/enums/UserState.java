package com.bool.ucenter.core.enums;

public enum UserState {

	DISABLE(0),//禁用，不允许登录
	ENABLED(1);//正常状态
	
	private int value;
	
	private UserState(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
}
