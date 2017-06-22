package com.fw.wk.utils;

import java.io.IOException;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class HttpClientUtil {	
	public static String doGet(String url) {
		// 构造HttpClient的实例
		HttpClient httpClient = new HttpClient(); // 创建GET方法的实例
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(8000);
		GetMethod getMethod = new GetMethod(url); 
		// 使用系统提供的默认的恢复策略
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler());
		getMethod.addRequestHeader("Content-type" , "text/html; charset=utf-8"); 
		getMethod.getParams().setContentCharset("utf-8");
		String result = null;
		try {
			// 执行getMethod
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode == HttpStatus.SC_OK) {
				result=new String(getMethod.getResponseBodyAsString());
			}
		} catch (HttpException e) {
			// 发生致命的异常，可能是协议不对或者返回的内容有问题
			e.printStackTrace();
		} catch (IOException e) {
			// 发生网络异常
			e.printStackTrace();
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
		return result;
	}



	public static String postMethod(String url,NameValuePair[] param){ 
		try{
			PostMethod post = new PostMethod(url);
			post.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8"); 
			post.setRequestBody(param);
			HttpClient httpClient = new HttpClient();   
			int statusCode = httpClient.executeMethod(post); 
			if (statusCode == HttpStatus.SC_OK) {
				String response =  new String(post.getResponseBodyAsString().getBytes("utf-8"));
				post.releaseConnection();
				return response;
			} else {
				post.releaseConnection();
				return null;
			}
		}catch(Exception e){
			e.getStackTrace();
			return null;
		}
	}
	
	public static String doPost(String url,NameValuePair[] param){ 
		try{
			PostMethod post = new PostMethod(url);
			post.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=utf-8"); 
			post.setRequestBody(param);
			HttpClient httpClient = new HttpClient();   
			int statusCode = httpClient.executeMethod(post); 
			if (statusCode == HttpStatus.SC_OK) {
				String response =  new String(post.getResponseBodyAsString().getBytes("utf-8"));
				post.releaseConnection();
				return response;
			} else {
				post.releaseConnection();
				return null;
			}
		}catch(Exception e){
			e.getStackTrace();
			return null;
		}
	}
}
