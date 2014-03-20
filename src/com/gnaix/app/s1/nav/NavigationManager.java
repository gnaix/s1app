package com.gnaix.app.s1.nav;

import java.util.Stack;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.gnaix.app.s1.R;
import com.gnaix.app.s1.activity.MainActivity;
import com.gnaix.app.s1.activity.PageFragment;

public class NavigationManager {
    private MainActivity mActivity;
    private final Stack<NavigationState> mBackStack = new MainThreadStack();
    private FragmentManager mFragmentManager;

    public static final int PAGE_FROUM_TOPIC = 1;
    public static final int PAGE_TOPIC_POST = 2;
    public static final int PAGE_TOPIC_DETAIL = 3;

    public NavigationManager(MainActivity activity) {
        init(activity);
    }

    private void init(MainActivity activity) {
        this.mActivity = activity;
        this.mFragmentManager = this.mActivity.getSupportFragmentManager();
    }

    public void clear() {
        this.mBackStack.removeAllElements();
        while (this.mFragmentManager.getBackStackEntryCount() > 0) {
            this.mFragmentManager.popBackStackImmediate();
        }
    }

    public void goBack() {
        if (!this.mBackStack.isEmpty()) {
            this.mBackStack.pop();
        }
        this.mFragmentManager.popBackStack();
    }

    public void showPage(int pageType, Fragment fragment) {
        showPage(pageType, fragment, false);
    }

    public void showPage(int pageType, Fragment fragment, boolean popPrevious) {
        FragmentTransaction transaction = this.mFragmentManager.beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        if (popPrevious) {
            goBack();
        }
        NavigationState state = new NavigationState(pageType);
        transaction.addToBackStack(state.backstackName);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        this.mBackStack.push(state);
        transaction.commitAllowingStateLoss();
    }

    public PageFragment getActivePage() {
        return (PageFragment) this.mFragmentManager.findFragmentById(R.id.content_frame);
    }

    public boolean canGoBack() {
        return mFragmentManager.getBackStackEntryCount() > 0;
    }

    public void refreshPage() {
        if (this.mBackStack.isEmpty()) {
            return;
        }
        PageFragment pageFragment = getActivePage();
        if (pageFragment != null) {
            if(pageFragment.isAdded()) {
                pageFragment.setRefreshRequired(false);
            }else {
                pageFragment.setRefreshRequired(true);
            }
            pageFragment.refresh();
        }
    }
}
