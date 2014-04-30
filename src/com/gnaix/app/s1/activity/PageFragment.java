package com.gnaix.app.s1.activity;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gnaix.app.s1.Constants;
import com.gnaix.app.s1.R;
import com.gnaix.app.s1.nav.NavigationManager;
import com.umeng.analytics.MobclickAgent;

public abstract class PageFragment extends Fragment {
    protected Context mContext;
    protected ViewGroup mDataView;
    protected NavigationManager mNavigationManager;
    private boolean mRefreshRequired = false;
    protected boolean mSaveInstanceStateCalled;
    protected PageFragmentHost mPageFragmentHost;
    protected View mloadingIndicator, mErrorIndicator;
    private AnimationDrawable loadingDrawable;
    protected TextView mErrorMsgTv;
    protected Button mRetryButton;

    protected PageFragment() {
        setArguments(new Bundle());
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(Constants.TAG, ((Object)this).getClass().getSimpleName() + " onActivityCreated()");
        if (!(getActivity() instanceof PageFragmentHost)) {
            throw new IllegalAccessError("Host activity must implement PageFragmentHost");
        }
        if ((PageFragmentHost) getActivity() != this.mPageFragmentHost) {
            this.mPageFragmentHost = ((PageFragmentHost) getActivity());
            this.mContext = getActivity();
            this.mNavigationManager = this.mPageFragmentHost.getNavigationManager();
        }
        bindViews();
        this.mSaveInstanceStateCalled = false;
    }
    

    public abstract void bindViews();

    public View findViewById(int id) {
        if (mDataView != null) {
            return mDataView.findViewById(id);
        }
        return null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(Constants.TAG, ((Object)this).getClass().getSimpleName() + " onAttach()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(Constants.TAG, ((Object)this).getClass().getSimpleName() + " onDetach()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSaveInstanceStateCalled = false;
        Log.d(Constants.TAG, ((Object)this).getClass().getSimpleName() + " onCreate()");
    }

    public void onSaveInstanceState(Bundle paramBundle) {
        this.mSaveInstanceStateCalled = true;
        Log.d(Constants.TAG, ((Object)this).getClass().getSimpleName() + " onSaveInstanceState()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(Constants.TAG, ((Object)this).getClass().getSimpleName() + " onResume()");
        MobclickAgent.onPageStart(((Object)this).getClass().getSimpleName());
        this.mSaveInstanceStateCalled = false;
        if (this.isRefreshRequired()) {
            refresh();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(Constants.TAG, ((Object)this).getClass().getSimpleName() + " onCreateView()");
        this.mSaveInstanceStateCalled = false;
        View contentView = inflater.inflate(R.layout.fragment_host, container, false);
        mloadingIndicator = contentView.findViewById(R.id.loading_indicator);
        mloadingIndicator.setVisibility(View.GONE);
        loadingDrawable = (AnimationDrawable) mloadingIndicator.findViewById(R.id.loadingIv).getBackground();
        mErrorIndicator = contentView.findViewById(R.id.page_error_indicator);
        mErrorIndicator.setVisibility(View.GONE);
        mErrorMsgTv = (TextView) mErrorIndicator.findViewById(R.id.error_msg);
        mRetryButton = (Button) mErrorIndicator.findViewById(R.id.retry_button);
        mDataView = (ViewGroup) contentView.findViewById(R.id.page_content);
        View view = inflater.inflate(getLayoutRes(), mDataView, false);
        mDataView.addView(view);
        this.mSaveInstanceStateCalled = false;
        return contentView;
    }

    public void rebindActionBar() {
    }

    /**
     * fragment will call this method in onResume when isRefreshRequired
     */
    public abstract void refresh();

    public void showLoadingIndicator() {
        mloadingIndicator.setVisibility(View.VISIBLE);
        loadingDrawable.start();
        mErrorIndicator.setVisibility(View.GONE);
    }

    public void onRetry() {

    }

    public void showErrorIndicator(String errorMsg) {
        mloadingIndicator.setVisibility(View.GONE);
        loadingDrawable.stop();
        mErrorIndicator.setVisibility(View.VISIBLE);
        mErrorMsgTv.setText(errorMsg);
        mRetryButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onRetry();
            }
        });
    }

    public void hideErrorIndicator() {
        mErrorIndicator.setVisibility(View.GONE);
        mloadingIndicator.setVisibility(View.GONE);
        loadingDrawable.stop();
    }

    public void hideLoadingIndicator() {
        mloadingIndicator.setVisibility(View.GONE);
        loadingDrawable.stop();
        mErrorIndicator.setVisibility(View.GONE);
    }

    /**
     * 设置layout的resource id
     * 
     * @return
     */
    protected abstract int getLayoutRes();

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(((Object)this).getClass().getSimpleName());
    }

    protected void setArgument(String key, Parcelable value) {
        getArguments().putParcelable(key, value);
    }

    protected void setArgument(String key, String value) {
        getArguments().putString(key, value);
    }

    protected void setArgument(String key, boolean value) {
        getArguments().putBoolean(key, value);
    }

    protected void setArgument(String key, int value) {
        getArguments().putInt(key, value);
    }

    protected void setArgument(String key, Serializable value) {
        getArguments().putSerializable(key, value);
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.d(Constants.TAG, ((Object)this).getClass().getSimpleName() + " onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Constants.TAG, ((Object)this).getClass().getSimpleName() + " onDestroy()");
    }

    /**
     * @return the mRefreshRequired
     */
    public boolean isRefreshRequired() {
        return mRefreshRequired;
    }

    /**
     * @param mRefreshRequired
     *            the mRefreshRequired to set
     */
    public void setRefreshRequired(boolean mRefreshRequired) {
        this.mRefreshRequired = mRefreshRequired;
    }

    public PageFragmentHost getPageFragmentHost() {
        return mPageFragmentHost;
    }

}
