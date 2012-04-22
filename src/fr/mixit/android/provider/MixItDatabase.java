package fr.mixit.android.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import fr.mixit.android.MixItApplication;
import fr.mixit.android.provider.MixItContract.CommentsColumns;
import fr.mixit.android.provider.MixItContract.InterestsColumns;
import fr.mixit.android.provider.MixItContract.MembersColumns;
import fr.mixit.android.provider.MixItContract.SessionsColumns;
import fr.mixit.android.provider.MixItContract.SharedLinksColumns;

public class MixItDatabase extends SQLiteOpenHelper {

	static final boolean DEBUG_MODE = MixItApplication.DEBUG_MODE;
	static final String TAG = MixItDatabase.class.getSimpleName();

	static final String DATABASE_NAME = "mixit.db";

	private static final int DATABASE_VERSION_2011 = 1;
	private static final int DATABASE_VERSION_2012_FIRST = 2;
	private static final int DATABASE_VERSION_2012_SECOND = 3;
	private static final int DATABASE_VERSION_2012 = 10;
	private static final int DATABASE_VERSION = DATABASE_VERSION_2012_SECOND;
	
	static final String LEFT_OUTER_JOIN = " LEFT OUTER JOIN ";
	static final String ON = " ON ";
	static final String EQUAL = " = ";
	static final String DOT = ".";
	
	public interface MembersBadges {
		String MEMBER_ID = "member_id";
		String BADGE_ID = "badge_id";
	}
	
	public class Badges {
	    public static final String CONTENT_TYPE =
	            "vnd.android.cursor.dir/vnd.mixit.badge";
	    public static final String CONTENT_ITEM_TYPE =
	            "vnd.android.cursor.item/vnd.mixit.badge";
	}

	public interface MembersInterests {
		String MEMBER_ID = "member_id";
		String INTEREST_ID = "interest_id";
	}

	public interface MembersLinks {
		String MEMBER_ID = "member_id";
		String LINK_ID = "link_id";
	}
	
	public interface SessionsSpeakers {
		String SESSION_ID = "session_id";
		String SPEAKER_ID = "speaker_id";
	}

	public interface SessionsInterests {
		String SESSION_ID = "session_id";
		String INTEREST_ID = "interest_id";
	}

	public interface SessionsComments {
		String SESSION_ID = "session_id";
		String COMMENT_ID = "comment_id";
	}

	interface Tables {
		String INTERESTS = "interests";
		String MEMBERS = "members";
		String SHARED_LINKS= "shared_links";
		String SESSIONS = "sessions";
		String COMMENTS = "comments";
		
		String MEMBERS_BADGES = "members_badges";
		String MEMBERS_INTERESTS = "members_interests";
		String MEMBERS_LINKS = "members_links";

		String SESSIONS_SPEAKERS = "sessions_speakers";
		String SESSIONS_INTERESTS = "sessions_interests";
		String SESSIONS_COMMENTS = "sessions_comments";
		
