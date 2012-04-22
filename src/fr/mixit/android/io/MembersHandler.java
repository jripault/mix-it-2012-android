package fr.mixit.android.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import fr.mixit.android.provider.MixItContract;
import fr.mixit.android.provider.MixItDatabase;
import fr.mixit.android.utils.Lists;
import fr.mixit.android.utils.Maps;
import fr.mixit.android.utils.Sets;

public class MembersHandler extends JsonHandler {

	static final String TAG = MembersHandler.class.getSimpleName();

	static final String FALSE = "false";

	static final String TAG_ID = "id";
	static final String TAG_LOGIN = "login";
	static final String TAG_EMAIL = "email";
	static final String TAG_FIRSTNAME = "firstname";
	static final String TAG_LASTNAME = "lastname";
	static final String TAG_COMPANY = "company";
	static final String TAG_TICKETING_REGISTERED = "ticketingRegistered";
	static final String TAG_SHORT_DESCRIPTION = "shortDescription";
	static final String TAG_SHORT_DESC = "shortdesc";
	static final String TAG_LONG_DESCRIPTION = "longDescription";
	static final String TAG_LONG_DESC = "longdesc";
	static final String TAG_NB_CONSULT = "nbConsults";
	static final String TAG_IMAGE_URL = "urlImage";

	static final String TAG_LINKS = "links";

	static final String TAG_LINKERS = "linkers";

	static final String TAG_INTERESTS = "interests";

	static final String TAG_BADGES = "badges";

	static final String TAG_SESSIONS = "sessions";

	static final String TAG_SHARED_LINKS = "sharedLinks";
	static final String TAG_ORDER_NUM = "ordernum";
	static final String TAG_NAME = "name";
	static final String TAG_URL = "URL";

	static final String TAG_ACCOUNTS = "accounts";
	static final String TAG_GOOGLE = "Google";
	static final String TAG_GOOGLE_ID = "googleId";
	static final String TAG_TWITTER = "Twitter";
	static final String TAG_SCREEN_NAME = "screenName";
	static final String TAG_LINKEDIN = "LinkedIn";
	static final String TAG_PROVIDER = "provider";
	static final String TAG_LAST_STATUS_ID = "lastStatusId";// ???
	static final String TAG_LAST_FETCHED = "lastFetched";// ???

	static final String TAG_MEMBER = "member";// ???

	boolean deleteMembersNotFoundInDB = false;
	boolean deleteMemberDataNotFoundInDB = false;
	HashSet<String> memberIds;
	HashMap<String, HashSet<String>> memberInterestsIds;
	HashMap<String, HashSet<String>> memberBadgesIds;
	HashMap<String, HashSet<String>> memberSharedLinksIds;
	HashMap<String, HashSet<String>> memberLinksIds;
	HashMap<String, HashSet<String>> memberLinkersIds;
	HashMap<String, HashSet<String>> memberSessionsIds;
	

	public MembersHandler() {
		this(false);
	}

	public MembersHandler(boolean deleteMemberDataNotFoundInDB) {
		this(deleteMemberDataNotFoundInDB, false);
	}

	public MembersHandler(boolean deleteMemberDataNotFoundInDB, boolean deleteMembersNotFoundInDB) {
		super(MixItContract.CONTENT_AUTHORITY);

		this.deleteMemberDataNotFoundInDB = deleteMemberDataNotFoundInDB;
		this.deleteMembersNotFoundInDB = deleteMembersNotFoundInDB;
	}

	@Override
	public ArrayList<ContentProviderOperation> parse(ArrayList<JSONArray> entries, ContentResolver resolver) throws JSONException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
		memberIds = new HashSet<String>();
		memberInterestsIds = Maps.newHashMap();
		memberBadgesIds = Maps.newHashMap();
		memberSharedLinksIds = Maps.newHashMap();
		memberLinksIds = Maps.newHashMap();
		memberLinkersIds = Maps.newHashMap();
		memberSessionsIds = Maps.newHashMap();

		int nbEntries = 0;
		for (JSONArray members : entries) {
			Log.d(TAG, "Retrieved " + members.length() + " members entries.");
			nbEntries += members.length();

			batch.addAll(parseMembers(members, resolver));
		}

