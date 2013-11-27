package com.littlepancake.android.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncFetchRemoteFile extends AsyncTask<String, Integer, ArrayList<String>>{

	private final String tag = getClass().getSimpleName();

	public static final int NUM_EXPECTED_ARGS = 2;
	private static final int INDEX_URL        = 0;
	private static final int INDEX_FILE       = 1;

	private ProgressDialog progressDialog = null;

	public AsyncFetchRemoteFile() {

	}

	public AsyncFetchRemoteFile(ProgressDialog pd) {
		this.progressDialog = pd;
	}

	@Override
	protected ArrayList<String> doInBackground(String... params) {

		if( params.length != NUM_EXPECTED_ARGS ) {
			Log.d(tag, "Incorrect number of arguments. Returning null...");
			return null;
		}

		/* Expecting two parameters. 
		 * First is a complete URL to the remote file.
		 * Second is a complete local path to write the file.
		 * 
		 * These are not really checked for any sort of correctness, so expect
		 * Exceptions if you pass junk.
		 */
		String fileURLString  = params[INDEX_URL];
		String fileNameString = params[INDEX_FILE];

		try {
			File dbFile   = new File(fileNameString);
			File dbParent = new File(dbFile.getParent());
			if( dbParent.mkdirs() ) {
				//Log.d(tag, "Made dir.");
			}
			else {
				//Log.d(tag, "Didn't make dir.");
			}

			/* http://android-developers.blogspot.com.br/2011/09/androids-http-clients.html */
			/* http://stackoverflow.com/questions/9910706/how-to-write-a-potentially-huge-inputstream-to-file */
			URL url = new URL(fileURLString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				OutputStream stream = new BufferedOutputStream(new FileOutputStream(dbFile)); 
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					stream.write(buffer, 0, len);
				}
				if(stream!=null)
					stream.close();
			}

			finally {
				urlConnection.disconnect();
			}
		} catch (FileNotFoundException e) {
			Log.d(tag, "FileNotFoundException?");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d(tag, "IOException?");
			e.printStackTrace();
		}

		Log.d(tag, "Leaving doInBackground()");
		return null;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if( progressDialog != null ) progressDialog.dismiss();
	}

	@Override
	protected void onPostExecute(ArrayList<String> al) {
		if( progressDialog != null ) progressDialog.dismiss();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

}
