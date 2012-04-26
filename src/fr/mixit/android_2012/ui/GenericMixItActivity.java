package fr.mixit.android_2012.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import fr.mixit.android_2012.R;

public class GenericMixItActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedStateInstance) {
		super.onCreate(savedStateInstance);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(getContentLayoutId());
	}

	protected int getContentLayoutId() {
		return R.layout.activity_generic;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent upIntent = getParentIntent();
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				// This activity is not part of the application's task, so create a new task
				// with a synthesized back stack.
				TaskStackBuilder builder = TaskStackBuilder.from(this);
				Intent greatGrandParentIntent = getGreatGrandParentIntent();
				if (greatGrandParentIntent != null) {
					builder.addNextIntent(greatGrandParentIntent);
				}
				Intent grandParentIntent = getGrandParentIntent();
				if (grandParentIntent != null) {
					builder.addNextIntent(grandParentIntent);
				}
				builder.addNextIntent(upIntent).startActivities();
				finish();
			} else {
				// This activity is part of the application's task, so simply
				// navigate up to the hierarchical parent activity.
				NavUtils.navigateUpTo(this, upIntent);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
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

}
