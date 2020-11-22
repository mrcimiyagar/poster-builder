package com.one.two.three.poster.back.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.one.two.three.poster.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Pouyan-PC on 12/25/2017.
 */

public class UpdateChecker {

    public interface OnUpdateCheckListener {
        void onNewUpdate(AppInfo app);
        void onNoNewUpdate();
    }

    public static class AppInfo {
        private String packageName;
        private int versionCode;
        private String versionName;
        private String newFeatures;
        private int apkLength;
        private int status;

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public void setNewFeatures(String newFeatures) {
            this.newFeatures = newFeatures;
        }

        public void setApkLength(int apkLength) {
            this.apkLength = apkLength;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getPackageName() {
            return packageName;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public String getNewFeatures() {
            return newFeatures;
        }

        public int getApkLength() {
            return apkLength;
        }

        public int getStatus() {
            return status;
        }
    }

    private Context mContext;
    private final String UPDATE_URL = "http://diginiaz.com/collage/update/";
    private final String packageName;
    private final String deviceName = Build.MODEL;
    private final int versionCode = BuildConfig.VERSION_CODE;
    private RequestBody body;
    private OnUpdateCheckListener listener;
    OkHttpClient client = new OkHttpClient();

    public UpdateChecker(Context mContext) {
        this.mContext = mContext;
        packageName = mContext.getPackageName();
        body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("package_name", packageName)
                .addFormDataPart("version_code", versionCode + "")
                .addFormDataPart("device_name", deviceName)
                .build();
    }

    public void setUpdateListener(OnUpdateCheckListener listener){
        this.listener = listener;
    }

    public void checkUpdate(){
        final Request request = new Request.Builder()
                .url(UPDATE_URL)
                .post(body)
                .build();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    String result = response.body().string();
                    JSONObject res = new JSONObject(result);
                    if(res.getInt("version_code") > versionCode){
                        AppInfo app = new AppInfo();
                        app.setPackageName(res.getString("package"));
                        app.setVersionCode(res.getInt("version_code"));
                        app.setVersionName(res.getString("version_name"));
                        app.setApkLength(res.getInt("length"));
                        app.setStatus(res.getInt("status"));
                        app.setNewFeatures(res.getString("features"));
                        if(listener != null)
                            listener.onNewUpdate(app);
                    }else{
                        if(listener != null)
                            listener.onNoNewUpdate();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

}
