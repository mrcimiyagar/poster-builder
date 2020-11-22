package com.one.two.three.poster.front.components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.one.two.three.poster.R;
import com.one.two.three.poster.back.core.Core;
import com.one.two.three.poster.back.helpers.GraphicHelper;

import org.w3c.dom.Text;

import static com.airbnb.lottie.utils.Utils.getScreenWidth;

/**
 * Created by Pouyan-PC on 12/8/2017.
 */

public class DialogAddText extends Dialog {

    public interface OnAddListener{
        void onAdd(String text, TEXT_ALIGN align, Typeface typeface);
        void onCancel();
    }

    public enum TEXT_ALIGN {RIGHT, LEFT, CENTER}

    private Context mContext;
    private OnAddListener listener;
    private String defaultText = "";
    private Typeface defaultTypeface = null;

    private TextView btnTextAlign;
    private TextView btnTextFont;
    private TextView txtAddTextSave;
    private TextView txtAddTextCancel;
    private EditText edtAddText;

    private TEXT_ALIGN align = TEXT_ALIGN.RIGHT;

    public DialogAddText(@NonNull Context context, OnAddListener listener) {
        super(context);
        this.mContext = context;
        this.listener = listener;
    }

    public DialogAddText(Context context, OnAddListener listener, String defaultText, Typeface defaultTypeface){
        super(context);
        this.mContext = context;
        this.listener = listener;
        this.defaultText = defaultText;
        this.defaultTypeface = defaultTypeface;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_text);

        getWindow().setLayout(GraphicHelper.getInstance().getScreenWidth((Activity) mContext), ViewGroup.LayoutParams.WRAP_CONTENT);
        setCancelable(false);

        btnTextAlign = findViewById(R.id.btnTextAlign);
        btnTextFont = findViewById(R.id.btnTextFont);
        txtAddTextSave = findViewById(R.id.txtAddTextSave);
        txtAddTextCancel = findViewById(R.id.txtAddTextCancel);
        btnTextFont = findViewById(R.id.btnTextFont);
        edtAddText = findViewById(R.id.edtAddText);

        edtAddText.setTypeface(Core.getInstance().getFont());
        btnTextAlign.setTypeface(Core.getInstance().getFont());
        btnTextFont.setTypeface(Core.getInstance().getFont());

        if(!defaultText.equals(""))
            edtAddText.setText(defaultText);

        if(defaultTypeface != null)
            edtAddText.setTypeface(defaultTypeface);

        btnTextAlign.setText(Html.fromHtml(mContext.getString(R.string.align) + ": <b>" + mContext.getString(R.string.align_right) + "</b>"));

        btnTextAlign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(align){
                    case RIGHT:
                        align = TEXT_ALIGN.CENTER;
                        btnTextAlign.setText(Html.fromHtml(mContext.getString(R.string.align) + ": <b>" + mContext.getString(R.string.align_center) + "</b>"));
                        edtAddText.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
                        break;

                    case LEFT:
                        align = TEXT_ALIGN.RIGHT;
                        btnTextAlign.setText(Html.fromHtml(mContext.getString(R.string.align) + ": <b>" + mContext.getString(R.string.align_right) + "</b>"));
                        edtAddText.setGravity(Gravity.RIGHT|Gravity.TOP);
                        break;

                    case CENTER:
                        align = TEXT_ALIGN.LEFT;
                        btnTextAlign.setText(Html.fromHtml(mContext.getString(R.string.align) + ": <b>" + mContext.getString(R.string.align_left) + "</b>"));
                        edtAddText.setGravity(Gravity.LEFT|Gravity.TOP);
                        break;
                }
            }
        });

        btnTextFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSelectFont fontSelect = new DialogSelectFont(mContext);
                fontSelect.setListener(new DialogSelectFont.OnFontSelectListener() {
                    @Override
                    public void onFontSelect(Typeface font) {
                        edtAddText.setTypeface(font);
                    }
                });
                fontSelect.show();
            }
        });

        txtAddTextCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onCancel();
               dismiss();
            }
        });

        txtAddTextSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null && !edtAddText.getText().toString().equals("")){
                    String text = edtAddText.getText().toString();
                    Typeface typeface = edtAddText.getTypeface();
                    listener.onAdd(text, align, typeface);
                    dismiss();
                }
            }
        });

    }

    private int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }
}
