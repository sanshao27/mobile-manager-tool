/**
 * 
 */

package com.blue.sky.mobile.manager.music.activities;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.*;
import com.blue.sky.control.slidinguppanel.SlidingUpPanelLayout;
import com.blue.sky.control.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.blue.sky.mobile.manager.R;

import com.blue.sky.mobile.manager.music.IApolloService;
import com.blue.sky.mobile.manager.music.module.local.MusicLoveListFragment;
import com.blue.sky.mobile.manager.music.module.local.MusicSongListFragment;
import com.blue.sky.mobile.manager.music.helpers.utils.ApolloUtils;
import com.blue.sky.mobile.manager.music.helpers.utils.MusicUtils;
import com.blue.sky.mobile.manager.music.preferences.SettingsHolder;
import com.blue.sky.mobile.manager.music.service.ApolloService;
import com.blue.sky.mobile.manager.music.service.ServiceToken;
import com.blue.sky.mobile.manager.music.ui.adapters.PagerAdapter;
import com.blue.sky.mobile.manager.music.ui.adapters.ScrollingTabsAdapter;
import com.blue.sky.mobile.manager.music.ui.fragments.BottomActionBarFragment;
import com.blue.sky.mobile.manager.music.ui.widgets.ScrollableTabView;

/**
 * @author Andrew Neal
 * @Note This is the "holder" for all of the tabs
 */
public class MusicLibrary extends FragmentActivity implements ServiceConnection {
	
	private SlidingUpPanelLayout mPanel;
    
	private ServiceToken mToken;
    
	public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
    
	BottomActionBarFragment mBActionbar;
    
	private boolean isAlreadyStarted = false;	
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        // Landscape mode on phone isn't ready
        if (!ApolloUtils.isTablet(this))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // Scan for music
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);       
       // getSupportFragmentManager().beginTransaction().add(R.id.bottomactionbar_new, new BottomActionBarFragment(), "bottomactionbar_new").commit();
        
        // Layout
        setContentView(R.layout.music_library_browser);
        
        mBActionbar =(BottomActionBarFragment) getSupportFragmentManager().findFragmentById(R.id.bottomactionbar_new);
  
        mBActionbar.setUpQueueSwitch(this);
        
        mPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        mPanel.setAnchorPoint(0);
        
        mPanel.setDragView(findViewById(R.id.bottom_action_bar_dragview));
        //mPanel.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
        mPanel.setAnchorPoint(0.0f);
        mPanel.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset < 0.2) {
                    mBActionbar.onExpanded();
                    if (getActionBar().isShowing()) {
                        getActionBar().hide();
                    }
                } else {
                    mBActionbar.onCollapsed();
                    if (!getActionBar().isShowing()) {
                        getActionBar().show();
                    }
                }
            }
            @Override
            public void onPanelExpanded(View panel) {
            }
            @Override
            public void onPanelCollapsed(View panel) {
            }
            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
        
        String startedFrom = getIntent().getStringExtra("started_from");
        if(startedFrom!=null){
        	ViewTreeObserver vto = mPanel.getViewTreeObserver();
        	vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        	    @Override
        	    public void onGlobalLayout() {
        	    	if(!isAlreadyStarted){
            	        //mPanel.expandPane();
            	        isAlreadyStarted=true;
        	    	}
        	    }
        	});
        }
        
        // Style the actionbar
        initActionBar();

        // Control Media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        // Important!
        initPager();  
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//    	if(mPanel.isExpanded()){
//            mPanel.collapsePane();
//    	}
//    	else{
//    		super.onBackPressed();
//    	}
    }
    
    @Override
    public void onServiceConnected(ComponentName name, IBinder obj) {
        MusicUtils.mService = IApolloService.Stub.asInterface(obj);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        MusicUtils.mService = null;
    }

    @Override
    protected void onStart() {

        // Bind to Service
        mToken = MusicUtils.bindToService(this, this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ApolloService.META_CHANGED);
        super.onStart();
    }

    @Override
    protected void onStop() {
        // Unbind
        if (MusicUtils.mService != null)
            MusicUtils.unbindFromService(mToken);

        //TODO: clear image cache

        super.onStop();
    }

    /**
     * Initiate ViewPager and PagerAdapter
     */
    public void initPager() {
        // Initiate PagerAdapter
        PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        mPagerAdapter.addFragment(new MusicSongListFragment());
        mPagerAdapter.addFragment(new MusicLoveListFragment());

        // Initiate ViewPager
        ViewPager mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mViewPager.setPageMargin(getResources().getInteger(R.integer.viewpager_margin_width));
        mViewPager.setPageMarginDrawable(R.drawable.music_viewpager_margin);
        mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
        mViewPager.setAdapter(mPagerAdapter);
        //mViewPager.setCurrentItem(0);

        // Tabs
        initScrollableTabs(mViewPager);
    }

    /**
     * Initiate the tabs
     */
    public void initScrollableTabs(ViewPager mViewPager) {
        ScrollableTabView mScrollingTabs = (ScrollableTabView)findViewById(R.id.scrollingTabs);
        ScrollingTabsAdapter mScrollingTabsAdapter = new ScrollingTabsAdapter(this);
        mScrollingTabs.setAdapter(mScrollingTabsAdapter,null);
        mScrollingTabs.setViewPager(mViewPager);
    }
    
    /**
     * For the theme chooser
     */
    private void initActionBar() {
    	ActionBar actBar = getActionBar();
    	actBar.setDisplayUseLogoEnabled(true);
        actBar.setDisplayShowTitleEnabled(false);
    }
    
    /**
     * Respond to clicks on actionbar options
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.action_search:
	            onSearchRequested();
	            break;

	        case R.id.action_settings:
	        	startActivityForResult(new Intent(this, SettingsHolder.class),0);
	            break;

	        case R.id.action_eqalizer:
	    	    final Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
	            if (getPackageManager().resolveActivity(intent, 0) == null) {
		        	startActivity(new Intent(this, SimpleEq.class));
	        	}
	        	else{
	        		intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, MusicUtils.getCurrentAudioId());
	        		startActivity(intent);
	        	}
	            break;

	        case R.id.action_shuffle_all:
	        	shuffleAll();
	            break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	Intent i = getBaseContext().getPackageManager()
	             .getLaunchIntentForPackage( getBaseContext().getPackageName() );
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
    }
    
    /**
     * Initiate the Top Actionbar
     */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.music_actionbar_top, menu);
	    return true;
	}

	/**
     * Shuffle all the tracks
     */
    public void shuffleAll() {
        Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] {
            BaseColumns._ID
        };
        String selection = AudioColumns.IS_MUSIC + "=1";
        String sortOrder = "RANDOM()";
        Cursor cursor = MusicUtils.query(this, uri, projection, selection, null, sortOrder);
        if (cursor != null) {
            MusicUtils.shuffleAll(this, cursor);
            cursor.close();
            cursor = null;
        }
    }
}
