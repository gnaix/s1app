package com.gnaix.app.s1.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private int uid;
    private String username;
    private int status;
    private int adminid;
    private int credits;//积分
    private String lastvisit;//最后访问时间
    private String lastpost;//最后发表时间
    private String regdate;//注册时间
    private int oltime;//在线时间（小时）
    private int posts;//发帖
    private int groupid;
    private String grouptitle;
    private int readaccess;//阅读权限
    
    private User(Parcel in) {
        uid = in.readInt();
        username = in.readString();
        status = in.readInt();
        adminid = in.readInt();
        credits = in.readInt();
        lastvisit = in.readString();
        lastpost = in.readString();
        regdate = in.readString();
        oltime = in.readInt();
        posts = in.readInt();
        groupid = in.readInt();
        grouptitle = in.readString();
        readaccess = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeString(username);
        dest.writeInt(status);
        dest.writeInt(adminid);
        dest.writeInt(credits);
        dest.writeString(lastvisit);
        dest.writeString(lastpost);
        dest.writeString(regdate);
        dest.writeInt(oltime);
        dest.writeInt(posts);
        dest.writeInt(groupid);
        dest.writeString(grouptitle);
        dest.writeInt(readaccess);
    }
    
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAdminid() {
        return adminid;
    }

    public void setAdminid(int adminid) {
        this.adminid = adminid;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getLastvisit() {
        return lastvisit;
    }

    public void setLastvisit(String lastvisit) {
        this.lastvisit = lastvisit;
    }

    public String getLastpost() {
        return lastpost;
    }

    public void setLastpost(String lastpost) {
        this.lastpost = lastpost;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    public int getOltime() {
        return oltime;
    }

    public void setOltime(int oltime) {
        this.oltime = oltime;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public String getGrouptitle() {
        return grouptitle;
    }

    public void setGrouptitle(String grouptitle) {
        this.grouptitle = grouptitle;
    }

    public int getReadaccess() {
        return readaccess;
    }

    public void setReadaccess(int readaccess) {
        this.readaccess = readaccess;
    }

}
