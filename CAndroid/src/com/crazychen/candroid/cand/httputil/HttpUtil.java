package com.crazychen.candroid.cand.httputil;

import com.crazychen.candroid.cand.httputil.base.Listener;
import com.crazychen.candroid.cand.httputil.base.Request;
import com.crazychen.candroid.cand.httputil.core.RequestQueue;
import com.crazychen.candroid.cand.httputil.request.StringRequest;


public class HttpUtil {
	public static RequestQueue mqueue = new RequestQueue(RequestQueue.DEFAULT_CORE_NUMS,null);
	public HttpUtil() {}		
	
	public void getString(Request.HttpMethod method,String hostUrl,
			Listener<String> listener,String... params){
		Request<String> req = new StringRequest(method,hostUrl,listener);
		req.setParams(params);
		mqueue.addRequest(req);		
	}
	
	public <T> void get(Request<T> req){
		mqueue.addRequest(req);
	}
	
	public void getJson(Request.HttpMethod method,Listener<HttpUtil> listener,String... params){
		
	}
}
