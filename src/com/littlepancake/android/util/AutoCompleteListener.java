package com.littlepancake.android.util;

import java.util.List;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class AutoCompleteListener implements TextWatcher {
	private final String tag = getClass().getSimpleName();

	private AutoCompleteTextView autoCompleteView = null;
	private Context context = null;
	private boolean querying, selected;
	private ArrayAdapter<String> adapter = null;
	
	private AsyncAutoCompleteQuery asyncQ;

	private String dbName = null;

	private String currentText;

	private int limit;

	private String colName;

	private String tableName;

	public AutoCompleteListener() {

	}

	public AutoCompleteListener(Context c, AutoCompleteTextView autoCompleteView, String dbName) {
		this.dbName = dbName;
		this.context = c;
		this.autoCompleteView = autoCompleteView;

		this.autoCompleteView.addTextChangedListener(this);
		
	}

	public interface AsyncAutoCompleteInterface {

	}

	public void onCompletion(List<String> list) {
		Log.d(tag, "in onCompletion()");
		if( list == null || selected) {
			querying = false;
			return;
		}

		String freshestText = autoCompleteView.getText().toString();
		/* Don't make another query if the string currently in the editText
		 * field matches what we searched on earlier.
		 */
		if( freshestText.matches(currentText) ) {
			querying = false;
		}
		/* However, if they don't match, that means the user has entered
		 * more text since the last time we did a db query.
		 */
		else {
			querying = false;
		}

		/* Update the adapter with the query results.  There's a 
		 * chance the adapter is null, so be careful. */
		if( list.size() == 1 && list.get(0).matches(freshestText) ) {
			/* Only one element and it matches our text? 
			 * Assume that means we found what we needed. Kill 
			 * Adapter.
			 */
			if( adapter != null ) adapter.clear();
		}
		else {
			/* Otherwise, update the Adapter with new data. */
			adapter = new ArrayAdapter<String>(context,
					R.layout.cardname_autocomplete, list);
			autoCompleteView.setAdapter(adapter);
		}
		/* Let the View know to update. */
		if( adapter != null ) adapter.notifyDataSetChanged();

	}

	@Override
	public void afterTextChanged(Editable s) {
		/* Looks like we might be ready to edit text again, clear selected flag. */
		selected = false;		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {			
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		//Log.d(tag, "in onTextChanged()");
		/* Two things to check:  
		 *  1. Have we already launched an AsyncTask to get values?
		 *  2. Have they typed at least a few characters?
		 * If so, let's get the matching strings from db.
		 */
		if( s.length() == 0 ) {
			/* This is here in case the user selected one of the autocomplete
			 * options, then deleted the entire name to start over.
			 */
			selected = false;
		}
		else if( !querying && s.length() > 2 && !selected ) {
			querying = true;
			currentText = autoCompleteView.getText().toString();

			asyncQ = new AsyncAutoCompleteQuery(this, context, dbName);

			asyncQ.setLimit(limit);
			asyncQ.setColName(colName);
			asyncQ.setTableName(tableName);
			
			asyncQ.execute(currentText);
		}

	}

	public String getDbName() {
		return dbName;
	}

	/* Only allow setting of dbName in constructor because that is the only time it
	 * should be used. */
//	public void setDbName(String dbName) {
//		this.dbName = dbName;
//	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getColName() {
		return colName;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
