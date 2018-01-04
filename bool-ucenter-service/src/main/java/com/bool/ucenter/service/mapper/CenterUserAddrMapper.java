package com.bool.ucenter.service.mapper;

import com.bool.ucenter.core.entity.CenterUserAddr;

public interface CenterUserAddrMapper {
    int deleteByPrimaryKey(Integer addrId);

    int insert(CenterUserAddr record);

    int insertSelective(CenterUserAddr record);

    CenterUserAddr selectByPrimaryKey(Integer addrId);

    int updateByPrimaryKeySelective(CenterUserAddr record);

    int updateByPrimaryKey(CenterUserAddr record);
}