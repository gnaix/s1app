package com.gnaix.app.s1.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gnaix.app.s1.R;
import com.gnaix.app.s1.activity.ForumListFragment.OnForumSelectedListener;
import com.gnaix.app.s1.activity.ForumTopicListFragment.OnTopicClickerListener;
import com.gnaix.app.s1.bean.Forum;
import com.gnaix.app.s1.bean.Topic;
import com.gnaix.app.s1.service.Stage1ApiClient;
import com.gnaix.common.app.BaseActivity;
import com.gnaix.common.ui.AutoDismissFragmentDialog;
import com.gnaix.common.ui.AutoDismissFragmentDialog.AutoDismissListener;

public class MainActivity extends BaseActivity implements OnForumSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private ForumListFragment mForumListFragment;
    private ForumTopicListFragment mForumTopicListFragment;

    private Stage1ApiClient mStage1ApiClient;

    private View mDrawer;

    public Stage1ApiClient getStage1ApiClient() {
        return mStage1ApiClient;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        mStage1ApiClient = new Stage1ApiClient(this);
        mDrawer = findViewById(R.id.left_drawer);
        mForumListFragment = (ForumListFragment) getSupportFragmentManager().findFragmentById(
                R.id.left_drawer);
        mForumListFragment.setOnForumSelectedListener(this);

        mForumTopicListFragment = (ForumTopicListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_frame);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer_white,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                Forum forum = mForumListFragment.getSelectedForum();
                getSupportActionBar().setTitle(
                        forum == null ? getText(R.string.text_hot_threads) : forum.getName());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
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
        if (!mForumTopicListFragment.isAdded()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, mForumTopicListFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
        mForumTopicListFragment.request(forum, 1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onLoadFinish(Forum forum) {
        if (!mForumTopicListFragment.isAdded()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, mForumTopicListFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
        mForumTopicListFragment.request(forum, 1);
    }
}
