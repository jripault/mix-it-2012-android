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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ToggleButton;
import fr.mixit.android.R;
import fr.mixit.android.model.Track;
import fr.mixit.android.provider.MixItContract;
import fr.mixit.android.services.MixItService;
import fr.mixit.android.ui.SessionsActivity;
import fr.mixit.android.ui.adapters.SessionsAdapter;
import fr.mixit.android.utils.UIUtils;

public class SessionsListFragment extends BoundServiceListFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnClickListener {

	public static final String TAG = SessionsListFragment.class.getSimpleName();

	static final int CURSOR_SESSIONS = 1002;

	static final String STATE_CHECKED_POSITION = "checkedPosition";

	LinearLayout mTracksLayout;
	ToggleButton mTrackAgility;
	ToggleButton mTrackTechy;
	ToggleButton mTrackTrendy;
	ToggleButton mTrackGamy;
	ToggleButton mTrackWeby;
	private SessionsAdapter adapter;
	private int mCheckedPosition = -1;
	// private boolean mHasSetEmptyText = false;
	private int mode = SessionsActivity.DISPLAY_MODE_SESSIONS;
	boolean mIsFirstLoad = true;

	public static SessionsListFragment newInstance(Intent intent) {
		SessionsListFragment f = new SessionsListFragment();
		f.setArguments(UIUtils.intentToFragmentArguments(intent));
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_sessions_list, container, false);
		mTracksLayout = (LinearLayout) root.findViewById(R.id.tracks);
		mTrackAgility = (ToggleButton) mTracksLayout.findViewById(R.id.track_agility);
		mTrackAgility.setOnClickListener(this);
		mTrackAgility.setChecked(true);
		mTrackTechy = (ToggleButton) mTracksLayout.findViewById(R.id.track_techy);
		mTrackTechy.setOnClickListener(this);
		mTrackTechy.setChecked(true);
		mTrackTrendy = (ToggleButton) mTracksLayout.findViewById(R.id.track_trendy);
		mTrackTrendy.setOnClickListener(this);
		mTrackTrendy.setChecked(true);
		mTrackGamy = (ToggleButton) mTracksLayout.findViewById(R.id.track_gamy);
		mTrackGamy.setOnClickListener(this);
		mTrackGamy.setChecked(true);
		mTrackWeby = (ToggleButton) mTracksLayout.findViewById(R.id.track_weby);
		mTrackWeby.setOnClickListener(this);
		mTrackWeby.setChecked(true);
		
		if (mode == SessionsActivity.DISPLAY_MODE_SESSIONS) {
			mTracksLayout.setVisibility(View.VISIBLE);
		} else {
			mTracksLayout.setVisibility(View.GONE);
		}

