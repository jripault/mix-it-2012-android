package fr.mixit.android_2012.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import fr.mixit.android_2012.R;
import fr.mixit.android_2012.provider.MixItContract;

public class SharedLinksAdapter extends CursorAdapter {

	LayoutInflater inflater;

	class SharedLinkHolder {
		TextView title;
		TextView subtitle;
	}

	public SharedLinksAdapter(Context ctx) {
		super(ctx, null, 0);

		inflater = LayoutInflater.from(ctx);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = inflater.inflate(R.layout.item_shared_link, parent, false);
		SharedLinkHolder holder = new SharedLinkHolder();
		holder.title = (TextView) v.findViewById(R.id.shared_link_title);
		holder.subtitle = (TextView) v.findViewById(R.id.shared_link_subtitle);
		v.setTag(holder);
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		SharedLinkHolder holder = (SharedLinkHolder) view.getTag();
		holder.title.setText(cursor.getString(SharedLinkQuery.NAME));
		holder.subtitle.setText(cursor.getString(SharedLinkQuery.URL));
	}

	public interface SharedLinkQuery {
		String[] PROJECTION = { BaseColumns._ID, MixItContract.SharedLinks.MEMBER_ID, MixItContract.SharedLinks.NAME, MixItContract.SharedLinks.ORDER_NUM,
				MixItContract.SharedLinks.SHARED_LINK_ID, MixItContract.SharedLinks.URL };

		int _ID = 0;
		int MEMBER_ID = 1;
		int NAME = 2;
		int ORDER_NUM = 3;
		int SHARED_LINK_ID = 4;
		int URL = 5;
	}
	
}
