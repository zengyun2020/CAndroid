package com.crazychen.candroid.cand.dbutil;

import java.lang.reflect.Method;
import java.util.List;

import android.database.Cursor;
/**
 * 该类用于cursor向对象bean实体的转换
 * @author crazychen
 *
 */
public class CursorUtil {
	/**
	 * 根据数据库中查询的条目构造实体对象
	 * 要求实体类必须实现属性的get,set方法，和一个无参数的构造函数
	 * @param cursor
	 * @param list
	 * @param clazz
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	static <T> void getEntityFromCursor(Cursor cursor,List<T> list,Class<T> clazz) throws InstantiationException, IllegalAccessException{
		T t; // 反射出的实例    
	    String columnName; // 数据库字段名    
	    String methodName; // 方法名    
	    Method m; // 反射出的方法    
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {    
	        t = clazz.newInstance();  // 通过反射进行实例化    
	        for(int i=0;i<cursor.getColumnCount();i++) {    
	            columnName = cursor.getColumnName(i); // 获取数据库字段名    
	            try {    
	                switch (cursor.getType(i)) {    
	                case Cursor.FIELD_TYPE_INTEGER:    
	                    // 如果字段名是id的话， 对应的方法是setId    
	                    methodName = columnName.equals("id") ? "setId" :     
	                        getMethodName(cursor.getColumnName(i));    
	                    m = clazz.getMethod(methodName, int.class); // 反射出方法    
	                    m.invoke(t, cursor.getInt(i)); // 执行方法    
	                    break;    
	                case Cursor.FIELD_TYPE_FLOAT:   	      
	                    methodName = getMethodName(cursor.getColumnName(i));    
	                    m = clazz.getMethod(methodName, float.class);    
	                    m.invoke(t, cursor.getFloat(i));    
	                    break;    
	                default:    
	                    methodName = getMethodName(cursor.getColumnName(i));    
	                    m = clazz.getMethod(methodName, String.class);    
	                    m.invoke(t, cursor.getString(i));    
	                    break;    
	                }    
	            }catch(Exception e) {    
	                e.printStackTrace();    
	            }    
	        }    
	        list.add(t);    
	    }    
	}
	
    /**  
     * 根据字段名构建方法名  
     * @param columnName  
     * @return  
     */    
    public static String getMethodName(String columnName) {    
        String methodName = columnName;    
        methodName = Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);    
        methodName = "set" + methodName;    
                
        return methodName;    
    }   
}
