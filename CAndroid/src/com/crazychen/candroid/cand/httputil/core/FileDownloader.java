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
	/*
	public DownloadController add(String storeFilePath, String url, Listener<Void> listener) {
		// only fulfill requests that were initiated from the main thread.(reason for the Delivery?)
		throwIfNotOnMainThread();

		DownloadController controller = new DownloadController(storeFilePath, url, listener);
		synchronized (mTaskQueue) {
			mTaskQueue.add(controller);
		}
		schedule();
		return controller;
	}

	
	public DownloadController get(String storeFilePath, String url) {
		synchronized (mTaskQueue) {
			for (DownloadController controller : mTaskQueue) {
				if (controller.mStoreFilePath.equals(storeFilePath) &&
						controller.mUrl.equals(url)) return controller;
			}
		}
		return null;
	}


	private void schedule() {
		// make sure only one thread can manipulate the Task Queue.
		synchronized (mTaskQueue) {
			// counting ran task.
			int parallelTaskCount = 0;
			for (DownloadController controller : mTaskQueue) {
				if (controller.isDownloading()) parallelTaskCount++;
			}
			if (parallelTaskCount >= mParallelTaskCount) return;

			// try to deploy all Task if they're await.
			for (DownloadController controller : mTaskQueue) {
				if (controller.deploy() && ++parallelTaskCount == mParallelTaskCount) return;
			}
		}
	}


	private void remove(DownloadController controller) {
		// also make sure one thread operation
		synchronized (mTaskQueue) {
			mTaskQueue.remove(controller);
		}
		schedule();
	}

	
	public void clearAll() {
		// make sure only one thread can manipulate the Task Queue.
		synchronized (mTaskQueue) {
			while (mTaskQueue.size() > 0) {
				mTaskQueue.get(0).discard();
			}
		}
	}

	private void throwIfNotOnMainThread() {
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new IllegalStateException("FileDownloader must be invoked from the main thread.");
		}
	}


	public FileDownloadRequest buildRequest(String storeFilePath, String url) {
		return new FileDownloadRequest(storeFilePath, url);
	}

	public class DownloadController {
		// Persist the Request createing params for re-create it when pause operation gone.
		private Listener<Void> mListener;
		private String mStoreFilePath;
		private String mUrl;

		// The download request.
		private FileDownloadRequest mRequest;

		private int mStatus;
		public static final int STATUS_WAITING = 0;
		public static final int STATUS_DOWNLOADING = 1;
		public static final int STATUS_PAUSE = 2;
		public static final int STATUS_SUCCESS = 3;
		public static final int STATUS_DISCARD = 4;

		private DownloadController(String storeFilePath, String url, Listener<Void> listener) {
			mStoreFilePath = storeFilePath;
			mListener = listener;
			mUrl = url;
		}

		
		private boolean deploy() {
			if (mStatus != STATUS_WAITING) return false;

			mRequest = buildRequest(mStoreFilePath, mUrl);

			// we create a Listener to wrapping that Listener which developer specified,
			// for the onFinish(), onSuccess(), onError() won't call when request was cancel reason.
			mRequest.setListener(new Listener<Void>() {
				boolean isCanceled;

				@Override
				public void onPreExecute() {
					mListener.onPreExecute();
				}

				@Override
				public void onFinish() {
					// we don't inform FINISH when it was cancel.
					if (!isCanceled) {
						mStatus = STATUS_SUCCESS;
						mListener.onFinish();
						// when request was FINISH, remove the task and re-schedule Task Queue.
						remove(DownloadController.this);
					}
				}

				@Override
				public void onSuccess(Void response) {
					// we don't inform SUCCESS when it was cancel.
					if (!isCanceled) mListener.onSuccess(response);
				}

				@Override
				public void onError(NetroidError error) {
					// we don't inform ERROR when it was cancel.
					if (!isCanceled) mListener.onError(error);
				}

				@Override
				public void onCancel() {
					mListener.onCancel();
					isCanceled = true;
				}

				@Override
				public void onProgressChange(long fileSize, long downloadedSize) {
					mListener.onProgressChange(fileSize, downloadedSize);
				}
			});

			mStatus = STATUS_DOWNLOADING;
			mRequestQueue.add(mRequest);
			return true;
		}

		public int getStatus() {
			return mStatus;
		}

		public boolean isDownloading() {
			return mStatus == STATUS_DOWNLOADING;
		}
	
		public boolean pause() {
			if (mStatus == STATUS_DOWNLOADING) {
				mStatus = STATUS_PAUSE;
				mRequest.cancel();
				schedule();
				return true;
			}
			return false;
		}
	
		public boolean resume() {
			if (mStatus == STATUS_PAUSE) {
				mStatus = STATUS_WAITING;
				schedule();
				return true;
			}
			return false;
		}
	
		public boolean discard() {
			if (mStatus == STATUS_DISCARD) return false;
			if (mStatus == STATUS_SUCCESS) return false;
			if (mStatus == STATUS_DOWNLOADING) mRequest.cancel();
			mStatus = STATUS_DISCARD;
			remove(this);
			return true;
		}
	}
	*/
}
