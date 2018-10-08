package com.google.android.gms.samples.vision.ocrreader.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ocrcamera.db";
    public static final String TRANSACTION_TABLE_NAME = "detections";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " +TRANSACTION_TABLE_NAME+
                        "(id integer primary key, txt text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+TRANSACTION_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertTransaction (String txt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("txt", txt);

        db.insert(TRANSACTION_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TRANSACTION_TABLE_NAME+" where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TRANSACTION_TABLE_NAME);
        return numRows;
    }

    public ArrayList<String> getAllTransaction() {
        ArrayList<String> results = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TRANSACTION_TABLE_NAME+" ORDER BY id DESC", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            results.add(res.getString(res.getColumnIndex("txt")));
            res.moveToNext();
        }
        return results;
    }



    public void delateAllTransaction() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TRANSACTION_TABLE_NAME);
    }
}
