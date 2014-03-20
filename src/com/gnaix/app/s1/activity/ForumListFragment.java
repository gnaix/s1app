package com.gnaix.app.s1.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.gnaix.app.s1.R;
import com.gnaix.app.s1.bean.Forum;
import com.gnaix.app.s1.bean.Group;

public class ForumListFragment extends PageFragment implements OnChildClickListener {
    private ExpandableListView mListView;

    private ForumListAdapter mForumListAdapter;
    private ArrayList<Group> mGroups;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGroups = new ArrayList<Group>();
    }

    private ArrayList<Group> parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.e("ParsingActivity", ">>>>>>>>>>>>>>>>>.. parseXML..................");
        ArrayList<Group> list = new ArrayList<Group>();
        int eventType = parser.getEventType();
        Group group = null;
        Forum forum = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
            case XmlPullParser.START_DOCUMENT:
                break;
            case XmlPullParser.START_TAG:
                String name = parser.getName();
                // Log.e("ParsingActivity", " >>>>>.... Tag name   = " + name);
                if (name.equals("group")) {
                    group = new Group();
                    list.add(group);
                    group.setName(parser.getAttributeValue(null, "name"));
                } else if (name.equals("froum")) {
                    forum = new Forum();
                    forum.setFid(Integer.parseInt(parser.getAttributeValue(null, "id")));
                    forum.setName(parser.getAttributeValue(null, "name"));
                    group.getForums().add(forum);
                }
                break;
            case XmlPullParser.END_TAG:
                break;
            }
            eventType = parser.next();
        }
        return list;
    }

    private void loadFavForum() {
        // TODO Auto-generated method stub
    }

    private class ForumListAdapter extends BaseExpandableListAdapter {
        private LayoutInflater mLayoutInflater;
        private Resources mRes;
        private ArrayList<Group> mGroups;
        private int checkedGroupPosition = ExpandableListView.INVALID_POSITION;
        private int checkedChildPosition = ExpandableListView.INVALID_POSITION;

        public class GroupViewHolder {
            public ImageView indicator;
            public TextView forumTitle;
        }

        public ForumListAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
            mRes = context.getResources();
            mGroups = new ArrayList<Group>();
        }

        @Override
        public int getGroupCount() {
            return mGroups == null ? 0 : mGroups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroups.get(groupPosition).getForums().size();
        }

        @Override
        public Group getGroup(int groupPosition) {
            return mGroups.get(groupPosition);
        }

        @Override
        public Forum getChild(int groupPosition, int childPosition) {
            return mGroups.get(groupPosition).getForums().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        public void setData(ArrayList<Group> list) {
            mGroups.clear();
            mGroups = list;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new GroupViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.forum_group_layout, null, false);
                viewHolder.indicator = (ImageView) convertView.findViewById(R.id.image1);
                viewHolder.forumTitle = (TextView) convertView.findViewById(R.id.text1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GroupViewHolder) convertView.getTag();
            }
            if (isExpanded) {
                viewHolder.indicator.setImageResource(R.drawable.group_indicator_expand);
            } else {
                viewHolder.indicator.setImageResource(R.drawable.group_indicator_collapse);
            }
            viewHolder.forumTitle.setText(getGroup(groupPosition).getName());
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                ViewGroup parent) {
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.forum_item, null, false);
            }
            ((TextView) convertView).setText(getChild(groupPosition, childPosition).getName());
            if (checkedGroupPosition == groupPosition && checkedChildPosition == childPosition) {
                convertView.setBackgroundColor(mRes.getColor(R.color.deep_yellow));
            } else {
                convertView.setBackgroundColor(mRes.getColor(R.color.light_yellow));
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public void setItemSelected(int groupPosition, int childPosition) {
            checkedGroupPosition = groupPosition;
            checkedChildPosition = childPosition;
        }

        public Forum getSelected() {
            if (checkedGroupPosition != ExpandableListView.INVALID_POSITION
                    && checkedChildPosition != ExpandableListView.INVALID_POSITION) {
                return mForumListAdapter.getChild(checkedGroupPosition, checkedChildPosition);
            } else {
                return null;
            }
        }

        public void clear() {
            if (mGroups != null) {
                mGroups.clear();
            }

        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        mForumListAdapter.setItemSelected(groupPosition, childPosition);
        if (mListener != null) {
            mListener.onForumSelected(mForumListAdapter.getChild(groupPosition, childPosition));
        }
        mForumListAdapter.notifyDataSetChanged();
        return true;
    }

    public Forum getSelectedForum() {
        return mForumListAdapter.getSelected();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mForumListAdapter != null) {
            mForumListAdapter.clear();
        }
    }

    public OnForumSelectedListener mListener;

    public void setOnForumSelectedListener(OnForumSelectedListener listener) {
        mListener = listener;
    }

    public interface OnForumSelectedListener {
        public void onForumSelected(Forum forum);
        
        /**
         * 当论坛列表加载完成时，返回第一个论坛
         * @param forum
         */
        public void onLoadFinish(Forum forum);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.forum_list_layout;
    }

    @Override
    public void bindViews() {
        mListView = (ExpandableListView) getView().findViewById(R.id.list);
        mListView.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
        mListView.setOnChildClickListener(this);
        mForumListAdapter = new ForumListAdapter(getActivity());
        mForumListAdapter.setData(mGroups);
        mListView.setAdapter(mForumListAdapter);
    }

    @Override
    public void refresh() {
        new AsyncTask<Void, Void, ArrayList<Group>>() {
            @Override
            protected ArrayList<Group> doInBackground(Void... params) {
                ArrayList<Group> list = null;
                try {
                    XmlPullParserFactory pullParserFactory;
                    pullParserFactory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = pullParserFactory.newPullParser();
                    InputStream in_s = getActivity().getAssets().open("forumtree.xml");
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(in_s, null);
                    list = parseXML(parser);
                    loadFavForum();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return list;
            }

            protected void onPostExecute(ArrayList<Group> result) {
                mGroups.addAll(result);
                if(mGroups.size()>0) {
                    setRefreshRequired(false);
                }
                mForumListAdapter.notifyDataSetChanged();
                if (mListener != null) {
                    mListener.onLoadFinish(mForumListAdapter.getGroup(0).getForums().get(0));
                }
            }
        }.execute();
    }

}
