package com.bool.ucenter.service.mapper.ext;

import java.util.List;

import com.bool.ucenter.core.entity.CenterUserAddr;

public interface CenterUserAddrExtMapper {
	
	/**
	 * ���ҵ�ַ�б�
	 * @param userId
	 * @return
	 */
    List<CenterUserAddr> findAddrList(int userId);
    
    
    /**
     * ����Ĭ�ϵ�ַ
     * @param userId
     * @return
     */
    CenterUserAddr findDefault(int userId);
    
    /**
     * ȡ��Ĭ�ϵ�ַ
     * @param userId
     */
    void clearDefault(int userId);

}