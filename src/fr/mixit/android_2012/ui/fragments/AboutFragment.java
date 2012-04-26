package fr.mixit.android_2012.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

import fr.mixit.android_2012.R;
import fr.mixit.android_2012.utils.UIUtils;

public class AboutFragment extends SherlockDialogFragment {

	public static final String TAG = AboutFragment.class.getSimpleName();

	public static AboutFragment newInstance(Intent intent) {
		AboutFragment f = new AboutFragment();
		f.setArguments(UIUtils.intentToFragmentArguments(intent));
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_about, container, false);
		final TextView version = (TextView) v.findViewById(R.id.about_version);
		version.setText(getString(R.string.about_text4, UIUtils.getAppVersionName(getActivity(), getActivity().getPackageName())));
		return v;
	}

}
