package fr.mixit.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.view.Window;

import fr.mixit.android.R;
import fr.mixit.android.ui.fragments.BoundServiceContract;
import fr.mixit.android.ui.fragments.LoginAndroidAccountsListFragment;
import fr.mixit.android.ui.fragments.LoginAndroidAccountsListFragment.LoginAndroidAccountsContract;
import fr.mixit.android.ui.fragments.LoginOAuthFragment;
import fr.mixit.android.utils.UIUtils;

public class LoginAndroidAccountsListActivity extends GenericMixItActivity implements LoginAndroidAccountsContract, BoundServiceContract {
	
	LoginAndroidAccountsListFragment mLoginFrag;
	
	@Override
	protected void onCreate(Bundle bundle) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		super.onCreate(bundle);

		FragmentManager fm = getSupportFragmentManager();
		mLoginFrag = (LoginAndroidAccountsListFragment) fm.findFragmentByTag(LoginAndroidAccountsListFragment.TAG);
		if (mLoginFrag == null) {
			mLoginFrag = LoginAndroidAccountsListFragment.newInstance(getIntent());
			fm.beginTransaction().add(R.id.activity_login_android_account, mLoginFrag, LoginAndroidAccountsListFragment.TAG).commit();
		}
	}
	
	@Override
	protected int getContentLayoutId() {
		return R.layout.activity_login_android_accounts_list;
	}

	@Override
	public void loginSuccessful(String oauthLogin, int provider) {
		setResult(RESULT_OK);
		finish();
	}
	
	@Override
	public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
		String action = intent.getAction();
		if (LoginOAuthFragment.ACTION_LOGIN_OAUTH.equals(action)) {
			if (UIUtils.isTablet(this)) {
				// TODO : show LoginMixItFragment in dialog mode for tablet
			} else {
				super.startActivityFromFragment(fragment, intent, requestCode);
			}
			return;
		}
		
		super.startActivityFromFragment(fragment, intent, requestCode);
	}

	public Intent getParentIntent() {
		return new Intent(this, LoginMixItActivity.class);
	}

	public Intent getGrandParentIntent() {
		return new Intent(this, AccountActivity.class);
	}

	public Intent getGreatGrandParentIntent() {
		return new Intent(this, HomeActivity.class);
	}

	@Override
	public void setRefreshMode(boolean state) {
        setSupportProgressBarIndeterminateVisibility(state);
	}

}
