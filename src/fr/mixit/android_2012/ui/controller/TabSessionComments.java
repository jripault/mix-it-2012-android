package fr.mixit.android_2012.ui.controller;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;
import fr.mixit.android_2012.R;
import fr.mixit.android_2012.ui.adapters.CommentsAdapter;

public class TabSessionComments extends TabContainer {

	ListView mListView;
	CommentsAdapter mAdapter;

	public TabSessionComments(Context ctx, ViewGroup container) {
		super(ctx, container);
	}

	@Override
	protected void initView(ViewGroup container) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mContainerLayout = inflater.inflate(R.layout.tab_session_comments, container, false);
		mListView = (ListView) mContainerLayout.findViewById(android.R.id.list);
	}

	@Override
	public void setCursor(Cursor c) {
		// TODO : use ViewAnimator to switch between list and content
//		View noComments = (View) mContainerLayout.findViewById(R.id.session_no_comments);
//		if (c != null && c.moveToFirst()) {
//			if (mAdapter == null) {
//				mAdapter = new CommentsAdapter(mContext);
//				mListView.setAdapter(mAdapter);
//			}
//
//			mAdapter.changeCursor(c);
//			if (noComments != null) {
//				noComments.setVisibility(View.GONE);
//			}
//		} else {
//			if (mAdapter != null) {
//				mAdapter.changeCursor(null);
//			}
//			if (noComments != null) {
//				noComments.setVisibility(View.VISIBLE);
//			}
//		}
	}

}
