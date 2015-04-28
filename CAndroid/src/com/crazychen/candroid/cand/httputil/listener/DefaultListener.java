package com.crazychen.candroid.cand.httputil.listener;

import com.crazychen.candroid.cand.httputil.base.Listener;

public abstract class DefaultListener<T> extends Listener<T>{

	@Override
	public void onPreExecute() {}

	@Override
	public abstract void onSuccess(int stCode, T response, String errMsg);

	@Override
	public abstract void onFail(int stCode, T response, String errMsg);

	@Override
	public void onCancel() {}
	
}
