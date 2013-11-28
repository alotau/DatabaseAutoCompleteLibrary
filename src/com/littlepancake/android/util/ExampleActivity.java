package com.littlepancake.android.util;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

public class ExampleActivity extends Activity {
	private final String tag = getClass().getSimpleName();
	private final String dbName = "countries.db";
	private final String dbURL  = "http://www.littlepancake.com/app_data/test/";

	private String dbDir;

	private AutoCompleteTextView mAutoCompleteTextView;
	private AutoCompleteListener mAutoCompleteListener;
	private AsyncFetchRemoteFile asyncGetDb;

	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_example);

		dbDir = this.getDatabasePath(dbName).getAbsolutePath();
		Log.d(tag, dbDir);
		
		/* Grab handle to your AutoCompleteTextView. */
		mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_example);
		/* Create a new listener, pass in context, AutoCompleteTextView, and a database name. */
		mAutoCompleteListener = new AutoCompleteListener(this, mAutoCompleteTextView, dbName);

		/* Make some customizations as necessary. */
		mAutoCompleteListener.setLimit(20);
		mAutoCompleteListener.setColName("country");
		mAutoCompleteListener.setTableName("countries");

		/* Download a remotely-served database to back our AutoCompleteTextView. */
		if( !isNetworkAvailable() ) {
			/* All of these Log warnings would be good in a Toast/Dialog for the user. */
			Log.w(tag, "No Internet?");
			Log.w(tag, "Without an Internet connection, you will not be able to download the latest database.");
			Log.w(tag, "If you haven't ever downloaded a database, then you will not see any AutoComplete action.");
		}
		else {
			/* In a real app, you probably don't want to fetch the database EVERY time the app starts up. */
			Log.i(tag, "Data connection detected.");

			/* Create ProgressDialog to keep user notified of what's going on. */
			dialog = ProgressDialog.show(this, "", "Fetching database, please wait.", true);
			/* If the user cancels, then the db will not be downloaded. */
			dialog.setCancelable(true);

			/* In the background, go fetch the database.  You'll probably want your own database in a real app. */
			asyncGetDb = new AsyncFetchRemoteFile(dialog);
			asyncGetDb.execute(dbURL+dbName, dbDir);
		}

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
			= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
