package fr.mixit.android.ui.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.petebevin.markdown.MarkdownProcessor;

import fr.mixit.android.R;
import fr.mixit.android.provider.MixItContract;
import fr.mixit.android.services.MixItService;
import fr.mixit.android.ui.InterestsActivity;
import fr.mixit.android.ui.adapters.InterestsAdapter;
import fr.mixit.android.ui.adapters.MembersAdapter;
import fr.mixit.android.ui.adapters.MembersAdapter.MembersQuery;
import fr.mixit.android.ui.adapters.SessionsAdapter;
import fr.mixit.android.ui.adapters.SharedLinksAdapter;
import fr.mixit.android.ui.adapters.TabsAdapter;
import fr.mixit.android.ui.controller.TabContainer;
import fr.mixit.android.ui.controller.TabInterests;
import fr.mixit.android.ui.controller.TabInterests.InterestsListener;
import fr.mixit.android.ui.controller.TabMemberBio;
import fr.mixit.android.ui.controller.TabMembers;
import fr.mixit.android.ui.controller.TabMembers.MembersListener;
import fr.mixit.android.ui.controller.TabSessions;
import fr.mixit.android.ui.controller.TabSessions.SessionsListener;
import fr.mixit.android.ui.controller.TabSharedLinks;
import fr.mixit.android.ui.controller.TabSharedLinks.SharedLinksListener;
import fr.mixit.android.utils.IntentUtils;
import fr.mixit.android.utils.UIUtils;

