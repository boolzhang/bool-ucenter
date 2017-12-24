package com.bool.ucenter.service.mapper.ext;

import com.bool.ucenter.service.entity.CenterUser;

public interface CenterUserExtMapper {
	
	/**
	 * 根据手机号码查找用户
	 * @param mobile
	 * @return
	 */
    CenterUser selectByMobile(String mobile);
}