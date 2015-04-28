package com.crazychen.candroid.cand.httputil.request;

import java.util.concurrent.TimeUnit;

import com.crazychen.candroid.cand.httputil.base.Listener;
import com.crazychen.candroid.cand.httputil.base.Request;
import com.crazychen.candroid.cand.httputil.base.Response;

public class StringRequest extends Request<String> {

    public StringRequest(HttpMethod method, String url, Listener<String> listener) {
        super(method, url, listener);
        this.setCacheExpireTime(TimeUnit.MILLISECONDS, 0);
    }
    
    
    
    @Override
    public String parseResponse(Response response) {
        return new String(response.getRawData());
    }

}
