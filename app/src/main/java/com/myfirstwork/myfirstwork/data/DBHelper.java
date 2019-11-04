package com.myfirstwork.myfirstwork.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static String DATABASENAME ="MFWDatabase";
    public static int VERSION = 1;

    public static String ID="_id";
    //TAGS
    public static String TABLE_TAGS="tags_table";
    public static String TAGS_NAME="name";
    public DBHelper(Context context){
        super(context,DATABASENAME,null,VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_TAGS+" ("+
                ID+" integer not null primary key autoincrement unique, "+
                TAGS_NAME+" text not null "+
                ")");
        DefaultData.createTagsName(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("drop table if exists " + TABLE_TAGS);
            onCreate(db);
        }
    }
}