public class MemberDetailsFragment extends BoundServiceFragment implements LoaderManager.LoaderCallbacks<Cursor>, MembersListener, SessionsListener,
		InterestsListener, SharedLinksListener {

	public static final String TAG = MemberDetailsFragment.class.getSimpleName();

	static final String EXTRA_MEMBER_ID = "fr.mixit.android.ui.fragments.EXTRA_MEMBER_ID";

	static final int CURSOR_MEMBER = 1012;
	static final int CURSOR_INTERESTS = 1013;
	static final int CURSOR_BADGES = 1014;
	static final int CURSOR_LINKS = 1015;
	static final int CURSOR_LINKERS = 1016;
	static final int CURSOR_SESSIONS = 1017;
	static final int CURSOR_LIGHTNINGS = 1018;
	static final int CURSOR_SHARED_LINKS = 1019;
	// static final int CURSOR_STARRED_SESSIONS = 1020;

	static final String TAB_MEMBER = "member";
	static final String TAB_INTERESTS = "interests";
	static final String TAB_BADGES = "badges";
	static final String TAB_LINKS = "links";
	static final String TAB_LINKERS = "linkers";
	static final String TAB_SESSIONS = "sessions";
	static final String TAB_LIGHTNINGS = "lightnings";
	static final String TAB_SHARED_LINKS = "shared_links";
	// static final String TAB_STARRED_SESSIONS = "starred_sessions";

	int mMemberId;

	ViewAnimator mViewAnimator;
	TextView mName;
	TextView mCompany;
	TextView mShortDesc;
	TextView mNbConsult;
	ImageView mImage;

	TabHost mTabHost;
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	
	DisplayImageOptions mOptions;
	
	ImageLoader mImageLoader = ImageLoader.getInstance();
	boolean mIsFirstLoad = true;

	// public interface MemberDetailsContract {
	// public void onLinkOrLinkerItemClick(int memberId);
	// }
	//
	// MemberDetailsContract mContract;

	public static MemberDetailsFragment newInstance(Intent intent) {
		MemberDetailsFragment f = new MemberDetailsFragment();
		f.setArguments(UIUtils.intentToFragmentArguments(intent));
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mOptions = new DisplayImageOptions.Builder()
			.showImageForEmptyUrl(R.drawable.speaker_thumbnail)
			.showStubImage(R.drawable.speaker_thumbnail)
			.cacheInMemory()
			.cacheOnDisc()
//			.decodingType(DecodingType.MEMORY_SAVING)
			.build();
	}

	// @Override
	// public void onAttach(Activity activity) {
	// super.onAttach(activity);
	//
	// try {
	// mContract = (MemberDetailsContract) activity;
	// } catch (ClassCastException e) {
	// throw new ClassCastException(activity.toString() + " must implement MemberDetailsContract");
	// }
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_member_details, container, false);
		mViewAnimator = (ViewAnimator) root.findViewById(R.id.member_animator);
		mName = (TextView) root.findViewById(R.id.member_name);
		mCompany = (TextView) root.findViewById(R.id.member_company);
		mShortDesc = (TextView) root.findViewById(R.id.member_short_desc);
		mNbConsult = (TextView) root.findViewById(R.id.member_nb_consult);
		mImage = (ImageView) root.findViewById(R.id.member_image);

		mTabHost = (TabHost) root.findViewById(android.R.id.tabhost);
		mViewPager = (ViewPager) root.findViewById(R.id.pager);
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mTabHost.setup();

		mTabsAdapter = new TabsAdapter(getActivity(), mTabHost, mViewPager);

		TabContainer tabContainer = new TabMemberBio(getActivity(), mViewPager);
		mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_MEMBER).setIndicator(UIUtils.createTabView(getActivity(), getString(R.string.member_desc))), tabContainer);
		tabContainer = new TabMembers(getActivity(), mViewPager, this, mImageLoader);
		mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_LINKS).setIndicator(UIUtils.createTabView(getActivity(), getString(R.string.member_links))), tabContainer);
		tabContainer = new TabMembers(getActivity(), mViewPager, this, mImageLoader);
		mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_LINKERS).setIndicator(UIUtils.createTabView(getActivity(), getString(R.string.member_linkers))), tabContainer);
		tabContainer = new TabSessions(getActivity(), mViewPager, this);
		mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_SESSIONS).setIndicator(UIUtils.createTabView(getActivity(), getString(R.string.member_sessions))), tabContainer);
		tabContainer = new TabSessions(getActivity(), mViewPager, this);
		mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_LIGHTNINGS).setIndicator(UIUtils.createTabView(getActivity(), getString(R.string.member_lightnings))), tabContainer);
		// tabContainer = new TabSessions(getActivity(), mViewPager, this);
		// mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_STARRED_SESSIONS).setIndicator(UIUtils.createTabView(getActivity(), getString(R.string.member_starred_sessions))), tabContainer);
		tabContainer = new TabInterests(getActivity(), mViewPager, this, false);
		mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_INTERESTS).setIndicator(UIUtils.createTabView(getActivity(), getString(R.string.member_interests))), tabContainer);
		tabContainer = new TabSharedLinks(getActivity(), mViewPager, this);
		mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_SHARED_LINKS).setIndicator(UIUtils.createTabView(getActivity(), getString(R.string.member_shared_links))), tabContainer);
		
		clear();
	}
	
	@Override
	public void onStart() {
		super.onStart();

//		clearTabs();
		reload();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mImageLoader.stop();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == CURSOR_MEMBER) {
			final Intent i = UIUtils.fragmentArgumentsToIntent(args);
			mMemberId = fetchIdMember(i.getData(), args);
			if (mMemberId == -1) {
				Log.e(TAG, "this case should have been detected before in reload() method");
				displayMember(null);
				return null;
			} else {
				Uri memberUri = MixItContract.Members.buildMemberUri(String.valueOf(mMemberId));
				return new CursorLoader(getActivity(), memberUri, MemberQuery.PROJECTION, null, null, null);
			}
		} else if (id == CURSOR_LINKS) {
			final Intent i = UIUtils.fragmentArgumentsToIntent(args);
			mMemberId = fetchIdMember(i.getData(), args);
			if (mMemberId == -1) {
				Log.e(TAG, "this case should have been detected before in reload() method");
				displayLinks(null);
				return null;
			} else {
				Uri linksUri = MixItContract.Members.buildLinksDirUri(String.valueOf(mMemberId));
				return new CursorLoader(getActivity(), linksUri, MembersAdapter.MembersQuery.PROJECTION, null, null, MixItContract.Members.DEFAULT_SORT);
			}
		} else if (id == CURSOR_LINKERS) {
			final Intent i = UIUtils.fragmentArgumentsToIntent(args);
			mMemberId = fetchIdMember(i.getData(), args);
			if (mMemberId == -1) {
				Log.e(TAG, "this case should have been detected before in reload() method");
				displayLinkers(null);
				return null;
			} else {
				Uri linkersUri = MixItContract.Members.buildLinkersDirUri(String.valueOf(mMemberId));
				return new CursorLoader(getActivity(), linkersUri, MembersAdapter.MembersQuery.PROJECTION, null, null, MixItContract.Members.DEFAULT_SORT);
			}
		} else if (id == CURSOR_SESSIONS || id == CURSOR_LIGHTNINGS) {
			final Intent i = UIUtils.fragmentArgumentsToIntent(args);
			mMemberId = fetchIdMember(i.getData(), args);
			if (mMemberId == -1) {
				Log.e(TAG, "this case should have been detected before in reload() method");
				if (id == CURSOR_SESSIONS) {
					displaySessions(null);
				} else {
					displayLightnings(null);
				}
				return null;
			} else {
				Uri sessionsUri = MixItContract.Members.buildSessionsDirUri(String.valueOf(mMemberId), id == CURSOR_SESSIONS);
				return new CursorLoader(getActivity(), sessionsUri, SessionsAdapter.SessionsQuery.PROJECTION, null, null, MixItContract.Sessions.DEFAULT_SORT);
			}
		} else if (id == CURSOR_INTERESTS) {
			final Intent i = UIUtils.fragmentArgumentsToIntent(args);
			mMemberId = fetchIdMember(i.getData(), args);
			if (mMemberId == -1) {
				Log.e(TAG, "this case should have been detected before in reload() method");
				displayInterests(null);
				return null;
			} else {
				Uri interestsUri = MixItContract.Members.buildInterestsDirUri(String.valueOf(mMemberId));
				return new CursorLoader(getActivity(), interestsUri, InterestsAdapter.InterestsQuery.PROJECTION_WITH_MEMBERS_COUNT, MixItContract.Interests.MEMBERS_COUNT + ">0", null,
						MixItContract.Interests.DEFAULT_SORT);
			}
		} else if (id == CURSOR_SHARED_LINKS) {
			final Intent i = UIUtils.fragmentArgumentsToIntent(args);
			mMemberId = fetchIdMember(i.getData(), args);
			if (mMemberId == -1) {
				Log.e(TAG, "this case should have been detected before in reload() method");
				displaySharedLinks(null);
				return null;
			} else {
				Uri sharesLinksUri = MixItContract.Members.buildSharedLinksDirUri(String.valueOf(mMemberId));
				return new CursorLoader(getActivity(), sharesLinksUri, SharedLinksAdapter.SharedLinkQuery.PROJECTION, null, null,
						MixItContract.SharedLinks.DEFAULT_SORT);
			}
			// } else if (id == CURSOR_STARRED_SESSIONS) {
			// final Intent i = UIUtils.fragmentArgumentsToIntent(args);
			// mMemberId = fetchIdMember(i.getData(), args);
			// if (mMemberId == -1) {
			// Log.e(TAG, "this case should have been detected before in reload() method");
			// displaySessions(null);
			// return null;
			// } else {
			// Uri sessionsUri = MixItContract.Members.buildSessionsDirUri(String.valueOf(mMemberId), id == CURSOR_SESSIONS);
			// return new CursorLoader(getActivity(), sessionsUri, SessionsAdapter.SessionsQuery.PROJECTION, null, null, MixItContract.Sessions.DEFAULT_SORT);
			// }
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		int id = loader.getId();
		if (id == CURSOR_MEMBER) {
			displayMember(data);
			
			if (mIsFirstLoad) {
				refreshMemberData();
			}
		} else if (id == CURSOR_LINKS) {
			displayLinks(data);
		} else if (id == CURSOR_LINKERS) {
			displayLinkers(data);
		} else if (id == CURSOR_SESSIONS) {
			displaySessions(data);
		} else if (id == CURSOR_LIGHTNINGS) {
			displayLightnings(data);
		} else if (id == CURSOR_INTERESTS) {
			displayInterests(data);
		} else if (id == CURSOR_SHARED_LINKS) {
			displaySharedLinks(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int id = loader.getId();
		if (id == CURSOR_MEMBER) {
			displayMember(null);
		} else if (id == CURSOR_LINKS) {
			displayLinks(null);
		} else if (id == CURSOR_LINKERS) {
			displayLinkers(null);
		} else if (id == CURSOR_SESSIONS) {
			displaySessions(null);
		} else if (id == CURSOR_LIGHTNINGS) {
			displayLightnings(null);
		} else if (id == CURSOR_INTERESTS) {
			displayInterests(null);
		} else if (id == CURSOR_INTERESTS) {
			displaySharedLinks(null);
		}
	}

	public void setMemberId(int memberId) {
		if (mMemberId != memberId) {
			mMemberId = memberId;
			
			mIsFirstLoad = true;

			Bundle b = getArguments();
			b.putInt(EXTRA_MEMBER_ID, memberId);

			reload();
		}
	}

	int fetchIdMember(Uri uriMember, Bundle b) {
		if (uriMember == null) {
			if (b.containsKey(EXTRA_MEMBER_ID)) {
				return b.getInt(EXTRA_MEMBER_ID, -1);
			}
		} else {
			try {
				return Integer.parseInt(MixItContract.Members.getMemberId(uriMember));
			} catch (NumberFormatException e) {
			}
		}
		return -1;
	}

	void reload() {
		Bundle args = getArguments();
		final Intent i = UIUtils.fragmentArgumentsToIntent(args);
		Uri memberUri = i.getData();
		if (memberUri == null) {
			if (!args.containsKey(EXTRA_MEMBER_ID) || args.getInt(EXTRA_MEMBER_ID) == -1) {
				clear();
				return;
			}
		}
		if (getActivity() == null || isDetached()) {
			return;
		}
		LoaderManager lm = getLoaderManager();
		lm.restartLoader(CURSOR_MEMBER, args, this);
		lm.restartLoader(CURSOR_LINKS, args, this);
		lm.restartLoader(CURSOR_LINKERS, args, this);
		lm.restartLoader(CURSOR_SESSIONS, args, this);
		lm.restartLoader(CURSOR_LIGHTNINGS, args, this);
		lm.restartLoader(CURSOR_INTERESTS, args, this);
		lm.restartLoader(CURSOR_SHARED_LINKS, args, this);
	}

	void clear() {
		displayNoMemberSelected();
		displayMember(null);
		displayLinks(null);
		displayLinkers(null);
		displaySessions(null);
		displayLightnings(null);
		displayInterests(null);
		displaySharedLinks(null);
	}
	
	void displayNoMemberSelected() {
		if (mViewAnimator.getDisplayedChild() == 1) {
			mViewAnimator.showPrevious();
		}
	}

	void displayMember(Cursor c) {
		String name = null;
		String company = null;
		Spanned shortDesc = null;
		String nbConsult = null;

		if (c != null && c.moveToFirst()) {
			if (mViewAnimator.getDisplayedChild() == 0) {
				mViewAnimator.showNext();
			}
			StringBuilder nameStr = new StringBuilder();
			String firstName = c.getString(MemberQuery.FIRSTNAME);
			if (!TextUtils.isEmpty(firstName)) {
				nameStr.append(firstName);
				nameStr.append(' ');
			}
			nameStr.append(c.getString(MemberQuery.LASTNAME));
			name = nameStr.toString();

			company = c.getString(MemberQuery.COMPANY);
			MarkdownProcessor m = new MarkdownProcessor();
			String shortDescHTML = m.markdown(c.getString(MemberQuery.SHORT_DESC));
			shortDesc = Html.fromHtml(shortDescHTML);
			nbConsult = getString(R.string.nb_consult, c.getInt(MemberQuery.NB_CONSULT));

			mImageLoader.displayImage(c.getString(MembersQuery.IMAGE_URL), mImage, mOptions);
		} else {
			displayNoMemberSelected();
			mImage.setBackgroundDrawable(null);
		}

		mName.setText(name);
		mCompany.setText(company);
		mShortDesc.setText(shortDesc);
		mNbConsult.setText(nbConsult);

		try {
			TabMemberBio tabContainer = (TabMemberBio) mTabsAdapter.getTabContainer(TAB_MEMBER);
			if (tabContainer != null) {
				tabContainer.setCursor(c);
			} else {
				Log.e(TAG, "The TabContainer requested with tag TAB_SUMMARY does not exist");
			}
		} catch (ClassCastException e) {
			Log.e(TAG, "The TabContainer requested with tag TAB_SUMMARY is not a TabMemberBio object", e);
		}

		if (c != null) {
			c.close();
		}
	}

	void displayLinks(Cursor c) {
		try {
			TabMembers tabContainer = (TabMembers) mTabsAdapter.getTabContainer(TAB_LINKS);
			if (tabContainer != null) {
				tabContainer.setCursor(c);
			} else {
				Log.e(TAG, "The TabContainer requested with tag TAB_LINKS does not exist");
			}
		} catch (ClassCastException e) {
			Log.e(TAG, "The TabContainer requested with tag TAB_LINKS is not a TabMembers object", e);
		}
	}

	void displayLinkers(Cursor c) {
		try {
			TabMembers tabContainer = (TabMembers) mTabsAdapter.getTabContainer(TAB_LINKERS);
			if (tabContainer != null) {
				tabContainer.setCursor(c);
			} else {
				Log.e(TAG, "The TabContainer requested with tag TAB_LINKERS does not exist");
			}
		} catch (ClassCastException e) {
			Log.e(TAG, "The TabContainer requested with tag TAB_LINKERS is not a TabMembers object", e);
		}
	}

	void displaySessions(Cursor c) {
		try {
			TabSessions tabContainer = (TabSessions) mTabsAdapter.getTabContainer(TAB_SESSIONS);
			if (tabContainer != null) {
				tabContainer.setCursor(c);
			} else {
				Log.e(TAG, "The TabContainer requested with tag TAB_SESSIONS does not exist");
			}
		} catch (ClassCastException e) {
			Log.e(TAG, "The TabContainer requested with tag TAB_SESSIONS is not a TabSessions object", e);
		}
	}

	void displayLightnings(Cursor c) {
		try {
			TabSessions tabContainer = (TabSessions) mTabsAdapter.getTabContainer(TAB_LIGHTNINGS);
			if (tabContainer != null) {
				tabContainer.setCursor(c);
			} else {
				Log.e(TAG, "The TabContainer requested with tag TAB_LIGHTNINGS does not exist");
			}
		} catch (ClassCastException e) {
			Log.e(TAG, "The TabContainer requested with tag TAB_LIGHTNINGS is not a TabSessions object", e);
		}
	}

	void displayInterests(Cursor c) {
		try {
			TabInterests tabContainer = (TabInterests) mTabsAdapter.getTabContainer(TAB_INTERESTS);
			if (tabContainer != null) {
				tabContainer.setCursor(c);
			} else {
				Log.e(TAG, "The TabContainer requested with tag TAB_INTERESTS does not exist");
			}
		} catch (ClassCastException e) {
			Log.e(TAG, "The TabContainer requested with tag TAB_INTERESTS is not a TabInterests object", e);
		}
	}

	void displaySharedLinks(Cursor c) {
		try {
			TabSharedLinks tabContainer = (TabSharedLinks) mTabsAdapter.getTabContainer(TAB_SHARED_LINKS);
			if (tabContainer != null) {
				tabContainer.setCursor(c);
			} else {
				Log.e(TAG, "The TabContainer requested with tag TAB_SHARED_LINKS does not exist");
			}
		} catch (ClassCastException e) {
			Log.e(TAG, "The TabContainer requested with tag TAB_SHARED_LINKS is not a TabSharedLinks object", e);
		}
	}
	
	void refreshMemberData() {
		if (isBound && serviceReady) {
            setRefreshMode(true);

			Message msg = Message.obtain(null, MixItService.MSG_MEMBER, 0, 0);
			msg.replyTo = messenger;
			Bundle b = new Bundle();
			b.putInt(MixItService.EXTRA_ID, mMemberId);
			msg.setData(b);
			try {
				service.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			mIsFirstLoad = false;
		}
	}

	public interface MemberQuery {
		String[] PROJECTION = { BaseColumns._ID, MixItContract.Members.MEMBER_ID, MixItContract.Members.FIRSTNAME, MixItContract.Members.LASTNAME,
				MixItContract.Members.COMPANY, MixItContract.Members.IMAGE_URL, MixItContract.Members.TICKET_REGISTERED, MixItContract.Members.NB_CONSULT,
				MixItContract.Members.LONG_DESC, MixItContract.Members.SHORT_DESC };

		int _ID = 0;
		int MEMBER_ID = 1;
		int FIRSTNAME = 2;
		int LASTNAME = 3;
		int COMPANY = 4;
		int IMAGE_URL = 5;
		int TICKET_REGISTERED = 6;
		int NB_CONSULT = 7;
		int LONG_DESC = 8;
		int SHORT_DESC = 9;
	}

	@Override
	public void onMemberItemClick(String memberId) {
		final Uri memberUri = MixItContract.Members.buildMemberUri(memberId);
		final Intent intent = new Intent(Intent.ACTION_VIEW, memberUri);
		intent.putExtra(IntentUtils.EXTRA_FROM_ADD_TO_BACKSTACK, true);
		startActivity(intent);
	}

	@Override
	public void onSessionItemClick(String sessionId) {
		final Uri sessionUri = MixItContract.Sessions.buildSessionUri(sessionId);
		final Intent intent = new Intent(Intent.ACTION_VIEW, sessionUri);
		intent.putExtra(IntentUtils.EXTRA_FROM_ADD_TO_BACKSTACK, true);
		startActivity(intent);
	}

	@Override
	public void onInterestItemClick(String interestId, String name) {
		final Uri interestUri = MixItContract.Interests.buildInterestUri(interestId);
		final Intent intent = new Intent(Intent.ACTION_VIEW, interestUri);
		intent.putExtra(InterestsActivity.EXTRA_INTEREST_NAME, name);
		intent.putExtra(InterestsActivity.EXTRA_IS_FROM_SESSION, false);
		startActivity(intent);
	}
	
	static final String HTTP = "http://";
	
	@Override
	public void onSharedLinkItemClick(String url) {
		if (!url.startsWith(HTTP) && !url.startsWith(HTTP)) {
			url = HTTP + url;
		}
		final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}

	@Override
	protected void onMessageReceivedFromService(Message msg) {
		if (msg.what == MixItService.MSG_MEMBER) {
            setRefreshMode(false);
            
			switch (msg.arg1) {
			case MixItService.Response.STATUS_OK:
				reload();
				break;

			case MixItService.Response.STATUS_ERROR:
				break;

			case MixItService.Response.STATUS_NO_CONNECTIVITY:
				break;

			default:
				break;
			}
		}
	}
	
}
