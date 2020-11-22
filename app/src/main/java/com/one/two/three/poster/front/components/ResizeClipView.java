package com.one.two.three.poster.front.components;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by keyhan1376 on 2/27/2018.
 */

public class ResizeClipView extends android.support.v7.widget.AppCompatImageView {

    public interface OnDragListener {
        void dragged(float x, float y);
    }

    private float adx, ady;
    private OnDragListener dragListener;

    public void setOnDragListener(OnDragListener dragListener) {
        this.dragListener = dragListener;
    }

    public ResizeClipView(Context context) {
        super(context);
        this.init();
    }

    public ResizeClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public ResizeClipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    public void init() {

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN: {
                        adx = event.getX();
                        ady = event.getY();
                        return true;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        setX(getX() + event.getX() - adx);
                        setY(getY() + event.getX() - adx);
                        dragListener.dragged(getX(), getY());
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
