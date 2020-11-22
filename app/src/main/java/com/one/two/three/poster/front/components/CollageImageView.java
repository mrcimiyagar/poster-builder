package com.one.two.three.poster.front.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.one.two.three.poster.front.extras.RotationGestureDetector;

public class CollageImageView extends android.support.v7.widget.AppCompatImageView implements RotationGestureDetector.OnRotationGestureListener {

    private int frameWidth;
    private int frameHeight;

    private Bitmap originalImage;
    private int imageOriginalWidth;
    private int imageOriginalHeight;

    private Bitmap image;

    private float imageX;
    private float imageY;
    private float imageWidth;
    private float imageHeight;

    private Bitmap mask;

    boolean isSelected = false;

    RotationGestureDetector r;

    private double startDistance;

    private CollagePoint startTempPoint;
    private CollageSize startImageSize;

    private float finalAngle = 0;
    private float angle = 0;
    private boolean multiTouch = false;
    private boolean isOnClick = false;

    boolean flipped = false;

    private boolean touchable;

    private OnClickListener onClickListener;

    boolean recentlySelected;

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public CollageImageView(Context context) {
        super(context);
        init();
    }

    public CollageImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CollageImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.touchable = true;
        this.setScaleType(ScaleType.FIT_XY);
        r = new RotationGestureDetector(this);
    }

    public void setFrameDimensions(int frameWidth, int frameHeight) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
    }

    public Bitmap getImage() {
        return this.image;
    }

    public void setImage(Bitmap image, int imageWidth, int imageHeight) {
        this.imageOriginalWidth = imageWidth;
        this.imageOriginalHeight = imageHeight;
        this.originalImage = Bitmap.createScaledBitmap(image, imageWidth, imageHeight, false);

        // ***
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;

        this.imageX = (this.frameWidth - this.imageOriginalWidth) / 2;
        this.imageY = (this.frameHeight - this.imageOriginalHeight) / 2;

        // ***
        this.angle = 0;
        this.finalAngle = 0;
        this.isOnClick = false;
        this.multiTouch = false;
        updateTempImage();
        this.updateImageView();
    }

    public void setMask(Bitmap mask) {
        this.mask = Bitmap.createScaledBitmap(mask, this.frameWidth, this.frameHeight, false);
        if (this.image != null) {
            this.updateImageView();
        }
        else {
            this.semiUpdateImageView();
        }
    }

    public void setTouchable(boolean isTouchable) {
        this.touchable = isTouchable;
    }

    public void rotate90() {
        this.finalAngle += 90;
        this.angle = finalAngle;

        this.updateTempImage();

        this.updateImageView();
    }

    public void mirror() {
        flipped = !flipped;
        Bitmap tempImage = Bitmap.createBitmap(this.imageOriginalWidth, imageOriginalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tempImage);
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1, this.originalImage.getWidth() / 2, this.originalImage.getHeight() / 2);
        canvas.setMatrix(matrix);
        canvas.drawBitmap(this.originalImage, new Rect(0, 0, this.imageOriginalWidth, this.imageOriginalHeight),
                new RectF(0, 0, this.imageOriginalWidth, this.imageOriginalHeight), null);
        this.originalImage = tempImage;
        this.image = Bitmap.createBitmap(this.frameWidth, this.frameHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(this.image);
        canvas2.rotate(finalAngle, (imageX + imageWidth / 2), (this.imageY + this.imageHeight / 2));
        canvas2.drawBitmap(this.originalImage, new Rect(0, 0, this.imageOriginalWidth, this.imageOriginalHeight),
                new RectF(imageX, imageY, imageX + this.imageWidth, imageY + this.imageHeight), null);
        this.updateImageView();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.onClickListener = l;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (this.touchable) {

            if (isSelected && !recentlySelected) {
                r.onTouchEvent(event);
            }

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    startTempPoint = new CollagePoint(event.getX(), event.getY());
                    startImageSize = new CollageSize(imageWidth, imageHeight);
                    if (!isSelected) {
                        if (originalImage != null) {
                            onClickListener.onClick(this);
                            isOnClick = false;
                            recentlySelected = true;
                        } else {
                            isOnClick = true;
                        }
                    }

                    break;
                }
                case MotionEvent.ACTION_POINTER_DOWN: {
                    if (isSelected && !recentlySelected) {
                        this.startDistance = Math.sqrt(Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2));
                        multiTouch = true;
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE: {

                    if (isSelected && !recentlySelected) {

                        if (isOnClick && (Math.abs(event.getX() - startTempPoint.getX()) > 20
                                || Math.abs(event.getY() - startTempPoint.getY()) > 20)) {
                            isOnClick = false;
                        }

                        if (this.originalImage != null) {

                            if (event.getPointerCount() == 1 && !multiTouch) {

                                this.imageX += event.getX() - startTempPoint.getX();
                                this.imageY += event.getY() - startTempPoint.getY();

                                startTempPoint = new CollagePoint(event.getX(), event.getY());

                                this.updateTempImage();

                                this.updateImageView();
                            } else if (event.getPointerCount() > 1) {

                                double newDistance = Math.sqrt(Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2));

                                float ratio = (float) ((startImageSize.getWidth() / imageOriginalWidth) - 1 + (newDistance / startDistance));

                                if (ratio > 3) {
                                    ratio = 3;
                                } else if (ratio < 0.1) {
                                    ratio = 0.1f;
                                }

                                CollageSize oldSize = new CollageSize(this.imageWidth, this.imageHeight);

                                this.imageWidth = (imageOriginalWidth * ratio);
                                this.imageHeight = (imageOriginalHeight * ratio);

                                this.imageX -= (this.imageWidth - oldSize.getWidth()) / 2;
                                this.imageY -= (this.imageHeight - oldSize.getHeight()) / 2;

                                this.updateTempImage();

                                this.updateImageView();
                            }
                        }
                    }

                    break;
                }
                case MotionEvent.ACTION_UP: {

                    if (recentlySelected) {
                        recentlySelected = false;
                    }

                    if (isSelected) {
                        finalAngle = angle;
                        multiTouch = false;
                    } else {
                        if (isOnClick) {
                            onClickListener.onClick(this);
                            isOnClick = false;
                        }
                    }

                    break;
                }
            }

            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void OnRotation(RotationGestureDetector rotationDetector) {
        angle = finalAngle + -rotationDetector.getAngle();
        this.updateImageView();
    }

    private void updateTempImage() {

        this.image = Bitmap.createBitmap(this.frameWidth, this.frameHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.image);
        canvas.rotate(angle, (imageX + imageWidth / 2), (this.imageY + this.imageHeight / 2));
        canvas.drawBitmap(this.originalImage, new Rect(0, 0, this.imageOriginalWidth, this.imageOriginalHeight),
                new RectF(imageX, imageY, imageX + this.imageWidth, imageY + this.imageHeight), null);
    }

    private void updateImageView() {
        if (this.image != null && this.mask != null) {
            final Bitmap result = Bitmap.createBitmap(this.frameWidth, this.frameHeight, Bitmap.Config.ARGB_8888);
            Canvas mCanvas = new Canvas(result);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            mCanvas.drawBitmap(image, 0, 0, null);
            mCanvas.drawBitmap(mask, 0, 0, paint);
            paint.setXfermode(null);
            this.setImageBitmap(result);
        }
    }

    private void semiUpdateImageView() {
        if (this.mask != null) {
            final Bitmap result = Bitmap.createBitmap(this.frameWidth, this.frameHeight, Bitmap.Config.ARGB_8888);
            Canvas mCanvas = new Canvas(result);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            mCanvas.drawColor(Color.BLACK);
            mCanvas.drawBitmap(mask, 0, 0, paint);
            paint.setXfermode(null);
            this.setImageBitmap(result);
        }
    }

    private class CollagePoint {

        private float x;
        private float y;

        float getX() {
            return x;
        }

        float getY() {
            return y;
        }

        CollagePoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private class CollageSize {

        private float width;
        private float height;

        float getWidth() {
            return width;
        }

        float getHeight() {
            return height;
        }

        CollageSize(float width, float height) {
            this.width = width;
            this.height = height;
        }
    }
}