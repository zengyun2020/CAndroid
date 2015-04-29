package com.crazychen.candroid.cand.httputil.response;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;

import org.apache.http.StatusLine;

import com.crazychen.candroid.cand.httputil.base.Request;
import com.crazychen.candroid.cand.httputil.base.Response;
import com.crazychen.candroid.cand.httputil.request.FileRequest;
import com.crazychen.candroid.cand.normalutils.SDCardUtils;

public class FileResponse extends Response{
	InputStream filein;
	
	public FileResponse(StatusLine statusLine, Request<?> req) {
		super(statusLine, req);				
	}

	public InputStream getFilein() {
		return filein;
	}

	public void setFilein(InputStream filein) {
		this.filein = filein;
		storeData();
	}

	private void storeData(){		
        FileRequest freq = (FileRequest) this.req;
    	try {								
			File file = new File(SDCardUtils.getAvailablePath()+"candroidfile");
			if(!file.exists()){
				file.mkdir();
			}
			file = new File(SDCardUtils.getAvailablePath()+"candroidfile/"+freq.getPath());
			FileOutputStream fo = new FileOutputStream(file,false); 
			BufferedOutputStream bf = new BufferedOutputStream(fo);			
			int chByte =  0;   	
            //ѭ����ȡ���ص��ļ���buffer����������  
            while(!freq.isCanceled()&&(chByte = filein.read()) != -1) {  
	            //���ļ�д�뵽�ļ�  	           
            	bf.write(chByte);	            
            }  
            bf.flush();
            fo.close();
		} catch (IllegalStateException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
}
