package com.crazychen.candroid.cand.httputil.config;

import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * ���������ʹ��HttpClientStackִ������ʱΪhttps�������õ�SSLSocketFactory��
 * 
 */
public class HttpClientConfig extends HttpConfig {
    private static HttpClientConfig sConfig = new HttpClientConfig();
    SSLSocketFactory mSslSocketFactory;

    private HttpClientConfig() {

    }

    public static HttpClientConfig getConfig() {
        return sConfig;
    }

    /**
     * ����https�����SSLSocketFactory��HostnameVerifier
     * 
     * @param sslSocketFactory
     * @param hostnameVerifier
     */
    public void setHttpsConfig(SSLSocketFactory sslSocketFactory) {
        mSslSocketFactory = sslSocketFactory;
    }

    public SSLSocketFactory getSocketFactory() {
        return mSslSocketFactory;
    }
}