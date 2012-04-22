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
import fr.mixit.android.ui.adapters.SessionsAdapter;

public class TabSessions extends TabContainer implements OnItemClickListener {

	ListView mListView;
	SessionsAdapter mAdapter;

	public interface SessionsListener {
		public void onSessionItemClick(String sessionId);
	}

	SessionsListener mListener;

	public TabSessions(Context ctx, ViewGroup container, SessionsListener listener) {
		super(ctx, container);

		mListener = listener;
	}

	@Override
	protected void initView(ViewGroup container) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mContainerLayout = inflater.inflate(R.layout.tab_sessions, container, false);
		mListView = (ListView) mContainerLayout.findViewById(android.R.id.list);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void setCursor(Cursor c) {
		// TODO : use ViewAnimator to switch between list and empty ?
		if (c != null && c.moveToFirst()) {
			if (mAdapter == null) {
				mAdapter = new SessionsAdapter(mContext);
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
			String sessionId = c.getString(SessionsAdapter.SessionsQuery.SESSION_ID);
			if (!TextUtils.isEmpty(sessionId)) {
				mListener.onSessionItemClick(sessionId);
			}
		}
	}

}
