package com.gnaix.app.s1.bean;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Group implements Parcelable {
    private String name;
    private ArrayList<Forum> forums;

    public Group(){
        setForums(new ArrayList<Forum>());
    }
    
    private Group(Parcel in) {
        setName(in.readString());
        setForums(in.readArrayList(getClass().getClassLoader()));
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());        
        dest.writeTypedList(getForums());
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Forum> getForums() {
        return forums;
    }

    public void setForums(ArrayList<Forum> forums) {
        this.forums = forums;
    }

    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

}
