package com.example.electronicqueue.server;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DataBaseServerElectronicQueue extends SQLiteOpenHelper implements BaseColumns {

    public static final String NAME_BASE = "SQLBase.db";
    public static final int BASE_VERSION = 1;

    public static final String NAME_TABLE = "tableWindow";
    public static final String COLUMN_NUMBER_TICKET = "numberTicket";
    public static final String COLUMN_WINDOW = "window";

    public static final String SQL_TABLE_CREATE = "CREATE TABLE " + NAME_TABLE + " (" +
            DataBaseServerElectronicQueue._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NUMBER_TICKET + " INTEGER, " +
            COLUMN_WINDOW + " INTEGER" + ");";

    public DataBaseServerElectronicQueue(Context context) {
        super(context, NAME_BASE, null, BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(MainActivity.LOG_SERVER, "--- onCreate database ---");
        db.execSQL(SQL_TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
