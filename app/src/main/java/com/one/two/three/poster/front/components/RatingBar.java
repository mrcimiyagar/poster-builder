package com.one.two.three.poster.front.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.one.two.three.poster.R;
import com.one.two.three.poster.back.callbacks.OnRateChangeListener;


/**
 * Created by pouyan on 2/8/18.
 */

public class RatingBar extends LinearLayout {

    private int rate = 1;

    private Context mContext;
    private LayoutInflater inflater;
    private OnRateChangeListener rateListener;
    private ImageView[] stars;

    public RatingBar(Context context) {
        super(context);
        init(context);
    }

    public RatingBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context mContext){
        this.mContext = mContext;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.rating_bar, this, true);

        stars = new ImageView[5];
        stars[0] = view.findViewById(R.id.imgStar1);
        stars[1] = view.findViewById(R.id.imgStar2);
        stars[2] = view.findViewById(R.id.imgStar3);
        stars[3] = view.findViewById(R.id.imgStar4);
        stars[4] = view.findViewById(R.id.imgStar5);

        TextView[] numbers = new TextView[5];
        numbers[0] = view.findViewById(R.id.txtNumber1);
        numbers[1] = view.findViewById(R.id.txtNumber2);
        numbers[2] = view.findViewById(R.id.txtNumber3);
        numbers[3] = view.findViewById(R.id.txtNumber4);
        numbers[4] = view.findViewById(R.id.txtNumber5);


        OnClickListener starListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStar(v);
            }
        };

        for(ImageView img : stars)
            img.setOnClickListener(starListener);
    }

    private void selectStar(View v){
        int i;
        for(i=0; i<5; i++)
            if(stars[i].equals(v))
                break;

        int lastI = rate - 1;
        rate = i + 1;   // make it 1 to 5
        if(rateListener != null)
            rateListener.onRateChanged(rate);

        if(i < lastI){
            for(int index=lastI; index > i; index--){
                starAnimator(stars[index], lastI-index, false);
            }
        }else if(i > lastI){
            int dif = i - lastI;
            for(lastI+=1; lastI <= i; lastI++){
                starAnimator(stars[lastI], dif - (i -lastI), true);
            }
        }
    }

    public void setRate(int rate) {
        if(rate < 0 && rate > 6)
            return;
        selectStar(stars[rate-1]);
    }

    public int getRate() {
        return rate;
    }

    private void starAnimator(final ImageView img, int index, boolean isSelect){
        if(isSelect){   // Selecting
            ObjectAnimator animator = ObjectAnimator.ofFloat(img, "alpha", 0, 1);
            animator.setStartDelay(index * 120);
            animator.setDuration(250);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    img.setImageResource(R.drawable.ic_star_fill);
                }
            });
            animator.start();
        }else{  // Deselecting stars
            ObjectAnimator animator = ObjectAnimator.ofFloat(img, "alpha", 1, 0);
            animator.setStartDelay(index * 120);
            animator.setDuration(250);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    img.setImageResource(R.drawable.ic_star);
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }
            });
            animator.start();
        }
    }

}
