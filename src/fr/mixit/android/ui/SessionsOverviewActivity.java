package fr.mixit.android.ui;


public class SessionsOverviewActivity extends GenericMixItActivity/* implements OnNavigationListener*/ {
	
//	static final String TAG = SessionsOverviewActivity.class.getSimpleName();
//	
//	static final String STATUS_CURRENT_TAB = "tab";
//	static final String STATE_DISPLAY_MODE = "displayMode";
//
//	static final String TAB_TRACKS = "tracks";
//	static final String TAB_INTERESTS = "interests";
//	static final String TAB_ALL = "all";
//
//    TabHost mTabHost;
//    ViewPager  mViewPager;
//    TabsAdapter mTabsAdapter;
//    private SESSIONS_DISPLAY_MODE mode = SESSIONS_DISPLAY_MODE.SESSIONS;
//    
//	@Override
//	protected void onCreate(Bundle savedStateInstance) {
//		super.onCreate(savedStateInstance);
//		
//		setContentView(R.layout.activity_sessions_overview);
//
//		Context mContext = getSupportActionBar().getThemedContext();
//		ArrayAdapter<CharSequence> listAdapter = ArrayAdapter.createFromResource(mContext, R.array.sessions, R.layout.sherlock_spinner_item);
//		listAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
//
//		getSupportActionBar().setListNavigationCallbacks(listAdapter, this);
//		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//		
//		initTabs();
//		
//        if (savedStateInstance != null) {
//            mTabHost.setCurrentTabByTag(savedStateInstance.getString(STATUS_CURRENT_TAB));
//			mode = savedStateInstance.getInt(STATE_DISPLAY_MODE, 0) == 0 ? SESSIONS_DISPLAY_MODE.SESSIONS : SESSIONS_DISPLAY_MODE.LIGHTNING_TALKS;
//		}
//	}
//	
//	static int i = 0;
//	
//	void initTabs() {
//		if (mTabHost == null) {
//	        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
//	        mTabHost.setup();
//		} 
//
//		if (mViewPager == null) {
//			mViewPager = (ViewPager)findViewById(R.id.pager);
//		}
//
//        if (mTabsAdapter == null) {
//        	mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);
//        }
//
//        Bundle b = UIUtils.intentToFragmentArguments(getIntent());
//        b.putInt(IntentUtils.EXTRA_DISPLAY_MODE, mode == SESSIONS_DISPLAY_MODE.SESSIONS ? 0 : 1);
//        if (mode == SESSIONS_DISPLAY_MODE.SESSIONS) {
//	        mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_TRACKS+i).setIndicator("Tracks"),
//	                TracksFragment.class, b);
//        }
//        mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_INTERESTS+i).setIndicator("Interests"),
//                InterestsFragment.class, b);
//        mTabsAdapter.addTab(mTabHost.newTabSpec(TAB_ALL+i).setIndicator("All"),
//                SessionsListFragment.class, b);
//        i++;
//	}
//	
//	void resetTabs() {
//		mTabsAdapter.clearAllTabs();
////        mTabHost.clearAllTabs();
//	}
//	
//	@Override
//	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
//		// TODO : currently there is an error when adding tab after reseting
//		if (itemPosition == 0 && mode != SESSIONS_DISPLAY_MODE.SESSIONS) {
//			mode = SESSIONS_DISPLAY_MODE.SESSIONS;
//			resetTabs();
//			initTabs();
//		} else if (itemPosition == 1 && mode != SESSIONS_DISPLAY_MODE.LIGHTNING_TALKS) {
//			mode = SESSIONS_DISPLAY_MODE.LIGHTNING_TALKS;
//			resetTabs();
//			initTabs();
//		}
//		
//		return true;
//	}
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString(STATUS_CURRENT_TAB, mTabHost.getCurrentTabTag());
//		outState.putInt(STATE_DISPLAY_MODE, mode == SESSIONS_DISPLAY_MODE.SESSIONS ? 0 : 1);
//    }
//    
//    @Override
//    public void startActivityFromFragment(Fragment fragment, Intent intent, 
//            int requestCode) {
//    	if (UIUtils.isTablet(this)) {
//			Log.e(TAG, "This activity should not be executed on Tablet...");
//    	} else {
//    		super.startActivityFromFragment(fragment, intent, requestCode);
//    	}
//    }
//    
//    /**
//     * This is a helper class that implements the management of tabs and all
//     * details of connecting a ViewPager with associated TabHost.  It relies on a
//     * trick.  Normally a tab host has a simple API for supplying a View or
//     * Intent that each tab will show.  This is not sufficient for switching
//     * between pages.  So instead we make the content part of the tab host
//     * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
//     * view to show as the tab content.  It listens to changes in tabs, and takes
//     * care of switch to the correct paged in the ViewPager whenever the selected
//     * tab changes.
//     */
//    public static class TabsAdapter extends FragmentPagerAdapter
//            implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
//        private final Context mContext;
//        private final TabHost mTabHost;
//        private final ViewPager mViewPager;
//        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
//
//        static final class TabInfo {
//            private final String tag;
//            private final Class<?> clss;
//            private final Bundle args;
//
//            TabInfo(String _tag, Class<?> _class, Bundle _args) {
//                tag = _tag;
//                clss = _class;
//                args = _args;
//            }
//        }
//
//        static class DummyTabFactory implements TabHost.TabContentFactory {
//            private final Context mContext;
//
//            public DummyTabFactory(Context mContext) {
//                mContext = mContext;
//            }
//
//            @Override
//            public View createTabContent(String tag) {
//                View v = new View(mContext);
//                v.setMinimumWidth(0);
//                v.setMinimumHeight(0);
//                return v;
//            }
//        }
//
//        public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
//            super(activity.getSupportFragmentManager());
//            mContext = activity;
//            mTabHost = tabHost;
//            mViewPager = pager;
//            mTabHost.setOnTabChangedListener(this);
//            mViewPager.setAdapter(this);
//            mViewPager.setOnPageChangeListener(this);
//        }
//
//        public void clearAllTabs() {
//        	mTabHost.setCurrentTab(0);
//        	mTabHost.clearAllTabs();
//			mTabs.clear();
//			notifyDataSetChanged();
//		}
//
//		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
//            tabSpec.setContent(new DummyTabFactory(mContext));
//            String tag = tabSpec.getTag();
//
//            TabInfo info = new TabInfo(tag, clss, args);
//            mTabs.add(info);
//            mTabHost.addTab(tabSpec);
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public int getCount() {
//            return mTabs.size();
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            TabInfo info = mTabs.get(position);
//            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
//        }
//
//        @Override
//        public void onTabChanged(String tabId) {
//            int position = mTabHost.getCurrentTab();
//            mViewPager.setCurrentItem(position);
//        }
//
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        }
//
//        @Override
//        public void onPageSelected(int position) {
//            // Unfortunately when TabHost changes the current tab, it kindly
//            // also takes care of putting focus on it when not in touch mode.
//            // The jerk.
//            // This hack tries to prevent this from pulling focus out of our
//            // ViewPager.
//            TabWidget widget = mTabHost.getTabWidget();
//            int oldFocusability = widget.getDescendantFocusability();
//            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//            mTabHost.setCurrentTab(position);
//            widget.setDescendantFocusability(oldFocusability);
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int state) {
//        }
//    }
    
}
