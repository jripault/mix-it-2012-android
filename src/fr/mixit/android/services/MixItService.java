package fr.mixit.android.services;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Service;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import fr.mixit.android.MixItApplication;
import fr.mixit.android.R;
import fr.mixit.android.io.InterestsHandler;
import fr.mixit.android.io.JsonExecutor;
import fr.mixit.android.io.JsonHandler;
import fr.mixit.android.io.MembersHandler;
import fr.mixit.android.io.SessionsHandler;
import fr.mixit.android.model.OAuth;
import fr.mixit.android.provider.MixItContract;
import fr.mixit.android.utils.NetworkUtils;
import fr.mixit.android.utils.NetworkUtils.ConnectivityState;
import fr.mixit.android.utils.NetworkUtils.ResponseHttp;
import fr.mixit.android.utils.PrefUtils;

public class MixItService extends Service {

	static final boolean DEBUG_MODE = MixItApplication.DEBUG_MODE;
	static final String TAG = MixItService.class.getSimpleName();

	private volatile Looper serviceLooper;
	private volatile IncomingHandler handler;

	/** Keeps track of all current registered clients. */
	ArrayList<Messenger> clients = new ArrayList<Messenger>();

	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_INIT = 3;
	public static final int MSG_INTERESTS = 4;
	public static final int MSG_LOGIN = 5;
	public static final int MSG_LOGIN_OAUTH = 6;
	public static final int MSG_TALKS = 7;
	public static final int MSG_TALK = 8;
	public static final int MSG_TALK_ADD_TO_MY_PLANNING = 9;
	public static final int MSG_TALK_REMOVE_FROM_MY_PLANNING = 10;
	public static final int MSG_TALK_COMMENT = 11;
	public static final int MSG_LIGHTNING_TALKS = 12;
	public static final int MSG_LIGHTNING_TALK = 13;
	public static final int MSG_LIGHTNING_TALK_VOTE = 14;
	public static final int MSG_MEMBERS = 15;
	public static final int MSG_MEMBER = 16;
	public static final int MSG_MEMBER_LINK = 17;
	public static final int MSG_MEMBER_UNLINK = 18;
	public static final int MSG_MEMBER_ACTIVITIES = 19;
	public static final int MSG_PLANNING = 20;
	public static final int MSG_MY_PLANNING = 21;
	public static final int MSG_SUGGESTIONS_SESSIONS = 22;
	public static final int MSG_SUGGESTIONS_MEMBERS = 23;
	public static final int MSG_ACTIVITIES = 24;
	public static final int MSG_MY_ACTIVITIES = 25;
	public static final int MSG_VOTE_LIGHTNING_TALK = 26;
	public static final int MSG_STAR_SESSION = 27;

	public static final String EXTRA_FORCE_REFRESH = "fr.mixit.android.services.MixItService.EXTRA_FORCE_REFRESH";
	public static final String EXTRA_ID = "fr.mixit.android.services.MixItService.EXTRA_ID";
	public static final String EXTRA_LOGIN_PASSWORD = "fr.mixit.android.services.MixItService.EXTRA_LOGIN_PASSWORD";
	public static final String EXTRA_LOGIN_LOGIN = "fr.mixit.android.services.MixItService.EXTRA_LOGIN_LOGIN";
	public static final String EXTRA_LOGIN_OAUTH_PROVIDER = "fr.mixit.android.services.MixItService.EXTRA_LOGIN_OAUTH_PROVIDER";
	public static final String EXTRA_LOGIN_OAUTH_LOGIN = "fr.mixit.android.services.MixItService.EXTRA_LOGIN_OAUTH_LOGIN";
	public static final String EXTRA_ERROR_MESSAGE = "fr.mixit.android.utils.DetachableResultReceiver.EXTRA_RECEIVER_ERROR_MESSAGE";
	public static final String EXTRA_STATE_VOTE = "fr.mixit.android.services.MixItService.EXTRA_STATE_VOTE";
	public static final String EXTRA_STATE_STAR = "fr.mixit.android.services.MixItService.EXTRA_STATE_STAR";
	public static final String EXTRA_SESSION_ID = "fr.mixit.android.services.MixItService.EXTRA_SESSION_ID";

