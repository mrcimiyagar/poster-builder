package com.one.two.three.poster.front.components;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.one.two.three.poster.R;
import com.one.two.three.poster.back.core.Core;
import com.one.two.three.poster.back.models.PosterPreview;
import com.one.two.three.poster.util.IabHelper;
import com.one.two.three.poster.util.IabResult;
import com.one.two.three.poster.util.Purchase;

/**
 * Created by Pouyan-PC on 9/1/2017.
 */

public class DialogBuyPoster extends Dialog{

    public static final int NORMAL_FRAME = 1;
    public static final int BIG_FRAME = 2;

    private int selectedFrame;

    private Context mContext;
    private ImageView previewIV;
    private TextView txtBuy;
    private TextView txtCancel;

    private PosterPreview poster;

    public static final int REQUEST_CODE_IAB = 801;

    public void setPoster(PosterPreview poster) {
        this.poster = poster;
    }

    private OnPosterBuyListener listener;

    public static interface OnPosterBuyListener {
        void onBuy(PosterPreview poster);
    }

    /**
     *
     * @param context Context of application
     * @param defaultFrame Select normal frame {@link #NORMAL_FRAME} or big frame {@link #BIG_FRAME} by default
     */
    public DialogBuyPoster(Context context, int defaultFrame){
        super(context);
        this.mContext = context;
        selectedFrame = defaultFrame;

        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    public DialogBuyPoster(Context context) {
        this(context, NORMAL_FRAME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_buy_poster);

        getWindow().setLayout(getScreenWidth((Activity) mContext), ViewGroup.LayoutParams.WRAP_CONTENT);

        previewIV = (ImageView) findViewById(R.id.dialog_buy_poster_preview_image_view);
        txtBuy = (TextView) findViewById(R.id.txtSavePoster);
        txtCancel = (TextView) findViewById(R.id.txtCancelSave);

        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBuyPoster.this.dismiss();
            }
        });

        final IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                if(result.isSuccess() && info.getSku().equals(poster.getSku())){
                    if(listener != null)
                        DialogBuyPoster.this.dismiss();
                        listener.onBuy(poster);
                }else{
                    Toast.makeText(Core.getInstance().getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        txtBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPurchase = false;
                if(Core.getInstance().getInventory() != null)
                    hasPurchase = Core.getInstance().getInventory().hasPurchase(poster.getSku());
                else
                    Toast.makeText(Core.getInstance().getContext(), Core.getInstance().getContext().getString(R.string.iab_connect_problem), Toast.LENGTH_LONG).show();

                if(hasPurchase){
                    listener.onBuy(poster);
                }else{
                    try{
                        Core.getInstance().getIabHelper().launchPurchaseFlow(Core.getInstance().getCurrentActivity(), poster.getSku(), REQUEST_CODE_IAB, mPurchaseFinishedListener, "");
                    }catch (Exception e){
                        Toast.makeText(Core.getInstance().getContext(), Core.getInstance().getContext().getString(R.string.iab_connect_problem), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        if(selectedFrame == NORMAL_FRAME){
            selectedFrame = 0;
        }else if(selectedFrame == BIG_FRAME){
            selectedFrame = 0;
        }

        RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(getContext())
                .load(poster.getThumbnailPath())
                .apply(options)
                .into(previewIV);
    }

    public void setOnBuyListener(OnPosterBuyListener listener){
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
