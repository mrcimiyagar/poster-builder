package com.one.two.three.poster.front.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.one.two.three.poster.back.models.TextBackground;
import com.one.two.three.poster.back.models.TextShadow;
import com.one.two.three.poster.back.models.TextStroke;
import com.one.two.three.poster.back.models.TextStyle;
import com.one.two.three.poster.front.behaviours.ControllerView;

/**
 * Created by keyhan1376 on 12/20/2017.
 */

public class FloatingTextView extends CustomTextView {

    private float pivotX;
    private float pivotY;

    private int baseWidth;
    private int baseHeight;

    private boolean lockedFull;
    private boolean lockedMini;

    private FloatingMenu attachedMenu;
    private ImageView lockBtn;
    private ImageView deleteBtn;
    public void attachMenu(FloatingMenu menu, ImageView lockBtn, ImageView deleteBtn) {
        this.attachedMenu = menu;
        this.lockBtn = lockBtn;
        this.deleteBtn = deleteBtn;
    }
    public void detachMenu() {
        this.attachedMenu = null;
        this.lockBtn = null;
        this.deleteBtn = null;
    }

    private ControllerView attachedControllerView;
    public void attachMoveControlView(ControllerView moveTextView) {
        this.attachedControllerView = moveTextView;
    }
    public void detachMoveControlView() {
        this.attachedControllerView = null;
    }

    private TextBackground textBackground;
    public void setTextBackground(TextBackground textBackground) {
        this.textBackground = textBackground;
        this.init();
    }

    private TextShadow textShadow;
    public void setTextShadow(TextShadow textShadow) {
        this.textShadow = textShadow;
        this.init();
    }

    private TextStroke textStroke;
    public void setTextStroke(TextStroke textStroke) {
        this.textStroke = textStroke;
        this.init();
    }

    private TextStyle textStyle;

