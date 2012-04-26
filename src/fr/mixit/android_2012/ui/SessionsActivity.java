package fr.mixit.android_2012.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Window;

import fr.mixit.android_2012.R;
import fr.mixit.android_2012.provider.MixItContract;
import fr.mixit.android_2012.ui.fragments.BoundServiceContract;
import fr.mixit.android_2012.ui.fragments.MemberDetailsFragment;
import fr.mixit.android_2012.ui.fragments.SessionDetailsFragment;
import fr.mixit.android_2012.ui.fragments.SessionDetailsFragment.SessionDetailsContract;
import fr.mixit.android_2012.ui.fragments.SessionsListFragment;
import fr.mixit.android_2012.utils.IntentUtils;
import fr.mixit.android_2012.utils.UIUtils;

public class SessionsActivity extends GenericMixItActivity implements OnNavigationListener, SessionDetailsContract, BoundServiceContract {

	public static final int DISPLAY_MODE_SESSIONS = 1204101927;
	public static final int DISPLAY_MODE_LIGHTNING_TALKS = 1204101928;
	public static final int DISPLAY_MODE_SESSIONS_STARRED = 1204101929;

	static final String TAG = SessionsActivity.class.getSimpleName();

	static final String STATE_DISPLAY_MODE = "displayMode";

	public static final String EXTRA_STARRED_MODE = "fr.mixit.android.SessionsActivity.EXTRA_STARRED_MODE";

	SessionsListFragment sessionsListFrag;
	SessionDetailsFragment sessionDetailsFrag;

	int mTopFragCommitId = -1;

	int mode = DISPLAY_MODE_SESSIONS;

	@Override
	protected void onCreate(Bundle savedStateInstance) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
		super.onCreate(savedStateInstance);

        setRefreshMode(false);
		mode = getIntent().getBooleanExtra(EXTRA_STARRED_MODE, false) ? DISPLAY_MODE_SESSIONS_STARRED : DISPLAY_MODE_SESSIONS;

		if (!(mode == DISPLAY_MODE_SESSIONS_STARRED)) {
			Context context = getSupportActionBar().getThemedContext();
			ArrayAdapter<CharSequence> listAdapter = ArrayAdapter.createFromResource(context, R.array.sessions, R.layout.sherlock_spinner_item);
			listAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

			getSupportActionBar().setListNavigationCallbacks(listAdapter, this);
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		}

		if (savedStateInstance != null) {
			mode = savedStateInstance.getInt(STATE_DISPLAY_MODE, mode);
		}

		FragmentManager fm = getSupportFragmentManager();
		sessionsListFrag = (SessionsListFragment) fm.findFragmentByTag(SessionsListFragment.TAG);
		if (sessionsListFrag == null) {
			sessionsListFrag = SessionsListFragment.newInstance(getIntent());
			fm.beginTransaction().add(R.id.content_sessions_list, sessionsListFrag, SessionsListFragment.TAG).commit();
		}

		if (!(mode == DISPLAY_MODE_SESSIONS_STARRED)) {
			int itemSelected = 0;
			switch (mode) {
			case DISPLAY_MODE_SESSIONS:
				itemSelected = 0;
				break;

			case DISPLAY_MODE_LIGHTNING_TALKS:
				itemSelected = 1;
				break;

			default:
				itemSelected = 0;
				break;
			}
			getSupportActionBar().setSelectedNavigationItem(itemSelected);
		}
		sessionsListFrag.setDisplayMode(mode);

		if (UIUtils.isTablet(this)) {
			sessionDetailsFrag = (SessionDetailsFragment) fm.findFragmentByTag(SessionDetailsFragment.TAG);
			if (sessionDetailsFrag == null) {
				sessionDetailsFrag = SessionDetailsFragment.newInstance(getIntent());
				fm.beginTransaction().add(R.id.content_session_details, sessionDetailsFrag, SessionDetailsFragment.TAG).commit();
			}
		}
	}

	@Override
	protected int getContentLayoutId() {
		return R.layout.activity_sessions;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		if (itemPosition == 0 && mode != DISPLAY_MODE_SESSIONS) {
			mode = DISPLAY_MODE_SESSIONS;
			refresh(-1);
		} else if (itemPosition == 1 && mode != DISPLAY_MODE_LIGHTNING_TALKS) {
			mode = DISPLAY_MODE_LIGHTNING_TALKS;
			refresh(-1);
		}
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_DISPLAY_MODE, mode);
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
						ft.replace(R.id.content_session_details, frag);
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
					MemberDetailsFragment frag = MemberDetailsFragment.newInstance(intent);
					FragmentTransaction ft = fm.beginTransaction();
					ft.replace(R.id.content_session_details, frag);
					ft.addToBackStack(null);
					if (mTopFragCommitId == -1) {
						mTopFragCommitId = ft.commit();
					} else {
						ft.commit();
					}
					return;
				} else {
					super.startActivityFromFragment(fragment, intent, requestCode);
					return;
				}
			}
		}
		super.startActivityFromFragment(fragment, intent, requestCode);
		return;
	}

	void refresh(int sessionId) {
		sessionsListFrag.setDisplayMode(mode);
		sessionsListFrag.reload();

		if (sessionDetailsFrag != null) {
			sessionDetailsFrag.setSessionId(sessionId);
		}
	}

	@Override
	public void refreshMenu() {
		invalidateOptionsMenu();
	}

	@Override
	public void refreshList() {
		sessionsListFrag.reload();
	}

	@Override
	public void setRefreshMode(boolean state) {
        setSupportProgressBarIndeterminateVisibility(state);
	}

}
