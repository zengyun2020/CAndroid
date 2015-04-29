package com.crazychen.candroid.cand.httputil.request;

import java.util.concurrent.TimeUnit;

import com.crazychen.candroid.cand.httputil.base.Listener;
import com.crazychen.candroid.cand.httputil.base.Request;
import com.crazychen.candroid.cand.httputil.base.Response;
import com.crazychen.candroid.cand.httputil.listener.FileListener;

public class FileRequest extends Request<String>{
	private String path;
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public FileRequest(String path,String url, FileListener<String> listener) {
		super(HttpMethod.GET, url, listener);		
		this.setCacheExpireTime(TimeUnit.MILLISECONDS, 0);
		this.path = path;
		setFile(false);
	}		

	public void onPause(){
		this.cancel();
	}
	
	@Override
	public String parseResponse(Response response) {		
		return null;
	}
}
