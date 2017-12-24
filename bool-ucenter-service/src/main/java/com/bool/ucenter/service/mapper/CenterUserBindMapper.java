package com.bool.ucenter.service.mapper;

import com.bool.ucenter.service.entity.CenterUserBind;
import com.bool.ucenter.service.entity.CenterUserBindKey;

public interface CenterUserBindMapper {
    int deleteByPrimaryKey(CenterUserBindKey key);

    int insert(CenterUserBind record);

    int insertSelective(CenterUserBind record);

    CenterUserBind selectByPrimaryKey(CenterUserBindKey key);

    int updateByPrimaryKeySelective(CenterUserBind record);

    int updateByPrimaryKey(CenterUserBind record);
}