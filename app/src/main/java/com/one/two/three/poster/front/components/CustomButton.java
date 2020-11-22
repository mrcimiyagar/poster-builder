package com.one.two.three.poster.front.components;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.one.two.three.poster.back.core.Core;

/**
 * Created by Pouyan-PC on 7/25/2017.
 */

public class CustomButton extends AppCompatButton {

    public CustomButton(Context context) {
        super(context);
        init(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        setTypeface(Core.getInstance().getFont());
    }
}