		return root;
		// View v = super.onCreateView(inflater, container, savedInstanceState);
		// return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		if (savedInstanceState != null) {
			mCheckedPosition = savedInstanceState.getInt(STATE_CHECKED_POSITION, -1);
		}

		// if (!mHasSetEmptyText) {
		// // Could be a bug, but calling this twice makes it become visible when it shouldn't
		// // be visible.
		// setEmptyText(getString(R.string.empty_sessions));
		// mHasSetEmptyText = true;
		// }

		adapter = new SessionsAdapter(getActivity(), !(mode == SessionsActivity.DISPLAY_MODE_SESSIONS_STARRED));
		setListAdapter(adapter);
	}

	@Override
	public void onStart() {
		super.onStart();

		reload();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_CHECKED_POSITION, mCheckedPosition);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == CURSOR_SESSIONS) {
			final Intent i = UIUtils.fragmentArgumentsToIntent(args);
			Uri sessionsUri = i.getData();
			if (sessionsUri == null) {
				sessionsUri = MixItContract.Sessions.CONTENT_URI;
				sessionsUri = mode == SessionsActivity.DISPLAY_MODE_LIGHTNING_TALKS ? MixItContract.Sessions.CONTENT_URI_LIGNTHNING : MixItContract.Sessions.CONTENT_URI;
			}

			StringBuilder selection = new StringBuilder();
			if (mode == SessionsActivity.DISPLAY_MODE_SESSIONS) {
				if (!mTrackAgility.isChecked()) {
					selection.append(MixItContract.Sessions.TRACK_ID);
					selection.append("<>'");
					selection.append(Track.Agility.name());
					selection.append("'");
				}
				if (!mTrackTechy.isChecked()) {
					if (selection.length() > 0) {
						selection.append(" AND ");
					}
					selection.append(MixItContract.Sessions.TRACK_ID);
					selection.append("<>'");
					selection.append(Track.Techy.name());
					selection.append("'");
				}
				if (!mTrackTrendy.isChecked()) {
					if (selection.length() > 0) {
						selection.append(" AND ");
					}
					selection.append(MixItContract.Sessions.TRACK_ID);
					selection.append("<>'");
					selection.append(Track.Trendy.name());
					selection.append("'");
				}
				if (!mTrackGamy.isChecked()) {
					if (selection.length() > 0) {
						selection.append(" AND ");
					}
					selection.append(MixItContract.Sessions.TRACK_ID);
					selection.append("<>'");
					selection.append(Track.Gamy.name());
					selection.append("'");
				}
				if (!mTrackWeby.isChecked()) {
					if (selection.length() > 0) {
						selection.append(" AND ");
					}
					selection.append(MixItContract.Sessions.TRACK_ID);
					selection.append("<>'");
					selection.append(Track.Weby.name());
					selection.append("'");
				}
			}
			
			if (mode == SessionsActivity.DISPLAY_MODE_SESSIONS_STARRED) {
				selection.append(MixItContract.Sessions.IS_FAVORITE);
				selection.append("=1");
			}
			return new CursorLoader(getActivity(), sessionsUri, SessionsAdapter.SessionsQuery.PROJECTION, selection.toString(), null, MixItContract.Sessions.DEFAULT_SORT);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		int id = loader.getId();
		if (id == CURSOR_SESSIONS) {
			adapter.swapCursor(data);

			if (mCheckedPosition >= 0 && getView() != null) {
				getListView().setItemChecked(mCheckedPosition, true);
			}
			
			if (mIsFirstLoad) {
				refreshSessionsData();
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int id = loader.getId();
		if (id == CURSOR_SESSIONS) {
			adapter.swapCursor(null);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		final Cursor cursor = (Cursor) adapter.getItem(position);
		final String sessionId = cursor.getString(cursor.getColumnIndex(MixItContract.Sessions.SESSION_ID));
		final Uri sessionUri = MixItContract.Sessions.buildSessionUri(sessionId);
		final Intent intent = new Intent(Intent.ACTION_VIEW, sessionUri);
		startActivity(intent);

		getListView().setItemChecked(position, true);
		mCheckedPosition = position;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.track_agility) {
			reload();
		} else if (id == R.id.track_techy) {
			reload();
		} else if (id == R.id.track_trendy) {
			reload();
		} else if (id == R.id.track_gamy) {
			reload();
		} else if (id == R.id.track_weby) {
			reload();
		}
	}

	public void reload() {
		if (getActivity() == null || isDetached()) {
			return;
		}
		LoaderManager lm = getLoaderManager();
		lm.destroyLoader(CURSOR_SESSIONS);
		lm.restartLoader(CURSOR_SESSIONS, getArguments(), this);
	}
	
	void refreshSessionsData() {
		if (isBound && serviceReady) {
			Message msg = null;
			if (mode == SessionsActivity.DISPLAY_MODE_SESSIONS) {
				msg = Message.obtain(null, MixItService.MSG_TALKS, 0, 0);
			} else if (mode == SessionsActivity.DISPLAY_MODE_LIGHTNING_TALKS) {
				msg = Message.obtain(null, MixItService.MSG_LIGHTNING_TALKS, 0, 0);
			}
			if (msg != null) {
                setRefreshMode(true);

				msg.replyTo = messenger;
				Bundle b = new Bundle();
				msg.setData(b);
				try {
					service.send(msg);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				mIsFirstLoad = false;
			} else {
				setRefreshMode(false);
			}
		}
	}

	@Override
	protected void onMessageReceivedFromService(Message msg) {
		if (msg.what == MixItService.MSG_TALKS || msg.what == MixItService.MSG_LIGHTNING_TALKS) {
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

	public void clearCheckedPosition() {
		if (mCheckedPosition >= 0) {
			getListView().setItemChecked(mCheckedPosition, false);
			mCheckedPosition = -1;
		}
	}

	public void setDisplayMode(int displayMode) {
		mode = displayMode;
		
		mIsFirstLoad = true;

		if (mTracksLayout != null) {
			if (mode == SessionsActivity.DISPLAY_MODE_SESSIONS) {
				mTracksLayout.setVisibility(View.VISIBLE);
			} else {
				mTracksLayout.setVisibility(View.GONE);
			}
		}
		clearCheckedPosition();
	}

}
