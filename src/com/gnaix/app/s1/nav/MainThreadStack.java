package com.gnaix.app.s1.nav;

import java.util.Stack;

import android.os.Looper;

public class MainThreadStack<T> extends Stack<T> {
    public boolean isEmpty() {
        ensureOnMainThread();
        return super.isEmpty();
    }

    public T peek() {
        ensureOnMainThread();
        return super.peek();
    }

    public T pop() {
        ensureOnMainThread();
        return super.pop();
    }

    public T push(T paramT) {
        ensureOnMainThread();
        return super.push(paramT);
    }
    
    public static void ensureOnMainThread()
    {
      if (Looper.myLooper() == Looper.getMainLooper()) {
        return;
      }
      throw new IllegalStateException("This method must be called from the UI thread.");
    }
    
}