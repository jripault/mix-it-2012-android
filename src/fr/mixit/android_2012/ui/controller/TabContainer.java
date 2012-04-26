package fr.mixit.android_2012.ui.controller;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

public abstract class TabContainer {
	
	protected Context mContext;
	protected View mContainerLayout;
	
	public TabContainer(Context ctx, ViewGroup container) {
		super();
		
		mContext = ctx;

		initView(container);
	}
	
	abstract protected void initView(ViewGroup container);
	
	abstract public void setCursor(Cursor c);
	
	public View getView() {
		return mContainerLayout;
	}
	
}
