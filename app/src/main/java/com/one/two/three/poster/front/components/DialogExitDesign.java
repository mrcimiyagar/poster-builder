package com.one.two.three.poster.front.components;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.one.two.three.poster.R;


/**
 * Created by Pouyan-PC on 9/1/2017.
 */

public class DialogExitDesign extends Dialog{

    private Context mContext;
    private TextView txtOpen;
    private TextView txtCancel;

    public DialogExitDesign(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    private OnPosterExitListener listener;

    public static interface OnPosterExitListener {
        void onExit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exit_design);

        getWindow().setLayout(getScreenWidth((Activity) mContext), ViewGroup.LayoutParams.WRAP_CONTENT);

        txtOpen = (TextView) findViewById(R.id.txtSavePoster);
        txtCancel = (TextView) findViewById(R.id.txtCancelSave);

        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogExitDesign.this.dismiss();
            }
        });

        txtOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onExit();
                }
                DialogExitDesign.this.dismiss();
            }
        });
    }

    public void setOnExitListener(OnPosterExitListener listener){
        this.listener = listener;
    }

    private int getColor(int colorID){
        return ContextCompat.getColor(mContext, colorID);
    }

    private void animateColor(ImageView img, int startingColor, int endingColor){
        ArgbEvaluator colorEvaluator = new ArgbEvaluator();
        ObjectAnimator colorAnimator = ObjectAnimator.ofObject(img, "colorFilter", colorEvaluator, 0, 0);

        int currentColor = startingColor;
        if (colorAnimator.getAnimatedValue() != null) {
            currentColor = (Integer) colorAnimator.getAnimatedValue();
        }
        colorAnimator.setObjectValues(currentColor, endingColor);
        colorAnimator.setDuration(300);
        colorAnimator.start();
    }

    private int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }
}
