package com.crazychen.candroid.cand.httputil.core;

import java.util.concurrent.BlockingQueue;

import android.util.Log;

import com.crazychen.candroid.cand.httputil.base.Listener;
import com.crazychen.candroid.cand.httputil.base.Request;
import com.crazychen.candroid.cand.httputil.base.Response;
import com.crazychen.candroid.cand.httputil.cache.CacheManager;
import com.crazychen.candroid.cand.httputil.httpstack.HttpStack;

/**
 * ��������Executor,�̳���Thread,���������������ѭ����ȡ������ִ��
 * 
 */
final class NetworkExecutor extends Thread {    
    //�����������    
    private BlockingQueue<Request<?>> mRequestQueue;    
    //��������ջ    
    private HttpStack mHttpStack;    
    //����ַ���,�����Ͷ�ݵ����߳�    
    private static ResponseDelivery mResponseDelivery = new ResponseDelivery();     
    //�Ƿ�ֹͣ    
    private boolean isStop = false;
    //���󻺴� 
    private static CacheManager cacheManager = new CacheManager();
    
    public NetworkExecutor(BlockingQueue<Request<?>> queue, HttpStack httpStack) {
        mRequestQueue = queue;
        mHttpStack = httpStack;
    }

    @Override
    public void run() {
        try {
            while (!isStop) {
                final Request<?> request = mRequestQueue.take();   
                Listener<?> reqListener = null;
                if (request.getRequestListener()!=null){
                	reqListener = request.getRequestListener();
                }
                if (request.isCanceled()) {
                	if (reqListener != null) {
                		reqListener.onCancel();
                	}
                    Log.d("###", "### ȡ��ִ����");
                    continue;
                }
                Response response = null;
                if (reqListener != null) {
            		reqListener.onPreExecute();
            	}               
                if (isUseCache(request)) {
                    // �ӻ�����ȡ
                    response = cacheManager.get(request.getUrl());
                } else {
                    // �������ϻ�ȡ����
                    response = mHttpStack.performRequest(request);                   
                    // �����������Ҫ����,��ô����ɹ��򻺴浽mResponseCache��
                    if (request.shouldCache() && isSuccess(response)) {
                    	cacheManager.put(request.getUrl(), response);
                    }
                }                
                // �ַ�������
                mResponseDelivery.deliveryResponse(request, response);
            }
        } catch (InterruptedException e) {
            Log.i("###", "### ����ַ����˳�");
        }
    }

    private boolean isSuccess(Response response) {
        return response != null && response.getStatusCode() == 200;
    }

    private boolean isUseCache(Request<?> request) {
        return request.shouldCache() && cacheManager.get(request.getUrl()) != null;
    }

    public void quit() {
        isStop = true;
        interrupt();
    }
}
