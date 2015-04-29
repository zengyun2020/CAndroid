package com.crazychen.candroid.cand.httputil.httpstack;

import android.os.Build;

/**
 * ����api�汾ѡ��HttpClient����HttpURLConnection
 * 
 */
public final class HttpStackFactory {

    private static final int GINGERBREAD_SDK_NUM = 9;

    /**
     * ����SDK�汾����������ͬ��Httpִ����,��SDK 9֮ǰʹ��HttpClient,֮����ʹ��HttlUrlConnection,
     * ����֮��Ĳ����ο� :
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     * 
     * @return
     */
    public static HttpStack createHttpStack() {
        int runtimeSDKApi = Build.VERSION.SDK_INT;
        if (runtimeSDKApi >= GINGERBREAD_SDK_NUM) {
            return new HttpUrlConnStack();
        }

        return new HttpClientStack();
    }
}