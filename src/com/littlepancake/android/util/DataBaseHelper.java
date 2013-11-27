package com.littlepancake.android.util;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
	//private final String tag = getClass().getSimpleName();
	
	private String path;

	public static final String TABLE_NAME  = "names";
	public static final String COLUMN_ID   = "_id";
	public static final String COLUMN_NAME = "name";

	/* Database CREATE statement, not really useful to us in this library. */
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_NAME + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_NAME
			+ " text not null);";
	
	private SQLiteDatabase myDataBase; 

	public DataBaseHelper(Context context, String dbName) {
		super(context, dbName, null, 1);
		path = context.getDatabasePath(dbName).getAbsolutePath();
		//Log.d(tag, "path (built by context) = "+path);
	}

	public boolean openDataBase() {
		//String myPath = path + databaseName;
		//Log.d(tag, "path = "+myPath);
		try {
			myDataBase = 
					SQLiteDatabase.openDatabase(
							path, 
							null, 
							SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		} catch (SQLException sqle) {
			myDataBase = null;
			//Log.d(tag, "exception in openDataBase()");
			return false;
		}
		//Log.d(tag, "returning true from openDataBase()");
		return true;

	}

	@Override
	public synchronized void close() {

		if(myDataBase != null)
			myDataBase.close();

		super.close();

	}

	/* This method not useful in this library.  We assume db exists. */
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	/* Likewise, this method not useful in this library. */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(getClass().getName(), "Upgrading database from which will destroy all old data.");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

}