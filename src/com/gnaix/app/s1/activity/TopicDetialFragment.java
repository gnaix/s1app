package com.gnaix.app.s1.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gnaix.app.s1.R;
import com.gnaix.app.s1.bean.Forum;
import com.gnaix.app.s1.bean.Post;
import com.gnaix.app.s1.bean.Topic;
import com.gnaix.app.s1.service.Stage1ApiClient;
import com.gnaix.app.s1.service.Stage1ApiClient.Result;

public class TopicDetialFragment extends PageFragment implements Stage1ApiClient.ClientCallback {

    private ListView mListView;
    private TextView mSubjectTv;
    private PostListAdapter mPostListAdapter;
    private ArrayList<Post> mPostList;
    
    private int currentPage =1;
    private int taskIDRefresh;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostList = new ArrayList<Post>();
    }

    @Override
    public void rebindActionBar() {
        Forum forum = getArguments().getParcelable("FORUM");
        if(forum != null){
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
            public TextView autherTv, timeTv, contentTv,floorTv;
        }

        public PostListAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
        }

        public void setPostList(ArrayList<Post> list) {
            mPostList = list;
        }

        @Override
        public int getCount() {
            return mPostList == null?0:mPostList.size();
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
            viewHolder.floorTv.setText(post.getNumber()+"#");
            viewHolder.autherTv.setText(post.getAuthor());
            viewHolder.timeTv.setText(TopicListAdapter.converTime(post.getDbdateline() * 1000));
            viewHolder.contentTv.setText(Html.fromHtml(post.getMessage()));
            return convertView;
        }

    }

    @Override
    public void onRequestFinish(Result result) {
        hideLoadingIndicator();
        if (result.statueCode == Stage1ApiClient.SC_QEQUEST_SUCCESS
                && result.apiCode == Stage1ApiClient.API_REQUEST_TOPIC_POST_LIST) {
            ArrayList<Post> list = (ArrayList<Post>) result.mData;
            if (result.messageID == taskIDRefresh) {
                mPostList.clear();
            }
            mPostList.addAll(list);
            if(mPostList.size()>0) {
                setRefreshRequired(false);
            }
            mPostListAdapter.notifyDataSetChanged();
        }else {
            showErrorIndicator(result.message);
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
        mListView = (ListView) findViewById(R.id.list);
        mSubjectTv = (TextView) findViewById(R.id.subjectTv);
        mPostListAdapter = new PostListAdapter(getActivity());
        mPostListAdapter.setPostList(mPostList);
        mListView.setAdapter(mPostListAdapter);
        mPostListAdapter.notifyDataSetChanged();
    }

    @Override
    public void refresh() {
        showLoadingIndicator();
        Topic topic = getArguments().getParcelable("TOPIC");
        mSubjectTv.setText(topic.getSubject());
        currentPage = getArguments().getInt("PAGE");
        taskIDRefresh = getPageFragmentHost().getS1Api().request(getActivity(), this,
                String.valueOf(Stage1ApiClient.API_REQUEST_TOPIC_POST_LIST), String.valueOf(topic.getTid()),
                String.valueOf(currentPage));
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_topic_detail;
    }

}