		String MEMBERS_BADGES_JOIN_MEMBERS = MEMBERS_BADGES
				+ LEFT_OUTER_JOIN + MEMBERS
				+ ON + MEMBERS_BADGES + DOT + MembersBadges.MEMBER_ID + EQUAL + MEMBERS + DOT + MembersColumns.MEMBER_ID;
		String MEMBERS_INTERESTS_JOIN_INTERESTS = MEMBERS_INTERESTS
				+ LEFT_OUTER_JOIN + INTERESTS
				+ ON + MEMBERS_INTERESTS + DOT + MembersInterests.INTEREST_ID + EQUAL + INTERESTS + DOT + InterestsColumns.INTEREST_ID;
		String MEMBERS_INTERESTS_JOIN_MEMBERS = MEMBERS_INTERESTS
				+ LEFT_OUTER_JOIN + MEMBERS
				+ ON + MEMBERS_INTERESTS + DOT + MembersInterests.MEMBER_ID + EQUAL + MEMBERS + DOT + MembersColumns.MEMBER_ID;
		String MEMBERS_LINKS_JOIN_MEMBERS = MEMBERS_LINKS
				+ LEFT_OUTER_JOIN + MEMBERS
				+ ON + MEMBERS_LINKS + DOT + MembersLinks.LINK_ID + EQUAL + MEMBERS + DOT + MembersColumns.MEMBER_ID;
		String MEMBERS_LINKERS_JOIN_MEMBERS = MEMBERS_LINKS
				+ LEFT_OUTER_JOIN + MEMBERS
				+ ON + MEMBERS_LINKS + DOT + MembersLinks.MEMBER_ID + EQUAL + MEMBERS + DOT + MembersColumns.MEMBER_ID;
		String MEMBERS_JOIN_COMMENTS = MEMBERS
				+ LEFT_OUTER_JOIN + COMMENTS
				+ ON + MEMBERS + DOT + MembersColumns.MEMBER_ID + EQUAL + COMMENTS + DOT + CommentsColumns.AUTHOR_ID;
		String SESSIONS_SPEAKERS_JOIN_MEMBERS = SESSIONS_SPEAKERS
				+ LEFT_OUTER_JOIN + MEMBERS
				+ ON + SESSIONS_SPEAKERS + DOT + SessionsSpeakers.SPEAKER_ID + EQUAL + MEMBERS + DOT + MembersColumns.MEMBER_ID;
		String SESSIONS_SPEAKERS_JOIN_SESSIONS = SESSIONS_SPEAKERS
				+ LEFT_OUTER_JOIN + SESSIONS
				+ ON + SESSIONS_SPEAKERS + DOT + SessionsSpeakers.SESSION_ID + EQUAL + SESSIONS + DOT + SessionsColumns.SESSION_ID;
		String SESSIONS_INTERESTS_JOIN_INTERESTS = SESSIONS_INTERESTS
				+ LEFT_OUTER_JOIN + INTERESTS
				+ ON + SESSIONS_INTERESTS + DOT + SessionsInterests.INTEREST_ID + EQUAL + INTERESTS + DOT + InterestsColumns.INTEREST_ID;
		String SESSIONS_INTERESTS_JOIN_SESSIONS = SESSIONS_INTERESTS
				+ LEFT_OUTER_JOIN + SESSIONS
				+ ON + SESSIONS_INTERESTS + DOT + SessionsInterests.SESSION_ID + EQUAL + SESSIONS + DOT + SessionsColumns.SESSION_ID;
		String SESSIONS_COMMENTS_JOIN_COMMENTS = SESSIONS_COMMENTS
				+ LEFT_OUTER_JOIN + COMMENTS
				+ ON + SESSIONS_COMMENTS + DOT + SessionsComments.COMMENT_ID + EQUAL + COMMENTS + DOT + CommentsColumns.COMMENT_ID;
	}

	private interface References {
		String MEMBER_ID = "REFERENCES " + Tables.MEMBERS + "(" + MixItContract.Members.MEMBER_ID + ")";
		String INTEREST_ID = "REFERENCES " + Tables.INTERESTS + "(" + MixItContract.Interests.INTEREST_ID + ")";
		String SESSION_ID = "REFERENCES " + Tables.SESSIONS + "(" + MixItContract.Sessions.SESSION_ID + ")";
		String COMMENT_ID = "REFERENCES " + Tables.COMMENTS + "(" + MixItContract.Comments.COMMENT_ID + ")";
	}

	public MixItDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (DEBUG_MODE) {
			Log.d(TAG, "onCreate()");
		}

		db.execSQL("CREATE TABLE " + Tables.INTERESTS + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ InterestsColumns.INTEREST_ID + " TEXT NOT NULL,"
				+ InterestsColumns.NAME + " TEXT,"
				+ "UNIQUE (" + InterestsColumns.INTEREST_ID + ") ON CONFLICT REPLACE)");

		db.execSQL("CREATE TABLE " + Tables.MEMBERS + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MembersColumns.MEMBER_ID + " TEXT NOT NULL,"
				+ MembersColumns.LOGIN + " TEXT,"
				+ MembersColumns.EMAIL + " TEXT,"
				+ MembersColumns.FIRSTNAME + " TEXT,"
				+ MembersColumns.LASTNAME + " TEXT,"
				+ MembersColumns.COMPANY + " TEXT,"
				+ MembersColumns.SHORT_DESC + " TEXT,"
				+ MembersColumns.LONG_DESC + " TEXT,"
				+ MembersColumns.TICKET_REGISTERED + " INTEGER(1) NOT NULL DEFAULT 0,"
				+ MembersColumns.IMAGE_URL + " TEXT,"
				+ MembersColumns.NB_CONSULT + " INTEGER NOT NULL DEFAULT 0,"
				+ "UNIQUE (" + MembersColumns.MEMBER_ID + ") ON CONFLICT REPLACE)");

