package com.gnaix.app.s1.activity;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;

import com.gnaix.app.s1.nav.NavigationManager;
import com.gnaix.app.s1.service.Stage1ApiClient;

public interface PageFragmentHost {
    
    public Stage1ApiClient getS1Api();
    
    public NavigationManager getNavigationManager();
    
    public void showErrorDialog(String title, String message);
    
    public ActionBar getHostActionBar();
    
    public ActionBarDrawerToggle getHostActionBarDrawerToggle();
    
    public DrawerLayout getHostDrawerLayout();
}
