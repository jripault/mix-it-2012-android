package fr.mixit.android.io;

import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import fr.mixit.android.provider.MixItContract;
import fr.mixit.android.utils.Lists;

public class InterestsHandler extends JsonHandler {
	
	static final String TAG = InterestsHandler.class.getSimpleName();
	
	static final String TAG_ID = "id";
	static final String TAG_NAME = "name";
	
	
	public InterestsHandler() {
		super(MixItContract.CONTENT_AUTHORITY);
	}
	
	@Override
	public ArrayList<ContentProviderOperation> parse(ArrayList<JSONArray> entries, ContentResolver resolver) throws JSONException {
		final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();
		final HashSet<String> interestsIds = new HashSet<String>();
		
		for (JSONArray interests : entries) {
			Log.d(TAG, "Retrieved " + interests.length() + " more interests entries.");
			
	        for (int i=0; i < interests.length(); i++) {
	            JSONObject interest = interests.getJSONObject(i);
		        String id = interest.getString(TAG_ID);

		        final Uri interestUri = MixItContract.Interests.buildInterestUri(id);
		        interestsIds.add(id);

		        boolean tagUpdated = false;
		        boolean newInterest = false;
		        boolean build = false;
		        ContentProviderOperation.Builder builder;
		        if (isRowExisting(interestUri, InterestsQuery.PROJECTION, resolver)) {
			        builder = ContentProviderOperation.newUpdate(interestUri);
			        tagUpdated = isInterestUpdated(interestUri, interest, resolver);
		        } else {
			        newInterest = true;
			        builder = ContentProviderOperation.newInsert(MixItContract.Interests.CONTENT_URI);
					builder.withValue(MixItContract.Interests.INTEREST_ID, id);
					build = true;
		        }

		        if (newInterest || tagUpdated) {
			        builder.withValue(MixItContract.Interests.NAME, interest.getString(TAG_NAME));
			        build = true;
		        }
		        if (build) batch.add(builder.build());
	        }
		}
		
		return batch;
	}

	private static boolean isInterestUpdated(Uri uri, JSONObject tag, ContentResolver resolver) throws JSONException {
        final Cursor cursor = resolver.query(uri, InterestsQuery.PROJECTION, null, null, null);
        try {
            if (!cursor.moveToFirst()) return false;

            final String curName = cursor.getString(InterestsQuery.INTEREST_NAME).toLowerCase().trim();
        	final String newName = tag.has(TAG_NAME) ? tag.getString(TAG_NAME).toLowerCase().trim() : curName;

        	return !curName.equals(newName);
        } finally {
            cursor.close();
        }
	}

    private interface InterestsQuery {
        String[] PROJECTION = {
//                MixItContract.Interests.INTEREST_ID,
                MixItContract.Interests.NAME,
        };

//        int INTEREST_ID = 0;
        int INTEREST_NAME = 0;
    }
    
}
