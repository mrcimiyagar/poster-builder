package com.one.two.three.poster.front.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.one.two.three.poster.BuildConfig;
import com.one.two.three.poster.R;
import com.one.two.three.poster.back.core.Core;
import com.one.two.three.poster.back.utils.NumberFormater;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ActivitySettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView txtSettingExport = (TextView) findViewById(R.id.txtSettingExport);
        TextView txtAboutUs = (TextView) findViewById(R.id.txtAboutUs);
        TextView txtAboutDevelopers = (TextView) findViewById(R.id.txtAboutDevelopers);
        final TextView txtSettingExportDir = (TextView) findViewById(R.id.txtSettingExportDir);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChooserDialog().with(ActivitySettings.this)
                        .withFilter(true, false)
                        .withStartFile(Core.getInstance().EXPORT_PATH)
                        .withChosenListener(new ChooserDialog.Result() {
                            @Override
                            public void onChoosePath(String path, File pathFile) {
                                Toast.makeText(ActivitySettings.this, getString(R.string.dir_pick_success), Toast.LENGTH_LONG).show();
                                txtSettingExportDir.setText(path);
                                Core.getInstance().EXPORT_PATH = path;
                                SharedPreferences.Editor editor = Core.getInstance().getSharedPreferences().edit();
                                editor.putString(Core.EXPORT_DIR_KEY, path);
                                editor.apply();
                            }
                        })
                        .build()
                        .show();
            }
        };
        txtSettingExport.setOnClickListener(listener);
        txtSettingExportDir.setOnClickListener(listener);

        txtAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutUs();
            }
        });

        txtAboutDevelopers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutDevelopers();
            }
        });

        txtSettingExportDir.setText(Core.getInstance().EXPORT_PATH);

    }

    public void onHomeBtnClicked(View view) {
        this.finish();
    }

    private void showAboutUs(){
        final Dialog dialogAbout = new Dialog(this);
        dialogAbout.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAbout.setContentView(R.layout.dialog_about_us);
        dialogAbout.getWindow().setLayout(getScreenWidth(this), ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView txtDialogAboutUs = (TextView) dialogAbout.findViewById(R.id.txtDialogAboutUs);
        TextView txtDialogAboutUsLink = (TextView) dialogAbout.findViewById(R.id.txtDialogAboutUsLink);
        TextView txtDialogOK = (TextView) dialogAbout.findViewById(R.id.txtDialogOk);
        txtDialogAboutUs.setText(Html.fromHtml(readHtml()));
        txtDialogAboutUsLink.setText(Html.fromHtml("Telegram: <a href='http://t.me/OneTwoThreePoster'>@OneTwoThreePoster</a><br>" +
                "Instagram: <a href='http://instagram.com/onetwothreeposter/'>@OneTwoThreePoster</a>"));
        txtDialogAboutUsLink.setMovementMethod(LinkMovementMethod.getInstance());
        txtDialogOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAbout.dismiss();
            }
        });
        dialogAbout.show();
    }

    private void showAboutDevelopers(){
        final Dialog dialogAbout = new Dialog(this);
        dialogAbout.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAbout.setContentView(R.layout.dialog_about_developers);
        dialogAbout.getWindow().setLayout(getScreenWidth(this), ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView txtVersion = (TextView) dialogAbout.findViewById(R.id.txtDialogAboutVersion);
        TextView txtDialogOK = (TextView) dialogAbout.findViewById(R.id.txtDialogOk);
        String version = getString(R.string.version) + " : " + BuildConfig.VERSION_NAME;
        version = NumberFormater.convertToPersianNumbers(version);
        txtVersion.setText(version);
        txtDialogOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAbout.dismiss();
            }
        });
        LottieAnimationView animationView = (LottieAnimationView) dialogAbout.findViewById(R.id.animationView);
        LottieComposition lottieComposition = LottieComposition.Factory.fromFileSync(Core.getInstance().getContext(), "Animations/developers.json");
        animationView.setComposition(lottieComposition);
        animationView.playAnimation();
        animationView.setScale(2.0f);
        animationView.loop(true);
        dialogAbout.show();
    }

    private int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    private String readHtml() {
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            inputStream = getAssets().open("htmls/about_us.html");
            byteArrayOutputStream = new ByteArrayOutputStream();
            int i;
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

}