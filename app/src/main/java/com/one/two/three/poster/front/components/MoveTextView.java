package com.one.two.three.poster.front.components;

import android.content.Context;
import android.media.Image;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.one.two.three.poster.R;
import com.one.two.three.poster.front.behaviours.ControllerView;

import java.util.zip.Inflater;

/**
 * Created by Pouyan-PC on 12/19/2017.
 */

public class MoveTextView extends LinearLayout implements ControllerView {

    private Context mContext;
    private OnMoveClickListener listener;

    private ImageView imgMoveTop;
    private ImageView imgMoveRight;
    private ImageView imgMoveBottom;
    private ImageView imgMoveLeft;

    private LinearLayout layoutVerticalAlign;
    private LinearLayout layoutHorizontalAlign;

    public static final int MOVE_UP = 1;
    public static final int MOVE_DOWN = 2;
    public static final int MOVE_RIGHT = 3;
    public static final int MOVE_LEFT = 4;

    public static final int ALIGN_VERTICAL = 5;
    public static final int ALIGN_HORIZONTAL = 6;

    @Override
    public int getControllerHeight() {
        return (int)(172 * getResources().getDisplayMetrics().density);
    }

    public static interface OnMoveClickListener {
        void onMovedClicked(int move);
        void onAlignClicked(int align);
    }

    public MoveTextView(Context context) {
        super(context);
        initialize(context);
    }

    public MoveTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public MoveTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context mContext){
        this.mContext = mContext;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater == null)
            return;
        View view = inflater.inflate(R.layout.dialog_text_move, this, true);

        imgMoveTop = view.findViewById(R.id.imgMoveTop);
        imgMoveRight = view.findViewById(R.id.imgMoveRight);
        imgMoveBottom = view.findViewById(R.id.imgMoveBottom);
        imgMoveLeft = view.findViewById(R.id.imgMoveLeft);
        layoutHorizontalAlign = view.findViewById(R.id.layoutHorizontalAlign);
        layoutVerticalAlign = view.findViewById(R.id.layoutVerticalAlign);

        imgMoveTop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onMovedClicked(MOVE_UP);
            }
        });

        imgMoveBottom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onMovedClicked(MOVE_DOWN);
            }
        });

        imgMoveRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onMovedClicked(MOVE_RIGHT);
            }
        });

        imgMoveLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onMovedClicked(MOVE_LEFT);
            }
        });

        layoutHorizontalAlign.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onAlignClicked(ALIGN_HORIZONTAL);
            }
        });

        layoutVerticalAlign.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onAlignClicked(ALIGN_VERTICAL);
            }
        });

    }

    public void setMoveListener(OnMoveClickListener listener){
        this.listener = listener;
    }

}
