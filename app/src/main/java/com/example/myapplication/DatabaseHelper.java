package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "app_info.db";
    private static final String TABLE_NAME = "app_info_table";
    private static final String COL_INSTALL_TIME = "install_time";
    private static final String COL_APP_NAME = "app_name";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_INSTALL_TIME + " INTEGER, "
                + COL_APP_NAME + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void saveInstallationTimeAndAppName(long installationTime, String appName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_INSTALL_TIME, installationTime);
        contentValues.put(COL_APP_NAME, appName);
        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    // Add a method to retrieve the installation time and app name from the database
    Cursor getInstallationTimeAndAppName() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_INSTALL_TIME, COL_APP_NAME};
        return db.query(TABLE_NAME, columns, null, null, null, null, null);
    }
}
