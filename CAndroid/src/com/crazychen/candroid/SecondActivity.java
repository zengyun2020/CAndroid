package com.crazychen.candroid;

import com.crazychen.candroid.R;
import com.crazychen.candroid.cand.dbutil.DbUtil;
import com.crazychen.candroid.cand.exception.DbException;

import android.app.Activity;
import android.os.Bundle;

public class SecondActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		DbUtil db = DbUtil.create(this);
		try {
			db.dropDb();
			//db.save(new Person(2));
			db.save(new Person(3));
		} catch (DbException e) {			
			e.printStackTrace();
		}
	}
}
