package com.gnaix.app.s1.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Forum implements Parcelable {

    private int fid;
    private String name;
    private String description;
    
    public Forum() {
        name = "";
        description = "";
    }

    private Forum(Parcel in) {
        fid = in.readInt();
        name = in.readString();
        description = in.readString();
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(fid);
        out.writeString(name);
        out.writeString(description);
    }

    public static final Parcelable.Creator<Forum> CREATOR = new Parcelable.Creator<Forum>() {
        public Forum createFromParcel(Parcel in) {
            return new Forum(in);
        }

        public Forum[] newArray(int size) {
            return new Forum[size];
        }
    };

}
