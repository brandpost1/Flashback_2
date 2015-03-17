package brandpost.dev.flashback_2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Viktor on 2015-01-20.
 */
public final class ThreadDatabaseHelper extends SQLiteOpenHelper {

	private static final String DELETE_TABLES = "DROP TABLE IF EXISTS" + ThreadEntry.TABLE_NAME;
	private static final String CREATE_TABLES = "CREATE TABLE " + ThreadEntry.TABLE_NAME +
			" (" + ThreadEntry._ID + " VARCHAR(50) PRIMARY KEY," +
			ThreadEntry.COLUMN_THREAD_TITLE + " VARCHAR(50)," +
			ThreadEntry.COLUMN_THREAD_PAGE + " INTEGER," +
			ThreadEntry.COLUMN_THREAD_PARENT + " varchar(50))";

	private static final String DATABASE_NAME = "ThreadsDatabase.db";
	private static final int DATABASE_VERSION = 1;

	public ThreadDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public Cursor getThreads() {
		SQLiteDatabase database = getReadableDatabase();

		String[] selectedCols = {
				ThreadEntry._ID,                    // The url
				ThreadEntry.COLUMN_THREAD_TITLE,    // Title
		};


		return database.query(
				ThreadEntry.TABLE_NAME,             // Table name
				selectedCols,                       // Columns to get
				null,                               // WHERE-clause
				null,                               // WHERE-args
				ThreadEntry.COLUMN_THREAD_PARENT,   // Group by
				null,                               // Groups to include (null = all)
				null                                // Order
		);
	}

	public Cursor getThread(String id) {
		SQLiteDatabase database = getReadableDatabase();

		String[] selectedCols = {
				ThreadEntry._ID,
				ThreadEntry.COLUMN_THREAD_TITLE,
				ThreadEntry.COLUMN_THREAD_PAGE,
				ThreadEntry.COLUMN_THREAD_PAGE_CONTENT
		};

		String WHERE = ThreadEntry._ID + " = ?";
		String[] whereArgs = {id};

		return database.query(
				ThreadEntry.TABLE_NAME,         // From table
				selectedCols,                   // Columns
				WHERE,                          // WHERE id = ?
				whereArgs,                      // ^args
				null,                           // Group by
				null,                           // "Having" null = all rows = 1 row in this case since we are selecting by primary key
				null                            // Order
		);
	}

	public long addThread(String url, int page, String title, String parent, String threaddata) {
		SQLiteDatabase database = getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ThreadEntry._ID, url);
		values.put(ThreadEntry.COLUMN_THREAD_TITLE, title);
		values.put(ThreadEntry.COLUMN_THREAD_PAGE, page);
		values.put(ThreadEntry.COLUMN_THREAD_PARENT, parent);
		values.put(ThreadEntry.COLUMN_THREAD_PAGE_CONTENT, threaddata);

		long row = database.insert(
				ThreadEntry.TABLE_NAME,
				"null",
				values);

		return row;
	}

	public int removeThread(String url) {
		SQLiteDatabase database = getWritableDatabase();

		String query = ThreadEntry._ID + " = ?";
		String[] args = {String.valueOf(url)};

		return database.delete(
				ThreadEntry.TABLE_NAME,
				query,
				args);
	}

	public static abstract class ThreadEntry implements BaseColumns {
		public static final String TABLE_NAME = "openthreads";

		public static final String COLUMN_THREAD_TITLE = "threadtitle";             // Title of thread
		public static final String COLUMN_THREAD_PAGE = "threadpage";               // Page currently at
		public static final String COLUMN_THREAD_PAGE_CONTENT = "threadparent";     // XML Thread content
		public static final String COLUMN_THREAD_PARENT = "threadparent";           // Thread parent-forum
	}
}
