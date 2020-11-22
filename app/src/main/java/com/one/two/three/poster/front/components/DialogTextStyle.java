package com.one.two.three.poster.front.components;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.one.two.three.poster.R;
import com.one.two.three.poster.back.callbacks.OnVolumeProgressListener;
import com.one.two.three.poster.back.models.TextBackground;
import com.one.two.three.poster.back.models.TextShadow;
import com.one.two.three.poster.back.models.TextStroke;
import com.one.two.three.poster.back.models.TextStyle;
import com.one.two.three.poster.back.utils.TextParser;
import com.one.two.three.poster.front.adapters.AdapterPalette;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;

/**
 * Created by Pouyan-PC on 12/10/2017.
 */

public class DialogTextStyle extends Dialog {

    private Context mContext;
    private LayoutInflater inflater;

    private CustomTextView txtStyleSample;
    private TextView txtSaveStyle;
    private TextView txtCancelStyle;
    private ImageView imgSampleBackground;
    private LinearLayout container;
    private TextStyle textStyle = null;

    private Bitmap backgroundSample;

    private DiscreteSeekBar seekRed;
    private DiscreteSeekBar seekGreen;
    private DiscreteSeekBar seekBlue;

    private OnStyleSaveListener listener;

    public void setStyleSaveListener(OnStyleSaveListener listener) {
        this.listener = listener;
    }

    private int textBaseWidth;
    private int textBaseHeight;
    private int minDim;

    public static interface OnStyleSaveListener {
        void onStyleSave(TextStyle style);
    }

