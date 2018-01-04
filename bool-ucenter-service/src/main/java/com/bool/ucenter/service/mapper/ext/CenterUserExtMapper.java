package com.bool.ucenter.service.mapper.ext;

import com.bool.ucenter.core.entity.CenterUser;

public interface CenterUserExtMapper {
	
	/**
	 * 根据手机号查找用户
	 * @param mobile
	 * @return
	 */
    CenterUser selectByMobile(String mobile);
}