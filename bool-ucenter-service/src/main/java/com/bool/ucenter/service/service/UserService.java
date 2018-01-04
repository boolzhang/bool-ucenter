package com.bool.ucenter.service.service;

import com.bool.ucenter.core.entity.CenterUser;

public interface UserService {

	CenterUser findByMobile(String mobile);
}
