package com.crazychen.candroid.cand.httputil.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

import com.crazychen.candroid.cand.httputil.base.Request;
import com.crazychen.candroid.cand.httputil.httpstack.HttpStack;
import com.crazychen.candroid.cand.httputil.httpstack.HttpStackFactory;


/**
 * �������, ʹ�����ȶ���,ʹ��������԰������ȼ����д���. [ Thread Safe ] 
 */
public final class RequestQueue {   
    //�������
    private BlockingQueue<Request<?>> mRequestQueue = new PriorityBlockingQueue<Request<?>>();
    //��������л�������     
    private AtomicInteger mSerialNumGenerator = new AtomicInteger(0);    
    //Ĭ�ϵĺ�����     
    public static int DEFAULT_CORE_NUMS = Runtime.getRuntime().availableProcessors() + 1;  
    //CPU������ + 1���ַ��߳���     
    private int mDispatcherNums = DEFAULT_CORE_NUMS;    
    //NetworkExecutor,ִ������������߳�    
    private NetworkExecutor[] mDispatchers = null;    
    //Http���������ִ����    
    private HttpStack mHttpStack;

    /**
     * @param coreNums �̺߳�����
     * @param httpStack httpִ����
     */
    public RequestQueue(int coreNums, HttpStack httpStack) {
        mDispatcherNums = coreNums;
        mHttpStack = httpStack != null ? httpStack : HttpStackFactory.createHttpStack();
    }

    /**
     * ����NetworkExecutor
     */
    private final void startNetworkExecutors() {
        mDispatchers = new NetworkExecutor[mDispatcherNums];
        for (int i = 0; i < mDispatcherNums; i++) {
            mDispatchers[i] = new NetworkExecutor(mRequestQueue, mHttpStack);
            mDispatchers[i].start();
        }
    }

    public void start() {
        stop();
        startNetworkExecutors();
    }

    /**
     * ֹͣNetworkExecutor
     */
    public void stop() {
        if (mDispatchers != null && mDispatchers.length > 0) {
            for (int i = 0; i < mDispatchers.length; i++) {
                mDispatchers[i].quit();
            }
        }
    }

    /**
     * �����ظ��������
     * 
     * @param request
     */
    public void addRequest(Request<?> request) {
        if (!mRequestQueue.contains(request)) {
            request.setSerialNumber(this.generateSerialNumber());
            mRequestQueue.add(request);
        } else {        	
            Log.d("", "### ����������Ѿ�����");
        }
    }

    public void clear() {
        mRequestQueue.clear();
    }

    public BlockingQueue<Request<?>> getAllRequests() {
        return mRequestQueue;
    }

    /**
     * Ϊÿ����������һ��ϵ�к�
     * 
     * @return ���к�
     */
    private int generateSerialNumber() {
        return mSerialNumGenerator.incrementAndGet();
    }
	
}