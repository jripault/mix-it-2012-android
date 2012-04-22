package fr.mixit.android.ui.adapters;

import fr.mixit.android.R;
import fr.mixit.android.provider.MixItContract;
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

public class InterestsAdapter extends CursorAdapter implements SectionIndexer {

	LayoutInflater inflater;

	private AlphabetIndexer mIndexer;

	class InterestHolder {
		TextView name;
		TextView nbSession;
	}

	public InterestsAdapter(Context mContext) {
		super(mContext, null, 0);

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
		holder.nbSession.setText(cursor.getString(InterestsQuery.INTEREST_ID)); // TODO change for number of sessions for this interest
		NinePatchDrawable drawable = (NinePatchDrawable) holder.nbSession.getBackground();
		drawable.setColorFilter(new LightingColorFilter(mContext.getResources().getColor(R.color.foreground1), 1));
	}

	public interface InterestsQuery {
		String[] PROJECTION = {
				MixItContract.Interests._ID,
				MixItContract.Interests.INTEREST_ID,
				MixItContract.Interests.NAME,
		};
		
		String[] PROJECTION_WITH_SESSIONS_COUNT = {
				MixItContract.Interests._ID,
				MixItContract.Interests.INTEREST_ID,
				MixItContract.Interests.NAME,
		};

		int _ID = 0;
		int INTEREST_ID = 1;
		int NAME = 2;
	}
}
