package fr.mixit.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import fr.mixit.android.R;
import fr.mixit.android.ui.fragments.AboutFragment;
import fr.mixit.android.ui.fragments.AccountFragment;
import fr.mixit.android.ui.fragments.LoginMixItFragment;
import fr.mixit.android.utils.UIUtils;

public class AboutActivity extends GenericMixItActivity {
	
	AboutFragment mAboutFrag;

	@Override
	protected void onCreate(Bundle savedStateInstance) {
		super.onCreate(savedStateInstance);

		FragmentManager fm = getSupportFragmentManager();
		mAboutFrag = (AboutFragment) fm.findFragmentByTag(AboutFragment.TAG);
		if (mAboutFrag == null) {
			mAboutFrag = AboutFragment.newInstance(getIntent());
			fm.beginTransaction().add(R.id.content_about, mAboutFrag, AboutFragment.TAG).commit();
		}
	}

	@Override
	protected int getContentLayoutId() {
		return R.layout.activity_about;
	}
	
}
