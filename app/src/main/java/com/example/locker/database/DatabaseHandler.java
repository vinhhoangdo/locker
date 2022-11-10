package com.example.locker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PackageManager";
    private static final String TABLE_PACKAGE = "package";

    private static final String KEY_ID = "id";
    private static final String PACKAGE_NAME = "name";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        String CREATE_PACKAGE_TABLE = "CREATE TABLE " + TABLE_PACKAGE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + PACKAGE_NAME + " TEXT)";
        db.execSQL(CREATE_PACKAGE_TABLE);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PACKAGE);
        onCreate(db);
    }

    public boolean addPackage(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PACKAGE_NAME, name);
        int result = (int) db.insert(TABLE_PACKAGE, null, values);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean getPackageName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM package WHERE TRIM(name)= '" + name.trim() + "'", null);
        if (res.getCount() <= 0) {
            res.close();
            return false;
        }
        res.close();
        return true;
    }

    public int delete(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs = {name};

        return db.delete(TABLE_PACKAGE, PACKAGE_NAME + " = ?", whereArgs);
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM package ", null);
    }
}
