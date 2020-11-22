package com.one.two.three.poster.front.components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.one.two.three.poster.R;
import com.one.two.three.poster.back.callbacks.OnVolumeProgressListener;
import com.one.two.three.poster.back.utils.NumberFormater;
import com.one.two.three.poster.front.behaviours.ControllerView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


/**
 * Created by Pouyan-PC on 12/11/2017.
 */

public class VolumeView extends LinearLayout implements ControllerView {

    private Context mContext;
    private LayoutInflater inflater;
    private DiscreteSeekBar seekBar;
    private TextView txtValue;
    private OnVolumeProgressListener listener;
    Handler handler = new Handler();

    public VolumeView(Context context) {
        super(context);
        init(context);
    }

    public VolumeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VolumeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context mContext){
        this.mContext = mContext;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.volume_view, this, true);
        txtValue = view.findViewById(R.id.txtValue);
        seekBar = view.findViewById(R.id.seekbar);
        ImageView imgAdd = view.findViewById(R.id.imgAdd);
        ImageView imgSub = view.findViewById(R.id.imgSub);

        txtValue.setText(NumberFormater.convertToPersianNumbers("0"));

        imgAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = seekBar.getProgress();
                if(progress == seekBar.getMax())
                    return;
                progress++;
                seekBar.setProgress(progress);
                txtValue.setText(NumberFormater.convertToPersianNumbers(progress + ""));
                if(listener != null)
                    listener.onProgressChanged(seekBar.getProgress());
            }
        });

        imgSub.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = seekBar.getProgress();
                if(progress == seekBar.getMin())
                    return;
                progress--;
                seekBar.setProgress(progress);
                txtValue.setText(NumberFormater.convertToPersianNumbers(progress + ""));
                if(listener != null)
                    listener.onProgressChanged(seekBar.getProgress());
            }
        });

        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if(listener != null && fromUser){
                    listener.onProgressChanged(seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(final DiscreteSeekBar seekBar) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txtValue.setText(NumberFormater.convertToPersianNumbers(seekBar.getProgress() + ""));
                    }
                }, 250);    // waiting for progress animation end
            }
        });
    }

    public void setProgressListener(OnVolumeProgressListener listener){
        this.listener = listener;
    }

    public void setMax(int max){
        seekBar.setMax(max);
    }

    public void setMin(int min){
        seekBar.setMin(min);
    }

    public int getMin(){
        return seekBar.getMin();
    }

    public void setProgress(int progress){
        if(progress < getMin() || progress > getMax())
            return;

        ObjectAnimator animation = ObjectAnimator.ofInt(seekBar, "progress", seekBar.getProgress(), progress);
        animation.setDuration(300);
        animation.setInterpolator(new DecelerateInterpolator());
        txtValue.setText(NumberFormater.convertToPersianNumbers(progress + ""));
        animation.start();
        if(listener != null)
            listener.onProgressChanged(seekBar.getProgress());
    }

    public int getMax(){
        return seekBar.getMax();
    }

    public int getProgress(){
        return seekBar.getProgress();
    }

    public void setThemeColor(int color){
        seekBar.setThumbColor(color, color);
        seekBar.setTrackColor(Color.WHITE);
        seekBar.setScrubberColor(color);
    }

    @Override
    public int getControllerHeight() {
        return (int)(32 * getResources().getDisplayMetrics().density);
    }
}
