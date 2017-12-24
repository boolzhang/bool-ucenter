package com.bool.ucenter.service.service.impl;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bool.jutils.pass.PassHandler;
import com.bool.jutils.pass.PassInfo;
import com.bool.ucenter.core.enums.ThirdPlatform;
import com.bool.ucenter.core.enums.UserState;
import com.bool.ucenter.core.exceptions.MobileAlreadyExistsException;
import com.bool.ucenter.core.exceptions.MobileSMSIncorrectException;
import com.bool.ucenter.core.exceptions.TokenOverdueException;
import com.bool.ucenter.core.exceptions.UserDisabledException;
import com.bool.ucenter.core.exceptions.UserNameOrPassIncorrectException;
import com.bool.ucenter.core.exceptions.UserNotExistsException;
import com.bool.ucenter.core.mongo.UserSession;
import com.bool.ucenter.core.mongo.UserSessionRepository;
import com.bool.ucenter.core.service.CenterUserService;
import com.bool.ucenter.service.entity.CenterUser;
import com.bool.ucenter.service.entity.CenterUserBind;
import com.bool.ucenter.service.mapper.CenterUserBindMapper;
import com.bool.ucenter.service.mapper.CenterUserMapper;
import com.bool.ucenter.service.mapper.ext.CenterUserExtMapper;

@Service
public class CenterUserServiceImpl implements CenterUserService {
	
	@Autowired
	private CenterUserMapper centerUserMapper;
	
	@Autowired
	private CenterUserBindMapper centerUserBindMapper;
	
	@Autowired
	private CenterUserExtMapper centerUserExtMapper;
	
	@Autowired
	private UserSessionRepository userSessionRepository;
	
	@Override
	public UserSession checkToken(String token) throws TokenOverdueException, Exception {
		
		
		
		UserSession session = userSessionRepository.findOne(token);
		
		if(session==null) {
			throw new TokenOverdueException();
		}
		
		if(session.getSessionOverDue().before(new Date())) {
			//过期了，失效
			userSessionRepository.delete(token);
			throw new TokenOverdueException();
		}
		
		//失效时间2天
		Calendar cl = Calendar.getInstance();
		cl.setTime(new Date());
		cl.add(Calendar.DATE, 2);
		session.setSessionOverDue(cl.getTime());
		
		userSessionRepository.save(session);
		return session;
	}

	@Override
	public UserSession login(String mobile, String pass)
			throws UserNotExistsException, UserDisabledException, UserNameOrPassIncorrectException, Exception {
		
		return this.userLogin(mobile, pass, true);
	}

	@Override
	public UserSession loginWithSMS(String mobile, String inputSms, String savedSms)
			throws UserNotExistsException, UserDisabledException, MobileSMSIncorrectException, Exception {
		
		if(!inputSms.equals(savedSms)) {
			throw new MobileSMSIncorrectException();
		}
		
		return this.userLogin(mobile, null, false);
	}

	@Override
	public UserSession reg(String mobile, String pass, String inputSms, String savedSms)
			throws MobileAlreadyExistsException, Exception {
		
		
		if(!inputSms.equals(savedSms)) {
			throw new MobileSMSIncorrectException();
		}
		
		//保存用户
		CenterUser user = this.saveUser(mobile, pass, pass, null);

		//返回会话
		return this.generateSession(user);
	}

	@Override
	public UserSession thirdLogin(ThirdPlatform platform, String thirdUserId, String mobile, String thirdNickName,
			String thirdAvatar) throws UserDisabledException, Exception {
		
		CenterUserBind key = new CenterUserBind();
		key.setThirdPlatform(platform.getValue());
		key.setThirdUserId(thirdUserId);
		
		CenterUserBind bind = centerUserBindMapper.selectByPrimaryKey(key);
		
		//首次登录，未绑定的
		if(bind==null) {
			
			//保存用户
			CenterUser user = this.saveUser(mobile, RandomStringUtils.randomAlphanumeric(12), thirdNickName, thirdAvatar);
			
			//保存绑定关系
			bind = key;
			bind.setUserId(user.getUserId());
			bind.setBindTime(new Date());
			centerUserBindMapper.insertSelective(bind);
			
			//返回会话
			return this.generateSession(user);
			
			
		}else {
			return this.userLogin(bind.getUserId());
		}
		
	}

