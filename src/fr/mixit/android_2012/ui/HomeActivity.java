
 package fr.mixit.android_2012.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.mixit.android_2012.MixItApplication;
import fr.mixit.android_2012.R;
import fr.mixit.android_2012.ui.fragments.AboutFragment;
import fr.mixit.android_2012.utils.UIUtils;

public class HomeActivity extends GenericMixItActivity {
	
	@Override
	protected void onCreate(Bundle savedStateInstance) {
		super.onCreate(savedStateInstance);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
	}
	
	@Override
	protected int getContentLayoutId() {
		return MixItApplication.FORCE_OFFLINE ? R.layout.activity_home_offline : R.layout.activity_home;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(MixItApplication.FORCE_OFFLINE ? R.menu.home_offline : R.menu.home, menu);
//		super.onCreateOptionsMenu(menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_item_account) {
			startActivity(new Intent(this, AccountActivity.class));
			return true;
		} else if (itemId == R.id.menu_item_about) {
			if (UIUtils.isTablet(this)) {
				FragmentManager fm = getSupportFragmentManager();
				AboutFragment aboutFrag = (AboutFragment) fm.findFragmentByTag(AboutFragment.TAG);
				if (aboutFrag == null) {
					aboutFrag = AboutFragment.newInstance(getIntent());
				}
			    aboutFrag.show(fm, AboutFragment.TAG);
			} else {
				startActivity(new Intent(this, AboutActivity.class));
			}
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}
