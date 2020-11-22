package com.one.two.three.poster.front.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.one.two.three.poster.R;


public class ActivityCategories extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        View view = LayoutInflater.from(this).inflate(R.layout.custom_action_bar, null, false);
        toolbar.addView(view);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);

        TextView txtDownloads = (TextView) view.findViewById(R.id.txtDownloads);
        TextView txtHome = (TextView) view.findViewById(R.id.txtHome);
        TextView txtActionbarTitle = (TextView) view.findViewById(R.id.txtActionbarTitle);
        ImageView imgHome = (ImageView) view.findViewById(R.id.imgHome);

        txtActionbarTitle.setText(getString(R.string.new_poster));
        txtDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityCategories.this, ActivityDownloads.class));
            }
        });
        View.OnClickListener homeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityCategories.this, ActivityHome.class));
                ActivityCategories.this.finish();
            }
        };
        txtHome.setOnClickListener(homeListener);
        imgHome.setOnClickListener(homeListener);
    }

    /**
     * onClickListener for tags
     * @param view TextView of selected tag
     */
    public void tagSelect(View view) {
        TextView txt = (TextView) view;
        String name = txt.getText().toString();
        String tagID = (String) txt.getTag();

        Intent postersActivity = new Intent(ActivityCategories.this, ActivityPosters.class);
        postersActivity.putExtra("category_tag", tagID);
        postersActivity.putExtra("category_name", name);
        startActivity(postersActivity);
        this.finish();
    }
}