package com.bool.ucenter.core.service;

import java.util.List;

import com.bool.ucenter.core.entity.CenterUserAddr;
import com.bool.ucenter.core.enums.ThirdPlatform;
import com.bool.ucenter.core.exceptions.MobileAlreadyExistsException;
import com.bool.ucenter.core.exceptions.MobileSMSIncorrectException;
import com.bool.ucenter.core.exceptions.TokenOverdueException;
import com.bool.ucenter.core.exceptions.UserDisabledException;
import com.bool.ucenter.core.exceptions.UserNameOrPassIncorrectException;
import com.bool.ucenter.core.exceptions.UserNotExistsException;
import com.bool.ucenter.core.mongo.UserSession;

/**
 * 用户中心开放接口
 * @author boolzhang
 *
 */
public interface CenterUserService {

	/**
	 * 校验token是否还有效
	 * 
	 * @param token
	 * @return
	 * @throws TokenOverdueException
	 * @throws Exception
	 */
	UserSession checkToken(String token) throws TokenOverdueException, Exception;

	/**
	 * 用户登录，使用手机号和密码登录
	 * 
	 * @param mobile
	 * @param pass
	 * @return
	 * @throws UserNameOrPassIncorrectException
	 */
	UserSession login(String mobile, String pass)
			throws UserNotExistsException, UserDisabledException, UserNameOrPassIncorrectException, Exception;

	/**
	 * 用户登录，使用短信验证码登录
	 * 
	 * @param mobile
	 * @param inputSms
	 * @param savedSms
	 * @return
	 * @throws MobileSMSIncorrectException
	 */
	UserSession loginWithSMS(String mobile)
			throws UserNotExistsException, UserDisabledException, Exception;

	/**
	 * 用户注册，使用手机号码注册
	 * 
	 * @param mobile
	 * @param pass
	 * @param inputSms
	 * @param savedSms
	 * @return
	 * @throws MobileAlreadyExistsException
	 */
	UserSession reg(String mobile, String pass) throws MobileAlreadyExistsException, Exception;
	
	/**
	 * 绑定手机号
	 * @param userId
	 * @param mobile
	 * @param inputSms
	 * @param savedSms
	 * @throws MobileAlreadyExistsException
	 * @throws MobileSMSIncorrectException
	 * @throws Exception
	 */
	void bindMobile(int userId , String mobile) throws MobileAlreadyExistsException, MobileSMSIncorrectException, Exception;

	/**
	 * 使用第三方平台登录；
	 * 
	 * @param platform
	 * @param thirdUserId
	 * @param mobile
	 * @param thirdNickName
	 * @param thirdAvatar
	 * @return
	 * @throws Exception
	 */
	UserSession thirdLogin(ThirdPlatform platform, String thirdUserId, String mobile, String thirdNickName,
			String thirdAvatar) throws UserDisabledException, Exception;

	/**
	 * 重置用户密码，通过手机号设置密码
	 * 
	 * @param mobile
	 * @param newPsss
	 * @param inputSms
	 * @param savedSms
	 * @throws MobileSMSIncorrectException
	 */
	void resetPass(String mobile, String newPsss) throws MobileSMSIncorrectException, Exception;
	
	
	/**
	 * 查找用户的地址列表
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	List<CenterUserAddr> findAddrs(int userId) throws Exception;
	
	/**
	 * 保存地址
	 * @param userId
	 * @param address
	 * @param mobile
	 * @param contact
	 * @param isDefault
	 * @throws Exception
	 */
	int saveAddr(int userId , String address , String mobile , String contact , int isDefault) throws Exception;

	/**
	 * 更新地址信息；如果传，则不修改
	 * @param addrId
	 * @param userId
	 * @param address
	 * @param mobile
	 * @param contact
	 * @param isDefault
	 * @throws Exception
	 */
	void updateAddr(int addrId , int userId , String address , String mobile , String contact , int isDefault) throws Exception;
	
	/**
	 * 查找地址详情
	 * @param addrId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	CenterUserAddr findAddrDetail(int addrId , int userId) throws Exception;
	
	/**
	 * 删除地址
	 * @param addrId
	 * @param userId
	 * @throws Exception
	 */
	void deleteAddr(int addrId , int userId) throws Exception;
	
	/**
	 * 查找默认地址
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	CenterUserAddr findDefault(int userId) throws Exception;
	
	/**
	 * 查找用户绑定ID
	 * @param userId
	 * @param platform
	 * @return
	 */
	String findBindId(int userId , ThirdPlatform platform);
	
	
}
