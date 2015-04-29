package com.crazychen.candroid;

import com.crazychen.candroid.cand.dbutil.annotation.Id;
import com.crazychen.candroid.cand.dbutil.annotation.Table;

@Table(name="testtable")
public class Person {
	@Id(column="id")
	public int id = 1;
	private String name;
	
	public Person() {
		// TODO Auto-generated constructor stub
	}
	public Person(int id,String name) {
		this.id = id;
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return id+":my name is "+name+"\n";
	}
}
