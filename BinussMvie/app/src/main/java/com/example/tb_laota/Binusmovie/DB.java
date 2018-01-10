package com.example.tb_laota.Binusmovie;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bram on 1/10/2018.
 */

public class DB extends SQLiteOpenHelper {
    public DB(Context context) {
        super(context,"movie",null, 2);
    }
    public Cursor getAll(){
        SQLiteDatabase sqlite = getReadableDatabase();
        return sqlite.rawQuery("select * from user",null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user (" +
                "ID integer primary key autoincrement, " +
                "Username varchar not null, " +
                "movie varchar not null, " +
                "genre varchar not null, " +
                "year varchar not null"+");");
    }
    public void insert(String movie,String genre,String year){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into user values(" +
                "null ," +
                "'"+movie+"' ,"+
                "'"+genre+"' ,"+
                "'"+year+"'"+
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(CharSequence title, String finalGenreStr, String s) {
    }
}
