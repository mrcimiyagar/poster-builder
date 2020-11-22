package com.one.two.three.poster.front.components;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by keyhan1376 on 12/20/2017.
 */

public class FloatingLogoView extends AppCompatImageView {

    private ImageView deleteBtn;
    private ImageView resizeBtn;

    public void attachControlBtns(ImageView closeBtn, ImageView resizeBtn) {
        this.deleteBtn = closeBtn;
        this.resizeBtn = resizeBtn;
    }

    private float pivotX;
    private float pivotY;

    private boolean lockedFull;
    private boolean lockedMini;

    private boolean attachedMenuUp = false;
    private boolean attachedMoveControllerUp = false;

    private int frameContainerWidth;
    private int frameContainerHeight;

    public FloatingLogoView(Context context, int frameContainerWidth, int frameContainerHeight) {
        super(context);
        this.frameContainerWidth = frameContainerWidth;
        this.frameContainerHeight = frameContainerHeight;
        this.init();
    }

    public FloatingLogoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public FloatingLogoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    public interface OnSelectedListener {
        void selected();
    }

    private OnSelectedListener selectCallback;
    public void setOnSelectedListener(OnSelectedListener selectCallback) {
        this.selectCallback = selectCallback;
    }

    public void select() {
        this.selectCallback.selected();
    }

    // ***

    private float adx, ady;

    public void init() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(lockedFull)
                    return false;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        adx = event.getX();
                        ady = event.getY();
                        FloatingLogoView.this.selectCallback.selected();
                        FloatingLogoView.this.moveHorizontally(0);
                        FloatingLogoView.this.moveVertically(0);
                        return true;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (lockedMini)
                            return true;
                        FloatingLogoView.this.moveHorizontally(event.getX() - adx);
                        FloatingLogoView.this.moveVertically(event.getY() - ady);
                        return true;
                    }
                }
                return false;
            }
        });

        this.invalidate();
        this.requestLayout();
    }

    @Override
    public float getPivotX() {
        return pivotX;
    }

    @Override
    public float getPivotY() {
        return pivotY;
    }

    public void setLockedFull(boolean lockedFull) {
        this.lockedFull = lockedFull;
    }
    public boolean isLockedFull() {
        return this.lockedFull;
    }

    public void moveHorizontally(float value) {
        final float poseX = getX() + value;
        if (poseX + getWidth() < frameContainerWidth && poseX >= 0) {
            setX(poseX);
            pivotX = getX() + getMeasuredWidth() / 2;
            if (deleteBtn != null) {
                deleteBtn.setX(getX() + getMeasuredWidth() - deleteBtn.getMeasuredWidth() / 3);
            }
            if (resizeBtn != null) {
                resizeBtn.setX(getX() + getMeasuredWidth() - resizeBtn.getMeasuredWidth() / 3);
            }
        }
    }

    public void moveVertically(float value) {
        final float poseY = getY() + value;
        if (poseY + getMeasuredHeight() < frameContainerHeight && poseY >= 0) {
            setY(poseY);
            if (deleteBtn != null) {
                deleteBtn.setY(getY() - deleteBtn.getMeasuredHeight() * 2 / 3);
            }
            if (resizeBtn != null) {
                resizeBtn.setY(getY() + getMeasuredHeight() - resizeBtn.getMeasuredHeight() / 3);
            }
        }
    }

    private int dpToPx(float dp) {
        return (int)(dp * getResources().getDisplayMetrics().density);
    }
}