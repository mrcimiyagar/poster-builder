package com.one.two.three.poster.front.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.one.two.three.poster.R;
import com.one.two.three.poster.back.core.Core;

/**
 * Created by Pouyan-PC on 7/25/2017.
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {

    private static final int DEFAULT_STROKE_WIDTH = 0;

    private Context mContext;

    // fields
    private int _strokeColor;
    private float _strokeWidth;
    private boolean isDrawing;

    public CustomTextView(Context context) {
        super(context);
        init(context, null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        mContext = context;
        setTypeface(Core.getInstance().getFont());

        if(attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StrokedTextAttrs);
            _strokeColor = a.getColor(R.styleable.StrokedTextAttrs_textStrokeColor,
                    getCurrentTextColor());
            _strokeWidth = a.getFloat(R.styleable.StrokedTextAttrs_textStrokeWidth,
                    DEFAULT_STROKE_WIDTH);

            a.recycle();
        } else {
            _strokeColor = getCurrentTextColor();
            _strokeWidth = DEFAULT_STROKE_WIDTH;
        }
        setStrokeWidth(_strokeWidth);

    }

    public void setStrokeColor(int color) {
        _strokeColor = color;
        invalidate();
    }

    public void setStrokeWidth(float width) {
        //convert values specified in dp in XML layout to
        //px, otherwise stroke width would appear different
        //on different screens
        _strokeWidth = width;
        invalidate();
    }

//    protected static int spToPx(Context context, float sp) {
//        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
//        return (int) (sp * scale + 0.5f);
//    }

    @Override
    public void invalidate() {
        // Ignore invalidate() calls when isDrawing == true
        // (setTextColor(color) calls will trigger them,
        // creating an infinite loop)
        if(isDrawing) return;
        super.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(_strokeWidth > 0) {
            isDrawing = true;
            //set paint to fill mode
            Paint p = getPaint();
            p.setStyle(Paint.Style.FILL);
            //draw the fill part of text
            super.onDraw(canvas);
            //save the text color
            int currentTextColor = getCurrentTextColor();
            //set paint to stroke mode and specify
            //stroke color and width
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(_strokeWidth);
            p.setAntiAlias(true);
            p.setStrokeJoin(Paint.Join.ROUND);
            setTextColor(_strokeColor);
            //draw text stroke
            super.onDraw(canvas);
            //revert the color back to the one
            //initially specified
            setTextColor(currentTextColor);
            isDrawing = false;
        } else {
            super.onDraw(canvas);
        }
    }

}
