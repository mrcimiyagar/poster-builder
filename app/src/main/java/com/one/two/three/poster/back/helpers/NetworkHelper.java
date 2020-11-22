package com.one.two.three.poster.back.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.one.two.three.poster.back.callbacks.OnDataFetchedListener;
import com.one.two.three.poster.back.callbacks.OnDownloadProgressedListener;
import com.one.two.three.poster.back.callbacks.OnPosterDownloadedListener;
import com.one.two.three.poster.back.callbacks.OnPosterPreviewsFetchedListener;
import com.one.two.three.poster.back.core.Core;
import com.one.two.three.poster.back.models.PosterPreview;
import com.one.two.three.poster.back.utils.VoteHelper;
import com.one.two.three.poster.front.components.DialogSavePoster;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkHelper {

    private final String BASE_URL = "http://diginiaz.com/collage_laravel/public/apis";

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Core.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void ratePoster(final String posterId, final int rate){
        new PosterRateTask(posterId, rate).execute();
    }

    public void downloadBluePrint(final String id, final String url, final OnPosterDownloadedListener
            posterDownloaded, OnDownloadProgressedListener downloadProgressed) {

        new DownloadTask(url, Core.getInstance().TEMP_DIR_PATH + File.separator + "CachedDownload"
                , new Runnable() {
            @Override
            public void run() {

                File file = new File(Core.getInstance().TEMP_DIR_PATH + File.separator + "CachedDownload");
                String folderPath = Core.getInstance().DOWNLOAD_DIR_PATH + "/" + id;
                File destFolder = new File(folderPath);
                if (destFolder.exists()) {
                    destFolder.delete();
                }
                destFolder.mkdirs();
                if (file.exists() && file.length() > 0) {
                    File destFile = new File(folderPath + File.separator + "DataFile.pki");
                    file.renameTo(destFile);
                    posterDownloaded.blueprintDownloaded();
                }
            }
        }, downloadProgressed).execute();
    }

    public void downloadPreview(final String previewPath, String posterId, Runnable callback) {
        new DownloadTask(previewPath, Core.getInstance().DOWNLOAD_DIR_PATH + File.separator + posterId
                + File.separator + "preview.png", callback,
                new OnDownloadProgressedListener() {
                    @Override
                    public void downloadProgressed(float progress) {}
                }).execute();
    }

    public void downloadAPK(Runnable callback, OnDownloadProgressedListener progressedListener, int apkSize){
        new UpdateTask("http://diginiaz.com/collage/update/files/" + Core.getInstance().getPackageName() + ".apk", Core.getInstance().TEMP_DIR_PATH + "/"
                + Core.getInstance().getPackageName() + ".apk", callback, progressedListener, apkSize).execute();
    }

    public void fetchPostersData(String title, String frameCount, String minFrameCount, String maxFrameCount
            , String tags, String offset, String limit, final OnPosterPreviewsFetchedListener callback) {

        new FetchDataTask(title, frameCount, minFrameCount, maxFrameCount, tags, offset, limit, new OnDataFetchedListener() {
            @Override
            public void onDataFetched(String response) {

                if (response != null) {

                    try {

                        ArrayList<PosterPreview> posters = new ArrayList<>();

                        JSONArray resultJsonArr = new JSONArray(response);

                        for (int counter = 0; counter < resultJsonArr.length(); counter++) {
                            JSONObject jsonObject = ((JSONObject) resultJsonArr.get(counter));
                            String previewId = jsonObject.getString("id");
                            String previewTitle = jsonObject.getString("title");
                            String thumbPath = jsonObject.getString("image-preview");
                            String previewPrice = ((JSONObject) jsonObject.get("pki")).getString("price");
                            String previewDownload = ((JSONObject) jsonObject.get("pki")).getString("url");
                            int rate = jsonObject.getInt("score");
                            PosterPreview preview = new PosterPreview(previewId);
                            preview.setThumbnailPath(thumbPath);
                            preview.setPrice(previewPrice);
                            preview.setSku(previewTitle);
                            preview.setRate(rate);
                            preview.setUrl(previewDownload);
                            posters.add(preview);
                        }

                        callback.onPosterPreviewsFetched(posters);

                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            }
        }).execute();
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private String url;
        private String destination;
        private Runnable onDownloaded;
        private OnDownloadProgressedListener downloadProgressed;

        DownloadTask(String url, String destination, Runnable onDownloadFinished, OnDownloadProgressedListener onDownloadProgressed) {
            this.url = url;
            this.destination = destination;
            this.onDownloaded = onDownloadFinished;
            this.downloadProgressed = onDownloadProgressed;

            File file = new File(this.destination);

            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... f_url) {

            int count;

            try {
                URL url = new URL(this.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");        // Request only for header
                connection.setConnectTimeout(8000);         // 8 seconds
                connection.connect();
                int fileSize = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(this.destination);
                byte data[] = new byte[1024];
                int fetchedSize = 0;
                while ((count = input.read(data)) != -1) {
                    fetchedSize += count;
                    publishProgress(fetchedSize * 100 / fileSize);
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception ignored) {
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            super.onProgressUpdate(values);

            downloadProgressed.downloadProgressed(values[0]);
        }

        @Override
        protected void onPostExecute(String file_url) {
            this.onDownloaded.run();
        }
    }

    private class PosterRateTask extends AsyncTask<String, String, String> {

        private String posterId;
        private int rate;

        PosterRateTask(String posterId, int rate){
            this.posterId = posterId;
            this.rate = rate;
        }

        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build();

            RequestBody requestBody = RequestBody.create(null, new byte[0]);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/vote/" + posterId + "/" + rate)
                    .method("POST", requestBody)
                    .header("Content-Length", "0")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                VoteHelper helper = new VoteHelper(Core.getInstance().getContext());
                helper.addPoster(posterId);
                return response.body().string();
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private class FetchDataTask extends AsyncTask<String, String, String> {

        private String title;
        private String frameCount;
        private String minFrameCount;
        private String maxFrameCount;
        private String tags;
        private String offset;
        private String limit;
        private OnDataFetchedListener onDataFetched;

        FetchDataTask(String title, String frameCount, String minFrameCount, String maxFrameCount, String tags, String offset, String limit, OnDataFetchedListener callback) {
            this.title = title;
            this.frameCount = frameCount;
            this.minFrameCount = minFrameCount;
            this.maxFrameCount = maxFrameCount;
            this.tags = tags;
            this.offset = offset;
            this.limit = limit;
            this.onDataFetched = callback;
        }

        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build();

            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("title", title)
                    .addFormDataPart("frame_count", frameCount)
                    .addFormDataPart("frame_count_min", minFrameCount)
                    .addFormDataPart("frame_count_max", maxFrameCount)
                    .addFormDataPart("tags", tags)
                    .addFormDataPart("offset", offset)
                    .addFormDataPart("limit", limit)
                    .build();

            Request request = new Request.Builder()
                    .url(BASE_URL + "/posters")
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String res = response.body().string().replace("\\", "");
                Log.i("RATEEE", res.substring(10));
                return res.substring(res.indexOf("["));                                                ///////// Sub Stringggggg
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            this.onDataFetched.onDataFetched(s);
        }
    }


    private class UpdateTask extends AsyncTask<String, Integer, String> {

        private String url;
        private String destination;
        private Runnable onDownloaded;
        private int apkSize;
        private OnDownloadProgressedListener downloadProgressed;

        UpdateTask(String url, String destination, Runnable onDownloadFinished, OnDownloadProgressedListener onDownloadProgressed, int apkSize) {
            this.url = url;
            this.destination = destination;
            this.onDownloaded = onDownloadFinished;
            this.downloadProgressed = onDownloadProgressed;
            this.apkSize = apkSize;

            File file = new File(this.destination);

            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... f_url) {

            int count;

            try {
                URL url = new URL(this.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");        // Request only for header
                connection.connect();
                int fileSize = this.apkSize;

                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(this.destination);
                byte data[] = new byte[1024];
                int fetchedSize = 0;
                while ((count = input.read(data)) != -1) {
                    fetchedSize += count;
                    publishProgress(fetchedSize * 100 / fileSize);
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception ignored) {
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            downloadProgressed.downloadProgressed(values[0]);
        }

        @Override
        protected void onPostExecute(String file_url) {
            this.onDownloaded.run();
        }
    }

}