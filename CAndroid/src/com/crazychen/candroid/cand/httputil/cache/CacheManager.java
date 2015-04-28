package com.crazychen.candroid.cand.httputil.cache;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicStatusLine;

import android.support.v4.util.LruCache;
import android.util.Log;

import com.crazychen.candroid.cand.httputil.base.Cache;
import com.crazychen.candroid.cand.httputil.base.Response;
import com.crazychen.candroid.cand.httputil.cache.DiskLruCache.Editor;
import com.crazychen.candroid.cand.httputil.cache.DiskLruCache.Snapshot;
import com.crazychen.candroid.cand.normalutils.SDCardUtils;

public class CacheManager implements Cache<String, Response>{
	//�ڴ滺��
	private LruCache<String, Response> mLruCache;
	//Ӳ�̻���
	private DiskLruCache mDiskLruCache;
	private String cacheUrl = "candroidcache";
	private File cacheDir;
	public CacheManager(){		
		this(10,10 * 1024 * 1024);		
	}
	
	/**	 
	 * @param PartMenory ���ڴ漸��֮һ��Ϊ����
	 * @param maxDiskCacheSize Ӳ�̻��浥���ļ������ֵ
	 */
	public CacheManager(int PartMenory,int maxDiskCacheSize) {		
		// �����ʹ�õ�����ڴ�
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        
        // ȡ�˷�֮һ�Ŀ����ڴ���Ϊ����
        final int cacheSize = maxMemory / PartMenory;
        mLruCache = new LruCache<String, Response>(cacheSize) {

            @Override
            protected int sizeOf(String key, Response response) {
                return response.rawData.length / 1024;
            }
        };
        cacheDir = new File(SDCardUtils.getAvailablePath()+cacheUrl);     
		try {
			mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1, maxDiskCacheSize);
		} catch (IOException e) {
			Log.d("###", "Ӳ�̿ռ䲻��");
		} 
	}
	
    public String hashKeyForDisk(String key) {  
        String cacheKey;  
        try {  
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");  
            mDigest.update(key.getBytes());  
            cacheKey = bytesToHexString(mDigest.digest());  
        } catch (NoSuchAlgorithmException e) {  
            cacheKey = String.valueOf(key.hashCode());  
        }  
        return cacheKey;  
    }  
      
    private String bytesToHexString(byte[] bytes) {  
        StringBuilder sb = new StringBuilder();  
        for (int i = 0; i < bytes.length; i++) {  
            String hex = Integer.toHexString(0xFF & bytes[i]);  
            if (hex.length() == 1) {  
                sb.append('0');  
            }  
            sb.append(hex);  
        }  
        return sb.toString();  
    }  
	
	public Response get(String key) {
		key = hashKeyForDisk(key);
		Response response = null;
		if(mLruCache !=null){
			response = mLruCache.get(key);
			if(response!=null){
				Log.d("###", "�ڴ滺��get");
				return response;
			}
		}		
		if(mDiskLruCache!=null){
			Snapshot snap;
			try {
				Log.d("###", key);	
				snap = mDiskLruCache.get(key);
				if(snap!=null){					
					ResponeCache obj = (ResponeCache) new ObjectInputStream(snap.getInputStream(0)).readObject();
					if(obj!=null){
						Log.d("###", "Ӳ�̻���get");					
						ProtocolVersion protocolVersion = new ProtocolVersion("HTTP", 1, 1);       
				        StatusLine statusLine = new BasicStatusLine(protocolVersion,
				                obj.getStatusCode(), obj.getMsg());						
						response = new Response(statusLine);
						response.rawData = obj.rawData;
						Log.d("###", "Ӳ�̻���д���ڴ滺��");
						mLruCache.put(key, response);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				Log.d("###", "Ӳ�̻���get����ioexception"+e.getMessage());
			} catch (ClassNotFoundException e) {				
				e.printStackTrace();
				Log.d("###", "Ӳ�̻���get����classnotfind");
			}
		}
		return response;
    }

    public void put(String key, Response response) {
    	key = hashKeyForDisk(key);
    	try{
    		if(mLruCache!=null ){
    			Log.d("###", "�ڴ滺��");
    			mLruCache.put(key, response);
    		}
    		if(mDiskLruCache!=null){
	            Editor editor;    		
	    		editor = mDiskLruCache.edit(key);    		
	        	if (editor != null) {
	        		Log.d("###", "Ӳ�̻���");
	                OutputStream outputStream;    			
	    			outputStream = editor.newOutputStream(0);	    			
	    			new ObjectOutputStream(outputStream).writeObject(
	    					new ResponeCache(response.rawData, response.getStatusCode(), response.getMessage()));	    			
	    		    editor.commit();      		    			     
	            }  
	            mDiskLruCache.flush();  
    		}
    	}catch (IOException e) {	
    		Log.d("###", "Ӳ�̻���put����"+e.getMessage());
			e.printStackTrace();
		}    	
    }

    public void remove(String key) {
    	key = hashKeyForDisk(key);
    	try{
    		if(mLruCache!=null ){
    			Log.d("###", "ɾ���ڴ滺��");
    			mLruCache.remove(key);
    		}
    		if(mDiskLruCache!=null){
    			mDiskLruCache.remove(key);	     
    		}
    	}catch (IOException e) {	
    		Log.d("###", "Ӳ�̻���ɾ������"+e.getMessage());
			e.printStackTrace();
		}    	
    }
}
