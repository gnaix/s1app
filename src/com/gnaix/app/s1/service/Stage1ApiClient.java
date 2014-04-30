package com.gnaix.app.s1.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.gnaix.app.s1.Constants;
import com.gnaix.app.s1.R;
import com.gnaix.app.s1.bean.ForumTopicBean;
import com.gnaix.app.s1.bean.TopicPostBean;
import com.gnaix.common.ui.AutoDismissFragmentDialog;
import com.gnaix.common.ui.AutoDismissFragmentDialog.AutoDismissListener;
import com.google.gson.Gson;

public class Stage1ApiClient {
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

    public static final int API_REQUEST_LOGIN = 4;
    
    public static final int API_REQUEST_TOPIC_COMMENT = 5;
    
    private final AtomicInteger mCount = new AtomicInteger(1);
    private AndroidHttpClient mHttpClient;

    private CookieStore cookieStore;
    private HttpContext mHttpContext;
    private Resources mResource;

    private static final String KEY_API_CODE = "KEY_API_CODE";

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.151 Safari/535.19";

    private WeakReference<FragmentActivity> mActivityRef;

    private int request(ClientCallback callback, Bundle params) {
        int id = mCount.getAndIncrement();
        Log.d(Constants.TAG, "current task ID:" + id);
        Stage1AsynTask task = new Stage1AsynTask(params);
        task.setClientCallback(callback);
        task.setTaskID(id);
        task.execute();
        return id;
    }

    public int login(ClientCallback callback, String username, String password) {
        Bundle params = generateBundle(API_REQUEST_LOGIN);
        params.putString("username", username);
        params.putString("password", password);
        return request(callback, params);
    }
    
    public int comment(ClientCallback callback, String content, int tid,String formhash) {
        Bundle params = generateBundle(API_REQUEST_TOPIC_COMMENT);
        params.putString("content", content);
        params.putString("formhash", formhash);
        params.putInt("tid", tid);
        return request(callback, params);
    }

    public int getHotTopic(ClientCallback callback,int page) {
        Bundle params = generateBundle(API_REQUEST_HOT_TOPIC_LIST);
        params.putInt("page", page);
        return request(callback,params);
    }

    public int getForumTopic(ClientCallback callback, int fid, int page) {
        Bundle params = generateBundle(API_REQUEST_FORUM_TOPIC_LIST);
        params.putInt("fid", fid);
        params.putInt("page", page);
        return request(callback, params);
    }

    public int getTopicPost(ClientCallback callback, int tid, int page) {
        Bundle params = generateBundle(API_REQUEST_TOPIC_POST_LIST);
        params.putInt("tid", tid);
        params.putInt("page", page);
        return request(callback, params);
    }

