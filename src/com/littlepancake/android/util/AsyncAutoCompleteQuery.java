package com.littlepancake.android.util;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncAutoCompleteQuery extends AsyncTask<String, Integer, ArrayList<String>> {

	private final String tag = getClass().getSimpleName();
	AutoCompleteListener caller;
	SQLiteDatabase db;
	DataBaseHelper myDbHelper;
	String type;
	String databaseName;
	Context c;
	
	private String tableName = "cards";
	private String colName   = "name";
	private int limit        = 30;

	public AsyncAutoCompleteQuery(AutoCompleteListener caller, Context c, String dbName) {
		this.caller       = caller;
		this.db           = null;
		this.databaseName = dbName;
		this.c            = c;
		
//		if( this.databaseName == null ) {
//			myDbHelper = null;
//		}
//		else {
//			//Log.d(tag, "Trying to open "+databaseName);
//			
//			myDbHelper = new DataBaseHelper(c, databaseName);
//
//			boolean result = myDbHelper.openDataBase();
//
//			if( result ) {
//				db = myDbHelper.getReadableDatabase();
//			}
//			else {
//				db = null;
//			}
//		}
	}
	
	@Override
	protected ArrayList<String> doInBackground(String... params) {
		
		if( this.databaseName == null ) {
			myDbHelper = null;
		}
		else {			
			myDbHelper = new DataBaseHelper(c, databaseName);

			boolean result = myDbHelper.openDataBase();

			if( result ) {
				db = myDbHelper.getReadableDatabase();
			}
			else {
				db = null;
			}
		}
		
		if( db == null ) {
			Log.d(tag, "db is null??");
			return null;
		}
		
		String matchingString = params[0];
		String[] name = {colName};
		matchingString = matchingString.replace("'", "''");
		String selection = colName+" LIKE '%"+matchingString+"%'";
		
		Cursor cursor = db.query(	false,     /* distinct? */
									tableName, /* Table */
									name,      /* Columns */
									selection, /* Selection */
									null,      /* Selection Args */
									null,      /* Group By */
									null,      /* Having */
									null,      /* Order By */
									String.valueOf(limit));     /* Limit */
		ArrayList<String> retList = new ArrayList<String>();
		int nameIndex = cursor.getColumnIndex(colName);
		while( cursor.moveToNext() ) {
			retList.add(cursor.getString(nameIndex));
		}
		cursor.close();
		return retList;
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(ArrayList<String> results) {
		//Log.d("AsyncQuery", "In onPostExecute()");
		
		if( db != null ) {
		//	Log.d("AsyncQuery", "db was not null.");
			myDbHelper.close();
			caller.onCompletion(results);
		}
		else {
			Log.d("AsyncQuery", "db was null.");
			caller.onCompletion(null);
		}
	}

	public void setC(Context c) {
		this.c = c;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
