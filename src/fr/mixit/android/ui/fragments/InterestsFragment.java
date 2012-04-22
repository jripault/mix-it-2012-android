package fr.mixit.android.ui.fragments;

import com.actionbarsherlock.app.SherlockListFragment;

public class InterestsFragment extends SherlockListFragment/* implements LoaderManager.LoaderCallbacks<Cursor>*/ {

//	static final int CURSOR_INTERESTS = 1001;
//	private InterestsAdapter adapter;
//	SESSIONS_DISPLAY_MODE mode = SESSIONS_DISPLAY_MODE.SESSIONS;
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		Bundle b = getArguments();
//		if (b != null && b.containsKey(IntentUtils.EXTRA_DISPLAY_MODE)) {
//			int newMode = b.getInt(IntentUtils.EXTRA_DISPLAY_MODE, 0);
//			if (newMode == 1) {
//				mode = SESSIONS_DISPLAY_MODE.LIGHTNING_TALKS;
//			}
//		}
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		View v = super.onCreateView(inflater, container, savedInstanceState);
//		return v;
//	}
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//
//		adapter = new InterestsAdapter(getActivity());
//		setListAdapter(adapter);
//	}
//
//	@Override
//	public void onStart() {
//		super.onStart();
//
//		LoaderManager lm = getLoaderManager();
//		lm.restartLoader(CURSOR_INTERESTS, null, this);
//	}
//
//	@Override
//	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//		if (id == CURSOR_INTERESTS) {
//			return new CursorLoader(getActivity(), MixItContract.Interests.CONTENT_URI, InterestsQuery.PROJECTION_WITH_SESSIONS_COUNT, null, null, MixItContract.Interests.DEFAULT_SORT);
//		}
//		return null;
//	}
//
//	@Override
//	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//		int id = loader.getId();
//		if (id == CURSOR_INTERESTS) {
//			adapter.changeCursor(data);
//		}
//	}
//
//	@Override
//	public void onLoaderReset(Loader<Cursor> loader) {
//		int id = loader.getId();
//		if (id == CURSOR_INTERESTS) {
//			adapter.changeCursor(null);
//		}
//	}
//	
//	@Override
//	public void onListItemClick(ListView l, View v, int position, long id) {
//		super.onListItemClick(l, v, position, id);
//		
//		final Cursor interest = (Cursor) adapter.getItem(position);
//		Uri sessionUri;
//		if (interest != null) {
//			int interestId = interest.getInt(InterestsQuery.INTEREST_ID);
//			sessionUri = MixItContract.Sessions.buildSessionsUri(String.valueOf(interestId), mode == SESSIONS_DISPLAY_MODE.SESSIONS);
//		} else {
//			sessionUri = mode == SESSIONS_DISPLAY_MODE.SESSIONS ? MixItContract.Sessions.CONTENT_URI : MixItContract.Sessions.CONTENT_URI_LIGNTHNING;
//		}
//		
//        final Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(sessionUri);
//        startActivity(intent);
//	}
//	
//	public void setDisplayMode(SESSIONS_DISPLAY_MODE newMode) {
//		if (mode != newMode) {
//			mode = newMode;
//		}
//	}

}
