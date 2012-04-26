package fr.mixit.android_2012.provider;

import fr.mixit.android_2012.model.Track;
import android.net.Uri;
import android.provider.BaseColumns;

public class MixItContract {
	
	public static final String CONTENT_AUTHORITY = "fr.mixit.android";

	private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	public static final String PATH_INTERESTS = "interests";
	public static final String PATH_BADGES = "badges";
	public static final String PATH_MEMBERS = "members";
	public static final String PATH_SPEAKERS = "speakers";
	public static final String PATH_SHARED_LINKS = "shared_links";
	public static final String PATH_LINKERS = "linkers";
	public static final String PATH_LINKS = "links";
	public static final String PATH_SESSIONS = "sessions";
	public static final String PATH_LIGHTNINGS = "lightnings";
	public static final String PATH_TRACKS = "tracks";
	public static final String PATH_COMMENTS = "comments";
	

	interface InterestsColumns {
	    String INTEREST_ID = "interest_id";
	    String NAME = "interest_name";
	}
	
	public static class Interests implements InterestsColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INTERESTS).build();
		
	    public static final String CONTENT_TYPE =
	            "vnd.android.cursor.dir/vnd.mixit.interest";
	    public static final String CONTENT_ITEM_TYPE =
	            "vnd.android.cursor.item/vnd.mixit.interest";

	    public static final String SESSIONS_COUNT = "sessions_count";
	    public static final String MEMBERS_COUNT = "members_count";

	    public static final String DEFAULT_SORT = MixItDatabase.Tables.INTERESTS + "." + InterestsColumns.NAME + " ASC";

		public static Uri buildInterestUri(String interestId) {
	        return CONTENT_URI.buildUpon().appendPath(interestId).build();
		}
		
		public static Uri buildSessionsDir(String interestId) {
	        return CONTENT_URI.buildUpon().appendPath(interestId).appendPath(MixItProvider.ALL + PATH_SESSIONS).build();
		}
		
		public static Uri buildMembersDir(String interestId) {
	        return CONTENT_URI.buildUpon().appendPath(interestId).appendPath(PATH_MEMBERS).build();
		}
		
	    public static String getInterestId(Uri uri) {
	        return uri.getPathSegments().get(1);
	    }
	}
	
	
	//SessionIDs[] (dont le membre est speaker), AccountIDs[] (à minima LinkIT, mais Twitter ou Google en plus), 
	interface MembersColumns {
		String MEMBER_ID = "member_id";
		String LOGIN = "login";
		String EMAIL = "email";
		String FIRSTNAME = "firstname";
		String LASTNAME = "lastname";
		String COMPANY = "company";
		String SHORT_DESC = "short_desc";
		String LONG_DESC = "long_desc";
		String TICKET_REGISTERED = "registered";
		String IMAGE_URL = "image_url";
		String NB_CONSULT = "nb_consult";
	}
	
	public static class Members implements MembersColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MEMBERS).build();
		public static final Uri CONTENT_URI_SPEAKERS = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SPEAKERS).build();
		
	    public static final String CONTENT_TYPE =
	            "vnd.android.cursor.dir/vnd.mixit.member";
	    public static final String CONTENT_ITEM_TYPE =
	            "vnd.android.cursor.item/vnd.mixit.member";

		public static final String DEFAULT_SORT = "UPPER(" + MixItDatabase.Tables.MEMBERS + "." + MembersColumns.LASTNAME + ") ASC, " + MixItDatabase.Tables.MEMBERS + "." + MembersColumns.FIRSTNAME + " ASC";
		
		public static Uri buildMemberUri(String memberId) {
			return CONTENT_URI.buildUpon().appendPath(memberId).build();
		}

		public static Uri buildBadgesDirUri(String memberId) {
			return CONTENT_URI.buildUpon().appendPath(memberId).appendPath(PATH_BADGES).build();
		}

	    public static Uri buildMemberBadgeUri(String memberId, String badgeId) {
	        return CONTENT_URI.buildUpon().appendPath(memberId).appendPath(PATH_BADGES).appendPath(badgeId).build();
	    }

		public static Uri buildInterestsDirUri(String memberId) {
			return CONTENT_URI.buildUpon().appendPath(memberId).appendPath(PATH_INTERESTS).build();
		}

	    public static Uri buildMemberInterestUri(String memberId, String interestId) {
	        return CONTENT_URI.buildUpon().appendPath(memberId).appendPath(PATH_INTERESTS).appendPath(interestId).build();
	    }

		public static Uri buildSharedLinksDirUri(String memberId) {
			return CONTENT_URI.buildUpon().appendPath(memberId).appendPath(PATH_SHARED_LINKS).build();
		}

	    public static Uri buildMemberSharedLinkUri(String memberId, String sharedLinkId) {
	        return CONTENT_URI.buildUpon().appendPath(memberId).appendPath(PATH_SHARED_LINKS).appendPath(sharedLinkId).build();
	    }
	    
	    public static Uri buildLinkersDirUri(String memberId) {
	    	return CONTENT_URI.buildUpon().appendPath(memberId).appendPath(PATH_LINKERS).build();
	    }

	    public static Uri buildMemberLinkerUri(String memberId, String linkerId) {
	        return CONTENT_URI.buildUpon().appendPath(memberId).appendPath(PATH_LINKERS).appendPath(linkerId).build();
	    }
	    
	    public static Uri buildLinksDirUri(String memberId) {
	    	return CONTENT_URI.buildUpon().appendPath(memberId).appendPath(PATH_LINKS).build();
	    }

	    public static Uri buildMemberLinkUri(String memberId, String linkId) {
	        return CONTENT_URI.buildUpon().appendPath(memberId).appendPath(PATH_LINKS).appendPath(linkId).build();
	    }

	    public static Uri buildCommentsDirUri(String memberId) {
	        return CONTENT_URI.buildUpon().appendPath(memberId).appendPath(PATH_COMMENTS).build();
	    }

	    public static Uri buildMemberCommentUri(String memberId, String commentId) {
	        return CONTENT_URI.buildUpon().appendPath(memberId).appendPath(PATH_COMMENTS).appendPath(commentId).build();
	    }

		public static Uri buildSessionsDirUri(String speakerId, boolean isSession) {
		    return CONTENT_URI.buildUpon().appendPath(speakerId).appendPath(isSession ? PATH_SESSIONS : PATH_LIGHTNINGS).build();
		}

		public static Uri buildSpeakerSessionUri(String speakerId, String sessionId) {
		    return CONTENT_URI.buildUpon().appendPath(speakerId).appendPath(PATH_SESSIONS).appendPath(sessionId).build();
		}

		public static String getMemberId(Uri uri) {
		    return uri.getPathSegments().get(1);
		}

		public static String getInterestId(Uri uri) {
		    return uri.getPathSegments().get(3);
		}

		public static String getBadgeId(Uri uri) {
		    return uri.getPathSegments().get(3);
		}

		public static String getSharedLinkId(Uri uri) {
		    return uri.getPathSegments().get(3);
		}

		public static String getLinkId(Uri uri) {
		    return uri.getPathSegments().get(3);
		}

		public static String getLinkerId(Uri uri) {
		    return uri.getPathSegments().get(3);
		}

		public static String getCommentId(Uri uri) {
		    return uri.getPathSegments().get(3);
		}
		
		public static String getSessionId(Uri uri) {
		    return uri.getPathSegments().get(3);
		}
	}
	

	interface SharedLinksColumns {
		String SHARED_LINK_ID = "shared_link_id";
		String MEMBER_ID = "member_id";
		String ORDER_NUM = "order_num";
		String NAME = "name";
		String URL = "url";
	}
	
	public static class SharedLinks implements SharedLinksColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHARED_LINKS).build();
		
	    public static final String CONTENT_TYPE =
	            "vnd.android.cursor.dir/vnd.mixit.shared_link";
	    public static final String CONTENT_ITEM_TYPE =
	            "vnd.android.cursor.item/vnd.mixit.shared_link";

	    public static final String DEFAULT_SORT = MixItDatabase.Tables.SHARED_LINKS + "." + SharedLinksColumns.ORDER_NUM + " ASC";

		public static Uri buildSharedLinkUri(String sharedLinkId) {
	        return CONTENT_URI.buildUpon().appendPath(sharedLinkId).build();
		}
		
	    public static String getSharedLinkId(Uri uri) {
	        return uri.getPathSegments().get(1);
	    }
	}
	
	
	interface SessionsColumns {
		String SESSION_ID = "session_id";
		String TITLE = "title";
		String SUMMARY = "summary";
		String DESC = "desc";
		String TIME = "time";
		String TRACK_ID = "track_id";
		String IS_SESSION = "is_session";
		String NB_VOTES = "nb_vote";
		String MY_VOTE = "my_vote";
		String IS_FAVORITE = "is_favorite";
	}
	
	public static class Sessions implements SessionsColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SESSIONS).build();
		public static final Uri CONTENT_URI_LIGNTHNING = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LIGHTNINGS).build();
		
	    public static final String CONTENT_TYPE =
	            "vnd.android.cursor.dir/vnd.mixit.session";
	    public static final String CONTENT_ITEM_TYPE =
	            "vnd.android.cursor.item/vnd.mixit.session";

		public static final String DEFAULT_SORT = MixItDatabase.Tables.SESSIONS + "." + SessionsColumns.SESSION_ID + " ASC";
		
		public static Uri buildSessionUri(String sessionId) {
			return CONTENT_URI.buildUpon().appendPath(sessionId).build();
		}
		
	    public static Uri buildSpeakersDirUri(String sessionId) {
	        return CONTENT_URI.buildUpon().appendPath(sessionId).appendPath(PATH_MEMBERS).build();
	    }

	    public static Uri buildSessionSpeakerUri(String sessionId, String speakerId) {
	        return CONTENT_URI.buildUpon().appendPath(sessionId).appendPath(PATH_MEMBERS).appendPath(speakerId).build();
	    }

		public static Uri buildInterestsDirUri(String sessionId) {
			return CONTENT_URI.buildUpon().appendPath(sessionId).appendPath(PATH_INTERESTS).build();
		}

	    public static Uri buildSessionInterestUri(String sessionId, String interestId) {
	        return CONTENT_URI.buildUpon().appendPath(sessionId).appendPath(PATH_INTERESTS).appendPath(interestId).build();
	    }

		public static Uri buildCommentsDirUri(String sessionId) {
			return CONTENT_URI.buildUpon().appendPath(sessionId).appendPath(PATH_COMMENTS).build();
		}

	    public static Uri buildSessionCommentUri(String sessionId, String commentId) {
	        return CONTENT_URI.buildUpon().appendPath(sessionId).appendPath(PATH_COMMENTS).appendPath(commentId).build();
	    }

	    public static Uri buildSessionsUri(Track track) {
	    	return BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACKS).appendPath(track.name()).appendPath(PATH_SESSIONS).build();
	    }
	    
		public static Uri buildSessionsUri(String interestId, boolean isSession) {
	        return BASE_CONTENT_URI.buildUpon().appendPath(PATH_INTERESTS).appendPath(interestId).appendPath(isSession ? PATH_SESSIONS : PATH_LIGHTNINGS).build();
		}

		public static String getSessionId(Uri uri) {
		    return uri.getPathSegments().get(1);
		}

		public static String getSpeakerId(Uri uri) {
		    return uri.getPathSegments().get(3);
		}

		public static String getInterestIdFromSessionInterests(Uri uri) {
		    return uri.getPathSegments().get(3);
		}

		public static String getCommentId(Uri uri) {
		    return uri.getPathSegments().get(3);
		}

		public static String getTrackId(Uri uri) {
		    return uri.getPathSegments().get(1);
		}

		public static String getInterestIdFromInterestSessions(Uri uri) {
		    return uri.getPathSegments().get(1);
		}
	}
	
	
	interface CommentsColumns {
		String COMMENT_ID = "comment_id";
		String AUTHOR_ID = "author_id";
		String CONTENT = "content";
		String PUBLISH_DATE = "publish_date";
		String SESSION_ID = "session_id";
	}
	
	public static class Comments implements CommentsColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMMENTS).build();
		
	    public static final String CONTENT_TYPE =
	            "vnd.android.cursor.dir/vnd.mixit.comment";
	    public static final String CONTENT_ITEM_TYPE =
	            "vnd.android.cursor.item/vnd.mixit.comment";

	    // TODO : maybe sort comments in the other order (recent to old ?)
		public static final String DEFAULT_SORT = MixItDatabase.Tables.COMMENTS + "." + CommentsColumns.PUBLISH_DATE + " ASC";
		
		public static Uri buildCommentUri(String commentId) {
			return CONTENT_URI.buildUpon().appendPath(commentId).build();
		}

		public static String getCommentId(Uri uri) {
		    return uri.getPathSegments().get(1);
		}
	}
	
	
	private MixItContract() {
	}

}
