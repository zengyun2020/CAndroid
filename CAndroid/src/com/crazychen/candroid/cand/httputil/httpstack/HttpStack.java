package com.crazychen.candroid.cand.httputil.httpstack;

import com.crazychen.candroid.cand.httputil.base.Request;
import com.crazychen.candroid.cand.httputil.base.Response;

/**
 * ִ����������Ľӿ�
 */
public interface HttpStack {
    /**
     * ִ��Http����
     * 
     * @param request ��ִ�е�����
     * @return
     */
    public Response performRequest(Request<?> request);
}
