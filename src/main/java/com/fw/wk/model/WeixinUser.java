package com.fw.wk.model;

import java.util.Date;

import net.sf.json.JSONObject;

public class WeixinUser {
	
	private Integer id;
	
	private String nickname;
	
	private String headImgUrl;
	
	private String wxid;
	
	private String phone;
	
	private String realName;
	
	private Integer status;
	
	private Integer sex;
	
	private String city;
	
	private String country;
	
	private String province;
	
	private String unionid;
	
	private String remark;
	
	private Date create_time;
	
	public WeixinUser() {
		
	}

	public WeixinUser(JSONObject json) {
		this.nickname = json.getString("nickname");
		this.wxid = json.getString("openid");
		this.sex = json.getInt("sex");
		this.city = json.getString("city");
		this.country = json.getString("country");
		this.province = json.getString("province");
		this.headImgUrl = json.getString("headimgurl");
		this.remark =json.getString("unionid");
		this.unionid = json.getString("unionid");
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname==null?"":nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadImgUrl() {
		return headImgUrl==null?"":headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public String getWxid() {
		return wxid==null?"":wxid;
	}

	public void setWxid(String wxid) {
		this.wxid = wxid;
	}

	public String getPhone() {
		return phone==null?"":phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRealName() {
		return realName==null?"":realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Integer getStatus() {
		return status==null?0:status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSex() {
		return sex==null?0:sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getCity() {
		return city==null?"":city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country==null?"":country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province==null?"":province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getUnionid() {
		return unionid==null?"":unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public String getRemark() {
		return remark==null?"":remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
}
