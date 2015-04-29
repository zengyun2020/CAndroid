package com.crazychen.candroid.cand.httputil.core;

import java.util.concurrent.Executor;

import android.os.Handler;
import android.os.Looper;

import com.crazychen.candroid.cand.httputil.base.Request;
import com.crazychen.candroid.cand.httputil.base.Response;

/**
 * ������Ͷ����,��������Ͷ�ݸ�UI�߳�
 * 
 */
class ResponseDelivery implements Executor {

    /**
     * ���̵߳�hander
     */
    Handler mResponseHandler = new Handler(Looper.getMainLooper());

    /**
     * ����������,����ִ����UI�߳�
     * 
     * @param request
     * @param response
     */
    public void deliveryResponse(final Request<?> request, final Response response) {
        Runnable respRunnable = new Runnable() {

            @Override
            public void run() {
                request.deliveryResponse(response);
            }
        };

        execute(respRunnable);
    }

    @Override
    public void execute(Runnable command) {
        mResponseHandler.post(command);
    }

}