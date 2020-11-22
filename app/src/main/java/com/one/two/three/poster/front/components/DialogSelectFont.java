package com.one.two.three.poster.front.components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import com.one.two.three.poster.R;
import com.one.two.three.poster.front.adapters.AdapterFont;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pouyan-PC on 12/21/2017.
 */

public class DialogSelectFont extends Dialog {

    private Context mContext;
    private OnFontSelectListener listener;

    public interface OnFontSelectListener {
        void onFontSelect(Typeface font);
    }

    public DialogSelectFont(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_font);

        if(getWindow() != null)
            getWindow().setLayout(getScreenWidth((Activity) mContext), (int) (getScreenHeight((Activity) mContext) * 0.9));

        try {
            RecyclerView fontList = findViewById(R.id.fontList);
            fontList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            String[] fontsName = mContext.getAssets().list("fonts");
            ArrayList<Typeface> fonts = new ArrayList<>();
            for(String font : fontsName){
                fonts.add(Typeface.createFromAsset(mContext.getAssets(), "fonts/" + font));
            }
            AdapterFont adapterFont = new AdapterFont(fonts, new AdapterFont.OnFontClick() {
                @Override
                public void onClick(Typeface font) {
                    if(listener != null)
                        listener.onFontSelect(font);
                    dismiss();
                }
            });
            fontList.setAdapter(adapterFont);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Must be set to get selected typeface
     * @param listener
     */
    public void setListener(OnFontSelectListener listener) {
        this.listener = listener;
    }

    private int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    private int getScreenHeight(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.y;
    }

}