		if (nbEntries > 0) {
			if (deleteMemberDataNotFoundInDB) {
	        	for (Map.Entry<String, HashSet<String>> entry : memberInterestsIds.entrySet()) {
	        		String memberId = entry.getKey();
	        		HashSet<String> interestIds = entry.getValue();
				    final Uri memberInterestsUri = MixItContract.Members.buildInterestsDirUri(memberId);
	    	    	HashSet<String> lostInterestIds = getLostIds(interestIds, memberInterestsUri, InterestsQuery.PROJECTION, InterestsQuery.INTEREST_ID, resolver);
	            	for (String lostInterestId : lostInterestIds) {
		        		final Uri deleteUri = MixItContract.Members.buildMemberInterestUri(memberId, lostInterestId);
				    	batch.add(ContentProviderOperation.newDelete(deleteUri).build());
	            	}
	        	}
	        	
	        	for (Map.Entry<String, HashSet<String>> entry : memberSharedLinksIds.entrySet()) {
	        		String memberId = entry.getKey();
	        		HashSet<String> sharedLinkIds = entry.getValue();
				    final Uri memberSharedLinksUri = MixItContract.Members.buildSharedLinksDirUri(memberId);
	    	    	HashSet<String> lostSharedLinkIds = getLostIds(sharedLinkIds, memberSharedLinksUri, SharedLinksQuery.PROJECTION, SharedLinksQuery.SHARED_LINK_ID, resolver);
	            	for (String lostSharedLinkId : lostSharedLinkIds) {
		        		final Uri deleteUri = MixItContract.Members.buildMemberSharedLinkUri(memberId, lostSharedLinkId);
				    	batch.add(ContentProviderOperation.newDelete(deleteUri).build());
	            	}
	        	}
	        	
	        	for (Map.Entry<String, HashSet<String>> entry : memberLinksIds.entrySet()) {
	        		String memberId = entry.getKey();
	        		HashSet<String> linkIds = entry.getValue();
				    final Uri memberLinksUri = MixItContract.Members.buildLinksDirUri(memberId);
	    	    	HashSet<String> lostLinkIds = getLostIds(linkIds, memberLinksUri, LinksQuery.PROJECTION, LinksQuery.LINK_ID, resolver);
	            	for (String lostLinkId : lostLinkIds) {
		        		final Uri deleteUri = MixItContract.Members.buildMemberLinkUri(memberId, lostLinkId);
				    	batch.add(ContentProviderOperation.newDelete(deleteUri).build());
	            	}
	        	}
	        	
	        	for (Map.Entry<String, HashSet<String>> entry : memberLinkersIds.entrySet()) {
	        		String memberId = entry.getKey();
	        		HashSet<String> linkerIds = entry.getValue();
				    final Uri memberLinkersUri = MixItContract.Members.buildLinkersDirUri(memberId);
	    	    	HashSet<String> lostLinkerIds = getLostIds(linkerIds, memberLinkersUri, LinksQuery.PROJECTION, LinksQuery.LINKER_ID, resolver);
	            	for (String lostLinkerId : lostLinkerIds) {
		        		final Uri deleteUri = MixItContract.Members.buildMemberLinkerUri(memberId, lostLinkerId);
				    	batch.add(ContentProviderOperation.newDelete(deleteUri).build());
	            	}
	        	}
	        	
	        	for (Map.Entry<String, HashSet<String>> entry : memberSessionsIds.entrySet()) {
	        		String speakerId = entry.getKey();
	        		HashSet<String> sessionIds = entry.getValue();
				    Uri speakerSessionsUri = MixItContract.Members.buildSessionsDirUri(speakerId, true);
	    	    	HashSet<String> lostSessionIds = getLostIds(sessionIds, speakerSessionsUri, SessionsQuery.PROJECTION, SessionsQuery.SESSION_ID, resolver);
	            	for (String lostSessionId : lostSessionIds) {
		        		final Uri deleteUri = MixItContract.Members.buildSpeakerSessionUri(speakerId, lostSessionId);
				    	batch.add(ContentProviderOperation.newDelete(deleteUri).build());
	            	}
				    speakerSessionsUri = MixItContract.Members.buildSessionsDirUri(speakerId, false);
	    	    	lostSessionIds = getLostIds(sessionIds, speakerSessionsUri, SessionsQuery.PROJECTION, SessionsQuery.SESSION_ID, resolver);
	            	for (String lostSessionId : lostSessionIds) {
		        		final Uri deleteUri = MixItContract.Members.buildSpeakerSessionUri(speakerId, lostSessionId);
				    	batch.add(ContentProviderOperation.newDelete(deleteUri).build());
	            	}
	        	}
			}	
        	
			if (deleteMembersNotFoundInDB) {
				for (String lostId : getLostIds(memberIds, MixItContract.Members.CONTENT_URI, MembersQuery.PROJECTION, MembersQuery.MEMBER_ID, resolver)) {
					Uri deleteUri = MixItContract.Members.buildBadgesDirUri(lostId);
					batch.add(ContentProviderOperation.newDelete(deleteUri).build());
					deleteUri = MixItContract.Members.buildInterestsDirUri(lostId);
					batch.add(ContentProviderOperation.newDelete(deleteUri).build());
					deleteUri = MixItContract.Members.buildSharedLinksDirUri(lostId);
					batch.add(ContentProviderOperation.newDelete(deleteUri).build());
					deleteUri = MixItContract.Members.buildMemberUri(lostId);
					batch.add(ContentProviderOperation.newDelete(deleteUri).build());
				}
			}
		}

