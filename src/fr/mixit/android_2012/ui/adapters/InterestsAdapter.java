package fr.mixit.android_2012.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.SectionIndexer;
import android.widget.TextView;
import fr.mixit.android_2012.R;
import fr.mixit.android_2012.provider.MixItContract;

public class InterestsAdapter extends CursorAdapter implements SectionIndexer {

	LayoutInflater inflater;
	boolean mIsFromSession = true;
	private AlphabetIndexer mIndexer;

	class InterestHolder {
		TextView name;
		TextView nbSession;
	}

	public InterestsAdapter(Context mContext, boolean isFromSession) {
		super(mContext, null, 0);

		mIsFromSession = isFromSession;
		inflater = LayoutInflater.from(mContext);
		mIndexer = new AlphabetIndexer(null, InterestsQuery.NAME, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	}

	@Override
	public void changeCursor(Cursor cursor) {
		super.changeCursor(cursor);

		mIndexer.setCursor(cursor);
	}

	@Override
	public int getPositionForSection(int section) {
		return mIndexer.getPositionForSection(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		return mIndexer.getSectionForPosition(position);
	}

	@Override
	public Object[] getSections() {
		return mIndexer.getSections();
	}

	@Override
	public View newView(Context mContext, Cursor cursor, ViewGroup parent) {
		View v = inflater.inflate(R.layout.item_interest, parent, false);
		InterestHolder holder = new InterestHolder();
		holder.name = (TextView) v.findViewById(R.id.interest_name);
		holder.nbSession = (TextView) v.findViewById(R.id.nb_sessions);
		v.setTag(holder);
		return v;
	}

	@Override
	public void bindView(View view, Context mContext, Cursor cursor) {
		InterestHolder holder = (InterestHolder) view.getTag();
		holder.name.setText(cursor.getString(InterestsQuery.NAME));
		holder.nbSession.setText(cursor.getString(mIsFromSession ? InterestsQuery.SESSIONS_COUNT : InterestsQuery.MEMBERS_COUNT));
		NinePatchDrawable drawable = (NinePatchDrawable) holder.nbSession.getBackground();
		drawable.setColorFilter(new LightingColorFilter(mContext.getResources().getColor(R.color.foreground1), 1));
	}

	public interface InterestsQuery {
		String[] PROJECTION = { MixItContract.Interests._ID, MixItContract.Interests.INTEREST_ID, MixItContract.Interests.NAME, };

		String[] PROJECTION_WITH_SESSIONS_COUNT = { MixItContract.Interests._ID, MixItContract.Interests.INTEREST_ID, MixItContract.Interests.NAME,
				MixItContract.Interests.SESSIONS_COUNT, };

		String[] PROJECTION_WITH_MEMBERS_COUNT = { MixItContract.Interests._ID, MixItContract.Interests.INTEREST_ID, MixItContract.Interests.NAME,
				MixItContract.Interests.MEMBERS_COUNT, };

		int _ID = 0;
		int INTEREST_ID = 1;
		int NAME = 2;
		int SESSIONS_COUNT = 3;
		int MEMBERS_COUNT = 3;
	}
}
