package fr.mixit.android.ui.fragments;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.GoogleApi;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import fr.mixit.android.MixItApplication;
import fr.mixit.android.R;
import fr.mixit.android.model.OAuth;
import fr.mixit.android.utils.IntentUtils;
import fr.mixit.android.utils.UIUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewAnimator;

public class LoginOAuthFragment extends BoundServiceFragment {
	
	static final boolean DEBUG_MODE = MixItApplication.DEBUG_MODE;
	public static final String TAG = LoginOAuthFragment.class.getSimpleName();
	
	public static final String ACTION_LOGIN_OAUTH = "fr.mixit.android.ACTION_LOGIN_OAUTH";
	
	static final String CONSUMER_KEY_GOOGLE = "geekweavers.net";
	static final String CONSUMER_SECRET_GOOGLE = "dkYI9wmZHKaqlSu7mAj75MPF";
	static final String CONSUMER_KEY_TWITTER = "MZOi3DxRcdQVPSIVKmQQA";
	static final String CONSUMER_SECRET_TWITTER = "UH6dzqQ9osUN2XBUlhY3n9KPruSMVS6CmCM3lpMFHiI";
	static final String SCOPE_GOOGLE = "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";
	static final String CALLBACK_GOOGLE = "oauth://google";
	static final String CALLBACK_TWITTER = "oauth://twitter";
	static final String USER_PROFILE_JSON_URL_GOOGLE = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json";
	static final String USER_PROFILE_JSON_URL_TWITTER = "http://api.twitter.com/1/account/verify_credentials.json";
	// TokenUrl=https://api.twitter.com/oauth/request_token
	// Twitter.accessTokenUrl=https://api.twitter.com/oauth/access_token
	// Twitter.authorizeUrl=https://api.twitter.com/oauth/authenticate
	// Twitter.consumerKey=MZOi3DxRcdQVPSIVKmQQA
	// Twitter.consumerSecret=UH6dzqQ9osUN2XBUlhY3n9KPruSMVS6CmCM3lpMFHiI
	// Twitter.userProfileJsonUrl=http://api.twitter.com/1/account/verify_credentials.json
	// static final String REQUEST_URL = "https://www.google.com/accounts/OAuthGetRequestToken";
	// static final String ACCESS_URL = "https://www.google.com/accounts/OAuthGetAccessToken";
	// static final String AUTHORIZE_URL = "https://www.google.com/accounts/OAuthAuthorizeToken";
	// Google.userProfileJsonUrl=https://www.googleapis.com/oauth2/v1/userinfo?alt=json

	ViewAnimator mViewAnimator;
	ProgressBar mProgressBar;
	WebView mWebView;

	int mProvider = OAuth.ACCOUNT_TYPE_NO;
	
	public interface LoginOAuthContract {
		public void loginSuccessful(String oauthLogin);
		
		public void loginFailed();
	}

	public static LoginOAuthFragment newInstance(Intent intent) {
		LoginOAuthFragment f = new LoginOAuthFragment();
		f.setArguments(UIUtils.intentToFragmentArguments(intent));
		return f;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			LoginOAuthContract contract = (LoginOAuthContract) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement LoginOAuthContract");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.fragment_login_oauth, container, false);
		mViewAnimator = (ViewAnimator) v.findViewById(R.id.login_oauth_view_animator);
		mProgressBar = (ProgressBar) v.findViewById(R.id.login_oauth_progress_bar);
		mWebView = (WebView) v.findViewById(R.id.login_oauth_webview);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mProvider = getArguments().getInt(IntentUtils.EXTRA_PROVIDER, OAuth.ACCOUNT_TYPE_NO);

		if (mProvider != OAuth.ACCOUNT_TYPE_NO) {
			new OAuthInitTask(mProvider, this).execute();
		}
	}
	
	static OAuthService getOAuthService(int accountType) {
		final ServiceBuilder serviceBuilder = new ServiceBuilder();
		switch (accountType) {
		case OAuth.ACCOUNT_TYPE_GOOGLE:
			serviceBuilder.provider(GoogleApi.class).apiKey(CONSUMER_KEY_GOOGLE).apiSecret(CONSUMER_SECRET_GOOGLE).scope(SCOPE_GOOGLE)
					.callback(CALLBACK_GOOGLE);
			break;
		case OAuth.ACCOUNT_TYPE_TWITTER:
			serviceBuilder.provider(TwitterApi.class).apiKey(CONSUMER_KEY_TWITTER).apiSecret(CONSUMER_SECRET_TWITTER).callback(CALLBACK_TWITTER);
			break;

		default:
			return null;
		}

		OAuthService oauthService = serviceBuilder.build();
		
		return oauthService;
	}

