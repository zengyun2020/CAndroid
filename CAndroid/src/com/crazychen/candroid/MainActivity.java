package com.crazychen.candroid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.crazychen.candroid.cand.httputil.base.Listener;
import com.crazychen.candroid.cand.httputil.base.Request;
import com.crazychen.candroid.cand.httputil.core.RequestQueue;
import com.crazychen.candroid.cand.httputil.listener.FileListener;
import com.crazychen.candroid.cand.httputil.request.FileRequest;
import com.crazychen.candroid.cand.httputil.request.StringRequest;


public class MainActivity extends Activity {		
	Button btn;	
	TextView text;
	ImageView img;
	ListView mylist;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		/*
		setContentView(R.layout.main);
		mylist = (ListView) findViewById(R.id.mlist);
		List<String> mlist = new ArrayList<String>();
		mlist.add("http://ckytest-app.stor.sinaapp.com/9.jpg");
		for(int i=10;i<=40;i++){
			mlist.add("http://ckytest-app.stor.sinaapp.com/"+i+".jpg");
			//mlist.add("http://avatar.csdn.net/4/3/4/1_zhy_cheng.jpg");
		}
        mylist.setAdapter(new MyAdapter(mlist,this));
        */
		setContentView(R.layout.activity_main);
		img = (ImageView) findViewById(R.id.img);
		btn = (Button) findViewById(R.id.btn);		
      
		final RequestQueue queue = new RequestQueue(1, null);				
		queue.start();	
		btn.setOnClickListener(new OnClickListener() {
			//https://codeload.github.com/zhaoqp2010/andbase/zip/master
			@Override
			public void onClick(View v) {
				/*
				Request<String> req = new StringRequest(Request.HttpMethod.GET,
						"http://www.tuicool.com/articles/QRzyeiB",
						new Listener<String>() {
					
					@Override
					public void onSuccess(int stCode, String response, String errMsg) {	
						System.out.println("ss");
						System.out.println(stCode);
						System.out.println(response);
						System.out.println(errMsg);
					}
				});
				*/
				Request<String> req = new FileRequest("sads1.jpg",
						//"http://101.4.136.3:9999/4dx.pc6.com/gm/wordpress_CN.zip",
						"http://ckytest-app.stor.sinaapp.com/9.jpg",
						new FileListener<String>() {
							@Override
							public void onSuccess(int stCode, String response,
									String errMsg) {
								System.out.println(stCode);
							}

							@Override
							public void onPreExecute() {
								
							}

							@Override
							public void onFail(int stCode, String response,
									String errMsg) {
								
							}

							@Override
							public void onCancel() {
								
							}

							@Override
							public void onProgressChange(long fileSize,
									long downloadedSize) {
								// TODO Auto-generated method stub
								
							}											
				});				
				queue.addRequest(req);			
			}
		});	
		
	}				
}
