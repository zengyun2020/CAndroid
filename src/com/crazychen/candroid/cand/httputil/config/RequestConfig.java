package com.crazychen.candroid.cand.httputil.config;

public class RequestConfig {
	public String userAgent = "default";
    public int soTimeOut = 10000;
    public int connTimeOut = 10000;
	public RequestConfig(String userAgent, int soTimeOut, int connTimeOut) {
		super();
		this.userAgent = userAgent;
		this.soTimeOut = soTimeOut;
		this.connTimeOut = connTimeOut;
	}        
	
	public RequestConfig() {
		
	}
}
