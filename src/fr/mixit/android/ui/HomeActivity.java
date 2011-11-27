package fr.mixit.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import fr.mixit.android.R;
import fr.mixit.android.service.SyncService;
import fr.mixit.android.utils.DetachableResultReceiver;

public class HomeActivity extends BaseActivity {
    
	private ActivityStreamFragment mActivityStreamFragment;
    private SyncStatusUpdaterFragment mSyncStatusUpdaterFragment;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FragmentManager fm = getSupportFragmentManager();

        mActivityStreamFragment = (ActivityStreamFragment) fm.findFragmentById(R.id.fragment_stream);

        mSyncStatusUpdaterFragment = (SyncStatusUpdaterFragment) fm.findFragmentByTag(SyncStatusUpdaterFragment.TAG);
        if (mSyncStatusUpdaterFragment == null) {
            mSyncStatusUpdaterFragment = new SyncStatusUpdaterFragment();
            fm.beginTransaction().add(mSyncStatusUpdaterFragment, SyncStatusUpdaterFragment.TAG).commit();

            triggerRefresh();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                triggerRefresh();
                return true;
/*            	// TODO : managing menu refresh button
                Toast.makeText(this, "Fake refreshing...", Toast.LENGTH_SHORT).show();
                getActionBarHelper().setRefreshActionItemState(true);
                getWindow().getDecorView().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                getActionBarHelper().setRefreshActionItemState(false);
                            }
                        }, 1000);
                break;*/

            case R.id.menu_search:
            	// TODO : managing menu search button
                Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_user:
            	// TODO : managing menu profile button
                Toast.makeText(this, "Tapped user profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_sponsor:
            	// TODO : managing menu sponsors button
                Toast.makeText(this, "Tapped sponsors", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_participants:
            	// TODO : managing menu participants button
                Toast.makeText(this, "Tapped participant Mix-it", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_about:
            	// TODO : managing menu about button
                Toast.makeText(this, "Tapped about", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_settings:
            	// TODO : managing menu settings button
                Toast.makeText(this, "Tapped settings", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void triggerRefresh() {
        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SyncService.class);
        intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER, mSyncStatusUpdaterFragment.mReceiver);
        startService(intent);

        if (mActivityStreamFragment != null) {
        	mActivityStreamFragment.refresh();
        }
    }

    private void updateRefreshStatus(boolean refreshing) {
    	getActionBarHelper().setRefreshActionItemState(refreshing);
    }

    /**
     * A non-UI fragment, retained across configuration changes, that updates its activity's UI
     * when sync status changes.
     */
    public static class SyncStatusUpdaterFragment extends Fragment implements DetachableResultReceiver.Receiver {
        public static final String TAG = SyncStatusUpdaterFragment.class.getName();

        private boolean mSyncing = false;
        private DetachableResultReceiver mReceiver;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            mReceiver = new DetachableResultReceiver(new Handler());
            mReceiver.setReceiver(this);
        }

        /** {@inheritDoc} */
        public void onReceiveResult(int resultCode, Bundle resultData) {
            HomeActivity activity = (HomeActivity) getActivity();
            if (activity == null) {
                return;
            }

            switch (resultCode) {
                case SyncService.STATUS_RUNNING: {
                    mSyncing = true;
                    break;
                }
                case SyncService.STATUS_FINISHED: {
                    mSyncing = false;
                    break;
                }
                case SyncService.STATUS_ERROR: {
                    // Error happened down in SyncService, show as toast.
                    mSyncing = false;
                    final String errorText = getString(R.string.toast_sync_error, resultData.getString(Intent.EXTRA_TEXT));
                    Toast.makeText(activity, errorText, Toast.LENGTH_LONG).show();
                    break;
                }
            }

            activity.updateRefreshStatus(mSyncing);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            ((HomeActivity) getActivity()).updateRefreshStatus(mSyncing);
        }
    }

}