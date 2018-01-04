package com.bool.ucenter.core.exceptions;

public class UserNotExistsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "用户不存在！";
	}
}
