package com.one.two.three.poster.front.components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.one.two.three.poster.R;
import com.one.two.three.poster.back.core.Core;

/**
 * Created by Pouyan-PC on 9/1/2017.
 */

public class DialogNoInternet extends Dialog{

    private Context mContext;
    private LottieAnimationView animationView;
    private TextView txtDialogOk;

    public DialogNoInternet(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_no_internet);

        getWindow().setLayout(getScreenWidth((Activity) mContext), ViewGroup.LayoutParams.WRAP_CONTENT);

        txtDialogOk = (TextView) findViewById(R.id.txtDialogOk);
        animationView = (LottieAnimationView) findViewById(R.id.animationView);
        LottieComposition lottieComposition = LottieComposition.Factory.fromFileSync(Core.getInstance().getContext(), "Animations/no_internet.json");
        animationView.setComposition(lottieComposition);
        animationView.loop(true);
        animationView.setScale(2.0f);
        animationView.playAnimation();

        txtDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    private int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

}