		db.execSQL("CREATE TABLE " + Tables.SHARED_LINKS + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ SharedLinksColumns.SHARED_LINK_ID + " TEXT NOT NULL,"
				+ SharedLinksColumns.MEMBER_ID + " TEXT NOT NULL " + References.MEMBER_ID + ","
				+ SharedLinksColumns.ORDER_NUM + " INTEGER NOT NULL DEFAULT 0,"
				+ SharedLinksColumns.NAME + " TEXT,"
				+ SharedLinksColumns.URL + " TEXT,"
				+ "UNIQUE (" + SharedLinksColumns.SHARED_LINK_ID + ") ON CONFLICT REPLACE)");

		db.execSQL("CREATE TABLE " + Tables.SESSIONS + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ SessionsColumns.SESSION_ID + " TEXT NOT NULL,"
				+ SessionsColumns.TITLE + " TEXT,"
				+ SessionsColumns.SUMMARY + " TEXT,"
				+ SessionsColumns.DESC + " TEXT,"
				+ SessionsColumns.TIME + " INTEGER,"
				+ SessionsColumns.TRACK_ID + " TEXT NOT NULL DEFAULT \'\',"
				+ SessionsColumns.IS_SESSION + " INTEGER(1) NOT NULL DEFAULT 1,"
				+ SessionsColumns.NB_VOTES + " INTEGER NOT NULL DEFAULT 0,"
				+ SessionsColumns.MY_VOTE + " INTEGER(1) NOT NULL DEFAULT 0,"
				+ SessionsColumns.IS_FAVORITE + " INTEGER(1) NOT NULL DEFAULT 0,"
				+ "UNIQUE (" + SessionsColumns.SESSION_ID + ") ON CONFLICT REPLACE)");

		db.execSQL("CREATE TABLE " + Tables.COMMENTS + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ CommentsColumns.COMMENT_ID + " TEXT NOT NULL,"
				+ CommentsColumns.AUTHOR_ID + " TEXT NOT NULL " + References.MEMBER_ID + ","
				+ CommentsColumns.CONTENT + " TEXT,"
				+ CommentsColumns.PUBLISH_DATE + " INTEGER,"
				+ CommentsColumns.SESSION_ID + " TEXT " + References.SESSION_ID + ","
				+ "UNIQUE (" + CommentsColumns.COMMENT_ID + ") ON CONFLICT REPLACE)");

		db.execSQL("CREATE TABLE " + Tables.MEMBERS_BADGES + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MembersBadges.MEMBER_ID + " TEXT NOT NULL " + References.MEMBER_ID + ","
				+ MembersBadges.BADGE_ID + " TEXT NOT NULL ,"
				+ "UNIQUE (" + MembersBadges.MEMBER_ID + "," + MembersBadges.BADGE_ID + ") ON CONFLICT REPLACE)");

		db.execSQL("CREATE TABLE " + Tables.MEMBERS_INTERESTS + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MembersInterests.MEMBER_ID + " TEXT NOT NULL " + References.MEMBER_ID + ","
				+ MembersInterests.INTEREST_ID + " TEXT NOT NULL " + References.INTEREST_ID + ","
				+ "UNIQUE (" + MembersInterests.MEMBER_ID + "," + MembersInterests.INTEREST_ID + ") ON CONFLICT REPLACE)");

		db.execSQL("CREATE TABLE " + Tables.MEMBERS_LINKS + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MembersLinks.MEMBER_ID + " TEXT NOT NULL " + References.MEMBER_ID + ","
				+ MembersLinks.LINK_ID + " TEXT NOT NULL " + References.MEMBER_ID + ","
				+ "UNIQUE (" + MembersLinks.MEMBER_ID + "," + MembersLinks.LINK_ID + ") ON CONFLICT REPLACE)");

		db.execSQL("CREATE TABLE " + Tables.SESSIONS_SPEAKERS + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ SessionsSpeakers.SESSION_ID + " TEXT NOT NULL " + References.SESSION_ID + ","
				+ SessionsSpeakers.SPEAKER_ID + " TEXT NOT NULL " + References.MEMBER_ID + ","
				+ "UNIQUE (" + SessionsSpeakers.SESSION_ID + "," + SessionsSpeakers.SPEAKER_ID + ") ON CONFLICT REPLACE)");