	void loadUrl(final Token requestToken, String url) {
		if (url != null) {
			mViewAnimator.showNext();
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.setWebViewClient(new WebViewClient() {
	
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
	
					// check for our custom callback protocol otherwise use default behavior
					if (url.startsWith("oauth")) {
						// authorization complete hide webview for now.
						Uri uri = Uri.parse(url);
						String verifier = uri.getQueryParameter("oauth_verifier");
						Verifier v = new Verifier(verifier);// TODO : change toast message to nothing
						if (uri.getHost().equals("google")) {
							Toast.makeText(LoginOAuthFragment.this.getActivity(), "It work's Google", Toast.LENGTH_LONG).show();
						} else if (uri.getHost().equals("twitter")) {
							Toast.makeText(LoginOAuthFragment.this.getActivity(), "It work's Twitter", Toast.LENGTH_LONG).show();
						}

						mViewAnimator.showPrevious();
						new GetOAuthLogin(requestToken, v, mProvider, LoginOAuthFragment.this).execute();
						
						return true;
					}
	
					return super.shouldOverrideUrlLoading(view, url);
				}
			});
			mWebView.loadUrl(url);
		} else {
			// TODO : print an error message
		}
	}

	// TODO extract async tasks to manage orientation screen correctly
	private static class OAuthInitTask extends AsyncTask<Void, Void, String> {

		static final boolean DEBUG_MODE = LoginOAuthFragment.DEBUG_MODE;
		static final String TAG = OAuthInitTask.class.getSimpleName();

		int accountType;
		LoginOAuthFragment fragment;
		Token requestToken;
		
		public OAuthInitTask(int type, LoginOAuthFragment frag) {
			super();
			accountType = type;
			fragment = frag;
		}

		@Override
		protected String doInBackground(Void... params) {
			OAuthService oauthService = LoginOAuthFragment.getOAuthService(accountType);
			requestToken = oauthService.getRequestToken();
			final String authURL = oauthService.getAuthorizationUrl(requestToken);
			if (DEBUG_MODE) {
				Log.d(TAG, "request token:" + requestToken);
				Log.d(TAG, "auth url:" + authURL);
			}

			return authURL;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			fragment.loadUrl(requestToken, result);
		}
	}
	
	private static class GetOAuthLogin extends AsyncTask<Void, Void, String> {

		Token token;
		Verifier verifier;
		int accountType;
		LoginOAuthFragment fragment;
		
		GetOAuthLogin(Token requestToken, Verifier v, int type, LoginOAuthFragment frag) {
			super();
			
			token = requestToken;
			verifier = v;
			accountType = type;
			fragment = frag;
		}
		
		@Override
		protected String doInBackground(Void... params) {
			OAuthService oauthService = LoginOAuthFragment.getOAuthService(accountType);
			Token accessToken = oauthService.getAccessToken(token, verifier);
			Log.d("OAUTH", "access token:" + accessToken);
			
			String token = accessToken.getToken();
			String secret = accessToken.getSecret();
			Log.d("RequestTokenActivity", "verifier:" + verifier + " token:" + token + " secret:" + secret);

			OAuthRequest o = null;
			switch (accountType) {
			case OAuth.ACCOUNT_TYPE_GOOGLE:
				o = new OAuthRequest(Verb.GET, USER_PROFILE_JSON_URL_GOOGLE);
				break;

			case OAuth.ACCOUNT_TYPE_TWITTER:
				o = new OAuthRequest(Verb.GET, USER_PROFILE_JSON_URL_TWITTER);
				break;
				
			default:
				break;
			} 
			oauthService.signRequest(new Token(token, secret), o);

		    Response res = o.send();
	        Log.d("TAG", "code:" + res.getCode() + " body:" + res.getBody());
	           
	        String jsontext = res.getBody();
	        try {
				JSONObject value = new JSONObject(jsontext);
				String str = null;
				switch (accountType) {
				case OAuth.ACCOUNT_TYPE_GOOGLE:
					str = value.getString("email");
//					int index = str.indexOf("@");
//					str = str.substring(0, index);
					Log.d("email", str);
					break;

				case OAuth.ACCOUNT_TYPE_TWITTER:
					str = value.getString("name");
//					int index = str.indexOf("@");
//					str = str.substring(0, index);
					Log.d("name", str);
					break;

				default:
					break;
				}
				return str;
	        } catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if (fragment != null && fragment.getActivity() != null && !fragment.isDetached()) {
				if (result != null) {
					((LoginOAuthContract) fragment.getActivity()).loginSuccessful(result);
				} else {
					Toast.makeText(fragment.getActivity(), fragment.getString(R.string.login_oauth_failed, OAuth.getProviderString(accountType)), Toast.LENGTH_LONG).show();
					((LoginOAuthContract) fragment.getActivity()).loginFailed();
				}
			}
		}
	}

	@Override
	protected void onMessageReceivedFromService(Message msg) {
		// TODO Auto-generated method stub

	}

}
