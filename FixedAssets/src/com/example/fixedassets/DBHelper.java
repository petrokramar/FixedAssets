package com.example.fixedassets;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DBHelper extends SQLiteOpenHelper {

	@Override
	public void onOpen(SQLiteDatabase db) {
		if (db != null) {
			
		}
	}

	private static final String TAG = "MyLog";
	
    public DBHelper(Context context) {
      // конструктор суперкласса
      super(context, "fixed_assets.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      Log.d(TAG, "--- onCreate database ---");

      // создаем таблицу с полями
      db.execSQL("create table assets ("
              + "id integer primary key autoincrement," 
              + "code text,"
              + "name text,"
              + "number text,"
              + "code_department text,"
              + "code_person text)");
      db.execSQL("create table departments ("
              + "id integer primary key autoincrement," 
              + "code text,"
              + "name text)");
      db.execSQL("create table persons ("
              + "id integer primary key autoincrement," 
              + "code text,"
              + "name text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
    	db.execSQL("drop table if exists assets");
        db.execSQL("drop table if exists departments");
        db.execSQL("drop table if exists persons");
        onCreate(db);
        */
    }
    
	public Asset getAsset(String number) {
        String selectQuery = "select a.code as a_code, a.name as a_name, a.number as a_number, d.name as d_name, " + 
        	" p.name as p_name from assets as a left outer join departments as d left outer join persons as p " +
        	" where a.number = " +
        	number + " and a.code_department = d.code " +
        	" and a.code_person = p.code";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
 
        if (c != null) {
        	if (c.moveToFirst()) {
        		
        		for (String s : c.getColumnNames()) {
					Log.d(TAG, s);
				}
					
        		Asset asset = new Asset();
                asset.setCode(c.getString(c.getColumnIndex("a_code")));
                asset.setName(c.getString(c.getColumnIndex("a_name")));
                asset.setNumber(c.getString(c.getColumnIndex("a_number")));
                asset.setDepartment(c.getString(c.getColumnIndex("d_name")));
                asset.setPerson(c.getString(c.getColumnIndex("p_name")));
/*
                Log.d(TAG, c.getString(c.getColumnIndex("a.code")));
				Log.d(TAG, c.getString(c.getColumnIndex("assets.name")));
				Log.d(TAG, ""+c.getColumnIndex("assets.name"));
				Log.d(TAG, c.getString(c.getColumnIndex("a.number")));
				Log.d(TAG, c.getString(c.getColumnIndex("d.name")));
				Log.d(TAG, ""+c.getColumnIndex("d.name"));
				Log.d(TAG, c.getString(c.getColumnIndex("p.name")));
				Log.d(TAG, ""+c.getColumnIndex("p.name"));
*/                
                return asset;    
            }else{
                return null;
            }	
		} else {
            return null;
		}
        
 
    }

}
    