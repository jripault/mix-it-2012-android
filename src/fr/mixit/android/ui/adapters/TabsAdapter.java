package fr.mixit.android.ui.adapters;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;
import android.widget.TabWidget;
import fr.mixit.android.R;
import fr.mixit.android.ui.controller.TabContainer;
import fr.mixit.android.utils.UIUtils;

/**
 * This is a helper class that implements the management of tabs and all details of connecting a ViewPager with associated TabHost. It relies on a trick.
 * Normally a tab host has a simple API for supplying a View or Intent that each tab will show. This is not sufficient for switching between pages. So
 * instead we make the content part of the tab host 0dp high (it is not shown) and the TabsAdapter supplies its own dummy view to show as the tab content.
 * It listens to changes in tabs, and takes care of switch to the correct paged in the ViewPager whenever the selected tab changes.
 */
public class TabsAdapter extends PagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
	private final Context mContext;
	private final TabHost mTabHost;
	private final HorizontalScrollView mHorizontalScrollV;
	private final ViewPager mViewPager;
	private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
	private static int mScreenWidth;

	static final class TabInfo {
		private final String tag;
		private final TabContainer tabContainer;

		TabInfo(String _tag, TabContainer _tabContainer) {
			tag = _tag;
			tabContainer = _tabContainer;
		}
	}

	static class DummyTabFactory implements TabHost.TabContentFactory {
		private final Context mContext;

		public DummyTabFactory(Context context) {
			mContext = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}

	public TabsAdapter(Context context, TabHost tabHost, ViewPager pager) {
		super();
		mScreenWidth = UIUtils.getScreenWidth(context);
		mContext = context;
		mTabHost = tabHost;
		mHorizontalScrollV = (HorizontalScrollView) tabHost.findViewById(R.id.horizontal_tab_widget);
		mViewPager = pager;
		mTabHost.setOnTabChangedListener(this);
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}

	public void addTab(TabHost.TabSpec tabSpec, TabContainer tabContainer) {
		tabSpec.setContent(new DummyTabFactory(mContext));
		String tag = tabSpec.getTag();

		TabInfo info = new TabInfo(tag, tabContainer);
		mTabs.add(info);
		mTabHost.addTab(tabSpec);
		notifyDataSetChanged();
	}

	public TabContainer getTabContainer(String tag) {
		if (mTabs != null) {
			for (Iterator<TabInfo> iterator = mTabs.iterator(); iterator.hasNext();) {
				TabInfo tabInfo = (TabInfo) iterator.next();
				if (tabInfo.tag.equals(tag)) {
					return tabInfo.tabContainer;
				}
			}
		}
		return null;
	}
	
//	public int getPosition(String tag) {
//		if (mTabs != null) {
//			for (int i=0; i<mTabs.size(); i++) {
//				TabInfo tabInfo = mTabs.get(i);
//				if (tabInfo.tag.equals(tag)) {
//					return i;
//				}
//			}
//		}
//		return -1;
//	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		TabInfo tabInfo = mTabs.get(position);
		View tabView = tabInfo.tabContainer.getView();
		if (tabView.getParent() != null) {
			((ViewGroup) tabView.getParent()).removeView(tabView);
		}
		container.addView(tabView);
		return tabView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public int getCount() {
		return mTabs.size();
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		// Unfortunately when TabHost changes the current tab, it kindly
		// also takes care of putting focus on it when not in touch mode.
		// The jerk.
		// This hack tries to prevent this from pulling focus out of our
		// ViewPager.
		TabWidget widget = mTabHost.getTabWidget();
		int oldFocusability = widget.getDescendantFocusability();
		widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		mTabHost.setCurrentTab(position);
		widget.setDescendantFocusability(oldFocusability);
		if (mHorizontalScrollV != null) {
			final int width = widget.getMeasuredWidth();
//			final float oneTabWidth = ((float)width)/mTabs.size();
			float scrollOffset = (float)position * ((float)width - (float)mScreenWidth)/((float)mTabs.size() - 1.0F);
			mHorizontalScrollV.scrollTo((int)scrollOffset, 0);
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		int position = mTabHost.getCurrentTab();
		mViewPager.setCurrentItem(position);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
//	/**
//	 * A little hacky method that works only for members details fragment 
//	 */
//	public void clearTabs() {
////		for (int i=mTabHost.getTabWidget().getChildCount()-1; i>0; i--) {
////			mTabHost.getTabWidget().getChildAt(i).setVisibility(View.GONE);
////		}
//		mTabHost.setCurrentTab(0);
//		mTabs.clear();
//		mTabHost.clearAllTabs();
////		TabWidget widget = mTabHost.getTabWidget();
////		widget.removeAllViews();
////		mViewPager.removeAllViews();
////		mTabHost.setup();
////		notifyDataSetChanged();
//	}

}
