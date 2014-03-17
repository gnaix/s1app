package com.gnaix.app.s1.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Auth implements Parcelable {
    private String cookiepre;
    private String auth;
    private String saltkey;
    private String formhash;
    private String sid;

    private Auth(Parcel in) {
        cookiepre = in.readString();
        auth = in.readString();
        saltkey = in.readString();
        formhash = in.readString();
        sid = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cookiepre);
        dest.writeString(auth);
        dest.writeString(saltkey);
        dest.writeString(formhash);
        dest.writeString(sid);
    }

    public static final Parcelable.Creator<Auth> CREATOR = new Parcelable.Creator<Auth>() {
        public Auth createFromParcel(Parcel in) {
            return new Auth(in);
        }

        public Auth[] newArray(int size) {
            return new Auth[size];
        }
    };

    public String getCookiepre() {
        return cookiepre;
    }

    public void setCookiepre(String cookiepre) {
        this.cookiepre = cookiepre;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getSaltkey() {
        return saltkey;
    }

    public void setSaltkey(String saltkey) {
        this.saltkey = saltkey;
    }

    public String getFormhash() {
        return formhash;
    }

    public void setFormhash(String formhash) {
        this.formhash = formhash;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
