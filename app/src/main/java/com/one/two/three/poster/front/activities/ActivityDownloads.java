package com.one.two.three.poster.front.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.one.two.three.poster.R;
import com.one.two.three.poster.back.callbacks.OnPermissionGrantListener;
import com.one.two.three.poster.back.core.Core;
import com.one.two.three.poster.back.models.PosterPreview;
import com.one.two.three.poster.front.adapters.AdapterPoster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class ActivityDownloads extends BaseActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        View view = LayoutInflater.from(this).inflate(R.layout.custom_action_bar, null, false);
        toolbar.addView(view);
        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);

        TextView txtDownloads = (TextView) view.findViewById(R.id.txtDownloads);
        TextView txtBack = (TextView) view.findViewById(R.id.txtHome);
        TextView txtActionbarTitle = (TextView) view.findViewById(R.id.txtActionbarTitle);
        ImageView imgBack = (ImageView) view.findViewById(R.id.imgHome);

        txtBack.setText("برگشت");

        txtActionbarTitle.setText(getResources().getString(R.string.downloads));
        txtActionbarTitle.setTextSize(17);

        txtDownloads.setText("        ");

        imgBack.setImageResource(R.drawable.back);

        View.OnClickListener homeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        };
        txtBack.setOnClickListener(homeListener);
        imgBack.setOnClickListener(homeListener);

        recyclerView = (RecyclerView) findViewById(R.id.activity_downloads_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));

        this.askPermission(new OnPermissionGrantListener() {
            @Override
            public void onPermissionGranted() {

                Core.getInstance().checkAppFolders();

                ArrayList<PosterPreview> posterPreviews = new ArrayList<>();

                for (String posterId : Core.getInstance().getCacheHelper().getCachedBluePrints()) {

                    PosterPreview posterPreview = new PosterPreview(posterId);

                    File previewDetailsFile = new File(Core.getInstance().DOWNLOAD_DIR_PATH + File.separator + posterId + File.separator + "preview_details.txt");

                    if (previewDetailsFile.exists()) {
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(previewDetailsFile));
                            String content = reader.readLine();
                            String[] parts = content.split(",");
                            posterPreview.setThumbnailPath(parts[0]);
                            posterPreview.setPrice(parts[1]);
                            posterPreviews.add(posterPreview);
                        } catch (Exception ignored) { Log.d("KasperLogger", ignored.toString()); }
                    }
                }

                AdapterPoster posterAdapter = new AdapterPoster(AdapterPoster.BehindActivityName.DownloadsActivity, posterPreviews, ActivityDownloads.this, true);
                recyclerView.setAdapter(posterAdapter);
            }

            @Override
            public void onPermissionFailure() {
                Toast.makeText(ActivityDownloads.this, "please grant permission", Toast.LENGTH_SHORT).show();
            }
        }, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE  });
    }
}