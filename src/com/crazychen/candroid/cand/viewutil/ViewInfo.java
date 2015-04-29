package com.crazychen.candroid.cand.viewutil;
/**
 * 控件信息类
 * @author crazychen
 *
 */
public class ViewInfo{
	private int id;//控件id
	private String name;//控件名
	private String type;//控件类全名

	public ViewInfo(int id, String name, String type){
		this.id = id;
		this.name = name;
		this.type = type;
	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getType(){
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

}
