package fr.mixit.android.ui.controller;

import com.nostra13.universalimageloader.core.ImageLoader;

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
import fr.mixit.android.ui.adapters.MembersAdapter;

public class TabMembers extends TabContainer implements OnItemClickListener {

	ListView mListView;
	MembersAdapter mAdapter;
	ImageLoader mImageLoader;

	public interface MembersListener {
		public void onMemberItemClick(String memberId);
	}

	MembersListener mListener;

	public TabMembers(Context ctx, ViewGroup container, MembersListener listener, ImageLoader imageLoader) {
		super(ctx, container);

		mListener = listener;
		mImageLoader = imageLoader;
	}

	@Override
	protected void initView(ViewGroup container) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mContainerLayout = inflater.inflate(R.layout.tab_members, container, false);
		mListView = (ListView) mContainerLayout.findViewById(android.R.id.list);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void setCursor(Cursor c) {
		// TODO : use ViewAnimator to switch between list and empty ?
		if (c != null && c.moveToFirst()) {
			if (mAdapter == null) {
				mAdapter = new MembersAdapter(mContext, mImageLoader);
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
			String memberId = c.getString(MembersAdapter.MembersQuery.MEMBER_ID);
			if (!TextUtils.isEmpty(memberId)) {
				mListener.onMemberItemClick(memberId);
			}
		}
	}

}
