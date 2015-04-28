package com.crazychen.candroid.cand.httputil.cache;

import java.io.Serializable;

public class ResponeCache implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public byte[] rawData = new byte[0];
	public int statusCode = 200;
	public String msg;
	
	
	public byte[] getRawData() {
		return rawData;
	}

	public void setRawData(byte[] rawData) {
		this.rawData = rawData;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public ResponeCache(byte[] rawData, int statusCode, String msg) {
		super();
		this.rawData = rawData;
		this.statusCode = statusCode;
		this.msg = msg;
	}	
}
