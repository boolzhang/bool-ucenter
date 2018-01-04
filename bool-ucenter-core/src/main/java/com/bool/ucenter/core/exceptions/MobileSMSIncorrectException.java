package com.bool.ucenter.core.exceptions;

public class MobileSMSIncorrectException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "短信验证码不正确！";
	}
}
