package fr.mixit.android.ui;

import com.actionbarsherlock.view.Window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import fr.mixit.android.R;
import fr.mixit.android.ui.fragments.AccountFragment;
import fr.mixit.android.ui.fragments.BoundServiceContract;
import fr.mixit.android.ui.fragments.LoginAndroidAccountsListFragment;
import fr.mixit.android.ui.fragments.LoginMixItFragment;
import fr.mixit.android.ui.fragments.LoginMixItFragment.LoginMixItContract;
import fr.mixit.android.utils.UIUtils;

public class LoginMixItActivity extends GenericMixItActivity implements LoginMixItContract, BoundServiceContract {

	LoginMixItFragment mLoginMixItFragment;
	
	@Override
	protected void onCreate(Bundle savedStateInstance) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		super.onCreate(savedStateInstance);

        setRefreshMode(false);
		FragmentManager fm = getSupportFragmentManager();
		mLoginMixItFragment = (LoginMixItFragment) fm.findFragmentByTag(AccountFragment.TAG);
		if (mLoginMixItFragment == null) {
			mLoginMixItFragment = LoginMixItFragment.newInstance(getIntent());
			fm.beginTransaction().add(R.id.content_login_mixit, mLoginMixItFragment, LoginMixItFragment.TAG).commit();
		}
		
		setResult(RESULT_CANCELED);
	}

	@Override
	protected int getContentLayoutId() {
		return R.layout.activity_login_mixit;
	}

	@Override
	public void loginSuccessful() {
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void loginFailed() {
		// TODO : display error message ? or done in the fragment ?
	}
	
	@Override
	public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
		String action = intent.getAction();
		if (LoginAndroidAccountsListFragment.ACTION_ANDROID_ACCOUNTS.equals(action)) {
			if (UIUtils.isTablet(this)) {
				// TODO : show LoginAndroidAccountsListFragment in dialog mode for tablet
			} else {
				super.startActivityFromFragment(fragment, intent, requestCode);
			}
			return;
		}
		
		super.startActivityFromFragment(fragment, intent, requestCode);
	}

	public Intent getParentIntent() {
		return new Intent(this, HomeActivity.class);
	}

	public Intent getGrandParentIntent() {
		return null;
	}

	public Intent getGreatGrandParentIntent() {
		return null;
	}

	@Override
	public void setRefreshMode(boolean state) {
        setSupportProgressBarIndeterminateVisibility(state);
	}
	
}
