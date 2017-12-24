package com.bool.ucenter.service.service;

import com.bool.ucenter.service.entity.CenterUser;

public interface UserService {

	CenterUser findByMobile(String mobile);
}
