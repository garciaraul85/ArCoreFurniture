package com.example.secondar.gestures;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class CustomGestureDetector extends GestureDetector {

    CustomOnGestureListener mListener;

    public CustomGestureDetector(Context context, CustomOnGestureListener listener) {
        super(context, listener);
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean consume = (mListener != null) && mListener.onTouchEvent(ev);
        return consume || super.onTouchEvent(ev);
    }
}