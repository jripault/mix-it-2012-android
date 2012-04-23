package fr.mixit.android.ui.controller;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import fr.mixit.android.R;
import fr.mixit.android.ui.adapters.InterestsAdapter;

public class TabInterests extends TabContainer implements OnItemClickListener {

	ListView mListView;
	InterestsAdapter mAdapter;
	boolean mIsFromSession;

	public interface InterestsListener {
		public void onInterestItemClick(String interestId, String name);
	}

	InterestsListener mListener;

	public TabInterests(Context ctx, ViewGroup container, InterestsListener listener, boolean isFromSession) {
		super(ctx, container);

		mListener = listener;
		mIsFromSession = isFromSession;
	}

	@Override
	protected void initView(ViewGroup container) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mContainerLayout = inflater.inflate(R.layout.tab_interests, container, false);
		mListView = (ListView) mContainerLayout.findViewById(android.R.id.list);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void setCursor(Cursor c) {
		// TODO : use ViewAnimator to switch between list and empty ?
		if (c != null && c.moveToFirst()) {
			if (mAdapter == null) {
				mAdapter = new InterestsAdapter(mContext, mIsFromSession);
				mListView.setAdapter(mAdapter);
			}

			mAdapter.changeCursor(c);
		} else {
			if (mAdapter != null) {
				mAdapter.changeCursor(null);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mListener != null) {
			Cursor c = (Cursor) mAdapter.getItem(position);
			String interestId = c.getString(InterestsAdapter.InterestsQuery.INTEREST_ID);
			if (!TextUtils.isEmpty(interestId)) {
				mListener.onInterestItemClick(interestId, c.getString(InterestsAdapter.InterestsQuery.NAME));
			}
		}
	}

}
