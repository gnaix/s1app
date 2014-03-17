package com.gnaix.app.s1.bean;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {

    private int pid;
    private int tid;
    private int first;
    private String author;
    private int authorid;
    private String message;
    private int anonymous;
    private int attachment;
    private int status;
    private String username;
    private int adminid;
    private int groupid;
    private int memberstatus;
    private int number;//序号
    private long dbdateline;
    
    private ArrayList<String> attachments;
    
    public Post(){setAttachments(new ArrayList<String>());}
    
    private Post(Parcel in) {
        pid = in.readInt();
        tid = in.readInt();
        first = in.readInt();
        author = in.readString();
        authorid = in.readInt();
        message = in.readString();
        anonymous = in.readInt();
        attachment = in.readInt();
        status = in.readInt();
        username = in.readString();
        adminid = in.readInt();
        groupid = in.readInt();
        memberstatus = in.readInt();
        number = in.readInt();
        setDbdateline(in.readInt());
        setAttachments(in.readArrayList(getClass().getClassLoader()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pid);
        dest.writeInt(tid);
        dest.writeInt(first);
        dest.writeString(author);
        dest.writeInt(authorid);
        dest.writeString(message);
        dest.writeInt(anonymous);
        dest.writeInt(attachment);
        dest.writeInt(status);
        dest.writeString(username);
        dest.writeInt(adminid);
        dest.writeInt(groupid);
        dest.writeInt(memberstatus);
        dest.writeInt(number);
        dest.writeLong(getDbdateline());
        dest.writeList(getAttachments());
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(int anonymous) {
        this.anonymous = anonymous;
    }

    public int getAttachment() {
        return attachment;
    }

    public void setAttachment(int attachment) {
        this.attachment = attachment;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAdminid() {
        return adminid;
    }

    public void setAdminid(int adminid) {
        this.adminid = adminid;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public int getMemberstatus() {
        return memberstatus;
    }

    public void setMemberstatus(int memberstatus) {
        this.memberstatus = memberstatus;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ArrayList<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<String> attachments) {
        this.attachments = attachments;
    }

    /**
     * @return the dbdateline
     */
    public long getDbdateline() {
        return dbdateline;
    }

    /**
     * @param dbdateline the dbdateline to set
     */
    public void setDbdateline(long dbdateline) {
        this.dbdateline = dbdateline;
    }
}
