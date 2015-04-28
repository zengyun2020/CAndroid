package com.crazychen.candroid.cand.dbutil;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.crazychen.candroid.cand.exception.DbException;
import com.crazychen.candroid.cand.normalutils.IOUtils;
public class DbUtil {
	private static HashMap<String, DbUtil> DbMap = new HashMap<String, DbUtil>();
	private SQLiteDatabase database;
    private DaoConfig daoConfig;
    
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
                        //
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
    
    public void save(Object entity) throws DbException {
        try {           
            createTableIfNotExist(entity.getClass());            
            execNonQuery(SqlBuilder.save(entity));           
        } finally {
           
        }
    }

    /**
     * 删除表
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
            String sql = SqlBuilder.crateTable(entityType);
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

        private String dbDir;

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
