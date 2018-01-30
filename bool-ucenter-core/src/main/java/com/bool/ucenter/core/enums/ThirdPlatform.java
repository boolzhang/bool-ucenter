package com.bool.ucenter.core.enums;

public enum ThirdPlatform {

	QQ(1001),
	WEIXIN(1002),
	WEIBO(1003);
	
	private int value;
	
	private ThirdPlatform(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
}
