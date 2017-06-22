package com.fw.wk.common;

import net.sf.json.JSONObject;

import com.fw.wk.utils.HttpClientUtil;

public class WeixinTokenServer {
	
	private static String APPID = "wxcc904ce28f780e85";
	private static String APPSECRET = "5e93f14e669c060eda79d379aa58a43e";
	
	/**
	 * 获取AccessToken,7200秒过期
	 * @return
	 */
	public static JSONObject getAccessToken(){
		return JSONObject.fromObject( 
				HttpClientUtil.doGet( 
						"https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+APPID+"&secret="+APPSECRET
						) 
				);
	}
}
