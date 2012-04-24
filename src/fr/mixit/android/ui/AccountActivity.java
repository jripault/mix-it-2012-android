package fr.mixit.android.ui;

import com.actionbarsherlock.view.Window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import fr.mixit.android.R;
import fr.mixit.android.ui.fragments.AccountFragment;
import fr.mixit.android.ui.fragments.BoundServiceContract;
import fr.mixit.android.ui.fragments.LoginMixItFragment;
import fr.mixit.android.utils.UIUtils;

public class AccountActivity extends GenericMixItActivity implements BoundServiceContract {
	
	AccountFragment mAccountFrag;
	LoginMixItFragment mLoginMixItFragment;

	@Override
	protected void onCreate(Bundle savedStateInstance) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		super.onCreate(savedStateInstance);
		
		setRefreshMode(false);
		FragmentManager fm = getSupportFragmentManager();
		mAccountFrag = (AccountFragment) fm.findFragmentByTag(AccountFragment.TAG);
		if (mAccountFrag == null) {
			mAccountFrag = AccountFragment.newInstance(getIntent());
			fm.beginTransaction().add(R.id.content_account, mAccountFrag, AccountFragment.TAG).commit();
		}
	}

	@Override
	protected int getContentLayoutId() {
		return R.layout.activity_account;
	}
	
	@Override
	public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
		String action = intent.getAction();
		if (LoginMixItFragment.ACTION_LOGIN_MIXIT.equals(action)) {
			// TODO : show LoginMixItFragment in dialog mode for tablet and in full screen in smartphone mode
			if (UIUtils.isTablet(this)) {
				
			} else {
				super.startActivityFromFragment(fragment, intent, requestCode);
			}
			return;
		}
		
		super.startActivityFromFragment(fragment, intent, requestCode);
	}

	@Override
	public void setRefreshMode(boolean state) {
        setSupportProgressBarIndeterminateVisibility(state);
	}
	
}
