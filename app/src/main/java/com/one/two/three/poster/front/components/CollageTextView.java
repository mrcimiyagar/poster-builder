package com.one.two.three.poster.front.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatTextView;
import android.view.MotionEvent;

public class CollageTextView extends AppCompatTextView {

    private boolean touchable;

    public CollageTextView(Context context) {
        super(context);
        init();
    }

    public CollageTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CollageTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.touchable = true;
    }

    public void setTouchable(boolean isTouchable) {
        this.touchable = isTouchable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.touchable && super.onTouchEvent(event);
    }
}