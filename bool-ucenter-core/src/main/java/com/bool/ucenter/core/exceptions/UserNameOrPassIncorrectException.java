package com.bool.ucenter.core.exceptions;

public class UserNameOrPassIncorrectException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "用户名或密码错误！";
	}
	
}
