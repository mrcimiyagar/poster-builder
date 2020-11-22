package com.one.two.three.poster.front.components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.one.two.three.poster.BuildConfig;
import com.one.two.three.poster.R;
import com.one.two.three.poster.back.callbacks.OnDownloadProgressedListener;
import com.one.two.three.poster.back.core.Core;
import com.one.two.three.poster.back.helpers.NetworkHelper;
import com.one.two.three.poster.back.utils.NumberFormater;
import com.one.two.three.poster.back.utils.UpdateChecker;

import java.io.File;

import static com.airbnb.lottie.utils.Utils.getScreenHeight;

/**
 * Created by Pouyan-PC on 12/25/2017.
 */

public class DialogNewUpdate extends Dialog {

    private Context mContext;
    private UpdateChecker.AppInfo appInfo;

    public DialogNewUpdate(@NonNull Context context, UpdateChecker.AppInfo appInfo) {
        super(context);
        this.mContext = context;
        this.appInfo = appInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_update);

        if(getWindow() != null)
            getWindow().setLayout(getScreenWidth((Activity) mContext), ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView txtUpdateCurrentVersion = findViewById(R.id.txtUpdateCurrentVersion);
        TextView txtUpdateNewVersion = findViewById(R.id.txtUpdateNewVersion);
        TextView txtUpdateFeatures = findViewById(R.id.txtUpdateFeatures);
        final TextView txtUpdateDownload = findViewById(R.id.txtUpdateDownload);
        TextView txtUpdateOk = findViewById(R.id.txtUpdateOk);
        final DonutProgress downloadProgress = findViewById(R.id.downloadProgress);
        final ScrollView updateScroll = findViewById(R.id.updateScroll);

        String current = mContext.getString(R.string.current_version) + ": " + NumberFormater.convertToPersianNumbers(BuildConfig.VERSION_NAME);
        txtUpdateCurrentVersion.setText(current);
        txtUpdateNewVersion.setText(Html.fromHtml("<b>" + mContext.getString(R.string.new_version) + ": "
                + NumberFormater.convertToPersianNumbers(appInfo.getVersionName()) + "</b>"));
        txtUpdateFeatures.setText(appInfo.getNewFeatures());
        txtUpdateOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        ViewTreeObserver observer = updateScroll.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(updateScroll.getHeight() > dpToPixels(120)){
                    ViewGroup.LayoutParams params = updateScroll.getLayoutParams();
                    params.height = (int) dpToPixels(120);
                    updateScroll.setLayoutParams(params);
                    updateScroll.requestLayout();
                }
            }
        });

        txtUpdateDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtUpdateDownload.setVisibility(View.INVISIBLE);
                downloadProgress.setVisibility(View.VISIBLE);
                Core.getInstance().getNetworkHelper().downloadAPK(new Runnable() {
                    @Override
                    public void run() {
                        File apk = new File(Core.getInstance().TEMP_DIR_PATH + "/" + Core.getInstance().getPackageName() + ".apk");
//                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            Uri fileUri = android.support.v4.content.FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", apk);
//                            intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
//                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            mContext.startActivity(intent);
//                        }else{
//                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                            intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            mContext.startActivity(intent);
//                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Uri apkUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", apk);
                            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                            intent.setData(apkUri);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Core.getInstance().getCurrentActivity().startActivity(intent);
                        } else {
                            Uri apkUri = Uri.fromFile(apk);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Core.getInstance().getCurrentActivity().startActivity(intent);
                        }
                        if(isShowing())
                            dismiss();
                    }
                }, new OnDownloadProgressedListener() {
                    @Override
                    public void downloadProgressed(float progress) {
                        downloadProgress.setProgress(progress);
                    }
                }, appInfo.getApkLength());
            }
        });

    }

    private int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    public static float dpToPixels(float dpNum) {
        return Core.getInstance().getResources().getDisplayMetrics().density * dpNum;
    }

}
