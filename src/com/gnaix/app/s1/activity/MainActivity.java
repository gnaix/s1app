package com.gnaix.app.s1.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Toast;

import com.gnaix.app.s1.R;
import com.gnaix.app.s1.activity.ForumListFragment.OnForumSelectedListener;
import com.gnaix.app.s1.bean.Forum;
import com.gnaix.app.s1.nav.NavigationManager;
import com.gnaix.app.s1.service.Stage1ApiClient;
import com.gnaix.common.app.BaseActivity;
import com.gnaix.common.util.OSUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;

public class MainActivity extends BaseActivity implements OnForumSelectedListener, PageFragmentHost {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private NavigationManager mNavigationManager;
    private ForumListFragment mForumListFragment;
    private ForumTopicListFragment mForumTopicListFragment;

    private Stage1ApiClient mStage1ApiClient;

    private View mDrawer;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        MobclickAgent.setDebugMode(false);
        MobclickAgent.setAutoLocation(false);
        MobclickAgent.openActivityDurationTrack(false);
        UmengUpdateAgent.setUpdateCheckConfig(false);
        UmengUpdateAgent.setDeltaUpdate(true);
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.setUpdateUIStyle(UpdateStatus.STYLE_NOTIFICATION);

        mStage1ApiClient = new Stage1ApiClient(this);
        mNavigationManager = new NavigationManager(this);
        mDrawer = findViewById(R.id.left_drawer);

        mForumListFragment = new ForumListFragment();
        mForumListFragment.setRefreshRequired(true);
        mForumListFragment.setOnForumSelectedListener(this);

        mForumTopicListFragment = new ForumTopicListFragment();
        mForumTopicListFragment.setRefreshRequired(true);
        mForumTopicListFragment.setArgument("PAGE", 1);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.left_drawer, mForumListFragment);
        transaction.replace(R.id.content_frame, mForumTopicListFragment);
        transaction.commit();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer.getLayoutParams().width = calculateSideDrawerWidth();
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer_white,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                Forum forum = mForumListFragment.getSelectedForum();
                getSupportActionBar().setTitle(forum == null ? getText(R.string.text_hot_threads) : forum.getName());
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(R.string.app_name);
            }
        };
        getSupportActionBar().setTitle(getText(R.string.text_hot_threads));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (mPageNeedsRefresh) {
            mNavigationManager.refreshPage();
            mPageNeedsRefresh = false;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPageNeedsRefresh = true;
    }

    private boolean mPageNeedsRefresh = false;

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private int calculateSideDrawerWidth() {
        Resources resources = getResources();
        int widthPixels = resources.getDisplayMetrics().widthPixels;
        return (int) Math.min(widthPixels * 0.8, OSUtil.dip2px(getApplicationContext(), 320));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    @Override
    public boolean onSupportNavigateUp() {
        mNavigationManager.goBack();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mStage1ApiClient != null) {
            mStage1ApiClient.close();
        }
    }

    @Override
    public void onForumSelected(Forum forum) {
        mDrawerLayout.closeDrawer(mDrawer);
        mForumTopicListFragment.setArgument("FORUM", forum);
        mForumTopicListFragment.setArgument("PAGE", 1);
        mForumTopicListFragment.setRefreshRequired(true);
        if(!mForumTopicListFragment.isAdded()){
            mNavigationManager.showPage(NavigationManager.PAGE_FROUM_TOPIC, mForumTopicListFragment);
        }
        mForumTopicListFragment.refresh();
    }

    @Override
    public void onLoadFinish(Forum forum) {
        mForumTopicListFragment.setArgument("FORUM", forum);
        mForumTopicListFragment.setArgument("PAGE", 1);
        mForumTopicListFragment.setRefreshRequired(true);
        mForumTopicListFragment.refresh();
    }

    private long lastPressBack = 0;

    @Override
    public void onBackPressed() {
        if (mNavigationManager.canGoBack()) {
            mNavigationManager.goBack();
        } else {
            if (SystemClock.elapsedRealtime() - lastPressBack < 2000) {
                super.onBackPressed();
            } else {
                Toast.makeText(getApplicationContext(), R.string.text_exit_app, Toast.LENGTH_SHORT).show();
            }
            lastPressBack = SystemClock.elapsedRealtime();
        }
    }

    @Override
    public Stage1ApiClient getS1Api() {
        return mStage1ApiClient;
    }

    @Override
    public NavigationManager getNavigationManager() {
        return mNavigationManager;
    }

    @Override
    public void showErrorDialog(String title, String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public ActionBar getHostActionBar() {
        return getSupportActionBar();
    }

    @Override
    public ActionBarDrawerToggle getHostActionBarDrawerToggle() {
        return mDrawerToggle;
    }

    @Override
    public DrawerLayout getHostDrawerLayout() {
        return mDrawerLayout;
    }

}
