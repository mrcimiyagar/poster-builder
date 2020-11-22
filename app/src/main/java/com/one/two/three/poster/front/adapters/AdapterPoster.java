package com.one.two.three.poster.front.adapters;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.one.two.three.poster.R;
import com.one.two.three.poster.back.callbacks.OnDownloadProgressedListener;
import com.one.two.three.poster.back.callbacks.OnPermissionGrantListener;
import com.one.two.three.poster.back.callbacks.OnPosterDownloadedListener;
import com.one.two.three.poster.back.core.Core;
import com.one.two.three.poster.back.models.PosterPreview;
import com.one.two.three.poster.back.utils.BazaarHelper;
import com.one.two.three.poster.back.utils.NumberFormater;
import com.one.two.three.poster.front.activities.ActivityDesign;
import com.one.two.three.poster.front.activities.BaseActivity;
import com.one.two.three.poster.front.components.DialogBuyPoster;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by Pouyan-PC on 7/31/2017.
 */

public class AdapterPoster extends RecyclerView.Adapter<AdapterPoster.ViewHolder> {

    private final String TAG = "POUYANNN";

    public enum BehindActivityName { PostersActivity, PosterFramesActivity, DownloadsActivity }

    private BehindActivityName behindActivityName;
    private ArrayList<PosterPreview> posters;
    private BaseActivity activity;
    private boolean gridMode;

    public AdapterPoster(BehindActivityName name, ArrayList<PosterPreview> posters, BaseActivity activity, boolean gridMode) {
        this.behindActivityName = name;
        this.posters = new ArrayList<>(posters);
        this.notifyDataSetChanged();
        this.activity = activity;
        this.gridMode = gridMode;
    }