	// URLs
	static final String MAIN_URL = "http://mixittest.mixit.cloudbees.net/api";//"http://82.226.66.148:9000/api";// "http://192.168.0.49:9000/api";// http://test.mix-it.fr/api
	static final String URL_LOGIN = MAIN_URL + "/login";
	static final String URL_LOGIN_LINKIT = URL_LOGIN + "/linkit";
	static final String URL_INTERESTS = MAIN_URL + "/interests";
	static final String URL_TALKS = MAIN_URL + "/sessions";
	static final String URL_TALK = MAIN_URL + "/sessions/%d";
	static final String URL_TALK_ADD_TO_MY_PLANNING = URL_TALK + "/addtomyplanning";
	static final String URL_TALK_REMOVE_FROM_MY_PLANNING = URL_TALK + "/removefrommyplanning";
	static final String URL_TALK_COMMENT = URL_TALK + "/comment";
	static final String URL_LIGHTNING_TALKS = MAIN_URL + "/lightnings";
	static final String URL_LIGHTNING_TALK = MAIN_URL + "/lightnings/%d";
	static final String URL_LIGHTNING_TALK_VOTE = URL_LIGHTNING_TALK + "/vote";
	static final String URL_MEMBERS = MAIN_URL + "/members";
	static final String URL_MEMBER = MAIN_URL + "/member/%d";
	static final String URL_MEMBER_LINK = URL_MEMBER + "/link";
	static final String URL_MEMBER_UNLINK = URL_MEMBER + "/unlink";
	static final String URL_MEMBER_ACTIVITIES = URL_MEMBER + "/activities";
	static final String URL_PLANNING = MAIN_URL + "/planning";
	static final String URL_MY_PLANNING = MAIN_URL + "/myplanning";
	static final String URL_SUGGESTIONS_SESSIONS = MAIN_URL + "/suggestions/sessions";
	static final String URL_SUGGESTIONS_MEMBERS = MAIN_URL + "/suggestions/membres";
	static final String URL_ACTIVITIES = MAIN_URL + "/activities";
	static final String URL_MY_ACTIVITIES = MAIN_URL + "/myactivities";

	// Params
	static final String PARAM_OAUTH_PROVIDER = "oauth_provider";
	static final String PARAM_OAUTH_LOGIN = "oauth_login";
	static final String PARAM_LOGIN = "login";
	static final String PARAM_PASSWORD = "password";
	static final String PARAM_VALUE = "value";
	static final String PARAM_START = "start";
	static final String PARAM_NUMBER = "number";

	class IncomingHandler extends Handler {

