package com.gnaix.app.s1.nav;

import android.os.Parcel;
import android.os.Parcelable;

public class NavigationState implements Parcelable {
    public final String backstackName;
    public final int pageType;

    public NavigationState(int pageType) {
        this(pageType, Integer.toString((int) (2147483646.0D * Math.random())));
    }

    private NavigationState(int pageType, String backstackName) {
        this.pageType = pageType;
        this.backstackName = backstackName;
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "[type: " + this.pageType + ", name: " + this.backstackName + "]";
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pageType);
        dest.writeString(backstackName);
    }

    public static final Parcelable.Creator<NavigationState> CREATOR = new Parcelable.Creator<NavigationState>() {
        public NavigationState createFromParcel(Parcel in) {
            return new NavigationState(in.readInt(), in.readString());
        }

        public NavigationState[] newArray(int size) {
            return new NavigationState[size];
        }
    };
}
