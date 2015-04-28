package com.crazychen.candroid;

import com.crazychen.candroid.cand.dbutil.annotation.Id;
import com.crazychen.candroid.cand.dbutil.annotation.Table;

@Table(name="testtable")
public class Person {
	@Id(column="id")
	public int id = 1;
	
	public Person(int id) {
		this.id = id;
	}
}
