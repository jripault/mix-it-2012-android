package fr.mixit.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import fr.mixit.android.R;
import fr.mixit.android.utils.UIUtils;

public class AboutFragment extends SherlockFragment {

	public static final String TAG = AboutFragment.class.getSimpleName();

	public static AboutFragment newInstance(Intent intent) {
		AboutFragment f = new AboutFragment();
		f.setArguments(UIUtils.intentToFragmentArguments(intent));
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_about, container, false);
		final TextView version = (TextView) v.findViewById(R.id.about_version);
		version.setText(getString(R.string.about_text4, UIUtils.getAppVersionName(getActivity(), getActivity().getPackageName())));
		return v;
	}

}
