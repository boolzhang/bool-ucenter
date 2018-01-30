package com.bool.ucenter.core.exceptions;

public class TokenOverdueException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "登录会话超时，请重新登录！";
	}
}
