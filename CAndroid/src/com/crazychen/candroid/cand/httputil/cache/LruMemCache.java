
package com.crazychen.candroid.cand.httputil.cache;

import android.support.v4.util.LruCache;

import com.crazychen.candroid.cand.httputil.base.Cache;
import com.crazychen.candroid.cand.httputil.base.Response;

/**
 * �����������浽�ڴ���
 * 
 * @author mrsimple
 */
public class LruMemCache implements Cache<String, Response> {

    /**
     * Reponse����
     */
    private LruCache<String, Response> mResponseCache;
    
    public LruMemCache() {    	
        // �����ʹ�õ�����ڴ�
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // ȡ�˷�֮һ�Ŀ����ڴ���Ϊ����
        final int cacheSize = maxMemory / 8;
        mResponseCache = new LruCache<String, Response>(cacheSize) {

            @Override
            protected int sizeOf(String key, Response response) {
                return response.rawData.length / 1024;
            }
        };

    }

    @Override
    public Response get(String key) {
        return mResponseCache.get(key);
    }

    @Override
    public void put(String key, Response response) {
        mResponseCache.put(key, response);
    }

    @Override
    public void remove(String key) {
        mResponseCache.remove(key);
    }
}
