package fr.mixit.android.model;

public class OAuth {
	
	public static final int ACCOUNT_TYPE_NO = 1000;
	public static final int ACCOUNT_TYPE_GOOGLE = 1001;
	public static final int ACCOUNT_TYPE_TWITTER = 1002;
	
	static final String GOOGLE = "Google";
	static final String TWITTER = "Twitter";
	
	public static String getProviderString(int provider) {
		String providerName = null;
		switch (provider) {
		case ACCOUNT_TYPE_GOOGLE:
			providerName = GOOGLE;
			break;

		case ACCOUNT_TYPE_TWITTER:
			providerName = TWITTER;
			break;

		default:
			break;
		}
		
		return providerName;
	}
	
}
