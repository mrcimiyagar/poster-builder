package com.one.two.three.poster.front.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.one.two.three.poster.R;
import com.one.two.three.poster.back.core.Core;
import com.one.two.three.poster.back.utils.UpdateChecker;
import com.one.two.three.poster.front.components.CustomTextView;
import com.one.two.three.poster.front.components.DialogAddText;
import com.one.two.three.poster.front.components.DialogMessage;
import com.one.two.three.poster.front.components.DialogNewUpdate;
import com.one.two.three.poster.front.components.DialogSelectFont;
import com.one.two.three.poster.front.components.DialogTextStyle;


public class ActivityHome extends AppCompatActivity {

    private long lastPressed = 0;
    private boolean votedBefore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final LinearLayout logoContainer = findViewById(R.id.activity_hom_logo_container);
        final LinearLayout panelContainer = findViewById(R.id.activity_home_panel_container);

        logoContainer.setAlpha(0);
        panelContainer.setAlpha(0);
        panelContainer.setY(getResources().getDisplayMetrics().heightPixels);

        logoContainer.postDelayed(new Runnable() {
            @Override
            public void run() {

                logoContainer.animate().alpha(1).setDuration(500).start();

                panelContainer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        panelContainer.animate().alpha(1).y(getResources().getDisplayMetrics().heightPixels
                                - panelContainer.getMeasuredHeight() - 32 * getResources().getDisplayMetrics()
                                .density).setDuration(250).start();
                    }
                }, 500);
            }
        }, 650);

        UpdateChecker checker = new UpdateChecker(this);
        final Handler handler = new Handler();
        checker.setUpdateListener(new UpdateChecker.OnUpdateCheckListener() {
            @Override
            public void onNewUpdate(final UpdateChecker.AppInfo app) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        DialogNewUpdate dialog = new DialogNewUpdate(Core.getInstance().getCurrentActivity(), app);
                        dialog.show();
                    }
                });
            }

            @Override
            public void onNoNewUpdate() {
                if(!Core.getInstance().getSharedPreferences().getBoolean("SHARED_BEFORE", false)){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            shareDialog();
                        }
                    });
                }
            }
        });

        if(checkStoragePermission()){
            checker.checkUpdate();
        }

        SharedPreferences sharedPreferences = Core.getInstance().getSharedPreferences();
        votedBefore = sharedPreferences.getBoolean("VOTED_BEFORE", false);
    }

    public void onPosterBtnClicked(View view) {
        startActivity(new Intent(ActivityHome.this, ActivityPosters.class));
        this.finish();
    }

    public void onCVBtnClicked(View view) {

    }

    public void onImgTxtBtnClicked(View view) {

    }

    public void onComingSoon(View view) {
        CustomTextView msg = new CustomTextView(this);
        msg.setBackgroundResource(R.drawable.toast_background);
        int paddingPixels = (int) dpToPixels(6);
        msg.setPadding(paddingPixels, paddingPixels, paddingPixels, paddingPixels);
        msg.setText(getString(R.string.coming_soon));
        msg.setTextColor(Color.WHITE);
        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, (int) dpToPixels(84));
        toast.setView(msg);
        toast.show();
    }

    public void onHelpBtnClicked(View view) {
        startActivity(new Intent(this, ActivityHint.class));
    }

    public void onSettingsBtnClicked(View view) {
        startActivity(new Intent(this, ActivitySettings.class));
    }

    @Override
    public void onBackPressed() {

        if(votedBefore){
            CustomTextView msg = new CustomTextView(this);
            msg.setBackgroundResource(R.drawable.toast_background);
            int paddingPixels = (int) dpToPixels(6);
            msg.setPadding(paddingPixels, paddingPixels, paddingPixels, paddingPixels);
            msg.setText(getString(R.string.press_back));
            msg.setTextColor(Color.WHITE);
            Toast toast = new Toast(this);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, (int) dpToPixels(84));
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(msg);

            if(System.currentTimeMillis() - lastPressed < 2000){
                toast.cancel();
                ActivityHome.this.finish();
            }else{
                toast.show();
                lastPressed = System.currentTimeMillis();
            }
        }else{
            voteDialog();
        }
    }

    private float dpToPixels(float dpNum) {
        return Core.getInstance().getResources().getDisplayMetrics().density * dpNum;
    }

    private boolean checkStoragePermission(){
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void voteDialog(){
        DialogMessage voteDialog = new DialogMessage(this);
        voteDialog.show();

        voteDialog.setTitle(getString(R.string.your_vote));
        voteDialog.setMessage(getString(R.string.vote_message));
        voteDialog.setAnimation("5_stars.json");
        voteDialog.setPositiveButton(getString(R.string.submit_vote), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = Core.getInstance().getSharedPreferences().edit();
                editor.putBoolean("VOTED_BEFORE", true);
                editor.apply();
                votedBefore = true;
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setData(Uri.parse("bazaar://details?id=" + getPackageName()));
                intent.setPackage("com.farsitel.bazaar");
                startActivity(intent);
            }
        });

        voteDialog.setNegativeButton(getString(R.string.exit_app), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityHome.this.finish();
            }
        });

    }

    private void shareDialog(){
        DialogMessage shareDialog = new DialogMessage(this);
        shareDialog.show();

        shareDialog.setTitle(getString(R.string.share_title));
        shareDialog.setMessage(getString(R.string.share_message));
        shareDialog.setAnimation("share.json");
        shareDialog.setPositiveButton(getString(R.string.share), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = Core.getInstance().getSharedPreferences().edit();
                editor.putBoolean("SHARED_BEFORE", true);
                editor.apply();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                            getString(R.string.share_content) + "\nhttps://cafebazaar.ir/app/com.one.two.three.poster/?l=fa");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Core.getInstance().setCurrentActivity(this);
    }

}