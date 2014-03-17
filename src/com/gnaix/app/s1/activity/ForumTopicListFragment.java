package com.gnaix.app.s1.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.gnaix.app.s1.Constants;
import com.gnaix.app.s1.R;
import com.gnaix.app.s1.bean.Forum;
import com.gnaix.app.s1.bean.Topic;
import com.gnaix.app.s1.service.Stage1ApiClient;
import com.gnaix.app.s1.service.Stage1ApiClient.Result;
import com.gnaix.common.ui.AutoDismissFragmentDialog;
import com.gnaix.common.ui.AutoDismissFragmentDialog.AutoDismissListener;

public class ForumTopicListFragment extends Fragment implements AutoDismissListener,
        OnItemClickListener, Stage1ApiClient.ClientCallback {

    private ListView mListView;
    private TopicListAdapter mTopicListAdapter;
    private int currentPage = 1;
    private AutoDismissFragmentDialog mDialog;
    private TopicDetialFragment mTopicDetialFragment;
    private Stage1ApiClient mClient;
    private int taskIDRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTopicDetialFragment = new TopicDetialFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDialog = new AutoDismissFragmentDialog();
        mDialog.setAutoDismiss(true);
        mDialog.setCancelable(false);
        mDialog.setAutoDismissListener(this);
        mTopicListAdapter = new TopicListAdapter(getActivity());
        mListView.setAdapter(mTopicListAdapter);
        mListView.setOnItemClickListener(this);

        MainActivity host = (MainActivity) getActivity();
        mClient = host.getStage1ApiClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_topic_list, container, false);
        mListView = (ListView) contentView.findViewById(R.id.list);
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
        if (mTopicListAdapter != null) {
            mTopicListAdapter.clear();
        }
    }

    @Override
    public void onAutoDismiss(AutoDismissFragmentDialog dialog) {
        Toast.makeText(getActivity().getApplicationContext(), R.string.msg_loading_failed,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!mTopicDetialFragment.isAdded()) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, mTopicDetialFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
        mTopicDetialFragment.request(mTopicListAdapter.getItem(position), 1);
        if (mOnTopicClickerListener != null) {
            // mOnTopicClickerListener.onItemClick(mTopicListAdapter.getItem(position),
            // position);
        }
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
        if (result.statueCode == Stage1ApiClient.SC_QEQUEST_SUCCESS
                && (result.apiCode == Stage1ApiClient.API_REQUEST_FORUM_TOPIC_LIST || result.apiCode == Stage1ApiClient.API_REQUEST_HOT_TOPIC_LIST)) {
            ArrayList<Topic> list = (ArrayList<Topic>) result.mData;
            if (result.messageID == taskIDRefresh) {
                mTopicListAdapter.setTopicList(list);
            } else {
                mTopicListAdapter.addTopics(list);
            }
            mTopicListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestCancel(Result result) {
        // TODO Auto-generated method stub

    }

    public int request(Forum forum, int page) {
        currentPage = page;
        int apiCode = Constants.ID_HOT_TOPIC_FROUM == forum.getFid() ? Stage1ApiClient.API_REQUEST_HOT_TOPIC_LIST
                : Stage1ApiClient.API_REQUEST_FORUM_TOPIC_LIST;
        int id = mClient.request(getActivity(), this, true, String.valueOf(apiCode),
                String.valueOf(forum.getFid()), String.valueOf(currentPage));
        return id;
    }

    @Override
    public void onRequestProgress(int i) {
        // TODO Auto-generated method stub

    }
}
