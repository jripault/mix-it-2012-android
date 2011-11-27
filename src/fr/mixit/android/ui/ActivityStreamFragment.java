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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import fr.mixit.android.R;

/**
 * Activity that displays the user's stream on Link-it or if no user is
 * connected, the Mix-it's stream on Link-it
 */
public class ActivityStreamFragment extends Fragment {

	private View mLoadingSpinner;
	private ListView mListView;
	private String[] mSampleItems = { "Stream Line 1", "Stream Line 2",
			"Stream Line 3", "Stream Line 4", "Stream Line 5", "Stream Line 6",
			"Stream Line 7", "Stream Line 2", "Stream Line 3", "Stream Line 4",
			"Stream Line 5", "Stream Line 6", "Stream Line 7", "Stream Line 2",
			"Stream Line 3", "Stream Line 4", "Stream Line 5", "Stream Line 6",
			"Stream Line 7" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.fragment_activity_stream, null);

		// For some reason, if we omit this, NoSaveStateFrameLayout thinks we are
		// FILL_PARENT / WRAP_CONTENT, making the progress bar stick to the top of the activity.
		root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

		mLoadingSpinner = root.findViewById(R.id.loading_spinner);
		mListView = (ListView) root.findViewById(android.R.id.list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_stream, mSampleItems);
		mListView.setAdapter(adapter);
		return root;
	}

}
