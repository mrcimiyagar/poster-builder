package com.one.two.three.poster.front.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.one.two.three.poster.R;


/**
 * Created by Pouyan-PC on 11/3/2017.
 */

public class ActivityHint extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);

        TextView txtHome = (TextView) findViewById(R.id.txtActionHome);
        ImageView imgHome = (ImageView) findViewById(R.id.imgActionHome);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHint.this.finish();
            }
        };
        txtHome.setOnClickListener(listener);
        imgHome.setOnClickListener(listener);
    }
}
