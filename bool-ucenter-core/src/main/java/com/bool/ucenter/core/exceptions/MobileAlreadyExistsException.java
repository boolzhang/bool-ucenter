package com.bool.ucenter.core.exceptions;

public class MobileAlreadyExistsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "手机号码已经被绑定了！";
	}
}
