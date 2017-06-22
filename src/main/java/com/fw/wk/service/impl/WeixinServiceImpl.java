package com.fw.wk.service.impl;

import java.util.Date;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fw.wk.cache.WeixinTokenServer;
import com.fw.wk.dao.BaseDao;
import com.fw.wk.model.WeixinUser;
import com.fw.wk.service.WeixinService;
import com.fw.wk.utils.HttpClientUtil;
@Service
public class WeixinServiceImpl extends BaseDao implements WeixinService{
	private Logger logger = Logger.getLogger(getClass());
	@Autowired
	private WeixinTokenServer tokenServer;

	public int saveUser(WeixinUser user) {
		if(user == null){
			return 0;
		}
		String sql = "INSERT INTO weixin_user(nickname,head_img_url,wxid,phone,real_name,sex,city,country,province,unionid,remark,create_time)VALUES(?,?,?,?,?,?,?,?,?,?,?,now())";
		return jdbcTemplate.update(sql, user.getNickname(),
				user.getHeadImgUrl(),
				user.getWxid(),
				user.getPhone(),
				user.getRealName(),
				user.getSex(),
				user.getCity(),
				user.getCountry(),
				user.getProvince(),
				user.getUnionid(),
				user.getRemark());
	}
	
	public int updateUser(WeixinUser user){
		String sql = "UPDATE weixin_user SET nickname=?,head_img_url=?,sex=?,remark=? WHERE id=?";
		return jdbcTemplate.update(sql, user.getNickname(), user.getHeadImgUrl(), user.getSex(), user.getRemark(), user.getId());
	}

	public WeixinUser queryUserByWxid(String wxid) {
		if(wxid == null){
			return null;
		}
		String sql = "SELECT id,nickname,head_img_url,wxid,phone,real_name,status,sex,city,country,province,unionid,remark,create_time FROM weixin_user WHERE wxid=?";
		Map<String, Object> result = jdbcTemplate.queryForMap(sql, wxid);
		if(result != null && result.size() > 0){
			WeixinUser user = new WeixinUser();
			user.setId((Integer)result.get("id"));
			user.setNickname((String) result.get("nickname"));
			user.setHeadImgUrl((String) result.get("head_img_url"));
			user.setWxid((String) result.get("wxid"));
			user.setPhone((String) result.get("phone"));
			user.setRealName((String) result.get("real_name"));
			user.setStatus((Integer) result.get("status"));
			user.setSex((Integer) result.get("sex"));
			user.setCountry((String) result.get("country"));
			user.setProvince((String) result.get("province"));
			user.setUnionid((String) result.get("unionid"));
			user.setRemark((String) result.get("remark"));
			user.setCreate_time((Date) result.get("create_time"));
			return user;
		}
		return null;
	}

	public WeixinUser megerUser(String wxid) {
		WeixinUser netUser = getUserInfo(wxid);
		logger.info("User of Weixin is " + JSONObject.fromObject(netUser));
		if(netUser != null){
			WeixinUser dbUser = queryUserByWxid(wxid);
			if(dbUser == null){
				dbUser = netUser;
				saveUser(dbUser);
			}else{
				dbUser.setHeadImgUrl(netUser.getHeadImgUrl());
				dbUser.setNickname(netUser.getNickname());
				dbUser.setSex(netUser.getSex());
				dbUser.setRemark(netUser.getRemark());
				updateUser(dbUser);
			}
			logger.info("User of DB is " + JSONObject.fromObject(netUser));
			return dbUser;
		}
		
		return null;
	}
	
	/**
	 * 获取用户信息
	 * @return
	 */
	public WeixinUser getUserInfo( String openid ){
		JSONObject userJSON = JSONObject.fromObject( 
				HttpClientUtil.doGet( 
						"https://api.weixin.qq.com/cgi-bin/user/info?access_token="+tokenServer.getAccessToken() +"&openid="+openid+"&lang=zh_CN"
						) 
				);
		if(userJSON.getInt("errcode") == 40013){
			return null;
		}
		if(userJSON.getInt("subscribe") == 0){
			return null;
		}
		return new WeixinUser(userJSON);
	}

}
