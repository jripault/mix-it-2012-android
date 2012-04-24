package fr.mixit.android.ui.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;

import fr.mixit.android.MixItApplication;
import fr.mixit.android.R;
import fr.mixit.android.provider.MixItContract;
import fr.mixit.android.services.MixItService;
import fr.mixit.android.ui.MembersActivity;
import fr.mixit.android.ui.adapters.MembersAdapter;
import fr.mixit.android.utils.UIUtils;

public class MembersListFragment extends BoundServiceListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	static final int CURSOR_MEMBERS = 1003;

	static final String STATE_CHECKED_POSITION = "checkedPosition";

	public static final String TAG = MembersListFragment.class.getSimpleName();

	MembersAdapter adapter;
	int mCheckedPosition = -1;
	boolean mHasSetEmptyText = false;
	int mode = MembersActivity.DISPLAY_MODE_ALL_MEMBERS;
	ImageLoader mImageLoader = ImageLoader.getInstance();
	boolean mIsFirstLoad = true;

	public static MembersListFragment newInstance(Intent intent) {
		MembersListFragment f = new MembersListFragment();
		f.setArguments(UIUtils.intentToFragmentArguments(intent));
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		getListView().setDrawingCacheEnabled(false);

		if (savedInstanceState != null) {
			mCheckedPosition = savedInstanceState.getInt(STATE_CHECKED_POSITION, -1);
		}

		if (!mHasSetEmptyText) {
			// Could be a bug, but calling this twice makes it become visible when it shouldn't
			// be visible.
			setEmptyText(getString(R.string.empty_members));
			mHasSetEmptyText = true;
		}
		
		adapter = new MembersAdapter(getActivity(), mImageLoader);
		setListAdapter(adapter);
	}

	@Override
	public void onStart() {
		super.onStart();
		
		reload();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		mImageLoader.stop();
	}
	
	public void reload() {
		if (getActivity() == null || isDetached()) {
			return;
		}
		LoaderManager lm = getLoaderManager();
		lm.destroyLoader(CURSOR_MEMBERS);
		lm.restartLoader(CURSOR_MEMBERS, getArguments(), this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_CHECKED_POSITION, mCheckedPosition);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == CURSOR_MEMBERS) {
			final Intent i = UIUtils.fragmentArgumentsToIntent(args);
			Uri membersUri = i.getData();
			if (membersUri == null) {
				membersUri = mode == MembersActivity.DISPLAY_MODE_ALL_MEMBERS ? MixItContract.Members.CONTENT_URI : MixItContract.Members.CONTENT_URI_SPEAKERS;
			}
			return new CursorLoader(getActivity(), membersUri, MembersAdapter.MembersQuery.PROJECTION, null, null, MixItContract.Members.DEFAULT_SORT);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		int id = loader.getId();
		if (id == CURSOR_MEMBERS) {
			adapter.swapCursor(data);

			if (mCheckedPosition >= 0 && getView() != null) {
				getListView().setItemChecked(mCheckedPosition, true);
			}
			
			if (mIsFirstLoad) {
				refreshMembersData();
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int id = loader.getId();
		if (id == CURSOR_MEMBERS) {
			adapter.swapCursor(null);
		}
	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
//		v.setActivated(true);
		
		final Cursor cursor = (Cursor) adapter.getItem(position);
		final String memberId = cursor.getString(cursor.getColumnIndex(MixItContract.Members.MEMBER_ID));
		final Uri memberUri = MixItContract.Members.buildMemberUri(memberId);
		final Intent intent = new Intent(Intent.ACTION_VIEW, memberUri);
        startActivity(intent);

		getListView().setItemChecked(position, true);
		mCheckedPosition = position;
	}
	
	void refreshMembersData() {
		if (MixItApplication.FORCE_OFFLINE) {
			return;
		}
		if (isBound && serviceReady) {
            setRefreshMode(true);

			Message msg = Message.obtain(null, MixItService.MSG_MEMBERS, 0, 0);
			msg.replyTo = messenger;
			Bundle b = new Bundle();
			msg.setData(b);
			try {
				service.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			mIsFirstLoad = false;
		}
	}

	@Override
	protected void onMessageReceivedFromService(Message msg) {
		if (msg.what == MixItService.MSG_MEMBERS) {
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
            setRefreshMode(false);
		}
	}

	public void clearCheckedPosition() {
		if (mCheckedPosition >= 0) {
			getListView().setItemChecked(mCheckedPosition, false);
			mCheckedPosition = -1;
		}
	}
	
	public void setDisplayMode(int displayMode) {
//		if (mode != displayMode) { 
			mode = displayMode;
			
			mIsFirstLoad = true;

			clearCheckedPosition();
//		}
	}

}
