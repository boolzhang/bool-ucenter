package com.bool.ucenter.service.mapper.ext;

import java.util.List;

import com.bool.ucenter.core.entity.CenterUserAddr;

public interface CenterUserAddrExtMapper {
	
	/**
	 * 查找地址列表
	 * @param userId
	 * @return
	 */
    List<CenterUserAddr> findAddrList(int userId);
    
    
    /**
     * 查找默认地址
     * @param userId
     * @return
     */
    CenterUserAddr findDefault(int userId);
    
    /**
     * 取消默认地址
     * @param userId
     */
    void clearDefault(int userId);

}