		db.execSQL("CREATE TABLE " + Tables.SESSIONS_INTERESTS + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ SessionsInterests.SESSION_ID + " TEXT NOT NULL " + References.SESSION_ID + ","
				+ SessionsInterests.INTEREST_ID + " TEXT NOT NULL " + References.INTEREST_ID + ","
				+ "UNIQUE (" + SessionsInterests.SESSION_ID + "," + SessionsInterests.INTEREST_ID + ") ON CONFLICT REPLACE)");

		db.execSQL("CREATE TABLE " + Tables.SESSIONS_COMMENTS + " ("
				+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ SessionsComments.SESSION_ID + " TEXT NOT NULL " + References.SESSION_ID + ","
				+ SessionsComments.COMMENT_ID + " TEXT NOT NULL " + References.COMMENT_ID + ","
				+ "UNIQUE (" + SessionsComments.SESSION_ID + "," + SessionsComments.COMMENT_ID + ") ON CONFLICT REPLACE)");

		createIndices(db);
	}

	private static void createIndices(SQLiteDatabase db) {
		db.execSQL("CREATE INDEX " + Tables.INTERESTS + "_" + MixItContract.Interests.INTEREST_ID + "_IDX ON "
				+ Tables.INTERESTS + "(" + MixItContract.Interests.INTEREST_ID + ")");

		db.execSQL("CREATE INDEX " + Tables.MEMBERS + "_" + MixItContract.Members.MEMBER_ID + "_IDX ON "
				+ Tables.MEMBERS + "(" + MixItContract.Members.MEMBER_ID + ")");
		
		db.execSQL("CREATE INDEX " + Tables.SHARED_LINKS + "_" + MixItContract.SharedLinks.SHARED_LINK_ID + "_IDX ON "
				+ Tables.SHARED_LINKS + "(" + MixItContract.SharedLinks.SHARED_LINK_ID + ")");

		db.execSQL("CREATE INDEX " + Tables.SHARED_LINKS + "_" + MixItContract.SharedLinks.MEMBER_ID + "_IDX ON "
				+ Tables.SHARED_LINKS + "(" + MixItContract.SharedLinks.MEMBER_ID + ")");

		db.execSQL("CREATE INDEX " + Tables.SESSIONS + "_" + MixItContract.Sessions.SESSION_ID + "_IDX ON "
				+ Tables.SESSIONS + "(" + MixItContract.Sessions.SESSION_ID + ")");
		
		db.execSQL("CREATE INDEX " + Tables.SESSIONS + "_" + MixItContract.Sessions.TRACK_ID + "_IDX ON "
				+ Tables.SESSIONS + "(" + MixItContract.Sessions.TRACK_ID + ")");
		
		db.execSQL("CREATE INDEX " + Tables.COMMENTS + "_" + MixItContract.Comments.COMMENT_ID + "_IDX ON "
				+ Tables.COMMENTS + "(" + MixItContract.Comments.COMMENT_ID + ")");
		
		db.execSQL("CREATE INDEX " + Tables.COMMENTS + "_" + MixItContract.Comments.AUTHOR_ID + "_IDX ON "
				+ Tables.COMMENTS + "(" + MixItContract.Comments.AUTHOR_ID + ")");
		
		db.execSQL("CREATE INDEX " + Tables.COMMENTS + "_" + MixItContract.Comments.SESSION_ID + "_IDX ON "
				+ Tables.COMMENTS + "(" + MixItContract.Comments.SESSION_ID + ")");
		
		db.execSQL("CREATE INDEX " + Tables.MEMBERS_BADGES + "_" + MembersBadges.MEMBER_ID + "_IDX ON "
				+ Tables.MEMBERS_BADGES + "(" + MembersBadges.MEMBER_ID + ")");

		db.execSQL("CREATE INDEX " + Tables.MEMBERS_BADGES + "_" + MembersBadges.BADGE_ID + "_IDX ON "
				+ Tables.MEMBERS_BADGES + "(" + MembersBadges.BADGE_ID + ")");

		db.execSQL("CREATE INDEX " + Tables.MEMBERS_INTERESTS + "_" + MembersInterests.MEMBER_ID + "_IDX ON "
				+ Tables.MEMBERS_INTERESTS + "(" + MembersInterests.MEMBER_ID + ")");

		db.execSQL("CREATE INDEX " + Tables.MEMBERS_INTERESTS + "_" + MembersInterests.INTEREST_ID + "_IDX ON "
				+ Tables.MEMBERS_INTERESTS + "(" + MembersInterests.INTEREST_ID + ")");

		db.execSQL("CREATE INDEX " + Tables.MEMBERS_LINKS + "_" + MembersLinks.MEMBER_ID + "_IDX ON "
				+ Tables.MEMBERS_LINKS + "(" + MembersLinks.MEMBER_ID + ")");

		db.execSQL("CREATE INDEX " + Tables.MEMBERS_LINKS + "_" + MembersLinks.LINK_ID + "_IDX ON "
				+ Tables.MEMBERS_LINKS + "(" + MembersLinks.LINK_ID + ")");

		db.execSQL("CREATE INDEX " + Tables.SESSIONS_SPEAKERS + "_" + SessionsSpeakers.SESSION_ID + "_IDX ON "
				+ Tables.SESSIONS_SPEAKERS + "(" + SessionsSpeakers.SESSION_ID + ")");

		db.execSQL("CREATE INDEX " + Tables.SESSIONS_SPEAKERS + "_" + SessionsSpeakers.SPEAKER_ID + "_IDX ON "
				+ Tables.SESSIONS_SPEAKERS + "(" + SessionsSpeakers.SPEAKER_ID + ")");

		db.execSQL("CREATE INDEX " + Tables.SESSIONS_INTERESTS + "_" + SessionsInterests.SESSION_ID + "_IDX ON "
				+ Tables.SESSIONS_INTERESTS + "(" + SessionsInterests.SESSION_ID + ")");

		db.execSQL("CREATE INDEX " + Tables.SESSIONS_INTERESTS + "_" + SessionsInterests.INTEREST_ID + "_IDX ON "
				+ Tables.SESSIONS_INTERESTS + "(" + SessionsInterests.INTEREST_ID + ")");

		db.execSQL("CREATE INDEX " + Tables.SESSIONS_COMMENTS + "_" + SessionsComments.SESSION_ID + "_IDX ON "
				+ Tables.SESSIONS_COMMENTS + "(" + SessionsComments.SESSION_ID + ")");

		db.execSQL("CREATE INDEX " + Tables.SESSIONS_COMMENTS + "_" + SessionsComments.COMMENT_ID + "_IDX ON "
				+ Tables.SESSIONS_COMMENTS + "(" + SessionsComments.COMMENT_ID + ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (DEBUG_MODE) {
			Log.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
		}

		int version = oldVersion;

		switch (version) {
		case DATABASE_VERSION_2011:
			db.execSQL("DROP TABLE IF EXISTS sessions");
			db.execSQL("DROP TABLE IF EXISTS speakers");
			db.execSQL("DROP TABLE IF EXISTS slots");
			db.execSQL("DROP TABLE IF EXISTS tracks");
			db.execSQL("DROP TABLE IF EXISTS tags");
			db.execSQL("DROP TABLE IF EXISTS sessions_speakers");
			db.execSQL("DROP TABLE IF EXISTS sessions_tags");
			db.execSQL("DROP TABLE IF EXISTS sync");
			db.execSQL("DROP TRIGGER IF EXISTS sessions_search_insert");
			db.execSQL("DROP TRIGGER IF EXISTS sessions_search_delete");
			db.execSQL("DROP TRIGGER IF EXISTS sessions_search_update");
			db.execSQL("DROP TABLE IF EXISTS sessions_search");
			db.execSQL("DROP TRIGGER IF EXISTS speakers_search_insert");
			db.execSQL("DROP TRIGGER IF EXISTS speakers_search_delete");
			db.execSQL("DROP TRIGGER IF EXISTS speakers_search_update");
			db.execSQL("DROP TABLE IF EXISTS speakers_search");
			db.execSQL("DROP TABLE IF EXISTS search_suggest");
			version = DATABASE_VERSION_2012_FIRST;
		case DATABASE_VERSION_2012_FIRST:
			db.execSQL("DROP TABLE IF EXISTS " + Tables.INTERESTS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.MEMBERS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.SHARED_LINKS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.SESSIONS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.COMMENTS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.MEMBERS_BADGES);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.MEMBERS_INTERESTS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.MEMBERS_LINKS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.SESSIONS_SPEAKERS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.SESSIONS_INTERESTS);
			db.execSQL("DROP TABLE IF EXISTS " + Tables.SESSIONS_COMMENTS);
			version = DATABASE_VERSION_2012_SECOND;
		case DATABASE_VERSION_2012_SECOND:
		}

		onCreate(db);
	}

}
