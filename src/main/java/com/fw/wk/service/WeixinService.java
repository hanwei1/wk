package com.fw.wk.service;

import com.fw.wk.model.WeixinUser;

public interface WeixinService {

	int saveUser(WeixinUser user);
	
	WeixinUser queryUserByWxid(String wxid);

	WeixinUser megerUser(String wxid);
}
