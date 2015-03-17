package brandpost.dev.flashback_2.database;

import android.provider.BaseColumns;

/**
 * Created by Viktor on 2015-01-20.
 */
public class UserCommentDatabaseHelper {

	public static abstract class UserCommentEntry implements BaseColumns {
		public static final String TABLE_NAME = "usercomments";

		public static final String COLUMN_MY_ID = "myid";
		public static final String COLUMN_COMMENTED_USER_ID = "commenteduserid";
		public static final String COLUMN_COMMENT_TEXT = "commenttext";
	}
}
