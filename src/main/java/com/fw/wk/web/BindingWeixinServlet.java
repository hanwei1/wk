package com.fw.wk.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fw.wk.cache.WeixinTokenServer;
import com.fw.wk.common.TextMessage;
import com.fw.wk.model.WeixinUser;
import com.fw.wk.service.WeixinService;
import com.fw.wk.utils.MessageUtil;
@Controller
public class BindingWeixinServlet extends HttpServlet {
	private static final long serialVersionUID = -7702896929638469882L;

	private Logger logger = Logger.getLogger( getClass() );

	// 与接口配置信息中的Token要一致
	private static String token = "jifenwall";  

	@Autowired
	private WeixinTokenServer tokenServer;
	@Autowired
	private WeixinService weixin;

	 
	/**
	 * 确认请求来自微信服务器
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 微信加密签名
		String signature = request.getParameter("signature"); 
		if( signature == null  )
			return;
		PrintWriter out = response.getWriter();
		logger.info(request.getParameter("signature") );
		logger.info(request.getParameter("timestamp") );
		logger.info(request.getParameter("nonce") );
		logger.info(request.getParameter("echostr") );
		// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
		if ( checkSignature(signature, request.getParameter("timestamp"), request.getParameter("nonce"))) {
			out.print(  request.getParameter("echostr") ); 
		}
		out.flush();
		out.close();
		out = null;
	}



	/**
	 * 处理微信服务器发来的消息
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 将请求、响应的编码均设置为UTF-8（防止中文乱码）  
		request.setCharacterEncoding("UTF-8");  
		response.setCharacterEncoding("UTF-8");  
		// 调用核心业务类接收消息、处理消息  
		PrintWriter out = response.getWriter();  
		out.print( processRequest(request));  
		out.flush();
		out.close();  
	}




	/**
	 * 验证签名
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	public static boolean checkSignature(String signature, String timestamp, String nonce) {
		String[] arr = new String[] { token, timestamp, nonce };
		Arrays.sort(arr);  
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			content.append(arr[i]);
		}
		MessageDigest md = null;
		String tmpStr = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] digest = md.digest(content.toString().getBytes());
			tmpStr = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		content = null;
		return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
	}



	/**
	 * 将字节数组转换为十六进制字符串
	 * @param byteArray
	 * @return
	 */
	private static String byteToStr(byte[] byteArray) {
		String strDigest = "";
		for (int i = 0; i < byteArray.length; i++) {
			strDigest += byteToHexStr(byteArray[i]);
		}
		return strDigest;
	}




	/**
	 * 将字节转换为十六进制字符串
	 * @param mByte
	 * @return
	 */
	private static String byteToHexStr(byte mByte) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = Digit[mByte & 0X0F];
		String s = new String(tempArr);
		return s;
	}

	/**
	 * 处理微信发来的请求
	 * 
	 * @param request
	 * @return
	 */
	public String processRequest(HttpServletRequest request) {

		String respMessage  = null;
		Map<String, String> requestMap = null;
		try {
			requestMap = MessageUtil.parseXml(request);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		String fromUserName = requestMap.get("FromUserName"); // 发送方帐号（open_id）
		String msgType      = requestMap.get("MsgType");      // 消息类型
		String toUserName   = requestMap.get("ToUserName");   // 公众帐号
		weixin.megerUser( fromUserName );
		
		if(MessageUtil.REQ_MESSAGE_TYPE_EVENT.equals(msgType)){
			String eventType = requestMap.get("Event");
			TextMessage textMessage = new TextMessage();
			if(MessageUtil.EVENT_TYPE_SUBSCRIBE.equals(eventType)){
				textMessage.setToUserName(fromUserName);
				textMessage.setFromUserName(toUserName);
				textMessage.setCreateTime(new Date().getTime());
				textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
				textMessage.setFuncFlag(0);
				textMessage.setContent( "大人您终于来了，小白在此恭候多时了!\r\t " );
				respMessage = MessageUtil.textMessageToXml(textMessage);
			}else if(MessageUtil.EVENT_TYPE_UNSUBSCRIBE.equals(eventType)){
				textMessage.setToUserName(fromUserName);
				textMessage.setFromUserName(toUserName);
				textMessage.setCreateTime(new Date().getTime());
				textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
				textMessage.setFuncFlag(0);
				textMessage.setContent( "大人您走好！" );
				respMessage = MessageUtil.textMessageToXml(textMessage);
			}
		}
	
		return respMessage;
	}


	/**
	 * MD5加密算法
	 * 
	 * 说明：32位加密算法
	 * 
	 * @param 待加密的数据
	 * @return 加密结果，全小写的字符串
	 */
	public static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] btInput = s.getBytes("utf-8");
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
