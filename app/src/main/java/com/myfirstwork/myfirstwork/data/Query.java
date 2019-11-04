package com.myfirstwork.myfirstwork.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.myfirstwork.myfirstwork.data.source.Tag;

import java.util.ArrayList;
import java.util.List;

public class Query {

    private DBHelper dbHelper;
    private SQLiteDatabase database;

    public Query(Context context){
        this.dbHelper=new DBHelper(context);
        this.database = dbHelper.getReadableDatabase();
    }

    public void close(){
        database.close();
        dbHelper.close();
    }

    public List<Tag> getTags(){
        ArrayList <Tag> tags = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * from "+DBHelper.TABLE_TAGS ,null);
        cursor.moveToNext();
        for (int i= 0;!cursor.isAfterLast();i++){
            Tag tag = new Tag();
            tag.setId(cursor.getInt(cursor.getColumnIndex(DBHelper.ID)));
            tag.setName(cursor.getString(cursor.getColumnIndex(DBHelper.TAGS_NAME)));
            tags.add(tag);
            cursor.moveToNext();
        }
        return tags;
    }

    public Tag getTagByID(int id){
        Tag tag = new Tag();
        Cursor cursor = database.rawQuery("SELECT * FROM "+DBHelper.TABLE_TAGS+" WHERE " +DBHelper.ID+" = "+id,null);
        cursor.moveToNext();
        tag.setName(cursor.getString(cursor.getColumnIndex(DBHelper.TAGS_NAME)));
        tag.setId(cursor.getInt(cursor.getColumnIndex(DBHelper.ID)));
        return tag;
    }

    public void insertTag(Tag tag){
        ContentValues values = new ContentValues();
        values.put(DBHelper.ID,tag.getId());
        values.put(DBHelper.TAGS_NAME,tag.getName());
        database.insert(DBHelper.TABLE_TAGS,null,values);
    }
}
