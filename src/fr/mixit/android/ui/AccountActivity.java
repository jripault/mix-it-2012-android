package fr.mixit.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import fr.mixit.android.R;
import fr.mixit.android.ui.fragments.AccountFragment;
import fr.mixit.android.ui.fragments.LoginMixItFragment;
import fr.mixit.android.utils.UIUtils;

public class AccountActivity extends GenericMixItActivity {
	
	AccountFragment mAccountFrag;
	LoginMixItFragment mLoginMixItFragment;

	@Override
	protected void onCreate(Bundle savedStateInstance) {
		super.onCreate(savedStateInstance);

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
	
}
