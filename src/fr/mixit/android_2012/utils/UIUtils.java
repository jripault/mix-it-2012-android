package fr.mixit.android_2012.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import fr.mixit.android_2012.R;

public class UIUtils {

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * Converts an intent into a {@link Bundle} suitable for use as fragment arguments.
	 */
	public static Bundle intentToFragmentArguments(Intent intent) {
		Bundle arguments = new Bundle();
		if (intent == null) {
			return arguments;
		}

		final Uri data = intent.getData();
		if (data != null) {
			arguments.putParcelable("_uri", data);
		}

		final Bundle extras = intent.getExtras();
		if (extras != null) {
			arguments.putAll(intent.getExtras());
		}

		return arguments;
	}

	/**
	 * Converts a fragment arguments bundle into an intent.
	 */
	public static Intent fragmentArgumentsToIntent(Bundle arguments) {
		Intent intent = new Intent();
		if (arguments == null) {
			return intent;
		}

		final Uri data = arguments.getParcelable("_uri");
		if (data != null) {
			intent.setData(data);
		}

		intent.putExtras(arguments);
		intent.removeExtra("_uri");
		return intent;
	}

	public static View createTabView(final Context ctx, final String text) {
		View view = LayoutInflater.from(ctx).inflate(R.layout.tab_indicator_holo, null);
		TextView tv = (TextView) view.findViewById(android.R.id.title);
		tv.setText(text);
		return view;
	}
	
	@SuppressWarnings("deprecation")
	public static int getScreenWidth(Context ctx) {
		int width = 0;
		
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			width = size.x;
		} else {
			width = display.getWidth();
		}
		
		return width;
	}
	
	/**
	 * Returns the version name we are currently in
	 * @param appPackageName - full name of the package of an app, 'com.dcg.meneame' for example.
	 */
	public static String getAppVersionName(Context context, String appPackageName) {
	    if (context!= null) {
	        try {
	            return context.getPackageManager().getPackageInfo(appPackageName, 0).versionName;
	        } catch (PackageManager.NameNotFoundException e) {
	        }
	    }
	    return null;
	}

}
