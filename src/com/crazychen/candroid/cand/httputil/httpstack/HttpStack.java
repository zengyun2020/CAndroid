package com.crazychen.candroid.cand.httputil.httpstack;

import com.crazychen.candroid.cand.httputil.base.Request;
import com.crazychen.candroid.cand.httputil.base.Response;

/**
 * 执行网络请求的接口
 */
public interface HttpStack {
    /**
     * 执行Http请求
     * 
     * @param request 待执行的请求
     * @return
     */
    public Response performRequest(Request<?> request);
}