    public TextStyle getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
    }

    private int frameContainerHeight = 0;
    private int frameContainerWidth = 0;

    private boolean attachedMenuUp = false;
    private boolean attachedMoveControllerUp = false;

    public FloatingTextView(Context context, int frameContainerWidth, int frameContainerHeight) {
        super(context);
        this.frameContainerWidth = frameContainerWidth;
        this.frameContainerHeight = frameContainerHeight;
        this.init();
    }

    public FloatingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public FloatingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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
    private Paint textBackgroundPaint;
    private RectF textBackgroundRect;

    public int getBaseWidth() {
        return baseWidth;
    }

    public int getBaseHeight() {
        return baseHeight;
    }

    public void init() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(lockedFull)
                    return true;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        adx = event.getX();
                        ady = event.getY();
                        FloatingTextView.this.selectCallback.selected();
                        FloatingTextView.this.moveHorizontally(0);
                        FloatingTextView.this.moveVertically(0);
                        return true;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        if (lockedMini)
                            return true;
                        FloatingTextView.this.moveHorizontally(event.getX() - adx);
                        FloatingTextView.this.moveVertically(event.getY() - ady);
                        return true;
                    }
                }
                return false;
            }
        });

        this.textBackgroundPaint = new Paint();

        if (this.textBackground != null) {
            this.textBackgroundPaint.setColor(this.textBackground.getColor());
        }

        if (this.textShadow != null) {
            int shadowColor = Color.argb(this.textShadow.getAlpha(), Color.red(this.textShadow.getColor())
                    , Color.green(this.textShadow.getColor()), Color.blue(this.textShadow.getColor()));
            this.setShadowLayer(this.textShadow.getRadius(), this.textShadow.getX(), this.textShadow.getY()
                    , shadowColor);
        }

        if (this.textBackground != null) {
            int color = this.textBackground.getColor();
            this.textBackgroundPaint.setColor(Color.argb(this.textBackground.getAlpha(), Color.red(color), Color.green(color), Color.blue(color)));
            this.setPadding(textBackground.getWidth(), textBackground.getHeight(), textBackground.getWidth(), textBackground.getHeight());
            this.textBackgroundRect = new RectF(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
        }

        if(this.textStroke != null) {
            int color = textStroke.getColor();
            int argb = Color.argb(textStroke.getAlpha(), Color.red(color), Color.green(color), Color.blue(color));
            this.setStrokeColor(argb);
            this.setStrokeWidth(textStroke.getWidth());
        }

        this.invalidate();
        this.requestLayout();
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        baseWidth = getMeasuredWidth();
        baseHeight = getMeasuredHeight();
    }

    @Override
    public float getPivotX() {
        return pivotX;
    }

    @Override
    public float getPivotY() {
        return pivotY;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return false;
    }

    public void setLockedFull(boolean lockedFull) {
        this.lockedFull = lockedFull;
    }
    public boolean isLockedFull() {
        return this.lockedFull;
    }

    public void setLockedMini(boolean lockedMini) {
        this.lockedMini = lockedMini;
    }
    public boolean isLockedMini() {
        return this.lockedMini;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (this.textBackground != null) {
            canvas.drawRoundRect(this.textBackgroundRect, this.textBackground.getCorner(), this.textBackground.getCorner(), this.textBackgroundPaint);
        }

        super.onDraw(canvas);
    }

    public void moveHorizontally(float value) {
        final float poseX = getX() + value;
        if (poseX + getWidth() < frameContainerWidth && poseX >= 0) {
            setX(getX() + value);
            pivotX = getX() + getMeasuredWidth() / 2;
            if (deleteBtn != null) {
                deleteBtn.setX(getX() + getMeasuredWidth() - deleteBtn.getMeasuredWidth() / 3);
            }
            if (lockBtn != null) {
                lockBtn.setX(getX() - lockBtn.getMeasuredWidth() * 2 / 3);
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
            if (lockBtn != null) {
                lockBtn.setY(getY() - lockBtn.getMeasuredHeight() * 2 / 3);
            }

            if (this.attachedControllerView != null) {

                float finalAttachedControllerY = getY() - this.attachedControllerView.getControllerHeight() - dpToPx(24);

                if (finalAttachedControllerY < 0) {

                    this.attachedMoveControllerUp = false;
                }
                else {

                    this.attachedMoveControllerUp = true;

                }

                if (this.attachedMoveControllerUp) {

                    finalAttachedControllerY = getY() - this.attachedControllerView.getControllerHeight() - dpToPx(24);
                }
                else {

                    finalAttachedControllerY = getY() + this.getMeasuredHeight();
                }

                ((View) this.attachedControllerView).setY(finalAttachedControllerY);
            }

            // ***

            if (this.attachedMenu != null) {

                float finalAttachedMenuY = getY() + this.getMeasuredHeight();

                if (this.attachedControllerView == null) {

                    if (finalAttachedMenuY + attachedMenu.getMeasuredHeight() > frameContainerHeight) {
                        attachedMenuUp = true;
                    } else {
                        attachedMenuUp = false;
                    }

                    if (attachedMenuUp) {
                        finalAttachedMenuY = getY() - this.attachedMenu.getMeasuredHeight() - dpToPx(24);
                    } else {
                        finalAttachedMenuY = getY() + this.getMeasuredHeight();
                    }
                }
                else {

                    if (this.attachedMoveControllerUp) {

                        if (finalAttachedMenuY + attachedMenu.getMeasuredHeight() > frameContainerHeight) {
                            attachedMenuUp = true;
                        } else {
                            attachedMenuUp = false;
                        }

                        if (attachedMenuUp) {
                            finalAttachedMenuY = getY() - this.attachedControllerView.getControllerHeight()
                                    - this.attachedMenu.getControllerHeight();
                        } else {
                            finalAttachedMenuY = getY() + this.getMeasuredHeight();
                        }
                    }
                    else {

                        if (finalAttachedMenuY + this.attachedControllerView.getControllerHeight()
                                + attachedMenu.getControllerHeight() > frameContainerHeight) {
                            attachedMenuUp = true;
                        } else {
                            attachedMenuUp = false;
                        }

                        if (attachedMenuUp) {
                            finalAttachedMenuY = getY() - this.attachedMenu.getControllerHeight() - dpToPx(24);
                        } else {
                            finalAttachedMenuY = getY() + this.getMeasuredHeight() + this.attachedControllerView
                                    .getControllerHeight();
                        }
                    }
                }

                this.attachedMenu.setY(finalAttachedMenuY);
            }
        }
    }

    private int dpToPx(float dp) {
        return (int)(dp * getResources().getDisplayMetrics().density);
    }
}