	@Override
	public void resetPass(String mobile, String newPsss, String inputSms, String savedSms)
			throws MobileSMSIncorrectException, Exception {
		
		if(!inputSms.equals(savedSms)) {
			throw new MobileSMSIncorrectException();
		}
		
		CenterUser user = centerUserExtMapper.selectByMobile(mobile);
		
		//用户不存在
		if(user==null) {
			throw new UserNotExistsException();
		}
		
		PassInfo passInfo = PassHandler.buildPassword(newPsss);
		user.setPasswd(passInfo.getPassword());
		user.setPassKey(passInfo.getSalt());
		//保存用户
		centerUserMapper.updateByPrimaryKeySelective(user);
		
	}
	
	
	/**
	 * 用户登录
	 * @param mobile
	 * @param pass
	 * @param checkPass
	 * @return
	 * @throws UserNotExistsException
	 * @throws UserDisabledException
	 * @throws UserNameOrPassIncorrectException
	 * @throws Exception
	 */
	private UserSession userLogin(String mobile , String pass , boolean checkPass) throws UserNotExistsException, UserDisabledException, UserNameOrPassIncorrectException, Exception{
		

		CenterUser user = centerUserExtMapper.selectByMobile(mobile);
		
		//用户不存在
		if(user==null) {
			throw new UserNotExistsException();
		}
		
		//用户状态不正确
		if(UserState.ENABLED.getValue()!=user.getState()) {
			throw new UserDisabledException();
		}
		
		
		if(checkPass) {
			//校验密码
			boolean check = PassHandler.checkPass(pass, user.getPassKey(), user.getPasswd());
			
			if(!check) {
				throw new UserNameOrPassIncorrectException();
			}
		}
		
		//返回会话
		return this.generateSession(user);
	}
	
	
	/**
	 * 用户登录
	 * @param mobile
	 * @param pass
	 * @param checkPass
	 * @return
	 * @throws UserNotExistsException
	 * @throws UserDisabledException
	 * @throws UserNameOrPassIncorrectException
	 * @throws Exception
	 */
	private UserSession userLogin(int userId) throws UserNotExistsException, UserDisabledException, UserNameOrPassIncorrectException, Exception{
		

		CenterUser user = centerUserMapper.selectByPrimaryKey(userId);
		
		//用户不存在
		if(user==null) {
			throw new UserNotExistsException();
		}
		
		//用户状态不正确
		if(UserState.ENABLED.getValue()!=user.getState()) {
			throw new UserDisabledException();
		}
		
		//构建会话
		return this.generateSession(user);
	}
	
	/**
	 * 保存用户信息
	 * @param mobile
	 * @param pass
	 * @param nickName
	 * @param avatar
	 * @return
	 */
	private CenterUser saveUser(String mobile , String pass , String nickName , String avatar) {
		
		CenterUser user = new CenterUser();
		PassInfo passInfo = PassHandler.buildPassword(pass);
		user.setMobile(mobile);
		user.setNickName(nickName);
		user.setAvatar(avatar);
		user.setPasswd(passInfo.getPassword());
		user.setPassKey(passInfo.getSalt());
		user.setState(UserState.ENABLED.getValue());
		user.setLastLoginTime(new Date());
		user.setCreateTime(new Date());
		
		//保存用户
		centerUserMapper.insertSelective(user);
		
		return user;
		
	}
	
	/**
	 * 根据用户信息产生保存session数据并返回
	 * @param user
	 * @return
	 */
	private UserSession generateSession(CenterUser user){
		
		//构建会话
		UserSession session = new UserSession(user.getUserId() , user.getMobile() , user.getNickName() , user.getAvatar());
		
		//保存会话
		userSessionRepository.save(session);
		
		return session;
	}

	

}
