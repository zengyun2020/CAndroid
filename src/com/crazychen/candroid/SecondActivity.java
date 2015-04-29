package com.crazychen.candroid;

import com.crazychen.candroid.R;
import com.crazychen.candroid.cand.dbutil.DbUtil;
import com.crazychen.candroid.cand.exception.DbException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SecondActivity extends Activity{
	Button insert_btn;	
	Button update_btn;	
	Button delete_btn;	
	Button select_btn;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database);		
		final DbUtil db = DbUtil.create(this);		
		try {
			db.dropDb();
		} catch (DbException e1) {			
			e1.printStackTrace();
		}		
		insert_btn = (Button) findViewById(R.id.insert_btn);	
		insert_btn.setOnClickListener(new OnClickListener() {
			int count = 1;
			@Override
			public void onClick(View v) {
				try {					
					db.insert(new Person(0,"sss"));
				} catch (DbException e) {					
					e.printStackTrace();
				}
			}
			
		});		
		
		update_btn = (Button) findViewById(R.id.update_btn);	
		update_btn.setOnClickListener(new OnClickListener() {
			int count = 1;
			@Override
			public void onClick(View v) {
				try {										
					//db.update(new Person(count++,"aaa"));
					db.where("name=\"sss\"").update(new Person(count++,"aaa"));
				} catch (DbException e) {					
					e.printStackTrace();
				}
			}			
		});		
		
		delete_btn = (Button) findViewById(R.id.delete_btn);	
		delete_btn.setOnClickListener(new OnClickListener() {
			int count = 1;
			@Override
			public void onClick(View v) {
				try {										
					db.where("id="+count++).delete(Class.forName("com.crazychen.candroid.Person"));
				} catch (DbException e) {					
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		});	
				
		select_btn = (Button) findViewById(R.id.select_btn);	
		select_btn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				try {										
					System.out.println(db.where("name=\"sss\"").oderBy("id desc").select(Class.forName("com.crazychen.candroid.Person")));					
					System.out.println("findFirstK"+db.limit(1).findFirstK(Class.forName("com.crazychen.candroid.Person"),2));										
				} catch (DbException e) {					
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		});	
	}
}
