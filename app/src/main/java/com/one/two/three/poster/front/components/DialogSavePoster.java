package com.one.two.three.poster.front.components;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.one.two.three.poster.R;
import com.one.two.three.poster.back.core.Core;
import com.one.two.three.poster.back.utils.VoteHelper;


/**
 * Created by Pouyan-PC on 9/1/2017.
 */

public class DialogSavePoster extends Dialog {

    public static final int NORMAL_FRAME = 1;
    public static final int BIG_FRAME = 2;

    private int selectedFrame;
    private String posterId;

    private Context mContext;
    private ImageView imgBigFrame;
    private ImageView imgNormalFrame;
    private TextView txtSave;
    private TextView txtCancel;

    private OnPosterSaveListener listener;
    private RatingBar ratingBar;
    private int rate;

    public static interface OnPosterSaveListener {
        void onSave(int frame, int rate);
    }

    // test

    /**
     *
     * @param context Context of application
     * @param defaultFrame Select normal frame {@link #NORMAL_FRAME} or big frame {@link #BIG_FRAME} by default
     */
    public DialogSavePoster(Context context, int defaultFrame){
        super(context);
        this.mContext = context;
        selectedFrame = defaultFrame;

        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    public DialogSavePoster(Context context) {
        this(context, NORMAL_FRAME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_save_poster);

        getWindow().setLayout(getScreenWidth((Activity) mContext), ViewGroup.LayoutParams.WRAP_CONTENT);

        imgBigFrame = findViewById(R.id.imgBigFrame);
        imgNormalFrame = findViewById(R.id.imgNormalFrame);
        txtSave = findViewById(R.id.txtSavePoster);
        txtCancel = findViewById(R.id.txtCancelSave);
        ratingBar = findViewById(R.id.ratingBar);

        final ViewGroup sectionRate = findViewById(R.id.sectionRate);
        VoteHelper voteHelper = new VoteHelper(mContext);
        if(voteHelper.getVotedPosters().contains(posterId) || !Core.getInstance().getNetworkHelper().isNetworkAvailable()){
            sectionRate.setVisibility(View.GONE);
        }else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ratingBar.setRate(5);
                }
            }, 80);
        }

        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSavePoster.this.dismiss();
            }
        });

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sectionRate.getVisibility() == View.GONE)
                    rate = 0;
                else
                    rate = ratingBar.getRate();

                if(listener != null){
                    listener.onSave(selectedFrame, rate);
                }
                DialogSavePoster.this.dismiss();
            }
        });

        imgBigFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBigFrame();
            }
        });

        imgNormalFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNormalFrame();
            }
        });

        if(selectedFrame == NORMAL_FRAME){
            selectedFrame = 0;
            selectNormalFrame();
        }else if(selectedFrame == BIG_FRAME){
            selectedFrame = 0;
            selectBigFrame();
        }

    }

    public void setOnSaveListener(OnPosterSaveListener listener){
        this.listener = listener;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public void selectBigFrame(){
        if(selectedFrame == BIG_FRAME)
            return;
        animateColor(imgBigFrame, getColor(R.color.colorPrimaryDark), getColor(R.color.colorHomePink));
        animateColor(imgNormalFrame, getColor(R.color.colorHomePink), getColor(R.color.colorPrimaryDark));
        selectedFrame = BIG_FRAME;
    }

    public void selectNormalFrame(){
        if(selectedFrame == NORMAL_FRAME)
            return;
        animateColor(imgBigFrame, getColor(R.color.colorHomePink), getColor(R.color.colorPrimaryDark));
        animateColor(imgNormalFrame, getColor(R.color.colorPrimaryDark), getColor(R.color.colorHomePink));
        selectedFrame = NORMAL_FRAME;
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
