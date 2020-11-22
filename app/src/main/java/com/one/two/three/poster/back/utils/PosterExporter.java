package com.one.two.three.poster.back.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;

import com.one.two.three.poster.back.core.Core;
import com.one.two.three.poster.front.activities.ActivityDesign;
import com.one.two.three.poster.front.activities.BaseActivity;
import com.one.two.three.poster.front.components.DialogExportResult;
import com.one.two.three.poster.front.components.DialogSavePoster;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by keyhan1376 on 1/20/2018.
 */

public class PosterExporter {

    public static void exportPoster(final String posterId, final int posterSizeMode
            , final View posterContainer, final BaseActivity activity) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final File parentDir = new File(Core.getInstance().EXPORT_PATH);
                    if (!parentDir.exists()) {
                        try {
                            parentDir.mkdirs();
                        } catch (Exception ignored) {

                        }
                    }

                    final View v = posterContainer;

                    int exportWidth, exportHeight;

                    if (posterSizeMode == DialogSavePoster.BIG_FRAME) {
                        exportWidth = 2480;
                        exportHeight = 3508;
                    }
                    else {
                        exportWidth = 904;
                        exportHeight = 1280;
                    }

                    final Bitmap b = Bitmap.createBitmap(exportWidth, exportHeight, Bitmap.Config.RGB_565);
                    Canvas c = new Canvas(b);
                    Matrix matrix = new Matrix();
                    matrix.setScale((float)exportWidth / (float)v.getMeasuredWidth(), (float)exportHeight / (float)v.getMeasuredHeight());
                    c.setMatrix(matrix);
                    v.draw(c);

                    File exportedPosterFile;
                    int fileNameCounter = 0;
                    exportedPosterFile = new File(parentDir, posterId + "_" + fileNameCounter + ".png");
                    while (exportedPosterFile.exists()) {
                        exportedPosterFile = new File(parentDir, posterId + "_" + fileNameCounter + ".png");
                        fileNameCounter++;
                    }
                    exportedPosterFile.createNewFile();
                    FileOutputStream outputStream = new FileOutputStream(exportedPosterFile);
                    b.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    final String exportedFilePath = exportedPosterFile.getPath();

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            DialogExportResult exportResultDialog = new DialogExportResult(activity);
                            exportResultDialog.setPath(exportedFilePath);
                            exportResultDialog.setOnOpenListener(new DialogExportResult.OnPosterOpenListener() {
                                @Override
                                public void onOpen(String path) {

                                    Intent intent = new Intent();
                                    intent.setAction(android.content.Intent.ACTION_VIEW);
                                    intent.setDataAndType(FileProvider.getUriForFile(activity
                                            , activity.getApplicationContext()
                                                    .getPackageName() + ".provider"
                                            , new File(path)), "image/png");
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    activity.startActivity(intent);
                                }
                            });
                            exportResultDialog.show();
                        }
                    });
                } catch (Exception ignored) {

                }
            }
        }).start();
    }
}
