package fr.mixit.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import fr.mixit.android.R;
import fr.mixit.android.ui.fragments.SessionDetailsFragment;
import fr.mixit.android.ui.fragments.SessionDetailsFragment.SessionDetailsContract;

public class SessionDetailsActivity extends GenericMixItActivity implements SessionDetailsContract {

	SessionDetailsFragment sessionDetailsFrag;

	@Override
	protected void onCreate(Bundle savedStateInstance) {
		super.onCreate(savedStateInstance);

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

}