    /**
     *
     * @param context must be AppCompatActivity
     */
    public DialogTextStyle(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public DialogTextStyle(Context context, TextStyle textStyle){
        super(context);
        mContext = context;
        if(textStyle != null)
            this.textStyle = textStyle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_text_style);

        Spinner spinnerStyles = findViewById(R.id.spinnerStyle);
        container = findViewById(R.id.styleContainer);
        txtSaveStyle = findViewById(R.id.txtSaveStyle);
        txtCancelStyle = findViewById(R.id.txtCancelStyle);
        txtStyleSample = findViewById(R.id.txtStyleSample);
        imgSampleBackground = findViewById(R.id.imgSampleBackground);

        if(backgroundSample != null)
            imgSampleBackground.setImageBitmap(backgroundSample);

        txtSaveStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onStyleSave(textStyle);
                dismiss();
            }
        });

        txtCancelStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, mContext.getResources().getStringArray(R.array.text_styles));
        adapter.setDropDownViewResource(R.layout.spinner_drop_down);
        spinnerStyles.setAdapter(adapter);
        spinnerStyles.getBackground().setColorFilter(mContext.getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        spinnerStyles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        createColorStyle();
                        break;
                    case 1:
                        createShadowStyle();
                        break;
                    case 2:
                        createStrokeStyle();
                        break;
                    case 3:
                        createBackgroundStyle();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        drawShadow();
        drawBackground();
        drawStroke();
        final ViewTreeObserver observer = txtStyleSample.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                textBaseHeight = txtStyleSample.getHeight();
                textBaseWidth = txtStyleSample.getWidth();
                if(textBaseWidth < textBaseHeight){
                    minDim = textBaseWidth;
                }else{
                    minDim = textBaseHeight;
                }
                drawBackground();
            }
        });
        if(textStyle != null)
            txtStyleSample.setTextColor(textStyle.getTextColor());
        else
            txtStyleSample.setTextColor(Color.BLACK);


        if(textStyle == null)
            textStyle = new TextStyle();
    }

    private void createColorStyle(){
        container.removeAllViews();
        View view = inflater.inflate(R.layout.fragment_style_color, container);
        final VolumeView volumeRed = view.findViewById(R.id.volumeRed);
        final VolumeView volumeGreen = view.findViewById(R.id.volumeGreen);
        final VolumeView volumeBlue = view.findViewById(R.id.volumeBlue);
        RecyclerView list = view.findViewById(R.id.color_list);

        if(textStyle.getTextColor() != 0){
            int color = textStyle.getTextColor();
            volumeRed.setProgress(Color.red(color));
            volumeGreen.setProgress(Color.green(color));
            volumeBlue.setProgress(Color.blue(color));
        }

        ArrayList<Integer> colors = TextParser.getInstance().getImportedColors();
        AdapterPalette adapter = new AdapterPalette(colors, new AdapterPalette.OnPaletteClick() {
            @Override
            public void onClick(int color) {
                volumeRed.setProgress(Color.red(color));
                volumeGreen.setProgress(Color.green(color));
                volumeBlue.setProgress(Color.blue(color));
                textStyle.setTextColor(color);
                txtStyleSample.setTextColor(color);
            }
        });
        list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true));
        list.setAdapter(adapter);

        volumeRed.setThemeColor(Color.parseColor("#CA0000"));
        volumeBlue.setThemeColor(Color.parseColor("#0000AF"));
        volumeGreen.setThemeColor(Color.parseColor("#007E00"));

        volumeRed.setProgressListener(new OnVolumeProgressListener() {
            @Override
            public void onProgressChanged(int progress) {
                textStyle.setTextColor(Color.argb(255, progress, volumeGreen.getProgress(), volumeBlue.getProgress()));
                txtStyleSample.setTextColor(textStyle.getTextColor());
            }
        });

        volumeGreen.setProgressListener(new OnVolumeProgressListener()  {
            @Override
            public void onProgressChanged(int progress) {
                textStyle.setTextColor(Color.argb(255, volumeRed.getProgress(), progress, volumeBlue.getProgress()));
                txtStyleSample.setTextColor(textStyle.getTextColor());
            }
        });

        volumeBlue.setProgressListener(new OnVolumeProgressListener() {
            @Override
            public void onProgressChanged(int progress) {
                textStyle.setTextColor(Color.argb(255, volumeRed.getProgress(), volumeGreen.getProgress(), progress));
                txtStyleSample.setTextColor(textStyle.getTextColor());
            }
        });

    }

    private void createShadowStyle(){
        container.removeAllViews();
        View view = inflater.inflate(R.layout.fragment_style_shadow, container);
        VolumeView volumeShadowAlpha = view.findViewById(R.id.volumeShadowAlpha);
        VolumeView volumeShadowRadius = view.findViewById(R.id.volumeShadowRadius);
        VolumeView volumeShadowDX = view.findViewById(R.id.volumeShadowDX);
        VolumeView volumeShadowDY = view.findViewById(R.id.volumeShadowDY);
        RecyclerView list = view.findViewById(R.id.color_list);

        final TextShadow shadowStyle;
        if(textStyle.getTextShadow() == null){
            shadowStyle = new TextShadow();
            textStyle.setTextShadow(shadowStyle);
        }else{
            shadowStyle = textStyle.getTextShadow();
            txtStyleSample.setTextColor(textStyle.getTextColor());
            volumeShadowAlpha.setProgress(shadowStyle.getAlpha());
            volumeShadowRadius.setProgress(shadowStyle.getRadius());
            volumeShadowDX.setProgress(shadowStyle.getX());
            volumeShadowDY.setProgress(shadowStyle.getY());
        }

        ArrayList<Integer> colors = TextParser.getInstance().getImportedColors();
        AdapterPalette adapter = new AdapterPalette(colors, new AdapterPalette.OnPaletteClick() {
            @Override
            public void onClick(int color) {
                shadowStyle.setColor(color);
                drawShadow();
            }
        });
        list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true));
        list.setAdapter(adapter);

        volumeShadowRadius.setMax(25);
        volumeShadowDX.setMin(-50);
        volumeShadowDX.setMax(50);
        volumeShadowDX.setProgress(0);
        volumeShadowDY.setMin(-50);
        volumeShadowDY.setMax(50);
        volumeShadowDY.setProgress(0);

        volumeShadowAlpha.setProgressListener(new OnVolumeProgressListener() {
            @Override
            public void onProgressChanged(int progress) {
                shadowStyle.setAlpha(progress);
                drawShadow();
            }
        });

        volumeShadowRadius.setProgressListener(new OnVolumeProgressListener() {
            @Override
            public void onProgressChanged(int progress) {
                shadowStyle.setRadius(progress);
                drawShadow();
            }
        });

        volumeShadowDX.setProgressListener(new OnVolumeProgressListener() {
            @Override
            public void onProgressChanged(int progress) {
                shadowStyle.setX(progress);
                drawShadow();
            }
        });

        volumeShadowDY.setProgressListener(new OnVolumeProgressListener() {
            @Override
            public void onProgressChanged(int progress) {
                shadowStyle.setY(progress);
                drawShadow();
            }
        });
    }

    private void createStrokeStyle(){
        container.removeAllViews();
        View view = inflater.inflate(R.layout.fragment_style_stroke, container);
        VolumeView volumeAlpha = view.findViewById(R.id.volumeStrokeAlpha);
        VolumeView volumeWidth = view.findViewById(R.id.volumeStrokeWidth);
        RecyclerView list = view.findViewById(R.id.color_list);

        final TextStroke strokeStyle;
        if(textStyle.getTextStroke() == null){
            strokeStyle = new TextStroke();
            textStyle.setTextStroke(strokeStyle);
        }else{
            strokeStyle = textStyle.getTextStroke();
            volumeAlpha.setProgress(strokeStyle.getAlpha());
            volumeWidth.setProgress(strokeStyle.getWidth());
        }

        ArrayList<Integer> colors = TextParser.getInstance().getImportedColors();
        AdapterPalette adapter = new AdapterPalette(colors, new AdapterPalette.OnPaletteClick() {
            @Override
            public void onClick(int color) {
                strokeStyle.setColor(color);
                drawStroke();
            }
        });
        list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true));
        list.setAdapter(adapter);

        volumeWidth.setMax(8);

        volumeAlpha.setProgressListener(new OnVolumeProgressListener() {
            @Override
            public void onProgressChanged(int progress) {
                strokeStyle.setAlpha(progress);
                drawStroke();
            }
        });

        volumeWidth.setProgressListener(new OnVolumeProgressListener() {
            @Override
            public void onProgressChanged(int progress) {
                strokeStyle.setWidth(progress);
                drawStroke();
            }
        });
    }

    private void createBackgroundStyle(){
        container.removeAllViews();
        View view = inflater.inflate(R.layout.fragment_style_background, container);
        final VolumeView volumeAlpha = view.findViewById(R.id.volumeBackgroundAlpha);
        VolumeView volumeWidth = view.findViewById(R.id.volumeBackgroundWidth);
        VolumeView volumeHeight = view.findViewById(R.id.volumeBackgroundHeight);
        VolumeView volumeCorners = view.findViewById(R.id.volumeBackgroundCorners);
        RecyclerView list = view.findViewById(R.id.color_list);

        final TextBackground backgroundStyle;
        if(textStyle.getTextBackground() == null){
            backgroundStyle = new TextBackground();
            textStyle.setTextBackground(backgroundStyle);
        }else{
            backgroundStyle = textStyle.getTextBackground();
            volumeAlpha.setProgress(backgroundStyle.getAlpha());
            volumeCorners.setProgress(backgroundStyle.getCorner());
            volumeHeight.setProgress(backgroundStyle.getHeight());
            volumeWidth.setProgress(backgroundStyle.getWidth());
        }

        ArrayList<Integer> colors = TextParser.getInstance().getImportedColors();
        AdapterPalette adapter = new AdapterPalette(colors, new AdapterPalette.OnPaletteClick() {
            @Override
            public void onClick(int color) {
                backgroundStyle.setColor(color);
                volumeAlpha.setProgress(255);
                backgroundStyle.setAlpha(255);
                drawBackground();
            }
        });
        list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true));
        list.setAdapter(adapter);

        volumeWidth.setMax(100);    // Percent of images width
        volumeHeight.setMax(100);   // Percent of images height
        volumeCorners.setMax(100);

        volumeAlpha.setProgressListener(new OnVolumeProgressListener() {
            @Override
            public void onProgressChanged(int progress) {
                backgroundStyle.setAlpha(progress);
                drawBackground();
            }
        });

        volumeCorners.setProgressListener(new OnVolumeProgressListener() {
            @Override
            public void onProgressChanged(int progress) {
                backgroundStyle.setCorner(progress);
                drawBackground();
            }
        });

        volumeHeight.setProgressListener(new OnVolumeProgressListener() {
            @Override
            public void onProgressChanged(int progress) {
                backgroundStyle.setHeight(progress);
                drawBackground();
            }
        });

        volumeWidth.setProgressListener(new OnVolumeProgressListener() {
            @Override
            public void onProgressChanged(int progress) {
                backgroundStyle.setWidth(progress);
                drawBackground();
            }
        });
    }

    private void drawStroke(){
        if(textStyle == null || textStyle.getTextStroke() == null)
            return;

        TextStroke stroke = textStyle.getTextStroke();

        int color = stroke.getColor();
        int argb = Color.argb(stroke.getAlpha(), Color.red(color), Color.green(color), Color.blue(color));
        txtStyleSample.setStrokeColor(argb);
        txtStyleSample.setStrokeWidth(stroke.getWidth());
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) txtStyleSample.getLayoutParams();
//        params.height = textBaseHeight + 2 * stroke.getWidth();
//        params.width = textBaseWidth + 2 * stroke.getWidth();
//        txtStyleSample.setLayoutParams(params);
    }

    private void drawBackground(){
        if(textStyle == null || textStyle.getTextBackground() == null)
            return;
        TextBackground background = textStyle.getTextBackground();
        int color = background.getColor();
        GradientDrawable drawable = (GradientDrawable) txtStyleSample.getBackground();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(Color.argb(background.getAlpha(), Color.red(color), Color.green(color), Color.blue(color)));

        int lrPadding = background.getWidth();
        int tbPadding = background.getHeight();
        txtStyleSample.setPadding(lrPadding, tbPadding, lrPadding, tbPadding);

        float corner = (background.getCorner() / 100.0f) * (minDim / 2);
        drawable.setCornerRadius(corner);
    }

    private void drawShadow(){
        if(textStyle == null || textStyle.getTextShadow() == null)
            return;
        TextShadow shadow = textStyle.getTextShadow();
        int color = shadow.getColor();
        int argb = Color.argb(shadow.getAlpha(), Color.red(color), Color.green(color), Color.blue(color));
        txtStyleSample.setShadowLayer(shadow.getRadius(), shadow.getX(), shadow.getY(), argb);
    }

    public int convertDpToPixel(float dp){
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int) (dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public void setBackgroundSample(Bitmap bmp){
        this.backgroundSample = bmp;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(backgroundSample != null)
            backgroundSample.recycle();
    }
}
