package com.crazychen.candroid.cand.dbutil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.crazychen.candroid.cand.exception.DbException;
import com.crazychen.candroid.cand.normalutils.IOUtils;
/**
 * 数据库操作类，提供各种操作接口
 * 确保事务安全，不确保多线程安全
 * @author crazychen
 *
 */
public class DbUtil {
	//数据库名对应一个数据库操作类
	private static HashMap<String, DbUtil> DbMap = new HashMap<String, DbUtil>();
	//数据库
	private SQLiteDatabase database;
	//数据库配置类
    private DaoConfig daoConfig;
    //sql语句构造类
    private SqlBuilder mSqlBuidler = new SqlBuilder();
    private DbUtil(DaoConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("daoConfig may not be null");
        }
        this.database = createDatabase(config);
        this.daoConfig = config;
    }


    private synchronized static DbUtil getInstance(DaoConfig daoConfig) {
        DbUtil dao = DbMap.get(daoConfig.getDbName());
        if (dao == null) {
            dao = new DbUtil(daoConfig);
            DbMap.put(daoConfig.getDbName(), dao);
        } else {
            dao.daoConfig = daoConfig;
        }
       
        SQLiteDatabase database = dao.database;
        int oldVersion = database.getVersion();
        int newVersion = daoConfig.getDbVersion();
        if (oldVersion != newVersion) {
            if (oldVersion != 0) {
                DbUpgradeListener upgradeListener = daoConfig.getDbUpgradeListener();
                if (upgradeListener != null) {
                    upgradeListener.onUpgrade(dao, oldVersion, newVersion);
                } else {
                    try {
                        dao.dropDb();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }
            database.setVersion(newVersion);
        }
        return dao;
    }
    
    public static DbUtil create(Context context) {
        DaoConfig config = new DaoConfig(context);
        return getInstance(config);
    }

    public static DbUtil create(Context context, String dbName) {
        DaoConfig config = new DaoConfig(context);
        config.setDbName(dbName);
        return getInstance(config);
    }
    
    public static DbUtil create(Context context, String dbName, int dbVersion, DbUpgradeListener dbUpgradeListener) {
        DaoConfig config = new DaoConfig(context);
        config.setDbName(dbName);
        config.setDbVersion(dbVersion);
        config.setDbUpgradeListener(dbUpgradeListener);
        return getInstance(config);
    }
    
    /**
     * 创建数据库
     * @param config
     * @return
     */
    private SQLiteDatabase createDatabase(DaoConfig config) {
        SQLiteDatabase result = null;       
        result = config.getContext().openOrCreateDatabase(config.getDbName(), 0, null);        
        return result;
    }
    
    /**
     * insert新数据
     * @param entity
     * @throws DbException
     */
    public void insert(Object entity) throws DbException {
        try {   
        	database.beginTransaction();
            createTableIfNotExist(entity.getClass());            
            execNonQuery(mSqlBuidler.buildInsertSql(entity));     
            database.setTransactionSuccessful();
        } finally {
           database.endTransaction();
        }
    }

    /**
     * updates数据
     * 如果之前没有调用where()函数，将会更新与entity相同id的条目
     * 否则更新受where语句约束的条目
     * @param entity
     * @throws DbException
     */
    public void update(Object entity) throws DbException {
    	 try {           
    		 database.beginTransaction();
             createTableIfNotExist(entity.getClass());            
             execNonQuery(mSqlBuidler.buildUpdateSql(entity));     
             database.setTransactionSuccessful();
         } finally {
        	 database.endTransaction();
         }
    }      
    
    /**
     * delete数据
     * 如果之前没有调用where()函数，将会更新与entity相同id的条目
     * 否则更新受where语句约束的条目
     * @param entity
     * @throws DbException
     */
    public void delete(Object entity) throws DbException{
    	 try {           
    		 database.beginTransaction();
             createTableIfNotExist(entity.getClass());            
             execNonQuery(mSqlBuidler.buildDeleteSql(entity));  
             database.setTransactionSuccessful();
         } finally {
        	 database.endTransaction();
         }
    }
    
    /**
     * delete数据
     * 如果之前没有调用where()函数，将会更新与entity相同id的条目
     * 否则抛出异常
     * @param entity
     * @throws DbException
     */
    public void delete(Class<?> clazz) throws DbException{
   	 	try {           
   	 		database.beginTransaction();
            createTableIfNotExist(clazz);            
            execNonQuery(mSqlBuidler.buildDeleteSql(clazz));    
            database.setTransactionSuccessful();
        } finally {
        	database.endTransaction();
        }
    }
    
    /**
     * delete所有数据     
     * @param entity
     * @throws DbException
     */
    public void deleteAll(Class<?> clazz) throws DbException{
   	 	try {           
   	 		database.beginTransaction();
            createTableIfNotExist(clazz);            
            execNonQuery(mSqlBuidler.buildDeleteSql(clazz));  
            database.setTransactionSuccessful();
        } finally {
        	database.endTransaction();
        }
    }
   
    /**
     * 根据id删除数据
     * @param clazz
     * @param id
     * @throws DbException
     */
    public void deleteById(Class<?> clazz,int id) throws DbException{
   	 	try {           
   	 		database.beginTransaction();
            createTableIfNotExist(clazz);            
            execNonQuery(mSqlBuidler.buildDeleteByIdSql(clazz, id));
            database.setTransactionSuccessful();
        } finally {
        	database.endTransaction();
        }
    }
    
    /**
     * select所有数据
     * @param clazz
     * @return
     * @throws DbException
     */
    public <T> List<T> select(Class<T> clazz) throws DbException {       	
        createTableIfNotExist(clazz);            
        List<T> result = new ArrayList<T>();
        Cursor cursor = execQuery(mSqlBuidler.buildSelectSql(clazz));
        
        if (cursor != null) {
            try {
                CursorUtil.getEntityFromCursor(cursor, result, clazz);
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }      
		return result;
    }
    
    /**
     * 通过id select
     * @param clazz
     * @param id
     * @return
     * @throws DbException
     */
    public <T> List<T> findById(Class<T> clazz,int id) throws DbException {    	  
        createTableIfNotExist(clazz);            
        List<T> result = new ArrayList<T>();
        Cursor cursor = execQuery(mSqlBuidler.buildFindByIdSql(clazz,id));
        if (cursor != null) {
            try {
                CursorUtil.getEntityFromCursor(cursor, result, clazz);
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }      
		return result;
    }
    
    /**
     * 查找第一条数据
     * @param clazz
     * @return
     * @throws DbException
     */
    public <T> List<T> findFirst(Class<T> clazz) throws DbException {    	  
        createTableIfNotExist(clazz);            
        List<T> result = new ArrayList<T>();
        Cursor cursor = execQuery(mSqlBuidler.buildFindFirst(clazz));
        if (cursor != null) {
            try {
                CursorUtil.getEntityFromCursor(cursor, result, clazz);
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }      
		return result;
    }
    
    /**
     * 查找前k条数据
     * @param clazz
     * @param k
     * @return
     * @throws DbException
     */
    public <T> List<T> findFirstK(Class<T> clazz,int k) throws DbException {    	  
        createTableIfNotExist(clazz);            
        List<T> result = new ArrayList<T>();
        Cursor cursor = execQuery(mSqlBuidler.buildFindFirstK(clazz,k));
        if (cursor != null) {
            try {
                CursorUtil.getEntityFromCursor(cursor, result, clazz);
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }      
		return result;
    }
    
    /**
     * 数据总数
     * @param clazz
     * @return
     * @throws DbException
     */
    public int count(Class<?> clazz) throws DbException{
    	int result = 0;
    	createTableIfNotExist(clazz);                 
        Cursor cursor = execQuery(mSqlBuidler.buildCount(clazz));
        if (cursor != null) {
            try {
                cursor.moveToFirst();
                result = cursor.getInt(0);
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }      
		return result;
	}
    
    /**
     * 查找最后一条数据
     * @param clazz
     * @return
     * @throws DbException
     */
    public <T> List<T> findLast(Class<T> clazz) throws DbException {    	  
        createTableIfNotExist(clazz);            
        List<T> result = new ArrayList<T>();
        Cursor cursor = execQuery(mSqlBuidler.buildFindLast(clazz));
        if (cursor != null) {
            try {
                CursorUtil.getEntityFromCursor(cursor, result, clazz);
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }      
		return result;
    }
    
    /**
     * 查找最后k条数据
     * @param clazz
     * @param k
     * @return
     * @throws DbException
     */
    public <T> List<T> findLastK(Class<T> clazz,int k) throws DbException {    	  
        createTableIfNotExist(clazz);            
        List<T> result = new ArrayList<T>();
        Cursor cursor = execQuery(mSqlBuidler.buildFindLastK(clazz,k));
        if (cursor != null) {
            try {
                CursorUtil.getEntityFromCursor(cursor, result, clazz);
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }      
		return result;
    }
    
    public DbUtil where(String whereSql){
    	mSqlBuidler.buildWhereSql(whereSql);
    	return this;
    }
    
    public DbUtil groupBy(String byWhat){
    	mSqlBuidler.buildGroupBySql(byWhat);
    	return this;
    }
    
    public DbUtil oderBy(String byWhat){
    	mSqlBuidler.buildOrderBySql(byWhat);
    	return this;
    }
    
    public DbUtil limit(int limitSql){
    	mSqlBuidler.buildLimitSql(limitSql);
    	return this;
    }
    
    public DbUtil limit(int limitFrom,int limitTo){
    	mSqlBuidler.buildLimitSql(limitFrom,limitTo);
    	return this;
    }
    
    public DbUtil distinct(boolean flag){
    	mSqlBuidler.buildDistinctSql(flag);
    	return this;
    }
    
    /**
     * 删除数据库
     * @throws DbException
     */
    public void dropDb() throws DbException {
        Cursor cursor = execQuery("SELECT name FROM sqlite_master WHERE type='table' AND name<>'sqlite_sequence'");
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    try {
                        String tableName = cursor.getString(0);
                        execNonQuery("DROP TABLE " + tableName);
                        MyTable.remove(tableName);
                    } catch (Throwable e) {
                        
                    }
                }

            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }
    }
    
    /**
     * 执行非查询语句
     * @param sql
     * @throws DbException
     */
    public void execNonQuery(String sql) throws DbException {      
        try {
            database.execSQL(sql);
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }
    
    /**
     * 执行查询语句
     * @param sql
     * @return
     * @throws DbException
     */
    public Cursor execQuery(String sql) throws DbException {        
        try {
            return database.rawQuery(sql, null);
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }
    
    
    /**
     * 创建table
     * @param entityType
     * @throws DbException
     */
    public void createTableIfNotExist(Class<?> entityType) throws DbException {
        if (!tableIsExist(entityType)) {
            String sql = mSqlBuidler.crateTable(entityType);
            execNonQuery(sql);         
        }
    }
    
    /**
     * 检查table是否被创建
     * @param entityType
     * @return
     * @throws DbException
     */
    public boolean tableIsExist(Class<?> entityType) throws DbException {
        MyTable table = MyTable.get(entityType);
        if (table.isCheckedDatabase()) {
            return true;
        }

        Cursor cursor = execQuery("SELECT COUNT(*) AS c FROM sqlite_master WHERE type='table' AND name='" + table.tableName + "'");
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    int count = cursor.getInt(0);
                    if (count > 0) {
                        table.setCheckedDatabase(true);
                        return true;
                    }
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }

        return false;
    }
    
    /**
     * 数据库配置
     * @author Administrator     
     */
    public static class DaoConfig {
        private Context context;
        private String dbName = "candroid.db";//默认数据库名称
        private int dbVersion = 1;
        private DbUpgradeListener dbUpgradeListener;

        public DaoConfig(Context context) {
            this.context = context.getApplicationContext();
        }

        public Context getContext() {
            return context;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            if (!TextUtils.isEmpty(dbName)) {
                this.dbName = dbName;
            }
        }

        public int getDbVersion() {
            return dbVersion;
        }

        public void setDbVersion(int dbVersion) {
            this.dbVersion = dbVersion;
        }

        public DbUpgradeListener getDbUpgradeListener() {
            return dbUpgradeListener;
        }       
        public void setDbUpgradeListener(DbUpgradeListener dbUpgradeListener) {
            this.dbUpgradeListener = dbUpgradeListener;
        }    
    }
    
    /**
     * 数据库升级接口类
     * @author Administrator   
     */
    public interface DbUpgradeListener {
        public void onUpgrade(DbUtil db, int oldVersion, int newVersion);
    }
}
