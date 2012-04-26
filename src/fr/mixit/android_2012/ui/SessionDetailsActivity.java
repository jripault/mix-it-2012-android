package fr.mixit.android_2012.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.view.Window;

import fr.mixit.android_2012.R;
import fr.mixit.android_2012.ui.fragments.BoundServiceContract;
import fr.mixit.android_2012.ui.fragments.SessionDetailsFragment;
import fr.mixit.android_2012.ui.fragments.SessionDetailsFragment.SessionDetailsContract;

public class SessionDetailsActivity extends GenericMixItActivity implements SessionDetailsContract, BoundServiceContract {

	SessionDetailsFragment sessionDetailsFrag;

	@Override
	protected void onCreate(Bundle savedStateInstance) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		super.onCreate(savedStateInstance);

        setRefreshMode(false);
		FragmentManager fm = getSupportFragmentManager();
		sessionDetailsFrag = (SessionDetailsFragment) fm.findFragmentByTag(SessionDetailsFragment.TAG);
		if (sessionDetailsFrag == null) {
			sessionDetailsFrag = SessionDetailsFragment.newInstance(getIntent());
			fm.beginTransaction().add(R.id.content_root, sessionDetailsFrag, SessionDetailsFragment.TAG).commit();
		}
	}

	@Override
	public void refreshMenu() {
		invalidateOptionsMenu();
	}

	@Override
	public void refreshList() {
		// Nothing to do because there is no list to refresh in this activity
	}

	public Intent getParentIntent() {
		return new Intent(this, SessionsActivity.class);
	}

	public Intent getGrandParentIntent() {
		return new Intent(this, HomeActivity.class);
	}

	public Intent getGreatGrandParentIntent() {
		return null;
	}

	@Override
	public void setRefreshMode(boolean state) {
        setSupportProgressBarIndeterminateVisibility(state);
	}

}
