package fr.mixit.android_2012.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.view.Window;

import fr.mixit.android_2012.R;
import fr.mixit.android_2012.model.OAuth;
import fr.mixit.android_2012.ui.fragments.BoundServiceContract;
import fr.mixit.android_2012.ui.fragments.LoginOAuthFragment;
import fr.mixit.android_2012.ui.fragments.LoginOAuthFragment.LoginOAuthContract;
import fr.mixit.android_2012.utils.IntentUtils;

public class LoginOAuthActivity extends GenericMixItActivity implements LoginOAuthContract, BoundServiceContract {
	
	LoginOAuthFragment mLoginFrag;
	int mProvider;

	@Override
	protected void onCreate(Bundle bundle) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		super.onCreate(bundle);

        setRefreshMode(false);
		FragmentManager fm = getSupportFragmentManager();
		mLoginFrag = (LoginOAuthFragment) fm.findFragmentByTag(LoginOAuthFragment.TAG);
		if (mLoginFrag == null) {
			mLoginFrag = LoginOAuthFragment.newInstance(getIntent());
			fm.beginTransaction().add(R.id.activity_login_oauth, mLoginFrag).commit();
		}
		mProvider = getIntent().getIntExtra(IntentUtils.EXTRA_PROVIDER, OAuth.ACCOUNT_TYPE_NO);
	}
	
	@Override
	protected int getContentLayoutId() {
		return R.layout.activity_login_oauth;
	}

	@Override
	public void loginSuccessful(String oauthLogin) {
		Intent data = new Intent();
		data.putExtra(IntentUtils.EXTRA_OAUTH_LOGIN, oauthLogin);
		setResult(RESULT_OK, data);
		finish();
	}

	@Override
	public void loginFailed() {
		setResult(RESULT_CANCELED);
		finish();
	}

	public Intent getParentIntent() {
		return new Intent(this, LoginAndroidAccountsListActivity.class);
	}

	public Intent getGrandParentIntent() {
		return new Intent(this, LoginMixItActivity.class);
	}

	public Intent getGreatGrandParentIntent() {
		return new Intent(this, AccountActivity.class);
	}

	@Override
	public void setRefreshMode(boolean state) {
        setSupportProgressBarIndeterminateVisibility(state);
	}
	
}
