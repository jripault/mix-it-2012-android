package fr.mixit.android.ui;

import fr.mixit.android.R;
import android.os.Bundle;

public class ScheduleActivity extends GenericMixItActivity {
	
	@Override
	protected void onCreate(Bundle savedStateInstance) {
		super.onCreate(savedStateInstance);
	}
	
	@Override
	protected int getContentLayoutId() {
		return R.layout.activity_schedule;
	}

}