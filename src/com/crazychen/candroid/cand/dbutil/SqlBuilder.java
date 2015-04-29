package com.crazychen.candroid.cand.dbutil;

import java.util.HashMap;
import java.util.Map.Entry;

import android.util.Log;

import com.crazychen.candroid.cand.exception.DbException;
public class SqlBuilder {	
	private static HashMap<Class<?>,MyTable> map = new HashMap<Class<?>,MyTable>();
	private String where = "";
	private String groupBy = "";
	private String orderBy = "";
	private boolean distinct = false;
	private int limitFrom = -1;
	private int limitTo = -1;
	private MyTable getTableByClass(Class<?> clazz) throws DbException{
		MyTable table;
		if((table=map.get(clazz))==null){
			try {
				table = MyTable.get(clazz);
			} catch (DbException e) {
				throw e;
			}	
			map.put(clazz, table);
		}
		return table;
	}
	
	public synchronized String crateTable(Class<?> clazz) throws DbException{
		StringBuilder sb = new StringBuilder();
		MyTable table;
		try {
			table = getTableByClass(clazz);
		} catch (DbException e) {
			throw e;
		}		
		sb.append("CREATE TABLE IF NOT EXISTS ");
	    sb.append(table.tableName);
	    sb.append("( ").append(table.idName).append(" INTEGER PRIMARY KEY AUTOINCREMENT ,");
	    for(Entry<String,String> entry:table.fieldMap.entrySet()){
	    	sb.append(entry.getKey()).append(" ").append(entry.getValue()).append(",");
	    }
	    sb.deleteCharAt(sb.length()-1);
	    sb.append(");");
	    cleanCondition();
	    Log.d("###", sb.toString());
	    return sb.toString();
	}
	
	/**
	 * 构造insert语句
	 * @param obj
	 * @return
	 * @throws DbException 
	 */
	public synchronized String buildInsertSql(Object obj) throws DbException{
		StringBuilder sb = new StringBuilder();		
		MyTable table;
		try {
			table = getTableByClass(obj.getClass());
		} catch (DbException e) {
			throw e;
		}		
		sb.append("INSERT INTO ").append(table.tableName).append(" (");
		for(String key:table.getFieldValue(obj).keySet()){
			sb.append(key).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(") VALUES(");
		for(String value:table.getFieldValue(obj).values()){
			sb.append("\"").append(value).append("\",");
		}		
		sb.deleteCharAt(sb.length()-1);
		sb.append(");");
		cleanCondition();
		Log.d("###", sb.toString());
	    return sb.toString();
	}

	/**
	 * 构造update语句
	 * @param obj
	 * @return
	 * @throws DbException 
	 */
	public String buildUpdateSql(Object obj) throws DbException {
		StringBuilder sb = new StringBuilder();		
		MyTable table;
		try {
			table = getTableByClass(obj.getClass());
		} catch (DbException e) {
			throw e;
		}	
		sb.append("UPDATE ").append(table.tableName).append(" SET ");		
		String id = table.idValue;
		for(Entry<String, String> entry:table.getFieldValue(obj).entrySet()){			
			sb.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\",");			
		}
		sb.deleteCharAt(sb.length()-1);
		if("".equals(where)){			
			sb.append(" WHERE ").append(table.idName).append("=").append(id);
		}else{
			sb.append(" WHERE ").append(where);
		}				
		cleanCondition();
		Log.d("###", sb.toString());
	    return sb.toString();
	}	
	
	/**
	 * 构造删除语句
	 * @param clazz
	 * @param id
	 * @return
	 * @throws DbException 
	 */
	public synchronized String buildDeleteByIdSql(Class<?> clazz,int id) throws DbException {
		StringBuilder sb = new StringBuilder();		
		MyTable table;
		try {
			table = getTableByClass(clazz);
		} catch (DbException e) {
			throw e;
		}		
		sb.append("DELETE FROM ").append(table.tableName).append(" WHERE ").
		append(table.idName).append("=").append(id);
		cleanCondition();
		Log.d("###", sb.toString());
		return sb.toString();
	}
	
	public synchronized String buildDeleteSql(Class<?> clazz) throws DbException {
		StringBuilder sb = new StringBuilder();
		MyTable table = getTableByClass(clazz);	
		sb.append("DELETE FROM ").append(table.tableName).append(" WHERE ");
		if(!"".equals(where)){
			sb.append(where);
		}else{
			throw new DbException("no delete condition!");
		}
		cleanCondition();
		Log.d("###", sb.toString());	
		return sb.toString();
	}
	
	public synchronized String buildDeleteAllSql(Class<?> clazz) throws DbException {
		StringBuilder sb = new StringBuilder();
		MyTable table = getTableByClass(clazz);	
		sb.append("DELETE FROM ").append(table.tableName);
		cleanCondition();
		Log.d("###", sb.toString());	
		return sb.toString();
	}
		
	public synchronized String buildDeleteSql(Object obj) throws DbException {
		StringBuilder sb = new StringBuilder();		
		MyTable table;
		try {
			table = getTableByClass(obj.getClass());
		} catch (DbException e) {
			throw e;
		}		
		sb.append("DELETE FROM ").append(table.tableName).append(" WHERE ");
		if("".equals(where)){				
			sb.append(table.idName).append("=").append(table.idValue);
		}else{
			sb.append(where);
		}						
		cleanCondition();
		Log.d("###", sb.toString());
		return sb.toString();
	}	
	
