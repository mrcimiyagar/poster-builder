package com.one.two.three.poster.front.components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.one.two.three.poster.R;
import com.one.two.three.poster.back.core.Core;
import com.one.two.three.poster.back.helpers.GraphicHelper;

/**
 * Created by Pouyan-PC on 12/8/2017.
 */

public class DialogAddLogo extends Dialog {

    public interface OnAddListener{
        void onAdd();
        void onCancel();
    }

    private Context mContext;
    private OnAddListener listener;

    private TextView galleryTV;
    private TextView cancelTV;

    public DialogAddLogo(@NonNull Context context, OnAddListener listener) {
        super(context);
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_logo);

        getWindow().setLayout(GraphicHelper.getInstance().getScreenWidth((Activity) mContext), ViewGroup.LayoutParams.WRAP_CONTENT);
        setCancelable(false);

        cancelTV = findViewById(R.id.dialog_add_logo_cancel_text_View);
        galleryTV = findViewById(R.id.dialog_add_logo_gallery_text_view);

        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onCancel();
               dismiss();
            }
        });

        galleryTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onAdd();
                dismiss();
            }
        });
    }
}
