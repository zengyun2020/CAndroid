package com.crazychen.candroid.cand.httputil.base;

public abstract class Listener<T> {

	public abstract void onPreExecute();
	
	public abstract void onSuccess(int stCode, T response, String errMsg);
	
	public abstract void onFail(int stCode, T response, String errMsg);
			
	public abstract void onCancel();		
}