package com.one.two.three.poster.front.components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.one.two.three.poster.R;
import com.one.two.three.poster.back.core.Core;

/**
 * Created by pouyan on 2/7/18.
 */

public class DialogMessage extends Dialog {

    private Context mContext;
    private TextView txtTitle;
    private TextView txtMessage;
    private TextView txtNegative;
    private TextView txtPositive;
    private LottieAnimationView animationView;

    private View.OnClickListener positiveListener;
    private View.OnClickListener negativeListener;

    public DialogMessage(@NonNull Context context) {
        super(context);
        init(context);
    }

    public DialogMessage(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected DialogMessage(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context mContext){
        this.mContext = mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_message);

        getWindow().setLayout(getScreenWidth((Activity) mContext), ViewGroup.LayoutParams.WRAP_CONTENT);

        animationView = findViewById(R.id.animationView);
        txtTitle = findViewById(R.id.dialogMessageTxtTitle);
        txtMessage = findViewById(R.id.dialogMessageTxtMessage);
        txtNegative = findViewById(R.id.dialogMessageTxtNegative);
        txtPositive = findViewById(R.id.dialogMessageTxtPositive);

        txtPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positiveListener.onClick(v);
                dismiss();
            }
        });

        txtNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                negativeListener.onClick(v);
                dismiss();
            }
        });


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(animationView.getVisibility() == View.VISIBLE)
                    animationView.playAnimation();
            }
        }, 200);

    }

    public void setTitle(String title){
        txtTitle.setText(title);
    }

    public void setMessage(String message){
        txtMessage.setText(message);
    }

    public void setPositiveButton(String label, View.OnClickListener listener){
        if(!label.equals("")){
            txtPositive.setText(label);
            txtPositive.setVisibility(View.VISIBLE);
            positiveListener = listener;
        }
    }

    public void setNegativeButton(String label, View.OnClickListener listener){
        if(!label.equals("")){
            txtNegative.setText(label);
            txtNegative.setVisibility(View.VISIBLE);
            negativeListener = listener;
        }
    }

    public void setAnimation(String fileName){
        LottieComposition lottieComposition = LottieComposition.Factory.fromFileSync(Core.getInstance().getContext(), "Animations/" + fileName);
        animationView.setComposition(lottieComposition);
        animationView.loop(false);
        animationView.setScale(2.0f);

        animationView.setVisibility(View.VISIBLE);
    }

    private int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

}
