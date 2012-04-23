package fr.mixit.android.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.Window;

import fr.mixit.android.R;
import fr.mixit.android.provider.MixItContract;
import fr.mixit.android.ui.fragments.BoundServiceContract;
import fr.mixit.android.ui.fragments.MemberDetailsFragment;
import fr.mixit.android.ui.fragments.MembersListFragment;
import fr.mixit.android.ui.fragments.SessionDetailsFragment;
import fr.mixit.android.ui.fragments.SessionsListFragment;
import fr.mixit.android.ui.fragments.SessionDetailsFragment.SessionDetailsContract;
import fr.mixit.android.utils.IntentUtils;
import fr.mixit.android.utils.UIUtils;

public class InterestsActivity extends GenericMixItActivity implements TabListener, SessionDetailsContract, BoundServiceContract {

	static final String TAG = InterestsActivity.class.getSimpleName();
	
	static final String STATE_TAB_SELECTED = "fr.mixit.android.InterestsActivity.STATE_TAB_SELECTED";
	public static final String EXTRA_INTEREST_NAME = "fr.mixit.android.EXTRA_INTEREST_NAME";
	public static final String EXTRA_IS_FROM_SESSION = "fr.mixit.android.EXTRA_IS_FROM_SESSION";

	SessionsListFragment sessionsListFrag;
	SessionDetailsFragment sessionDetailsFrag;
	MembersListFragment membersListFrag;
	MemberDetailsFragment memberDetailsFrag;
	
	int mSelectedTab = 0;
	int mTopFragCommitId = -1;
	
	boolean mIsFromSession = true;

	String mInterestTitle;
	String mInterestId;
	Intent mSessionsIntent;
	Intent mMembersIntent;
	
	@Override
	protected void onCreate(Bundle savedStateInstance) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		super.onCreate(savedStateInstance);

		if (savedStateInstance != null) {
			mSelectedTab = savedStateInstance.getInt(STATE_TAB_SELECTED, mSelectedTab);
		}
		
		Intent i = getIntent();
		mInterestTitle = i.getStringExtra(EXTRA_INTEREST_NAME);
		mIsFromSession = i.getBooleanExtra(EXTRA_IS_FROM_SESSION, mIsFromSession);
		
		refactorIntent(i);
		
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        getSupportActionBar().setTitle(mInterestTitle);
        
        ActionBar.Tab tab = getSupportActionBar().newTab();
        tab.setText(getString(R.string.btn_sessions));
        tab.setTabListener(this);
        getSupportActionBar().addTab(tab);
        
        tab = getSupportActionBar().newTab();
        tab.setText(getString(R.string.btn_members));
        tab.setTabListener(this);
        getSupportActionBar().addTab(tab);
        
