package fr.mixit.android.io;

public abstract class BaseHandler {

    private final String mAuthority;
	private boolean localSync;
	
	public BaseHandler(String authority) {
		this.mAuthority = authority;
		this.localSync = false;
	}

	public String getAuthority() {
		return mAuthority;
	}
	
	public boolean isLocalSync() {
		return localSync;
	}

	public boolean isRemoteSync() {
		return !localSync;
	}

	public void setLocalSync(boolean localSync) {
		this.localSync = localSync;
	}
	
}
