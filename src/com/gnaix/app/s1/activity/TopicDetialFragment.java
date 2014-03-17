package com.gnaix.app.s1.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gnaix.app.s1.R;
import com.gnaix.app.s1.bean.Post;
import com.gnaix.app.s1.bean.Topic;
import com.gnaix.app.s1.service.Stage1ApiClient;
import com.gnaix.app.s1.service.Stage1ApiClient.Result;
import com.gnaix.common.ui.AutoDismissFragmentDialog;
import com.gnaix.common.ui.AutoDismissFragmentDialog.AutoDismissListener;

public class TopicDetialFragment extends Fragment implements AutoDismissListener ,Stage1ApiClient.ClientCallback {

    private ListView mListView;
    private TextView mSubjectTv;
    private AutoDismissFragmentDialog mDialog;
    private PostListAdapter mPostListAdapter;
    
    private int currentPage =1;
    private Stage1ApiClient mClient;
    private int taskIDRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDialog = new AutoDismissFragmentDialog();
        mDialog.setAutoDismiss(true);
        mDialog.setCancelable(false);
        mDialog.setAutoDismissListener(this);
        MainActivity host = (MainActivity) getActivity();
        mClient = host.getStage1ApiClient();
        mPostListAdapter = new PostListAdapter(getActivity());
        mListView.setAdapter(mPostListAdapter);
        
        Bundle args = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_topic_detail, container, false);
        mListView = (ListView) contentView.findViewById(R.id.list);
        mSubjectTv = (TextView) contentView.findViewById(R.id.subjectTv);
        return contentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.thread_list_fragment_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPostListAdapter != null) {
            mPostListAdapter.clear();
        }
    }

    public int request(Topic topic, int page) {
        mSubjectTv.setText(topic.getSubject());
        currentPage = page;
        int id = mClient.request(getActivity(), this, true,
                String.valueOf(Stage1ApiClient.API_REQUEST_TOPIC_POST_LIST), String.valueOf(topic.getTid()),
                String.valueOf(currentPage));
        return id;
    }

    @Override
    public void onAutoDismiss(AutoDismissFragmentDialog dialog) {
        Toast.makeText(getActivity().getApplicationContext(), R.string.msg_loading_failed, Toast.LENGTH_SHORT).show();
    }

    public class PostListAdapter extends BaseAdapter {

        private ArrayList<Post> mPostList;
        private LayoutInflater mLayoutInflater;

        public class PostViewHolder {
            public TextView autherTv, timeTv, contentTv,floorTv;
        }

        public PostListAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
            mPostList = new ArrayList<Post>();
        }

        public void setPostList(ArrayList<Post> list) {
            if (list != null) {
                mPostList.clear();
                mPostList.addAll(list);
                notifyDataSetChanged();
            }
        }

        public void addPosts(ArrayList<Post> list) {
            if (list != null) {
                mPostList.addAll(list);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            mPostList.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mPostList.size();
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
        if (result.statueCode == Stage1ApiClient.SC_QEQUEST_SUCCESS
                && result.apiCode == Stage1ApiClient.API_REQUEST_TOPIC_POST_LIST) {
            ArrayList<Post> list = (ArrayList<Post>) result.mData;
            if (result.messageID == taskIDRefresh) {
                mPostListAdapter.setPostList(list);
            } else {
                mPostListAdapter.addPosts(list);
            }
            mPostListAdapter.notifyDataSetChanged();
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
}
