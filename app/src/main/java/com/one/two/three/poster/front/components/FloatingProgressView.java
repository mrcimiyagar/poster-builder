package com.one.two.three.poster.front.components;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.one.two.three.poster.R;
import com.one.two.three.poster.back.callbacks.OnVolumeProgressListener;
import com.one.two.three.poster.back.core.Core;
import com.one.two.three.poster.front.behaviours.ControllerView;

/**
 * Created by Pouyan-PC on 12/23/2017.
 */

public class FloatingProgressView extends LinearLayout implements ControllerView {

    private Context mContext;
    private TextView txtLabel;
    private VolumeView volumeProgress;
    private OnVolumeProgressListener listener;

    public FloatingProgressView(Context context) {
        super(context);
        initialize(context);
    }

    public FloatingProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public FloatingProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    public void initialize(Context mContext){
        this.mContext = mContext;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.floating_progress, this, true);

        txtLabel = view.findViewById(R.id.txtLabel);
        volumeProgress = view.findViewById(R.id.volumeProgress);
    }

    public void setMin(int min){
        volumeProgress.setMin(min);
    }

    public void setMax(int max){
        volumeProgress.setMax(max);
    }

    public void setProgress(int progress) {
        volumeProgress.setProgress(progress);
    }

    public void setLabel(String label){
        txtLabel.setText(label);
    }

    public String getLabel(){
        return txtLabel.getText().toString();
    }

    public void setProgressListener(OnVolumeProgressListener listener){
        volumeProgress.setProgressListener(listener);
    }

    public int getProgress(){
        return volumeProgress.getProgress();
    }

    @Override
    public int getControllerHeight() {
        return (int)(48 * Core.getInstance().getResources().getDisplayMetrics().density);
    }
}
