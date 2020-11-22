package com.one.two.three.poster.front.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.one.two.three.poster.R;

public class BuyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        View view = LayoutInflater.from(this).inflate(R.layout.custom_action_bar, null, false);
        toolbar.addView(view);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);

        TextView txtBuy = (TextView) view.findViewById(R.id.txtDownloads);
        TextView txtBack = (TextView) view.findViewById(R.id.txtHome);
        TextView txtActionbarTitle = (TextView) view.findViewById(R.id.txtActionbarTitle);
        ImageView imgBack = (ImageView) view.findViewById(R.id.imgHome);

        txtBack.setText("برگشت");

        txtActionbarTitle.setText("خرید");
        txtActionbarTitle.setTextSize(23);

        txtBuy.setText("تایید خرید");

        imgBack.setImageResource(R.drawable.back);

        View.OnClickListener homeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        };
        txtBack.setOnClickListener(homeListener);
        imgBack.setOnClickListener(homeListener);

        ImageView previewIV = (ImageView) findViewById(R.id.activity_buy_image_view);
        Glide.with(this)
                .load(getIntent().getExtras().getString("poster-thumb-url"))
                .into(previewIV);
    }
}