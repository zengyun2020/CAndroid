package com.crazychen.candroid.cand.viewutil;
/**
 * �ؼ���Ϣ��
 * @author crazychen
 *
 */
public class ViewInfo{
	private int id;//�ؼ�id
	private String name;//�ؼ���
	private String type;//�ؼ���ȫ��

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
