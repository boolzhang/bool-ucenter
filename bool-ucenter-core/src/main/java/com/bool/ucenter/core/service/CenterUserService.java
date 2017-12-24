package com.bool.ucenter.core.service;

import com.bool.ucenter.core.enums.ThirdPlatform;
import com.bool.ucenter.core.exceptions.MobileAlreadyExistsException;
import com.bool.ucenter.core.exceptions.MobileSMSIncorrectException;
import com.bool.ucenter.core.exceptions.TokenOverdueException;
import com.bool.ucenter.core.exceptions.UserDisabledException;
import com.bool.ucenter.core.exceptions.UserNameOrPassIncorrectException;
import com.bool.ucenter.core.exceptions.UserNotExistsException;
import com.bool.ucenter.core.mongo.UserSession;

public interface CenterUserService {
	
	
	/**
	 * 校验token是否还有效
	 * @param token
	 * @return
	 * @throws TokenOverdueException
	 * @throws Exception
	 */
	UserSession checkToken(String token) throws TokenOverdueException , Exception;

	/**
	 * 用户登录，使用手机号和密码登录
	 * @param mobile
	 * @param pass
	 * @return
	 * @throws UserNameOrPassIncorrectException
	 */
	UserSession login(String mobile, String pass) throws UserNotExistsException,UserDisabledException,UserNameOrPassIncorrectException,Exception;

	/**
	 * 用户登录，使用短信验证码登录
	 * @param mobile
	 * @param inputSms
	 * @param savedSms
	 * @return
	 * @throws MobileSMSIncorrectException
	 */
	UserSession loginWithSMS(String mobile, String inputSms , String savedSms) throws UserNotExistsException,UserDisabledException,MobileSMSIncorrectException,Exception;

	/**
	 * 用户注册，使用手机号码注册
	 * @param mobile
	 * @param pass
	 * @param inputSms
	 * @param savedSms
	 * @return
	 * @throws MobileAlreadyExistsException
	 */
	UserSession reg(String mobile, String pass, String inputSms , String savedSms) throws MobileAlreadyExistsException,Exception;

	/**
	 * 使用第三方平台登录；
	 * @param platform
	 * @param thirdUserId
	 * @param mobile
	 * @param thirdNickName
	 * @param thirdAvatar
	 * @return
	 * @throws Exception
	 */
	UserSession thirdLogin(ThirdPlatform platform, String thirdUserId, String mobile, String thirdNickName, String thirdAvatar)
			throws UserDisabledException,Exception;
	
	
	/**
	 * 重置用户密码，通过手机号设置密码
	 * @param mobile
	 * @param newPsss
	 * @param inputSms
	 * @param savedSms
	 * @throws MobileSMSIncorrectException
	 */
	void resetPass(String mobile , String newPsss , String inputSms , String savedSms) throws MobileSMSIncorrectException,Exception;
	
	
}