        getSupportActionBar().setSelectedNavigationItem(mSelectedTab);
	}

	@Override
	protected int getContentLayoutId() {
		return R.layout.activity_interests;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_TAB_SELECTED, mSelectedTab);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		int tabPosition = tab.getPosition();
		switch (tabPosition) {
		case 0:
			if (sessionsListFrag == null) {
				sessionsListFrag = SessionsListFragment.newInstance(mSessionsIntent);
			}
			ft.replace(R.id.content_interests_list, sessionsListFrag, SessionsListFragment.TAG);
			if (UIUtils.isTablet(this)) {
				if (sessionDetailsFrag == null) {
					sessionDetailsFrag = SessionDetailsFragment.newInstance(null);
				}
				ft.replace(R.id.content_interest_details, sessionDetailsFrag, SessionDetailsFragment.TAG);
			}
			break;

		case 1:
			if (membersListFrag == null) {
				membersListFrag = MembersListFragment.newInstance(mMembersIntent);
			}
			ft.replace(R.id.content_interests_list, membersListFrag, MembersListFragment.TAG);
			if (UIUtils.isTablet(this)) {
				if (memberDetailsFrag == null) {
					memberDetailsFrag = MemberDetailsFragment.newInstance(null);
				}
				ft.replace(R.id.content_interest_details, memberDetailsFrag, MemberDetailsFragment.TAG);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        final FragmentManager fm = getSupportFragmentManager();
		if (mTopFragCommitId != -1) {
			fm.popBackStack(mTopFragCommitId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			mTopFragCommitId = -1;
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	static final char SLASH = '/';

	@Override
	public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
		Uri uri = intent.getData();
		if (uri != null && uri.getAuthority().equals(MixItContract.Sessions.CONTENT_URI.getAuthority())) {
			boolean addToBackStack = intent.getBooleanExtra(IntentUtils.EXTRA_FROM_ADD_TO_BACKSTACK, false);
            final FragmentManager fm = getSupportFragmentManager();
			// SESSION
			if (uri.getEncodedPath().startsWith(SLASH + MixItContract.PATH_SESSIONS) || uri.getEncodedPath().startsWith(SLASH + MixItContract.PATH_LIGHTNINGS)) {
				if (UIUtils.isTablet(this)) {
					if (addToBackStack) {
						SessionDetailsFragment frag = SessionDetailsFragment.newInstance(intent);
		                FragmentTransaction ft = fm.beginTransaction();
		                ft.replace(R.id.content_interest_details, frag);
		                ft.addToBackStack(null);
		                if (mTopFragCommitId == -1) {
		                	mTopFragCommitId = ft.commit();
						} else {
							ft.commit();
						}
						return;
					} else {
						if (mTopFragCommitId != -1) {
							fm.popBackStack(mTopFragCommitId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
							mTopFragCommitId = -1;
						}
						if (sessionDetailsFrag != null) {
							int sessionId = Integer.parseInt(MixItContract.Sessions.getSessionId(uri));
							sessionDetailsFrag.setSessionId(sessionId);
							return;
						} else {
							Log.e(TAG, "no fragment session details found but device is tablet");
						}
					}
				} else {
					super.startActivityFromFragment(fragment, intent, requestCode);
					return;
				}
			} else if (uri.getEncodedPath().startsWith(SLASH + MixItContract.PATH_MEMBERS)
					|| uri.getEncodedPath().startsWith(SLASH + MixItContract.PATH_SPEAKERS)) {
				if (UIUtils.isTablet(this)) {
					if (addToBackStack) {
						MemberDetailsFragment frag = MemberDetailsFragment.newInstance(intent);
		                FragmentTransaction ft = fm.beginTransaction();
		                ft.replace(R.id.content_interest_details, frag);
		                ft.addToBackStack(null);
		                if (mTopFragCommitId == -1) {
		                	mTopFragCommitId = ft.commit();
						} else {
							ft.commit();
						}
						return;
					} else {
						if (mTopFragCommitId != -1) {
							fm.popBackStack(mTopFragCommitId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
							mTopFragCommitId = -1;
						}
						if (memberDetailsFrag != null) {
							int memberId = Integer.parseInt(MixItContract.Members.getMemberId(uri));
							memberDetailsFrag.setMemberId(memberId);
							return;
						} else {
							Log.e(TAG, "no fragment member details found but device is tablet");
						}
					}
				} else {
					super.startActivityFromFragment(fragment, intent, requestCode);
					return;
				}
			}
		}
		super.startActivityFromFragment(fragment, intent, requestCode);
		return;
	}
	
	void refactorIntent(Intent i) {
		Uri uri = i.getData();
		
		if (uri == null) {
			Log.e(TAG, "No uri provided");
			return;
		}
		
		mInterestId = MixItContract.Interests.getInterestId(uri);
		
		if (TextUtils.isEmpty(mInterestId)) {
			Log.e(TAG, "The uri provided does not correspond to a valid uri");
			return;
		}
		
		final Uri sessionsUri = MixItContract.Interests.buildSessionsDir(mInterestId);
		mSessionsIntent = new Intent(Intent.ACTION_VIEW, sessionsUri);
		
		final Uri membersUri = MixItContract.Interests.buildMembersDir(mInterestId);
		mMembersIntent = new Intent(Intent.ACTION_VIEW, membersUri);
	}

	@Override
	public void refreshMenu() {
		invalidateOptionsMenu();
	}

	@Override
	public void refreshList() {
		sessionsListFrag.reload();
	}

	public Intent getParentIntent() {
		if (UIUtils.isTablet(this)) {
			if (mIsFromSession) {
				return new Intent(this, SessionsActivity.class);
			} else {
				return new Intent(this, MembersActivity.class);
			}
		} else {
			if (mIsFromSession) {
				return new Intent(this, SessionDetailsActivity.class);
			} else {
				return new Intent(this, MemberDetailsActivity.class);
			}
		}
	}

	public Intent getGrandParentIntent() {
		if (UIUtils.isTablet(this)) {
			return new Intent(this, HomeActivity.class);
		} else {
			if (mIsFromSession) {
				return new Intent(this, SessionsActivity.class);
			} else {
				return new Intent(this, MembersActivity.class);
			}
		}
	}

	public Intent getGreatGrandParentIntent() {
		if (!UIUtils.isTablet(this)) {
			return new Intent(this, HomeActivity.class);
		}
		return null;
	}

	@Override
	public void setRefreshMode(boolean state) {
        setSupportProgressBarIndeterminateVisibility(state);
	}

}
