package com.myfirstwork.myfirstwork.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class DefaultData {
    public static void createTagsName(SQLiteDatabase database){
        String [] names = {"IT", "Строительство", "Транспорт", "Образование", "Медицина","Менеджмент"};
        for (String name : names){
        ContentValues values = new ContentValues();
        values.put(DBHelper.TAGS_NAME,name);
        database.insert(DBHelper.TABLE_TAGS,null,values);
        }

    }
}
