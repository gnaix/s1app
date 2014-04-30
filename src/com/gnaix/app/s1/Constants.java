package com.gnaix.app.s1;

public final class Constants {
    public static final String TAG = "Stage1st";
    public static final String SERVER_BASE = "http://bbs.saraba1st.com/2b/";
    
    public static final int PAGE_SIZE = 15;
    public static final int ID_HOT_TOPIC_FROUM = 999;

    public static final String URI_FORUM_LIST = "api/mobile/index.php?mobile=no&version=1&module=forumindex";
    public static final String URI_HOT_TOPIC_LIST = "api/mobile/index.php?mobile=no&version=1&module=hotthread";
    public static final String URI_TOPIC_LIST = "api/mobile/index.php?mobile=no&version=1&module=forumdisplay&submodule=checkpost&orderby=dateline";
    
    public static final String URI_POST_LIST = "api/mobile/index.php?mobile=no&version=1&module=viewthread&submodule=checkpost";

    public static final String URI_LOGIN = "member.php?mod=logging&action=login&loginsubmit=yes&infloat=yes&lssubmit=yes&inajax=1";
    public static final String URI_COMMENT = "api/mobile/index.php?mobile=no&version=1&module=sendreply&replysubmit=yes&noticetrimstr=&mobiletype=2";
    
    public static final String URI_PUBLISH_TOPIC = "";
    public static final String URI_REPLY_TOPIC = "";
    
    public static final int REQUEST_CODE_LOGIN = 1;
}
