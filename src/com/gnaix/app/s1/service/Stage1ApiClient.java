package com.gnaix.app.s1.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.gnaix.app.s1.Constants;
import com.gnaix.app.s1.R;
import com.gnaix.app.s1.bean.ForumTopicBean;
import com.gnaix.app.s1.bean.HotTopicBean;
import com.gnaix.app.s1.bean.Post;
import com.gnaix.app.s1.bean.Topic;
import com.gnaix.app.s1.bean.TopicPostBean;
import com.gnaix.common.ui.AutoDismissFragmentDialog;
import com.gnaix.common.ui.AutoDismissFragmentDialog.AutoDismissListener;
import com.gnaix.common.util.NetworkUtil;
import com.google.gson.Gson;

public class Stage1ApiClient {
    public static final int SC_NETWORK_DISCONNECT = -10;
    public static final int SC_QEQUEST_FAIL = -11;
    public static final int SC_ILLEGAL_ARGUMENT = -12;
    public static final int SC_QEQUEST_SUCCESS = 200;

    /**
     * 获取热门主题
     */
    public static final int API_REQUEST_HOT_TOPIC_LIST = 1;
    /**
     * 获取主题列表
     */
    public static final int API_REQUEST_FORUM_TOPIC_LIST = 2;
    /**
     * 获取回帖列表
     */
    public static final int API_REQUEST_TOPIC_POST_LIST = 3;

    private final AtomicInteger mCount = new AtomicInteger(1);
    private AndroidHttpClient mHttpClient;

    private WeakReference<FragmentActivity> mActivityRef;

    public int request(Context context, ClientCallback callback, boolean showDialog,
            String... params) {
        int id = mCount.getAndIncrement();
        Log.d(Constants.TAG, "current task ID:" + id);
        Stage1AsynTask task = new Stage1AsynTask(context);
        task.setClientCallback(callback);
        task.setShowDialogWhenRequest(showDialog);
        task.execute(params);
        //mQueue.add(task);
        return id;
    }

    /*
         private LinkedList<Stage1AsynTask> mQueue;
         public boolean isClose = false;
         public class WorkThread extends Thread {
            @Override
            public void run() {
                while (!isClose) {
                    synchronized (mQueue) {
                        if (mQueue.size() == 0) {
                            try {
                                mQueue.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Stage1AsynTask task = mQueue.getFirst();
                        task.execute(task.params);
                    }
                }
            }
        }*/

    public Stage1ApiClient(FragmentActivity activity) {
        this.mActivityRef = new WeakReference<FragmentActivity>(activity);
        mHttpClient = AndroidHttpClient.newInstance("Stage1st Client",
                activity.getApplicationContext());
        //mQueue = new LinkedList<Stage1AsynTask>();
    }

    public interface ClientCallback {
        void onRequestFinish(Result result);
        
        void onRequestProgress(int i);
        /**
         * @param result
         *            The result, can be null
         */
        void onRequestCancel(Result result);

    }

