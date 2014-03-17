package com.gnaix.app.s1.bean;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Topic implements Parcelable{
    
    private int tid;
    private int fid;
    private int readperm;//阅读权限
    private String author;
    private int authorid;
    private String subject;
    private long dbdateline;
    private long dblastpost;
    private String lastposter;
    private int views;//查看数
    private int replies;//回复数
    private int digest;
    private int attachment;
    private ArrayList<Post> posts;
    private boolean readable = true;

    public Topic(){
        setReadable(true);
        posts = new ArrayList<Post>();
        subject = "";
        lastposter = "";
    }
    
    private Topic(Parcel in) {
        tid = in.readInt();
        fid = in.readInt();
        readperm = in.readInt();
        author = in.readString();
        authorid = in.readInt();
        subject = in.readString();
        dbdateline = in.readInt();
        dblastpost = in.readInt();
        lastposter = in.readString();
        views = in.readInt();
        replies = in.readInt();
        digest = in.readInt();
        attachment = in.readInt();
        posts = in.readArrayList(getClass().getClassLoader());
        setReadable(in.readByte() == 1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(tid);
        dest.writeInt(fid);
        dest.writeInt(readperm);
        dest.writeString(author);
        dest.writeInt(authorid);
        dest.writeString(subject);
        dest.writeLong(dbdateline);
        dest.writeLong(dblastpost);
        dest.writeString(lastposter);
        dest.writeInt(views);
        dest.writeInt(replies);
        dest.writeInt(digest);
        dest.writeInt(attachment);
        dest.writeList(posts);
        dest.writeByte((byte) (isReadable()?1:0));
    }
    
    public static final Parcelable.Creator<Topic> CREATOR = new Parcelable.Creator<Topic>() {
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getReadperm() {
        return readperm;
    }

    public void setReadperm(int readperm) {
        this.readperm = readperm;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAuthorid() {
        return authorid;
    }

    public void setAuthorid(int authorid) {
        this.authorid = authorid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public long getDateline() {
        return dbdateline;
    }

    public void setDateline(int dateline) {
        this.dbdateline = dateline;
    }

    public long getLastpost() {
        return dblastpost;
    }

    public void setLastpost(int lastpost) {
        this.dblastpost = lastpost;
    }

    public String getLastposter() {
        return lastposter;
    }

    public void setLastposter(String lastposter) {
        this.lastposter = lastposter;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getReplies() {
        return replies;
    }

    public void setReplies(int replies) {
        this.replies = replies;
    }

    public int getDigest() {
        return digest;
    }

    public void setDigest(int digest) {
        this.digest = digest;
    }

    public int getAttachment() {
        return attachment;
    }

    public void setAttachment(int attachment) {
        this.attachment = attachment;
    }

    public boolean isReadable() {
        return readable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }

}
