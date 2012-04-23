package fr.mixit.android.ui;

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

import fr.mixit.android.R;
import fr.mixit.android.provider.MixItContract;
import fr.mixit.android.ui.fragments.BoundServiceContract;
import fr.mixit.android.ui.fragments.MemberDetailsFragment;
import fr.mixit.android.ui.fragments.MembersListFragment;
import fr.mixit.android.ui.fragments.SessionDetailsFragment;
import fr.mixit.android.ui.fragments.SessionDetailsFragment.SessionDetailsContract;
import fr.mixit.android.utils.IntentUtils;
import fr.mixit.android.utils.UIUtils;

public class MembersActivity extends GenericMixItActivity implements OnNavigationListener/*, MemberDetailsContract */, SessionDetailsContract, BoundServiceContract {

	public static final int DISPLAY_MODE_ALL_MEMBERS = 1204101929;
	public static final int DISPLAY_MODE_SPEAKERS = 1204101930;

	static final String TAG = MembersActivity.class.getSimpleName();

	static final String STATE_DISPLAY_MODE = "displayMode";

	MembersListFragment membersListFrag;
	MemberDetailsFragment memberDetailsFrag;
	
	int mTopFragCommitId = -1;
	
	int mode = DISPLAY_MODE_ALL_MEMBERS;

	@Override
	protected void onCreate(Bundle savedStateInstance) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		super.onCreate(savedStateInstance);

		Context context = getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> listAdapter = ArrayAdapter.createFromResource(context, R.array.members, R.layout.sherlock_spinner_item);
		listAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		getSupportActionBar().setListNavigationCallbacks(listAdapter, this);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		if (savedStateInstance != null) {
			mode = savedStateInstance.getInt(STATE_DISPLAY_MODE, DISPLAY_MODE_ALL_MEMBERS);
		}

		FragmentManager fm = getSupportFragmentManager();
		membersListFrag = (MembersListFragment) fm.findFragmentByTag(MembersListFragment.TAG);
		if (membersListFrag == null) {
			membersListFrag = MembersListFragment.newInstance(getIntent());
			fm.beginTransaction().add(R.id.content_members_list, membersListFrag, MembersListFragment.TAG).commit();
		}

		int itemSelected = 0;
		switch (mode) {
		case DISPLAY_MODE_ALL_MEMBERS:
			itemSelected = 0;
			break;

		case DISPLAY_MODE_SPEAKERS:
			itemSelected = 1;
			break;

		default:
			itemSelected = 0;
			break;
		}
		getSupportActionBar().setSelectedNavigationItem(itemSelected);
		membersListFrag.setDisplayMode(mode);

		if (UIUtils.isTablet(this)) {
			memberDetailsFrag = (MemberDetailsFragment) fm.findFragmentByTag(MemberDetailsFragment.TAG);
			if (memberDetailsFrag == null) {
				memberDetailsFrag = MemberDetailsFragment.newInstance(getIntent());
				fm.beginTransaction().add(R.id.content_member_details, memberDetailsFrag, MemberDetailsFragment.TAG).commit();
			}
		}
	}

	@Override
	protected int getContentLayoutId() {
		return R.layout.activity_members;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		if (itemPosition == 0 && mode != DISPLAY_MODE_ALL_MEMBERS) {
			mode = DISPLAY_MODE_ALL_MEMBERS;
			refresh(-1);
		} else if (itemPosition == 1 && mode != DISPLAY_MODE_SPEAKERS) {
			mode = DISPLAY_MODE_SPEAKERS;
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
		if (uri != null && uri.getAuthority().equals(MixItContract.Members.CONTENT_URI.getAuthority())) {
			boolean addToBackStack = intent.getBooleanExtra(IntentUtils.EXTRA_FROM_ADD_TO_BACKSTACK, false);
            final FragmentManager fm = getSupportFragmentManager();
			// MEMBER
			if (uri.getEncodedPath().startsWith(SLASH + MixItContract.PATH_MEMBERS) || uri.getEncodedPath().startsWith(SLASH + MixItContract.PATH_SPEAKERS)) {
				if (UIUtils.isTablet(this)) {
					if (addToBackStack) {
						MemberDetailsFragment frag = MemberDetailsFragment.newInstance(intent);
		                FragmentTransaction ft = fm.beginTransaction();
		                ft.replace(R.id.content_member_details, frag);
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
			} else if (uri.getEncodedPath().startsWith(SLASH + MixItContract.PATH_SESSIONS)
					|| uri.getEncodedPath().startsWith(SLASH + MixItContract.PATH_LIGHTNINGS)) {
				if (UIUtils.isTablet(this)) {
					SessionDetailsFragment frag = SessionDetailsFragment.newInstance(intent);
	                FragmentTransaction ft = fm.beginTransaction();
	                ft.replace(R.id.content_member_details, frag);
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
	}

	void refresh(int memberId) {
		membersListFrag.setDisplayMode(mode);
		membersListFrag.reload();

		if (memberDetailsFrag != null) {
			memberDetailsFrag.setMemberId(memberId);
		}
	}

	@Override
	public void refreshMenu() {
		invalidateOptionsMenu();
	}

	@Override
	public void refreshList() {
		// Nothing to do because we don't display the list of sessions
	}

	@Override
	public void setRefreshMode(boolean state) {
        setSupportProgressBarIndeterminateVisibility(state);
	}

}
