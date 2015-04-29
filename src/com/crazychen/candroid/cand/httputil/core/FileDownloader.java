package com.crazychen.candroid.cand.httputil.core;

import java.util.LinkedList;

import android.util.Log;

import com.crazychen.candroid.cand.httputil.request.FileRequest;

public class FileDownloader {
	
	//任务队列
	private final RequestQueue mRequestQueue;
	
	//暂停队列
	private final LinkedList<FileRequest> mPauseQueue;
	
	public FileDownloader(RequestQueue queue) {
		mRequestQueue = queue;		
		mPauseQueue = new LinkedList<FileRequest>();		
	}

	public void onCancel(FileRequest req){
		req.cancel();
	}
	
	public void onPause(FileRequest req){
		mPauseQueue.add(req);
		req.cancel();		
	}
	
	public void onRestart(FileRequest req){
		if(!req.isCanceled()){
			Log.d("###", "already start");
			return;
		}
		mRequestQueue.addRequest(req);
		mPauseQueue.remove();
	}	
}
