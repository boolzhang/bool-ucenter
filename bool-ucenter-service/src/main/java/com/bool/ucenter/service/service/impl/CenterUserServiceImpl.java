package com.bool.ucenter.service.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.bool.jutils.pass.PassHandler;
import com.bool.jutils.pass.PassInfo;
import com.bool.ucenter.core.entity.CenterUser;
import com.bool.ucenter.core.entity.CenterUserAddr;
import com.bool.ucenter.core.entity.CenterUserBind;
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
import com.bool.ucenter.service.mapper.CenterUserAddrMapper;
import com.bool.ucenter.service.mapper.CenterUserBindMapper;
import com.bool.ucenter.service.mapper.CenterUserMapper;
import com.bool.ucenter.service.mapper.ext.CenterUserAddrExtMapper;
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
	private CenterUserAddrExtMapper centerUserAddrExtMapper;
	
	@Autowired
	private CenterUserAddrMapper centerUserAddrMapper;
	
	
	
	@Autowired
	private UserSessionRepository userSessionRepository;
	
	@Override
	public UserSession checkToken(String token) throws TokenOverdueException, Exception {
		
		
		System.out.println("++++++token为："+token);
		
		UserSession session = userSessionRepository.findOne(token);
		
		
		System.out.println("++++++session为："+session);
		
		if(session==null) {
			throw new TokenOverdueException();
		}
		
		if(session.getSessionOverDue().before(new Date())) {
			//删除会话
			this.clearSession(session.getUserId());
			throw new TokenOverdueException();
		}
		
		//设置会话有效期
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
	public UserSession loginWithSMS(String mobile)
			throws UserNotExistsException, UserDisabledException, Exception {

		return this.userLogin(mobile, null, false);
	}

	@Override
	public UserSession reg(String mobile, String pass)
			throws MobileAlreadyExistsException, Exception {
		
		//保存用户并返回信息
		CenterUser user = this.saveUser(mobile, pass, pass, null);

		//产生会话信息
		return this.generateSession(user);
	}
	
	@Override
	public void bindMobile(int userId, String mobile)
			throws MobileAlreadyExistsException, Exception {

		CenterUser user = new CenterUser();
		user.setUserId(userId);
		user.setMobile(mobile);
		
		try {
			centerUserMapper.updateByPrimaryKeySelective(user);
		}catch(DuplicateKeyException e) {
			throw new MobileAlreadyExistsException();
		}
		
	}

	@Override
	public UserSession thirdLogin(ThirdPlatform platform, String thirdUserId, String mobile, String thirdNickName,
			String thirdAvatar) throws UserDisabledException, Exception {
		
		CenterUserBind key = new CenterUserBind();
		key.setThirdPlatform(platform.getValue());
		key.setThirdUserId(thirdUserId);
		
		CenterUserBind bind = centerUserBindMapper.selectByPrimaryKey(key);
		
		//新用户
		if(bind==null) {
			
			//参数用户中心用户
			CenterUser user = this.saveUser(mobile, RandomStringUtils.randomAlphanumeric(12), thirdNickName, thirdAvatar);
			
			//产生绑定信息
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
	public void resetPass(String mobile, String newPsss)
			throws MobileSMSIncorrectException, Exception {
		
		CenterUser user = centerUserExtMapper.selectByMobile(mobile);
		
		//用户找不到
		if(user==null) {
			throw new UserNotExistsException();
		}
		
		PassInfo passInfo = PassHandler.buildPassword(newPsss);
		user.setPasswd(passInfo.getPassword());
		user.setPassKey(passInfo.getSalt());
		//更新用户信息
		centerUserMapper.updateByPrimaryKeySelective(user);
		
	}
	
	/**
	 * 根据用户ID清除会话
	 * @param userId
	 */
	private void clearSession(int userId) {
		
		List<UserSession>  list = userSessionRepository.findByUserId(userId);
		
		if(!CollectionUtils.isEmpty(list)) {
			
			for(UserSession session: list) {
				userSessionRepository.delete(session);
			}
		}
		
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
		
		//校验用户
		this.checkState(user);
		
		
		if(checkPass) {
			//检验密码
			boolean check = PassHandler.checkPass(pass, user.getPassKey(), user.getPasswd());
			
			if(!check) {
				throw new UserNameOrPassIncorrectException();
			}
		}
		
		//产生会话
		return this.generateSession(user);
	}
	
	
	/**
	 * 用户登录
	 * @param userId
	 * @return
	 * @throws UserNotExistsException
	 * @throws UserDisabledException
	 * @throws UserNameOrPassIncorrectException
	 * @throws Exception
	 */
	private UserSession userLogin(int userId) throws UserNotExistsException, UserDisabledException, UserNameOrPassIncorrectException, Exception{
		

		CenterUser user = centerUserMapper.selectByPrimaryKey(userId);
		
		//校验用户
		this.checkState(user);
		
		//产生会话
		return this.generateSession(user);
	}
	
	
	/**
	 * 通用校验用户状态
	 * @param user
	 * @throws UserNotExistsException
	 * @throws UserDisabledException
	 */
	private void checkState(CenterUser user) throws UserNotExistsException, UserDisabledException {
		
		//用户不存在
		if(user==null) {
			throw new UserNotExistsException();
		}
		
		//状态判断
		if(UserState.ENABLED.getValue()!=user.getState()) {
			throw new UserDisabledException();
		}
	}
	
	/**
	 * 保存用户
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
		
		//保存
		centerUserMapper.insertSelective(user);
		
		return user;
		
	}
	
	/**
	 * 根据用户产生会话
	 * @param user
	 * @return
	 */
	private UserSession generateSession(CenterUser user){
		
		//产生会话
		UserSession session = new UserSession(user.getUserId() , user.getMobile() , user.getNickName() , user.getAvatar());
		
		
		//删除旧的会话
		this.clearSession(user.getUserId());
		
		//保存到库
		userSessionRepository.save(session);
		
		return session;
	}

	@Override
	public List<CenterUserAddr> findAddrs(int userId) throws Exception {
		return centerUserAddrExtMapper.findAddrList(userId);
	}

	@Transactional(value="ucenter" , rollbackFor=Exception.class)
	@Override
	public int saveAddr(int userId, String address, String mobile, String contact , int isDefault) throws Exception {
		
		
		
		if(isDefault==1) {
			centerUserAddrExtMapper.clearDefault(userId);
		}
		
		CenterUserAddr addr = new CenterUserAddr();
		addr.setAddress(address);
		addr.setContact(contact);
		addr.setMobile(mobile);
		addr.setUserId(userId);
		addr.setIsDefault(0);
		addr.setIsDefault(isDefault);
		centerUserAddrMapper.insertSelective(addr);
		
		return addr.getAddrId();
	}

	@Transactional(value="ucenter" , rollbackFor=Exception.class)
	@Override
	public void updateAddr(int addrId, int userId, String address, String mobile, String contact, int isDefault)
			throws Exception {
		
		CenterUserAddr addr = centerUserAddrMapper.selectByPrimaryKey(addrId);
		

		if(!addr.getUserId().equals(userId)) {
			throw new Exception("您无权修改此地址！");
		}
		
		if(isDefault==1) {
			centerUserAddrExtMapper.clearDefault(userId);
		}
		
		addr.setAddress(address);
		addr.setContact(contact);
		addr.setMobile(mobile);
		addr.setIsDefault(isDefault);
		
		centerUserAddrMapper.updateByPrimaryKeySelective(addr);
		
		
	}

	@Override
	public CenterUserAddr findAddrDetail(int addrId, int userId) throws Exception {

		CenterUserAddr addr = centerUserAddrMapper.selectByPrimaryKey(addrId);
		if(addr!=null && !addr.getUserId().equals(userId)) {
			throw new Exception("地址不存在或无权查看此地址！");
		}
		return addr;
	}

	@Override
	public void deleteAddr(int addrId, int userId) throws Exception {
		
		CenterUserAddr addr = this.findAddrDetail(addrId, userId);
		
		if(addr!=null) {
			centerUserAddrMapper.deleteByPrimaryKey(addrId);
		}
	}

	@Override
	public CenterUserAddr findDefault(int userId) throws Exception {
		return centerUserAddrExtMapper.findDefault(userId);
	}

	@Override
	public String findBindId(int userId, ThirdPlatform platform) {
		return centerUserExtMapper.findBindId(userId, platform.getValue());
	}

	@Override
	public void updateBind(ThirdPlatform platform, String thirdUserId, String newThirdUserId) {
		centerUserExtMapper.updateBind(platform.getValue(), thirdUserId, newThirdUserId);
	}

	@Override
	public boolean checkHasBind(String thirdUserId, ThirdPlatform platform) {
		
		return centerUserExtMapper.countBindId(thirdUserId, platform.getValue())>0;
	}

	

	

}
