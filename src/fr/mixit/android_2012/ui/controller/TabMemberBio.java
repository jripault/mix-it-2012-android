package fr.mixit.android_2012.ui.controller;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.petebevin.markdown.MarkdownProcessor;

import fr.mixit.android_2012.R;
import fr.mixit.android_2012.ui.fragments.MemberDetailsFragment;

public class TabMemberBio extends TabContainer {

	TextView mBio;

	public TabMemberBio(Context ctx, ViewGroup container) {
		super(ctx, container);
	}

	@Override
	protected void initView(ViewGroup container) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mContainerLayout = inflater.inflate(R.layout.tab_member_bio, container, false);
		mBio = (TextView) mContainerLayout.findViewById(R.id.member_bio);
	}

	@Override
	public void setCursor(Cursor c) {
		// TODO : use ViewAnimator to switch between content and empty ?
		if (c != null) {
			String bio = c.getString(MemberDetailsFragment.MemberQuery.LONG_DESC);
			if (!TextUtils.isEmpty(bio)) {
				MarkdownProcessor m = new MarkdownProcessor();
				String descriptionHTML = m.markdown(bio);
				mBio.setText(Html.fromHtml(descriptionHTML));
			} else {
				mBio.setText(R.string.no_member_bio);
			}
		} else {
			mBio.setText(R.string.no_member_bio);
		}
	}

}