	/**
	 * 构造查找语句
	 * @param clazz
	 * @return
	 * @throws DbException
	 */
	public synchronized String buildSelectSql(Class<?> clazz) throws DbException {
		StringBuilder sb = new StringBuilder();		
		MyTable table;
		try {
			table = getTableByClass(clazz);
		} catch (DbException e) {
			throw e;
		}		
		if(distinct){
			sb.append("SELECT DISTINCT * FROM ").append(table.tableName);
		}else{
			sb.append("SELECT * FROM ").append(table.tableName);
		}
		fillCondition(sb);
		Log.d("###", sb.toString());
		return sb.toString();
	}				
	
	public synchronized String buildFindByIdSql(Class<?> clazz,int id) throws DbException {
		StringBuilder sb = new StringBuilder();		
		MyTable table;
		try {
			table = getTableByClass(clazz);
		} catch (DbException e) {
			throw e;
		}		
		sb.append("SELECT * FROM ").append(table.tableName);			
		sb.append(" WHERE ").append(table.idName).append("=").append(id);		
		buildLimitSql(0, 1);
		fillCondition(sb);
		Log.d("###", sb.toString());
		return sb.toString();
	}		
	
	public synchronized String buildFindFirst(Class<?> clazz) throws DbException{
		StringBuilder sb = new StringBuilder();		
		MyTable table;
		try {
			table = getTableByClass(clazz);
		} catch (DbException e) {
			throw e;
		}		
		sb.append("SELECT * FROM ").append(table.tableName);			
		buildOrderBySql(table.idName+" asc");
		buildLimitSql(1,-1);
		fillCondition(sb);
		Log.d("###", sb.toString());
		return sb.toString();
	}
	
	public synchronized String buildFindFirstK(Class<?> clazz,int k) throws DbException{
		StringBuilder sb = new StringBuilder();		
		MyTable table;
		try {
			table = getTableByClass(clazz);
		} catch (DbException e) {
			throw e;
		}		
		if(distinct){
			sb.append("SELECT DISTINCT * FROM ").append(table.tableName);
		}else{
			sb.append("SELECT * FROM ").append(table.tableName);
		}		
		buildOrderBySql(table.idName+" asc");
		buildLimitSql(0,k);
		fillCondition(sb);
		Log.d("###", sb.toString());
		return sb.toString();
	}
	
	public synchronized String buildFindLast(Class<?> clazz) throws DbException{
		StringBuilder sb = new StringBuilder();		
		MyTable table;
		try {
			table = getTableByClass(clazz);
		} catch (DbException e) {
			throw e;
		}		
		sb.append("SELECT * FROM ").append(table.tableName);			
		buildOrderBySql(table.idName+" desc");
		buildLimitSql(1,-1);
		fillCondition(sb);
		Log.d("###", sb.toString());
		return sb.toString();
	}
	
	public synchronized String buildFindLastK(Class<?> clazz,int k) throws DbException{
		StringBuilder sb = new StringBuilder();		
		MyTable table;
		try {
			table = getTableByClass(clazz);
		} catch (DbException e) {
			throw e;
		}		
		if(distinct){
			sb.append("SELECT DISTINCT * FROM ").append(table.tableName);
		}else{
			sb.append("SELECT * FROM ").append(table.tableName);
		}		
		buildOrderBySql(table.idName+" desc");
		buildLimitSql(0,k);
		fillCondition(sb);
		Log.d("###", sb.toString());
		return sb.toString();
	}
	
	public synchronized String buildCount(Class<?> clazz) throws DbException{
		StringBuilder sb = new StringBuilder();		
		MyTable table;
		try {
			table = getTableByClass(clazz);
		} catch (DbException e) {
			throw e;
		}		
		sb.append("SELECT COUNT(*) FROM ").append(table.tableName);						
		Log.d("###", sb.toString());
		return sb.toString();
	}
	
	public synchronized void fillCondition(StringBuilder sb){
		if(!"".equals(where)){
			sb.append(" WHERE ").append(where);
		}
		if(!"".equals(groupBy)){
			sb.append(" GROUP BY ").append(groupBy);
		}
		if(!"".equals(orderBy)){
			sb.append(" ORDER BY ").append(orderBy);
		}
		if(-1!=limitFrom){
			sb.append(" Limit ").append(limitFrom);
			if(-1!=limitTo){
				sb.append(",").append(limitTo);
			}			
		}
		cleanCondition();
	}
	
	private synchronized void cleanCondition(){
		where = "";
		groupBy = "";
		limitFrom = -1;
		limitTo = -1;
		orderBy = "";
		distinct = false;		
	}
	
	public synchronized void buildWhereSql(String whereSql){
		where = whereSql;		
	}

	public void buildGroupBySql(String byWhat) {
		groupBy = byWhat;
	}

	public void buildLimitSql(int from,int to) {
		limitFrom  = from;
		limitTo = to;
	}	
	
	public void buildLimitSql(int from) {
		buildLimitSql(from,-1);
	}	
	
	public void buildOrderBySql(String byWhat){
		orderBy = byWhat;
	}
	
	public void buildDistinctSql(boolean flag){
		distinct = flag;
	}
}
