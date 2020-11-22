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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.one.two.three.poster.R;
import com.one.two.three.poster.back.core.Core;

/**
 * Created by Pouyan-PC on 9/1/2017.
 */

public class DialogExportResult extends Dialog{

    private Context mContext;
    private TextView pathTV;
    private TextView txtOpen;
    private TextView txtCancel;
    private LottieAnimationView animationView;

    private String exportedPath;

    public DialogExportResult(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public void setPath(String path) {
        this.exportedPath = path;
    }

    private OnPosterOpenListener listener;

    public static interface OnPosterOpenListener {
        void onOpen(String path);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_export_result);

        getWindow().setLayout(getScreenWidth((Activity) mContext), ViewGroup.LayoutParams.WRAP_CONTENT);

        pathTV = (TextView) findViewById(R.id.dialog_export_result_path_text_view);
        txtOpen = (TextView) findViewById(R.id.txtSavePoster);
        txtCancel = (TextView) findViewById(R.id.txtCancelSave);
        animationView = (LottieAnimationView) findViewById(R.id.animationView);

        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        pathTV.setText(exportedPath);

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogExportResult.this.dismiss();
            }
        });

        txtOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onOpen(exportedPath);
                }
                DialogExportResult.this.dismiss();
            }
        });

        LottieComposition lottieComposition = LottieComposition.Factory.fromFileSync(Core.getInstance().getContext(), "Animations/save_done.json");
        animationView.setComposition(lottieComposition);
        animationView.setScale(2.0f);
    }

    public void setOnOpenListener(OnPosterOpenListener listener){
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

    @Override
    public void show() {
        super.show();

        animationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                animationView.playAnimation();
            }
        },100);

    }

    private int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }
}
