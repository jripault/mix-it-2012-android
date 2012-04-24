package fr.mixit.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import fr.mixit.android.MixItApplication;
import fr.mixit.android.R;
import fr.mixit.android.ui.MapActivity;
import fr.mixit.android.ui.MembersActivity;
import fr.mixit.android.ui.ScheduleActivity;
import fr.mixit.android.ui.SessionsActivity;
import fr.mixit.android.ui.StreamActivity;

public class DashboardFragment extends SherlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(MixItApplication.FORCE_OFFLINE ? R.layout.fragment_dashboard_offline : R.layout.fragment_dashboard, container, false);

		// Attach event handlers
		View v = root.findViewById(R.id.home_btn_schedule);
		if (v != null) {
			v.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					startActivity(new Intent(getActivity(), ScheduleActivity.class));
					// TODO : uncomment after adding schedule screens
					// if (UIUtils.isTablet(getActivity())) {
					// startActivity(new Intent(getActivity(), ScheduleMultiPaneActivity.class));
					// } else {
					// startActivity(new Intent(getActivity(), ScheduleActivity.class));
					// }
				}
			});
		}

		v = null;
		v = root.findViewById(R.id.home_btn_stream);
		if (v != null) {
			v.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					startActivity(new Intent(getActivity(), StreamActivity.class));
					// TODO : uncomment after adding stream screens
					// if (UIUtils.isTablet(getActivity())) {
					// } else {
					// startActivity(new Intent(getActivity(), ActivityStreamActivity.class));
					// }
				}
			});
		}

		v = null;
		v = root.findViewById(R.id.home_btn_sessions);
		if (v != null) {
			v.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
//					if (UIUtils.isTablet(getActivity())) {
						final Intent i = new Intent(getActivity(), SessionsActivity.class);
						startActivity(i);
//					} else {
//						final Intent i = new Intent(getActivity(), SessionsOverviewActivity.class);
//						startActivity(i);
//					}
					// TODO : uncomment after adding sessions screens
					// if (UIUtils.isTablet(getActivity())) {
					// startActivity(new Intent(getActivity(), SessionsMultiPaneActivity.class));
					// } else {
					// final Intent intent = new Intent(Intent.ACTION_VIEW, MixItContract.Tracks.CONTENT_URI);
					// intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.title_session_tracks));
					// startActivity(intent);
					// }
				}
			});
		}

		v = null;
		v = root.findViewById(R.id.home_btn_members);
		if (v != null) {
			v.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					startActivity(new Intent(getActivity(), MembersActivity.class));
					// TODO : uncomment after adding speakers screens
					// if (UIUtils.isTablet(getActivity())) {
					// startActivity(new Intent(getActivity(), SpeakersMultiPaneActivity.class));
					// } else {
					// final Intent intent = new Intent(Intent.ACTION_VIEW, MixItContract.Tracks.CONTENT_URI);
					// intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.title_speaker_tracks));
					// intent.putExtra(TracksFragment.EXTRA_NEXT_TYPE, TracksFragment.NEXT_TYPE_SPEAKERS);
					// startActivity(intent);
					// }
				}
			});
		}

		v = null;
		v = root.findViewById(R.id.home_btn_starred);
		if (v != null) {
			v.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					final Intent i = new Intent(getActivity(), SessionsActivity.class);
					i.putExtra(SessionsActivity.EXTRA_STARRED_MODE, true);
					startActivity(i);
					// TODO : uncomment after adding starred screen
					// Launch list of sessions and speakers the user has starred
					// startActivity(new Intent(getActivity(), StarredActivity.class));
				}
			});
		}

		v = null;
		v = root.findViewById(R.id.home_btn_map);
		if (v != null) {
			v.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					startActivity(new Intent(getActivity(), MapActivity.class));
					// TODO : uncomment after adding map screen
					// startActivity(new Intent(getActivity(), UIUtils.getMapActivityClass(getActivity())));
				}
			});
		}
		return root;
	}
}
