package fr.mixit.android.ui.fragments;

import android.app.Activity;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.nostra13.universalimageloader.core.ImageLoader;

import fr.mixit.android.MixItApplication;
import fr.mixit.android.R;
import fr.mixit.android.provider.MixItContract;
import fr.mixit.android.services.MixItService;
import fr.mixit.android.ui.InterestsActivity;
import fr.mixit.android.ui.adapters.InterestsAdapter;
import fr.mixit.android.ui.adapters.MembersAdapter;
import fr.mixit.android.ui.adapters.TabsAdapter;
import fr.mixit.android.ui.controller.TabContainer;
import fr.mixit.android.ui.controller.TabInterests;
import fr.mixit.android.ui.controller.TabInterests.InterestsListener;
import fr.mixit.android.ui.controller.TabSessionSummarySpeakers;
import fr.mixit.android.ui.controller.TabSessionSummarySpeakers.SpeakersListener;
import fr.mixit.android.utils.IntentUtils;
import fr.mixit.android.utils.UIUtils;

public class SessionDetailsFragment extends BoundServiceFragment implements LoaderManager.LoaderCallbacks<Cursor>, SpeakersListener, InterestsListener {

	public static final String TAG = SessionDetailsFragment.class.getSimpleName();
	
	static final int CURSOR_SESSION = 1006;
//	static final int CURSOR_COMMENTS = 1007;
	static final int CURSOR_SPEAKERS = 1008;
//	static final int CURSOR_ACTIVITIES = 1009;
//	static final int CURSOR_IN_PARALLEL = 1010;
	static final int CURSOR_INTERESTS = 1011;

	static final String TAB_SUMMARY = "summary";
//	static final String TAB_COMMENTS = "comments";
//	static final String TAB_IN_PARALLEL = "in_parallel";
//	static final String TAB_ACTIVITIES = "activities";
	static final String TAB_INTERESTS = "interests";

	static final String EXTRA_SESSION_ID = "fr.mixit.android.ui.fragments.EXTRA_SESSION_ID";

	int mSessionId;
	boolean mIsVoted = false;
	boolean mIsSession = true;
	String mTitleStr = null;
	boolean mIsFirstLoad = true;

	ViewAnimator mViewAnimator;
	TextView mTitle;
	TextView mTime;
	TabHost mTabHost;
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;

	ImageLoader mImageLoader = ImageLoader.getInstance();

	public interface SessionDetailsContract {
		public void refreshMenu();
		
		public void refreshList();
	}

	SessionDetailsContract mContract;
	
	public static SessionDetailsFragment newInstance(Intent intent) {
		SessionDetailsFragment f = new SessionDetailsFragment();
		f.setArguments(UIUtils.intentToFragmentArguments(intent));
		return f;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mContract = (SessionDetailsContract) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement SessionDetailsContract");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_session_details, container, false);
		mViewAnimator = (ViewAnimator) root.findViewById(R.id.session_animator);
		mTitle = (TextView) root.findViewById(R.id.session_title);
		mTime = (TextView) root.findViewById(R.id.session_time);
		mTime.setVisibility(View.GONE);
		mTabHost = (TabHost) root.findViewById(android.R.id.tabhost);
		mViewPager = (ViewPager) root.findViewById(R.id.pager);
		return root;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mTabHost.setup();

		mTabsAdapter = new TabsAdapter(getActivity(), mTabHost, mViewPager);

		TabContainer tabContainer = new TabSessionSummarySpeakers(getActivity(), mViewPager, this, mImageLoader);
		mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_SUMMARY).setIndicator(UIUtils.createTabView(getActivity(), getString(R.string.session_summary))), tabContainer);