    private class Stage1AsynTask extends AsyncTask<String, Integer, Result> implements
            AutoDismissListener {

        private Context mContext;
        private int mDelay = 100;//默认每个任务延迟100毫秒启动

        private AutoDismissFragmentDialog mDialog;
        private ClientCallback mClientCallback;
        private boolean showDialogWhenRequest;

        public int mTaskID;

        public void setTaskID(int tasKID) {
            this.mTaskID = tasKID;
        }

        /**
         * 设置任务延迟启动的毫秒数
         * 
         * @param second
         */
        public void setDelay(int milliseconds) {
            mDelay = milliseconds;
        }

        public Stage1AsynTask(Context context) {
            mContext = context.getApplicationContext();
        }

        public ArrayList<Topic> getForumTopic(int fid, int page) throws IOException {
            Gson gson = new Gson();
            HttpPost request = new HttpPost(Constants.SERVER_BASE + Constants.URI_TOPIC_LIST);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("fid", String.valueOf(fid)));
            params.add(new BasicNameValuePair("tpp", String.valueOf(Constants.PAGE_SIZE)));
            params.add(new BasicNameValuePair("page", String.valueOf(page)));
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            AndroidHttpClient.modifyRequestToAcceptGzipResponse(request);
            InputStream in = null;
            try {
                HttpResponse response = mHttpClient.execute(request);
                if (response != null && response.getStatusLine() != null
                        && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                    in = new BufferedInputStream(AndroidHttpClient.getUngzippedContent(response
                            .getEntity()));
                    ForumTopicBean bean = gson.fromJson(new InputStreamReader(in),
                            ForumTopicBean.class);
                    return bean.Variables.forum_threadlist;
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            return null;
        }

        /**
         * @param tid
         * @param page
         * @return
         * @throws IOException
         */
        public ArrayList<Post> getTopicPost(int tid, int page) throws IOException {
            Gson gson = new Gson();
            HttpPost request = new HttpPost(Constants.SERVER_BASE + Constants.URI_POST_LIST);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("tid", String.valueOf(tid)));
            params.add(new BasicNameValuePair("ppp", String.valueOf(Constants.PAGE_SIZE)));
            params.add(new BasicNameValuePair("page", String.valueOf(page)));
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            AndroidHttpClient.modifyRequestToAcceptGzipResponse(request);
            InputStream in = null;
            try {
                HttpResponse response = mHttpClient.execute(request);
                if (response != null && response.getStatusLine() != null
                        && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                    in = new BufferedInputStream(AndroidHttpClient.getUngzippedContent(response
                            .getEntity()));
                    TopicPostBean bean = gson.fromJson(new InputStreamReader(in),
                            TopicPostBean.class);
                    return bean.Variables.postlist;
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            return null;
        }

        public ArrayList<Topic> getHotTopic() throws IOException {
            Gson gson = new Gson();
            HttpGet request = new HttpGet(Constants.SERVER_BASE + Constants.URI_HOT_TOPIC_LIST);
            AndroidHttpClient.modifyRequestToAcceptGzipResponse(request);
            InputStream in = null;
            try {
                HttpResponse response = mHttpClient.execute(request);
                if (response != null && response.getStatusLine() != null
                        && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                    in = new BufferedInputStream(AndroidHttpClient.getUngzippedContent(response
                            .getEntity()));
                    HotTopicBean bean = gson
                            .fromJson(new InputStreamReader(in), HotTopicBean.class);
                    return bean.Variables.data;
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            return null;
        }

        @Override
        protected Result doInBackground(String... params) {
            if (mDelay >= 0) {
                try {
                    Thread.sleep(mDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int apiCode = Integer.parseInt(params[0]);
            Result result = new Result();
            result.messageID = mTaskID;
            if (!NetworkUtil.isNetworkConnected(mContext)) {
                fillFailedResult(apiCode, "network is not connected", SC_NETWORK_DISCONNECT, result);
                return result;
            }
            switch (apiCode) {
            case API_REQUEST_FORUM_TOPIC_LIST:
                try {
                    int fid = Integer.parseInt(params[1]);
                    int page = Integer.parseInt(params[2]);
                    fillSuccessResult(apiCode, getForumTopic(fid, page), result);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    fillFailedResult(apiCode, e.getMessage(), SC_ILLEGAL_ARGUMENT, result);
                } catch (IOException e) {
                    e.printStackTrace();
                    fillFailedResult(apiCode, e.getMessage(), SC_QEQUEST_FAIL, result);
                }
                break;
            case API_REQUEST_HOT_TOPIC_LIST:
                try {
                    fillSuccessResult(apiCode, getHotTopic(), result);
                } catch (IOException e) {
                    e.printStackTrace();
                    fillFailedResult(apiCode, e.getMessage(), SC_QEQUEST_FAIL, result);
                }
                break;
            case API_REQUEST_TOPIC_POST_LIST:
                try {
                    int tid = Integer.parseInt(params[1]);
                    int page = Integer.parseInt(params[2]);
                    fillSuccessResult(apiCode, getTopicPost(tid, page), result);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    fillFailedResult(apiCode, e.getMessage(), SC_ILLEGAL_ARGUMENT, result);
                } catch (IOException e) {
                    e.printStackTrace();
                    fillFailedResult(apiCode, e.getMessage(), SC_QEQUEST_FAIL, result);
                }
                break;
            }
            return result;
        }

        private void fillFailedResult(int apiCode, String msg, int stateCode, Result result) {
            publishProgress(stateCode);
            result.apiCode = apiCode;
            result.statueCode = stateCode;
            result.message = msg;
        }

        public void fillSuccessResult(int apiCode, Object data, Result result) {
            result.apiCode = apiCode;
            result.mData = data;
            result.statueCode = SC_QEQUEST_SUCCESS;
        }

        @Override
        protected void onPostExecute(Result result) {
            FragmentActivity activity = mActivityRef.get();
            if (mDialog != null && activity != null && !activity.isFinishing()) {
                mDialog.dismiss();
            }
            if (getClientCallback() != null) {
                getClientCallback().onRequestFinish(result);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (getClientCallback() != null) {
                getClientCallback().onRequestProgress(values[0]);
            }
        }

        @Override
        protected void onCancelled(Result result) {
            FragmentActivity activity = mActivityRef.get();
            if (mDialog != null && activity != null && !activity.isFinishing()) {
                mDialog.dismiss();
            }
            if (getClientCallback() != null) {
                getClientCallback().onRequestCancel(result);
            }
        }

        @Override
        protected void onPreExecute() {
            if (isShowDialogWhenRequest()) {
                mDialog = new AutoDismissFragmentDialog();
                mDialog.setAutoDismiss(true);
                mDialog.setAutoDismissListener(this);
                mDialog.setCancelable(false);
                mDialog.setDuration(30000);
                FragmentActivity activity = mActivityRef.get();
                if (activity != null && mDialog != null && !activity.isFinishing()) {
                    mDialog.show(activity.getSupportFragmentManager());
                }
            }
        }

        /**
         * @return the showDialogWhenRequest
         */
        public boolean isShowDialogWhenRequest() {
            return showDialogWhenRequest;
        }

        /**
         * @param showDialogWhenRequest
         *            the showDialogWhenRequest to set
         */
        public void setShowDialogWhenRequest(boolean showDialogWhenRequest) {
            this.showDialogWhenRequest = showDialogWhenRequest;
        }

        /**
         * @return the mClientCallback
         */
        private ClientCallback getClientCallback() {
            return mClientCallback;
        }

        /**
         * @param mClientCallback
         *            the mClientCallback to set
         */
        public final void setClientCallback(ClientCallback mClientCallback) {
            this.mClientCallback = mClientCallback;
        }

        @Override
        public void onAutoDismiss(AutoDismissFragmentDialog dialog) {
            cancel(true);
        }
    }

    public static class Result implements Serializable {
        public int apiCode;
        public int messageID;
        public int statueCode;
        public String message;
        public Object mData;

        public Result() {
            statueCode = SC_QEQUEST_FAIL;
        }
    }

    public void close() {
        mHttpClient.close();
        mActivityRef.clear();
    }
}