		public IncomingHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message callMsg) {
			Response r;
			switch (callMsg.what) {
			case MSG_REGISTER_CLIENT:
				addToClients(callMsg);
				break;
			case MSG_UNREGISTER_CLIENT:
				removeFromClients(callMsg);
				break;
			default:
				if (clients.contains(callMsg.replyTo)) {
					r = handleMixItMessage(callMsg.what, callMsg.arg1, callMsg.arg2, callMsg.getData());
					for (int i = clients.size() - 1; i >= 0; i--) {
						try {
							Message responseMsg = Message.obtain(null, callMsg.what, r.status, r.arg2);
							responseMsg.setData(r.bundle);
							clients.get(i).send(responseMsg);
						} catch (RemoteException e) {
							// The client is dead. Remove it from the list;
							// we are going through the list from back to front
							// so this is safe to do inside the loop.
							clients.remove(i);
						}
					}
					// int index = clients.indexOf(msg.replyTo);
					// if (index != -1) {
					// try {
					// Message m = Message.obtain(null, msg.what, r.status, r.arg2);
					// m.setData(r.bundle);
					// msg.replyTo.send(m);
					// } catch (RemoteException e) {
					// // The client is dead. Remove it from the list;
					// // we are going through the list from back to front
					// // so this is safe to do inside the loop.
					// clients.remove(index);
					// }
					// }
				} else {
					// we don't need to do anything because the object
					// requesting this call is no longer listening
				}
				break;
			}
		}

	}

	Messenger messenger;

	public static class Response {

		public static final int STATUS_OK = 1202091753;
		public static final int STATUS_NO_CONNECTIVITY = 1202091754;
		public static final int STATUS_ERROR_PARAMS = 1202091755;
		public static final int STATUS_ERROR = 1202091756;
		public static final int STATUS_NO_WS = 1202091757;

		int status = 0;
		int arg2 = 0;
		Bundle bundle;

		Response() {
			super();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		HandlerThread thread = new HandlerThread(TAG);
		thread.start();

		serviceLooper = thread.getLooper();
		handler = new IncomingHandler(serviceLooper);
		messenger = new Messenger(handler);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return messenger.getBinder();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public Response handleMixItMessage(int reqId, int arg1, int arg2, Bundle b) {
		if (DEBUG_MODE) {
			Log.d(TAG, "handleMessage: reqId=" + reqId + " and Bundle=" + b.toString());
		}
		Response r;
		switch (reqId) {
		case MSG_INIT:
			r = init(b);
			break;

		case MSG_INTERESTS:
			r = getInterests(b);
			break;
			
		case MSG_TALKS:
			r = getSessions(b);
			break;
			
		case MSG_TALK:
			r = getSession(b);
			break;
			
		case MSG_LIGHTNING_TALKS:
			r = getLightningTalks(b);
			break;
			
		case MSG_LIGHTNING_TALK:
			r = getLightningTalk(b);
			break;
			
		case MSG_MEMBERS:
			r = getMembers(b);
			break;
			
		case MSG_MEMBER:
			r = getMember(b);
			break;

		case MSG_LOGIN:
			r = login(b);
			break;

		case MSG_LOGIN_OAUTH:
			r = loginWithOAuth(b);
			break;

		case MSG_VOTE_LIGHTNING_TALK:
			r = voteForLightningTalk(b);
			break;

		case MSG_STAR_SESSION:
			r = starSession(b);
			break;

		default:
			r = new Response();
			r.status = Response.STATUS_NO_WS;
			break;
		}

		return r;
	}

	/**
	 * Add new client to the client list
	 * 
	 * @param newClient
	 *            client to add
	 */
	private void addToClients(Message newClient) {
		if (DEBUG_MODE) {
			Log.d(TAG, "addToClients:" + newClient.replyTo.hashCode());
		}
		clients.add(newClient.replyTo);
		Message m = Message.obtain(null, newClient.what, 0, 0);
		try {
			newClient.replyTo.send(m);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Remove old client from client list
	 * 
	 * @param oldClient
	 *            client to remove
	 */
	public void removeFromClients(Message oldClient) {
		if (DEBUG_MODE) {
			Log.d(TAG, "removeFromClients:" + oldClient.replyTo.hashCode());
		}
		clients.remove(oldClient);
		Message m = Message.obtain(null, oldClient.what, 0, 0);
		try {
			oldClient.replyTo.send(m);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	static final String ASSET_INTERESTS_JSON = "interests.json";
	static final String ASSET_MEMBERS_JSON = "members.json";
	static final String ASSET_SESSIONS_JSON = "sessions.json";
	static final String ASSET_LIGTHNING_TALKS_JSON = "lightnings.json";

	private Response init(Bundle b) {
		if (DEBUG_MODE) {
			Log.d(TAG, "init() with bundle=" + b);
		}
		Response r = new Response();

		JsonExecutor executor = new JsonExecutor(getContentResolver());

        final SharedPreferences syncServicePrefs = getSharedPreferences(PrefUtils.MIXITSCHED_SYNC, Context.MODE_PRIVATE);
        final int localVersion = syncServicePrefs.getInt(PrefUtils.LOCAL_VERSION, PrefUtils.VERSION_NONE);
        
		try {
			// if no data yet, we need to execute the local data
            final long startLocal = System.currentTimeMillis();
            final boolean localParse = localVersion < PrefUtils.VERSION_LOCAL;
    		if (DEBUG_MODE) {
    			Log.d(TAG, "found localVersion=" + localVersion + " and VERSION_LOCAL=" + PrefUtils.VERSION_LOCAL);
    		}
            if (localParse) {
				executor.execute(getApplicationContext(), ASSET_INTERESTS_JSON, new InterestsHandler());
				executor.execute(getApplicationContext(), ASSET_MEMBERS_JSON, new MembersHandler(true, true));
				executor.execute(getApplicationContext(), ASSET_SESSIONS_JSON, new SessionsHandler(true, true, true));
				executor.execute(getApplicationContext(), ASSET_LIGTHNING_TALKS_JSON, new SessionsHandler(false, true, true));

                // Save local parsed version
            	syncServicePrefs.edit().putInt(PrefUtils.LOCAL_VERSION, PrefUtils.VERSION_LOCAL).commit();
            }

    		if (DEBUG_MODE) {
    			Log.d(TAG, "local sync took " + (System.currentTimeMillis() - startLocal) + "ms");
    		}	
			// we ask if we need to update our data with the new data from net, if so, then update
            final long startRemote = System.currentTimeMillis();
            boolean performRemoteSync = performRemoteSync(/*mResolver, *//*mHttpClient, */b, this);
            if (performRemoteSync) {
            	getInterests(null);
            	getMembers(null);
            	getSessions(null);
            	getLightningTalks(null);
            	// TODO : add remote initialization
            }
    		if (DEBUG_MODE) {
    			Log.d(TAG, "remote sync took " + (System.currentTimeMillis() - startRemote) + "ms");
    		}

            if (!localParse && performRemoteSync) {
//            	NotificationUtils.cancelNotifications(mContext);
            }
		} catch (JsonHandler.JsonHandlerException e) {
			Log.e(TAG, "An error occured while processing local files", e);
		}

		r.status = Response.STATUS_OK;
		r.bundle = new Bundle();
		return r;
	}

	private Response getInterests(Bundle b) {
		if (DEBUG_MODE) {
			Log.d(TAG, "getInterests() with bundle=" + b);
		}
		Response r = new Response();
		r.bundle = new Bundle();

		if (NetworkUtils.getConnectivity(this) == ConnectivityState.NONE) {
			r.status = Response.STATUS_NO_CONNECTIVITY;
			return r;
		}

		ResponseHttp myInterestResponse = NetworkUtils.sendURL(URL_INTERESTS, false, null);
		if (myInterestResponse == null || myInterestResponse.status != HttpURLConnection.HTTP_OK) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /interests");
			r.status = Response.STATUS_ERROR;
			return r;
		}
		
		String json = myInterestResponse.jsonText;

		if (TextUtils.isEmpty(json)) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /interests : No Data to retrieve");
			r.status = Response.STATUS_ERROR;
			return r;
		}

		JsonExecutor executor = new JsonExecutor(getContentResolver());
		try {
			executor.execute(json, new InterestsHandler());
			r.status = Response.STATUS_OK;
		} catch (JsonHandler.JsonHandlerException e) {
			Log.e(TAG, "An error occured while processing JSON data from /interests web service", e);
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /interests : error while processing JSON");
			r.status = Response.STATUS_ERROR;
		}

		return r;
	}

	private Response getSessions(Bundle b) {
		if (DEBUG_MODE) {
			Log.d(TAG, "getSessions() with bundle=" + b);
		}
		Response r = new Response();
		r.bundle = new Bundle();

		if (NetworkUtils.getConnectivity(this) == ConnectivityState.NONE) {
			r.status = Response.STATUS_NO_CONNECTIVITY;
			return r;
		}

		ResponseHttp mySessionResponse = NetworkUtils.sendURL(URL_TALKS, false, null);
		if (mySessionResponse == null || mySessionResponse.status != HttpURLConnection.HTTP_OK) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /sessions");
			r.status = Response.STATUS_ERROR;
			return r;
		}

		String json = mySessionResponse.jsonText;

		if (TextUtils.isEmpty(json)) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /sessions : No Data to retrieve");
			r.status = Response.STATUS_ERROR;
			return r;
		}

		JsonExecutor executor = new JsonExecutor(getContentResolver());
		try {
			executor.execute(json, new SessionsHandler(true, true, true));
			r.status = Response.STATUS_OK;
		} catch (JsonHandler.JsonHandlerException e) {
			Log.e(TAG, "An error occured while processing JSON data from /sessions web service", e);
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /sessions : error while processing JSON");
			r.status = Response.STATUS_ERROR;
		}

		return r;
	}

	private Response getSession(Bundle b) {
		if (DEBUG_MODE) {
			Log.d(TAG, "getSession() with bundle=" + b);
		}
		
		Response r = new Response();
		r.bundle = new Bundle();
		
		if (b == null || !b.containsKey(EXTRA_ID)) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Missing param {id} (via MixItService.EXTRA_ID) for calling /sessions/{id}");
			r.status = Response.STATUS_ERROR_PARAMS;
			return r;
		}
		
		if (NetworkUtils.getConnectivity(this) == ConnectivityState.NONE) {
			r.status = Response.STATUS_NO_CONNECTIVITY;
			return r;
		}
		
		int idSession = b.getInt(EXTRA_ID);
		ResponseHttp mySessionResponse = NetworkUtils.sendURL(String.format(URL_TALK, idSession), false, null);
		if (mySessionResponse == null || mySessionResponse.status != HttpURLConnection.HTTP_OK) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /sessions/" + idSession);
			r.status = Response.STATUS_ERROR;
			return r;
		}

		String json = mySessionResponse.jsonText;

		if (TextUtils.isEmpty(json)) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /sessions/" + idSession + " : No Data to retrieve");
			r.status = Response.STATUS_ERROR;
			return r;
		}

		JsonExecutor executor = new JsonExecutor(getContentResolver());
		try {
			executor.execute(json, new SessionsHandler(true, true, true));
			r.status = Response.STATUS_OK;
		} catch (JsonHandler.JsonHandlerException e) {
			Log.e(TAG, "An error occured while processing JSON data from /sessions/" + idSession + " web service", e);
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /sessions/" + idSession + " : error while processing JSON");
			r.status = Response.STATUS_ERROR;
		}

		return r;
	}

	private Response getLightningTalks(Bundle b) {
		if (DEBUG_MODE) {
			Log.d(TAG, "getLightningTalks() with bundle=" + b);
		}
		Response r = new Response();
		r.bundle = new Bundle();

		if (NetworkUtils.getConnectivity(this) == ConnectivityState.NONE) {
			r.status = Response.STATUS_NO_CONNECTIVITY;
			return r;
		}

		ResponseHttp myLightningTalksResponse = NetworkUtils.sendURL(URL_LIGHTNING_TALKS, false, null);
		if (myLightningTalksResponse == null || myLightningTalksResponse.status != HttpURLConnection.HTTP_OK) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /lightnings");
			r.status = Response.STATUS_ERROR;
			return r;
		}

		String json = myLightningTalksResponse.jsonText;

		if (TextUtils.isEmpty(json)) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /lightnings : No Data to retrieve");
			r.status = Response.STATUS_ERROR;
			return r;
		}

		JsonExecutor executor = new JsonExecutor(getContentResolver());
		try {
			executor.execute(json, new SessionsHandler(false, true, true));
			r.status = Response.STATUS_OK;
		} catch (JsonHandler.JsonHandlerException e) {
			Log.e(TAG, "An error occured while processing JSON data from /lightnings web service", e);
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /lightnings : error while processing JSON");
			r.status = Response.STATUS_ERROR;
		}

		return r;
	}

	private Response getLightningTalk(Bundle b) {
		if (DEBUG_MODE) {
			Log.d(TAG, "getLightningTalk() with bundle=" + b);
		}
		
		Response r = new Response();
		r.bundle = new Bundle();
		
		if (b == null || !b.containsKey(EXTRA_ID)) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Missing param {id} (via MixItService.EXTRA_ID) for calling /lightnings/{id}");
			r.status = Response.STATUS_ERROR_PARAMS;
			return r;
		}
		
		if (NetworkUtils.getConnectivity(this) == ConnectivityState.NONE) {
			r.status = Response.STATUS_NO_CONNECTIVITY;
			return r;
		}
		
		int idLightningTalk = b.getInt(EXTRA_ID);
		ResponseHttp myLightningTalkResponse = NetworkUtils.sendURL(String.format(URL_LIGHTNING_TALK, idLightningTalk), false, null);
		if (myLightningTalkResponse == null || myLightningTalkResponse.status != HttpURLConnection.HTTP_OK) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /lightnings/" + idLightningTalk);
			r.status = Response.STATUS_ERROR;
			return r;
		}

		String json = myLightningTalkResponse.jsonText;

		if (TextUtils.isEmpty(json)) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /lightnings/" + idLightningTalk + " : No Data to retrieve");
			r.status = Response.STATUS_ERROR;
			return r;
		}

		JsonExecutor executor = new JsonExecutor(getContentResolver());
		try {
			executor.execute(json, new SessionsHandler(false, true, true));
			r.status = Response.STATUS_OK;
		} catch (JsonHandler.JsonHandlerException e) {
			Log.e(TAG, "An error occured while processing JSON data from /sessions/" + idLightningTalk + " web service", e);
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /sessions/" + idLightningTalk + " : error while processing JSON");
			r.status = Response.STATUS_ERROR;
		}

		return r;
	}

	private Response getMembers(Bundle b) {
		if (DEBUG_MODE) {
			Log.d(TAG, "getMembers() with bundle=" + b);
		}
		Response r = new Response();
		r.bundle = new Bundle();

		if (NetworkUtils.getConnectivity(this) == ConnectivityState.NONE) {
			r.status = Response.STATUS_NO_CONNECTIVITY;
			return r;
		}

		ResponseHttp myMemberResponse = NetworkUtils.sendURL(URL_MEMBERS, false, null);
		if (myMemberResponse == null || myMemberResponse.status != HttpURLConnection.HTTP_OK) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /members");
			r.status = Response.STATUS_ERROR;
			return r;
		}

		String json = myMemberResponse.jsonText;

		if (TextUtils.isEmpty(json)) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /members : No Data to retrieve");
			r.status = Response.STATUS_ERROR;
			return r;
		}

		JsonExecutor executor = new JsonExecutor(getContentResolver());
		try {
			executor.execute(json, new MembersHandler(true, true));
			r.status = Response.STATUS_OK;
		} catch (JsonHandler.JsonHandlerException e) {
			Log.e(TAG, "An error occured while processing JSON data from /members web service", e);
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /members : error while processing JSON");
			r.status = Response.STATUS_ERROR;
		}

		return r;
	}

	private Response getMember(Bundle b) {
		if (DEBUG_MODE) {
			Log.d(TAG, "getMember() with bundle=" + b);
		}
		Response r = new Response();
		r.bundle = new Bundle();
		
		if (b == null || !b.containsKey(EXTRA_ID)) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Missing param {id} (via MixItService.EXTRA_ID) for calling /members/{id}");
			r.status = Response.STATUS_ERROR_PARAMS;
			return r;
		}

		if (NetworkUtils.getConnectivity(this) == ConnectivityState.NONE) {
			r.status = Response.STATUS_NO_CONNECTIVITY;
			return r;
		}

		int idMember = b.getInt(EXTRA_ID);
		ResponseHttp myMemberResponse = NetworkUtils.sendURL(String.format(URL_MEMBER, idMember), false, null);
		if (myMemberResponse == null || myMemberResponse.status != HttpURLConnection.HTTP_OK) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /members/" + idMember);
			r.status = Response.STATUS_ERROR;
			return r;
		}

		String json = myMemberResponse.jsonText;

		if (TextUtils.isEmpty(json)) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /members/" + idMember + " : No Data to retrieve");
			r.status = Response.STATUS_ERROR;
			return r;
		}

		JsonExecutor executor = new JsonExecutor(getContentResolver());
		try {
			executor.execute(json, new MembersHandler(false, false));
			r.status = Response.STATUS_OK;
		} catch (JsonHandler.JsonHandlerException e) {
			Log.e(TAG, "An error occured while processing JSON data from /members/" + idMember + " web service", e);
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling /members/" + idMember + " : error while processing JSON");
			r.status = Response.STATUS_ERROR;
		}

		return r;
	}

	private Response login(Bundle b) {
		if (DEBUG_MODE) {
			Log.d(TAG, "login() with bundle=" + b);
		}

		Response r = new Response();
		r.bundle = new Bundle();

		if (NetworkUtils.getConnectivity(this) == ConnectivityState.NONE) {
			r.status = Response.STATUS_NO_CONNECTIVITY;
			return r;
		}

		String login = b.getString(EXTRA_LOGIN_LOGIN);
		String pass = b.getString(EXTRA_LOGIN_PASSWORD);

		if (TextUtils.isEmpty(login) || TextUtils.isEmpty(pass)) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling login with LinkIT {login=" + login + ", pass=" + pass + " : Missing/Bad parameters");
			r.status = Response.STATUS_ERROR;
			return r;
		}

		HashMap<String, String> paramsLogin = new HashMap<String, String>(2);
		paramsLogin.put(PARAM_LOGIN, encode(login));
		paramsLogin.put(PARAM_PASSWORD, encode(pass));

		ResponseHttp myLoginResponse = NetworkUtils.sendURL(URL_LOGIN_LINKIT, false, paramsLogin);
		if (myLoginResponse == null || myLoginResponse.status != HttpURLConnection.HTTP_OK) {
			// 401 non loggé, 404 indispo
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling login with LinkIT {login=" + login + ", pass=" + pass
					+ " : Error while getting result from web services");
			r.status = Response.STATUS_ERROR;
			return r;
		}

		String json = myLoginResponse.jsonText;
		
		if (TextUtils.isEmpty(json)) {
			// r.bundle.putString(EXTRA_RECEIVER_ERROR_MESSAGE, "Error while calling login with LinkIT {login=" + login
			// + ", pass=" + pass + " : Error while converting result InputStream to String");
			// r.status = Response.STATUS_ERROR;
		}
		r.status = Response.STATUS_OK;
		r.bundle.putString(EXTRA_LOGIN_LOGIN, login);
		r.bundle.putString(EXTRA_LOGIN_PASSWORD, pass);

		return r;
	}

	private Response loginWithOAuth(Bundle b) {
		if (DEBUG_MODE) {
			Log.d(TAG, "loginWithOAuth() with bundle=" + b);
		}

		Response r = new Response();
		r.bundle = new Bundle();

		int provider = b.getInt(EXTRA_LOGIN_OAUTH_PROVIDER, OAuth.ACCOUNT_TYPE_NO);
		String loginOAuth = b.getString(EXTRA_LOGIN_OAUTH_LOGIN);
		if (provider == OAuth.ACCOUNT_TYPE_NO || TextUtils.isEmpty(loginOAuth)) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling login with OAuth {provider=" + provider + ", login=" + loginOAuth
					+ " : Missing/Bad parameters");
			r.status = Response.STATUS_ERROR;
			return r;
		}

		HashMap<String, String> paramsLoginOAuth = new HashMap<String, String>(2);
		paramsLoginOAuth.put(PARAM_OAUTH_PROVIDER, OAuth.getProviderString(provider));
		paramsLoginOAuth.put(PARAM_OAUTH_LOGIN, encode(loginOAuth));

		ResponseHttp myLoginOAuthResponse = NetworkUtils.sendURL(URL_LOGIN, false, paramsLoginOAuth);
		if (myLoginOAuthResponse == null || myLoginOAuthResponse.status != HttpURLConnection.HTTP_OK) {// TODO : 401 non loggé, 404 indispo
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling login with OAuth {provider=" + provider + ", login=" + loginOAuth
					+ " : Error while getting result from web services");
			r.status = Response.STATUS_ERROR;
			return r;
		}

		String connectionOAuthJson = myLoginOAuthResponse.jsonText;

		if (TextUtils.isEmpty(connectionOAuthJson)) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling login with OAuth {provider=" + provider + ", login=" + loginOAuth
					+ " : Error while converting result InputStream to String");
			r.status = Response.STATUS_ERROR;
			return r;
		}

		r.bundle.putInt(EXTRA_LOGIN_OAUTH_PROVIDER, provider);
		r.bundle.putString(EXTRA_LOGIN_OAUTH_LOGIN, loginOAuth);
		r.status = Response.STATUS_OK;
		return r;
	}
	
	private Response voteForLightningTalk(Bundle b) {
		if (DEBUG_MODE) {
			Log.d(TAG, "voteForLightningTalk() with bundle=" + b);
		}
		
		Response r = new Response();
		r.bundle = new Bundle();
		
		if (NetworkUtils.getConnectivity(this) == ConnectivityState.NONE) {
			r.status = Response.STATUS_NO_CONNECTIVITY;
			return r;
		}

		boolean stateVote = b.getBoolean(EXTRA_STATE_VOTE, false);
		int lightningTalkId = b.getInt(EXTRA_SESSION_ID, -1);

		if (lightningTalkId == -1) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling voteForLightningTalk {lightningTalkId=" + lightningTalkId + ", stateVote=" + stateVote + " : Missing/Bad parameters");
			r.status = Response.STATUS_ERROR;
			return r;
		}
		
		// TODO : WS call
		
		// TODO : change state in provider
		final Uri lightningUri = MixItContract.Sessions.buildSessionUri(String.valueOf(lightningTalkId));
		final ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(lightningUri);
		builder.withValue(MixItContract.Sessions.MY_VOTE, stateVote ? 1 : 0);
		final ContentProviderOperation ope = builder.build();
		final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>(1);
		batch.add(ope);
		try {
			getContentResolver().applyBatch(MixItContract.CONTENT_AUTHORITY, batch);
		} catch (RemoteException e) {
			Log.e(TAG, "Error while trying to update session star", e);
		} catch (OperationApplicationException e) {
			Log.e(TAG, "Error while trying to update session star", e);
		}
		r.status = Response.STATUS_OK;

		return r;
	}
	
	private Response starSession(Bundle b) {
		if (DEBUG_MODE) {
			Log.d(TAG, "starSession() with bundle=" + b);
		}

		Response r = new Response();
		r.bundle = new Bundle();

		if (NetworkUtils.getConnectivity(this) == ConnectivityState.NONE) {
			r.status = Response.STATUS_NO_CONNECTIVITY;
			return r;
		}

		boolean stateStar = b.getBoolean(EXTRA_STATE_STAR, false);
		int sessionId = b.getInt(EXTRA_SESSION_ID, -1);

		if (sessionId == -1) {
			r.bundle.putString(EXTRA_ERROR_MESSAGE, "Error while calling starSession {sessionId=" + sessionId + ", stateStar=" + stateStar + " : Missing/Bad parameters");
			r.status = Response.STATUS_ERROR;
			return r;
		}
		
		// TODO : WS call
		
		// TODO : change state in provider
		final Uri sessionUri = MixItContract.Sessions.buildSessionUri(String.valueOf(sessionId));
		final ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(sessionUri);
		builder.withValue(MixItContract.Sessions.IS_FAVORITE, stateStar ? 1 : 0);
		final ContentProviderOperation ope = builder.build();
		final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>(1);
		batch.add(ope);
		try {
			getContentResolver().applyBatch(MixItContract.CONTENT_AUTHORITY, batch);
		} catch (RemoteException e) {
			Log.e(TAG, "Error while trying to update session star", e);
		} catch (OperationApplicationException e) {
			Log.e(TAG, "Error while trying to update session star", e);
		}
		r.status = Response.STATUS_OK;

		return r;
	}

	static String encode(String str) {
		try {
			String plaintext = str;
			int shiftKey = 5;
			shiftKey = shiftKey % 26;

			String cipherText = "";

			for (int i = 0; i < plaintext.length(); i++) {
				int asciiValue = (int) plaintext.charAt(i);
				if (asciiValue < 65 || (asciiValue > 90 && asciiValue < 97) || asciiValue > 122) {
					cipherText += plaintext.charAt(i);
					continue;
				}

				int basicValue = 0;
				int newAsciiValue = 0;

				if (asciiValue >= 65 && asciiValue <= 90) {
					basicValue = asciiValue - 65;
					newAsciiValue = 65 + ((basicValue + shiftKey) % 26);
				} else if (asciiValue >= 97 || asciiValue <= 122) {
					basicValue = asciiValue - 97;
					newAsciiValue = 97 + ((basicValue + shiftKey) % 26);
				}
				cipherText += (char) newAsciiValue;
			}

			if (DEBUG_MODE)
				Log.e(TAG, "login to encode : " + str + " ; encoded login : " + cipherText);
			return cipherText;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Should we perform a remote sync?
	 */
	private static boolean performRemoteSync(/*ContentResolver resolver, *//*HttpClient httpClient, */Bundle bundle, Context context) {
	    final SharedPreferences settingsPrefs = context.getSharedPreferences(PrefUtils.SETTINGS_NAME, MODE_PRIVATE);
	    final SharedPreferences syncServicePrefs = context.getSharedPreferences(PrefUtils.MIXITSCHED_SYNC, Context.MODE_PRIVATE);
	    final boolean onlySyncWifi = settingsPrefs.getBoolean(context.getString(R.string.sync_only_wifi_key), false);
	    final int localVersion = syncServicePrefs.getInt(PrefUtils.LOCAL_VERSION, PrefUtils.VERSION_NONE);
	    if (!onlySyncWifi || isWifiConnected(context)) {
	        final boolean remoteParse = localVersion < PrefUtils.VERSION_REMOTE;
		    final boolean forceRemoteRefresh = bundle.getBoolean(EXTRA_FORCE_REFRESH, true); // TODO : change the force refresh to user intention only
//		    final boolean hasContentChanged = hasContentChanged(resolver, httpClient);
		    return !MixItApplication.FORCE_OFFLINE && (remoteParse || forceRemoteRefresh)/* || hasContentChanged*/;
	    }
	    return false;
	}

	/**
	 * Are we connected to a WiFi network?
	 */
	private static boolean isWifiConnected(Context context) {
		final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager != null) {
			NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			return (networkInfo != null && networkInfo.getState().equals(NetworkInfo.State.CONNECTED));
		}

		return false;
	}

}
