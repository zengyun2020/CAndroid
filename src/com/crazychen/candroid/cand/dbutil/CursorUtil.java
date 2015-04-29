package com.crazychen.candroid.cand.dbutil;

import java.lang.reflect.Method;
import java.util.List;

import android.database.Cursor;
/**
 * ��������cursor�����beanʵ���ת��
 * @author crazychen
 *
 */
public class CursorUtil {
	/**
	 * �������ݿ��в�ѯ����Ŀ����ʵ�����
	 * Ҫ��ʵ�������ʵ�����Ե�get,set��������һ���޲����Ĺ��캯��
	 * @param cursor
	 * @param list
	 * @param clazz
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	static <T> void getEntityFromCursor(Cursor cursor,List<T> list,Class<T> clazz) throws InstantiationException, IllegalAccessException{
		T t; // �������ʵ��    
	    String columnName; // ���ݿ��ֶ���    
	    String methodName; // ������    
	    Method m; // ������ķ���    
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {    
	        t = clazz.newInstance();  // ͨ���������ʵ����    
	        for(int i=0;i<cursor.getColumnCount();i++) {    
	            columnName = cursor.getColumnName(i); // ��ȡ���ݿ��ֶ���    
	            try {    
	                switch (cursor.getType(i)) {    
	                case Cursor.FIELD_TYPE_INTEGER:    
	                    // ����ֶ�����id�Ļ��� ��Ӧ�ķ�����setId    
	                    methodName = columnName.equals("id") ? "setId" :     
	                        getMethodName(cursor.getColumnName(i));    
	                    m = clazz.getMethod(methodName, int.class); // ���������    
	                    m.invoke(t, cursor.getInt(i)); // ִ�з���    
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
     * �����ֶ�������������  
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
