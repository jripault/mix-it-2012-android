package fr.mixit.android.ui;

import android.os.Bundle;
import fr.mixit.android.R;

public class StreamActivity extends GenericMixItActivity {
	
	@Override
	protected void onCreate(Bundle savedStateInstance) {
		super.onCreate(savedStateInstance);
	}
	
	@Override
	protected int getContentLayoutId() {
		return R.layout.activity_stream;
	}
	
}
