package fr.mixit.android.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.actionbarsherlock.app.SherlockListFragment;

import fr.mixit.android.MixItApplication;
import fr.mixit.android.services.MixItService;

public abstract class BoundServiceListFragment extends SherlockListFragment {

	static final boolean DEBUG_MODE = MixItApplication.DEBUG_MODE;
	static final String TAG = BoundServiceListFragment.class.getSimpleName();

	/** Flag indicating whether we have called bind on the service. */
	boolean isBound = false;
	/**
	 * Flag indicating whether the service is bound and we have registered to it.
	 */
	boolean serviceReady = false;

	/** Messenger for communicating with service. */
	Messenger service = null;

	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MixItService.MSG_REGISTER_CLIENT:
				onServiceReady();
				break;
			case MixItService.MSG_UNREGISTER_CLIENT:
				onServiceNotReady();
				break;
			default:
				if (DEBUG_MODE) {
					Log.d(TAG, "onMessageReceivedFromService(): What:" + msg.what + " - Status:" + msg.arg1);
				}
				onMessageReceivedFromService(msg);
			}
		}
	}

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger messenger = new Messenger(new IncomingHandler());

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder aService) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. We are communicating with our
			// service through an IDL interface, so get a client-side
			// representation of that from the raw service object.
			service = new Messenger(aService);

			// We want to monitor the service for as long as we are
			// connected to it.
			try {
				Message msg = Message.obtain(null, MixItService.MSG_REGISTER_CLIENT);
				msg.replyTo = messenger;
				service.send(msg);
			} catch (RemoteException e) {
				// In this case the service has crashed before we could even
				// do anything with it; we can count on soon being
				// disconnected (and then reconnected if it can be restarted)
				// so there is no need to do anything here.
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			service = null;
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			BoundServiceContract contract = (BoundServiceContract) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement BoundServiceContract");
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		doBindService();
	}

	@Override
	public void onStop() {
		super.onStop();

		doUnbindService();
	}

	protected void doBindService() {
		if (DEBUG_MODE) {
			Log.d(TAG, "doBindService()");
		}
		// Establish a connection with the service. We use an explicit
		// class name because there is no reason to be able to let other
		// applications replace our component.
		getActivity().bindService(new Intent(getActivity(), MixItService.class), mConnection, Context.BIND_AUTO_CREATE);
		isBound = true;
	}

	protected void doUnbindService() {
		if (DEBUG_MODE) {
			Log.d(TAG, "doUnbindService()");
		}
		if (isBound) {
			// If we have received the service, and hence registered with
			// it, then now is the time to unregister.
			if (service != null) {
				try {
					Message msg = Message.obtain(null, MixItService.MSG_UNREGISTER_CLIENT);
					msg.replyTo = messenger;
					service.send(msg);
				} catch (RemoteException e) {
					// There is nothing special we need to do if the service
					// has crashed.
				}
			}

			// Detach our existing connection.
			getActivity().unbindService(mConnection);
			isBound = false;
		}
	}

	/**
	 * When the service is bound and we are registered to it, we refresh the Data from WebService with the {@link MixItService}
	 */
	protected void onServiceReady() {
		serviceReady = true;
	}

	/**
	 * Called when a message other than register and unregister is received from the service. Be careful, you need to treat the information, and refresh UI
	 * before calling the super method.
	 * 
	 * @param msg
	 *            the message received from the service
	 */
	abstract protected void onMessageReceivedFromService(Message msg);
	
	/**
	 * Called when the parent activity is no longer registered to this service, ergo the service won't treat possible request from us
	 */
	protected void onServiceNotReady() {
		serviceReady = false;
	}
	
	void setRefreshMode(boolean state) {
		if (getActivity() != null && !isDetached()) {
			((BoundServiceContract) getActivity()).setRefreshMode(state);
		}
	}
	
}
