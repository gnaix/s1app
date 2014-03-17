package com.gnaix.app.s1.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.text.TextUtils;

public class BlackList {
    private static final int MAX_SIZE = 100;
    public Set<String> keywords = new HashSet<String>(MAX_SIZE);

    public boolean addAll(ArrayList<String> list) throws IllegalAccessException {
        if (list.size() > MAX_SIZE) {
            throw new IllegalAccessException("list beyond the capacity of the blacklist");
        }
        return keywords.addAll(list);
    }

    public void clear() {
        keywords.clear();
    }

    public boolean isBlock(String content) {
        for (Iterator<String> i = keywords.iterator(); i.hasNext();) {
            if (content.contains(i.next())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param list
     *            the list of keyword to remove.
     * @return true if this set was modified, false otherwise.
     */
    public boolean removeAll(ArrayList<String> list) {
        return keywords.removeAll(list);
    }

    public boolean add(String keyword) throws IllegalAccessException {
        if (!TextUtils.isEmpty(keyword)) {
            if (keywords.size() < MAX_SIZE) {
                return keywords.add(keyword);
            } else {
                throw new IllegalAccessException("blacklist have reach the limitation:" + MAX_SIZE);
            }
        }
        return false;
    }

    public int size() {
        return keywords.size();
    }

    public boolean remove(String keyword) {
        return keywords.remove(keyword);
    }
}
