package com.crazychen.candroid;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.crazychen.candroid.cand.httputil.base.Request;
import com.crazychen.candroid.cand.httputil.core.RequestQueue;
import com.crazychen.candroid.cand.httputil.listener.DefaultListener;
import com.crazychen.candroid.cand.httputil.request.ImageRequest;

public class MyAdapter extends BaseAdapter {  
	  
    List<String> mlist = null;  
    Context mContext = null;          
    LayoutInflater mInflater;   
    ViewHolder holder;  
    RequestQueue queue;
    public MyAdapter(List<String> mlist, Context mContext) {  
        super();  
        this.mlist = mlist;  
        this.mContext = mContext;  
        mInflater = LayoutInflater.from(mContext);  
        queue = new RequestQueue(1, null);
		queue.start();
    }  
  
    @Override  
    public int getCount() {  
        return mlist.size();          
    }  
  
    @Override  
    public String getItem(int position) {  
        return mlist.get(position);  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
    //静态内部类  
    static class ViewHolder{  
        ImageView img;          
    }    
      
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        View view = convertView;  
        //缓存机制  
        if(view == null){  
            view = mInflater.inflate(R.layout.vlist, null);  
            holder = new ViewHolder();  
            holder.img = (ImageView) view.findViewById(R.id.img);             
            view.setTag(holder);  
        }else{              
            holder = (ViewHolder)view.getTag();   
        }         
        Request<Bitmap> req = new ImageRequest(mlist.get(position),
				new DefaultListener<Bitmap>() {
			
			@Override
			public void onSuccess(int stCode, Bitmap response, String errMsg) {				
				 holder.img.setImageBitmap(response);
			}

			@Override
			public void onFail(int stCode, Bitmap response, String errMsg) {
				
			}					
		},200,200,Config.ARGB_8888);	
        queue.addRequest(req);	
        return view;  
    }     
}  