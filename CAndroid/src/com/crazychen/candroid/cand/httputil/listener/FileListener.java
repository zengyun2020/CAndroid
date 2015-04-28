package com.crazychen.candroid.cand.httputil.listener;

import com.crazychen.candroid.cand.httputil.base.Listener;

public abstract class FileListener<T> extends Listener<T>{
	
	public abstract void onProgressChange(long fileSize, long downloadedSize);
}
