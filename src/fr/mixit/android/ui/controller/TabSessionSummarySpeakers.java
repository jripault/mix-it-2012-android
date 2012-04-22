package fr.mixit.android.ui.controller;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.petebevin.markdown.MarkdownProcessor;

import fr.mixit.android.R;
import fr.mixit.android.ui.adapters.MembersAdapter;

public class TabSessionSummarySpeakers extends TabContainer implements OnItemClickListener {

	ListView mListView;
	View mHeaderSummaryView;
	MembersAdapter mAdapter;
	ImageLoader mImageLoader;
	
	public interface SpeakersListener {
		public void onSpeakerItemClick(String memberId);
	}

	SpeakersListener mListener;

	public TabSessionSummarySpeakers(Context ctx, ViewGroup container, SpeakersListener listener, ImageLoader imageLoader) {
		super(ctx, container);

		mListener = listener;
		mImageLoader = imageLoader;
	}

	@Override
	protected void initView(ViewGroup container) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mContainerLayout = inflater.inflate(R.layout.tab_session_summary, container, false);
		mListView = (ListView) mContainerLayout.findViewById(android.R.id.list);

		mHeaderSummaryView = inflater.inflate(R.layout.header_sessions_summary, mListView, false);
		mListView.addHeaderView(mHeaderSummaryView, null, false);
		mListView.setOnItemClickListener(this);
	}

	public void setSummary(String summaryStr) {
		TextView summary = (TextView) mHeaderSummaryView.findViewById(R.id.session_summary);

		if (!TextUtils.isEmpty(summaryStr)) {
			MarkdownProcessor m = new MarkdownProcessor();
			String descriptionHTML = m.markdown(summaryStr);
			summary.setText(Html.fromHtml(descriptionHTML));
		} else {
			summary.setText(R.string.no_session_summary);
		}
	}

	@Override
	public void setCursor(Cursor c) {
		View noSpeakersV = (View) mHeaderSummaryView.findViewById(R.id.session_no_speaker);
		if (c != null && c.moveToFirst()) {
			if (mAdapter == null) {
				mAdapter = new MembersAdapter(mContext, mImageLoader);
				mListView.setAdapter(mAdapter);
			}

			mAdapter.changeCursor(c);
			if (noSpeakersV != null) {
				noSpeakersV.setVisibility(View.GONE);
			}
		} else {
			if (mAdapter != null) {
				mAdapter.changeCursor(null);
			}
			if (noSpeakersV != null) {
				noSpeakersV.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mListener != null) {
			if (position > 0) {
				Cursor c = (Cursor) mAdapter.getItem(position - 1);
				String memberId = c.getString(MembersAdapter.MembersQuery.MEMBER_ID);
				if (!TextUtils.isEmpty(memberId)) {
					mListener.onSpeakerItemClick(memberId);
				}
			}
		}
	}

}