		return batch;
	}

	public ArrayList<ContentProviderOperation> parseMembers(JSONArray entries, ContentResolver resolver) throws JSONException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

		for (int i = 0; i < entries.length(); i++) {
			JSONObject member = entries.getJSONObject(i);
			String id = member.getString(TAG_ID);
			memberIds.add(id);

			final Uri memberUri = MixItContract.Members.buildMemberUri(id);

			boolean memberUpdated = false;
			boolean newMember = false;
			boolean build = false;
			ContentProviderOperation.Builder builder;

			if (isRowExisting(memberUri, MembersQuery.PROJECTION, resolver)) {
				builder = ContentProviderOperation.newUpdate(memberUri);
				memberUpdated = isMemberUpdated(memberUri, member, resolver);
			} else {
				newMember = true;
				builder = ContentProviderOperation.newInsert(MixItContract.Members.CONTENT_URI);
				builder.withValue(MixItContract.Members.MEMBER_ID, id);
				build = true;
			}

			if (newMember || memberUpdated) {
				StringBuilder str = new StringBuilder();
				if (member.has(TAG_LASTNAME)) {
					String lastname = member.getString(TAG_LASTNAME);
					if (!TextUtils.isEmpty(lastname)) {
						lastname = lastname.toLowerCase();
						str.append(lastname.substring(0, 1).toUpperCase());
						str.append(lastname.substring(1, lastname.length()));
						lastname = str.toString();
					}
					builder.withValue(MixItContract.Members.LASTNAME, lastname);
				}
				if (member.has(TAG_FIRSTNAME)) {
					str.setLength(0);
					String firstname = member.getString(TAG_FIRSTNAME);
					if (!TextUtils.isEmpty(firstname)) {
						firstname = firstname.toLowerCase();
						str.append(firstname.substring(0, 1).toUpperCase());
						str.append(firstname.substring(1, firstname.length()));
						firstname = str.toString();
					}
					builder.withValue(MixItContract.Members.FIRSTNAME, firstname);
				}
				if (member.has(TAG_LOGIN))
					builder.withValue(MixItContract.Members.LOGIN, member.getString(TAG_LOGIN));
				if (member.has(TAG_EMAIL))
					builder.withValue(MixItContract.Members.EMAIL, member.getString(TAG_EMAIL));
				if (member.has(TAG_SHORT_DESC))
					builder.withValue(MixItContract.Members.SHORT_DESC, member.getString(TAG_SHORT_DESC));
				if (member.has(TAG_SHORT_DESCRIPTION))
					builder.withValue(MixItContract.Members.LONG_DESC, member.getString(TAG_SHORT_DESCRIPTION));
				if (member.has(TAG_LONG_DESC))
					builder.withValue(MixItContract.Members.LONG_DESC, member.getString(TAG_LONG_DESC));
				if (member.has(TAG_LONG_DESCRIPTION))
					builder.withValue(MixItContract.Members.LONG_DESC, member.getString(TAG_LONG_DESCRIPTION));
				if (member.has(TAG_COMPANY))
					builder.withValue(MixItContract.Members.COMPANY, member.getString(TAG_COMPANY));
				if (member.has(TAG_IMAGE_URL))
					builder.withValue(MixItContract.Members.IMAGE_URL, member.getString(TAG_IMAGE_URL));
				if (member.has(TAG_TICKETING_REGISTERED))
					builder.withValue(MixItContract.Members.TICKET_REGISTERED, (member.getBoolean(TAG_TICKETING_REGISTERED) ? 1 : 0));
				if (member.has(TAG_NB_CONSULT))
					builder.withValue(MixItContract.Members.NB_CONSULT, member.getInt(TAG_NB_CONSULT));
				build = true;
			}
			if (build) {
				batch.add(builder.build());
			}

			if (member.has(TAG_BADGES)) {
				JSONArray batches = member.getJSONArray(TAG_BADGES);
				batch.addAll(parseBadges(id, batches));
			}
			
			if (member.has(TAG_INTERESTS)) {
				JSONArray interests = member.getJSONArray(TAG_INTERESTS);
				batch.addAll(parseInterests(id, interests));
			}
			
			if (member.has(TAG_SHARED_LINKS)) {
				JSONArray sharedLinks = member.getJSONArray(TAG_SHARED_LINKS);
				batch.addAll(parseSharedLinks(id, sharedLinks, resolver));
			}
			
			if (member.has(TAG_LINKS)) {
				JSONArray linkers = member.getJSONArray(TAG_LINKS);
				batch.addAll(parseLinks(id, linkers));
			}
			
			if (member.has(TAG_LINKERS)) {
				JSONArray linkers = member.getJSONArray(TAG_LINKERS);
				batch.addAll(parseLinkers(id, linkers));
			}
			
			if (member.has(TAG_SESSIONS)) {
				JSONArray sessions = member.getJSONArray(TAG_SESSIONS);
				batch.addAll(parseSessions(id, sessions));
			}
		}
		return batch;
	}

	public ArrayList<ContentProviderOperation> parseBadges(String memberId, JSONArray badges) throws JSONException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
		
	    final Uri memberBadgesUri = MixItContract.Members.buildBadgesDirUri(memberId);
		final HashSet<String> badgesIds = Sets.newHashSet();
		
		for (int i = 0; i < badges.length(); i++) {
			// TODO : should be the id no ? Or I should refers to the name has values in the enum
			String badgeId = badges.getString(i);
//			int badge = entries.getString(i);
//			Badge.valueOf(arg0)
			badgesIds.add(badgeId);
			
			batch.add(ContentProviderOperation.newInsert(memberBadgesUri)
	    			.withValue(MixItDatabase.MembersBadges.BADGE_ID, badgeId)
	    			.withValue(MixItDatabase.MembersBadges.MEMBER_ID, memberId).build());
		}
		
		return batch;
	}

	public ArrayList<ContentProviderOperation> parseInterests(String memberId, JSONArray interests) throws JSONException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
		
	    final Uri memberInterestsUri = MixItContract.Members.buildInterestsDirUri(memberId);
		final HashSet<String> interestsIds = Sets.newHashSet();
    	
    	for (int j = 0; j < interests.length(); j++) {
    		JSONObject interest = interests.getJSONObject(j);
		    final String interestId = interest.getString(TAG_ID);
        	interestsIds.add(interestId);

	    	batch.add(ContentProviderOperation.newInsert(memberInterestsUri)
	    			.withValue(MixItDatabase.MembersInterests.INTEREST_ID, interestId)
	    			.withValue(MixItDatabase.MembersInterests.MEMBER_ID, memberId).build());
    	}
    	
    	memberInterestsIds.put(memberId, interestsIds);
		
		return batch;
	}

	static final String FORMATER = "%d";

	public ArrayList<ContentProviderOperation> parseSessions(String memberId, JSONArray session) throws JSONException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
		
	    final Uri speakerSessionsUri = MixItContract.Members.buildSessionsDirUri(memberId, true);
		final HashSet<String> sessionsIds = Sets.newHashSet();
    	
    	for (int j = 0; j < session.length(); j++) {
    		final int id = session.getInt(j);
    		final String sessionId = String.format(FORMATER, id);
    		sessionsIds.add(sessionId);

	    	batch.add(ContentProviderOperation.newInsert(speakerSessionsUri)
	    			.withValue(MixItDatabase.SessionsSpeakers.SPEAKER_ID, memberId)
	    			.withValue(MixItDatabase.SessionsSpeakers.SESSION_ID, sessionId).build());
    	}
    	
    	memberSessionsIds.put(memberId, sessionsIds);
		return batch;
	}

	private ArrayList<ContentProviderOperation> parseSharedLinks(String memberId, JSONArray sharedLinks, ContentResolver resolver) throws JSONException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
		
		final HashSet<String> sharedLinksIds = Sets.newHashSet();
    	
    	for (int j = 0; j < sharedLinks.length(); j++) {
    		JSONObject sharedLink = sharedLinks.getJSONObject(j);
			String id = sharedLink.getString(TAG_ID);
			sharedLinksIds.add(id);

			final Uri sharedLinkUri = MixItContract.SharedLinks.buildSharedLinkUri(id);

			boolean sharedLinkUpdated = false;
			boolean newSharedLink = false;
			boolean build = false;
			ContentProviderOperation.Builder builder;

			if (isRowExisting(sharedLinkUri, SharedLinksQuery.PROJECTION, resolver)) {
				builder = ContentProviderOperation.newUpdate(sharedLinkUri);
				sharedLinkUpdated = isSharedLinkUpdated(sharedLinkUri, memberId, sharedLink, resolver);
			} else {
				newSharedLink = true;
				builder = ContentProviderOperation.newInsert(MixItContract.SharedLinks.CONTENT_URI);
				builder.withValue(MixItContract.SharedLinks.SHARED_LINK_ID, id);
				build = true;
			}

			if (newSharedLink || sharedLinkUpdated) {
				if (sharedLink.has(TAG_ORDER_NUM))
					builder.withValue(MixItContract.SharedLinks.ORDER_NUM, sharedLink.getInt(TAG_ORDER_NUM));
				if (sharedLink.has(TAG_NAME))
					builder.withValue(MixItContract.SharedLinks.NAME, sharedLink.getString(TAG_NAME));
				if (sharedLink.has(TAG_URL))
					builder.withValue(MixItContract.SharedLinks.URL, sharedLink.getString(TAG_URL));
				builder.withValue(MixItContract.SharedLinks.MEMBER_ID, memberId);
				build = true;
			}
			if (build) {
				batch.add(builder.build());
			}
    	}
    	
    	memberInterestsIds.put(memberId, sharedLinksIds);
		
		return batch;
	}

	public ArrayList<ContentProviderOperation> parseLinks(String memberId, JSONArray links) throws JSONException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
		
	    final Uri memberLinksUri = MixItContract.Members.buildLinksDirUri(memberId);
		final HashSet<String> linksIds = Sets.newHashSet();
    	
    	for (int j = 0; j < links.length(); j++) {
    		JSONObject link = links.getJSONObject(j);
		    final String linkId = link.getString(TAG_ID);
        	linksIds.add(linkId);

	    	batch.add(ContentProviderOperation.newInsert(memberLinksUri)
	    			.withValue(MixItDatabase.MembersLinks.LINK_ID, linkId)
	    			.withValue(MixItDatabase.MembersLinks.MEMBER_ID, memberId).build());
    	}
    	
    	memberLinksIds.put(memberId, linksIds);
    	
		return batch;
	}

	public ArrayList<ContentProviderOperation> parseLinkers(String memberId, JSONArray linkers) throws JSONException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
		
	    final Uri memberLinkersUri = MixItContract.Members.buildLinkersDirUri(memberId);
		final HashSet<String> linkersIds = Sets.newHashSet();
    	
    	for (int j = 0; j < linkers.length(); j++) {
    		JSONObject linker = linkers.getJSONObject(j);
		    final String linkerId = linker.getString(TAG_ID);
		    linkersIds.add(linkerId);

	    	batch.add(ContentProviderOperation.newInsert(memberLinkersUri)
	    			.withValue(MixItDatabase.MembersLinks.LINK_ID, memberId)
	    			.withValue(MixItDatabase.MembersLinks.MEMBER_ID, linkerId).build());
    	}
    	
    	memberLinkersIds.put(memberId, linkersIds);
    	
		return batch;
	}

	private static boolean isMemberUpdated(Uri uri, JSONObject member, ContentResolver resolver) throws JSONException {
		final Cursor cursor = resolver.query(uri, MembersQuery.PROJECTION, null, null, null);
		try {
			if (!cursor.moveToFirst())
				return false;

			final String curLogin = !TextUtils.isEmpty(cursor.getString(MembersQuery.LOGIN)) ? cursor.getString(MembersQuery.LOGIN).toLowerCase().trim() : "";
			final String curEmail = !TextUtils.isEmpty(cursor.getString(MembersQuery.EMAIL)) ? cursor.getString(MembersQuery.EMAIL).toLowerCase().trim() : "";
			final String curFirstName = !TextUtils.isEmpty(cursor.getString(MembersQuery.FIRSTNAME)) ? cursor.getString(MembersQuery.FIRSTNAME).toLowerCase().trim() : "";
			final String curLastName = !TextUtils.isEmpty(cursor.getString(MembersQuery.LASTNAME)) ? cursor.getString(MembersQuery.LASTNAME).toLowerCase().trim() : "";
			final String curCompany = !TextUtils.isEmpty(cursor.getString(MembersQuery.COMPANY)) ? cursor.getString(MembersQuery.COMPANY).toLowerCase().trim() : "";
			final String curShortDesc = !TextUtils.isEmpty(cursor.getString(MembersQuery.SHORT_DESC)) ? cursor.getString(MembersQuery.SHORT_DESC).toLowerCase().trim() : "";
			final String curLongDesc = !TextUtils.isEmpty(cursor.getString(MembersQuery.LONG_DESC)) ? cursor.getString(MembersQuery.LONG_DESC).toLowerCase().trim() : "";
			final int curTicketRegistered = cursor.getInt(MembersQuery.TICKET_REGISTERED);
			final String curImageUrl = !TextUtils.isEmpty(cursor.getString(MembersQuery.IMAGE_URL)) ? cursor.getString(MembersQuery.IMAGE_URL).toLowerCase().trim() : "";
			final int curNbConsult = cursor.getInt(MembersQuery.NB_CONSULT);
			final String newLogin = member.has(TAG_LOGIN) ? member.getString(TAG_LOGIN).toLowerCase().trim() : curLogin;
			final String newEmail = member.has(TAG_EMAIL) ? member.getString(TAG_EMAIL).toLowerCase().trim() : curEmail;
			final String newFirstName = member.has(TAG_FIRSTNAME) ? member.getString(TAG_FIRSTNAME).toLowerCase().trim() : curFirstName;
			final String newLastName = member.has(TAG_LASTNAME) ? member.getString(TAG_LASTNAME).toLowerCase().trim() : curLastName;
			final String newCompany = member.has(TAG_COMPANY) ? member.getString(TAG_COMPANY).toLowerCase().trim() : curCompany;
			final String newShortDesc = member.has(TAG_SHORT_DESC) ? member.getString(TAG_SHORT_DESC).toLowerCase().trim()
					: (member.has(TAG_SHORT_DESCRIPTION) ? member.getString(TAG_SHORT_DESCRIPTION).toLowerCase().trim() : curShortDesc);
			final String newLongDesc = member.has(TAG_LONG_DESC) ? member.getString(TAG_LONG_DESC).toLowerCase().trim()
					: (member.has(TAG_LONG_DESCRIPTION) ? member.getString(TAG_LONG_DESCRIPTION).toLowerCase().trim() : curLongDesc);
			final int newTicketRegistered = member.has(TAG_TICKETING_REGISTERED) ? (member.getBoolean(TAG_TICKETING_REGISTERED) ? 1 : 0) : curTicketRegistered;
			final String newImageUrl = member.has(TAG_IMAGE_URL) ? member.getString(TAG_IMAGE_URL).toLowerCase().trim() : curImageUrl;
			final int newNbConsult = member.has(TAG_NB_CONSULT) ? member.getInt(TAG_NB_CONSULT) : curNbConsult;

			return (!curFirstName.equals(newFirstName) || !curLastName.equals(newLastName) || !curLogin.equals(newLogin) || !curEmail.equals(newEmail)
					|| !curShortDesc.equals(newShortDesc) || !curLongDesc.equals(newLongDesc) || curTicketRegistered != newTicketRegistered
					|| !curImageUrl.equals(newImageUrl) || !curCompany.equals(newCompany) || curNbConsult != newNbConsult);
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	private static boolean isSharedLinkUpdated(Uri uri, String memberId, JSONObject sharedLink, ContentResolver resolver) throws JSONException {
		String[] selectionArgs = {memberId};
		final Cursor cursor = resolver.query(uri, SharedLinksQuery.PROJECTION, MixItContract.SharedLinks.MEMBER_ID + " = ?", selectionArgs, null);
		try {
			if (!cursor.moveToFirst())
				return false;

			final int curOrderNum = cursor.getInt(SharedLinksQuery.ORDER_NAME);
			final String curName = cursor.getString(SharedLinksQuery.NAME).toLowerCase().trim();
			final String curUrl = cursor.getString(SharedLinksQuery.URL).toLowerCase().trim();
			final int newOrderNum = sharedLink.has(TAG_ORDER_NUM) ? sharedLink.getInt(TAG_ORDER_NUM) : curOrderNum;
			final String newName = sharedLink.has(TAG_NAME) ? sharedLink.getString(TAG_NAME).toLowerCase().trim() : curName;
			final String newUrl = sharedLink.has(TAG_URL) ? sharedLink.getString(TAG_URL).toLowerCase().trim() : curUrl;

			return (!curName.equals(newName) || !curUrl.equals(newUrl) || curOrderNum != newOrderNum);
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	private interface MembersQuery {
		String[] PROJECTION = {
				MixItContract.Members.MEMBER_ID,
				MixItContract.Members.LOGIN,
				MixItContract.Members.EMAIL,
				MixItContract.Members.FIRSTNAME,
				MixItContract.Members.LASTNAME,
				MixItContract.Members.COMPANY,
				MixItContract.Members.SHORT_DESC,
				MixItContract.Members.LONG_DESC,
				MixItContract.Members.TICKET_REGISTERED,
				MixItContract.Members.IMAGE_URL,
				MixItContract.Members.NB_CONSULT
		};

		int MEMBER_ID = 0;
		int LOGIN = 1;
		int EMAIL = 2;
		int FIRSTNAME = 3;
		int LASTNAME = 4;
		int COMPANY = 5;
		int SHORT_DESC = 6;
		int LONG_DESC = 7;
		int TICKET_REGISTERED = 8;
		int IMAGE_URL = 9;
		int NB_CONSULT = 10;
	}

	interface InterestsQuery {
		String[] PROJECTION = {
				MixItContract.Interests.INTEREST_ID,
		};
		
		int INTEREST_ID = 0;
	}
	
    interface SharedLinksQuery {
        String[] PROJECTION = {
        		MixItContract.SharedLinks.SHARED_LINK_ID,
        		MixItContract.SharedLinks.ORDER_NUM,
        		MixItContract.SharedLinks.NAME,
        		MixItContract.SharedLinks.URL
        };

        int SHARED_LINK_ID = 0;
        int ORDER_NAME = 1;
        int NAME = 2;
        int URL = 3;
    }
    
    interface LinksQuery {
    	String[] PROJECTION = {
    			MixItDatabase.MembersLinks.LINK_ID,
        		MixItDatabase.MembersLinks.MEMBER_ID
    	};
    	
    	int LINK_ID = 0;
    	int LINKER_ID = 1;
    }
	
	interface SessionsQuery {
		String[] PROJECTION = {
				MixItContract.Sessions.SESSION_ID,
		};
		
		int SESSION_ID = 0;
	}
	
}
