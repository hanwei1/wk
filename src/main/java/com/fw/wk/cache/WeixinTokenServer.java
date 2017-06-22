package com.fw.wk.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import net.sf.json.JSONObject;

import com.fw.wk.utils.HttpClientUtil;
@Service
public class WeixinTokenServer {
	
	private static String APPID = "wxcc904ce28f780e85";
	private static String APPSECRET = "5e93f14e669c060eda79d379aa58a43e";
	/**
	 * 获取AccessToken,7200秒过期
	 * @return
	 */
	@Cacheable(value="cacheToken", key="'WK_ACCESS_TOKEN'")
	public  String getAccessToken(){
		return JSONObject.fromObject( 
				HttpClientUtil.doGet( 
						"https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+APPID+"&secret="+APPSECRET
						) 
				).get("access_token").toString();
	}
}
