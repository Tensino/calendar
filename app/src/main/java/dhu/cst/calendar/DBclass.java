package dhu.cst.calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;

public class DBclass extends SQLiteOpenHelper implements Serializable {

    //带全部参数的构造函数，此构造函数必不可少
    public DBclass(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库sql语句 并 执行
        String sql = "create table Event(time INT primary key,Schedule VARCHAR(5000))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
