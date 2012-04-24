package fr.mixit.android.ui;

import com.actionbarsherlock.view.Window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import fr.mixit.android.R;
import fr.mixit.android.ui.fragments.BoundServiceContract;
import fr.mixit.android.ui.fragments.MemberDetailsFragment;
import fr.mixit.android.utils.UIUtils;

public class MemberDetailsActivity extends GenericMixItActivity implements /*MemberDetailsContract,*/ BoundServiceContract {
	
	static final String TAG = MemberDetailsActivity.class.getSimpleName();
	
	MemberDetailsFragment memberDetailsFrag;
	
	@Override
	protected void onCreate(Bundle savedStateInstance) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		super.onCreate(savedStateInstance);
		
        setRefreshMode(false);
		FragmentManager fm = getSupportFragmentManager();
		memberDetailsFrag = (MemberDetailsFragment) fm.findFragmentByTag(MemberDetailsFragment.TAG);
		if (memberDetailsFrag == null) {
			memberDetailsFrag = MemberDetailsFragment.newInstance(getIntent());
			fm.beginTransaction().add(R.id.content_root, memberDetailsFrag, MemberDetailsFragment.TAG).commit();
		}
	}

    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, 
            int requestCode) {
    	if (UIUtils.isTablet(this)) {
			Log.e(TAG, "How are we here ?");
//			if (memberDetailsFrag != null) {
//				Uri memberUri = intent.getData();
//				int memberId;
//				if (memberUri != null) {
//					memberId = Integer.parseInt(MixItContract.Members.getMemberId(memberUri));
//					memberDetailsFrag.setMemberId(memberId);
//				} else {
//					Log.e(TAG, "no uri found");
//				}
//			} else {
//				Log.e(TAG, "no fragment member details found but device is tablet");
//			}
    	} else {
    		super.startActivityFromFragment(fragment, intent, requestCode);
    	}
    }

	public Intent getParentIntent() {
		return new Intent(this, MembersActivity.class);
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
