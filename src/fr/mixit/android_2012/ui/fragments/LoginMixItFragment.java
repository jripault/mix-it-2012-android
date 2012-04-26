package fr.mixit.android_2012.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import fr.mixit.android_2012.R;
import fr.mixit.android_2012.model.OAuth;
import fr.mixit.android_2012.services.MixItService;
import fr.mixit.android_2012.utils.IntentUtils;
import fr.mixit.android_2012.utils.UIUtils;

public class LoginMixItFragment extends BoundServiceFragment implements OnClickListener {
	
	public static final String ACTION_LOGIN_MIXIT = "fr.mixit.android.ACTION_LOGIN_MIXIT";

	public static final String TAG = LoginMixItFragment.class.getSimpleName();
	
	public interface LoginMixItContract {
		public void loginSuccessful();
		
		public void loginFailed();
	}

	public static LoginMixItFragment newInstance(Intent intent) {
		LoginMixItFragment f = new LoginMixItFragment();
		f.setArguments(UIUtils.intentToFragmentArguments(intent));
		return f;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			LoginMixItContract contract = (LoginMixItContract) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement LoginMixItContract");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.fragment_login_mixit, container, false);
		v.findViewById(R.id.login_bt).setOnClickListener(this);
		v.findViewById(R.id.login_google_bt).setOnClickListener(this);
		v.findViewById(R.id.login_twitter_bt).setOnClickListener(this);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setRefreshMode(false);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (id == R.id.login_bt) {
			// TODO : use login and pass from EditTexts
			login("mathieu", "mathieu");
		} else {
			Intent i = new Intent(LoginAndroidAccountsListFragment.ACTION_ANDROID_ACCOUNTS);
			if (id == R.id.login_google_bt) {
				i.putExtra(IntentUtils.EXTRA_PROVIDER, OAuth.ACCOUNT_TYPE_GOOGLE);
			} else if (id == R.id.login_twitter_bt) {
				i.putExtra(IntentUtils.EXTRA_PROVIDER, OAuth.ACCOUNT_TYPE_TWITTER);
			}
			startActivityForResult(i, IntentUtils.REQUEST_CODE_LOGIN_ANDROID_ACCOUNTS);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == IntentUtils.REQUEST_CODE_LOGIN_ANDROID_ACCOUNTS) {
			if (getActivity() != null && !isDetached()) {
				if (resultCode == Activity.RESULT_OK) {
					((LoginMixItContract) getActivity()).loginSuccessful();
				} else {
					((LoginMixItContract) getActivity()).loginFailed();
				}
			}
		}
	}
	
	void login(String login, String pass) {
		if (isBound && serviceReady) {
			Message msg = Message.obtain(null, MixItService.MSG_LOGIN, 0, 0);
			msg.replyTo = messenger;
			Bundle b = new Bundle();
			b.putString(MixItService.EXTRA_LOGIN_LOGIN, login);
			b.putString(MixItService.EXTRA_LOGIN_PASSWORD, pass);
			msg.setData(b);
			try {
				service.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onMessageReceivedFromService(Message msg) {
		if (msg.what == MixItService.MSG_VOTE_LIGHTNING_TALK) {
			if (getActivity() != null && !isDetached()) {
				switch (msg.arg1) {
				case MixItService.Response.STATUS_OK:
					Toast.makeText(getActivity(), R.string.login_successful, Toast.LENGTH_SHORT).show();
					((LoginMixItContract) getActivity()).loginSuccessful();
					break;
	
				case MixItService.Response.STATUS_ERROR:
					Toast.makeText(getActivity(), R.string.error_login, Toast.LENGTH_SHORT).show();
					break;
	
				case MixItService.Response.STATUS_NO_CONNECTIVITY:
					Toast.makeText(getActivity(), R.string.functionnality_need_connectivity, Toast.LENGTH_SHORT).show();
					break;
	
				default:
					break;
				}
			}
		}
	}

}