    public void addPosters(ArrayList<PosterPreview> posters) {
        final int oldSize = this.posters.size();
        int counter = + 0;
        for (int i = 0; i < posters.size(); i++) {
            boolean mustAdd = true;
            PosterPreview poster = posters.get(counter);
            for(PosterPreview p : this.posters){            // Preventing posters add twice!
                if(p.getId().equals(poster.getId())){
                    Log.i(TAG, "poster duplicate find : " + p.getSku() + " , " + poster.getSku());
                    mustAdd = false;
                    break;
                }
            }
            if(mustAdd){
                this.posters.add(poster);
                this.notifyItemInserted(oldSize + counter);
                counter++;
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_poster, parent, false);
        if (this.gridMode) {
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT
                    , (int) (500f / 353f * ((activity.getResources().getDisplayMetrics().widthPixels - 48
                    * activity.getResources().getDisplayMetrics().density) / 3 + 24 * activity.getResources()
                    .getDisplayMetrics().density))));
            viewHolder = new ViewHolder(itemView);
        } else {
            itemView.setLayoutParams(new RecyclerView.LayoutParams((int)(108 * activity.getResources()
                    .getDisplayMetrics().density), RecyclerView.LayoutParams.MATCH_PARENT));
            viewHolder = new ViewHolder(itemView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final PosterPreview poster = posters.get(position);

        holder.txtPrice.setText(poster.getPrice().equals("0") ? activity.getResources().getString(R.string.free) : NumberFormater.convertToPersianNumbers(poster.getPrice() + " تومان"));

        switch(poster.getRate()){
            case 5:
                holder.star5.setImageResource(R.drawable.ic_star_fill);
            case 4:
                holder.star4.setImageResource(R.drawable.ic_star_fill);
            case 3:
                holder.star3.setImageResource(R.drawable.ic_star_fill);
            case 2:
                holder.star2.setImageResource(R.drawable.ic_star_fill);
            case 1:
                holder.star1.setImageResource(R.drawable.ic_star_fill);
        }


        if (behindActivityName == BehindActivityName.DownloadsActivity) {
            Glide.with(activity)
                    .load(new File(Core.getInstance().DOWNLOAD_DIR_PATH + File.separator +
                        poster.getId() + File.separator  + "preview.png"))
                    .into(holder.imgThumbnail);
        }
        else {
            RequestOptions options = new RequestOptions();
            options.diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(activity)
                    .load(poster.getThumbnailPath())
                    .apply(options)
                    .into(holder.imgThumbnail);
        }

        if (behindActivityName == BehindActivityName.PostersActivity) {
            holder.newSign.setVisibility(View.VISIBLE);
        }
        else {
            holder.newSign.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!activity.isDownloadingPoster()) {

                    activity.askPermission(new OnPermissionGrantListener() {
                        @Override
                        public void onPermissionGranted() {

                            Core.getInstance().checkAppFolders();

                            if (poster.getPrice().equals("0")) {
                                clearTempTrash();
                                handleDownload(holder, poster.getId(), poster.getUrl());
                            } else {

                                boolean purchased;
                                if(Core.getInstance().getInventory() != null) {
                                    purchased = Core.getInstance().getInventory().hasPurchase(poster.getSku());
                                } else {
                                    if(BazaarHelper.isBazaarInstalled()){
                                        Toast.makeText(Core.getInstance().getContext(), Core.getInstance().getContext().getString(R.string.bazaar_login), Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(Core.getInstance().getContext(), Core.getInstance().getContext().getString(R.string.bazaar_is_not_installed), Toast.LENGTH_SHORT).show();
                                    }
                                        purchased = false;
                                }

                                if (purchased || behindActivityName == BehindActivityName.DownloadsActivity) {
                                    clearTempTrash();
                                    handleDownload(holder, poster.getId(), poster.getUrl());
                                } else {
                                    DialogBuyPoster dialogBuyPoster = new DialogBuyPoster(activity);
                                    dialogBuyPoster.setPoster(poster);
                                    dialogBuyPoster.setOnBuyListener(new DialogBuyPoster.OnPosterBuyListener() {
                                        @Override
                                        public void onBuy(PosterPreview poster) {
                                            clearTempTrash();
                                            handleDownload(holder, poster.getId(), poster.getUrl());
                                        }
                                    });
                                    dialogBuyPoster.show();
                                }
                            }
                        }

                        @Override
                        public void onPermissionFailure() {
                            Toast.makeText(activity, Core.getInstance().getContext().getString(R.string.grant_permission), Toast.LENGTH_SHORT).show();
                        }
                    }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.posters.size();
    }

    private void clearTempTrash() {

        for (File file : new File(Core.getInstance().TEMP_DIR_PATH).listFiles()) {
            file.delete();
        }
    }

    private void checkPermission(final Runnable runnable) {

        activity.askPermission(new OnPermissionGrantListener() {
            @Override
            public void onPermissionGranted() {
                runnable.run();
            }

            @Override
            public void onPermissionFailure() {
                Toast.makeText(activity, Core.getInstance().getContext().getString(R.string.grant_permission), Toast.LENGTH_SHORT).show();
            }
        }, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE });
    }

    private void handleDownload(final ViewHolder holder, final String posterId, final String posterUrl) {

        if (posterUrl.length() > 0 && !Core.getInstance().getCacheHelper().getCachedBluePrints().contains(posterId)) {

            activity.setDownloadingPoster(true);

            holder.progressbar.setVisibility(View.VISIBLE);

            final int posterIndex = holder.getAdapterPosition();

            Core.getInstance().getNetworkHelper().downloadBluePrint(posterId, posterUrl
                    , new OnPosterDownloadedListener() {
                        @Override
                        public void blueprintDownloaded() {

                            PosterPreview posterPreview = posters.get(posterIndex);

                            Core.getInstance().getCacheHelper().cacheBluePrint(posterId);

                            holder.progressbar.setVisibility(View.GONE);

                            activity.setDownloadingPoster(false);

                            File previewDetailsFile = new File(Core.getInstance().DOWNLOAD_DIR_PATH + File.separator + posterId + File.separator + "preview_details.txt");

                            if (previewDetailsFile.exists()) {
                                previewDetailsFile.delete();
                            }

                            try {
                                previewDetailsFile.createNewFile();
                                PrintWriter writer = new PrintWriter(new FileWriter(previewDetailsFile));
                                writer.write(posterPreview.getThumbnailPath() + "," + posterPreview.getPrice());
                                writer.close();
                            } catch (Exception ignored) {}

                            Core.getInstance().getNetworkHelper().downloadPreview(posterPreview.getThumbnailPath()
                                    , posterPreview.getId(), new Runnable() {
                                        @Override
                                        public void run() {}
                                    });
                            openPoster(holder.getAdapterPosition());

                        }
                    }, new OnDownloadProgressedListener() {
                        @Override
                        public void downloadProgressed(float progress) {

                            holder.progressbar.setProgress(progress);
                        }
                    });
        }
        else {

            openPoster(holder.getAdapterPosition());
        }
    }

    private void openPoster(final int position) {

        checkPermission(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(activity, ActivityDesign.class);
                intent.putExtra("poster_id", posters.get(position).getId());
                intent.putExtra("behind_activity_name", AdapterPoster.this.behindActivityName + "");
                activity.startActivity(intent);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView txtPrice;
        ImageView imgThumbnail;
        DonutProgress progressbar;
        ImageView newSign;

        // Rating Stars
        ImageView star1;
        ImageView star2;
        ImageView star3;
        ImageView star4;
        ImageView star5;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            txtPrice = itemView.findViewById(R.id.txtPosterPrice);
            imgThumbnail = itemView.findViewById(R.id.imgPosterThumbnail);
            progressbar = itemView.findViewById(R.id.donut_progress);
            newSign = itemView.findViewById(R.id.poster_new_image_view);

            star1 = itemView.findViewById(R.id.imgStar1);
            star2 = itemView.findViewById(R.id.imgStar2);
            star3 = itemView.findViewById(R.id.imgStar3);
            star4 = itemView.findViewById(R.id.imgStar4);
            star5 = itemView.findViewById(R.id.imgStar5);
        }
    }

}