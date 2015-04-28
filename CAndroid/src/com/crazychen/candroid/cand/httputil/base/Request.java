package com.crazychen.candroid.cand.httputil.base;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.crazychen.candroid.cand.httputil.config.RequestConfig;
public abstract class Request<T> implements Comparable<Request<T>> {

	//����ʽ
    public static enum HttpMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");
       
        private String mHttpMethod = "";

        private HttpMethod(String method) {
            mHttpMethod = method;
        }

        @Override
        public String toString() {
            return mHttpMethod;
        }
    }

    
    //���ȼ�ö��
    public static enum Priority {
        LOW,
        NORMAL,
        HIGN,
        IMMEDIATE
    }

    //Ĭ�ϱ��뷽ʽ
    public static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    public final static String HEADER_CONTENT_TYPE = "Content-Type";    
    //�������к�    
    protected int mSerialNum = 0;
    //���ȼ�Ĭ������ΪNormal
    protected Priority mPriority = Priority.NORMAL;
    //�Ƿ�ȡ��������   
    protected boolean isCancel = false;
    //����ʱ��
    private long mCacheExpireTime = 1000*60*60*4;
    //����Listener    
    protected Listener<T> mRequestListener;
    //�����url    
    private String mUrl = "";
    //����ķ���    
    HttpMethod mHttpMethod = HttpMethod.GET;
    //�����header    
    private Map<String, String> mHeaders = new HashMap<String, String>();
    //�������     
    private Map<String, String> mBodyParams = new HashMap<String, String>();
    //�Ƿ��ļ�����
    protected boolean isFile = false;
       
	protected RequestConfig rconfig = new RequestConfig(); 
      
	public Request(HttpMethod method, String url, Listener<T> listener) {
        mHttpMethod = method;
        mUrl = url;
        mRequestListener = listener;
    }
    
    public Request(String url, Listener<T> listener) {
		this(HttpMethod.GET, url, listener);
	}

    public void onPrepare(){}
    
    public void addHeader(String name, String value) {
    	mHeaders.remove(name);
        mHeaders.put(name, value);
    }
    
    public void setParams(String... params){
    	if(params==null || (params.length&1)==1) return;
    	for(int i=0;i<params.length;i+=2){
			mBodyParams.put(params[i], params[i+1]);
		}
    }
    
    public String getUrl() {
        return mUrl;
    }
    
    public int getSerialNumber() {
        return mSerialNum;
    }

    public void setSerialNumber(int mSerialNum) {
        this.mSerialNum = mSerialNum;
    }        

    public Map<String, String> getHeaders() {
        return mHeaders;
    }
    
    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority mPriority) {
        this.mPriority = mPriority;
    }

    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public long getCacheExpireTime() {
		return mCacheExpireTime;
	}
	
	public void setCacheExpireTime(TimeUnit timeUnit, int amount) {		
		this.mCacheExpireTime = System.currentTimeMillis() + timeUnit.toMillis(amount);
	}
    
    /**
     * ��ԭ�������������н������
     * 
     * @param response
     * @return
     */
    public abstract T parseResponse(Response response);

    /**
     * ����Response,�÷���������UI�߳�.
     * 
     * @param response
     */
    public final void deliveryResponse(Response response) {
        T result = parseResponse(response);
        if (mRequestListener != null) {
            int stCode = response != null ? response.getStatusCode() : -1;
            String msg = response != null ? response.getMessage() : "unkown error";
            Log.e("###", "### ִ�лص� : stCode = " + stCode + ", result : " + result + ", err : " + msg);
            mRequestListener.onSuccess(stCode, result, msg);            
        }
    }    

    public Listener<T> getRequestListener() {
        return mRequestListener;
    }           

    public HttpMethod getHttpMethod() {
        return mHttpMethod;
    }
    
    public Map<String, String> getParams() {
        return mBodyParams;
    }

    public boolean isHttps() {
        return mUrl.startsWith("https");
    }

    public boolean shouldCache() {
    	return mCacheExpireTime-System.currentTimeMillis() > 0;
    }

    public void cancel() {
        isCancel = true;
    }

    public boolean isCanceled() {
        return isCancel;
    }
   
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }
    
    public byte[] getBody() {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    public RequestConfig getRconfig() {
		return rconfig;
	}
    
    public boolean isFile() {
		return isFile;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}


	public void setRconfig(RequestConfig rconfig) {
		this.rconfig = rconfig;
	}
    
    @Override
    public int compareTo(Request<T> another) {
        Priority myPriority = this.getPriority();
        Priority anotherPriority = another.getPriority();
        // ������ȼ����,��ô������ӵ����е����к�˳����ִ��
        return myPriority.equals(anotherPriority) ? this.getSerialNumber()
                - another.getSerialNumber()
                : myPriority.ordinal() - anotherPriority.ordinal();
    }   
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mHeaders == null) ? 0 : mHeaders.hashCode());
        result = prime * result + ((mHttpMethod == null) ? 0 : mHttpMethod.hashCode());
        result = prime * result + ((mBodyParams == null) ? 0 : mBodyParams.hashCode());
        result = prime * result + ((mPriority == null) ? 0 : mPriority.hashCode());
        result = prime * result + (mCacheExpireTime>0 ? 1231 : 1237);
        result = prime * result + ((mUrl == null) ? 0 : mUrl.hashCode());
        return result;       
    }

    @Override
    public boolean equals(Object obj) {    	    	
    	if(!(obj instanceof Request))
    		return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        Request<?> other = (Request<?>) obj;
        if (mHeaders == null) {
            if (other.mHeaders != null)
                return false;
        } else if (!mHeaders.equals(other.mHeaders))
            return false;
        if (mHttpMethod != other.mHttpMethod)
            return false;
        if (mBodyParams == null) {
            if (other.mBodyParams != null)
                return false;
        } else if (!mBodyParams.equals(other.mBodyParams))
            return false;
        if (mPriority != other.mPriority)
            return false;
        if (mCacheExpireTime != other.mCacheExpireTime)
            return false;
        if (mUrl == null) {
            if (other.mUrl != null)
                return false;
        } else if (!mUrl.equals(other.mUrl))
            return false;
        return true;       
    }
}
