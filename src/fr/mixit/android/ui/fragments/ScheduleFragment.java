package fr.mixit.android.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockFragment;

import fr.mixit.android.R;

public class ScheduleFragment extends SherlockFragment {

	WebView mJpegView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_schedule, container, false);
        mJpegView = (WebView) v.findViewById(R.id.jpg_webview);
        mJpegView.getSettings().setBuiltInZoomControls(true);
        mJpegView.getSettings().setDefaultZoom(ZoomDensity.FAR);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mJpegView.loadUrl("file:///android_asset/programme_site.html");
	}
	
}
