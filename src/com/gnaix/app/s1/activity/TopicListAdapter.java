package com.gnaix.app.s1.activity;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gnaix.app.s1.Constants;
import com.gnaix.app.s1.R;
import com.gnaix.app.s1.bean.Topic;
import com.gnaix.common.util.TimeUtil;

public class TopicListAdapter extends BaseAdapter {

    private ArrayList<Topic> mTopicList;
    private LayoutInflater mLayoutInflater;

    public class TopicViewHolder {
        public TextView autherTv, timeTv, subjectTv, lastPostTv, readTv, replyTv;
    }

    public TopicListAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setTopicList(ArrayList<Topic> list) {
        mTopicList = list;
    }

    @Override
    public int getCount() {
        return mTopicList == null?0:mTopicList.size();
    }

    @Override
    public Topic getItem(int position) {
        return mTopicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TopicViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new TopicViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.topic_item, null, false);
            viewHolder.subjectTv = (TextView) convertView.findViewById(R.id.subjectTv);
            viewHolder.readTv = (TextView) convertView.findViewById(R.id.readTv);
            viewHolder.lastPostTv = (TextView) convertView.findViewById(R.id.lastPostTv);
            viewHolder.replyTv = (TextView) convertView.findViewById(R.id.replyTv);
            viewHolder.timeTv = (TextView) convertView.findViewById(R.id.postTimeTv);
            viewHolder.autherTv = (TextView) convertView.findViewById(R.id.authorNameTv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TopicViewHolder) convertView.getTag();
        }
        Topic topic = getItem(position);
        viewHolder.autherTv.setText(topic.getAuthor());
        viewHolder.readTv.setText(String.format("浏览(%1$d)", topic.getViews()));
        viewHolder.replyTv.setText(String.format("回复(%1$d)", topic.getReplies()));
        viewHolder.timeTv.setText(converTime(topic.getDateline() * 1000));
        String lastText = String.format("最后回复(%1$s %2$s)", topic.getLastposter(),
                converTime(topic.getLastpost() * 1000));
        viewHolder.lastPostTv.setText(lastText);
        lastText = null;
        viewHolder.subjectTv.setText(topic.getSubject());
        return convertView;
    }

    public static String converTime(long timestamp) {
        long currentmillSeconds = System.currentTimeMillis();
        long timeGap = (currentmillSeconds - timestamp) / 1000;// 与现在时间相差秒数

        Calendar current = Calendar.getInstance();
        Calendar past = Calendar.getInstance();
        past.setTimeInMillis(timestamp);

        int curY = current.get(Calendar.YEAR);
        int curM = current.get(Calendar.MONTH);
        int curD = current.get(Calendar.DAY_OF_MONTH);

        int pastY = past.get(Calendar.YEAR);
        int pastM = past.get(Calendar.MONTH);
        int pastD = past.get(Calendar.DAY_OF_MONTH);

        if (curY == pastY && curM == pastM && curD == pastD) {
            if (timeGap < 60) {
                return "刚刚";
            }
            if (timeGap >= 60 && timeGap < 3600) {
                return timeGap / 60 + "分钟前";
            }
            if (timeGap >= 3600 && timeGap < 24 * 3600) {
                return timeGap / 3600 + "小时前";
            }
        }
        if (curY == pastY && curM == pastM) {
            if(curD - pastD == 1){
                return TimeUtil.getStandardTime(timestamp, "昨天 HH:mm");
            }else
            if(curD - pastD == 2){
                return TimeUtil.getStandardTime(timestamp, "前天 HH:mm");
            }else{
                return curD - pastD + "天前";
            }
        }
        if(curY == pastY){
            return curM - pastM + "个月前";
        }else{
            return curY - pastY + "年前";
        }

    }
}