package com.gnaix.app.s1.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gnaix.app.s1.Constants;
import com.gnaix.app.s1.R;
import com.gnaix.app.s1.bean.Forum;
import com.gnaix.app.s1.bean.Post;
import com.gnaix.app.s1.bean.Topic;
import com.gnaix.app.s1.bean.TopicPostBean;
import com.gnaix.app.s1.service.Stage1ApiClient;
import com.gnaix.app.s1.service.Stage1ApiClient.Result;
import com.gnaix.common.ui.PullRefreshListView;
import com.gnaix.common.ui.PullRefreshListView.OnPullListener;

public class TopicDetialFragment extends PageFragment implements Stage1ApiClient.ClientCallback,
        OnPullListener {

    private PullRefreshListView mListView;
    private TextView mSubjectTv;
    private PostListAdapter mPostListAdapter;
    private ArrayList<Post> mPostList;
    private String formhash;
    private EditText inputEt;
    private Button sendBtn;

    private int currentPage = 1;
    private int taskIDRefresh;
    private int taskIDLoadMore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostList = new ArrayList<Post>();
    }

    @Override
    public void rebindActionBar() {
        Forum forum = getArguments().getParcelable("FORUM");
        if (forum != null) {
            getPageFragmentHost().getHostActionBar().setTitle(forum.getName());
        }
        getPageFragmentHost().getHostActionBar().setDisplayHomeAsUpEnabled(true);
        getPageFragmentHost().getHostActionBar().setHomeButtonEnabled(true);
        getPageFragmentHost().getHostActionBar().setDisplayShowHomeEnabled(false);
        getPageFragmentHost().getHostActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPostList != null) {
            mPostList.clear();
        }
    }

    public class PostListAdapter extends BaseAdapter {

        private ArrayList<Post> mPostList;
        private LayoutInflater mLayoutInflater;

        public class PostViewHolder {
            public TextView autherTv, timeTv, contentTv, floorTv;
        }

        public PostListAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        public void setPostList(ArrayList<Post> list) {
            mPostList = list;
        }

        @Override
        public int getCount() {
            return mPostList == null ? 0 : mPostList.size();
        }

        @Override
        public Post getItem(int position) {
            return mPostList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PostViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new PostViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.post_item, null, false);
                viewHolder.floorTv = (TextView) convertView.findViewById(R.id.floorTv);
                viewHolder.contentTv = (TextView) convertView.findViewById(R.id.contentTv);
                viewHolder.timeTv = (TextView) convertView.findViewById(R.id.postTimeTv);
                viewHolder.autherTv = (TextView) convertView.findViewById(R.id.authorNameTv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (PostViewHolder) convertView.getTag();
            }
            Post post = getItem(position);
            viewHolder.floorTv.setText(post.getNumber() + "#");
            viewHolder.autherTv.setText(post.getAuthor());
            viewHolder.timeTv.setText(TopicListAdapter.converTime(post.getDbdateline() * 1000));
            viewHolder.contentTv.setText(Html.fromHtml(post.getMessage()));
            return convertView;
        }

    }

    @Override
    public void onRequestFinish(Result result) {
        hideLoadingIndicator();
        if (result.statueCode == Stage1ApiClient.SC_QEQUEST_SUCCESS) {
            if (result.apiCode == Stage1ApiClient.API_REQUEST_TOPIC_POST_LIST) {
                TopicPostBean topicPostBean = (TopicPostBean) result.mData;
                formhash = topicPostBean.Variables.formhash;
                if (result.messageID == taskIDRefresh) {
                    mPostList.clear();
                    mListView.completeRefresh();
                } else if (result.messageID == taskIDLoadMore) {
                    currentPage++;
                    mListView.completeLoadMore();
                }
                mPostList.addAll(topicPostBean.Variables.postlist);
                if (mPostList.size() > 0) {
                    setRefreshRequired(false);
                }
                Topic topic = getArguments().getParcelable("TOPIC");
                if(mPostList.size()>=topic.getReplies()) {
                    mListView.setLoadMoreable(false);
                }
                mPostListAdapter.notifyDataSetChanged();
            }
        } else {
            if (result.messageID == taskIDRefresh) {
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
        mListView = (PullRefreshListView) findViewById(R.id.post_list);
        mListView.setFooterLayout(R.layout.pull_refresh_listview_footer);
        mListView.setOnPullListener(this);
        mSubjectTv = (TextView) findViewById(R.id.subjectTv);
        mPostListAdapter = new PostListAdapter(getActivity());
        mPostListAdapter.setPostList(mPostList);
        mListView.setAdapter(mPostListAdapter);
        mPostListAdapter.notifyDataSetChanged();
        
        inputEt = (EditText) findViewById(R.id.input);
        sendBtn = (Button) findViewById(R.id.send);
        
        sendBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String text = inputEt.getText().toString();
                if(!TextUtils.isEmpty(text)) {
                    Topic topic = getArguments().getParcelable("TOPIC");
                    getPageFragmentHost().getS1Api().comment(TopicDetialFragment.this, text, topic.getTid(), formhash);
                }
            }
        });
    }

    @Override
    public void refresh() {
        showLoadingIndicator();
        Topic topic = getArguments().getParcelable("TOPIC");
        mSubjectTv.setText(topic.getSubject());
        taskIDRefresh = getPageFragmentHost().getS1Api().getTopicPost(this, topic.getTid(),
                currentPage = 1);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_topic_detail;
    }

    @Override
    public void onPullRefresh() {
        refresh();
    }

    @Override
    public void onLoadMore() {
        Topic topic = getArguments().getParcelable("TOPIC");
        int totalPage = (int) Math.ceil(((float) topic.getReplies()) / Constants.PAGE_SIZE);
        if (currentPage < totalPage && mPostList.size() < topic.getReplies()) {
            taskIDLoadMore = getPageFragmentHost().getS1Api().getTopicPost(this, topic.getTid(),
                    currentPage + 1);
        }
    }

}
