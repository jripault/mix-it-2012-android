/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.mixit.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fr.mixit.android.R;
import fr.mixit.android.utils.UIUtils;

public class DashboardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container);

        // Attach event handlers
        View v = root.findViewById(R.id.home_btn_schedule);
        if (v != null) {
        	v.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	// TODO : uncomment after adding schedule screens
	/*                if (UIUtils.isTablet(getActivity())) {
	                    startActivity(new Intent(getActivity(), ScheduleMultiPaneActivity.class));
	                } else {
	                    startActivity(new Intent(getActivity(), ScheduleActivity.class));
	                }*/
	            }
	        });
        }
        
        v = null;
        v = root.findViewById(R.id.home_btn_stream);
        if (v != null) {
        	v.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                if (UIUtils.isTablet(getActivity())) {
	                } else {
	                    startActivity(new Intent(getActivity(), ActivityStreamActivity.class));
	                }
	            }
	        });
        }
        
        v = null;
        v = root.findViewById(R.id.home_btn_sessions);
        if (v != null) {
        	v.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                // Launch sessions list
	            	// TODO : uncomment after adding sessions screens
	/*                if (UIUtils.isTablet(getActivity())) {
	                    startActivity(new Intent(getActivity(), SessionsMultiPaneActivity.class));
	                } else {
	                    final Intent intent = new Intent(Intent.ACTION_VIEW, ScheduleContract.Tracks.CONTENT_URI);
	                    intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.title_session_tracks));
	                    intent.putExtra(TracksFragment.EXTRA_NEXT_TYPE, TracksFragment.NEXT_TYPE_SESSIONS);
	                    startActivity(intent);
	                }*/
	            }
	        });
        }
        
        v = null;
        v = root.findViewById(R.id.home_btn_speakers);
        if (v != null) {
        	v.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	                // Launch speakers list
	            	// TODO : uncomment after adding speakers screens
	/*                if (UIUtils.isTablet(getActivity())) {
	                    startActivity(new Intent(getActivity(), SpeakersMultiPaneActivity.class));
	                } else {
	                    final Intent intent = new Intent(Intent.ACTION_VIEW, ScheduleContract.Tracks.CONTENT_URI);
	                    intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.title_speaker_tracks));
	                    intent.putExtra(TracksFragment.EXTRA_NEXT_TYPE, TracksFragment.NEXT_TYPE_SPEAKERS);
	                    startActivity(intent);
	                }*/
	            }
	        });
        }
        
        v = null;
        v = root.findViewById(R.id.home_btn_starred);
        if (v != null) {
        	v.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	// TODO : uncomment after adding starred screen
	                // Launch list of sessions and speakers the user has starred
	//                startActivity(new Intent(getActivity(), StarredActivity.class));                
	            }
	        });
        }
        
        v = null;
        v = root.findViewById(R.id.home_btn_map);
        if (v != null) {
        	v.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	// TODO : uncomment after adding map screen
	                // Launch map of conference venue
	/*                startActivity(new Intent(getActivity(), UIUtils.getMapActivityClass(getActivity())));*/
	            }
	        });
        }

        return root;
    }
}