//		tabContainer = new TabSessionComments(getActivity(), mViewPager);
//		mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_COMMENTS).setIndicator(UIUtils.createTabView(getActivity(), getString(R.string.session_comments))), tabContainer);
		// tabContainer = new TabSessionInParallel(getActivity(), mViewPager);
		// mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_IN_PARALLEL).setIndicator(getString(R.string.session_in_parallel)), tabContainer);
		// tabContainer = new TabSessionActivities(getActivity(), mViewPager);
		// mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_ACTIVITIES).setIndicator(getString(R.string.session_activities)), tabContainer);
		tabContainer = new TabInterests(getActivity(), mViewPager, this, true);
		mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_INTERESTS).setIndicator(UIUtils.createTabView(getActivity(), getString(R.string.session_interests))), tabContainer);

		clear();
	}

	@Override
	public void onStart() {
		super.onStart();

		reload();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		mImageLoader.stop();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == CURSOR_SESSION) {
			final Intent i = UIUtils.fragmentArgumentsToIntent(args);
			mSessionId = fetchIdSession(i.getData(), args);
			if (mSessionId == -1) {
				Log.e(TAG, "this case should have been detected before in reload() method");
				displaySession(null);
				return null;
			} else {
				Uri sessionUri = MixItContract.Sessions.buildSessionUri(String.valueOf(mSessionId));
				return new CursorLoader(getActivity(), sessionUri, SessionQuery.PROJECTION, null, null, null);
			}
//		} else if (id == CURSOR_COMMENTS) {
//			final Intent i = UIUtils.fragmentArgumentsToIntent(args);
//			mSessionId = fetchIdSession(i.getData(), args);
//			if (mSessionId == -1) {
//				Log.e(TAG, "this case should have been detected before in reload() method");
//				displayComments(null);
//				return null;
//			} else {
//				Uri commentsUri = MixItContract.Sessions.buildCommentsDirUri(String.valueOf(mSessionId));
//				return new CursorLoader(getActivity(), commentsUri, CommentsAdapter.CommentsQuery.PROJECTION, null, null, null);
//			}
		} else if (id == CURSOR_SPEAKERS) {
			final Intent i = UIUtils.fragmentArgumentsToIntent(args);
			mSessionId = fetchIdSession(i.getData(), args);
			if (mSessionId == -1) {
				Log.e(TAG, "this case should have been detected before in reload() method");
				displaySpeakers(null);
				return null;
			} else {
				Uri speakersUri = MixItContract.Sessions.buildSpeakersDirUri(String.valueOf(mSessionId));
				return new CursorLoader(getActivity(), speakersUri, MembersAdapter.MembersQuery.PROJECTION, null, null, null);
			}
		} else if (id == CURSOR_INTERESTS) {
			final Intent i = UIUtils.fragmentArgumentsToIntent(args);
			mSessionId = fetchIdSession(i.getData(), args);
			if (mSessionId == -1) {
				Log.e(TAG, "this case should have been detected before in reload() method");
				displayInterests(null);
				return null;
			} else {
				Uri interestsUri = MixItContract.Sessions.buildInterestsDirUri(String.valueOf(mSessionId));
				return new CursorLoader(getActivity(), interestsUri, InterestsAdapter.InterestsQuery.PROJECTION_WITH_SESSIONS_COUNT, MixItContract.Interests.SESSIONS_COUNT + ">0", null,
						MixItContract.Interests.DEFAULT_SORT);
			}
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		int id = loader.getId();
		if (id == CURSOR_SESSION) {
			displaySession(data);
			
			if (mIsFirstLoad) {
				refreshSessionData();
			}
//		} else if (id == CURSOR_COMMENTS) {
//			displayComments(data);
		} else if (id == CURSOR_SPEAKERS) {
			displaySpeakers(data);
		} else if (id == CURSOR_INTERESTS) {
			displayInterests(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int id = loader.getId();
		if (id == CURSOR_SESSION) {
			displaySession(null);
//		} else if (id == CURSOR_COMMENTS) {
//			displayComments(null);
		} else if (id == CURSOR_SPEAKERS) {
			displaySpeakers(null);
		} else if (id == CURSOR_INTERESTS) {
			displayInterests(null);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.session_details, menu);

		MenuItem actionItem = menu.findItem(R.id.menu_item_share_action_provider_action_bar);
		ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
		actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		MenuItem actionItem = menu.findItem(R.id.menu_item_vote_favorite);
		if (!mIsSession) {
			if (MixItApplication.FORCE_OFFLINE) {
				actionItem.setVisible(false);
				actionItem.setEnabled(false);
			}
			if (mIsVoted) {
				actionItem.setTitle(R.string.action_bar_vote_delete);
				actionItem.setIcon(R.drawable.ic_vote_down);
			} else {
				actionItem.setTitle(R.string.action_bar_vote_add);
				actionItem.setIcon(R.drawable.ic_vote_up);
			}
		} else {
			if (mIsVoted) {
				actionItem.setTitle(R.string.action_bar_favorite_delete);
				actionItem.setIcon(R.drawable.ic_starred);
			} else {
				actionItem.setTitle(R.string.action_bar_favorite_add);
				actionItem.setIcon(R.drawable.ic_star);
			}
		}

		actionItem = menu.findItem(R.id.menu_item_share_action_provider_action_bar);
		ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
		actionProvider.setShareIntent(createShareIntent());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_item_vote_favorite) {
			if (!mIsSession) {
				voteForLightning(!mIsVoted);
			} else {
				favoriteSession(!mIsVoted);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public void setSessionId(int sessionId) {
		if (mSessionId != sessionId) {
			mSessionId = sessionId;
			
			mIsFirstLoad = true;
			
			Bundle b = getArguments();
			b.putInt(EXTRA_SESSION_ID, sessionId);

			reload();
		}
	}

	int fetchIdSession(Uri uriSession, Bundle b) {
		if (uriSession == null) {
			if (b.containsKey(EXTRA_SESSION_ID)) {
				return b.getInt(EXTRA_SESSION_ID, -1);
			}
		} else {
			try {
				return Integer.parseInt(MixItContract.Sessions.getSessionId(uriSession));
			} catch (NumberFormatException e) {
			}
		}
		return -1;
	}

	void reload() {
		Bundle args = getArguments();
		final Intent i = UIUtils.fragmentArgumentsToIntent(args);
		Uri sessionUri = i.getData();
		if (sessionUri == null) {
			if (!args.containsKey(EXTRA_SESSION_ID) || args.getInt(EXTRA_SESSION_ID) == -1) {
				clear();
				return;
			}
		}
		if (getActivity() == null || isDetached()) {
			return;
		}
		LoaderManager lm = getLoaderManager();
		lm.restartLoader(CURSOR_SESSION, args, this);
//		lm.restartLoader(CURSOR_COMMENTS, args, this);
		lm.restartLoader(CURSOR_SPEAKERS, args, this);
		lm.restartLoader(CURSOR_INTERESTS, args, this);
	}

	void clear() {
		displayNoSessionSelected();
		displaySession(null);
//		displaySpeakers(null); // TODO changing the cursor of the list with a header crashes application when refresh ui of the listview...
//		displayComments(null);
		displayInterests(null);
	}

	void displayNoSessionSelected() {
		if (mViewAnimator.getDisplayedChild() == 1) {
			mViewAnimator.showPrevious();
		}
	}

	void displaySession(Cursor c) {
		String time = null;
		String desc = null;
		if (c != null && c.moveToFirst()) {
			if (mViewAnimator.getDisplayedChild() == 0) {
				mViewAnimator.showNext();
			}
			mTitleStr = c.getString(SessionQuery.TITLE);
			time = c.getString(SessionQuery.TIME);
			desc = c.getString(SessionQuery.DESC);
			mIsSession = c.getInt(SessionQuery.IS_SESSION) == 1 ? true : false;
			mIsVoted = c.getInt(mIsSession ? SessionQuery.IS_FAVORITE : SessionQuery.MY_VOTE) == 1 ? true : false;
		} else {
			displayNoSessionSelected();
		}
		mTitle.setText(mTitleStr);
		mTime.setText(time);

		try {
			TabSessionSummarySpeakers tabContainer = (TabSessionSummarySpeakers) mTabsAdapter.getTabContainer(TAB_SUMMARY);
			if (tabContainer != null) {
				tabContainer.setSummary(desc);
			} else {
				Log.e(TAG, "The TabContainer requested with tag TAB_SUMMARY does not exist");
			}
		} catch (ClassCastException e) {
			Log.e(TAG, "The TabContainer requested with tag TAB_SUMMARY is not a TabSessionSummarySpeakers object", e);
		}
		if (c != null) {
			c.close();
		}

		mContract.refreshMenu();
	}

	void displaySpeakers(Cursor c) {
		try {
			TabSessionSummarySpeakers tabContainer = (TabSessionSummarySpeakers) mTabsAdapter.getTabContainer(TAB_SUMMARY);
			if (tabContainer != null) {
				tabContainer.setCursor(c);
			} else {
				Log.e(TAG, "The TabContainer requested with tag TAB_SUMMARY does not exist");
			}
		} catch (ClassCastException e) {
			Log.e(TAG, "The TabContainer requested with tag TAB_SUMMARY is not a TabSessionSummarySpeakers object", e);
		}
	}

//	void displayComments(Cursor c) {
//		try {
//			TabSessionComments tabContainer = (TabSessionComments) mTabsAdapter.getTabContainer(TAB_COMMENTS);
//			if (tabContainer != null) {
//				tabContainer.setCursor(c);
//			} else {
//				Log.e(TAG, "The TabContainer requested with tag TAB_COMMENTS does not exist");
//			}
//		} catch (ClassCastException e) {
//			Log.e(TAG, "The TabContainer requested with tag TAB_COMMENTS is not a TabSessionComments object", e);
//		}
//	}

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
	
	public void refreshSessionData() {
		if (MixItApplication.FORCE_OFFLINE) {
			return;
		}
		if (isBound && serviceReady) {
            setRefreshMode(true);
            
			Message msg = Message.obtain(null, mIsSession ? MixItService.MSG_TALK : MixItService.MSG_LIGHTNING_TALK, 0, 0);
			msg.replyTo = messenger;
			Bundle b = new Bundle();
			b.putInt(MixItService.EXTRA_ID, mSessionId);
			msg.setData(b);
			try {
				service.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			mIsFirstLoad = false;
		}
	}

	private interface SessionQuery {
		String[] PROJECTION = { BaseColumns._ID, MixItContract.Sessions.SESSION_ID, MixItContract.Sessions.TITLE, MixItContract.Sessions.TRACK_ID,
				MixItContract.Sessions.IS_FAVORITE, MixItContract.Sessions.IS_SESSION, MixItContract.Sessions.NB_VOTES, MixItContract.Sessions.MY_VOTE,
				MixItContract.Sessions.SUMMARY, MixItContract.Sessions.TIME, MixItContract.Sessions.DESC };

		int _ID = 0;
		int SESSION_ID = 1;
		int TITLE = 2;
		int TRACK_COLOR = 3;
		int IS_FAVORITE = 4;
		int IS_SESSION = 5;
		int NB_VOTES = 6;
		int MY_VOTE = 7;
		int SUMMARY = 8;
		int TIME = 9;
		int DESC = 10;
	}

	@Override
	public void onSpeakerItemClick(String memberId) {
		final Uri memberUri = MixItContract.Members.buildMemberUri(memberId);
		final Intent intent = new Intent(Intent.ACTION_VIEW, memberUri);
		intent.putExtra(IntentUtils.EXTRA_FROM_ADD_TO_BACKSTACK, true);
		startActivity(intent);
	}

	@Override
	public void onInterestItemClick(String interestId, String name) {
		final Uri interestUri = MixItContract.Interests.buildInterestUri(interestId);
		final Intent intent = new Intent(Intent.ACTION_VIEW, interestUri);
		intent.putExtra(InterestsActivity.EXTRA_INTEREST_NAME, name);
		intent.putExtra(InterestsActivity.EXTRA_IS_FROM_SESSION, true);
		startActivity(intent);
	}

	private Intent createShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_session_subject));
		shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_session_text, mTitleStr, "http://http://www.mix-it.fr/sessions"));
		return shareIntent;
	}

	void voteForLightning(boolean addVote) {
		if (MixItApplication.FORCE_OFFLINE) {
			return;
		}
		if (isBound && serviceReady) {
            setRefreshMode(true);
            
			Message msg = Message.obtain(null, MixItService.MSG_VOTE_LIGHTNING_TALK, 0, 0);
			msg.replyTo = messenger;
			Bundle b = new Bundle();
			b.putBoolean(MixItService.EXTRA_STATE_VOTE, addVote);
			b.putInt(MixItService.EXTRA_SESSION_ID, mSessionId);
			msg.setData(b);
			try {
				service.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	void favoriteSession(boolean addFavorite) {
		if (isBound && serviceReady) {
            setRefreshMode(true);
            
			Message msg = Message.obtain(null, MixItService.MSG_STAR_SESSION, 0, 0);
			msg.replyTo = messenger;
			Bundle b = new Bundle();
			b.putBoolean(MixItService.EXTRA_STATE_STAR, addFavorite);
			b.putInt(MixItService.EXTRA_SESSION_ID, mSessionId);
			msg.setData(b);
			try {
				service.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onMessageReceivedFromService(Message msg) {
		if (msg.what == MixItService.MSG_VOTE_LIGHTNING_TALK) {
            setRefreshMode(false);
            
			switch (msg.arg1) {
			case MixItService.Response.STATUS_OK:
				reload();
				mContract.refreshList();
				break;

			case MixItService.Response.STATUS_ERROR:
				Toast.makeText(getActivity(), R.string.error_vote_lightning_talk, Toast.LENGTH_SHORT).show();
				break;

			case MixItService.Response.STATUS_NO_CONNECTIVITY:
				Toast.makeText(getActivity(), R.string.functionnality_need_connectivity, Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		} else if (msg.what == MixItService.MSG_STAR_SESSION) {
            setRefreshMode(false);
            
			switch (msg.arg1) {
			case MixItService.Response.STATUS_OK:
				reload();
				mContract.refreshList();
				break;

			case MixItService.Response.STATUS_ERROR:
				Toast.makeText(getActivity(), R.string.error_star_session, Toast.LENGTH_SHORT).show();
				break;

			case MixItService.Response.STATUS_NO_CONNECTIVITY:
				Toast.makeText(getActivity(), R.string.functionnality_need_connectivity, Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
		} else if (msg.what == MixItService.MSG_TALK || msg.what == MixItService.MSG_LIGHTNING_TALK) {
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
