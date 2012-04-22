
 package fr.mixit.android.ui;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import fr.mixit.android.R;
import fr.mixit.android.utils.UIUtils;
import android.content.Intent;
import android.os.Bundle;

public class HomeActivity extends GenericMixItActivity {
	
	@Override
	protected void onCreate(Bundle savedStateInstance) {
		super.onCreate(savedStateInstance);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
	}
	
	@Override
	protected int getContentLayoutId() {
		return R.layout.activity_home;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.home, menu);
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
				// TODO : display the about in a dialog
			} else {
				startActivity(new Intent(this, AboutActivity.class));
			}
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}
