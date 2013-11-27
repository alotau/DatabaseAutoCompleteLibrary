DatabaseAutoCompleteLibrary
===========================

I've had several apps where I needed a custom AutoCompleteTextView that was backed by a database.  It always seemed like more work than it should have been.  I finally condensed my code and made an Android library project out of it.

There is not too much code to look through if you are interested.  However as a quick overview, here's how you might use it:

String dbName = "my.db";
String dbDir  = this.getDatabasePath(dbName).getAbsolutePath();
/* Grab handle to your AutoCompleteTextView. */
mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete_example);
/* Create a new listener, pass in context, AutoCompleteTextView, and a database name. */
mAutoCompleteListener = new AutoCompleteListener(this, mAutoCompleteTextView, dbName);

/* Make some customizations as necessary. */
mAutoCompleteListener.setDbName(dbName);
mAutoCompleteListener.setLimit(20);
mAutoCompleteListener.setColName("country");
mAutoCompleteListener.setTableName("countries");


Then, bing bang boom, Bob's your uncle and you're done.

Of course, this assumes you have the database stored in the right spot and that you've set the column/table names appropriately.  As a little more help in illustration, the example app included in the repo has a utility to asynchronously grab a remote sqlite database and store it on the device.

This is my first public github repo, so please let me know if anything more would be helpful.
