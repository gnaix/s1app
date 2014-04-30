package com.gnaix.app.s1.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ForumTopicBean implements Serializable{
    public Variable Variables;

    public class Variable{
        public String formhash;
        public String auth;
        public String saltkey;
        public String cookiepre;
        public ArrayList<Topic> forum_threadlist;
        public ArrayList<Topic> data;
    }
}
