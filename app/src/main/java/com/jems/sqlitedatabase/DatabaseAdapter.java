package com.jems.sqlitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseAdapter {
    private DBHelper helper;

    public DatabaseAdapter(Context context) {
        helper = new DBHelper(context);
    }

    /////////////insert Data /////////////////
    public long insertData(String name, String password) {
        SQLiteDatabase database = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(DBHelper.NAME, name);
        contentValues.put(DBHelper.PASSWORD, password);

        //if insertion successfull then it return the inserted row id other wise it return negative value
        long result = database.insert(DBHelper.TABLE_NAME, null, contentValues);

        database.close();

        return result;
    }

    //////////  Fetch ALl Records  ////////////////
    public ArrayList<String> fetchAllRecords() {
        ArrayList<String> arrayList = new ArrayList<>();
        String data;
        SQLiteDatabase database = helper.getReadableDatabase();
        //second argument if we pass null then it fetch * all coloums
        //database.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);

        // OR
        //if we want only some selected coloums then we pass string array of coloums, pass it as second argument...
        String[] coloums = {DBHelper.UID, DBHelper.NAME, DBHelper.PASSWORD};
        Cursor cursor = database.query(DBHelper.TABLE_NAME, coloums, null, null, null, null, null);
        while (cursor.moveToNext()) {
          /* //that is not a good practice may be in future coloms index changes, so instead of doing static stuff do work dynamically
           int id = cursor.getInt(0);
           String name = cursor.getString(1);
           String password = cursor.getString(2);*/

            //best practice
            int index_uid = cursor.getColumnIndex(DBHelper.UID);
            int index_name = cursor.getColumnIndex(DBHelper.NAME);
            int index_password = cursor.getColumnIndex(DBHelper.PASSWORD);

            int id = cursor.getInt(index_uid);
            String name = cursor.getString(index_name);
            String password = cursor.getString(index_password);

            data = (id + " " + name + " " + password);
            arrayList.add(data);
        }

        database.close();

        return arrayList;
    }

    //////////////////////////  Search User Record  //////////////////////////
    public String searchRecord(String searchText) {
        StringBuffer buffer = new StringBuffer();
        SQLiteDatabase database = helper.getReadableDatabase();

        String[] coloums = {DBHelper.UID, DBHelper.NAME};

        String whereClause = DBHelper.NAME + " =? OR " + DBHelper.PASSWORD + " =?"; //nothing but where condition
        String[] selectionArgs = {searchText}; //parameters we want to match from database values respectively
        Cursor cursor = database.query(DBHelper.TABLE_NAME, coloums, whereClause, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            int index_uid = cursor.getColumnIndex(DBHelper.UID);
            int index_name = cursor.getColumnIndex(DBHelper.NAME);

            int id = cursor.getInt(index_uid);
            String getName = cursor.getString(index_name);

            buffer.append(id + " " + getName + "\n");
        }

        database.close();

        // buffer.toString(); convert string buffer object to string
        // cursor.getCount(); total results that we get from databases
        return buffer.toString() + "Total Records Found : " + cursor.getCount();
    }

    //////////  Update User Record  ////////////////
    public int updateRecord(String id, String uName, String uPassword) {
        SQLiteDatabase database = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.NAME, uName);
        contentValues.put(DBHelper.PASSWORD, uPassword);

        String whereClause = DBHelper.UID + " =?";
        String[] whereArgs = new String[]{id};
        int result = database.update(DBHelper.TABLE_NAME, contentValues, whereClause, whereArgs);
        database.close();

        return result;
    }

    //////////////////////////  Delete User Record  //////////////////////////
    public int deleteRecord(String id) {
        SQLiteDatabase database = helper.getWritableDatabase();
        String whereClause = DBHelper.UID + " = ?";

      /*
        String[] whereArgs = {id};
        int count = database.delete(DBHelper.TABLE_NAME, whereClause, whereArgs);
     */
        //OR Reccomended Approuch

        int count = database.delete(DBHelper.TABLE_NAME, whereClause, new String[]{id});
        database.close();
        return count;
    }


    ////////////// Static Inner Class ////////////////
    static class DBHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "user_database";
        private static final String TABLE_NAME = "user_table";
        private static final int DATABASE_VERSION = 3;
        private static final String UID = "_id";
        private static final String NAME = "name";
        private static final String PASSWORD = "password";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME
                + " VARCHAR(255) NOT NULL, " + PASSWORD + " VARCHAR(255));";
        /*THIS IS SQLITE SEMICOLEN ");" AND THAT IS OPTIONAL, & LAST SEMICOLON IS JAVA STATEMENT TERMINATOR SEMICOLON*/
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        private Context context;

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;

            //ToastMessage.show(context, "Constructor Called...");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
            ToastMessage.show(context, "onCreate Called...Database Created!!");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            ToastMessage.show(context, "onUpgrade Called...");
            db.execSQL(DROP_TABLE);
            onCreate(db);
        }
    }
}
