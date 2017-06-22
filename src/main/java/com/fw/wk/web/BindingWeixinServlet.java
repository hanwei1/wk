package com.fw.wk.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

import com.fw.wk.utils.MessageUtil;
@Controller
public class BindingWeixinServlet extends HttpServlet {
	private static final long serialVersionUID = -7702896929638469882L;

	private Logger logger = Logger.getLogger( getClass() );

	// 与接口配置信息中的Token要一致
	private static String token = "jifenwall";  

	//private WeixinBlogManagerIFace  iface = new WeixinBlogManagerIFaceImpl(); 

	 
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
	 * 获取用户信息
	 * @return
	 */
/*	public JSONObject getUserInfo( String openid ){
		return JSONObject.fromObject( 
				httpClient.doGet( 
						"https://api.weixin.qq.com/cgi-bin/user/info?access_token=&openid="+openid+"&lang=zh_CN"
						) 
				);
	}*/






//	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
		/*String fromUserName = requestMap.get("FromUserName"); // 发送方帐号（open_id）
		String msgType      = requestMap.get("MsgType");      // 消息类型
		String toUserName   = requestMap.get("ToUserName");   // 公众帐号
		Weixin_blog_user bloguser = iface.getWeixinUserByWid( fromUserName );
		Weixin_blog_user_msg msg = new Weixin_blog_user_msg();
		msg.setWxid( fromUserName );
		msg.setMsgtype( msgType );
		msg.setCreatetime( R.getTime() );

		TextMessage textMessage = new TextMessage();
		textMessage.setToUserName(fromUserName);
		textMessage.setFromUserName(toUserName);
		textMessage.setCreateTime(new Date().getTime());
		textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
		textMessage.setFuncFlag(0);
		textMessage.setContent( "您好亲！小瑜正在回复的路上了，hold不住特别需要和我私聊也可以加我，微信号：keepyoga001" );
		respMessage = MessageUtil.textMessageToXml(textMessage);

		if( msgType.equals("text") ) {   // 文本
			msg.setContent( requestMap.get("Content").trim() );
			
			if(System.currentTimeMillis() < 1476460799000l && msg.getContent().startsWith("818818") && msg.getContent().length() > 7){
				String realName = msg.getContent().substring(6);
				System.out.println("start create zhengshu picture , name = " + realName);
				WeixinBlogManagerIFaceImpl weixin = new WeixinBlogManagerIFaceImpl();
				Weixin_blog_user user = weixin.getWeixinUserByWid(fromUserName);
				user.setRealname(realName);
				weixin.addOrUpdateWeixinUser(user);
				int uid = user.getId();
				String no = "";
				if(uid < 10){
					no = "Y0000" + uid;
				}else if(uid < 100){
					no = "Y000" + uid;
				}else if(uid < 1000){
					no = "Y00" + uid;
				}else if(uid < 10000){
					no = "Y0" + uid;
				}else{
					no = "Y" + uid;
				}
				NameValuePair[] pargms = {new NameValuePair("zsno", no),new NameValuePair("zsname", user.getRealname())};
				String res = httpClient.doPost("http://wx.keepyoga.com/weixin/createzs.do", pargms);
				if("exists".equals(res)){
					textMessage = new TextMessage();
					textMessage.setToUserName(fromUserName);
					textMessage.setFromUserName(toUserName);
					textMessage.setCreateTime(new Date().getTime());
					textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
					textMessage.setFuncFlag(0);
					textMessage.setContent( "您好亲！您已经生成过一次证书了，不能再次生成哦！" );
					respMessage = MessageUtil.textMessageToXml(textMessage);
					return respMessage;
				}
				String url = Config.getString("domain") + "/w/faq/index.html";
				if(user != null){
					url = Config.getString("domain") + "/weixin/midautumnzs/"+user.getId()+".html?imgurl=" + res ;
					
				}
				NewsMessage newMessage = new NewsMessage();
				newMessage.setToUserName( fromUserName );
				newMessage.setFromUserName(toUserName);
				newMessage.setCreateTime(new Date().getTime());
				newMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
				List<Article> articles = new ArrayList<Article>();
				Article article = new Article();
				article.setPicUrl(  "http://wimg.keepyoga.com/734394356101245431.jpg");
				article.setTitle( "您参与的理疗集训营的学习证明到啦!" );
				article.setUrl( url );
				article.setDescription( "" );
				articles.add( article );
				newMessage.setArticleCount(articles.size());
				newMessage.setArticles( articles );					
				respMessage= MessageUtil.newsMessageToXml(newMessage);
				return respMessage;
			}
			
			// 关键词匹配，看是否返回关键词对应的内容列表 
			HashMap<String,Weixin_blog_keyword> keyword = iface.getWeixinBlogByCache();

			if( keyword != null && keyword.containsKey( msg.getContent() ) ) {
				Weixin_blog_keyword key = keyword.get( msg.getContent()  );

				if( key.getCtype() == 1 ) {
					List<HashMap<String,Object>> list = iface.getWeixinCList( keyword.get( msg.getContent() ).getId()+"" );
					if( list != null && list.size() > 0 ) {
						NewsMessage newMessage = new NewsMessage();
						newMessage.setToUserName( fromUserName );
						newMessage.setFromUserName(toUserName);
						newMessage.setCreateTime(new Date().getTime());
						newMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
						List<Article> articles = new ArrayList<Article>();
						for(  HashMap<String,Object> map : list ){
							Article article = new Article();
							article.setPicUrl(  R.CDNDOMAIN +"/"+ R.getCdnPath( map.get("img")+"") );
							article.setTitle(  map.get("title")+"");
							article.setUrl( map.get("url")+"");
							article.setDescription( map.get("title")+"");
							articles.add( article );
						}
						newMessage.setArticleCount(articles.size());
						newMessage.setArticles( articles );
						respMessage = MessageUtil.newsMessageToXml(newMessage);
					}
				}
				if( key.getCtype() == 2 ) {
					textMessage = new TextMessage();
					textMessage.setToUserName(fromUserName);
					textMessage.setFromUserName(toUserName);
					textMessage.setCreateTime(new Date().getTime());
					textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
					textMessage.setFuncFlag(0);
					textMessage.setContent(  key.getContent()  );
					respMessage = MessageUtil.textMessageToXml(textMessage);
				}
			}
			
		}else if(msgType.equals("image")){  // 图片
			msg.setPicurl( requestMap.get("PicUrl") );
		}else if(msgType.equals("location")){  // 坐标
			msg.setLabel( requestMap.get("Label")  );
			msg.setScale( requestMap.get("Scale"));
			msg.setLocation_x( requestMap.get("Location_X")  );
			msg.setLocation_y( requestMap.get("Location_Y")  );
		}else if(msgType.equals("shortvideo")){  // 短视频
			msg.setMediaid( requestMap.get("MediaId") );
			msg.setThumbmediaid(requestMap.get("ThumbMediaId") );
		}else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
			String eventType = requestMap.get("Event");
			// 订阅
			if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {  // 此刻窗外春风十里，瑜是乎，咱们扎堆聊聊瑜伽那些事儿~
				textMessage.setToUserName(fromUserName);
				textMessage.setFromUserName(toUserName);
				textMessage.setCreateTime(new Date().getTime());
				textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
				textMessage.setFuncFlag(0);
				textMessage.setContent( "欢迎来到 “瑜是乎”，来了就不想要你走。\r\t「瑜是乎」是一个名师聚集的在线学习平台，这里有你最想听的瑜伽经管课程，有你最想看的瑜伽教学视频，帮你快速提高管理能力和教学技能。\r\t「瑜是乎」是值得付费的语音问答社区，60秒语音问答让知识直接变现，你问我答，有料的名师在此等你来。\r\t http://weixin.keepyoga.com/w/faq/index.html; \r\t 点击链接就可进入瑜是乎社区。\r\t 如果其他问题咨询请加客服微信:keepyoga001 " );
				respMessage = MessageUtil.textMessageToXml(textMessage);
				
				
				 分销活动发送通知   begin 
				if(System.currentTimeMillis() < 1479916799000l){
					WeixinManagerIFaceImpl manager = new WeixinManagerIFaceImpl();
					HashMap<String, Object> signup = manager.getSignupByWxid(fromUserName);
					if(signup != null && signup.size() > 0){
						NewsMessage newMessage = new NewsMessage();
						newMessage.setToUserName( fromUserName );
						newMessage.setFromUserName(toUserName);
						newMessage.setCreateTime(new Date().getTime());
						newMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
						List<Article> articles = new ArrayList<Article>();
						Article article = new Article();
						article.setPicUrl(  "http://wimg.keepyoga.com/318657995096718647.jpg" );
						article.setTitle( "您已成功购买瑜伽私教线上集训营课程！" );
						article.setUrl( Config.getString("domain") + "/weixin/jieshao.html" );
						article.setDescription( "点击下方“详情”，查看听课与提问参与方式；同时，请留意瑜是乎平台的上课通知！" );
						articles.add( article );
						newMessage.setArticleCount(articles.size());
						newMessage.setArticles( articles );					
						respMessage= MessageUtil.newsMessageToXml(newMessage);
						
					}
					
				}
				 分销活动发送通知   end  
				
				*//** 中秋活动关注特约通知 begin 
				WeixinManagerIFaceImpl manager = new WeixinManagerIFaceImpl();
				List<String> list = manager.getVoteFromByWxid(fromUserName);
				NewsMessage newMessage = new NewsMessage();
				newMessage.setToUserName( fromUserName );
				newMessage.setFromUserName(toUserName);
				newMessage.setCreateTime(new Date().getTime());
				newMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
				List<Article> articles = new ArrayList<Article>();
				for(String uid : list){
					Weixin_blog_user u = null;
					try {
						u = new Weixin_blog_user(Integer.parseInt(uid));
						u.load();
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(u == null){
						continue;
					}
					Article article = new Article();
					article.setPicUrl(  "http://wimg.keepyoga.com/690948651966212905.jpg" );
					article.setTitle( "给" + u.getNickname() + "投票：中秋豪礼-瑜伽重磅课程免费送" );
					article.setUrl( Config.getString("domain") + "/weixin/"+uid+"/midautumn.html" );
					article.setDescription( "发起人：" + u.getNickname() );
					articles.add( article );
					logger.info("------weixin uid:"+uid);
					newMessage.setArticleCount(articles.size());
					newMessage.setArticles( articles );					
					respMessage= MessageUtil.newsMessageToXml(newMessage);
					break;
				}				
				 中秋活动关注特约通知 end **//*
				
			} else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
				if( bloguser != null ) {
					bloguser.setCancelfollow(1);
					bloguser.setCanceltime(sdf.format( new Date() ));
					iface.addOrUpdateWeixinUser( bloguser );
					textMessage.setToUserName(fromUserName);
					textMessage.setFromUserName(toUserName);
					textMessage.setCreateTime(new Date().getTime());
					textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
					textMessage.setFuncFlag(0);
					textMessage.setContent( "亲，你要走了，我舍不得你，你还会回来么？" );
					respMessage = MessageUtil.textMessageToXml(textMessage);
				}
			} 
		}else
			msg = null;
		if( msg != null && !msg.getMsgtype().equals("event") ){
			R.insertMsg(msg);
		}*/
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
