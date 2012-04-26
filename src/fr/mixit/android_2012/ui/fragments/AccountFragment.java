package fr.mixit.android_2012.ui.fragments;

import android.app.Activity;
import android.content.Intent;

import com.actionbarsherlock.app.SherlockFragment;

import fr.mixit.android_2012.utils.IntentUtils;
import fr.mixit.android_2012.utils.UIUtils;

public class AccountFragment extends SherlockFragment {

	public static final String TAG = AccountFragment.class.getSimpleName();

	boolean mIsFirstStart = true;

	public static AccountFragment newInstance(Intent intent) {
		AccountFragment f = new AccountFragment();
		f.setArguments(UIUtils.intentToFragmentArguments(intent));
		return f;
	}

	@Override
	public void onStart() {
		super.onStart();

		if (!isLogged()) {
			if (mIsFirstStart) {
				startLoginScreen();
			}
		} else {
			refresh();
		}

		mIsFirstStart = false;
	}

	boolean isLogged() {
		return false;
	}

	void startLoginScreen() {
		Intent i = new Intent(LoginMixItFragment.ACTION_LOGIN_MIXIT);
		startActivityForResult(i, IntentUtils.REQUEST_CODE_LOGIN_FROM_ACCOUNT);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == IntentUtils.REQUEST_CODE_LOGIN_FROM_ACCOUNT) {
			if (resultCode == Activity.RESULT_CANCELED) {
				if (getActivity() != null && !isDetached()) {
					getActivity().finish();
				}	
			} else {
				refresh();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	void refresh() {
		// TODO : display user informations
	}

}
