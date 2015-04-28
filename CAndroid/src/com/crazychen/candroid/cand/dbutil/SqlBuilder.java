package com.crazychen.candroid.cand.dbutil;

import java.util.HashMap;
import java.util.Map.Entry;

import android.util.Log;


public class SqlBuilder {	
	public synchronized static String crateTable(Class<?> clazz){
		StringBuilder sb = new StringBuilder();
		MyTable table = MyTable.get(clazz);
		sb.append("CREATE TABLE IF NOT EXISTS ");
	    sb.append(table.tableName);
	    sb.append("( ");
	    for(Entry<String,String> entry:table.fieldMap.entrySet()){
	    	sb.append(entry.getValue()).append(" ").append(entry.getKey()).append(",");
	    }
	    sb.deleteCharAt(sb.length()-1);
	    sb.append(");");
	    Log.d("###", sb.toString());
	    return sb.toString();
	}
	
	public synchronized static String save(Object obj){
		StringBuilder sb = new StringBuilder();
		MyTable table = MyTable.get(obj.getClass());	
		sb.append("INSERT INTO ").append(table.tableName).append(" (");
		for(String key:table.getFieldValue(obj).keySet()){
			sb.append(key).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(") VALUES(");
		for(String value:table.getFieldValue(obj).values()){
			sb.append(value).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(");");
		Log.d("###", sb.toString());
	    return sb.toString();
	}
}
