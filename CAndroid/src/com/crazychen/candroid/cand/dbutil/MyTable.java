package com.crazychen.candroid.cand.dbutil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.crazychen.candroid.cand.dbutil.annotation.Table;
import com.crazychen.candroid.cand.dbutil.annotation.Transient;



public class MyTable {
	public String tableName;
    public int id;
    public Map<String,String> fieldMap = new HashMap<String,String>();   
    public static Map<String,MyTable> tableMap = new HashMap<String,MyTable>();
    private boolean isCheckedDatabase;
    
    
    private MyTable(){};
    
	public static synchronized MyTable get(Class<?> entityType) {
		Table table = entityType.getAnnotation(Table.class);
		if(tableMap.containsKey(table.name())){
			return tableMap.get(table.name());
		}
		MyTable mTable = new MyTable();		
		mTable.tableName = table.name();
        for(Field field:entityType.getDeclaredFields()){
        	if(field.getAnnotation(Transient.class)==null){        	        	
        		mTable.fieldMap.put(field.getName(),ConvertType(field.getType()));        	
        	}
        }
        tableMap.put(mTable.tableName, mTable);
        return mTable;
    }
		
	public Map<String, String> getFieldValue(Object obj){
		Map<String,String> keyValueMap = new HashMap<String, String>();
		Class<?> clazz = obj.getClass();
		for(Field field:clazz.getDeclaredFields()){
		   if(field.getAnnotation(Transient.class)==null){        	        	
			   try {
				   keyValueMap.put(field.getName(), String.valueOf(field.get(obj)));
			   } catch (IllegalAccessException e) {					
				   e.printStackTrace();
			   } catch (IllegalArgumentException e) {					
				   e.printStackTrace();
			   }
		   }
		}
		return keyValueMap;
	}
	
	public synchronized static void remove(String tableName){
		tableMap.remove(tableName);
	}
	
	public boolean isCheckedDatabase(){
		return isCheckedDatabase;
	}
	
	public void setCheckedDatabase(boolean flag){
		isCheckedDatabase = flag;
	}
	
	private static String ConvertType(Class<?> clazz){		
		if(clazz==Integer.class || clazz==int.class){
			return "Integer";
		}else if(clazz==Long.class || clazz==long.class){
			return "Integer";
		}else if(clazz==Double.class || clazz==double.class){
			return "REAL";
		}else if(clazz==Float.class || clazz==float.class){
			return "REAL";
		}else if(clazz==Boolean.class || clazz==boolean.class){
			return "Integer";
		}else if(clazz==String.class){					
			return "TEXT";
		}
		return "NULL";
	}
}
