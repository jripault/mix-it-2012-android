package fr.mixit.android.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import fr.mixit.android.R;
import fr.mixit.android.model.Track;
import fr.mixit.android.provider.MixItContract;

public class TracksFragment extends SherlockListFragment {
	
	private TracksAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		adapter = new TracksAdapter(getActivity());
		setListAdapter(adapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
        final Track track = (Track) adapter.getItem(position);
        Uri sessionUri;
        if (track == null) {
        	sessionUri = MixItContract.Sessions.CONTENT_URI;
        } else {
        	sessionUri = MixItContract.Sessions.buildSessionsUri(track);
        }

//        sessionUri.buildUpon()
//    	.appendQueryParameter(MixItContract.SessionCounts.SESSION_INDEX_EXTRAS, Boolean.TRUE.toString())
//    	.build();
        
//        final String trackTitle = cursor.getString(TracksQuery.TRACK_NAME);
//        final int trackColor = cursor.getInt(TracksQuery.TRACK_COLOR);

        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(sessionUri);
//        intent.putExtra(Intent.EXTRA_TITLE, trackTitle);
//        intent.putExtra(SessionsActivity.EXTRA_TRACK_COLOR, trackColor);
//        intent.putExtra(SessionsActivity.EXTRA_FOCUS_CURRENT_NEXT_SESSION, true);
        startActivity(intent);
	}
	
	private class TracksAdapter extends BaseAdapter {
		
		LayoutInflater inflater;
		
		class TrackHolder {
			TextView name;
			TextView nbSession;
		}
		
		public TracksAdapter(Context ctx) {
			super();
			
			inflater = LayoutInflater.from(ctx);
		}

		@Override
		public int getCount() {
			return Track.values().length;
		}

		@Override
		public Object getItem(int position) {
			return Track.values()[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TrackHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_interest, parent, false);
				holder = new TrackHolder();
				holder.name = (TextView) convertView.findViewById(R.id.interest_name);
				holder.nbSession = (TextView) convertView.findViewById(R.id.nb_sessions);
				convertView.setTag(holder);
			} else {
				holder = (TrackHolder) convertView.getTag();
			}
			
			holder.name.setText(getItem(position).toString());
//			holder.nbSession.setText(text); // TODO : add number of sessions in this track
			
			return convertView;
		}
		
	}

}
