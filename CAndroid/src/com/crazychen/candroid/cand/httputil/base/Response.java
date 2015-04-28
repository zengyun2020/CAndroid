package com.crazychen.candroid.cand.httputil.base;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class Response extends BasicHttpResponse{
	protected Request<?> req = null;
	public byte[] rawData = new byte[0];
    /**     
     * @param statusLine ×´Ì¬Âë
     */
    public Response(StatusLine statusLine) {
        super(statusLine);
    }

    public Response(ProtocolVersion ver, int code, String reason) {
        super(ver, code, reason);
    }
    
    public Response(StatusLine statusLine,Request<?> req) {
        super(statusLine);
        this.req = req;
    }
    
    /**
     * ÊµÌå
     */
    @Override
    public void setEntity(HttpEntity entity) {
        super.setEntity(entity);
        Log.d("###", entity.getContentType().getValue());
        if(req.isFile()){
        	rawData = entityToBytes(getEntity());       
        }
    }

    public byte[] getRawData() {
        return rawData;
    }

    public int getStatusCode() {
        return getStatusLine().getStatusCode();
    }

    public String getMessage() {
        return getStatusLine().getReasonPhrase();
    }

    /** Reads the contents of HttpEntity into a byte[]. */
    private byte[] entityToBytes(HttpEntity entity) {
        try {        	
            return EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }           
}