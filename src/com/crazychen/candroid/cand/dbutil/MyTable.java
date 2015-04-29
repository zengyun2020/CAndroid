package com.crazychen.candroid.cand.dbutil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.crazychen.candroid.cand.dbutil.annotation.Id;
import com.crazychen.candroid.cand.dbutil.annotation.Table;
import com.crazychen.candroid.cand.dbutil.annotation.Transient;
import com.crazychen.candroid.cand.exception.DbException;
/**
 * 数据表实体类
 * @author crazychen
 *
 */
public class MyTable {
	//表名
	public String tableName;
	//主键名
	public String idName;
	//当前主键值
	public String idValue;
	//表元素名与元素类型
    public Map<String,String> fieldMap = new HashMap<String,String>();
    //每个表名对应一个table实体类
    public static Map<String,MyTable> tableMap = new HashMap<String,MyTable>();
    //用于标记是否检查过表的存在
    private boolean isCheckedDatabase;
    
    
    private MyTable(){};
    
    /**
     * 获得某类型对应table对象
     * @param entityType
     * @return
     * @throws DbException 
     */
	public static synchronized MyTable get(Class<?> entityType) throws DbException {
		Table table = entityType.getAnnotation(Table.class);
		if(tableMap.containsKey(table.name())){
			return tableMap.get(table.name());
		}
		MyTable mTable = new MyTable();		
		mTable.tableName = table.name();
        for(Field field:entityType.getDeclaredFields()){            
        	field.setAccessible(true);
        	if(field.getAnnotation(Id.class)!=null){
        		mTable.idName = field.getName();
        	}else{
	        	if(field.getAnnotation(Transient.class)==null){        	        	
	        		mTable.fieldMap.put(field.getName(),ConvertType(field.getType()));        	
	        	}     
        	}
        }
        if(mTable.idName==null||"".equals(mTable.idName)){
        	throw new DbException("the bean must own @id");
        }else{
        	tableMap.put(mTable.tableName, mTable);
        }
        return mTable;
    }
		
	/**
	 * 获得某对象的属性名值对
	 * @param obj
	 * @return
	 */
	public Map<String, String> getFieldValue(Object obj){
		Map<String,String> keyValueMap = new HashMap<String, String>();
		Class<?> clazz = obj.getClass();
		for(Field field:clazz.getDeclaredFields()){
		   field.setAccessible(true);
		   if(field.getAnnotation(Transient.class)==null){
			   try {
				   if(field.getAnnotation(Id.class)!=null){
					   idValue = String.valueOf(field.get(obj));
				   }else{			
					   keyValueMap.put(field.getName(), String.valueOf(field.get(obj)));					   
				   }   
			   }catch (IllegalAccessException e) {					
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
	
	/**
	 * 将class类型转换成数据库类型
	 * @param clazz
	 * @return
	 */
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
