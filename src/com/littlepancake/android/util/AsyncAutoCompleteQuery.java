package com.littlepancake.android.util;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncAutoCompleteQuery extends AsyncTask<String, Integer, ArrayList<String>> {

	private final String tag = getClass().getSimpleName();
	private AutoCompleteListener caller;
	private SQLiteDatabase db;
	private static DataBaseHelper myDbHelper = null;
	private String databaseName;
	private Context c;
	
	private String tableName = "cards";
	private String colName   = "name";
	private int limit        = 30;
	private String limitStr  = "30";

	public AsyncAutoCompleteQuery(AutoCompleteListener caller, Context c, String dbName) {
		this.caller       = caller;
		this.db           = null;
		this.databaseName = dbName;
		this.c            = c;
	}
	
	@Override
	protected ArrayList<String> doInBackground(String... params) {
		
		if( this.databaseName == null ) {
			myDbHelper = null;
		}
		else {			
			if( myDbHelper == null ) myDbHelper = new DataBaseHelper(c, databaseName);

			if( myDbHelper.openDataBase() ) {
				db = myDbHelper.getReadableDatabase();
			}
			else {
				db = null;
			}
		}
		
		if( db == null ) {
			Log.e(tag, "db is null?  No useful work will be done by this task.");
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
									limitStr); /* Limit */
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
		this.limitStr = String.valueOf(this.limit);
	}

}
