package com.bool.ucenter.service.mapper.ext;

import org.apache.ibatis.annotations.Param;

import com.bool.ucenter.core.entity.CenterUser;

public interface CenterUserExtMapper {
	
	/**
	 * 根据手机号查找用户
	 * @param mobile
	 * @return
	 */
    CenterUser selectByMobile(String mobile);
    
    /**
     * 查找第三方用户ID
     * @param userId
     * @param thirdPlatform
     * @return
     */
    String findBindId(@Param("userId")int userId, @Param("thirdPlatform")int thirdPlatform);
}