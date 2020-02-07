package com.lchj.ydyypt.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.lchj.ydyypt.bean.AppBean;
import com.lchj.ydyypt.bean.AppModule;
import com.lchj.ydyypt.bean.dao.DaoMaster;

import org.greenrobot.greendao.database.Database;

import static com.lchj.ydyypt.bean.dao.DaoMaster.dropAllTables;

public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
    public MySQLiteOpenHelper(Context context, String name) {
        super(context, name);
    }

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name);
    }

    @Override
    public void onCreate(Database db) {
        super.onCreate(db);
        /**
         * 这个方法
         * 1、在第一次打开数据库的时候才会走
         * 2、在清除数据之后再次运行-->打开数据库，这个方法会走
         * 3、没有清除数据，不会走这个方法
         * 4、数据库升级的时候这个方法不会走
         */

    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        dropAllTables(db, true);
        onCreate(db);
        super.onUpgrade(db, oldVersion, newVersion);
        /**
         * 1、第一次创建数据库的时候，这个方法不会走
         * 2、清除数据后再次运行(相当于第一次创建)这个方法不会走
         * 3、数据库已经存在，而且版本升高的时候，这个方法才会调用
         */
//        if (oldVersion < newVersion) {
//            // 需要进行数据迁移更新的实体类 ，新增的不用加
////            MigrationHelper.getInstance().migrate(db, AppBean.class);
//            MigrationHelper.getInstance().migrate(db, AppBean.class);
//        }

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        /**
         * 执行数据库的降级操作
         * 1、只有新版本比旧版本低的时候才会执行
         * 2、如果不执行降级操作，会抛出异常
         */

    }
}
