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
			//�����ˣ�ʧЧ
			userSessionRepository.delete(token);
			throw new TokenOverdueException();
		}
		
		//ʧЧʱ��2��
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
		
		//�����û�
		CenterUser user = this.saveUser(mobile, pass, pass, null);

		//���ػỰ
		return this.generateSession(user);
	}

	@Override
	public UserSession thirdLogin(ThirdPlatform platform, String thirdUserId, String mobile, String thirdNickName,
			String thirdAvatar) throws UserDisabledException, Exception {
		
		CenterUserBind key = new CenterUserBind();
		key.setThirdPlatform(platform.getValue());
		key.setThirdUserId(thirdUserId);
		
		CenterUserBind bind = centerUserBindMapper.selectByPrimaryKey(key);
		
		//�״ε�¼��δ�󶨵�
		if(bind==null) {
			
			//�����û�
			CenterUser user = this.saveUser(mobile, RandomStringUtils.randomAlphanumeric(12), thirdNickName, thirdAvatar);
			
			//����󶨹�ϵ
			bind = key;
			bind.setUserId(user.getUserId());
			bind.setBindTime(new Date());
			centerUserBindMapper.insertSelective(bind);
			
			//���ػỰ
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
		
		//�û�������
		if(user==null) {
			throw new UserNotExistsException();
		}
		
		PassInfo passInfo = PassHandler.buildPassword(newPsss);
		user.setPasswd(passInfo.getPassword());
		user.setPassKey(passInfo.getSalt());
		//�����û�
		centerUserMapper.updateByPrimaryKeySelective(user);
		
	}
	
	
	/**
	 * �û���¼
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
		
		//�û�������
		if(user==null) {
			throw new UserNotExistsException();
		}
		
		//�û�״̬����ȷ
		if(UserState.ENABLED.getValue()!=user.getState()) {
			throw new UserDisabledException();
		}
		
		
		if(checkPass) {
			//У������
			boolean check = PassHandler.checkPass(pass, user.getPassKey(), user.getPasswd());
			
			if(!check) {
				throw new UserNameOrPassIncorrectException();
			}
		}
		
		//���ػỰ
		return this.generateSession(user);
	}
	
	
	/**
	 * �û���¼
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
		
		//�û�������
		if(user==null) {
			throw new UserNotExistsException();
		}
		
		//�û�״̬����ȷ
		if(UserState.ENABLED.getValue()!=user.getState()) {
			throw new UserDisabledException();
		}
		
		//�����Ự
		return this.generateSession(user);
	}
	
	/**
	 * �����û���Ϣ
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
		
		//�����û�
		centerUserMapper.insertSelective(user);
		
		return user;
		
	}
	
	/**
	 * �����û���Ϣ��������session���ݲ�����
	 * @param user
	 * @return
	 */
	private UserSession generateSession(CenterUser user){
		
		//�����Ự
		UserSession session = new UserSession(user.getUserId() , user.getMobile() , user.getNickName() , user.getAvatar());
		
		//����Ự
		userSessionRepository.save(session);
		
		return session;
	}

	

}
