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
import fr.mixit.android.ui.adapters.SharedLinksAdapter;

public class TabSharedLinks extends TabContainer implements OnItemClickListener {

	ListView mListView;
	SharedLinksAdapter mAdapter;

	public interface SharedLinksListener {
		public void onSharedLinkItemClick(String url);
	}

	SharedLinksListener mListener;

	public TabSharedLinks(Context ctx, ViewGroup container, SharedLinksListener listener) {
		super(ctx, container);

		mListener = listener;
	}

	@Override
	protected void initView(ViewGroup container) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mContainerLayout = inflater.inflate(R.layout.tab_shared_links, container, false);
		mListView = (ListView) mContainerLayout.findViewById(android.R.id.list);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void setCursor(Cursor c) {
		// TODO : use ViewAnimator to switch between list and empty ?
		if (c != null && c.moveToFirst()) {
			if (mAdapter == null) {
				mAdapter = new SharedLinksAdapter(mContext);
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
			String url = c.getString(SharedLinksAdapter.SharedLinkQuery.URL);
			if (!TextUtils.isEmpty(url)) {
				mListener.onSharedLinkItemClick(url);
			}
		}
	}

}
