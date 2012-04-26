package fr.mixit.android_2012.ui.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import fr.mixit.android_2012.MixItApplication;
import fr.mixit.android_2012.R;
import fr.mixit.android_2012.model.OAuth;
import fr.mixit.android_2012.services.MixItService;
import fr.mixit.android_2012.ui.adapters.AccountAdapter;
import fr.mixit.android_2012.utils.IntentUtils;
import fr.mixit.android_2012.utils.UIUtils;

public class LoginAndroidAccountsListFragment extends BoundServiceFragment implements OnItemClickListener, OnClickListener {

	static final boolean DEBUG_MODE = MixItApplication.DEBUG_MODE;
	public static final String TAG = LoginAndroidAccountsListFragment.class.getSimpleName();

	public static final String ACTION_ANDROID_ACCOUNTS = "fr.mixit.android.ACTION_LOGIN_ANDROID_ACCOUNTS";

	AccountManager mAccountManager;

	ListView lv;
	AccountAdapter adapter;

	int mProvider = OAuth.ACCOUNT_TYPE_NO;

	public interface LoginAndroidAccountsContract {
		public void loginSuccessful(String oauthLogin, int provider);
	}

	public static LoginAndroidAccountsListFragment newInstance(Intent intent) {
		LoginAndroidAccountsListFragment f = new LoginAndroidAccountsListFragment();
		f.setArguments(UIUtils.intentToFragmentArguments(intent));
		return f;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			LoginAndroidAccountsContract contract = (LoginAndroidAccountsContract) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement LoginAndroidAccountsContract");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.fragment_login_android_accounts_list, container, false);
		lv = (ListView) v.findViewById(android.R.id.list);
		lv.setOnItemClickListener(this);
		v.findViewById(R.id.other_account_bt).setOnClickListener(this);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAccountManager = AccountManager.get(getActivity());
		Account[] accounts = null;
		mProvider = getArguments().getInt(IntentUtils.EXTRA_PROVIDER, OAuth.ACCOUNT_TYPE_NO);
		switch (mProvider) {
		case OAuth.ACCOUNT_TYPE_GOOGLE:
			accounts = mAccountManager.getAccountsByType("com.google");
			break;
		case OAuth.ACCOUNT_TYPE_TWITTER:
			accounts = mAccountManager.getAccountsByType("com.twitter.android.auth.login");
			break;

		default:
			break;
		}
		if (accounts != null && accounts.length == 0) {
			startLoginOAuthOnWeb();
		}
		adapter = new AccountAdapter(accounts, getActivity());
		lv.setAdapter(adapter);
		
		setRefreshMode(false);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Account account = (Account) lv.getItemAtPosition(position);
		if (DEBUG_MODE) {
			Log.d(TAG, "selected account name : " + account.name + " and of type : " + account.type);
		}
		loginOAuth(account.name);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (id == R.id.other_account_bt) {
			startLoginOAuthOnWeb();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == IntentUtils.REQUEST_CODE_LOGIN_OAUTH_FROM_ANDROID_ACCOUNT) {
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					Bundle b = data.getExtras();
					String oauthLogin = b.getString(IntentUtils.EXTRA_OAUTH_LOGIN);
					if (!TextUtils.isEmpty(oauthLogin)) {
						loginOAuth(oauthLogin);
					} else {
						// TODO : print error message or send failed message to activity
					}
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// Nothing to do because error message is already displayed
			}
		}
	}

	void startLoginOAuthOnWeb() {
		Intent i = new Intent(LoginOAuthFragment.ACTION_LOGIN_OAUTH);
		i.putExtra(IntentUtils.EXTRA_PROVIDER, mProvider);
		startActivityForResult(i, IntentUtils.REQUEST_CODE_LOGIN_OAUTH_FROM_ANDROID_ACCOUNT);
	}

	void loginOAuth(String login) {
		if (isBound && serviceReady) {
			Message msg = Message.obtain(null, MixItService.MSG_LOGIN_OAUTH, 0, 0);
			msg.replyTo = messenger;
			Bundle b = new Bundle();
			b.putInt(MixItService.EXTRA_LOGIN_OAUTH_PROVIDER, mProvider);
			b.putString(MixItService.EXTRA_LOGIN_OAUTH_LOGIN, login);
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
		if (msg.what == MixItService.MSG_LOGIN_OAUTH) {
			if (getActivity() != null && !isDetached()) {
				switch (msg.arg1) {
				case MixItService.Response.STATUS_OK:
					Bundle b = msg.getData();
					int provider = b.getInt(MixItService.EXTRA_LOGIN_OAUTH_PROVIDER, OAuth.ACCOUNT_TYPE_NO);
					String oauthLogin = b.getString(MixItService.EXTRA_LOGIN_OAUTH_LOGIN);
					Toast.makeText(getActivity(), getString(R.string.android_accounts_login_successful, OAuth.getProviderString(provider), oauthLogin),
							Toast.LENGTH_LONG).show();
					((LoginAndroidAccountsContract) getActivity()).loginSuccessful(oauthLogin, provider);
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
