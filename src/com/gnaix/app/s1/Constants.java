package com.gnaix.app.s1;

public final class Constants {
    public static final String TAG = "Stage1st";
    public static final String SERVER_BASE = "http://bbs.saraba1st.com/2b/";

    public static final String TAG_REQUEST_FORUM = "TAG_REQUEST_FORUM";
    public static final String TAG_REQUEST_TOPIC_LIST = "TAG_REQUEST_TOPIC_LIST";
    public static final String TAG_REQUEST_HOT_TOPIC_LIST = "TAG_REQUEST_HOT_TOPIC_LIST";
    public static final String TAG_REQUEST_POST_LIST = "TAG_REQUEST_POST_LIST";
    
    public static final int PAGE_SIZE = 20;
    public static final int ID_HOT_TOPIC_FROUM = 999;

    public static final String URI_FORUM_LIST = "api/mobile/index.php?mobile=no&version=1&module=forumindex";
    public static final String URI_HOT_TOPIC_LIST = "api/mobile/index.php?mobile=no&version=1&module=hotthread";
    public static final String URI_TOPIC_LIST = "api/mobile/index.php?mobile=no&version=1&module=forumdisplay&submodule=checkpost&orderby=dateline";
    
    public static final String URI_POST_LIST = "api/mobile/index.php?mobile=no&version=1&module=viewthread&submodule=checkpost";
}
