package fr.mixit.android_2012.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import fr.mixit.android_2012.R;
import fr.mixit.android_2012.model.Track;
import fr.mixit.android_2012.provider.MixItContract;

public class SessionsAdapter extends CursorAdapter {

	LayoutInflater inflater;
	boolean mDisplayStar = true;

	class SessionHolder {
		View track;
		ImageView starred;
		TextView title;
		TextView subtitle;
	}

	public SessionsAdapter(Context ctx) {
		this(ctx, true);
	}
	
	public SessionsAdapter(Context ctx, boolean displayStar) {
		super(ctx, null, 0);

		inflater = LayoutInflater.from(ctx);
		mDisplayStar = displayStar;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = inflater.inflate(R.layout.item_session, parent, false);
		SessionHolder holder = new SessionHolder();
		holder.starred = (ImageView) v.findViewById(R.id.star_button);
		Drawable drawable = holder.starred.getDrawable();
		drawable.setColorFilter(new LightingColorFilter(mContext.getResources().getColor(R.color.star_color), 1));
		holder.title = (TextView) v.findViewById(R.id.session_title);
		holder.subtitle = (TextView) v.findViewById(R.id.session_subtitle);
		holder.subtitle.setVisibility(View.GONE);
		holder.track = v.findViewById(R.id.session_track);
		v.setTag(holder);
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		SessionHolder holder = (SessionHolder) view.getTag();
		holder.title.setText(cursor.getString(SessionsQuery.TITLE));

		// TODO : add time information
		// TODO : add track's color information
		// final long blockStart = cursor.getLong(SessionsQuery.BLOCK_START);
		// final long blockEnd = cursor.getLong(SessionsQuery.BLOCK_END);
		// final String roomName = cursor.getString(SessionsQuery.ROOM_NAME);
		// final String company = formatSessionSubtitle(blockStart, blockEnd, roomName, mContext);
		// holder.subtitle.setText(company);

		final boolean starred = cursor.getInt(SessionsQuery.IS_FAVORITE) != 0;
		holder.starred.setVisibility(mDisplayStar && starred ? View.VISIBLE : View.INVISIBLE);
		
		String track = cursor.getString(SessionsQuery.TRACK);
		int color = android.R.color.transparent;
		if (Track.Agility.name().equals(track)) {
			color = R.color.agility;
		} else if (Track.Techy.name().equals(track)) {
			color = R.color.techy;
		} else if (Track.Trendy.name().equals(track)) {
			color = R.color.trendy;
		} else if (Track.Weby.name().equals(track)) {
			color = R.color.weby;
		} else if (Track.Gamy.name().equals(track)) {
			color = R.color.gamy;
		}
		holder.track.setBackgroundColor(mContext.getResources().getColor(color));

		// // Possibly indicate that the session has occurred in the past.
		// UIUtils.setSessionTitleColor(blockStart, blockEnd, titleView, subtitleView);
	}

	public interface SessionsQuery {
		String[] PROJECTION = { BaseColumns._ID, MixItContract.Sessions.SESSION_ID, MixItContract.Sessions.TITLE, MixItContract.Sessions.TRACK_ID,
				MixItContract.Sessions.IS_FAVORITE };

		int _ID = 0;
		int SESSION_ID = 1;
		int TITLE = 2;
		int TRACK = 3;
		int IS_FAVORITE = 4;
	}
	
}
