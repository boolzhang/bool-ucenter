package com.bool.ucenter.service.mapper.ext;

import com.bool.ucenter.service.entity.CenterUser;

public interface CenterUserExtMapper {
	
	/**
	 * �����ֻ���������û�
	 * @param mobile
	 * @return
	 */
    CenterUser selectByMobile(String mobile);
}