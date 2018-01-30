package com.bool.ucenter.service.mapper;

import com.bool.ucenter.core.entity.CenterUser;

public interface CenterUserMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(CenterUser record);

    int insertSelective(CenterUser record);

    CenterUser selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(CenterUser record);

    int updateByPrimaryKey(CenterUser record);
}