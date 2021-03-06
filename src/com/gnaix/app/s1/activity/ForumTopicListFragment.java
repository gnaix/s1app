package com.gnaix.app.s1.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.gnaix.app.s1.Constants;
import com.gnaix.app.s1.R;
import com.gnaix.app.s1.bean.Forum;
import com.gnaix.app.s1.bean.ForumTopicBean;
import com.gnaix.app.s1.bean.Topic;
import com.gnaix.app.s1.nav.NavigationManager;
import com.gnaix.app.s1.service.Stage1ApiClient;
import com.gnaix.app.s1.service.Stage1ApiClient.Result;
import com.gnaix.common.ui.PullRefreshListView;
import com.gnaix.common.ui.PullRefreshListView.OnPullListener;

public class ForumTopicListFragment extends PageFragment implements OnItemClickListener, Stage1ApiClient.ClientCallback, OnPullListener {

    private PullRefreshListView mListView;
    private TopicListAdapter mTopicListAdapter;
    private int currentPage = 1;
    private TopicDetialFragment mTopicDetialFragment;
    private int taskIDRefresh;
    private int taskIDLoadMore;
    private ArrayList<Topic> mTopicList;
    private LoginFragment mLoginFragment;
    private String formhash;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTopicDetialFragment = new TopicDetialFragment();
        mTopicList = new ArrayList<Topic>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTopicList != null) {
            mTopicList.clear();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mTopicDetialFragment.setArgument("TOPIC", mTopicListAdapter.getItem(position));
        mTopicDetialFragment.setArgument("PAGE", 1);
        mTopicDetialFragment.setArgument("FORUM", getArguments().getParcelable("FORUM"));
        mTopicDetialFragment.setRefreshRequired(true);
        mNavigationManager.showPage(NavigationManager.PAGE_TOPIC_DETAIL, mTopicDetialFragment);
    }

    public OnTopicClickerListener mOnTopicClickerListener;

    public void setOnTopicClickerListener(OnTopicClickerListener listener) {
        mOnTopicClickerListener = listener;
    }

    public interface OnTopicClickerListener {
        void onItemClick(Topic topic, int position);
    }

    @Override
    public void onRequestFinish(Result result) {
        hideLoadingIndicator();
        if(result.statueCode == Stage1ApiClient.SC_QEQUEST_SUCCESS) {
            if(result.apiCode == Stage1ApiClient.API_REQUEST_FORUM_TOPIC_LIST || result.apiCode == Stage1ApiClient.API_REQUEST_HOT_TOPIC_LIST) {
                ForumTopicBean forumTopicBean = (ForumTopicBean) result.mData;
                formhash = forumTopicBean.Variables.formhash;
                if (result.messageID == taskIDRefresh) {
                    mTopicList.clear();
                    mListView.completeRefresh();
                }else if(result.messageID == taskIDLoadMore) {
                    currentPage++;
                    mListView.completeLoadMore();
                }
                mTopicList.addAll(forumTopicBean.Variables.forum_threadlist);
                if (mTopicList.size() > 0) {
                    setRefreshRequired(false);
                }
                mTopicListAdapter.notifyDataSetChanged();
            }
        }else {
            if(result.messageID == taskIDRefresh) {
                showErrorIndicator(result.message);
            }
        }
    }

    @Override
    public void onRequestCancel(Result result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRequestProgress(int i) {
        // TODO Auto-generated method stub

    }

    @Override
    public void bindViews() {
        setHasOptionsMenu(true);
        rebindActionBar();
        mTopicListAdapter = new TopicListAdapter(getActivity());
        mTopicListAdapter.setTopicList(mTopicList);
        mListView = (PullRefreshListView) findViewById(R.id.topic_list);
        mListView.setFooterLayout(R.layout.pull_refresh_listview_footer);
        mListView.setOnPullListener(this);
        mListView.setAdapter(mTopicListAdapter);
        mListView.setOnItemClickListener(this);
        mTopicListAdapter.notifyDataSetChanged();

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_topic_list;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.topic_list_fragment_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (getPageFragmentHost().getHostActionBarDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }
        if (item.getItemId() == R.id.action_login) {
            mLoginFragment = new LoginFragment();
            mLoginFragment.setRefreshRequired(false);
            mNavigationManager.showPage(NavigationManager.PAGE_LOGIN, mLoginFragment);
            return true;
        }
        if (item.getItemId() == R.id.action_post) {
            Toast.makeText(getActivity(), "发帖", Toast.LENGTH_SHORT).show();
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refresh() {
        showLoadingIndicator();
        Forum forum = getArguments().getParcelable("FORUM");
        if (forum == null || Constants.ID_HOT_TOPIC_FROUM == forum.getFid()) {
            taskIDRefresh = mPageFragmentHost.getS1Api().getHotTopic(this,currentPage=1);
        } else {
            taskIDRefresh = mPageFragmentHost.getS1Api().getForumTopic(this,forum.getFid(), currentPage=1);
        }
    }

    @Override
    public void rebindActionBar() {
        Forum forum = getArguments().getParcelable("FORUM");
        if (forum == null) {
            getPageFragmentHost().getHostActionBar().setTitle(getText(R.string.text_hot_threads));
        } else {
            getPageFragmentHost().getHostActionBar().setTitle(forum.getName());
        }
        getPageFragmentHost().getHostActionBar().setHomeButtonEnabled(true);
        getPageFragmentHost().getHostActionBar().setDisplayShowHomeEnabled(true);
        getPageFragmentHost().getHostActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        getPageFragmentHost().getHostDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onRetry() {
        refresh();
    }

    @Override
    public void onPullRefresh() {
        refresh();        
    }

    @Override
    public void onLoadMore() {
        Forum forum = getArguments().getParcelable("FORUM");
        if (forum == null || Constants.ID_HOT_TOPIC_FROUM == forum.getFid()) {
            taskIDLoadMore = mPageFragmentHost.getS1Api().getHotTopic(this,currentPage+1);
        } else {
            taskIDLoadMore = mPageFragmentHost.getS1Api().getForumTopic(this,forum.getFid(), currentPage+1);
        }
    }

}
