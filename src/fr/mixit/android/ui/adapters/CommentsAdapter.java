package fr.mixit.android.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import fr.mixit.android.provider.MixItContract;

public class CommentsAdapter extends CursorAdapter {

	LayoutInflater inflater;

	class CommentHolder {
		TextView content;
	}

	public CommentsAdapter(Context ctx) {
		super(ctx, null, 0);

		inflater = LayoutInflater.from(ctx);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
//		View v = inflater.inflate(R.layout.item_comment, parent, false);
//		CommentHolder holder = new CommentHolder();
//		holder.content = (ImageView) v.findViewById(R.id.comment_content);
//		v.setTag(holder);
//		return v;
		return null;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		CommentHolder holder = (CommentHolder) view.getTag();
		holder.content.setText(cursor.getString(CommentsQuery.CONTENT));
	}


	// TODO : complete with other data for displaying comments (author name...
	public interface CommentsQuery {
		String[] PROJECTION = { BaseColumns._ID, MixItContract.Comments.CONTENT };

		int _ID = 0;
		int CONTENT = 1;
	}

}
