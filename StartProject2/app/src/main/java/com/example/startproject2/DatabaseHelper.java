package com.example.startproject2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "movie";
    public static final String MOVIE_ID = "_id";
    public static final String MOVIE_TITLE = "title";
    public static final String MOVIE_DIRECTOR = "director";
    public static final String MOVIE_ACTOR = "actor";
    public static final String MOVIE_LINK = "link";
    public static final String MOVIE_RATING = "rating";
    public static final String MOVIE_IMAGE = "image";
    public static final String MOVIE_PUBDATE = "pubDate";

    public static final String [] ALL_COLUMNS = {MOVIE_ID, MOVIE_TITLE, MOVIE_DIRECTOR, MOVIE_ACTOR, MOVIE_LINK, MOVIE_RATING, MOVIE_IMAGE, MOVIE_PUBDATE};

    private static final String CREATE_TABLE = "create table " + TABLE_NAME + " (" +
            MOVIE_ID + " integer primary key autoincrement, " +
            MOVIE_TITLE + " text, " +
            MOVIE_DIRECTOR + " text, " +
            MOVIE_ACTOR + " text, " +
            MOVIE_LINK + " text, " +
            MOVIE_RATING + " real, " +
            MOVIE_IMAGE + " text, " +
            MOVIE_PUBDATE + " text)";


    public DatabaseHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}