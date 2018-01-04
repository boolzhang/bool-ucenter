package com.bool.ucenter.core.exceptions;

public class UserDisabledException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "账户被禁用，请联系管理员！";
	}
}
