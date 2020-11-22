package com.one.two.three.poster.front.components;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.one.two.three.poster.R;
import com.one.two.three.poster.front.behaviours.ControllerView;

/**
 * Created by keyhan1376 on 12/20/2017.
 */

public class FloatingMenu extends FrameLayout implements ControllerView {

    private Button textColorBTN;
    private Button pickFontBTN;
    private Button editTextBTN;
    private Button moveBTN;
    private Button rotateBTN;
    private Button textSizeBTN;

    public interface MenuButtonsClickListener {

        void textColorBtnClicked();
        void pickFontBtnClicked();
        void editTextBtnClicked();
        void moveBtnClicked();
        void rotateBtnClicked();
        void textSizeBtnClicked();
    }

    private MenuButtonsClickListener buttonClickListener;
    public void setButtonsClickListener(MenuButtonsClickListener clickListener) {
        this.buttonClickListener = clickListener;
    }

    public FloatingMenu(@NonNull Context context) {
        super(context);
        this.init(context);
    }

    public FloatingMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public FloatingMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FloatingMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);
    }

    private void init(Context context) {

        LinearLayout contentView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout
                .floating_menu, null, false);
        this.addView(contentView);

        textColorBTN = contentView.findViewById(R.id.floating_menu_text_color_button);
        pickFontBTN = contentView.findViewById(R.id.floating_menu_pick_font_button);
        editTextBTN = contentView.findViewById(R.id.floating_menu_edit_text_button);
        moveBTN = contentView.findViewById(R.id.floating_menu_move_button);
        rotateBTN = contentView.findViewById(R.id.floating_menu_rotate_button);
        textSizeBTN = contentView.findViewById(R.id.floating_menu_text_size_button);

        textColorBTN.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickListener.textColorBtnClicked();
            }
        });

        pickFontBTN.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickListener.pickFontBtnClicked();
            }
        });

        editTextBTN.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickListener.editTextBtnClicked();
            }
        });

        moveBTN.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickListener.moveBtnClicked();
                selectButton(moveBTN);
            }
        });

        rotateBTN.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickListener.rotateBtnClicked();
                selectButton(rotateBTN);
            }
        });

        textSizeBTN.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickListener.textSizeBtnClicked();
                selectButton(textSizeBTN);
            }
        });
    }

    @Override
    public int getControllerHeight() {
        return (int)(128 * getResources().getDisplayMetrics().density);
    }

    private void selectButton(Button btn){
        rotateBTN.setBackgroundResource(R.drawable.menu_button_background);
        textSizeBTN.setBackgroundResource(R.drawable.menu_button_background);
        moveBTN.setBackgroundResource(R.drawable.menu_button_background);
        moveBTN.setTextColor(getResources().getColor(R.color.colorPrimary));
        textSizeBTN.setTextColor(getResources().getColor(R.color.colorPrimary));
        rotateBTN.setTextColor(getResources().getColor(R.color.colorPrimary));

        btn.setBackgroundResource(R.drawable.menu_button_background_selected);
        btn.setTextColor(Color.WHITE);

    }

}