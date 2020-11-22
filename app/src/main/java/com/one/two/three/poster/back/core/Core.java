package com.one.two.three.poster.back.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.one.two.three.poster.back.helpers.CacheHelper;
import com.one.two.three.poster.back.helpers.NetworkHelper;
import com.one.two.three.poster.back.utils.UpdateChecker;
import com.one.two.three.poster.front.components.DialogNewUpdate;
import com.one.two.three.poster.util.IabHelper;
import com.one.two.three.poster.util.Inventory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Core extends Application {

    private static Core instance;
    public static Core getInstance() {
        return instance;
    }

    private CacheHelper cacheHelper;
    private NetworkHelper networkHelper;
    private Typeface font;
    private SharedPreferences sharedPreferences;
    private Context mContext;
    private Activity mCurrentActivity;
    private IabHelper mHelper;
    private Inventory mInventory;

    private String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwDt30RovepRIsCSvCeKMh26cWYg6Ts5hIdt+KAw20y8DducSLaMH3wJXjEhcBAdUjbG4VAq70O8JTmOuPBXiWyPKKPXJx23lWRGOiEIdsZGaqxa1OO9GsuQB5GI9I8lCVv/W1mEZQ3N8u7dd0TQr87MAYFt4TeiaAwPP3HVdXYgRhsstzopg8ggW/jEo/YPOTZQmcQK9hg4ffgn5skTxQcMAvcQzFGEbr0H7zokZkUCAwEAAQ==";

    public String SD_CARD_PATH;
    public String APP_DIR_PATH;
    public String TEMP_DIR_PATH;
    public String DOWNLOAD_DIR_PATH;
    public String EXPORT_PATH;

    public static final String EXPORT_DIR_KEY = "export-path";

    public CacheHelper getCacheHelper() {
        return cacheHelper;
    }

    public NetworkHelper getNetworkHelper() {
        return networkHelper;
    }

    public Typeface getFont(){
        return font;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        mContext = getApplicationContext();

        SD_CARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        APP_DIR_PATH = SD_CARD_PATH + "/android/data/" + getPackageName();
        TEMP_DIR_PATH = APP_DIR_PATH + "/temp";
        DOWNLOAD_DIR_PATH = APP_DIR_PATH + "/downloads";
        EXPORT_PATH = SD_CARD_PATH + "/123Poster";

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String exportPath = sharedPreferences.getString(EXPORT_DIR_KEY, "");

        if (exportPath.equals("") || exportPath.equals("nothing")) {
            sharedPreferences.edit().putString(EXPORT_DIR_KEY, EXPORT_PATH).apply();
        } else {
            EXPORT_PATH = exportPath;
        }

        this.checkAppFolders();

        this.cacheHelper = new CacheHelper();
        this.networkHelper = new NetworkHelper();
        this.font = Typeface.createFromAsset(getAssets(), "fonts/iransans.TTF");

    }

    public SharedPreferences getSharedPreferences(){
        return sharedPreferences;
    }

    public void checkAppFolders() {

        File appDir = new File(APP_DIR_PATH);

        if (!appDir.exists()) {
            appDir.mkdirs();
        }

        File tempDir = new File(TEMP_DIR_PATH);

        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        File downloadDir = new File(DOWNLOAD_DIR_PATH);

        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }

        File exportDir = new File(EXPORT_PATH);

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
    }

    public Context getContext(){
        return mContext;
    }

    public void setIabHelper(IabHelper mHelper) {
        this.mHelper = mHelper;
    }

    public IabHelper getIabHelper() {
        return mHelper;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public String getBase64EncodedPublicKey() {
        return base64EncodedPublicKey;
    }

    public Inventory getInventory() {
        return mInventory;
    }

    public void setInventory(Inventory mInventory) {
        this.mInventory = mInventory;
    }
}