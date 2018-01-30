package com.bool.ucenter.service.mapper;

import com.bool.ucenter.core.entity.CenterUserBind;
import com.bool.ucenter.core.entity.CenterUserBindKey;

public interface CenterUserBindMapper {
    int deleteByPrimaryKey(CenterUserBindKey key);

    int insert(CenterUserBind record);

    int insertSelective(CenterUserBind record);

    CenterUserBind selectByPrimaryKey(CenterUserBindKey key);

    int updateByPrimaryKeySelective(CenterUserBind record);

    int updateByPrimaryKey(CenterUserBind record);
}