    private Bundle generateBundle(int apiCode) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_API_CODE, apiCode);
        return bundle;
    }

    public Stage1ApiClient(final FragmentActivity activity) {
        this.mActivityRef = new WeakReference<FragmentActivity>(activity);
        mHttpClient = AndroidHttpClient.newInstance(USER_AGENT, activity.getApplicationContext());
        mResource = activity.getApplicationContext().getResources();
        HttpConnectionParams.setConnectionTimeout(mHttpClient.getParams(), 5000);
        HttpConnectionParams.setSoTimeout(mHttpClient.getParams(), 30000);

        setCookieStore(new BasicCookieStore());
        mHttpContext = new SyncBasicHttpContext(new BasicHttpContext());
        mHttpContext.setAttribute(ClientContext.COOKIE_STORE, getCookieStore());

        new Thread() {
            public void run() {
                loadCookies(activity);
            }
        }.start();
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

    private class Stage1AsynTask extends AsyncTask<Void, Integer, Result> implements
            AutoDismissListener {

        private int mDelay = 100;//默认每个任务延迟100毫秒启动

        //private AutoDismissFragmentDialog mDialog;
        private ClientCallback mClientCallback;

        private int mTaskID;

        private Bundle mParams;

        public Stage1AsynTask(Bundle params) {
            this.mParams = params;
        }

        public void setTaskID(int tasKID) {
            this.mTaskID = tasKID;
        }

        /**
         * 设置任务延迟启动的毫秒数
         * 
         * @param milliseconds
         *            延迟毫秒值
         */
        public void setDelay(int milliseconds) {
            mDelay = milliseconds;
        }

        private ForumTopicBean getForumTopicImpl(int fid, int page) throws Exception {
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
                HttpResponse response = mHttpClient.execute(request, mHttpContext);
                if (response != null && response.getStatusLine() != null
                        && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                    in = new BufferedInputStream(AndroidHttpClient.getUngzippedContent(response
                            .getEntity()));
                    ForumTopicBean bean = gson.fromJson(new InputStreamReader(in),
                            ForumTopicBean.class);
                    return bean;
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            return null;
        }
        
        private boolean commentImpl(String message, int tid,String formhash) throws Exception {
            HttpPost request = new HttpPost(Constants.SERVER_BASE + Constants.URI_COMMENT);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("formhash", formhash));
            params.add(new BasicNameValuePair("message", message));
            params.add(new BasicNameValuePair("tid", String.valueOf(tid)));
            InputStream in = null;
            try {
                request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                AndroidHttpClient.modifyRequestToAcceptGzipResponse(request);
                HttpResponse response = mHttpClient.execute(request, mHttpContext);
                if (response != null && response.getStatusLine() != null
                        && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    in = new BufferedInputStream(AndroidHttpClient.getUngzippedContent(response
                            .getEntity()));
                    String content = convertStreamToString(in);
                    Log.d(Constants.TAG, content);
                    return content.contains("post_reply_succeed");
                }
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }

        private boolean loginImpl(String username, String password) throws Exception {
            HttpPost request = new HttpPost(Constants.SERVER_BASE + Constants.URI_LOGIN);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("fastloginfield", "username"));
            params.add(new BasicNameValuePair("cookietime", "2592000"));
            params.add(new BasicNameValuePair("handlekey", "ls"));
            params.add(new BasicNameValuePair("quickforward", "yes"));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            InputStream in = null;
            try {
                request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                AndroidHttpClient.modifyRequestToAcceptGzipResponse(request);
                HttpResponse response = mHttpClient.execute(request, mHttpContext);
                if (response != null && response.getStatusLine() != null
                        && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    in = new BufferedInputStream(AndroidHttpClient.getUngzippedContent(response
                            .getEntity()));
                    String content = convertStreamToString(in);
                    Log.d(Constants.TAG, content);
                    for (Cookie cookie : cookieStore.getCookies()) {
                        if (cookie.getName().contains("_auth")) {
                            return true;
                        }
                    }
                }
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }

        /**
         * @param tid
         *            topic id
         * @param page
         *            page
         * @return post列表
         * @throws IOException
         */
        private TopicPostBean getTopicPostImpl(int tid, int page) throws Exception {
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
                HttpResponse response = mHttpClient.execute(request, mHttpContext);
                if (response != null && response.getStatusLine() != null
                        && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                    in = new BufferedInputStream(AndroidHttpClient.getUngzippedContent(response
                            .getEntity()));
                    TopicPostBean bean = gson.fromJson(new InputStreamReader(in),
                            TopicPostBean.class);
                    return bean;
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            return null;
        }

        private ForumTopicBean getHotTopicImpl(int page) throws Exception {
            Gson gson = new Gson();
            HttpGet request = new HttpGet(Constants.SERVER_BASE + Constants.URI_HOT_TOPIC_LIST+"&page="+page);
            InputStream in = null;
            try {
                AndroidHttpClient.modifyRequestToAcceptGzipResponse(request);
                HttpResponse response = mHttpClient.execute(request, mHttpContext);
                if (response != null && response.getStatusLine() != null
                        && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                    in = new BufferedInputStream(AndroidHttpClient.getUngzippedContent(response
                            .getEntity()));
                    ForumTopicBean bean = gson
                            .fromJson(new InputStreamReader(in), ForumTopicBean.class);
                    bean.Variables.forum_threadlist = bean.Variables.data;
                    return bean;
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            return null;
        }

        private int getApiCode() {
            return mParams.getInt(KEY_API_CODE);
        }

        @Override
        protected Result doInBackground(Void... params) {
            if (mDelay >= 0) {
                try {
                    Thread.sleep(mDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int apiCode = getApiCode();
            Result result = new Result();
            result.messageID = mTaskID;
            switch (apiCode) {
            case API_REQUEST_FORUM_TOPIC_LIST:
                try {
                    int fid = mParams.getInt("fid");
                    int page = mParams.getInt("page");
                    fillSuccessResult(apiCode, getForumTopicImpl(fid, page), result);
                }catch (Exception e) {
                    e.printStackTrace();
                    fillFailedResult(apiCode, mResource.getString(R.string.msg_failed), SC_QEQUEST_FAIL, result);
                }
                break;
            case API_REQUEST_HOT_TOPIC_LIST:
                try {
                    int page = mParams.getInt("page");
                    fillSuccessResult(apiCode, getHotTopicImpl(page), result);
                }catch (Exception e) {
                    e.printStackTrace();
                    fillFailedResult(apiCode, mResource.getString(R.string.msg_failed), SC_QEQUEST_FAIL, result);
                }
                break;
            case API_REQUEST_TOPIC_COMMENT:
                try {
                    int tid = mParams.getInt("tid");
                    String message = mParams.getString("content");
                    String formhash = mParams.getString("formhash");
                    fillSuccessResult(apiCode, commentImpl(message, tid, formhash), result);
                }catch (Exception e) {
                    e.printStackTrace();
                    fillFailedResult(apiCode, mResource.getString(R.string.msg_failed), SC_QEQUEST_FAIL, result);
                }
                break;                
            case API_REQUEST_TOPIC_POST_LIST:
                try {
                    int tid = mParams.getInt("tid");
                    int page = mParams.getInt("page");
                    fillSuccessResult(apiCode, getTopicPostImpl(tid, page), result);
                }catch (Exception e) {
                    e.printStackTrace();
                    fillFailedResult(apiCode, mResource.getString(R.string.msg_failed), SC_QEQUEST_FAIL, result);
                }
                break;
            case API_REQUEST_LOGIN:
                try {
                    String username = mParams.getString("username");
                    String password = mParams.getString("password");
                    fillSuccessResult(apiCode, loginImpl(username, password), result);
                } catch (Exception e) {
                    e.printStackTrace();
                    fillFailedResult(apiCode, mResource.getString(R.string.msg_failed), SC_QEQUEST_FAIL, result);
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
            /*            FragmentActivity activity = mActivityRef.get();
                        if (mDialog != null && activity != null && !activity.isFinishing()) {
                            mDialog.dismiss();
                        }*/
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
            /*            FragmentActivity activity = mActivityRef.get();
                        if (mDialog != null && activity != null && !activity.isFinishing()) {
                            mDialog.dismiss();
                        }*/
            if (getClientCallback() != null) {
                getClientCallback().onRequestCancel(result);
            }
        }

        @Override
        protected void onPreExecute() {
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
        persistentCookies();
        mHttpClient.close();
        mActivityRef.clear();
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * @return the cookieStore
     */
    public CookieStore getCookieStore() {
        return cookieStore;
    }

    /**
     * @param cookieStore
     *            the cookieStore to set
     */
    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    private void loadCookies(final Context context) {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(context.openFileInput("stage1.auth"));
            String json = convertStreamToString(bis);
            Log.d(Constants.TAG, json);
            JSONArray jsonArray = new JSONArray(json);
            int size = jsonArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject jobj = jsonArray.optJSONObject(i);
                if (jobj != null) {
                    String name = jobj.optString("name");
                    String value = jobj.optString("value");
                    String path = jobj.optString("path");
                    String expiry = jobj.optString("expiry");
                    String domain = jobj.optString("domain");
                    String version = jobj.optString("version");
                    BasicClientCookie cookie = new BasicClientCookie(name, value);
                    cookie.setDomain(domain);
                    cookie.setPath(path);
                    cookie.setValue(version);
                    if (!TextUtils.isEmpty(expiry) && "null".equalsIgnoreCase(expiry)) {
                        SimpleDateFormat format = new SimpleDateFormat(
                                "EEE MMM dd HH:mm:ss zzz yyyy");
                        try {
                            Date aparsed = format.parse(expiry);
                            cookie.setExpiryDate(aparsed);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    getCookieStore().addCookie(cookie);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (Exception e2) {
                }
            }
        }
    }

    private void persistentCookies() {
        FragmentActivity activity = mActivityRef.get();
        if (activity != null) {
            BufferedOutputStream fo = null;
            try {
                List<Cookie> cookies = cookieStore.getCookies();
                JSONArray cookieJsonArray = new JSONArray();
                JSONObject cookieJson = null;
                for (Cookie cookie : cookies) {
                    cookieJson = new JSONObject();
                    try {
                        cookieJson.put("name", cookie.getName());
                        cookieJson.put("value", cookie.getValue());
                        cookieJson.put("path", cookie.getPath());
                        cookieJson.put("domain", cookie.getDomain());
                        cookieJson.put("expiry", cookie.getExpiryDate());
                        cookieJson.put("version", cookie.getVersion());
                        cookieJsonArray.put(cookieJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                fo = new BufferedOutputStream(activity.openFileOutput("stage1.auth",
                        Context.MODE_PRIVATE), 8192);
                String json = cookieJsonArray.toString();
                Log.d(Constants.TAG, json);
                fo.write(json.getBytes("UTF-8"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fo != null) {
                    try {
                        fo.close();
                    } catch (Exception e2) {
                    }
                }
            }
        }
    }

}
